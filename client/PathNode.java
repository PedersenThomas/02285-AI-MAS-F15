package client;

import java.util.ArrayList;
import java.util.Comparator;

public class PathNode {
	private int stepCount;
	private Point position;
	private World world;
	
	PathNode(Point position, World world) {
		this.position = position;
		this.stepCount = 0;
		this.world = world;
	}
	
	public int getStepCount() {
		return stepCount;
	}

	
	PathNode(Point position, int parentStepCount, World world) {
		this.position = position;
		this.stepCount = parentStepCount + 1;
		this.world = world;
	}
	
	public ArrayList< PathNode > getExpandedNodes(Point targetPos, boolean ignoreBoxes) {
		ArrayList< PathNode > expandedNodes = new ArrayList< PathNode >();
		for ( Command.dir c : Command.dir.values() ) {
			Point newPos = position.move(c);
			if(!world.isWallAt(newPos)) {		
				if(ignoreBoxes || !world.isBoxAt(newPos) || newPos.equals(targetPos)) {
				  expandedNodes.add( new PathNode(newPos, stepCount, world) );
				}
			}
		}
		return expandedNodes;
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
	
	public World getWorld() {
		return world;
	}
	
	
}
