package client;

public class MoveBoxSubIntention extends SubIntention{
	private Point endPosition;
	private Box box;

	public MoveBoxSubIntention(Box box, Point endPosition, Intention rootIntention) {
		super(rootIntention);
		this.box = box;
		this.endPosition = endPosition;
	}
	
	public MoveBoxSubIntention(MoveBoxSubIntention old) {
		super(old);
		this.box = new Box(old.box);
		this.endPosition = new Point(old.endPosition);
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
