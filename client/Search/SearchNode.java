package client.Search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import client.Box;
import client.Command;
import client.World;
import client.Client.Agent;

public abstract class SearchNode {
	protected Command command;
	protected SearchNode previousNode;
	protected int stepCount;
	protected World world;
	
	public SearchNode(World world) {
		this.world = world;
		
		this.stepCount = 0;
	}
	
	public SearchNode(World world, SearchNode parentNode, Command command) {
		this.world = new World(world);
		this.previousNode = parentNode;
		this.command = command;
		this.stepCount = parentNode.getStepCount() +1;
	}
	
	public World getWorld() {
		return world;
	}
	
	public int getStepCount() {
		return stepCount;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Box box : world.getBoxes()) {
			buffer.append(box.toString() + " ");
		}
		
		for (Agent agent : world.getAgents()) {
			buffer.append(agent.toString() + " ");
		}
		
		buffer.append(" Command: " + command);
		
		return buffer.toString();
	}
	
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals( Object obj );
	
	public abstract ArrayList< SearchNode > getExpandedNodes();
}
