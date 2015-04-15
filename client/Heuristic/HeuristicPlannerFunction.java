package client.Heuristic;

import client.Box;
import client.Goal;
import client.Intention;
import client.Client.Agent;
import client.Search.SearchNode;

public class HeuristicPlannerFunction implements IHeuristicFunction {
	private Intention intention;
	private int agentId;
	
	public HeuristicPlannerFunction(Intention intention, int agentId) {
		this.intention = intention;
		this.agentId = agentId;
	}	
	
	@Override
	public int heuristic(SearchNode node) {
		Box box = node.getWorld().getBoxById(intention.getBox().getId());
		Goal goal = intention.getGoal();
		Agent agent = node.getWorld().getAgent(agentId);
		int distanceFromAgentToBox = (agent.getPosition().distance(box.getPosition()));
		int distanceFromBoxToGoal = box.getPosition().distance(goal.getPosition()); 
		int goalCount = node.getWorld().getNumberOfUncompletedGoals();
		return distanceFromAgentToBox + distanceFromBoxToGoal + goalCount;
	}
}
