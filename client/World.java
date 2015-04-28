package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import client.Client.Agent;
import client.Intention.GoalComparator;
import client.Heuristic.AStar;
import client.Heuristic.HeuristicPathFunction;
import client.Heuristic.IHeuristicFunction;
import client.Search.BestFirstSearch;
import client.Search.PathNode;
import client.Search.SearchNode;

public class World {
	private List<Box> boxes = new ArrayList<Box>();
	private static List<Goal> goals = new ArrayList<Goal>();
	private List<Agent> agents = new ArrayList<Agent>();
	private List<Point> walls = new ArrayList<Point>();
	private static List<Point> rechableCells = new ArrayList<Point>();
	private int width;
	private int height;
	private Map<Integer,Intention> intentionMap = new HashMap<>();
	private List<SubIntention> jobList = new ArrayList<>();
	private Map<Integer,LinkedList<Command>> planMap=new HashMap<>();

	public World() {
	}

	public World(World old) {
		for (Box box : old.boxes) {
			this.boxes.add(new Box(box));
		}
		for (Agent agent : old.agents) {
			this.agents.add(agent.CloneAgent());
		}
		for (Point wall : old.walls) {
			this.walls.add(new Point(wall));
		}
		
		this.width = old.width;
		this.height = old.height;
		
		// No deep-copy! (on purpose)
		/*this.intentionMap = old.intentionMap;
		this.jobList = old.jobList;
		this.planMap = old.planMap;
		
		
		
		/*/for (Map.Entry<Integer, Intention> entry : old.intentionMap.entrySet()) {
			this.intentionMap.put(entry.getKey(), new Intention(entry.getValue()));
		}
		
		for (SubIntention job : old.jobList) {
			this.jobList.add(new SubIntention(job));
		}
		
		for (Map.Entry<Integer, LinkedList<Command>> entry : old.planMap.entrySet()) {
			LinkedList<Command> cmds = new LinkedList<>();
			for (Command cmd : entry.getValue()) {
				cmds.add(cmd);
			}
			this.planMap.put(entry.getKey(), cmds);
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLevelSize() {
		return width * height;
	}
	
	public List<Point> getRechableCells() {
		return rechableCells;
	}
	
	public static void setRechableCells(List<Point> rechableCells) {
		World.rechableCells = Collections.unmodifiableList(rechableCells);
	}	

	public List<Box> getBoxes() {
		return Collections.unmodifiableList(boxes);
	}
	
	public List<Box> getBoxes(String color) {
		List<Box> result = new ArrayList<Box>();
		for(Box b:boxes) {
			if(b.getColor().equals(color))
				result.add(b);
		}
		return result;
	}

	public List<Goal> getGoals() {
		return Collections.unmodifiableList(goals);
	}
	
	public List<Goal> getCompletedGoals() {
		List<Goal> completedGoals = new ArrayList<Goal>();
		for(Goal g:goals) {
			if(isGoalCompleted(g))
				completedGoals.add(g);
		}
		return Collections.unmodifiableList(completedGoals);
	}

	public int getNumberOfUncompletedGoals() {
		int result = 0;
		for(Goal g: goals) {
			if(!isGoalCompleted(g)) {
				result++;	
			}
		}
		return result;
	}

	public List<Agent> getAgents() {
		return Collections.unmodifiableList(agents);
	}

	public List<Point> getWalls() {
		return Collections.unmodifiableList(walls);
	}

	public int getNumberOfAgents() {
		return agents.size();
	}

	public void setLevelSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Agent getAgent(int id) {
		return agents.get(id);
	}

	public void addBox(Box b) {
		boxes.add(b);
	}

	public void addWall(int x, int y) {
		walls.add(new Point(x, y));
	}

	public void addGoal(Goal g) {
		goals.add(g);
	}

	public void addAgent(Agent a) {
		agents.add(a);
	}
	
	public boolean putIntention(int agentId, Box box, Goal goal) {
		if(!isIntentionAvailable(box,goal))
			return false;
		
		intentionMap.put(agentId,new Intention(goal, box));
		return true;
	}
	
	public boolean putPlan(int agentId,Queue<Command> commandQueue){		
		planMap.put(agentId, (LinkedList<Command>)commandQueue);
		return true;
	}
	
	public boolean validPlan(int agentId){		
		LinkedList<Command> plan = planMap.get(agentId);
		for (Map.Entry<Integer, LinkedList<Command>> entry : planMap.entrySet()) {
			if(agentId != entry.getKey()) {
				boolean conflict = checkPlans(agentId, plan, entry.getKey(),entry.getValue());
				
				if(conflict) {
					if(agentId < entry.getKey())
						return false;
				}
			}
		}
		return true;		
	}
	
	public boolean validStep(int agentId){		
		LinkedList<Command> plan = planMap.get(agentId);
		Agent a = getAgent(agentId);		
		Point pos = a.getPosition();	
		Point newPos = pos.move(plan.get(0).dir1);
		
		for (Map.Entry<Integer, LinkedList<Command>> entry : planMap.entrySet()) {
			if(agentId != entry.getKey()) {
				Agent otherAgent = getAgent(entry.getKey());
				
				Point otherPos = otherAgent.getPosition();
				if(newPos.equals(otherPos)) {
					return false;
				}
				
				LinkedList<Command> otherPlan = planMap.get(entry.getKey());

				boolean conflict = false;
				for(int i=0;i<otherPlan.size();i++) {			
					otherPos = otherPos.move(otherPlan.get(i).dir1);
					
					if(pos.equals(otherPos)) {
						conflict = true;
						break;
					}
					
					if(newPos.equals(otherPos)) {
						conflict = true;
						break;
					}
				}	
				
				if(conflict) {
					if(agentId > entry.getKey()) {
						return false;
					}
				}
				
			}
		}
		return true;		
	}
	
	public boolean checkPlans(int agentId1, LinkedList<Command> plan1, int agentId2, LinkedList<Command> plan2) {
		Agent a1 = getAgent(agentId1);
		Agent a2 = getAgent(agentId2);
		
		Point pos1 = a1.getPosition();		
		Point pos2 = a2.getPosition();	
		
		if(agentId1 > agentId2) {
			pos2 = a2.getLastPosition();
		}
		
		
		for(int i=0;i<Math.max(plan1.size(), plan2.size());i++) {			
			Point newPos1 = pos1;
			if(i<plan1.size())
				newPos1 = pos1.move(plan1.get(i).dir1);
			
			Point newPos2 = pos2;
			if(agentId1 > agentId2) {
				if(i==0)
					newPos2 = a2.getPosition();
				else if(i-1<plan2.size())
					newPos2 = pos2.move(plan2.get(i-1).dir1);
			}
			else if(i<plan2.size())
				newPos2 = pos2.move(plan2.get(i).dir1);
			
			if(pos1.equals(pos2))
				return true;
			
			if(pos1.equals(newPos2))
				return true;
			
			if(newPos1.equals(pos2))
				return true;
			
			if(newPos1.equals(newPos2))
				return true;			

			
			pos1 = newPos1;
			pos2 = newPos2;
		}		
		
		return false;
	}
	
	public boolean updatePlan(int agentId){
		planMap.get(agentId).poll();
		return true;
	}
	
	public void clearIntention(int agentId) {
		intentionMap.remove(agentId);
	}
	
	public boolean isIntentionAvailable(Box box, Goal goal) {
		for (Map.Entry<Integer, Intention> entry : intentionMap.entrySet())
		{
		    if(entry.getValue().getBox().equals(box) || entry.getValue().getGoal().equals(goal)) {
		    	return false;
		    }
		}
		return true;
	}

	public void printWorld() {
		System.err.println("==============================");
		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				Point p = new Point(x,y);
				if(isWallAt(p)) {
					System.err.print("+");
				}
				else if(isBoxAt(p)) {
					System.err.print("A");
				}
				else {
					boolean printFlag = false;
					for (Agent a:agents) {
						if(a.getPosition().equals(p)) {
							System.err.print("0");
							printFlag = true;
							break;
						}
					}
					if(!printFlag) {
						System.err.print(" ");
					}
				}
			}
			System.err.print("\n");
		}
		System.err.println("==============================");
	}

