package client;

import java.util.PriorityQueue;

public class StrategyBestFirst {
	private Heuristic heuristic;
	private PriorityQueue< World > frontier;
	
	public StrategyBestFirst( Heuristic h ) {
		super();
		heuristic = h;
		frontier = new PriorityQueue<World>(h);
	}
	public World getAndRemoveLeaf() {
		return frontier.poll();
	}

	public void addToFrontier( World n ) {
		frontier.add(n);
	}

	public int countFrontier() {
		return frontier.size();
	}

	public boolean frontierIsEmpty() {
		return frontier.isEmpty();
	}

	public boolean inFrontier( World n ) {
		return frontier.contains(n);
	}

	public String toString() {
		return "Best-first Search (PriorityQueue) using " + heuristic.toString();
	}
}