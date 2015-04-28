package client;

public class TravelSubIntention extends SubIntention {
	private Point endPosition;
	
	public TravelSubIntention(Point endPosition, Intention rootIntention) {
		super(rootIntention);
		this.endPosition = endPosition;
	}

	public Point getEndPosition() {
		return endPosition;
	}
	
	@Override
	public String toString() {
		return "SubIntention: " + " -> " + endPosition;
	}	
}
