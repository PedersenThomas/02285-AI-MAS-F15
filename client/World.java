package client;

import java.util.ArrayList;
import java.util.List;

import client.TestClientValentin.Agent;

public class World {
	private List<Box> boxes = new ArrayList<Box>();
	private List<Goal> goals = new ArrayList<Goal>();
	private List<Agent> agents = new ArrayList<Agent>();
	private List<Point> walls = new ArrayList<Point>();
	private int width;
	private int height;

	public int getNumberOfAgents() {
		return agents.size();
	}

	public void setLevelSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Agent getAgent(int i) {
		return agents.get(i);
	}

	public void addBox(Box b) {
		boxes.add(b);
	}

	public void addWall(int x, int y) {
		walls.add(new Point(x, y));
	}

	public void addGoal(Goal g) {
		goals.add(g);
	}

	public void addAgent(Agent a) {
		agents.add(a);
	}

	private boolean boxAt(Point point) {
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).getPosition() == point) {
				return true;
			}
		}
		return false;
	}

	private boolean wallAt(Point point) {
		for (int i = 0; i < walls.size(); i++) {
			if (walls.get(i) == point) {
				return true;
			}
		}

		return false;
	}

	private boolean isFreeCell(Point point) {
		if (wallAt(point))
			return false;

		if (boxAt(point))
			return false;

		return true;
	}

	private Box getBoxAt(Point position) {
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).getPosition() == position) {
				return boxes.get(i);
			}
		}
		return null;
	}

	public int update(Agent a, Command c) {
		switch (c.actType) {
		case Move: {
			Point agentDestPosition = a.position.move(c.dir1);
			if (isFreeCell(agentDestPosition)) {
				a.position = agentDestPosition;
			}
			break;
		}
		case Push: {

			Point boxSrcPosition = a.position.move(c.dir1);
			Point boxDestPosition = boxSrcPosition.move(c.dir2);

			if (boxAt(boxSrcPosition) && isFreeCell(boxDestPosition)
					&& !Command.isOpposite(c.dir1, c.dir2)) {
				a.position = boxSrcPosition;
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(boxDestPosition);
			}

			break;
		}
		case Pull: {
			Point agentDestPosition = a.position.move(c.dir1);
			Point boxSrcPosition = a.position.move(c.dir2);

			if (boxAt(boxSrcPosition)
					&& isFreeCell(agentDestPosition)
					&& !Command.isOpposite(c.dir1, c.dir2)) {
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(a.position);
				a.position = agentDestPosition;
			}
			break;
		}
		}

		return 0;
	}
}
