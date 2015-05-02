package client;

import java.util.PriorityQueue;

public class SafeSpotDetector {
	public static PriorityQueue<SafePoint> detectSafeSpots(World world) {
		PriorityQueue<SafePoint> safeSpots = new PriorityQueue<SafePoint>();
		for(Point point : world.getRechableCells()) {
			if(world.isGoalAt(point) || world.isBoxAt(point)) {
				continue;
			}
			SafePoint spoint = new SafePoint(point);
			System.err.println("------ reachable point " + spoint + "------");
			if(Pattern.isSafePoint(spoint, world)) {
				safeSpots.add(spoint);
			}
		}
		return safeSpots;
	}
}
