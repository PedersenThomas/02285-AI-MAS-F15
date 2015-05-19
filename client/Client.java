package client;

import java.io.*;
import java.util.*;

import javax.management.RuntimeErrorException;

import client.Search.PlannerNode;

public class Client {
	public World world = new World();

	public static final Command NoOp = NoOpCommand.instance;
	
	enum AgentStatus {
		ACTIVE,
		WAITING
	};

	public class Agent {
		private int id;
		private String color = "NoColor";
		private Point position;
		private Point newPosition;
		private AgentStatus status;
		private Plan plan = null;
		private Intention intention;
		private SubIntention currentSubIntention = null;
		private Queue<SubIntention> subIntentions = null;
		private int inactivityCounter = 0;
		private int sleepTime = 0;
		
	

		Agent( int id, String color, Point position ) {
			if(id > 9) {
				throw new RuntimeException("Id is invalid: " + id);
			}
			this.id = id;
			this.position = position;
			this.newPosition = position;
			this.status = AgentStatus.ACTIVE;
			
			if(color != null) {
				this.color = color;
			}
		}

		public Agent CloneAgent() {
			Agent clone = new Agent(this.id, this.color, this.position);
			clone.status = this.status;
			return clone;
		}

		public Point getPosition() {
			return position;
		}
		
		public Point getNewPosition() {
			return newPosition;
		}

		public void setPosition(Point position) {			
			this.position = position;
		}
		
		public String getColor() {
			return color;
		}
		
		public Intention getIntention() {
			return intention;
		}

		public int getId() {
			return id;
		}
		
		public AgentStatus getStatus() {
			return status;
		}
		
		public void setStatus(AgentStatus status) {
			if((status == AgentStatus.ACTIVE) && 
					(this.status == AgentStatus.WAITING)) {
				//The world has changed -> find new intentions
				replan();
			}
			this.status = status;
		}
		
		public void clearIntention() {
			intention=null;
		}
		
		public void sleep(int time) {
			this.status = AgentStatus.WAITING;
			sleepTime = time;
		}

		/**
		 * Compute the next command for the agent.
		 */
		public Command act() {		
			
			if(world.getNumberOfUncompletedGoals() == 0) {
				return NoOp;
			}
			
			if(this.status == AgentStatus.WAITING) {	
				sleepTime--;
				
				if(sleepTime <= 0) {
					this.status = AgentStatus.ACTIVE;
				}
				else				
					return NoOp;
			}

			// BDI Version 2
			SubIntention delegatedSubIntention = null;
			//Is there some job in the world, which this agent can solve.
			if(plan == null || plan.isEmpty()) {
				delegatedSubIntention = world.popJob(this);
			
				//Make sure an agent has an intention.
				if((delegatedSubIntention == null) && (subIntentions == null || subIntentions.isEmpty())) {									
					//deliberate by choosing a set of intentions based on current beliefs
					intention = Intention.deliberate(world, this);
					Logger.logLine("Agent[" + this.getId() + "] Got the intention: " + intention);
					if(intention == null) {
						return NoOp;
					}
					
					if(!world.putIntention(this.id, intention.getBox(), intention.getGoal())) {
						Logger.logLine("Agent[" + this.getId() + "] Couldn't put the intention in the world.");
						return NoOp;
					}
					subIntentions = new LinkedList<SubIntention>(IntentionDecomposer.decomposeIntention(intention, world, this.id));
				}
				
				//Make sure that we have an subIntention to plan for.
				if(delegatedSubIntention == null) {
					Logger.logLine("Agent[" + this.getId() + "] No Delegated subIntention");
				}
				else {
					Logger.logLine("Agent[" + this.getId() + "] Have this cool Delegated subIntention: " + delegatedSubIntention);
					
					if(!world.validateJob(delegatedSubIntention,this)) {
						Logger.logLine("Agent[" + this.getId() + "] Delegated subIntention is not valid anymore!");
						world.notifyAgent(delegatedSubIntention.getOwner());
						return NoOp;
					}
					
					Queue<SubIntention> temp = null;
					if((subIntentions != null && !subIntentions.isEmpty())) 
						temp = new LinkedList<SubIntention>(subIntentions);
					subIntentions = new LinkedList<SubIntention>(IntentionDecomposer.decomposeSubIntention(delegatedSubIntention, world, this.id).subIntentions);
					if(temp!= null)
						subIntentions.addAll(temp);
				}				
					
				currentSubIntention = subIntentions.peek();	
				while(currentSubIntention != null) {												
			
					// Check if this agent can do the job
					if (currentSubIntention instanceof MoveBoxSubIntention) {
						MoveBoxSubIntention moveSubIntention = (MoveBoxSubIntention)currentSubIntention;
						if(!moveSubIntention.getBox().getColor().equals(color)) {
							world.addJob(moveSubIntention);
							subIntentions.poll();
							Logger.logLine(this.id + ": Please do it! >> " + moveSubIntention);
							this.sleep(30);
							Logger.logLine(this.id + " I am waiting>> ");
							
						} 
						else {							
							break;  // I can do it!
						}
					}
					else {						
						break;  // I can do it!
					}
					currentSubIntention = subIntentions.peek();
				}
				
				if(status == AgentStatus.WAITING)
					return NoOp;
				else
					currentSubIntention = subIntentions.poll();	
				
				plan = new Plan(world, currentSubIntention, this);
				if(plan.isEmpty()) {
					// Maybe the planner has recognized that it is better to wait
					if(status != AgentStatus.WAITING) {							
						Queue<SubIntention> temp = new LinkedList<SubIntention>(subIntentions); 
						subIntentions = IntentionDecomposer.splitSubIntention(currentSubIntention, world, id);
						if(subIntentions.size() < 2) {
							if(delegatedSubIntention != null) {
								//Someone is waiting
								world.getAgent(delegatedSubIntention.getOwner()).setStatus(AgentStatus.ACTIVE);
							}
							
							Logger.logLine("["+id+"] No plan -> find new intentions");
							replan();
							sleep(6);
						} else {
							Logger.logLine("["+id+"] No plan -> Split subintentions");
							subIntentions.addAll(temp);
						}												
					}

					return NoOp;
				}
			}

			if(!world.validPlan(this.id)) {
				inactivityCounter++;
				
				if(inactivityCounter > 30) {
					Logger.logLine("["+id+"] Timout -> replan");
					replan();
				}
				
				return NoOp;
			}
			
			//execute the plan
			Command cmd = plan.execute();
			World tempWorld = new World(world);
			boolean validUpdate = tempWorld.update(tempWorld.getAgent(this.getId()), cmd);
			if(!validUpdate) {
				//TODO We have here an invalid command. What to do now?
				Logger.logLine("Invalid command: " + cmd + " " + this);
				
				// The world has changed since we have created this plan. Our plan is outdated!
				replan();
				return NoOp;
			}
			newPosition = tempWorld.getAgent(this.getId()).getPosition();
			
			//if intention is completed it should be removed from the sequence of active intentions in the world
			if(plan.isEmpty() && intention != null && intention.getGoal().getPosition().equals(tempWorld.getBoxById(intention.getBox().getId()).getPosition())) {
				world.clearIntention(this.id);
			}
			
			if(cmd instanceof NotifyAgentCommand) {
				this.clearIntention();
			}
			
			inactivityCounter = 0;
			Logger.logLine("["+id+"] " + cmd.toString() + "\t-> " + this.position.toString());
			return cmd;
		}
		
