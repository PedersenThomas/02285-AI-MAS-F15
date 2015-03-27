package client;

import java.util.Queue;
import java.util.Random;

import client.TestClientValentin.Agent;

public class Plan {
	private static Random rand = new Random();
	StrategyBestFirst strategy;
	Queue<Command> commandQueue;
	
	public Plan(World world, Intention i, Agent agent) {
		Heuristic h = new Greedy(i, agent.getId());
		strategy = new StrategyBestFirst(h);
		
		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( new StrategyActionNode( world, agent ) );

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
			iterations++;
		}
	}
	
	public Command execute() {
		//System.err.println(Command.every.length);
		//System.err.println("sdfsdffdff");
		int randNum=rand.nextInt(Command.every.length);
		//System.err.println(randNum);
		return Command.every[randNum];
		
	}
}
