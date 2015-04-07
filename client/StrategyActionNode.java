package client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import client.Command.type;
import client.Client.Agent;

public class StrategyActionNode {
	private World world;
	private StrategyActionNode previousNode;
	private Command command;
	private int agentId;
	private int stepCount = 0;
	
	public StrategyActionNode(World world, int agentId) {
		this.world = world;
		this.agentId = agentId;
	}
	
	public StrategyActionNode(World world, int agentId, StrategyActionNode parentNode, Command command) {
		this.world = new World(world);
		this.agentId = agentId;
		this.previousNode = parentNode;
		this.command = command;
		this.stepCount = parentNode.getStepCount() +1;
	}
	
	public Queue<Command> extractList() {
		LinkedList<Command> queue = new LinkedList<Command>();

		StrategyActionNode node = this;
		while(node != null && node.command != null) {
			queue.add(0, node.command);
			node = node.previousNode;
		}
		return queue;
	}
	
	public World getWorld() {
		return world;
	}
	
	public int getStepCount() {
		return stepCount;
	}
	
	public ArrayList< StrategyActionNode > getExpandedNodes() {
		ArrayList< StrategyActionNode > expandedNodes = new ArrayList< StrategyActionNode >( Command.every.length );
		//Agent agent = world.getAgent(agentId);
		for ( Command c : Command.every ) {
			World newWorld = new World(world);
			newWorld.update(newWorld.getAgent(agentId), c);
			if(!world.equals(newWorld)) {
				StrategyActionNode node = new StrategyActionNode(newWorld, agentId, this, c);
				expandedNodes.add( node );
			}
			// Determine applicability of action
			/*Point newAgentPosition = agent.getPosition().move(c.dir1);

			if ( c.actType == type.Move ) {
				// Check if there's a wall or box on the cell to which the agent is moving
				if ( world.isFreeCell(newAgentPosition) ) {
					StrategyActionNode node = new StrategyActionNode(this.world, agentId, this, c);
					node.getWorld().getAgent(agent.getId()).setPosition(newAgentPosition);
					expandedNodes.add( node );
				}
			} else if ( c.actType == type.Push ) {
				// Make sure that there's actually a box to move
				if ( world.isBoxAt(newAgentPosition) ) {
					Point newBoxPosition = newAgentPosition.move(c.dir2);
					// .. and that new cell of box is free
					if ( world.isFreeCell(newBoxPosition) ) {
						StrategyActionNode node = new StrategyActionNode(this.world, agentId, this, c);
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
						StrategyActionNode node = new StrategyActionNode(this.world, agentId, this, c);
						node.getWorld().getAgent(agent.getId()).setPosition(newAgentPosition);
						node.getWorld().getBoxAt(currentBoxPosition).setPosition(newBoxPosition);
						expandedNodes.add( node );
					}
				}
			}*/
		}
		return expandedNodes;
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
		return this.world.equals(other.world);
	}
}
