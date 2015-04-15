package client;

import java.util.Queue;

import client.Client.Agent;
import client.Heuristic.AStar;
import client.Heuristic.Heuristic;
import client.Heuristic.HeuristicPlannerFunction;
import client.Search.BestFirstSearch;
import client.Search.SearchNode;
import client.Search.PlannerNode;

public class Plan {
	private BestFirstSearch strategy;
	private Queue<Command> commandQueue;

	public Plan(World world, Intention i, Agent agent) {
		if(i == null) {
			throw new RuntimeException("Intention is null");
		}
		System.err.println("Planing for Intention: " + i);
		Heuristic h = new AStar(new HeuristicPlannerFunction(i, agent.getId()));
		strategy = new BestFirstSearch(h);

		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( new PlannerNode( world, agent.getId() ) );

		int numUncompletedGoals = world.getNumberOfUncompletedGoals();
		int intendedGoalScore = world.getGoalPriorityScore(i.getGoal());
		int iterations = 0;
		while ( true ) {
			iterations++;
			if ( strategy.frontierIsEmpty() ) {
				break;
			}
			if(iterations % 10000 == 0)
			  System.err.println( iterations + "..." );

			PlannerNode leafNode = (PlannerNode)strategy.getAndRemoveLeaf();

			if ( leafNode.getWorld().isGoalCompleted(i.getGoal()) 
					//I don't think this will be a good idea when we start to move into Multi-Agents.
					/*&&
				 (leafNode.getWorld().getNumberOfUncompletedGoals() < numUncompletedGoals)*/) {
			    commandQueue = leafNode.extractListOfCommands();
				break;
			}

			strategy.addToExplored( leafNode );
			for ( SearchNode n : leafNode.getExpandedNodes() ) {
				if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {

					// Check if a completed goal has been destroyed
					boolean highPriorityGoalDestroyed = false;
					for(Goal g:n.getWorld().getGoals()) {
						if((!n.getWorld().isGoalCompleted(g)) &&
						    (leafNode.getWorld().isGoalCompleted(g))) {
							// Check if the destroyed goal has a higher priority score than the intended goal
							//if(n.getWorld().getGoalPriorityScore(g) > intendedGoalScore) {
							if(g.getTotalOrder() < i.getGoal().getTotalOrder()) {
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

	public Command peek() {
		return commandQueue.peek();
	}

	public boolean isEmpty() {
		return commandQueue.isEmpty();
	}
}
