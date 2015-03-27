package client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import client.Command.type;
import client.TestClientValentin.Agent;

public class StrategyActionNode {
	private World world;
	private StrategyActionNode previousNode;
	private Command command;
	private Agent agent;
	
	public StrategyActionNode(World world, Agent agent) {
		this.world = world;
		this.agent = agent;
	}
	
	public StrategyActionNode(World world, Agent agent, StrategyActionNode parentNode, Command command) {
		this.world = new World(world);
		this.agent = agent;
		this.previousNode = parentNode;
		this.command = command;
	}
	
	public Queue<Command> extractList() {
		LinkedList<Command> queue = new LinkedList<Command>();
		
		StrategyActionNode node = previousNode;
		while(node != null) {
			queue.add(0, node.command);
			node = node.previousNode;
		}
		
		return queue;
	}
	
	public World getWorld() {
		return world;
	}
	
	public ArrayList< StrategyActionNode > getExpandedNodes() {
		ArrayList< StrategyActionNode > expandedNodes = new ArrayList< StrategyActionNode >( Command.every.length );
		for ( Command c : Command.every ) {
			// Determine applicability of action
			Point newAgentPosition = agent.getPosition().move(c.dir1);

			if ( c.actType == type.Move ) {
				// Check if there's a wall or box on the cell to which the agent is moving
				if ( world.isFreeCell(newAgentPosition) ) {
					StrategyActionNode node = new StrategyActionNode(this.world, this.agent, this, c);
					node.getWorld().getAgent(agent.getId()).setPosition(newAgentPosition);
					expandedNodes.add( node );
				}
			} else if ( c.actType == type.Push ) {
				// Make sure that there's actually a box to move
				if ( world.isBoxAt(newAgentPosition) ) {
					Point newBoxPosition = newAgentPosition.move(c.dir2);
					// .. and that new cell of box is free
					if ( world.isFreeCell(newBoxPosition) ) {
						StrategyActionNode node = new StrategyActionNode(this.world, this.agent, this, c);
						node.getWorld().getAgent(agent.getId()).setPosition(newAgentPosition);
						node.getWorld().getBoxAt(newAgentPosition).setPosition(newBoxPosition);
						expandedNodes.add( node );
					}
				}
			} else if ( c.actType == type.Pull ) {
				// Cell is free where agent is going
				if ( world.isFreeCell(newAgentPosition) ) {
					// .. and there's a box in "dir2" of the agent
					Point currentBoxPosition = agent.getPosition().move(c.dir2);
					Point newBoxPosition = agent.getPosition();
					if ( world.isBoxAt( currentBoxPosition ) ) {
						StrategyActionNode node = new StrategyActionNode(this.world, this.agent, this, c);
						node.getWorld().getAgent(agent.getId()).setPosition(newAgentPosition);
						node.getWorld().getBoxAt(newAgentPosition).setPosition(newBoxPosition);
						expandedNodes.add( node );
					}
				}
			}
		}
		return expandedNodes;
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
		StrategyActionNode other = (StrategyActionNode) obj;
		if(!this.world.equals(other.world)) {
			return false;
		}
		return true;
	}
}
