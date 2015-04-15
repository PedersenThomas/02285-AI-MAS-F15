package client;

import java.util.ArrayList;
import java.util.List;

public class SafeSpotDetector {
	public static List<Point> detectSafeSpots(World world) {
		List<Point> safeSpots = new ArrayList<Point>();
		for(Point point : world.getRechableCells()) {
			int nearObjectsCounter = 0;
			for(Command.dir dir: Command.dir.values()) {
				Point p = point.move(dir);
				if (!world.isFreeCell(p)) {
					nearObjectsCounter++;
				}
			}
			if (nearObjectsCounter == 3) {
				safeSpots.add(point);
			}
		}
		return safeSpots;
	}
}
