package client;

public class Goal {
	private Point position;
	private char letter;	
	
	public Goal(int x, int y, char letter) {
		super();
		position = new Point(x,y);
		this.letter = letter;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}	
	
	@Override
	public String toString() {
		return "Letter: " + letter + " Position: " + position;
	}
}
