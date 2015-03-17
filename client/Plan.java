package client;

import java.util.Random;

public class Plan {
	private static Random rand = new Random();
	
	public Plan(Beliefs B, Intention I) {
		
	}
	
	public Command execute() {
		return Command.every[rand.nextInt( Command.every.length )];
	}
}
