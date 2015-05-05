package client;

import java.io.*;
import java.util.*;

public class Client {
	public World world = new World();
	
	enum AgentStatus {
		ACTIVE,
		WAITING
	};

	public class Agent {
		private int id;
		private String color = "NoColor";
		private Point position;
		private Point lastPosition;
		private AgentStatus status;
		private Plan plan = null;
		private Queue<SubIntention> subIntentions = null;
		private int inactivityCounter = 0;

		Agent( int id, String color, Point position ) {
			if(id > 9) {
				throw new RuntimeException("Id is invalid: " + id);
			}
			this.id = id;
			this.position = position;
			this.lastPosition = position;
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
		
		public Point getLastPosition() {
			return lastPosition;
		}

		public void setPosition(Point position) {			
			this.position = position;
		}
		
		public String getColor() {
			return color;
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
				world.clearIntention(this.id);
				subIntentions.clear();
			}
			this.status = status;
		}

		public final static String NoOp = "NoOp";
		/**
		 * Compute the next command for the agent.
		 */
		public String act() {
//			System.err.println("No Operation for agent " + this);
//			for (Goal goal : world.getGoals()) {
//				System.err.println(goal + " IsComplete:" + world.isGoalCompleted(goal));
//			}
			this.lastPosition = this.position;
			
			if(world.getNumberOfUncompletedGoals() == 0) {
				return NoOp;
			}
			
			if(this.status == AgentStatus.WAITING) {				
				return NoOp;
			}

			// BDI Version 2
			SubIntention delegatedSubIntention = null;
			//Is there some job in the world, which this agent can solve.
			if(plan == null || plan.isEmpty()) {
				//TODO Jobs should be an Intention an not SubIntention
				delegatedSubIntention = world.popJob(this);
			}
			
			//Make sure an agent have an intention.
			if((delegatedSubIntention == null) && (subIntentions == null || subIntentions.isEmpty()) && ((plan == null) || (plan.isEmpty())) ) {									
				//deliberate by choosing a set of intentions based on current beliefs
				Intention intention = Intention.deliberate(world, this);
				if(intention == null) {
					return NoOp;
				}
				if(!world.putIntention(this.id, intention.getBox(), intention.getGoal())) {
					return NoOp;
				}
				subIntentions = new LinkedList<SubIntention>(IntentionDecomposer.decomposeIntention(intention, world, this.id));			
			}
			
			//System.err.println("Plan is null: " + (plan == null) + " Plan is empty: " + (plan != null ? plan.isEmpty() : "null"));
			if(plan == null || plan.isEmpty()) {
				
				//Make sure that we have an subIntention to plan for.
				SubIntention subIntention = null;
				if(delegatedSubIntention == null) {
					subIntention = subIntentions.peek();
				} else {
					subIntention = delegatedSubIntention;
				}				
				
				
				// Check if this agent can do the job
				if (subIntention instanceof MoveBoxSubIntention) {
					MoveBoxSubIntention moveSubIntention = (MoveBoxSubIntention)subIntention;
					if(!moveSubIntention.getBox().getColor().equals(color)) {
						subIntentions.poll();
						world.addJob(subIntention);
						System.err.println(this.id + ": Please do it! >> " + subIntention);
						this.status = AgentStatus.WAITING;
						return NoOp;
					}
				}
				
				if(delegatedSubIntention == null)
					subIntentions.poll();
				
				plan = new Plan(world, subIntention, this);
				if(plan.isEmpty()) {
					world.clearIntention(this.id);
					subIntentions.clear();
					System.err.println("["+id+"] No plan -> find new intentions");
					return NoOp;
				}
			}

			if(!world.validPlan(this.id)) {
				//System.err.println("No Valid Plan");
				inactivityCounter++;
				
				if(inactivityCounter > 25) {
					System.err.println("["+id+"] Timout -> replan");
					world.clearIntention(this.id);
					subIntentions.clear();
					world.clearPlan(id);
					plan = null;
					inactivityCounter = 0;
				}
				
				return NoOp;
			}
			
			//execute the plan
			Command cmd = plan.execute();
			boolean validUpdate = world.update(this, cmd);
			if(!validUpdate) {
				//TODO We have here an invalid command. What to do now?
				System.err.println("Invalid command: " + cmd + " " + this);
			}
			
			if(plan.isEmpty()) {
				world.clearIntention(this.id);
			}
			inactivityCounter = 0;
			System.err.println("["+id+"] " + cmd.toString() + "\t-> " + this.position.toString());
			return cmd.toString();
		}

		@Override
		public String toString() {
			return "AGENT " + id + " Color: " + color + " Position: " + position;
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

		for ( int i = 0; i < world.getNumberOfAgents() - 1; i++ )
			jointAction += world.getAgent( i ).act() + ",";

		jointAction += world.getAgent( world.getNumberOfAgents() - 1 ).act() + "]";
		
		if(countSubstring(jointAction, Agent.NoOp) == world.getNumberOfAgents()) {
			deadlockCount++;
			
			if(deadlockCount > 1000) 
				throw new RuntimeException("Deadlock: No agent is moving");		
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
		

		//System.err.println("   jointAction:" + jointAction);
		if(percepts.contains("false")) {
			System.err.println("******************************************************************************************************");
			System.err.println("************************************We made an Invalide action****************************************");
			System.err.println(percepts);
			System.err.println(jointAction);
			System.err.println("******************************************************************************************************");
		}
		
		return true;
	}

	public static void main( String[] args ) {

		// Use stderr to print to console
		System.err.println( "Hello from RandomWalkClient. I am sending this using the error outputstream" );
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
