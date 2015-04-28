package client;

public class SubIntention {
	private Intention rootIntention;
	
	public SubIntention(Intention rootIntention) {
		this.rootIntention = rootIntention;
	}
	
	public SubIntention(SubIntention old) {
		this.rootIntention = new Intention(old.rootIntention);
	}
		
	@Override
	public String toString() {
		return "SubIntention: " + rootIntention;
	}

	public Intention getRootIntention() {
		return rootIntention;
	}
}
