package client;

import client.Search.SearchNode;

public abstract class SubIntention {
	private Intention rootIntention;
	private int owner;
	protected Point startPosition;
	
	public SubIntention(Intention rootIntention, int owner, Point startPosition) {
		this.rootIntention = rootIntention;
		this.owner = owner;
		this.startPosition = startPosition;
	}
	
	public SubIntention(SubIntention old) {
		this.rootIntention = new Intention(old.rootIntention);
		this.owner = old.owner;
		this.startPosition = old.startPosition;
	}

	public Intention getRootIntention() {
		return rootIntention;
	}
	
	public int getOwner() {
		return owner;
	}
	
	public Point getStartPosition() {
		return startPosition;
	}
	
	public abstract SubIntention deepCopy();
	
	public abstract boolean isCompleted(SearchNode node);
		
	@Override
	public String toString() {
		return "SubIntention: " + rootIntention;
	}
}
