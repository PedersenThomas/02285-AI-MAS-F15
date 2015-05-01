package client;

public class Box {
	private int id;
	public Point position;
	private char letter;
	private String color = "NoColor";
	
	public Box(Box old) {
		this.id = old.id;
		this.letter = old.letter;
		this.color = old.color;
		this.position = old.position;
	}
	
	public Box(int x, int y, char letter, String color, int id) {
		this.position = new Point(x, y);
		this.letter = letter;
		this.id = id;

		if(color != null) {
			this.color = color;
		}
	}
	
	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "BOX id: " + id + " Letter: " + letter + " Color: " + color + " Position: " + position;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.position.hashCode();
		result = prime * result + this.letter;
		result = prime * result + this.color.hashCode();
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		Box other = (Box) obj;
		if(other.getId() != this.getId()) {
			return false;
		}
		if(!this.position.equals(other.position)) {
			return false;
		}
		if(this.letter != other.letter) {
			return false;
		}
		if(this.color != other.color) {
			return false;
		}
		return true;
	}
}
