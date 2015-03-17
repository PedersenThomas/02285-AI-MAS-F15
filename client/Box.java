package client;



public class Box {
	private int X;
	private int Y;
	private int letter;
	private Color color;
	
	
	
	public Box(int x, int y, int letter, Color color) {
		super();
		X = x;
		Y = y;
		this.letter = letter;
		this.color = color;
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
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	
}
