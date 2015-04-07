package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import client.Client.Agent;
import client.Command.dir;

public class World {
	private List<Box> boxes = new ArrayList<Box>();
	private static List<Goal> goals = new ArrayList<Goal>();
	private List<Agent> agents = new ArrayList<Agent>();
	private static List<Point> walls = new ArrayList<Point>();
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
		this.width = old.width;
		this.height = old.height;
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
		return Character.toLowerCase(box.getLetter()) == goal.getLetter();
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

	public int update(Agent a, Command c) {
		
		switch (c.actType) {
		case Move: {
			Point agentDestPosition = a.getPosition().move(c.dir1);
			if (isFreeCell(agentDestPosition)) {
				a.setPosition(agentDestPosition);
			}
			break;
		}
		case Push: {

			Point boxSrcPosition = a.getPosition().move(c.dir1);
			Point boxDestPosition = boxSrcPosition.move(c.dir2);

			if (isBoxAt(boxSrcPosition) 
					&& isFreeCell(boxDestPosition)
					&& (!Command.isOpposite(c.dir1, c.dir2))) {
				a.setPosition(boxSrcPosition);
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(boxDestPosition);
			}

			break;
		}
		case Pull: {
			Point agentDestPosition = a.getPosition().move(c.dir1);
			Point boxSrcPosition = a.getPosition().move(c.dir2);

			if (isBoxAt(boxSrcPosition)
					&& isFreeCell(agentDestPosition)
					&& c.dir1 != c.dir2) {
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(a.getPosition());
				a.setPosition(agentDestPosition);
			}
			break;
		}
		}

		return 0;
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
		boolean innerGoal = isInnerGoal(goal);
		int numSurroundedWalls = 0;
		int numSurroundedGoals = 0;
		
		for(Command.dir dir:Command.dir.values()) {
			if(isWallAt(goal.getPosition().move(dir)))
				numSurroundedWalls++;
			if(isGoalAt(goal.getPosition().move(dir)))
				numSurroundedGoals++;
		}
		
		int score = numSurroundedWalls * 1000 +
				    numSurroundedGoals *  100;
		
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
	
}
