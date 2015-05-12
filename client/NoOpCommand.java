package client;

public class NoOpCommand extends Command {
	public final static Command instance = new NoOpCommand();
	
	public NoOpCommand() {
		super(null);
	}

	@Override
	public String toString() {
		return "NoOp";
	}
}
