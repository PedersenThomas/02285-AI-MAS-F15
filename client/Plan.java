package client;


import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import client.Command;
import client.Goal;
import client.Logger;
import client.Point;
import client.SafeSpotDetector;
import client.TravelSubIntention;
import client.Client.Agent;
import client.Client.AgentStatus;
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
	private static final int maxItersIncrement = 300;
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
		
		Point blockedCell = world.simplePathCheck(agent,subIntention.getStartPosition(), subIntention.getEndPosition());
		if(blockedCell != null) {
			
			world.clearPath(agent, subIntention.getStartPosition(), subIntention.getEndPosition(), blockedCell);
			agent.sleep(30);
			Logger.logLine("TravelPlanner: simplePathCheck failed");
			return;
		}
		
		
		
		Heuristic h = new AStar(new HeuristicPathFunction(world,subIntention.getEndPosition()));
		strategy = new BestFirstSearch(h);
		
		boolean ignoreBoxes = false;
		boolean ignoreAgents = false;
//		World simpleWorld = world.getSimplifiedCopy(agent.getId());
		World correctWorld = new World(world);
		strategy.addToFrontier( new PathNode( correctWorld, world.getAgent(agent.getId()).getPosition(), subIntention.getEndPosition(), ignoreBoxes, ignoreAgents, agent.getId()) );
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
			    
				if(world.getBoxAt(subIntention.getEndPosition()) != null) {
					removeLastFromQueue(commandQueue);
				}
				
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
		
		if(!world.isFreeCell(subIntention.getEndPosition())) {
			Box box = world.getBoxAt(subIntention.getEndPosition());
			if(box != null) {		
				//Check if someone is already moving this box
				if(!world.existsIntentionForBox(box)) {
					world.addJob(new MoveBoxSubIntention(box, SafeSpotDetector.getSafeSpotForBox(world, box, null), 
							subIntention.getRootIntention(), agent.getId()));
					agent.sleep(30);
				}
				return;
			}

			Agent otherAgent = world.getAgentAt(subIntention.getEndPosition());
			if(otherAgent != null && otherAgent.getId() != agent.getId()) {	
				world.handleBlockingAgentConflict(agent, otherAgent, null);				
				return;
			}
			
		}
		
		Point blockedCell = world.simplePathCheck(agent,agent.getPosition(), subIntention.getStartPosition());
		if(blockedCell != null) {
			world.clearPath(agent, agent.getPosition(), subIntention.getStartPosition(), blockedCell);
			Logger.logLine("MoveBoxPlanner agent to box: simplePathCheck failed");
			agent.sleep(30);
			return;
		}
		
		blockedCell = world.simplePathCheck(agent,subIntention.getStartPosition(), subIntention.getEndPosition());
		if(blockedCell != null) {
			world.clearPath(agent, subIntention.getStartPosition(), subIntention.getEndPosition(), blockedCell);
			Logger.logLine("MoveBoxPlanner box to goal: simplePathCheck failed");
			agent.sleep(30);
			return;
		}
		
		
		
		Heuristic h = new AStar(new HeuristicPlannerFunction(subIntention, agent.getId()));
		strategy = new BestFirstSearch(h);

//		World simpleWorld = world.getSimplifiedCopy(agent.getId());
		World correctWorld = new World(world);
		strategy.addToFrontier( new PlannerNode( correctWorld, agent.getId() ) );

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
	
	@Override
	public String toString() {
		if(commandQueue == null) {
			return "CommandQueue is null";
		} else {
			return commandQueue.toString();
		}
	}
}
