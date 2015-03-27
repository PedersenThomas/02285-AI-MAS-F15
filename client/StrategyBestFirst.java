<<<<<<< HEAD
package client;

import java.util.HashSet;
import java.util.PriorityQueue;

public class StrategyBestFirst {
	private HashSet< StrategyActionNode > explored;
	private Heuristic heuristic;
	private PriorityQueue< StrategyActionNode > frontier;
	
	public StrategyBestFirst( Heuristic h ) {
		super();
		heuristic = h;
		frontier = new PriorityQueue<StrategyActionNode>(h);
	}
	
	public void addToExplored( StrategyActionNode n ) {
		explored.add( n );
	}
	
	public boolean isExplored( StrategyActionNode n ) {
		return explored.contains( n );
	}
	
	public StrategyActionNode getAndRemoveLeaf() {
		return frontier.poll();
	}

	public void addToFrontier( StrategyActionNode n ) {
		frontier.add(n);
	}

	public int countFrontier() {
		return frontier.size();
	}

	public boolean frontierIsEmpty() {
		return frontier.isEmpty();
	}

	public boolean inFrontier( StrategyActionNode n ) {
		return frontier.contains(n);
	}

	public String toString() {
		return "Best-first Search (PriorityQueue) using " + heuristic.toString();
	}
=======
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
>>>>>>> 41ce5c94b2a39b460426eae8addb1c122e2b57c6
}