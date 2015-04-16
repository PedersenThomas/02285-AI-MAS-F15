package client;

public class SubIntention {
	private Point endPosition;
	private Box box;
	
	public SubIntention(Box box, Point endPosition) {
		this.box = box;
		this.endPosition = endPosition;
	}
	
	public Box getBox() {
		return box;
	}
	
	public Point getEndPosition() {
		return endPosition;
	}
	
	@Override
	public String toString() {
		return "SubIntention: " + box + " -> " + endPosition;
	}
}
