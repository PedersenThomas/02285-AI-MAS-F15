package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import client.Heuristic.AStar;
import client.Heuristic.HeuristicPathFunction;
import client.Search.BestFirstSearch;
import client.Search.PathNode;
import client.Search.SearchNode;

public class IntentionDecomposer {
	
	public static ArrayList<SubIntention> decomposeIntention(Intention intention, World world, int agentId){
		ArrayList<SubIntention> subIntentions = new ArrayList<SubIntention>();
		Point boxPosition = intention.getBox().getPosition();
		Point agentPosition = world.getAgent(agentId).getPosition();
		Queue<Point> path = findPathFromAgentToBox(world, agentPosition, boxPosition);
		World newWorld = world;
		
		for(Point point : path) {
			Box box = world.getBoxAt(point);
			if (box != null) {
				List<Point> safeSpots = SafeSpotDetector.detectSafeSpots(newWorld);
				Point savePosition = safeSpots.get(0);
				subIntentions.add(new SubIntention(box, savePosition));
				newWorld = new World(newWorld);
				newWorld.getBoxById(box.getId()).setPosition(savePosition);
			}
		}
		
		return subIntentions;
	};

	private static Queue<Point> findPathFromAgentToBox(World world, Point agentPosition,
			Point boxPosition) {
		HeuristicPathFunction pathFunction = new HeuristicPathFunction(
				boxPosition);
		AStar heuristic = new AStar(pathFunction);
		BestFirstSearch search = new BestFirstSearch(heuristic);

		search.addToFrontier(new PathNode(world, agentPosition, boxPosition, true));
		while (true) {
			if (search.frontierIsEmpty()) {
				throw new RuntimeException("Unable to reach the target position");
			}

			PathNode leafNode = (PathNode) search.getAndRemoveLeaf();

			if (leafNode.getPosition().equals(boxPosition)) {
				return leafNode.extractListOfPossitions();
			}

			search.addToExplored(leafNode);
			for (SearchNode n : leafNode.getExpandedNodes()) {
				if (!search.isExplored(n) && !search.inFrontier(n)) {
					search.addToFrontier(n);
				}
			}
		}
	}
}
