package client.Heuristic;

import client.Point;
import client.Search.PathNode;
import client.Search.SearchNode;

public class HeuristicPathFunction implements IHeuristicFunction {
	private Point goalPosition;

	public HeuristicPathFunction(Point goalPosition) {
		this.goalPosition = goalPosition;
	}
	
	@Override
	public int heuristic(SearchNode n) {
		if(!(n instanceof PathNode)) {
			throw new RuntimeException("SearchNode is not of type PathNode");
		}
		PathNode node = (PathNode)n;
		return goalPosition.distance(node.getPosition());
	}
}
