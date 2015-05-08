package client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import client.Client.Agent;
import client.Command.dir;
import client.Heuristic.AStar;
import client.Heuristic.HeuristicPathFunction;
import client.Search.BestFirstSearch;
import client.Search.PathNode;
import client.Search.SearchNode;

public class IntentionDecomposer {
	
	public static ArrayList<SubIntention> decomposeIntention(Intention intention, World world, int agentId){
		ArrayList<SubIntention> subIntentions = new ArrayList<SubIntention>();
		
		Point agentPosition = world.getAgent(agentId).getPosition();
		Point boxPosition = intention.getBox().getPosition();
		Point goalPosition = intention.getGoal().getPosition();

		World currentWorld = world;
		Queue<Point> pathFromAgentToBox = findPath(currentWorld, agentPosition, boxPosition);
		
		if(!canGoalBeCompletedByPull(world, goalPosition, agentPosition)) {
			// do something intelligent :)
		}	
		
		
		if(!world.isPositionReachable(agentPosition, boxPosition, false)) {
			//SubIntention to clear path from Agent to Box.
			currentWorld = moveBoxesOnPathToSafePlaces(currentWorld, pathFromAgentToBox, subIntentions, intention,agentId);

			Logger.debug("----------- Agent Path ----------");
			for (Point point : pathFromAgentToBox) {
				Logger.debug("" + point);
			}
		}		

		if(!world.isPositionReachable(boxPosition, goalPosition, false)) {
			//SubIntention to clear path from Box to Goal.
			Queue<Point> pathFromBoxToGoal = findPath(currentWorld, boxPosition, goalPosition);
			moveBoxesOnPathToSafePlaces(currentWorld, pathFromBoxToGoal, subIntentions, intention,agentId);

			Logger.debug("----------- Path ----------");
			for (Point point : pathFromBoxToGoal) {
				Logger.debug("" + point);
			}
		}	
		
		//SubIntention for moving box to goal.
		subIntentions.add(new TravelSubIntention(intention.getBox().getPosition(), agentId,intention, agentId));
		subIntentions.add(new MoveBoxSubIntention(intention.getBox(), goalPosition,intention, agentId));
		

		Logger.debug("----------- Intention Decomposer START----------");
		for (SubIntention subIntention : subIntentions) {
			Logger.debug(subIntention);
		}
		Logger.debug("-- Agent info --");
		Logger.debug(world.getAgent(agentId));
		Logger.debug("----------- Intention Decomposer END----------");
		return subIntentions;
	}

	private static World moveBoxesOnPathToSafePlaces(World world, Queue<Point> path, 
			                                         ArrayList<SubIntention> subIntentions, Intention intention, int agentId) {
		World newWorld = world;

		for(Point point : path) {
			Box box = world.getBoxAt(point);
			if (box != null) {
				Agent agent = world.getAgentToMoveBox(box);
				PriorityQueue<SafePoint> safeSpots = SafeSpotDetector.detectSafeSpots(newWorld, agent.getId());

				Logger.debug("-----------  Safe spots ----------");
				for (SafePoint safespot : safeSpots) {
					Logger.debug("" + safespot);
				}
				Point safePosition = null;
				//Find a safepoint not on the path.
				for (SafePoint safespot : safeSpots) {
					if(!path.contains(safespot)) {
						safePosition = safespot;
						break;
					}
				}	
				
				// Idea: get all connected cells from the safePosition which are not on the path and choose one of it
				/*ConnectedComponent cc = new ConnectedComponent(world);
				List<Point> points = cc.findPointsInConnectedComponent(safePosition);
				List<Point> connectedPoints = new ArrayList<Point>();
				for(Point p:points) {
					if(!path.contains(p)) {
						connectedPoints.add(p);
					}
				}
				
				// Find the spot closest to the path
				Point closestSafePoint = safePosition;
				int minDistance = 100;
				for(Point p:connectedPoints) {
					for(Point p2:path) {
						if(p.distance(p2) < minDistance) {
							closestSafePoint = p;
							minDistance = p.distance(p2);
						}
					}					
				}
				safePosition = closestSafePoint;
				*/				
				
				//Point savePosition = safeSpots.poll();
				subIntentions.add(new MoveBoxSubIntention(box, safePosition,intention,agentId));
				newWorld = new World(newWorld);
				newWorld.getBoxById(box.getId()).setPosition(safePosition);
			}
		}
		return newWorld;
	};
	
	public static boolean canGoalBeCompletedByPull(World world, Point goalPosition, Point agentPosition){
		//check if goal can be completed by pull
		int count = 0;
		for(Command.dir dir: Command.dir.values()) {
			if(!world.isFreeCell(goalPosition.move(dir))) {
				if(!agentPosition.equals(goalPosition.move(dir))) {
					count++;
				}
			}
		}
		return count != 3;
	}

	private static Queue<Point> findPath(World world, Point sourcePosition, Point targetPosition) {
		HeuristicPathFunction pathFunction = new HeuristicPathFunction(world,targetPosition);
		AStar heuristic = new AStar(pathFunction);
		BestFirstSearch search = new BestFirstSearch(heuristic);

		search.addToFrontier(new PathNode(world, sourcePosition, targetPosition, true));
		while (true) {
			if (search.frontierIsEmpty()) {
				throw new RuntimeException("Unable to reach the target position");
			}

			PathNode leafNode = (PathNode) search.getAndRemoveLeaf();

			if (leafNode.getPosition().equals(targetPosition)) {
				Queue<Point> path = leafNode.extractListOfPossitions();
				//do not want to clear the objectives away from the path
				path.poll();
				removeLastFromQueue(path);

				return path;
			}

			search.addToExplored(leafNode);
			for (SearchNode n : leafNode.getExpandedNodes()) {
				if (!search.isExplored(n) && !search.inFrontier(n)) {
					search.addToFrontier(n);
				}
			}
		}
	}

	private static void removeLastFromQueue (Queue<Point> queue) {
		//This is a hack. But the queue do not have a function for removing the last item.
		LinkedList<Point> list = (LinkedList<Point>)queue;
		list.removeLast();
	}
}
