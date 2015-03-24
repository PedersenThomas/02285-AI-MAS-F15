package client;

public class Intention {
	private Goal goal;
	private Box box;
	
	//TODO This should be beliefs not world.
	public Intention(World world) {
		
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}
}
