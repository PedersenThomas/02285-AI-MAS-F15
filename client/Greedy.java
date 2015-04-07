package client;

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
	}
	
	private int heuristic(StrategyActionNode node) {
		Box box = node.getWorld().getBoxById(i.getBox().getId());
		Goal goal = i.getGoal();
		int distanceFromBoxToGoal = box.getPosition().distance(goal.getPosition()); 
		int goalCount = node.getWorld().getGoals().size() - node.getWorld().getNumberOfUncompletedGoals();
		return distanceFromBoxToGoal + goalCount;
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
