package client;

import java.util.Queue;
import java.util.Random;

import client.Client.Agent;

public class Plan {
	private static Random rand = new Random();
	StrategyBestFirst strategy;
	Queue<Command> commandQueue;
	
	public Plan(World world, Intention i, Agent agent) {
		System.err.println("Planing for Ientention: " + i);
		Heuristic h = new Greedy(i, agent.getId());
		strategy = new StrategyBestFirst(h);
		
		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( new StrategyActionNode( world, agent.getId() ) );

		int iterations = 0;
		while ( true ) {
			
//			if ( iterations % 200 == 0 ) {
//				System.err.println( strategy.searchStatus() );
//			}
//			if ( Memory.shouldEnd() ) {
//				System.err.format( "Memory limit almost reached, terminating search %s\n", Memory.stringRep() );
//				return null;
//			}
//			if ( strategy.timeSpent() > 300 ) { // Minutes timeout
//				System.err.format( "Time limit reached, terminating search %s\n", Memory.stringRep() );
//				return null;
//			}

			if ( strategy.frontierIsEmpty() ) {
//				return null;
				System.err.println("No more nodes to concider. iterations: " + iterations);
				break;
			}

			StrategyActionNode leafNode = strategy.getAndRemoveLeaf();
			System.err.println("Plan. LeafNode" + leafNode);

			if ( leafNode.getWorld().isGoalCompleted(i.getGoal()) ) {
				commandQueue = leafNode.extractList();
				
				System.err.println("Found solution/plan iterations: " + iterations + " LeafNode: " + leafNode);
				break;
			}

			strategy.addToExplored( leafNode );
			for ( StrategyActionNode n : leafNode.getExpandedNodes() ) {
				System.err.println(n);
//				System.err.println("Explored:" + strategy.isExplored( n ) + " InFrontier: " + strategy.inFrontier( n ));
				if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
					strategy.addToFrontier( n );
				}
			}
			iterations++;
		}
		System.err.println("Stopped planing for Intention: " + i);
	}
	
	public Command execute() {
		System.err.println("Plan: CommandQueue length: " + commandQueue.size() );
		return commandQueue.poll();		
	}
}
