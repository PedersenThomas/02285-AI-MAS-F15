package client;

import java.util.Queue;

import client.Client.Agent;

public class Plan {
	StrategyBestFirst strategy;
	Queue<Command> commandQueue;
	
	public Plan(World world, Intention i, Agent agent) {
		if(i == null) {
			throw new RuntimeException("Intention is null");
		}
		System.err.println("Planing for Intention: " + i);
		Heuristic h = new Greedy(i, agent.getId());
		strategy = new StrategyBestFirst(h);
		
		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( new StrategyActionNode( world, agent.getId() ) );
		
		int numUncompletedGoals = world.getNumberOfUncompletedGoals();

		int iterations = 0;
		while ( true ) {
			iterations++;
			if ( strategy.frontierIsEmpty() ) {
				break;
			}
			if(iterations % 10000 == 0)
			  System.err.println( iterations + "..." );
			
			StrategyActionNode leafNode = strategy.getAndRemoveLeaf();
			
			if ( leafNode.getWorld().isGoalCompleted(i.getGoal()) && 
				 (leafNode.getWorld().getNumberOfUncompletedGoals() < numUncompletedGoals)) {
				    commandQueue = leafNode.extractList();
					break;
				}
			
			strategy.addToExplored( leafNode );
			for ( StrategyActionNode n : leafNode.getExpandedNodes() ) {
				if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
					
					// Check if a completed goal has been destroyed
					boolean highPriorityGoalDestroyed = false;
					for(Goal g:n.getWorld().getGoals()) {
						if((!n.getWorld().isGoalCompleted(g)) &&
						    (leafNode.getWorld().isGoalCompleted(g))) {							
							// Check if the destroyed goal has a higher priority score than the intended goal
							if(n.getWorld().getGoalPriorityScore(g) >= world.getGoalPriorityScore(i.getGoal())) {
								//yes -> that's not ok
								highPriorityGoalDestroyed = true;								
							}
							break;
						}						
					}
					// Everything ok -> add node to frontier
					if(!highPriorityGoalDestroyed)
						strategy.addToFrontier( n );
					
				}
			}			
		}
	}
	
	public Command execute() {
		return commandQueue.poll();		
	}
}
