package client;

import java.util.ArrayList;
import java.util.List;
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
		
		int numUncompletedGoals = world.getNumberOfUncompletedGoals();

		int cnt = 0;
		while ( true ) {
			cnt++;
			if ( strategy.frontierIsEmpty() ) {
				break;
			}
			if(cnt % 1000 == 0)
			  System.err.println( cnt + "..." );
			
			StrategyActionNode leafNode = strategy.getAndRemoveLeaf();
			
			if ( leafNode.getWorld().isGoalCompleted(i.getGoal()) && 
					 (leafNode.getWorld().getNumberOfUncompletedGoals() < numUncompletedGoals)) {
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
