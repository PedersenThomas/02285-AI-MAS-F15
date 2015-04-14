package client;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AgentPathSearch {
	private HashSet< PathNode > explored;
	private PriorityQueue< PathNode > frontier;
	
	public AgentPathSearch(Point goalPos ) {
		explored = new HashSet<PathNode>();
		frontier = new PriorityQueue<PathNode>(new PathNodeComperator(goalPos));		
	}
	
	public void addToExplored( PathNode n ) {
		explored.add( n );
	}
	
	public boolean isExplored( PathNode n ) {
		return explored.contains( n );
	}
	
	public PathNode getAndRemoveLeaf() {
		return frontier.poll();
	}

	public void addToFrontier( PathNode n ) {
		frontier.add(n);
	}

	public int countFrontier() {
		return frontier.size();
	}

	public boolean frontierIsEmpty() {
		return frontier.isEmpty();
	}

	public boolean inFrontier( PathNode n ) {
		return frontier.contains(n);
	}
	
	static class PathNodeComperator implements Comparator<PathNode> {

		private Point goalPos;
		
		public PathNodeComperator(Point goalPos) {
			this.goalPos = goalPos;
		}

		@Override
		public int compare(PathNode o1, PathNode o2) {
			int c1 = o1.getStepCount() + goalPos.distance(o1.getPosition());
			int c2 = o2.getStepCount() + goalPos.distance(o2.getPosition());
			return c1-c2;
		}
		
	}
}
