package client.Heuristic;

import client.StrategyActionNode;

public class AStar extends Heuristic {
	private IHeuristicFunction function;
	
	public AStar(IHeuristicFunction function) {
		this.function = function;
	}
	
	public int f( StrategyActionNode n) {
		return function.heuristic( n ) + n.getStepCount();
	}
	
	@Override
	public int compare(StrategyActionNode node1, StrategyActionNode node2) {
		return this.f( node1 ) - this.f( node2 );
	}

	@Override
	public String toString() {
		return "A* Heuristic";
	}
}
