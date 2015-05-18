package client;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import client.Client.Agent;

public class SafeSpotDetector {
	public static PriorityQueue<SafePoint> detectSafeSpots(World world, int agentId) {
		PriorityQueue<SafePoint> safeSpots = new PriorityQueue<SafePoint>();
		for(Point point : world.getRechableCells(agentId)) {
			if(world.isGoalAt(point) || world.isBoxAt(point)) {
				continue;
			}
			SafePoint spoint = new SafePoint(point);
			//Logger.logLine("------ reachable point " + spoint + "------");
			if(Pattern.isSafePoint(spoint, world)) {
				safeSpots.add(spoint);
			}
		}
		return safeSpots;
	}
	
	public static Point getSafeSpotForAgent(World world, int agentId, Queue<Point> path) {
		PriorityQueue<SafePoint> safeSpots = detectSafeSpots(world, agentId);

		//Logger.logLine("-----------  Safe spots ----------");
		//for (SafePoint safespot : safeSpots) {
		//	Logger.logLine("" + safespot);
		//}
		
		Point safePosition = null;
		//Find a safepoint not on the path.
		int minDist = 0;
		Point agentPos = world.getAgent(agentId).getPosition();
		for (SafePoint safespot : safeSpots) {
			if(world.isFreeCell(safespot)) {
				if(path == null || !path.contains(safespot)) {
					if(safePosition == null) {
						minDist =  agentPos.distance(safespot);
						safePosition = safespot;
					}
					else if(minDist > agentPos.distance(safespot)) {
						minDist = agentPos.distance(safespot);
						safePosition = safespot;
						
					}
					
				}
			}
		}
		return safePosition;
	}
	
	public static Point getSafeSpotForBox(World world, Box box, Queue<Point> path) {
		
		Agent agent = world.getAgentToMoveBox(box);
		PriorityQueue<SafePoint> safeSpots = detectSafeSpots(world, agent.getId());

		//Logger.logLine("-----------  Safe spots ----------");
		//for (SafePoint safespot : safeSpots) {
		//	Logger.logLine("" + safespot);
		//}
		Queue<Point> safeSpotsNotOnPath = new LinkedList<Point>();
		
		//Find safepoints not on the path.
		for (SafePoint safespot : safeSpots) {
			if(world.isFreeCell(safespot) || (agent.getPosition().equals(safespot))) {
				if(path == null || !path.contains(safespot)) {					
					safeSpotsNotOnPath.add(safespot);
				}
			}
		}
		
		//Logger.logLine(safeSpotsNotOnPath.size() + " safespots not on path");
		
		if(safeSpotsNotOnPath.size() == 1) return safeSpotsNotOnPath.poll();
		else if(safeSpotsNotOnPath.size() == 0) return safeSpots.poll();
		
		//Find reachable safespots
		Queue<Point> safeSpotsReachable = new LinkedList<Point>();
		for (Point safespot : safeSpotsNotOnPath) {
			if(world.isPositionReachable(box.getPosition(), safespot, false, false, agent.getId())) {
				safeSpotsReachable.add(safespot);
			}
		}
		//Logger.logLine(safeSpotsReachable.size() + " safespots reachable");
		
		if(safeSpotsReachable.size() == 1) return safeSpotsReachable.poll();
		else if(safeSpotsReachable.size() == 0) return safeSpotsNotOnPath.poll();
		
		//Find closest safespot
		Point safePosition = null;
		int minDist = 0;
		for (Point safespot : safeSpotsReachable) {			
			if(safePosition == null) {
				minDist =  box.getPosition().distance(safespot);
				safePosition = safespot;
			}
			else if(minDist > box.getPosition().distance(safespot)) {
				minDist = box.getPosition().distance(safespot);
				safePosition = safespot;				
			}
		}

		
		return safePosition;
	}
}
