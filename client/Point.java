package client;

public class Point {
	private int x;
	private int y;
	
	public Point(Point src) {
		this.x = src.x;
		this.y = src.y;
	}
	
	public Point(int x, int y) {
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
	
	/**
	 * Manhatten distance
	 * @param position
	 * @return
	 */
	public int distance(Point position) {
		return Math.abs(position.x - this.x) + Math.abs(position.y - this.y);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y+ ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Point)) {
			return false;
		}
		Point other = (Point)obj;
		return (other.x == this.x) && (other.y == this.y);
	}
	
	@Override
	public int hashCode() {
		final int prime = 127;
		return this.x * prime + 
			   this.y;
	}
}
