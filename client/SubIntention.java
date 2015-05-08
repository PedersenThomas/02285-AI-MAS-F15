package client;

import client.Search.SearchNode;

public abstract class SubIntention {
	private Intention rootIntention;
	private int owner;
	
	public SubIntention(Intention rootIntention, int owner) {
		this.rootIntention = rootIntention;
		this.owner = owner;
	}
	
	public SubIntention(SubIntention old) {
		this.rootIntention = new Intention(old.rootIntention);
		this.owner = old.owner;
	}

	public Intention getRootIntention() {
		return rootIntention;
	}
	
	public int getOwner() {
		return owner;
	}
	
	public abstract SubIntention deepCopy();
	
	public abstract boolean isCompleted(SearchNode node);
		
	@Override
	public String toString() {
		return "SubIntention: " + rootIntention;
	}
}
