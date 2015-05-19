package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import client.Client.Agent;
import client.Client.AgentStatus;
import client.Command.type;
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
	private static HashSet<Point> walls = new HashSet<Point>();
	private HashSet<Point> madeUpWalls = new HashSet<Point>();
	//private static List<Point> rechableCells = new ArrayList<Point>();
	
	private static Map<Integer, List<Point>> reachableCells = new HashMap<>();
	
	private int width;
	private int height;
	private Map<Integer, Intention> intentionMap = new HashMap<>();
	private List<SubIntention> jobList = new ArrayList<>();
	private Map<Integer, LinkedList<Command>> planMap = new HashMap<>();
	
	private Map<Integer, Integer[]> outerWallsX =  new HashMap<>();
	private Map<Integer, Integer[]> outerWallsY =  new HashMap<>();

	public Map<Integer, Intention> getIntentionMap() {
		return intentionMap;
	}
	
	public World() {
	}
	
	public World getSimplifiedCopy(int agentId) {
		World copy = new World(this);
		Agent a = copy.getAgent(agentId);
		copy.agents.clear();
		copy.addAgent(a);
		return copy;
	}

	public World(World old) {
		for (Box box : old.boxes) {
			this.boxes.add(new Box(box));
		}
		for (Agent agent : old.agents) {
			this.agents.add(agent.CloneAgent());
		}
		for (Point wall : old.madeUpWalls) {
			this.madeUpWalls.add(wall);
		}

		this.width = old.width;
		this.height = old.height;

		/* No deep-copy! (on purpose)
		 * 
		 * this.intentionMap = old.intentionMap; this.jobList = old.jobList;
		 * this.planMap = old.planMap;
		 * 
		 */

		for (Map.Entry<Integer, Intention> entry : old.intentionMap.entrySet()) {
			this.intentionMap.put(entry.getKey(), new Intention(entry.getValue()));
		}

		for (SubIntention job : old.jobList) {
			this.jobList.add(job.deepCopy());
		}

		for (Map.Entry<Integer, LinkedList<Command>> entry : old.planMap.entrySet()) {
			LinkedList<Command> cmds = new LinkedList<>();
			for (Command cmd : entry.getValue()) {
				cmds.add(cmd);
			}
			this.planMap.put(entry.getKey(), cmds);
		}
		
		for (Entry<Integer, Integer[]> entry : old.outerWallsX.entrySet()) {
			this.outerWallsX.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<Integer, Integer[]> entry : old.outerWallsY.entrySet()) {
			this.outerWallsY.put(entry.getKey(), entry.getValue());
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

	public List<Point> getRechableCells(int agentId) {
		return reachableCells.get(agentId);
	}

	public static void setRechableCells(List<Point> rechableCells, int agentId) {
		World.reachableCells.put(agentId, Collections.unmodifiableList(rechableCells));
	}

	public List<Box> getBoxes() {
		return Collections.unmodifiableList(boxes);
	}

	public List<Box> getBoxes(String color) {
		List<Box> result = new ArrayList<Box>();
		for (Box b : boxes) {
			if (b.getColor().equals(color))
				result.add(b);
		}
		return result;
	}

	public List<Goal> getGoals() {
		return Collections.unmodifiableList(goals);
	}

	public List<Goal> getCompletedGoals() {
		List<Goal> completedGoals = new ArrayList<Goal>();
		for (Goal g : goals) {
			if (isGoalCompleted(g))
				completedGoals.add(g);
		}
		return Collections.unmodifiableList(completedGoals);
	}

	public int getNumberOfUncompletedGoals() {
		int result = 0;
		for (Goal g : goals) {
			if (!isGoalCompleted(g)) {
				result++;
			}
		}
		return result;
	}
	
	public int getNumberOfUncompletedAndUnintendedGoals(int agentId) {
		int result = 0;
		String color = getAgent(agentId).getColor();
		
		if(intentionMap.get(agentId) != null)
			result++;
		
		for (Goal g : goals) {
			
			if (!isGoalCompleted(g) && isGoalAvailable(g)) {
				for(Box b: boxes) {
					if(b.getLetter() == g.getLetter()) {
						if(b.getColor().equals(color)) {
							result++;
						}
						break;
					}
				}				
			}
		}
		
		return result;
	}

	public List<Agent> getAgents() {
		return Collections.unmodifiableList(agents);
	}

	public Set<Point> getWalls() {
		return Collections.unmodifiableSet(walls);
	}

	public int getNumberOfAgents() {
		return agents.size();
	}

	public void setLevelSize(int width, int height) {
		this.width = width;
		this.height = height;
		
		for(int x= 0; x< width;x++) {
			int firstWall = 0;
			
			for(int y=0;y<height;y++) {
				if(walls.contains(new Point(x,y))) {
					firstWall = y;
					break;
				}
			}
			
			int lastWall = firstWall;
			for(int y=height-1;y>firstWall;y--) {
				if(walls.contains(new Point(x,y))) {
					lastWall = y;
					break;
				}
			}
			
			Integer[] values = new Integer[2];
			values[0] = firstWall;
			values[1] = lastWall;
			outerWallsX.put(x,values);
		}
		
		for(int y= 0; y< height;y++) {
			int firstWall = 0;
			
			for(int x=0;x<width;x++) {
				if(walls.contains(new Point(x,y))) {
					firstWall = x;
					break;
				}
			}
			int lastWall = firstWall;
			for(int x=width-1;x>firstWall;x--) {
				if(walls.contains(new Point(x,y))) {
					lastWall = x;
					break;
				}
			}
			Integer[] values = new Integer[2];
			values[0] = firstWall;
			values[1] = lastWall;
			outerWallsY.put(y,values);
		}				
		
	}

	public Agent getAgent(int id) {
		for(Agent agent:agents) {
			if(agent.getId() == id)
				return agent;
		}
		
		return null;
	}
	
	public Agent getAgentToMoveBox(Box box) {
		if(agents.size() == 1)
			return agents.get(0);
		
		// Check if there is a suitable agent who has nothing to do
		for(Agent agent:agents) {
			if(agent.getColor().equals(box.getColor())) {
				if((intentionMap.get(agent.getId()) == null) ||
					(getNumberOfUncompletedAndUnintendedGoals(agent.getId()) == 0)) {
						return agent;
				}
			}
		}		
		
		for(Agent agent:agents) {
			if(agent.getColor().equals(box.getColor()))
				return agent;
		}
		
		return null;
	}

	public void addBox(Box b) {
		boxes.add(b);
	}

	public void addWall(int x, int y) {
		walls.add(new Point(x, y));
	}

	public void addMadeUpWall(Point point) {
		madeUpWalls.add(point);
	}

	public void addGoal(Goal g) {
		goals.add(g);
	}

	public void addAgent(Agent a) {
		agents.add(a);
	}

	public boolean putIntention(int agentId, Box box, Goal goal) {
		if (!isIntentionAvailable(box, goal)) {
			return false;	
		}

		intentionMap.put(agentId, new Intention(goal, box));
		return true;
	}

	public boolean putPlan(int agentId, Queue<Command> commandQueue) {
		planMap.put(agentId, (LinkedList<Command>) commandQueue);
		return true;
	}
	
	public boolean clearPlan(int agentId) {
		planMap.remove(agentId);
		return true;
	}
	
	public boolean handleBlockingAgentConflict(Agent agent1, Agent agent2, LinkedList<Command> plan) {
		if((agent2.getStatus() != AgentStatus.ACTIVE) ||
				(getNumberOfUncompletedAndUnintendedGoals(agent2.getId())) == 0) {
			Logger.logLine("["+agent1.getId()+"]: Hey [" + agent2.getId() + "], get out of my way!");
			Point safePos = SafeSpotDetector.getSafeSpotForAgent(this, agent2.getId(), 
					Command.CommandsToPath(agent1.getPosition(), plan));
			agent2.setStatus(AgentStatus.ACTIVE);
			agent1.sleep(30);
			addJob(new TravelSubIntention(agent2.getPosition(), safePos, agent2.getId(), null, agent1.getId()));
			return true;
		}
		return false;
	}

	public boolean validPlan(int agentId) {
		// Check if the planned actions of the agent cause a conflict with one of the other agents' plans
		LinkedList<Command> plan = planMap.get(agentId);
		for (Map.Entry<Integer, LinkedList<Command>> entry : planMap.entrySet()) {
		//for (Agent otherAgent : agents) {
			Agent otherAgent = getAgent(entry.getKey());
			
			if ((agentId != otherAgent.getId())) {
				LinkedList<Command> otherPlan = planMap.get(otherAgent.getId());
				if(otherPlan == null)
					otherPlan = new LinkedList<Command>();
				
				boolean conflict = checkPlans(agentId, plan, otherAgent.getId(), otherPlan);

				if (conflict) {
					if(handleBlockingAgentConflict(getAgent(agentId), otherAgent, plan)) {
						return false;
					}
					else if (agentId > otherAgent.getId()) {						
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

		for (int i = 0; i < plan1.size(); i++) {
			Point newPos1 = pos1;
			Point boxPos1 = pos1;			
			newPos1 = pos1.move(plan1.get(i).dir1);

			if (plan1.get(i).actType == type.Push) {
				boxPos1 = newPos1.move(plan1.get(i).dir2);
			} else if (plan1.get(i).actType == type.Pull) {
				boxPos1 = pos1.move(plan1.get(i).dir2);
			}
			

			Point newPos2 = pos2;
			Point boxPos2 = pos2;
			if (agentId1 > agentId2) {
				if (i == 0) {
					newPos2 = a2.getNewPosition();
				}
				else if (i - 1 < plan2.size()) {
					newPos2 = pos2.move(plan2.get(i - 1).dir1);

					if (plan2.get(i - 1).actType == type.Push) {
						boxPos2 = newPos2.move(plan2.get(i - 1).dir2);
					} else if (plan2.get(i - 1).actType == type.Pull) {
						boxPos2 = pos2.move(plan2.get(i - 1).dir2);
					}
				}
			} else if (i < plan2.size()) {
				newPos2 = pos2.move(plan2.get(i).dir1);

				if (plan2.get(i).actType == type.Push) {
					boxPos2 = newPos2.move(plan2.get(i).dir2);
				} else if (plan2.get(i).actType == type.Pull) {
					boxPos2 = pos2.move(plan2.get(i).dir2);
				}
			}

			if (pos1.equals(pos2))
				return true;

			if (pos1.equals(newPos2))
				return true;

			if (pos1.equals(boxPos2))
				return true;

			if (newPos1.equals(pos2))
				return true;

			if (newPos1.equals(newPos2))
				return true;

			if (newPos1.equals(boxPos2))
				return true;

			if (boxPos1.equals(pos2))
				return true;

			if (boxPos1.equals(newPos2))
				return true;

			if (boxPos1.equals(boxPos2))
				return true;

			pos1 = newPos1;
			pos2 = newPos2;
		}

		return false;
	}

	public boolean validStep(int agentId) {
		// Check if the next step of the agent causes a conflict with one of the other agents' plans
		LinkedList<Command> plan = planMap.get(agentId);
		if (plan.isEmpty())
			return true;
		
		LinkedList<Command> nextStepPlan = new LinkedList<>();
		nextStepPlan.add(plan.get(0));

		for (Map.Entry<Integer, LinkedList<Command>> entry : planMap.entrySet()) {
			if (agentId != entry.getKey()) {
				Agent otherAgent = getAgent(entry.getKey());
				LinkedList<Command> otherPlan = planMap.get(otherAgent.getId());
	
				boolean conflict = checkPlans(agentId, nextStepPlan, otherAgent.getId(), otherPlan);
				if(conflict) {
					if(handleBlockingAgentConflict(getAgent(agentId), otherAgent, plan)) {
						return false;
					}
					else if (agentId > entry.getKey()) {						
						//LinkedList<Command> otherPlan = entry.getValue();					
						/* There is a conflict but the other agent has no intention to move
						if(otherPlan.isEmpty() && (getJob(otherAgent) == null) && 
								(otherAgent.getPosition().equals(otherAgent.getLastPosition()))) {
							PriorityQueue<SafePoint> safePoints = SafeSpotDetector.detectSafeSpots(this);
							Point movePos = safePoints.poll();
							this.addJob(new TravelSubIntention(movePos, otherAgent.getId(), null));
							
						}
						*/
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean updatePlan(int agentId) {
		planMap.get(agentId).poll();
		return true;
	}

	public void clearIntention(int agentId) {
		intentionMap.remove(agentId);
	}

	public boolean isIntentionAvailable(Box box, Goal goal) {
		for (Map.Entry<Integer, Intention> entry : intentionMap.entrySet()) {
			if (entry.getValue().getBox().equals(box) || entry.getValue().getGoal().equals(goal)) {
				return false;
			}
		}
		return true;
	}

	public boolean isGoalAvailable(Goal goal) {
		for (Map.Entry<Integer, Intention> entry : intentionMap.entrySet()) {
			if (entry.getValue().getGoal().equals(goal)) {
				return false;
			}
		}
		return true;
	}

	public Box getIntendedBoxForGoal(Goal goal) {
		for (Map.Entry<Integer, Intention> entry : intentionMap.entrySet()) {
			if (entry.getValue().getGoal().equals(goal)) {
				return entry.getValue().getBox();
			}
		}
		return null;
	}
	
	public boolean existsIntentionForBox(Box box) {
		for (Map.Entry<Integer, Intention> entry : intentionMap.entrySet()) {
			if (entry.getValue().getBox().getId() == box.getId()) {
				return true;
			}
		}
		return false;
	}

	public void printWorld() {
		Logger.logLine("==============================");
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Point p = new Point(x, y);
				if (isWallAt(p)) {
					Logger.log("+");
				} else if (isBoxAt(p)) {
					Logger.log("A");
				} else {
					boolean printFlag = false;
					for (Agent a : agents) {
						if (a.getPosition().equals(p)) {
							Logger.log("0");
							printFlag = true;
							break;
						}
					}
					if (!printFlag) {
						Logger.log(" ");
					}
				}
			}
			Logger.log("\n");
		}
		Logger.logLine("==============================");
	}

	public boolean isGoalCompleted(Goal goal) {
		Box box = getBoxAt(goal.getPosition());
		if (box == null) {
			return false;
		}
		return box.getLetter() == goal.getLetter();
	}

	public boolean isAgentAt(Point point) {
		for (Agent agent : agents) {
			if (agent.getPosition().equals(point)) {
				return true;
			}
		}
		return false;
	}

	public boolean isBoxAt(Point point) {
		for (Box box : boxes) {
			if (box.getPosition().equals(point)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWallAt(Point point) {
		if (walls.contains(point)) {
			return true;
		}

		if (madeUpWalls.contains(point)) {
			return true;
		}

		return false;
	}

	public boolean isGoalAt(Point point) {
		for (Goal goal : goals) {
			if (goal.getPosition().equals(point)) {
				return true;
			}
		}

		return false;
	}

	public Goal getGoalAt(Point point) {
		for (Goal goal : goals) {
			if (goal.getPosition().equals(point)) {
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
	
	public Agent getAgentAt(Point position) {
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).getPosition().equals(position)) {
				return agents.get(i);
			}
		}
		return null;
	}

	public Box getBoxById(int id) {
		for (Box box : this.getBoxes()) {
			if (box.getId() == id) {
				return box;
			}
		}
		return null;
	}

	/**
	 * Updates the world with a command an agent is executing.
	 * 
	 * @return boolean value which tells whether there is made a change.
	 */
	public void notifyAgent(int agentId) {		
		Agent a = this.getAgent(agentId);
		a.setStatus(AgentStatus.ACTIVE);	
		//a.clearIntention();
		
		/*List<SubIntention> removeJobs = new ArrayList<SubIntention>();
		for(SubIntention job: jobList) {
			if(job.getOwner() == agentId) {
				removeJobs.add(job);
			}
		}
		jobList.removeAll(removeJobs);*/
		
		// Delete all jobs! They might be outdated!
		while(!jobList.isEmpty()) {
			SubIntention si = jobList.get(0);
			a = this.getAgent(si.getOwner());
			a.setStatus(AgentStatus.ACTIVE);
			jobList.remove(0);				
		}
	}
	
	
	public boolean update(Agent agent, Command command) {
		
		if(command instanceof NotifyAgentCommand) {	
			int notifyId = ((NotifyAgentCommand)command).getAgentId();
			Logger.logLine(agent.getId() + ": Notify agent " + notifyId);
			notifyAgent(notifyId);		
			return true;
		} else if(command instanceof NoOpCommand) {			
			return true;
		} 
		
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
					&& (!Command.isOpposite(command.dir1, command.dir2))
					&& getBoxAt(boxSrcPosition).getColor().equals(agent.getColor())) {
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
					&& getBoxAt(boxSrcPosition).getColor().equals(agent.getColor())) {
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
		result = prime * result + Arrays.deepHashCode(agents.toArray());
		result = prime * result + Arrays.deepHashCode(boxes.toArray());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		World other = (World) obj;

		for (Box box : boxes) {
			if (!other.boxes.contains(box)) {
				return false;
			}
		}

		for (Agent agent : agents) {
			if (!other.agents.contains(agent)) {
				return false;
			}
		}
		return true;
	}

	public boolean isInnerGoal(Goal goal) {
		int numSurroundedWalls = 0;
		int numSurroundedGoals = 0;

		for (Command.dir dir : Command.dir.values()) {
			if (isWallAt(goal.getPosition().move(dir)))
				numSurroundedWalls++;
			if (isGoalAt(goal.getPosition().move(dir)))
				numSurroundedGoals++;
		}

		if ((numSurroundedWalls + numSurroundedGoals) == 4)
			return true;

		return false;
	}

	public int getGoalPriorityScore(Goal goal) {
		// Can be pre-computed because it doesn't change
		if (goal.getPriorityScore() >= 0)
			return goal.getPriorityScore();

		boolean innerGoal = isInnerGoal(goal);
		int numSurroundedWalls = 0;
		int numSurroundedInnerGoals = 0;
		int numSurroundedOuterGoals = 0;

		for (Command.dir dir : Command.dir.values()) {
			Point p = goal.getPosition().move(dir);
			if (isWallAt(p))
				numSurroundedWalls++;
			if (isGoalAt(p)) {
				if (isInnerGoal(getGoalAt(p)))
					numSurroundedInnerGoals++;
				else
					numSurroundedOuterGoals++;
			}
		}

		int score = 0;
		if (innerGoal)
			score += 50000;

		score += numSurroundedWalls * 10000 + numSurroundedInnerGoals * 1000 + numSurroundedOuterGoals * 100;

		if (innerGoal) {
			List<Goal> connectedGoals = new ArrayList<Goal>();
			connectedGoals.add(goal);
			getConnectedGoals(goal, connectedGoals);

			int minDistance = 99999;
			for (Goal g : connectedGoals) {
				int distance = g.getPosition().distance(goal.getPosition());
				if ((!isInnerGoal(g)) && (distance < minDistance)) {
					minDistance = distance;
				}
			}
			score += minDistance;
		}

		goal.setPriorityScore(score);
		return score;
	}

	public void getConnectedGoals(Goal goal, List<Goal> result) {
		for (Command.dir dir : Command.dir.values()) {
			Point p = goal.getPosition().move(dir);
			if (isGoalAt(p)) {
				Goal neighborGoal = getGoalAt(p);
				if (!result.contains(neighborGoal)) {
					result.add(neighborGoal);
					getConnectedGoals(neighborGoal, result);
				}
			}
		}
	}

	private Map<Integer, List<Goal>> agentGoalOrder;

	public List<Goal> getGoalOrderForAgent(int agentId) {
		
		if (agentGoalOrder == null)
			agentGoalOrder = new HashMap<Integer, List<Goal>>();

		if (agentGoalOrder.containsKey(agentId))
			return agentGoalOrder.get(agentId);

		// initial sorting according to goal priority score
		goals.sort(new GoalComparator(this));

		List<Goal> orderedGoals = new ArrayList<Goal>(goals);

		// remove goals, which cannot be completed by this agent
		List<Goal> removeGoals = new ArrayList<Goal>();
		for (Goal g : orderedGoals) {
			List<Box> boxes = getBoxes(getAgent(agentId).getColor());
			boolean goalCanBeCompleted = false;
			for (Box b : boxes) {
				if (b.getLetter() == g.getLetter()) {
					goalCanBeCompleted = true;
					break;
				}
			}
			if (!goalCanBeCompleted) {
				removeGoals.add(g);
			}
		}
		orderedGoals.removeAll(removeGoals);

		if (orderedGoals.isEmpty()) {
			return Collections.emptyList();
		}

		Point initialAgentPos = getAgent(agentId).getPosition();
		World initialCopyOfWorld = new World(this);

		/*
		 * for(Box b:boxes) {
		 * if(!b.getColor().equals(getAgent(agentId).getColor())) {
		 * initialCopyOfWorld.addWall(b.getPosition().getX(),
		 * b.getPosition().getY()); } }
		 */

		// Check paths
		boolean allChecked = false;
		while (!allChecked) {
			World copyOfWorld = new World(initialCopyOfWorld);
			Point agentPos = initialAgentPos;
			List<Box> boxes = copyOfWorld.getBoxes(getAgent(agentId).getColor());
			Collections.shuffle(boxes, new Random(System.currentTimeMillis()));
			for (int i = 0; i < orderedGoals.size(); i++) {
				Goal g = orderedGoals.get(i);
				boolean reachableBoxFound = false;
				for (Box b : boxes) {
					if (g.getLetter() == b.getLetter()) {
						if (copyOfWorld.isPositionReachable(agentPos, b.getPosition(), true, true, agentId)) {
							agentPos = b.getPosition();
							boxes.remove(b);
							reachableBoxFound = true;
							break;
						}
					}
				}
				
				if(!reachableBoxFound) {
					orderedGoals.remove(i);
					if (i != 0) {
						orderedGoals.add(0, g);
					}
					break;
				}				

				boolean pathExists = copyOfWorld.isPositionReachable(agentPos, g.getPosition(), true, true,agentId);
				if (pathExists) {
					copyOfWorld.addMadeUpWall(g.getPosition());
					agentPos = g.getPosition();
					if (i == orderedGoals.size() - 1) {
						allChecked = true;
					}
				} else {
					// Collections.swap(goals, i, i-1);
					
					orderedGoals.remove(i);
					if (i != 0) {
						orderedGoals.add(0, g);
					}
					
					break;
				}
			} // for(int i=0;i<goals.size();i++)
		} // while(!allChecked)

		agentGoalOrder.put(agentId, orderedGoals);

		return orderedGoals;
	}

	public void addJob(SubIntention i) {
		jobList.add(i);
	}

	public SubIntention getJob(Agent agent) {
		for (SubIntention i : jobList) {
			if (i instanceof MoveBoxSubIntention) {
				if (((MoveBoxSubIntention) i).getBox().getColor().equals(agent.getColor())) {
					return i;
				}
			}
			else if (i instanceof TravelSubIntention) {
				if (((TravelSubIntention) i).getAgentId() == agent.getId()) {
					return i;
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets the job for the agent and if one is found, is it removed from the world.
	 */
	public SubIntention popJob(Agent agent) {
		SubIntention job = getJob(agent);
		if(job != null) {
			removeJob(job);
		}
		return job;
	}
	
	public void removeJob(SubIntention job) {
		jobList.remove(job);
	}

	public boolean isPositionReachable(Point startPos, Point endPos, boolean ignoreBoxes, boolean ignoreAgents, int movingAgentId) {
		IHeuristicFunction function = new HeuristicPathFunction(this, endPos);
		AStar heuristic = new AStar(function);
		BestFirstSearch pathSearch = new BestFirstSearch(heuristic);
		pathSearch.addToFrontier(new PathNode(this, startPos, endPos, ignoreBoxes, ignoreAgents, movingAgentId));
		while (true) {
			if (pathSearch.frontierIsEmpty()) {
				return false;
			}

			PathNode leafNode = (PathNode) pathSearch.getAndRemoveLeaf();

			if (leafNode.getPosition().equals(endPos)) {
				return true;
			}

			pathSearch.addToExplored(leafNode);
			for (SearchNode n : leafNode.getExpandedNodes()) {
				if (!pathSearch.isExplored(n) && !pathSearch.inFrontier(n)) {
					pathSearch.addToFrontier(n);
				}
			}
		} // while(true)
	}
	
	
	public boolean validateJob(SubIntention subIntention, Agent agent) {
		
		if(subIntention instanceof TravelSubIntention) {
			TravelSubIntention travelSub = (TravelSubIntention) subIntention;
			if(travelSub.getStartPosition().equals(agent.getPosition())) {
				return true;
			}
		}
		else if(subIntention instanceof MoveBoxSubIntention) {
			MoveBoxSubIntention moveSub = (MoveBoxSubIntention) subIntention;
			if(moveSub.getStartPosition().equals(getBoxById(moveSub.getBox().getId()).getPosition())) {
				return true;
			}
		}
		
		return false;
	}
	
	public void clearPath(Agent agent, Point startPos, Point endPos, Point block) {		
		if(block.getX() != -1) {
			int x = block.getX();
			int firstWall = outerWallsX.get(x)[0];
			int lastWall = outerWallsX.get(x)[1];
			
			int potentialY = 0;
			int minObstacleCount = 100;
			for(int y=firstWall+1;y<lastWall;y++) {
				if(!isPositionReachable(startPos, new Point(x,y), true, true, agent.getId()))
					continue;
				
				int obstacleCount = 0;
				if(isCellBlocked(agent, new Point(x,y))) obstacleCount++;
				if(isCellBlocked(agent, new Point(x+1,y))) obstacleCount++;			
				if(isCellBlocked(agent, new Point(x-1,y))) obstacleCount++;
				if(isCellBlocked(agent, new Point(x,y+1))) obstacleCount++;
				if(isCellBlocked(agent, new Point(x,y-1))) obstacleCount++;
				
				if(minObstacleCount > obstacleCount) {
					minObstacleCount = obstacleCount;
					potentialY = y;
				}
				else if(minObstacleCount == obstacleCount) {
					if(startPos.distance(new Point(x,y)) > startPos.distance(new Point(x,potentialY))) {
						potentialY = y;
					}
				}
			}
			
			if(isCellBlocked(agent, new Point(x,potentialY))) {
				freeCell(agent, new Point(x,potentialY));				
			}
			if(isCellBlocked(agent, new Point(x+1,potentialY))) {
				freeCell(agent, new Point(x+1,potentialY));				
			}
			if(isCellBlocked(agent, new Point(x-1,potentialY))) {
				freeCell(agent, new Point(x-1,potentialY));				
			}
			if(isCellBlocked(agent, new Point(x,potentialY+1))) {
				freeCell(agent, new Point(x,potentialY+1));				
			}
			if(isCellBlocked(agent, new Point(x,potentialY-1))) {
				freeCell(agent, new Point(x,potentialY-1));				
			}			
			
		}
		else {
			int y = block.getY();
			int firstWall = outerWallsY.get(y)[0];
			int lastWall = outerWallsY.get(y)[1];
			
			int potentialX = 0;
			int minObstacleCount = 100;
			for(int x=firstWall+1;x<lastWall;x++) {
				if(!isPositionReachable(startPos, new Point(x,y), true, true, agent.getId()))
					continue;
				
				int obstacleCount = 0;
				if(isCellBlocked(agent, new Point(x,y))) obstacleCount++;
				if(isCellBlocked(agent, new Point(x+1,y))) obstacleCount++;			
				if(isCellBlocked(agent, new Point(x-1,y))) obstacleCount++;
				if(isCellBlocked(agent, new Point(x,y+1))) obstacleCount++;
				if(isCellBlocked(agent, new Point(x,y-1))) obstacleCount++;
				
				if(minObstacleCount > obstacleCount) {
					minObstacleCount = obstacleCount;
					potentialX = x;
				}
				else if(minObstacleCount == obstacleCount) {
					if(startPos.distance(new Point(x,y)) > startPos.distance(new Point(potentialX,y))) {
						potentialX = x;
					}
				}
			}
			
			if(isCellBlocked(agent, new Point(potentialX,y))) {
				freeCell(agent, new Point(potentialX,y));				
			}
			if(isCellBlocked(agent, new Point(potentialX+1,y))) {
				freeCell(agent, new Point(potentialX+1,y));				
			}
			if(isCellBlocked(agent, new Point(potentialX-1,y))) {
				freeCell(agent, new Point(potentialX-1,y));				
			}
			if(isCellBlocked(agent, new Point(potentialX,y+1))) {
				freeCell(agent, new Point(potentialX,y+1));				
			}
			if(isCellBlocked(agent, new Point(potentialX,y-1))) {
				freeCell(agent, new Point(potentialX,y-1));				
			}
		}
		
	}
	
	public void freeCell(Agent agent, Point cell) {
		Agent a = getAgentAt(cell);
		if(a != null && a.getId() != agent.getId()) {
			handleBlockingAgentConflict(agent, a, null);
			return;
		}
		
		Box b = getBoxAt(cell);
		if(b != null && (!b.getColor().equals(agent.getColor()))) {
			Point p = SafeSpotDetector.getSafeSpotForBox(this, b, null);
			MoveBoxSubIntention mbsi = new MoveBoxSubIntention(b, p, null, agent.getId());
			addJob(mbsi);
		}
	}
	
	public boolean isCellBlocked(Agent agent, Point cell) {
		Agent a = getAgentAt(cell);
		if(a != null && a.getId() != agent.getId())
			return true;
		
		Box b = getBoxAt(cell);
		if(b != null && (!b.getColor().equals(agent.getColor())))
				return true;
		
		return false;
	}
	
	public Point simplePathCheck(Agent agent, Point startPos, Point endPos) {
		int minX = Math.min(startPos.getX(), endPos.getX());
		int maxX = Math.max(startPos.getX(), endPos.getX());		
		int minY = Math.min(startPos.getY(), endPos.getY());
		int maxY = Math.max(startPos.getY(), endPos.getY());
		
		for(int x= minX; x< maxX;x++) {
			int firstWall = outerWallsX.get(x)[0];
			int lastWall = outerWallsX.get(x)[1];
			
			boolean freeCellFound = false;
			for(int y=firstWall+1;y<lastWall;y++) {
				if(checkPoint(new Point(x,y), startPos, endPos,agent) && 
					checkPoint(new Point(x+1,y), startPos, endPos,agent) &&
						isPositionReachable(startPos, new Point(x,y), true, true, agent.getId()) &&
						(checkPoint(new Point(x-1,y), startPos, endPos,agent) || 
						checkPoint(new Point(x,y+1), startPos, endPos,agent) || 
						checkPoint(new Point(x,y-1), startPos, endPos,agent))) {
					freeCellFound = true;
					break;	
				}
			}
			if(!freeCellFound) {
				return new Point(x,-1);
			}
		}
		
		for(int y= minY; y< maxY;y++) {
			int firstWall = outerWallsY.get(y)[0];
			int lastWall = outerWallsY.get(y)[1];
			
			boolean freeCellFound = false;
			for(int x=firstWall+1;x<lastWall;x++) {
				if(checkPoint(new Point(x,y), startPos, endPos,agent) && 
					checkPoint(new Point(x,y+1), startPos, endPos,agent) &&
					isPositionReachable(startPos, new Point(x,y), true, true, agent.getId()) &&
						(checkPoint(new Point(x-1,y), startPos, endPos,agent) || 
						checkPoint(new Point(x+1,y), startPos, endPos,agent) || 
						checkPoint(new Point(x,y-1), startPos, endPos,agent))) {
					freeCellFound = true;
					break;						
				}
			}
			if(!freeCellFound) {
				return new Point(-1,y);
			}
		}
		
		return null;
	}
	
	private boolean checkPoint(Point p, Point startPos, Point endPos,Agent agent) {
		Box b = getBoxAt(p);
		if(		isFreeCell(p) || 
				p.equals(agent.getPosition()) || 
				p.equals(startPos) || 
				p.equals(endPos) || 
				(b!=null && b.getColor().equals(agent.getColor())))
			return true;
		
		return false;
	}
}


