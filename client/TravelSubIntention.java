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
	
	public TravelSubIntention deepCopy() {
		return new TravelSubIntention(this.endPosition, this.getRootIntention());
	}
	
	@Override
	public String toString() {
		return "TravelSubIntention: " + " -> " + endPosition;
	}	
}
