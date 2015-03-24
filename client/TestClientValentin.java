package client;

import java.io.*;
import java.util.*;

import javax.swing.text.Position;

import client.RandomWalkClient.Agent;

public class TestClientValentin {
	private static Random rand = new Random();	
	public World world = new World();
	
	public class Agent {
		// We don't actually use these for Randomly Walking Around
		private char id;
		private String color;	
		private Beliefs B;
		
		public Point position;

		Agent( char id, String color, Point position ) {
			this.id = id;
			this.color = color;
			this.position = position;
			
			// Initial Beliefs
			this.B = new Beliefs();
		}

		
		
		public String act() {
			
			// BDI Version 2
			
			// Update Beliefs
			B.brf();
			
			//deliberate by choosing a set of intentions based on current beliefs
			Intention I = Intention.deliberate(world);
			
			//compute a plan from current beliefs and intentions:
			Plan pi = new Plan(world, I, id);
			
			//execute the plan
			Command cmd = pi.execute();
			world.update(this, cmd);
			System.err.println(cmd.toString() + "\t-> " + this.position.toString());
			return cmd.toString();
			
		}
	}

	private BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
	

	public TestClientValentin() throws IOException {
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
		while ( !line.equals( "" ) ) {
			for ( int i = 0; i < line.length(); i++ ) {
				char id = line.charAt( i );
				if ( '0' <= id && id <= '9' )
					world.addAgent( new Agent( id, colors.get( id ),new Point(i,y) ) );
				else if ( 'A' <= id && id <= 'Z' )
					world.addBox( new Box(i,y,id,Color.BLUE));
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
			TestClientValentin client = new TestClientValentin();
			while ( client.update() )
				;

		} catch ( IOException e ) {
			// Got nowhere to write to probably
		}
	}
}
