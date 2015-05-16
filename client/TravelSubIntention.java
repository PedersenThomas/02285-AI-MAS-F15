package client;

import client.Search.PathNode;
import client.Search.SearchNode;

public class TravelSubIntention extends SubIntention {
	private Point endPosition;
	private int agentId;
	
	public TravelSubIntention(Point startPosition, Point endPosition, int agentId, Intention rootIntention, int owner) {
		super(rootIntention,owner, startPosition);
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
		return new TravelSubIntention(this.startPosition,  this.endPosition, this.agentId, this.getRootIntention(), this.getOwner());
	}
	
	@Override
	public String toString() {
		return "TravelSubIntention: Agent [" + agentId + "] " + startPosition + " -> " + endPosition;
	}

	@Override
	public boolean isCompleted(SearchNode node) {
		PathNode pathNode = (PathNode) node;
		if (pathNode.getPosition().equals(this.getEndPosition())) {
			return true;
		}
		
		return false;
	}	
}
