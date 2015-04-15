package client.Search;

import java.util.HashSet;
import java.util.PriorityQueue;

import client.Heuristic.Heuristic;

public class BestFirstSearch {
	private HashSet<SearchNode> explored;
	private Heuristic heuristic;
	private PriorityQueue<SearchNode> frontier;

	public BestFirstSearch(Heuristic h) {
		heuristic = h;
		explored = new HashSet<SearchNode>();
		frontier = new PriorityQueue<SearchNode>(h);
	}

	public void addToExplored(SearchNode n) {
		explored.add(n);
	}

	public boolean isExplored(SearchNode n) {
		return explored.contains(n);
	}

	public SearchNode getAndRemoveLeaf() {
		return frontier.poll();
	}

	public void addToFrontier(SearchNode n) {
		frontier.add(n);
	}

	public int countFrontier() {
		return frontier.size();
	}

	public boolean frontierIsEmpty() {
		return frontier.isEmpty();
	}

	public boolean inFrontier(SearchNode n) {
		return frontier.contains(n);
	}

	public String toString() {
		return "Best-first Search (PriorityQueue) using "
				+ heuristic.toString();
	}
}