		public void replan() {
			Logger.logLine("replan for " + this);
			world.clearIntention(this.id);
			if(subIntentions != null)
			{
				/*while(!subIntentions.isEmpty()) {
					SubIntention si = subIntentions.poll();
					if(si.getOwner() != id) {
						world.notifyAgent(si.getOwner());
					}					
				}*/
				subIntentions.clear();
			}
			world.clearPlan(id);
			
			if(plan != null) {
				while(!plan.isEmpty()) {
					Command cmd = plan.execute();
					if(cmd instanceof NotifyAgentCommand) {
						int agentId = ((NotifyAgentCommand)cmd).getAgentId();
						world.getAgent(agentId).setStatus(AgentStatus.ACTIVE);
						//world.notifyAgent(agentId);
					}
				}
			}
			status = AgentStatus.ACTIVE;
			inactivityCounter = 0;		
		}

		@Override
		public String toString() {
			return "AGENT " + id + " Color: " + color + " Position: " + position + " Status: " + status;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.id;
			return result;
		}

		@Override
		public boolean equals( Object obj ) {
			if ( this == obj )
				return true;
			if ( obj == null )
				return false;
			if ( getClass() != obj.getClass() )
				return false;
			Agent other = (Agent) obj;
			if(this.id != other.id) {
				return false;
			}
			if(!this.position.equals(other.position)) {
				return false;
			}
			return true;
		}
	}

