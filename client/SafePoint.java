package client;

public class SafePoint extends Point implements Comparable<SafePoint> {

	private final int wallPriority = 1;
	private final int boxPriority = 2;
	//private final int agentPriority = 3;

	private int priority;
	private int numberofWalls;
	private int numberofBoxes;
	//private int numberofAgents;

	public SafePoint(Point p) {
		super(p);
	}

	public SafePoint(int x, int y) {
		super(x, y);
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return this.priority;
	}

	public void increaseObjectCounter(ObjectType objectType) {
		switch (objectType) {
		case wall:
			numberofWalls++;
			break;
		case agent:
			//numberofAgents++;
			break;
		case box:
			numberofBoxes++;
			break;
		case free:
			break;
		}
	}

	private int calculatePriority() {
		return wallPriority * this.numberofWalls + 
				boxPriority * this.numberofBoxes;
				//+ agentPriority * this.numberofAgents;
	}

	@Override
	public int compareTo(SafePoint spoint) {
		if (this.priority != spoint.priority) {
			return this.priority - spoint.priority;
		} else {
			return this.calculatePriority() - spoint.calculatePriority();
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " priority: " + priority + " calculated " + calculatePriority();
	}
}
