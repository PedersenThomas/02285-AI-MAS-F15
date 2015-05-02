package client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Goal {
	private Point position;
	private char letter;
	private int priorityScore;
	private Map<Integer,Integer> totalOrder;
	
	public Goal(int x, int y, char letter) {
		super();
		position = new Point(x,y);
		priorityScore = -1;
		totalOrder = new HashMap<>();
		this.letter = letter;
	}
	
	public Goal(Goal old) {
		position = old.position;
		letter = old.letter;
		priorityScore = old.priorityScore;
		this.totalOrder = new HashMap<>();
		for (Entry<Integer, Integer> entry : old.totalOrder.entrySet()) {
			this.totalOrder.put(entry.getKey(), entry.getValue());
		}
	}
	
	public Point getPosition() {
		return position;
	}
	
	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	public void setPriorityScore(int priorityScore) {
		this.priorityScore = priorityScore;
	}
	
	public int getPriorityScore() {
		return priorityScore;
	}
	
	public Integer getTotalOrder(int agentId) {
		return totalOrder.get(agentId);
	}
	
	public void setTotalOrder(int agentId, int totalOrder) {
		this.totalOrder.put(agentId, totalOrder);
	}
	
	@Override
	public String toString() {
		return "GOAL: Letter: " + letter + " Position: " + position /*+ " Order: " + totalOrder*/;
	}
}
