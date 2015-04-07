package client;

import java.io.*;
import java.util.*;

public class Client {
	public World world = new World();
	
	public class Agent {
		// We don't actually use these for Randomly Walking Around
		private int id;
		private String color;	
		private Beliefs B;
		private Point position;
		Plan plan = null;

		Agent( int id, String color, Point position ) {
			if(id > 9) {
				throw new RuntimeException("Id is invalid: " + id);
			}
			this.id = id;
			this.color = color;
			this.position = position;
			
			// Initial Beliefs
			this.B = new Beliefs();
		}
		
		public Agent CloneAgent() {
			return new Agent(this.id, this.color, this.position);
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
		
		public String act() {
			
			// BDI Version 2
			if((plan == null) || (plan.commandQueue.size() == 0)) {
			
				// Update Beliefs
				B.brf();
				
				//deliberate by choosing a set of intentions based on current beliefs
				Intention I = Intention.deliberate(world, this);
				//compute a plan from current beliefs and intentions:
				plan = new Plan(world, I, this);
			}
			
			//execute the plan
			Command cmd = plan.execute();
			world.update(this, cmd);
			System.err.println(cmd.toString() + "\t-> " + this.position.toString());
			return cmd.toString();
			
		}
		
		@Override
		public String toString() {
			return "AGENT " + id + " Color: " + color + " Position: " + position;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.id;
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
			Agent other = (Agent) obj;
			if(this.id != other.id) {
				return false;
			}
			if(!this.position.equals(other.position)) {
				return false;
			}
			return true;
		}
	}

	private BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
	

	public Client() throws IOException {
		readMap();
	}

	private void readMap() throws IOException {
		Map< Character, String > colors = new HashMap< Character, String >();
		String line, color;

		// Read lines specifying colors
		while ( ( line = in.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
			line = line.replaceAll( "\\s", "" );
			color = line.split( ":" )[0];

			for ( String id : line.split( ":" )[1].split( "," ) )
				colors.put( id.charAt( 0 ), color );
		}

		// Read lines specifying level layout
		int y=0;
		int width = 0;
		int boxId = 0;
		while ( !line.equals( "" ) ) {
			for ( int i = 0; i < line.length(); i++ ) {
				char id = line.charAt( i );
				if ( '0' <= id && id <= '9' )
					world.addAgent( new Agent( id - '0', colors.get( id ),new Point(i,y) ) );
				else if ( 'A' <= id && id <= 'Z' )
					world.addBox( new Box(i,y,id,Color.BLUE, boxId++));
				else if ( 'a' <= id && id <= 'z' )
					world.addGoal( new Goal(i,y,id));
				else if ( id=='+')
					world.addWall(i,y);
			}
			if(line.length() > width) {
				width = line.length();
			}
			line = in.readLine();
			y++;

		}
		world.setLevelSize(width, y);
		
	}

	public boolean update() throws IOException {
		String jointAction = "[";

		for ( int i = 0; i < world.getNumberOfAgents() - 1; i++ )
			jointAction += world.getAgent( i ).act() + ",";
		
		jointAction += world.getAgent( world.getNumberOfAgents() - 1 ).act() + "]";

		// Place message in buffer
		System.out.println( jointAction );
		
		// Flush buffer
		System.out.flush();

		// Disregard these for now, but read or the server stalls when its output buffer gets filled!
		String percepts = in.readLine();
		if ( percepts == null )
			return false;

		return true;
	}

	public static void main( String[] args ) {
		
		// Use stderr to print to console
		System.err.println( "Hello from RandomWalkClient. I am sending this using the error outputstream" );
		try {
			Client client = new Client();
			while ( client.update() )
				;

		} catch ( IOException e ) {
			// Got nowhere to write to probably
		}
	}
}
