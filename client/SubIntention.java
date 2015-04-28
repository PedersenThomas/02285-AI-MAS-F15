package client;

public class SubIntention {
	private Point endPosition;
	private Box box;
	private Intention rootIntention;
	
	public SubIntention(Box box, Point endPosition, Intention rootIntention) {
		this.box = box;
		this.endPosition = endPosition;
		this.rootIntention = rootIntention;
	}
	
	public SubIntention(SubIntention old) {
		this.box = new Box(old.box);
		this.endPosition = new Point(old.endPosition);
		this.rootIntention = new Intention(old.rootIntention);
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

	public Intention getRootIntention() {
		return rootIntention;
	}
}
