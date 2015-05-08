package client;

public class Logger {
	public static void logLine(Object msg) {
		System.err.println(msg);
	}
	
	public static void log(Object msg) {
		System.err.print(msg);
	}
	
	public static void logLineIf(Object msg, boolean condition) {
		if(condition) {
			System.err.println(msg);	
		}
	}
}
