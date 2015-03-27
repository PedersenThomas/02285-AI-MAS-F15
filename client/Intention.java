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
	public static Intention deliberate(World world) {
		//Generate the desires, that is, everything the agent might want to achieve.
		List<Box> boxes = world.getBoxes();	
		List<Agent> agents = world.getAgents();
		List<Goal> goals = world.getGoals();
		
		List<Intention> intentions = new ArrayList<>();
		
		Map<Goal,Map.Entry<Box,Integer>> minGoalDistance = new HashMap<Goal,Map.Entry<Box,Integer>>();
		
		for(Box box:boxes) {
			for(Goal goal:goals) {
				if((box.getLetter()-'A' == goal.getLetter()-'a') &&
					(!box.getPosition().equals(goal.getPosition())))	{
					intentions.add(new Intention(goal, box));	
					
					if(minGoalDistance.containsKey(goal)) {
						if(minGoalDistance.get(goal).getValue() > goal.getPosition().distance(box.getPosition())) {
							minGoalDistance.put(goal, new AbstractMap.SimpleEntry(box, goal.getPosition().distance(box.getPosition())));
						}
					}
					else {
						minGoalDistance.put(goal, new AbstractMap.SimpleEntry(box, goal.getPosition().distance(box.getPosition())));
					}
				}			
			}			
		}
		
		
		//Generate the intentions by choosing between competing desires and commit to some of them.
		List<Intention> remove_list = new ArrayList<Intention>();
		for(Intention intention:intentions) {
			if(minGoalDistance.get(intention.getGoal()).getKey() != intention.getBox()) {
				remove_list.add(intention);
			}
		}
		intentions.removeAll(remove_list);

		return intentions.get(0);
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
		return "IntenTion = Box: " + box + " Goal: " + goal;	
	}
}