	private BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );


	public Client() throws IOException {
		readMap();
	}

	private void readMap() throws IOException {
		Map< Character, String > colors = new HashMap< Character, String >();
		String line, color;

		// Read lines specifying colors
		while ( ( line = in.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
			line = line.replaceAll( "\\s", "" );
			color = line.split( ":" )[0];

			for ( String id : line.split( ":" )[1].split( "," ) )
				colors.put( id.charAt( 0 ), color );
		}

		// Read lines specifying level layout
		int y=0;
		int width = 0;
		int boxId = 0;
		while ( !line.equals( "" ) ) {
			for ( int i = 0; i < line.length(); i++ ) {
				char id = line.charAt( i );
				if ( '0' <= id && id <= '9' )
					world.addAgent( new Agent( id - '0', colors.get( id ),new Point(i,y) ) );
				else if ( 'A' <= id && id <= 'Z' )
					world.addBox( new Box(i,y,id,colors.get(id), boxId++));
				else if ( 'a' <= id && id <= 'z' )
					world.addGoal( new Goal(i,y, Character.toUpperCase(id)));
				else if ( id=='+')
					world.addWall(i,y);
			}
			if(line.length() > width) {
				width = line.length();
			}
			line = in.readLine();
			y++;

		}
		world.setLevelSize(width, y);
		
		//Find all the reachable cells.
		ConnectedComponent cc = new ConnectedComponent(world);
		for ( int i = 0; i < world.getNumberOfAgents(); i++ ) {
			Agent a = world.getAgents().get(i);
			World.setRechableCells(cc.findPointsInConnectedComponent(a.getPosition()),a.getId());
		}
		
	}

	private static int deadlockCount = 0;
	
	public boolean update() throws IOException {
		String jointAction = "[";
		List<Command> commands = new ArrayList<Command>();
		for ( int i = 0; i < world.getNumberOfAgents(); i++ ) {
			commands.add(world.getAgent( i ).act());
		}

		List<Boolean> validCommands = markValidCommands(commands);
		
		for (int index = 0; index<commands.size() ; index++) {
			if(validCommands.get(index)) {
				boolean isUpdateSuccessed = world.update(world.getAgent(index), commands.get(index));
				if(!isUpdateSuccessed) {
					Logger.logLine("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
					Logger.logLine("Invalid command: " + commands.get(index) + " agent " + index);
				}
			}
			
			if (validCommands.get(index)) {
				jointAction += commands.get(index) + ",";
			} else {
				jointAction += NoOp + ",";
				world.getAgent(index).replan();
			}
		}
		
		jointAction = jointAction.substring(0, jointAction.length()-1) + "]";
		
		if(countSubstring(jointAction, NoOp.toString()) == world.getNumberOfAgents()) {
			deadlockCount++;
			
			jointAction = preventDeadlock(deadlockCount, jointAction);	
		}
		else {
			deadlockCount = 0;
		}
		
		// Place message in buffer
		System.out.println( jointAction );

		// Flush buffer
		System.out.flush();

		// Disregard these for now, but read or the server stalls when its output buffer gets filled!
		String percepts = in.readLine();
		if ( percepts == null )
			return false;
		
		if(percepts.contains("false")) {
			Logger.logLine("*****************************************************************************************************************");
			Logger.logLine("************************************We made an Invalide action***************************************************");
			Logger.logLine(percepts);
			Logger.logLine(jointAction);
			Logger.logLine("*****************************************************************************************************************");
			throw new RuntimeException("Invalid move");
		}
		
		return true;
	}
	
	private String preventDeadlock(int deadlockCount, String jointAction) {
		Random rand = new Random(System.currentTimeMillis());
		if(deadlockCount > 50) {
			List<Agent> sleepingAgents = new ArrayList<Client.Agent>();;
			for(Agent a: world.getAgents()) {
				if(a.getStatus() != AgentStatus.ACTIVE) {
					sleepingAgents.add(a);
				}
			}
			
			if(sleepingAgents.size() > 0) {
				Agent a = sleepingAgents.get(rand.nextInt(sleepingAgents.size()));
				a.setStatus(AgentStatus.ACTIVE);
				a.replan();
			}			
		}
		
		
		if(deadlockCount > 1000) 
			throw new RuntimeException("Deadlock: No agent is moving");	
		
		return jointAction;
	}
	
	private List<Boolean> markValidCommands(List<Command> commands) {
		List<Boolean> validCommands = new ArrayList<Boolean>();
		List<Point> newlyUsedPoints = calculateNewlyUsedPoints(commands);
		Map<Point, Integer> uniqueCommandPicker = new HashMap<Point, Integer>();
		
		//Initialize validCommands with true.
		for (int i = 0; i < commands.size(); i++) {
			validCommands.add(true);
		}
		
		for (int i = 0; i < commands.size(); i++) {
			if((commands.get(i) instanceof NoOpCommand) || (commands.get(i) instanceof NotifyAgentCommand)){
				continue;
			}
			
			//Check if values are unique
			if(!uniqueCommandPicker.containsKey(newlyUsedPoints.get(i))) {
				uniqueCommandPicker.put(newlyUsedPoints.get(i), i);
			} else {
				validCommands.set(i, false);
			}
			
			
			//Check if new points are free. 
			if(!world.isFreeCell(newlyUsedPoints.get(i))) {
				validCommands.set(i, false);
			}
		}
		
		return validCommands;
	}
	
	private List<Point> calculateNewlyUsedPoints(List<Command> commands) {
		List<Point> newPoints = new ArrayList<Point>();
		
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);			
			Point agentPosition = world.getAgent(i).getPosition();
			switch(command.actType) {
			case Move:
			case Pull:
				newPoints.add(agentPosition.move(command.dir1));
				break;
			case Push:
				Point boxPosition = agentPosition.move(command.dir1);
				newPoints.add(boxPosition.move(command.dir2));
				break;
			}
		}
		
		return newPoints;
	}

	public static void main( String[] args ) {

		// Use stderr to print to console
		Logger.logLine( "Hello from WatsOn" );
		try {
			Client client = new Client();
			while ( client.update() )
				;

		} catch ( IOException e ) {
			// Got nowhere to write to probably
		}
	}
	

	public static int countSubstring(String str, String subStr){
		int count = 0;
		for (int loc = str.indexOf(subStr); loc != -1;
		     loc = str.indexOf(subStr, loc + subStr.length()))
			count++;
		return count;
	}
}
