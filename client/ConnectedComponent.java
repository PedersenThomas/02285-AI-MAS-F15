package client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import client.Command.dir;

public class ConnectedComponent {
	private HashSet<Point> explored = new HashSet<Point>();
	private World world;
	private int numberOfConnectedComponent;
	public boolean isBoxBlocking = true;
	public boolean isAgentBlocking = true;
	
	public int getNumberOfConnectedComponent() {
		return numberOfConnectedComponent;
	}
	
	public ConnectedComponent(World world) {
		this.world = world;
	}
	
	public void calculateNumberOfConnectedComponents() {
		for(int x = 0; x < world.getWidth(); x++) {
			for(int y = 0; y < world.getHeight(); y++) {
				Point startPoint = new Point(x, y);
				if(!freePoint(startPoint)) {
					continue;
				}
				if(explored.contains(startPoint)) {
					continue;
				}
				
				System.err.println(startPoint);
				numberOfConnectedComponent += 1;
				
				BFS(startPoint);
			}
		}		
	}
	
	private boolean freePoint(Point point) {
		if(world.isWallAt(point)) {
			return false;
		}
		if(world.isBoxAt(point) && isBoxBlocking) {
			return false;
		}
		if(world.isAgentAt(point) && isAgentBlocking) {
			return false;
		}
		return true;
	}

	private void BFS(Point startPoint) {
		Queue<Point> frontier = new LinkedList<Point>();
		frontier.add(startPoint);
		
		while(!frontier.isEmpty()) {
			Point point = frontier.poll();
			for (Point p : findNeighbours(point)) {
				if(!freePoint(p) ||
				   explored.contains(p) ||
				   frontier.contains(p)) {
					continue;
				} else {
					frontier.add(p);
				}
			}
			explored.add(point);
		}
	}
	
	private ArrayList<Point> findNeighbours(Point point) {
		ArrayList<Point> result = new ArrayList<Point>();
		if(point.getX() > 0) {
			result.add(point.move(dir.W));
		}
		if(point.getY() > 0) {
			result.add(point.move(dir.N));			
		}
		if(point.getX() < world.getWidth() -1) {
			result.add(point.move(dir.E));
		}
		if(point.getY() < world.getHeight() -1) {
			result.add(point.move(dir.S));			
		}
				
		return result;
	}
}
