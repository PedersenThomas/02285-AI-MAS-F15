package client;

public abstract class SubIntention {
	private Intention rootIntention;
	
	public SubIntention(Intention rootIntention) {
		this.rootIntention = rootIntention;
	}
	
	public SubIntention(SubIntention old) {
		this.rootIntention = new Intention(old.rootIntention);
	}

	public Intention getRootIntention() {
		return rootIntention;
	}
	
	public abstract SubIntention deepCopy();
		
	@Override
	public String toString() {
		return "SubIntention: " + rootIntention;
	}
}
