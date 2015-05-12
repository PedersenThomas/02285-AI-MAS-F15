package client;

import java.util.LinkedList;
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
	
	
	private static final int maxItersStart = 1000;
	private static final int maxItersIncrement = 1500;
	private static int maxIters = maxItersStart;

	public Plan(World world, SubIntention subIntention, Agent agent) {
		//Logger.debug("Plan iterations limit: " + maxIters);
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
		Logger.logLine("Planing for Intention: " + subIntention);
		Heuristic h = new AStar(new HeuristicPathFunction(world,subIntention.getEndPosition()));
		strategy = new BestFirstSearch(h);
		
		boolean ignoreBoxes = false;
		strategy.addToFrontier( new PathNode( world, world.getAgent(agent.getId()).getPosition(), subIntention.getEndPosition(), ignoreBoxes) );
		int iterations = 0;
		
		while ( true ) {
			iterations++;
			if ( strategy.frontierIsEmpty() ) {
				break;
			}
			if(iterations % 10000 == 0) {
				Logger.logLine( iterations + "..." );
			}
			
			if(iterations > maxIters) {
				maxIters += maxItersIncrement;
				return;
			}

			PathNode leafNode = (PathNode)strategy.getAndRemoveLeaf();

			if (subIntention.isCompleted(leafNode)) {
				commandQueue = leafNode.extractListOfCommands();
			    removeLastFromQueue(commandQueue);
				if(subIntention.getOwner() != agent.getId()) {
					commandQueue.add(new NotifyAgentCommand(subIntention.getOwner()));
				}		
				
			    world.putPlan(agent.getId(), commandQueue);
			    
			    if(commandQueue.isEmpty())
			    	commandQueue.add(new NoOpCommand());
			    //maxIters = maxItersStart;
			    break;
			}

			strategy.addToExplored(leafNode);
			for (SearchNode n : leafNode.getExpandedNodes()) {
				if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
					strategy.addToFrontier(n);
				}
			}
		}
		//if(commandQueue == null)
		//	throw new RuntimeException("No plan found for Intention: " + subIntention + " ("+ iterations + " iterations)");
	}
	
	private void MoveBoxPlanner(World world, MoveBoxSubIntention subIntention, Agent agent) {
		if(subIntention == null) {
			throw new RuntimeException("Intention is null");
		}
		if(!subIntention.getBox().getColor().equals(agent.getColor())) {
			throw new RuntimeException("Planning for a invalid move: Agent and only move boxes of same color: " + subIntention.getBox() + " " + agent);
		}
		
		Logger.logLine("[" + agent.getId() + "] Planing for Intention: " + subIntention);
		Heuristic h = new AStar(new HeuristicPlannerFunction(subIntention, agent.getId()));
		strategy = new BestFirstSearch(h);

		strategy.addToFrontier( new PlannerNode( world, agent.getId() ) );

		int iterations = 0;
		List<Goal> completedGoals = world.getCompletedGoals();
		while ( true ) {
			iterations++;
			if ( strategy.frontierIsEmpty() ) {
				break;
			}
			if(iterations % 10000 == 0) {
				Logger.logLine( iterations + "..." );
			}
			
			if(iterations > maxIters) {
				maxIters += maxItersIncrement;
			  return;
			}

			PlannerNode leafNode = (PlannerNode)strategy.getAndRemoveLeaf();

			
			if ( subIntention.isCompleted(leafNode)) {
			    commandQueue = leafNode.extractListOfCommands();
			    if(subIntention.getOwner() != agent.getId()) {
					commandQueue.add(new NotifyAgentCommand(subIntention.getOwner()));
				}
			    world.putPlan(agent.getId(), commandQueue);
			    maxIters = maxItersStart;
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
		//if(commandQueue == null)
		//	throw new RuntimeException("No plan found for Intention: " + subIntention + " ("+ iterations + " iterations)");
	}

	public Command execute() {
		return commandQueue.poll();
	}

	public Command peek() {
		return commandQueue.peek();
	}

	public boolean isEmpty() {
		if(commandQueue == null)
			return true;
		
		return commandQueue.isEmpty();
	}
	
	public void removeLastFromQueue(Queue<Command> queue) {
		if(queue instanceof LinkedList<?>) {
			LinkedList<Command> list = (LinkedList<Command>)queue;
			list.removeLast();
		}
	}
}
