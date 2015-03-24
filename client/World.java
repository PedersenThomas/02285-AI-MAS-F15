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
	
	public void printWorld() {
		System.err.println("==============================");
		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				Point p = new Point(x,y);
				if(wallAt(p)) {
					System.err.print("+");
				}
				else if(boxAt(p)) {
					System.err.print("A");
				}
				else {				
					boolean printFlag = false;
					for (Agent a:agents) {
						if(a.position.equals(p)) {
							System.err.print("0");
							printFlag = true;
							break;
						}
					}
					if(!printFlag) {
						System.err.print(" ");
					}
				}
			}
			System.err.print("\n");
		}
		System.err.println("==============================");
	}

	private boolean boxAt(Point point) {
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).getPosition().equals(point)) {
				return true;
				
			}
		}
		return false;
	}

	private boolean wallAt(Point point) {		
		for (int i = 0; i < walls.size(); i++) {
			if (walls.get(i).equals(point)) {
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
			if (boxes.get(i).getPosition().equals(position)) {
				return boxes.get(i);
			}
		}
		return null;
	}

	public int update(Agent a, Command c) {
		Point p=a.position;
		int flag=0;
		//System.err.println(c.actType+"sdfsdffd");
		if (c.actType==Command.type.Move){
			Point agentDestPosition = p.move(c.dir1);
			if (isFreeCell(agentDestPosition)) {
				a.position = agentDestPosition;
				flag=1;
				System.err.println(flag);
			}
		}else if (c.actType==Command.type.Push){
			Point bp,boxSrcPosition;
			bp=boxSrcPosition = p.move(c.dir1);
			Point boxDestPosition = bp.move(c.dir2);

			if (boxAt(boxSrcPosition) && isFreeCell(boxDestPosition)
					&& !Command.isOpposite(c.dir1, c.dir2)) {
				a.position = boxSrcPosition;
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(boxDestPosition);
				flag=2;
				System.err.println(flag);
			}
		}else if (c.actType==Command.type.Pull){
			Point agentDestPosition,ap;
			ap = agentDestPosition = p.move(c.dir1);
			Point boxSrcPosition = ap.move(c.dir2);
			if (boxAt(boxSrcPosition.x, boxSrcPosition.y)
					&& isFreeCell(agentDestPosition)
					&& !Command.isOpposite(c.dir1, c.dir2)) {
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(a.position);
				a.position = agentDestPosition;
				flag=3;
				System.err.println(flag);
			}
		}
		/*
		switch (c.actType) {
		case Move: {
			Point agentDestPosition = a.position.move(c.dir1);
			if (isFreeCell(agentDestPosition)) {
				a.position = agentDestPosition;
				flag=1;
				System.err.println(flag);
			}
			break;
		}
		case Push: {

			Point boxSrcPosition = a.position.move(c.dir1);
			Point boxDestPosition = boxSrcPosition.move(c.dir2);

			if (boxAt(boxSrcPosition) 
					&& isFreeCell(boxDestPosition)
					&& (!Command.isOpposite(c.dir1, c.dir2))) {
				a.position = boxSrcPosition;
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(boxDestPosition);
				flag=2;
				System.err.println(flag);
			}

			break;
		}
		case Pull: {
			Point agentDestPosition = a.position.move(c.dir1);
			Point boxSrcPosition = a.position.move(c.dir2);
<<<<<<< Updated upstream

			if (boxAt(boxSrcPosition)
=======
			if (boxAt(boxSrcPosition.x, boxSrcPosition.y)
>>>>>>> Stashed changes
					&& isFreeCell(agentDestPosition)
					&& !Command.isOpposite(c.dir1, c.dir2)) {
				Box b = getBoxAt(boxSrcPosition);
				b.setPosition(a.position);
				a.position = agentDestPosition;
				flag=3;
				System.err.println(flag);
			}
			break;
		}
		}
*/
		return flag;
	}
}
