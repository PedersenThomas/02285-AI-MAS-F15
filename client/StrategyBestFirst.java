package client;

import java.util.HashSet;
import java.util.PriorityQueue;

public class StrategyBestFirst {
	private HashSet< StrategyActionNode > explored;
	private Heuristic heuristic;
	private PriorityQueue< StrategyActionNode > frontier;
	
	public StrategyBestFirst( Heuristic h ) {
		heuristic = h;
		explored = new HashSet<StrategyActionNode>();
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
}