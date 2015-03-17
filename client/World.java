package client;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.TestClientValentin.Agent;

public class World {
	private List< Box > boxes = new ArrayList< Box >();
	private List< Goal > goals = new ArrayList< Goal >();
	private List< Agent > agents = new ArrayList< Agent >();	
	private List< Point > walls = new ArrayList< Point >();	
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
		walls.add(new Point(x,y));
	}
	public void addGoal(Goal g) {
		goals.add(g);
	}
	
	public void addAgent(Agent a) {
		agents.add(a);
	}
	
	
	private boolean boxAt(int x, int y) {
		for(int i=0;i<boxes.size();i++) {
			if((boxes.get(i).getX() == x) && (boxes.get(i).getY() == y)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean wallAt(int x, int y) {
		for(int i=0;i<walls.size();i++) {
			if((walls.get(i).x == x) && (walls.get(i).y == y)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isFreeCell(int x, int y) {
		if(wallAt(x, y))
			return false;
		
		return !boxAt(x, y);
	}
	
	public int update(Agent a, Command c) {
		switch(c.actType) {
		case Move:
			switch(c.dir1) {
			case E:
				if(isFreeCell(a.position.x+1,a.position.y))
				  a.position.x += 1;
				break;
			case W:
				if(isFreeCell(a.position.x-1,a.position.y))
				  a.position.x -= 1;
				break;
			case S:
				if(isFreeCell(a.position.x,a.position.y+1))
					a.position.y+=1;
				break;
			case N:
				if(isFreeCell(a.position.x,a.position.y-1))
					a.position.y-=1;
				break;
			}
			break;
		case Pull:
			break;
		case Push:
			break;
		}
		
		return 0;
	}
}
