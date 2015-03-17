package client;

public class Point {
	int x;
	int y;
	
	
	
	public Point(Point src) {
		this.x = src.x;
		this.y = src.y;
	}
	
	

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}



	public Point move(Command.dir dir) {
		Point p = new Point(this);
		
		switch (dir) {
		case E:
			p.x++;
			break;
		case W:
			p.x--;
			break;
		case N:
			p.y--;
			break;
		case S:
			p.y++;
			break;
		}
		return p;
	}
	
	public String toString() {
		return "(" + x + "," + y+ ")";
	}
	
	
}