	public boolean isGoalCompleted(Goal goal) {
		Box box = getBoxAt(goal.getPosition());
		if(box == null){
			return false;
		}
		return box.getLetter() == goal.getLetter();
	}

	public boolean isAgentAt(Point point) {
		for (Agent agent : agents) {
			if(agent.getPosition().equals(point)) {
				return true;
			}
		}
		return false;
	}

	public boolean isBoxAt(Point point) {
		for (Box box : boxes) {
			if(box.getPosition().equals(point)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWallAt(Point point) {
		for (Point wall : walls) {
			if(wall.equals(point)) {
				return true;
			}
		}

		return false;
	}

	public boolean isGoalAt(Point point) {
		for (Goal goal : goals) {
			if(goal.getPosition().equals(point)) {
				return true;
			}
		}

		return false;
	}

	public Goal getGoalAt(Point point) {
		for (Goal goal : goals) {
			if(goal.getPosition().equals(point)) {
				return goal;
			}
		}

		return null;
	}

	public boolean isFreeCell(Point point) {
		if (isWallAt(point)) {
			return false;
		}

		if (isBoxAt(point)) {
			return false;
		}
		
		if (isAgentAt(point)) {
			return false;
		}

		return true;
	}

	public Box getBoxAt(Point position) {
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).getPosition().equals(position)) {
				return boxes.get(i);
			}
		}
		return null;
	}

