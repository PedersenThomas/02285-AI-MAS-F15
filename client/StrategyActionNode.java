package client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class StrategyActionNode {
	private World world;
	private StrategyActionNode previousNode;
	private Command command;
	
	public StrategyActionNode(World world) {
		this.world = world;
	}
	
	public StrategyActionNode(World world, StrategyActionNode previousNode, Command command) {
		this.world = world;
		this.previousNode = previousNode;
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
}
