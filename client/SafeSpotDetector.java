package client;

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
	
	public static Point getSafeSpotForBox(World world, Box box) {
		Agent agent = world.getAgentToMoveBox(box);
		PriorityQueue<SafePoint> safeSpots = detectSafeSpots(world, agent.getId());

		//Logger.logLine("-----------  Safe spots ----------");
		//for (SafePoint safespot : safeSpots) {
		//	Logger.logLine("" + safespot);
		//}
		
		return safeSpots.poll();
	}
}
