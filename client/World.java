package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	private int width;
	private int height;

	public World() {}

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
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLevelSize() {
		return width*height;
	}

	public List<Box> getBoxes() {
		return Collections.unmodifiableList(boxes);
	}

	public List<Goal> getGoals() {
		return Collections.unmodifiableList(goals);
	}

	public int getNumberOfUncompletedGoals() {
		int result = 0;
		for(Goal g: goals) {
			if(!isGoalCompleted(g))
				result++;
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

			if (isBoxAt(boxSrcPosition)
					&& isFreeCell(boxDestPosition)
					&& (!Command.isOpposite(command.dir1, command.dir2))) {
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
					&& command.dir1 != command.dir2) {
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

		int score = numSurroundedWalls      * 10000 +
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
	
	static boolean sorted = false;
	public void sortGoals() {
		
		if(sorted) return;
		sorted = true;
		
		goals.sort(new GoalComparator(this));		
		
		// Check paths
		boolean allChecked = false;
		while(!allChecked) {
			World copyOfWorld = new World(this);
			Point agentPos = new Point(copyOfWorld.getAgent(0).getPosition());
			for(int i=0;i<goals.size();i++) {			
				Goal g = goals.get(i);
				boolean pathExists = copyOfWorld.isPositionReachable(agentPos, g.getPosition(), true);
				if(pathExists) {
					copyOfWorld.addWall(g.getPosition().getX(), g.getPosition().getY());
					//agentPos = g.getPosition();
					if(i==goals.size()-1) {
						allChecked = true;
					}
				}
				else {
					//Collections.swap(goals, i, i-1);
					goals.remove(i);
					goals.add(0, g);
					break;
				}				
			}  //for(int i=0;i<goals.size();i++)
		}  //while(!allChecked) 
		
		for(int i=0;i<goals.size();i++) {		
			goals.get(i).setTotalOrder(i);
			System.err.println(goals.get(i));
		}
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
