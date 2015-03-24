package client;



public class Goal {
	private int X;
	private int Y;
	private int letter;	
	
	
	public Goal(int x, int y, int letter) {
		super();
		X = x;
		Y = y;
		this.letter = letter;
	}
	
	public int getX() {
		return X;
	}
	public void setX(int x) {
		X = x;
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}
	public int getLetter() {
		return letter;
	}
	public void setLetter(int letter) {
		this.letter = letter;
	}	
	
}
