package client;

import java.util.Random;

public class Plan {
	private static Random rand = new Random();
	
	private int cnt = 0;
	public Plan(Beliefs B, Intention I) {
		
	}
	
	public Command execute() {
		//System.err.println(Command.every.length);
		//System.err.println("sdfsdffdff");
		int randNum=rand.nextInt(Command.every.length);
		//System.err.println(randNum);
		return Command.every[randNum];
		
	}
}
