package client.Heuristic;

import client.Box;
import client.Client.Agent;
import client.Point;
import client.SubIntention;
import client.Search.SearchNode;

public class HeuristicPlannerFunction implements IHeuristicFunction {
	private SubIntention intention;
	private int agentId;
	
	public HeuristicPlannerFunction(SubIntention intention, int agentId) {
		this.intention = intention;
		this.agentId = agentId;
	}	
	
	@Override
	public int heuristic(SearchNode node) {
		Box box = node.getWorld().getBoxById(intention.getBox().getId());
		Point targetPosition = intention.getEndPosition();
		Agent agent = node.getWorld().getAgent(agentId);
		int distanceFromAgentToBox = (agent.getPosition().distance(box.getPosition()));
		int distanceFromBoxToGoal = box.getPosition().distance(targetPosition); 
		int goalCount = node.getWorld().getNumberOfUncompletedGoals();
		return distanceFromAgentToBox + distanceFromBoxToGoal + goalCount;
	}
}
