package client;

import client.Client.Agent;

public class Greedy extends Heuristic {
	private Intention i;
	private int agentId;
	
	public Greedy(Intention i, int agentId) {
		this.i = i;
		this.agentId = agentId;
	}
	
	public int f( StrategyActionNode n) {
		//TODO - This has now turned into A*.
		return heuristic( n ) + n.getStepCount();
		//return heuristic( n ) + (n.getStepCount()*n.getStepCount());  // for testing
	}
	
	private int heuristic(StrategyActionNode node) {
		Box box = node.getWorld().getBoxById(i.getBox().getId());
		Goal goal = i.getGoal();
		Agent agent = node.getWorld().getAgent(agentId);
		int distanceFromAgentToBox = (agent.getPosition().distance(box.getPosition()));
		int distanceFromBoxToGoal = box.getPosition().distance(goal.getPosition()); 
		// I think it should be like this?
		int goalCount = /*node.getWorld().getGoals().size() -*/ node.getWorld().getNumberOfUncompletedGoals();
		return distanceFromAgentToBox + distanceFromBoxToGoal + goalCount;
	}
	
	@Override
	public int compare(StrategyActionNode node1, StrategyActionNode node2) {
		return this.f( node1 ) - this.f( node2 );
	}

	@Override
	public String toString() {
		return "Greedy Heuristic";
	}
}
