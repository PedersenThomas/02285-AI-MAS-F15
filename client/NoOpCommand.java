package client;

public class NoOpCommand extends Command {
	public NoOpCommand() {
		super(null);
	}

	@Override
	public String toString() {
		return "NoOp";
	}
}
