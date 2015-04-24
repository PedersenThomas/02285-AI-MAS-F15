package client;

import java.util.List;
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
	
	private static boolean isSafePoint(SafePoint spoint, World world) {
		for(Command.dir dir: Command.dir.values()) {
			Point p = spoint.move(dir);
			if (world.isWallAt(p)) {
				spoint.increaseNumberOfWalls();
			}
			if (world.isBoxAt(p)) {
				spoint.increaseNumberOfBoxes();
			}
		}
		if (spoint.getNumberOfSurrandedObjects() == 3 || spoint.getNumberOfSurrandedObjects() == 1) {
			return true;
		}
		return false;
	}
	
	private static List<SafePoint> detectPatterns(World world) {
		for(Point point : world.getRechableCells()) {
			
		}
		return null;
	}
}
