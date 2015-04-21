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
			for(Command.dir dir: Command.dir.values()) {
				Point p = point.move(dir);
				if (world.isWallAt(p)) {
					spoint.increaseNumberOfWalls();
				}
				if (world.isBoxAt(p)) {
					spoint.increaseNumberOfBoxes();
				}
			}
			if (spoint.getNumberOfSurrandedObjects() == 3 || spoint.getNumberOfSurrandedObjects() == 1) {
				safeSpots.add(spoint);
			}
		}
		return safeSpots;
	}
}
