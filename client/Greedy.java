package client;

public class Greedy extends Heuristic {
	private Intention i;
	private int agentId;
	
	public Greedy(Intention i, int agentId) {
		this.i = i;
		this.agentId = agentId;
	}
	
	public int f( World n) {
		return heuristic( n );
	}
	
	private int heuristic(World world) {
		
		Box box = i.getBox();
		Goal goal = i.getGoal();
		return 0;
	}
	
	@Override
	public int compare(World world1, World world2) {
		return this.f( world1 ) - this.f( world2 );
	}

	@Override
	public String toString() {
		return "Greedy Heuristic";
	}
}
