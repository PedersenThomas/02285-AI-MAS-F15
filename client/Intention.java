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
		List<Goal> goals = new ArrayList<Goal>(world.getGoals());
		
		goals.sort(new GoalComparator(world));
		
		List<Intention> intentions = new ArrayList<Intention>();		
		Map<Goal,Map.Entry<Box,Integer>> minGoalDistance = new HashMap<Goal,Map.Entry<Box,Integer>>();
		
		// determine for each goal its closest box
		for(Goal goal:goals) {
			if(!world.isGoalCompleted(goal)) {
				for(Box box:boxes) {
					if(box.getLetter() == goal.getLetter())	{					
						
						Boolean boxSuitable = true;
						Integer boxToGoalDistance = goal.getPosition().distance(box.getPosition());
						for(Goal otherGoal:goals) {
							if((otherGoal != goal) &&
							   (otherGoal.getLetter() == goal.getLetter())) {
								if(otherGoal.getPosition().equals(box.getPosition())) {
									boxSuitable = false;
								}
							}							
						}
							
						if(boxSuitable) {
							if(minGoalDistance.containsKey(goal)) {
								if(minGoalDistance.get(goal).getValue() > boxToGoalDistance) {
									minGoalDistance.put(goal, new AbstractMap.SimpleEntry<Box,Integer>(box, boxToGoalDistance));
								}
							}
							else {
								minGoalDistance.put(goal, new AbstractMap.SimpleEntry<Box,Integer>(box, boxToGoalDistance));
							}
						}							
					}			
				}	
			}
		}	
		
		for(int i=0;i<goals.size();i++) {			
			Goal intendedGoal = goals.get(i);
			if(minGoalDistance.containsKey(intendedGoal))		
			  return new Intention(intendedGoal, minGoalDistance.get(intendedGoal).getKey());
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
	
	@Override
	public String toString() {
		return "Intention = Box: " + box + " Goal: " + goal;	
	}
}