	public Box getBoxById(int id) {
		for(Box box: this.getBoxes()) {
			if(box.getId() == id) {
				return box;
			}
		}
		return null;
	}

	/**
	 * Updates the world with a command an agent is executing.
	 * @return boolean value which tells whether there is made a change.
	 */
	public boolean update(Agent agent, Command command) {

		switch (command.actType) {
		case Move: {
			Point agentDestPosition = agent.getPosition().move(command.dir1);
			if (isFreeCell(agentDestPosition)) {
				agent.setPosition(agentDestPosition);
				return true;
			}
			break;
		}
		case Push: {

			Point boxSrcPosition = agent.getPosition().move(command.dir1);
			Point boxDestPosition = boxSrcPosition.move(command.dir2);

			if ( isBoxAt(boxSrcPosition)
					&& isFreeCell(boxDestPosition)
					&& (!Command.isOpposite(command.dir1, command.dir2))
					&& getBoxAt(boxSrcPosition).getColor().equals(agent.getColor()) ) {
				agent.setPosition(boxSrcPosition);
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(boxDestPosition);
				return true;
			}

			break;
		}
		case Pull: {
			Point agentDestPosition = agent.getPosition().move(command.dir1);
			Point boxSrcPosition = agent.getPosition().move(command.dir2);

			if (isBoxAt(boxSrcPosition)
					&& isFreeCell(agentDestPosition)
					&& command.dir1 != command.dir2
					&& getBoxAt(boxSrcPosition).getColor().equals(agent.getColor()) ) {
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(agent.getPosition());
				agent.setPosition(agentDestPosition);
				return true;
			}
			break;
		}
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode( agents.toArray() );
		result = prime * result + Arrays.deepHashCode( boxes.toArray() );
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
		World other = (World) obj;

		for(Box box : boxes) {
			if (!other.boxes.contains(box)) {
				return false;
			}
		}

		for(Agent agent : agents) {
			if (!other.agents.contains(agent)) {
				return false;
			}
		}
		return true;
	}

	public boolean isInnerGoal(Goal goal) {
		int numSurroundedWalls = 0;
		int numSurroundedGoals = 0;

		for(Command.dir dir:Command.dir.values()) {
			if(isWallAt(goal.getPosition().move(dir)))
				numSurroundedWalls++;
			if(isGoalAt(goal.getPosition().move(dir)))
				numSurroundedGoals++;
		}

		if((numSurroundedWalls + numSurroundedGoals) == 4)
			return true;

		return false;
	}

	public int getGoalPriorityScore(Goal goal) {
		// Can be pre-computed because it doesn't change
		if(goal.getPriorityScore() >= 0)
			return goal.getPriorityScore();
		
		boolean innerGoal = isInnerGoal(goal);
		int numSurroundedWalls = 0;
		int numSurroundedInnerGoals = 0;
		int numSurroundedOuterGoals = 0;

		for(Command.dir dir:Command.dir.values()) {
			Point p = goal.getPosition().move(dir);
			if(isWallAt(p))
				numSurroundedWalls++;
			if(isGoalAt(p)) {
				if(isInnerGoal(getGoalAt(p)))
					numSurroundedInnerGoals++;
				else
					numSurroundedOuterGoals++;
			}				
		}

		int score = 0;
		if(innerGoal)
			score += 50000;
		
		score += 	numSurroundedWalls      * 10000 +
				    numSurroundedInnerGoals *  1000 +
		            numSurroundedOuterGoals *   100;

		if(innerGoal) {
			List<Goal> connectedGoals = new ArrayList<Goal>();
			connectedGoals.add(goal);
			getConnectedGoals(goal,connectedGoals);

			int minDistance = 99999;
			for(Goal g:connectedGoals) {
				int distance = g.getPosition().distance(goal.getPosition());
				if((!isInnerGoal(g)) && (distance < minDistance)) {
					minDistance = distance;
				}
			}
			score += minDistance;
		}
		
		goal.setPriorityScore(score);
		return score;
	}

	public void getConnectedGoals(Goal goal, List<Goal> result) {
		for(Command.dir dir:Command.dir.values()) {
			Point p = goal.getPosition().move(dir);
			if(isGoalAt(p)) {
				Goal neighborGoal = getGoalAt(p);
				if(!result.contains(neighborGoal)) {
					result.add(neighborGoal);
					getConnectedGoals(neighborGoal, result);
				}
			}
		}
	}
	
	private Map<Integer, List<Goal>> agentGoalOrder;
	
	public List<Goal> getGoalOrderForAgent(int agentId) {
		
		if(agentGoalOrder == null)
			agentGoalOrder = new HashMap<Integer, List<Goal>>();
		
		if(agentGoalOrder.containsKey(agentId))
			return agentGoalOrder.get(agentId);
		
		// initial sorting according to goal priority score
		goals.sort(new GoalComparator(this));
		
		
		List<Goal> orderedGoals = new ArrayList<Goal>(goals);	
		
		//remove goals, which cannot be completed by this agent
		List<Goal> removeGoals = new ArrayList<Goal>();
		for(Goal g:orderedGoals) {
			List<Box> boxes = getBoxes(getAgent(agentId).getColor());
			boolean goalCanBeCompleted = false;
			for(Box b:boxes) {
				if(b.getLetter() == g.getLetter()) {
					goalCanBeCompleted = true;
					break;
				}
			}
			if(!goalCanBeCompleted) {
				removeGoals.add(g);
			}			
		}
		orderedGoals.removeAll(removeGoals);
		
		if(orderedGoals.isEmpty()) {
			return Collections.emptyList();
		}
		
		Point initialAgentPos = new Point(getAgent(agentId).getPosition());
		World initialCopyOfWorld = new World(this);
		
		/*for(Box b:boxes) {
			if(!b.getColor().equals(getAgent(agentId).getColor())) {
				initialCopyOfWorld.addWall(b.getPosition().getX(), b.getPosition().getY());
			}
		}*/
		
		
		// Check paths
		boolean allChecked = false;
		while(!allChecked) {
			World copyOfWorld = new World(initialCopyOfWorld);
			Point agentPos = initialAgentPos;
			List<Box> boxes = copyOfWorld.getBoxes(getAgent(agentId).getColor());
			Collections.shuffle(boxes);  // could be done in a better way
			for(int i=0;i<orderedGoals.size();i++) {			
				Goal g = orderedGoals.get(i);
				boolean reachableBoxFound = false;
				for(Box b:boxes) {
					if(g.getLetter() == b.getLetter()) {
						if(copyOfWorld.isPositionReachable(agentPos, b.getPosition(), true)) {
							agentPos = b.getPosition();
							boxes.remove(b);
							reachableBoxFound = true;
							break;
						}
					}
				}
				//if(!reachableBoxFound)
				//	break;
				
				
				boolean pathExists = copyOfWorld.isPositionReachable(agentPos, g.getPosition(), true);
				if(pathExists) {
					copyOfWorld.addWall(g.getPosition().getX(), g.getPosition().getY());
					agentPos = g.getPosition();
					if(i==orderedGoals.size()-1) {
						allChecked = true;
					}
				}
				else {
					//Collections.swap(goals, i, i-1);
					if(i == 0) {
						return Collections.emptyList();
					}
					
					orderedGoals.remove(i);
					orderedGoals.add(0, g);
					break;
				}				
			}  //for(int i=0;i<goals.size();i++)
		}  //while(!allChecked) 
		
		System.err.println("=== ORDERED GOALS ===");
		for(int i=0;i<orderedGoals.size();i++) {		
			orderedGoals.get(i).setTotalOrder(agentId, i);
			System.err.println(orderedGoals.get(i));
		}
		
		agentGoalOrder.put(agentId, orderedGoals);
		
		return orderedGoals;
	}
	
	public void addJob(SubIntention i) {
		jobList.add(i);
	}
	
	public SubIntention getJob(Agent agent) {
		for(SubIntention i:jobList) {
			if(i instanceof MoveBoxSubIntention) {
				if(((MoveBoxSubIntention)i).getBox().getColor().equals(agent.getColor()))
					return i;
			}
			
		}
		return null;
	}
	
	public boolean isPositionReachable(Point agentPos, Point pos, boolean ignoreBoxes) {
		IHeuristicFunction function = new HeuristicPathFunction(pos);
		AStar heuristic = new AStar(function);
		BestFirstSearch pathSearch = new BestFirstSearch(heuristic);
		pathSearch.addToFrontier(new PathNode(this, agentPos, pos, ignoreBoxes));
		while ( true ) {
			if ( pathSearch.frontierIsEmpty() ) {
				//System.err.println(i + "> " + goal + ": No path!" );
				return false;
			}

			PathNode leafNode = (PathNode)pathSearch.getAndRemoveLeaf();

			if ( leafNode.getPosition().equals(pos)) {				
				//System.err.println(i + "> " +  goal + ": path found!" );
				return true;
			}

			pathSearch.addToExplored( leafNode );
			for ( SearchNode n : leafNode.getExpandedNodes() ) {
				if ( !pathSearch.isExplored( n ) && !pathSearch.inFrontier( n ) ) {
					pathSearch.addToFrontier( n );
				}
			}
		} // while(true)
	}
	
	
}
