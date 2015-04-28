package client.Search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import client.Command;
import client.Point;
import client.World;

public class PathNode extends SearchNode {
	private Point position;
	private Point targetPosition;
	private boolean ignoreBoxes;
	
	public PathNode(World world, Point position, Point targetPosition, boolean ignoreBoxes) {
		super(world);
		this.position = position;
		this.targetPosition = targetPosition;
		this.ignoreBoxes = ignoreBoxes;
	}
	
	PathNode(World world, PathNode parentNode, Command command, Point position) {
		super(world, parentNode, command);

		this.position = position;
		this.targetPosition = parentNode.targetPosition;
		this.ignoreBoxes = parentNode.ignoreBoxes;
	}
		
	public ArrayList< SearchNode > getExpandedNodes() {
		ArrayList< SearchNode > expandedNodes = new ArrayList< SearchNode >();
		for ( Command.dir dir : Command.dir.values() ) {
			Point newPos = position.move(dir);
			if(!world.isWallAt(newPos)) {		
				if(ignoreBoxes || !world.isBoxAt(newPos) || newPos.equals(targetPosition)) {
				  expandedNodes.add( new PathNode(world, this, new Command(dir), newPos) );
				}
			}
		}
		return expandedNodes;
	}
	
	public Queue<Point> extractListOfPossitions() {
		LinkedList<Point> queue = new LinkedList<Point>();

		PathNode node = this;
		while(node != null && node.getPosition() != null) {
			queue.add(0, node.getPosition());
			node = (PathNode)node.previousNode;
		}
		return queue;
	}
	
	public Queue<Command> extractListOfCommands() {
		LinkedList<Command> queue = new LinkedList<Command>();

		PathNode node = this;
		while(node != null && node.command != null) {
			queue.add(0, node.command);
			node = (PathNode)node.previousNode;
		}
		return queue;
	}
	
	public Point getPosition() {
		return position;
	}
	
	@Override
	public int hashCode() {
		return this.position.hashCode();
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		PathNode other = (PathNode) obj;
		return this.position.equals(other.position);
	}	
}
