package client;

import java.util.List;
import java.util.Queue;

import client.Goal;
import client.Client.Agent;
import client.Heuristic.AStar;
import client.Heuristic.Heuristic;
import client.Heuristic.HeuristicPathFunction;
import client.Heuristic.HeuristicPlannerFunction;
import client.Search.BestFirstSearch;
import client.Search.PathNode;
import client.Search.SearchNode;
import client.Search.PlannerNode;

public class Plan {
	private BestFirstSearch strategy;
	private Queue<Command> commandQueue;

	public Plan(World world, SubIntention subIntention, Agent agent) {
		if(subIntention instanceof MoveBoxSubIntention) {
			MoveBoxPlanner(world, (MoveBoxSubIntention)subIntention, agent);
		} else if (subIntention instanceof TravelSubIntention) {
			TravelPlanner(world, (TravelSubIntention)subIntention, agent);
		}
	}
	
	private void TravelPlanner(World world, TravelSubIntention subIntention, Agent agent) {
		if(subIntention == null) {
			throw new RuntimeException("Intention is null");
		}
		System.err.println("Planing for Intention: " + subIntention);
		Heuristic h = new AStar(new HeuristicPathFunction(subIntention.getEndPosition()));
		strategy = new BestFirstSearch(h);
		
		System.err.format( "Search starting with strategy %s\n", strategy );
		strategy.addToFrontier( new PathNode( world, world.getAgent(agent.getId()).getPosition(), subIntention.getEndPosition(), true) );
		int iterations = 0;
		while ( true ) {
			iterations++;
			if ( strategy.frontierIsEmpty() ) {
				break;
			}
			if(iterations % 10000 == 0)
			  System.err.println( iterations + "..." );

			PathNode leafNode = (PathNode)strategy.getAndRemoveLeaf();
			
			if (leafNode.getPosition().equals(subIntention.getEndPosition())) {
				//TODO: might be used
				//removeLastFromQueue(path);
				commandQueue = leafNode.extractListOfCommands();
			    world.putPlan(agent.getId(), commandQueue);

			}

			strategy.addToExplored(leafNode);
			for (SearchNode n : leafNode.getExpandedNodes()) {
				if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
					strategy.addToFrontier(n);
				}
			}
		}
	}
	
	private void MoveBoxPlanner(World world, MoveBoxSubIntention subIntention, Agent agent) {
		if(subIntention == null) {
			throw new RuntimeException("Intention is null");
		}
		if(!subIntention.getBox().getColor().equals(agent.getColor())) {
			throw new RuntimeException("Planning for a invalid move: Agent and only move boxes of same color: " + subIntention.getBox() + " " + agent);
		}
		
		System.err.println("Planing for Intention: " + subIntention);
		Heuristic h = new AStar(new HeuristicPlannerFunction(subIntention, agent.getId()));
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

			if ( leafNode.getWorld().getBoxById(subIntention.getBox().getId()).getPosition().equals(subIntention.getEndPosition())) {
			    commandQueue = leafNode.extractListOfCommands();
			    world.putPlan(agent.getId(), commandQueue);
				break;
			}
			
			strategy.addToExplored( leafNode );
			for ( SearchNode n : leafNode.getExpandedNodes() ) {
				if ( !strategy.isExplored( n ) && !strategy.inFrontier( n ) ) {
					
					// Check if a high-priority goal has been destroyed
					boolean validAction = true;
					for(Goal g:completedGoals) {
						Integer goalOrder = g.getTotalOrder(agent.getId());
						if(goalOrder != null && goalOrder < subIntention.getRootIntention().getGoal().getTotalOrder(agent.getId()) &&
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
