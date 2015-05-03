package client.Heuristic;

import client.Box;
import client.Goal;
import client.Point;
import client.World;
import client.Search.PathNode;
import client.Search.SearchNode;

public class HeuristicPathFunction implements IHeuristicFunction {
	private Point goalPosition;
	private World world;

	public HeuristicPathFunction(World world, Point goalPosition) {
		this.goalPosition = goalPosition;
		this.world = world;
	}
	
	@Override
	public int heuristic(SearchNode n) {
		if(!(n instanceof PathNode)) {
			throw new RuntimeException("SearchNode is not of type PathNode");
		}
		PathNode node = (PathNode)n;
		int blockedCells = 0;
		for(Point p : node.extractListOfPossitions()) {
			if(!world.isFreeCell(p)) {
				blockedCells++;
			}
		}
		return goalPosition.distance(node.getPosition()) + blockedCells*3;
	}
}
