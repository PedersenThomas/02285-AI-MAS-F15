package client;

public class NotifyAgentCommand extends Command {

	int agentId;
	public NotifyAgentCommand(int agentId) {
		super(null);
		this.agentId = agentId;
	}
	
	public int getAgentId() {
		return agentId;
	}
	
	@Override
	public String toString() {
		return "NoOp";
	}

}
