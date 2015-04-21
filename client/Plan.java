package client;

import java.util.List;
import java.util.Queue;

import client.Goal;
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

	public Plan(World world, SubIntention i, Agent agent) {
		if(i == null) {
			throw new RuntimeException("Intention is null");
		}
		System.err.println("Planing for Intention: " + i);
		Heuristic h = new AStar(new HeuristicPlannerFunction(i, agent.getId()));
		strategy = new BestFirstSearch(h);

		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( new PlannerNode( world, agent.getId() ) );

		int iterations = 0;
		List<Goal> completedGoals = world.getCompletedGoals();
		while ( true ) {
			iterations++;
			if ( strategy.frontierIsEmpty() ) {
				break;
			}
			if(iterations % 10000 == 0)
			  System.err.println( iterations + "..." );

			PlannerNode leafNode = (PlannerNode)strategy.getAndRemoveLeaf();

			if ( leafNode.getWorld().getBoxById(i.getBox().getId()).getPosition().equals(i.getEndPosition())) {
			    commandQueue = leafNode.extractListOfCommands();
				break;
			}

			strategy.addToExplored( leafNode );
			for ( SearchNode n : leafNode.getExpandedNodes() ) {
				if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
					
					// Check if a high-priority goal has been destroyed
					boolean validAction = true;
					for(Goal g:completedGoals) {
						if(g.getTotalOrder() < i.getRootIntention().getGoal().getTotalOrder() &&
						   !n.getWorld().isGoalCompleted(g)) {
							validAction = false;
							break;
						}
					}
					if(validAction)
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
