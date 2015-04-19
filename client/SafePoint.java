package client;

public class SafePoint extends Point implements Comparable<SafePoint> {
	
	private int numberOfWalls;
	private int numberOfBoxes;
	
	public SafePoint(Point p) {
		super(p);
	}
	
	public SafePoint(int x, int y) {
		super(x,y);
	}
	
	public void increaseNumberOfWalls() {
		numberOfWalls++;
	}
	
	public void increaseNumberOfBoxes() {
		numberOfBoxes++;
	}
	
	public int getNumberOfSurrandedObjects() {
		return numberOfWalls + numberOfBoxes;
	}
	
	public int compareTo (SafePoint spoint) {
		if (spoint.numberOfWalls != this.numberOfWalls) {
			return spoint.numberOfWalls - this.numberOfWalls;
		} else {
			return spoint.numberOfBoxes - this.numberOfBoxes;
		}
	} 
}
