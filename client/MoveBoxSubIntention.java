package client;

public class MoveBoxSubIntention extends SubIntention{
	private Point endPosition;
	private Box box;

	public MoveBoxSubIntention(Box box, Point endPosition, Intention rootIntention) {
		super(rootIntention);
		this.box = box;
		this.endPosition = endPosition;
	}
	
	public Box getBox() {
		return box;
	}
	
	public Point getEndPosition() {
		return endPosition;
	}
	
	public MoveBoxSubIntention deepCopy() {
		return new MoveBoxSubIntention(new Box(this.box), this.endPosition, this.getRootIntention());
	}
	
	@Override
	public String toString() {
		return "MoveBoxSubIntention: " + box + " -> " + endPosition;
	}	
}
