package client;

import java.util.Queue;

import client.Client.Agent;

public class Plan {
	StrategyBestFirst strategy;
	Queue<Command> commandQueue;
	
	public Plan(World world, Intention i, Agent agent) {
		System.err.println("Planing for Intention: " + i);
		Heuristic h = new Greedy(i, agent.getId());
		strategy = new StrategyBestFirst(h);
		
		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( new StrategyActionNode( world, agent.getId() ) );

		while ( true ) {
			if ( strategy.frontierIsEmpty() ) {
				break;
			}

			StrategyActionNode leafNode = strategy.getAndRemoveLeaf();

			if ( leafNode.getWorld().isGoalCompleted(i.getGoal()) ) {
				commandQueue = leafNode.extractList();
				break;
			}

			strategy.addToExplored( leafNode );
			for ( StrategyActionNode n : leafNode.getExpandedNodes() ) {
				if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
					strategy.addToFrontier( n );
				}
			}
		}
	}
	
	public Command execute() {
		return commandQueue.poll();		
	}
}
