package client;

public class Greedy extends Heuristic {
	private Intention i;
	private int agentId;
	
	public Greedy(Intention i, int agentId) {
		this.i = i;
		this.agentId = agentId;
	}
	
	public int f( StrategyActionNode n) {
		return heuristic( n );
	}
	
	private int heuristic(StrategyActionNode world) {
		
		Box box = i.getBox();
		Goal goal = i.getGoal();
		return box.getPosition().distance(goal.getPosition());
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
