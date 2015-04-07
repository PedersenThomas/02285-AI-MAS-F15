package client;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.Client.Agent;

public class Intention {
	private Goal goal;
	private Box box;
	
	//TODO This should be beliefs not world.
	public static Intention deliberate(World world, Agent agent) {
		//Generate the desires, that is, everything the agent might want to achieve.
		List<Box> boxes = world.getBoxes();	
		//List<Agent> agents = world.getAgents();  // only one agent
		List<Goal> goals = world.getGoals();
		
		List<Intention> intentions = new ArrayList<>();		
		Map<Goal,Map.Entry<Box,Integer>> minGoalDistance = new HashMap<Goal,Map.Entry<Box,Integer>>();
		
		// determine for each goal its closest box
		for(Goal goal:goals) {
			if(!world.isGoalCompleted(goal)) {
				for(Box box:boxes) {
					if(box.getLetter() == Character.toUpperCase(goal.getLetter()))	{					
						if(minGoalDistance.containsKey(goal)) {
							if(minGoalDistance.get(goal).getValue() > goal.getPosition().distance(box.getPosition())) {
								minGoalDistance.put(goal, new AbstractMap.SimpleEntry<Box,Integer>(box, goal.getPosition().distance(box.getPosition())));
							}
						}
						else {
							minGoalDistance.put(goal, new AbstractMap.SimpleEntry<Box,Integer>(box, goal.getPosition().distance(box.getPosition())));
						}
					}			
				}	
			}
		}		

		// Create for each unsatisfied goal an intention
		for (Map.Entry<Goal,Map.Entry<Box,Integer>> entry : minGoalDistance.entrySet())
		{
			intentions.add(new Intention(entry.getKey(), entry.getValue().getKey()));
		}
		
		// Check if all goals are satisfied
		if(intentions.isEmpty())
			return null;
		
		int minAgentDistance = intentions.get(0).box.getPosition().distance(agent.getPosition());
		int resultIdx = 0;
		
		// determine which box is closest to the agent
		for(int i=1;i<intentions.size();i++) {
			int distance = intentions.get(i).box.getPosition().distance(agent.getPosition());
			if(distance < minAgentDistance) {
				resultIdx = i;
				minAgentDistance = distance;
			}
		}

		return intentions.get(resultIdx);
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
