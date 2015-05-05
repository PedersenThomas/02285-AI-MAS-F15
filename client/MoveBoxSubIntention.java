package client;

import client.Search.PlannerNode;
import client.Search.SearchNode;

public class MoveBoxSubIntention extends SubIntention{
	private Point endPosition;
	private Box box;

	public MoveBoxSubIntention(Box box, Point endPosition, Intention rootIntention, int owner) {
		super(rootIntention, owner);
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
		return new MoveBoxSubIntention(new Box(this.box), this.endPosition, this.getRootIntention(), this.getOwner());
	}
	
	@Override
	public String toString() {
		return "MoveBoxSubIntention: " + box + " -> " + endPosition;
	}

	@Override
	public boolean isCompleted(SearchNode node) {
		PlannerNode plannerNode = (PlannerNode) node;		
		 if(plannerNode.getWorld().getBoxById(this.getBox().getId()).getPosition().equals(this.getEndPosition())) {
			 return true;
		 }
		return false;
	}	
}
