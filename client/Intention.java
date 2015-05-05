package client;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.Client.Agent;

public class Intention {
	private Goal goal;
	private Box box;
	//private Command.dir agentPos;  //currently not used
	
	public Intention(Intention old) {
		this.goal = new Goal(old.goal);
		this.box = new Box(old.box);
	}
	
	static class GoalComparator implements Comparator<Goal> {
		World world;
		public GoalComparator(World world) {
			this.world = world;
		}
		
	    @Override
	    public int compare(Goal a, Goal b) {
	        return world.getGoalPriorityScore(b) - world.getGoalPriorityScore(a);
	    }
	}
	
	//TODO This should be beliefs not world.
	public static Intention deliberate(World world, Agent agent) {
		//Generate the desires, that is, everything the agent might want to achieve.
		List<Box> boxes = world.getBoxes();	
		//List<Agent> agents = world.getAgents();  // only one agent
		List<Goal> goals = world.getGoalOrderForAgent(agent.getId());
				
		Map<Goal,Map.Entry<Box,Integer>> intentenionsMap = new HashMap<Goal,Map.Entry<Box,Integer>>();
		List<Box> takenBoxes = new ArrayList<>();		
		
		// determine for each goal its closest box
		for(Goal goal:goals) {
			if((!world.isGoalCompleted(goal)) && (world.isGoalAvailable(goal))) {
				boolean checkReachability = true;
				while(intentenionsMap.get(goal) == null) {
					for(Box box:boxes) {	
						// Check if agent can move box						
						if(!agent.getColor().equals(box.getColor()) || !world.isIntentionAvailable(box, goal)) {
							//System.err.println("++++++++++++++++++++++++++Intention is not available. " + box + "" + goal);
							continue;
						}
						
						// Check if box is already taken
						if(takenBoxes.contains(box))
							continue;
						
						if(box.getLetter() == goal.getLetter())	{
							if(checkReachability) {
								boolean isBoxReachable = world.isPositionReachable(agent.getPosition(), 
										                                           box.getPosition(), false);								
								if(!isBoxReachable)
									continue;
							}
							
							Integer boxToGoalDistance = goal.getPosition().distance(box.getPosition());	
							
							if(intentenionsMap.containsKey(goal)) {
								if(intentenionsMap.get(goal).getValue() > boxToGoalDistance) {
									intentenionsMap.put(goal, new AbstractMap.SimpleEntry<Box,Integer>(box, boxToGoalDistance));
								}
							}
							else {
								intentenionsMap.put(goal, new AbstractMap.SimpleEntry<Box,Integer>(box, boxToGoalDistance));
							}
														
						}			
					}
					checkReachability = false;
				}
				// Add the box to the list of taken boxes
				takenBoxes.add(intentenionsMap.get(goal).getKey());				
			}
			else {
				// If goal is already completed then add it to the list of taken boxes
				// so that goals with a lower priority can't take it away
				if(world.isGoalCompleted(goal))
					takenBoxes.add(world.getBoxAt(goal.getPosition()));		
				else
					takenBoxes.add(world.getIntendedBoxForGoal(goal));	
			}
		}	
		
		for(int i=0;i<goals.size();i++) {			
			Goal intendedGoal = goals.get(i);
			if(intentenionsMap.containsKey(intendedGoal)) {		
			  return new Intention(intendedGoal, intentenionsMap.get(intendedGoal).getKey());
			}
		}
		return null;
	}
	
	
	public Intention(Goal g, Box b) {
		this.goal = g;
		this.box = b;		
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}
	
	/* Currently not used
	public Command.dir getAgentPos() {
		return agentPos;
	}


	public void setAgentPos(Command.dir agentPos) {
		this.agentPos = agentPos;
	}
	*/
	
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		Intention other = (Intention) obj;
		if(!this.box.equals(other.box)) {
			return false;
		}
		if(!this.goal.equals(other.goal)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.box.hashCode() + this.goal.hashCode()*701;
	}


	@Override
	public String toString() {
		return "Intention = Box: " + box + " Goal: " + goal;	
	}
}
