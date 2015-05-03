package client;

public class TravelSubIntention extends SubIntention {
	private Point endPosition;
	private int agentId;
	
	public TravelSubIntention(Point endPosition, int agentId, Intention rootIntention, int owner) {
		super(rootIntention,owner);
		this.endPosition = endPosition;
		this.agentId = agentId;
	}

	public Point getEndPosition() {
		return endPosition;
	}
	public int getAgentId() {
		return agentId;
	}
	
	public TravelSubIntention deepCopy() {
		return new TravelSubIntention(this.endPosition, this.agentId, this.getRootIntention(), this.getOwner());
	}
	
	@Override
	public String toString() {
		return "TravelSubIntention: Agent [" + agentId + "] -> " + endPosition;
	}	
}
