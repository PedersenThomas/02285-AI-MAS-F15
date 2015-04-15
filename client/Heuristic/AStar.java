package client.Heuristic;

import client.Search.SearchNode;

public class AStar extends Heuristic {
	private IHeuristicFunction function;
	
	public AStar(IHeuristicFunction function) {
		this.function = function;
	}
	
	public int f( SearchNode n) {
		return function.heuristic( n ) + n.getStepCount();
	}
	
	@Override
	public int compare(SearchNode node1, SearchNode node2) {
		return this.f( node1 ) - this.f( node2 );
	}

	@Override
	public String toString() {
		return "A* Heuristic";
	}
}
