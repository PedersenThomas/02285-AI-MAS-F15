package client;

enum LoggerLevel {
	Debug(0),
	Info(10),
	Error(100);
	
	private int rank;
	
	private LoggerLevel(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return this.rank;
	}
}

public class Logger {
	public static LoggerLevel level = LoggerLevel.Debug;
	
	private static boolean highEnoughLevel(LoggerLevel level) {
		return level.getRank() >= Logger.level.getRank();
	}
		
	public static void debug(Object msg) {
		logLine(msg, LoggerLevel.Debug);
	}
	
	public static void info(Object msg) {
		logLine(msg, LoggerLevel.Info);
	}

	public static void error(Object msg) {
		logLine(msg, LoggerLevel.Error);
	}
	
	public static void logLine(Object msg, LoggerLevel level) {
		if (highEnoughLevel(level)) {
			System.err.println(msg);	
		}
	}
	
	public static void log(Object msg, LoggerLevel level) {
		System.err.print(msg);
	}
	
	public static void logLineIf(Object msg, LoggerLevel level, boolean condition) {
		if(condition) {
			logLine(msg, level);	
		}
	}
}
