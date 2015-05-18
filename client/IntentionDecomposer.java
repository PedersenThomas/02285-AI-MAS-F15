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
	public static class WorldSubIntentionsWrapper {
		public World world;
		ArrayList<SubIntention> subIntentions = new ArrayList<>();
		public WorldSubIntentionsWrapper(World world, ArrayList<SubIntention> subIntentions) {
			super();
			this.world = world;
			this.subIntentions = subIntentions;
		}
		
		
	}
	
	
	public static WorldSubIntentionsWrapper decomposeSubIntention(SubIntention intention, World world, int agentId){
		if(intention instanceof TravelSubIntention) {
			return decomposeTavelSubIntention((TravelSubIntention)intention, world, agentId);
		}
		else if(intention instanceof MoveBoxSubIntention) {
			return decomposeMoveBoxSubIntention((MoveBoxSubIntention)intention, world, agentId);
		}
		else
			return null;
	}
	
	public static WorldSubIntentionsWrapper decomposeTavelSubIntention(TravelSubIntention intention, World world, int agentId){
		ArrayList<SubIntention> subIntentions = new ArrayList<SubIntention>();
		
		Point agentPosition = world.getAgent(agentId).getPosition();
		Point endPosition = intention.getEndPosition();

		World currentWorld = world;
		Queue<Point> pathFromAgentToBox = findPath(currentWorld, agentPosition, endPosition);	
				
		
		if(!currentWorld.isPositionReachable(agentPosition, endPosition, false, true,agentId)) {
			//SubIntention to clear path from Agent to Box.
			currentWorld = moveBoxesOnPathToSafePlaces(currentWorld, pathFromAgentToBox, subIntentions, intention.getRootIntention(),agentId, false, intention.getOwner());

			Logger.logLine("----------- Agent Path ----------");
			for (Point point : pathFromAgentToBox) {
				Logger.logLine("" + point);
			}
		}
		
		subIntentions.add(intention);

		return new WorldSubIntentionsWrapper(currentWorld,subIntentions);
	}
	
	public static WorldSubIntentionsWrapper decomposeMoveBoxSubIntention(MoveBoxSubIntention intention, World world, int agentId){
		ArrayList<SubIntention> subIntentions = new ArrayList<SubIntention>();
		
		Point boxPosition = intention.getBox().getPosition();
		Point goalPosition = intention.getEndPosition();
		
		//if(!canGoalBeCompletedByPull(world, goalPosition, agentPosition)) {
		//	// do something intelligent :)
		//}	

		World currentWorld = world;
		
		Queue<Point> pathFromBoxToGoal = null;
		if(!currentWorld.isPositionReachable(boxPosition, goalPosition, false, true,-1)) {
			//SubIntention to clear path from Box to Goal.
			pathFromBoxToGoal = findPath(currentWorld, boxPosition, goalPosition);
			currentWorld = moveBoxesOnPathToSafePlaces(currentWorld, pathFromBoxToGoal, subIntentions, intention.getRootIntention(),agentId, true, intention.getOwner());

			Logger.logLine("----------- Path ----------");
			for (Point point : pathFromBoxToGoal) {
				Logger.logLine("" + point);
			}
		}	
		
		subIntentions.add(intention);

		return new WorldSubIntentionsWrapper(currentWorld,subIntentions);
	}
	
	public static ArrayList<SubIntention> decomposeIntention(Intention intention, World world, int agentId){
		ArrayList<SubIntention> subIntentions = new ArrayList<SubIntention>();
		
		Point agentPosition = world.getAgent(agentId).getPosition();
		Point boxPosition = intention.getBox().getPosition();
		Point goalPosition = intention.getGoal().getPosition();

		World currentWorld = new World(world);
		
		WorldSubIntentionsWrapper wrapper = decomposeTavelSubIntention(
				new TravelSubIntention(agentPosition, boxPosition, agentId, intention, agentId), 
				currentWorld, agentId);
		ArrayList<SubIntention> subIntentionsForTravel = wrapper.subIntentions;
		
				 
		wrapper = decomposeMoveBoxSubIntention(
				new MoveBoxSubIntention(intention.getBox(), goalPosition, intention, agentId), 
				wrapper.world, agentId);
		ArrayList<SubIntention> subIntentionsForMoveBox = wrapper.subIntentions;
		
		SubIntention agentToBoxSubIntention = subIntentionsForTravel.remove(subIntentionsForTravel.size()-1);		
		SubIntention boxToGoalSubIntention = subIntentionsForMoveBox.remove(subIntentionsForMoveBox.size()-1);		
		subIntentions.addAll(subIntentionsForTravel);
		subIntentions.addAll(subIntentionsForMoveBox);
		subIntentions.add(agentToBoxSubIntention);
		subIntentions.add(boxToGoalSubIntention);
		
		
		
		//Decompose very large paths (>50 points)
		//SubIntention for moving agent to box.
	 /*   int cnt = 0;
	    Point lastPos = agentPosition;
		for(Point p : pathFromAgentToBox) {
			cnt++;
			if((cnt % 50) == 0) {
				subIntentions.add(new TravelSubIntention(currentWorld.getAgent(agentId).getPosition(),p, agentId,intention, agentId));
				lastPos = p;
			}
		}		
		if(!lastPos.equals(currentWorld.getBoxById(intention.getBox().getId()).getPosition()) )		
			subIntentions.add(new TravelSubIntention(currentWorld.getAgent(agentId).getPosition(), currentWorld.getBoxById(intention.getBox().getId()).getPosition(), agentId,intention, agentId));
		*/
		/*
		//Decompose very large paths (>50 points)
		//SubIntention for moving box to goal.
		if(pathFromBoxToGoal != null) {
			cnt = 0;
		    lastPos = boxPosition;
			for(Point p : pathFromAgentToBox) {
				cnt++;
				if((cnt % 50) == 0) {
					subIntentions.add(new MoveBoxSubIntention(currentWorld.getBoxById(intention.getBox().getId()),p, intention, agentId));
				}
			}	
			if(!lastPos.equals(goalPosition))
			  subIntentions.add(new MoveBoxSubIntention(currentWorld.getBoxById(intention.getBox().getId()), goalPosition,intention, agentId));
		}
		else {
			subIntentions.add(new MoveBoxSubIntention(currentWorld.getBoxById(intention.getBox().getId()), goalPosition,intention, agentId));
		}*/
			
		

		Logger.logLine("----------- Intention Decomposer START----------");
		for (SubIntention subIntention : subIntentions) {
			Logger.logLine(subIntention);
		}
		Logger.logLine("-- Agent info --");
		Logger.logLine(world.getAgent(agentId));
		Logger.logLine("----------- Intention Decomposer END----------");
		return subIntentions;
	}

	private static World moveBoxesOnPathToSafePlaces(World world, Queue<Point> path, 
			                                         ArrayList<SubIntention> subIntentions, Intention intention, int agentId, boolean moveBoxToGoal, int intentionOwner) {
		boolean foundSpecialBoxToMove = false;
		World newWorld = world;
		
		for(Point point : path) {
			Box box = world.getBoxAt(point);
			if (box != null) {
				if(moveBoxToGoal && !foundSpecialBoxToMove) {
					//clear the path when you are not able to reach boxes, which you want to move to a save spot
					if(!world.isPositionReachable(world.getAgent(agentId).getPosition(), box.getPosition(), false, true,agentId)) {
						//SubIntention to clear path from Agent to Box.
						Queue<Point> pathFromAgentToBox = findPath(world, world.getAgent(agentId).getPosition(), box.getPosition());
						newWorld = moveBoxesOnPathToSafePlaces(newWorld, pathFromAgentToBox, subIntentions, intention, agentId, false, intentionOwner);
					}
				}
				
				Point safePosition = SafeSpotDetector.getSafeSpotForBox(newWorld, box, path);
				Logger.logLine("Safespot for " + box +": " +  safePosition);
				
				//Point savePosition = safeSpots.poll();
				Box newWorldBox = newWorld.getBoxById(box.getId());
				if(newWorldBox.getColor().equals(world.getAgent(agentId).getColor()))
					subIntentions.add(new TravelSubIntention(world.getAgent(agentId).getPosition(), newWorldBox.getPosition(), agentId, intention, intentionOwner));
				subIntentions.add(new MoveBoxSubIntention(newWorldBox, safePosition, intention, intentionOwner));
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

		search.addToFrontier(new PathNode(world, sourcePosition, targetPosition, true, true,-1));
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
