package client;

import java.util.Queue;
import java.util.Random;

public class Plan {
	private static Random rand = new Random();
	StrategyBestFirst strategy;
	Queue<StrategyActionNode> commandQueue;
	
	public Plan(World world, Intention i, int agentId) {
		Heuristic h = new Greedy(i, agentId);
		strategy = new StrategyBestFirst(h);
		
		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( world );

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
				break;
			}

			World leafNode = strategy.getAndRemoveLeaf();

			//TODO DON'T DELETE
//			if ( world.isGoalCompleted(i.getGoal()) ) {
//				commandQueue = 
//				break;
//			}
//
//			strategy.addToExplored( leafNode );
//			for ( Node n : leafNode.getExpandedNodes() ) {
//				if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
//					strategy.addToFrontier( n );
//				}
//			}
			iterations++;
		}
	}
	
	public Command execute() {
		return Command.every[rand.nextInt( Command.every.length )];
	}
}
