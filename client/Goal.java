package client;



public class Goal {
	private Point position;
	private int letter;	
	
	public Goal(int x, int y, int letter) {
		super();
		position = new Point(x,y);
		this.letter = letter;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public int getLetter() {
		return letter;
	}
	public void setLetter(int letter) {
		this.letter = letter;
	}	
	
}
