package client;

public class Box {
	public Point position;
	private int letter;
	private Color color;
	
	public Box(int x, int y, int letter, Color color) {
		super();
		this.position = new Point(x, y);
		this.letter = letter;
		this.color = color;
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

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}
}
