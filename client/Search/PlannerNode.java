package client.Search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import client.Command;
import client.World;

public class PlannerNode extends SearchNode {
	private int agentId;
	
	public PlannerNode(World world, int agentId) {
		super(world);
		this.agentId = agentId;
	}
	
	PlannerNode(World world, PlannerNode parentNode, Command command, int agentId) {
		super(world, parentNode, command);
		this.agentId = agentId;
	}
	
	public ArrayList< SearchNode > getExpandedNodes() {
		ArrayList< SearchNode > expandedNodes = new ArrayList< SearchNode >( Command.every.length );
		for ( Command c : Command.every ) {
			World newWorld = new World(world);
			boolean validCommand = newWorld.update(newWorld.getAgent(agentId), c);
			if(validCommand) {
				PlannerNode node = new PlannerNode(newWorld, this, c, agentId);								
				expandedNodes.add( node );
			}
		}
		return expandedNodes;
	}
	
	public Queue<Command> extractListOfCommands() {
		LinkedList<Command> queue = new LinkedList<Command>();

		SearchNode node = this;
		while(node != null && node.command != null) {
			queue.add(0, node.command);
			node = node.previousNode;
		}
		return queue;
	}
	
	@Override
	public int hashCode() {
		return this.world.hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		PlannerNode other = (PlannerNode) obj;
		return this.world.equals(other.world);
	}
}
