package client;

import java.util.HashSet;

public class Pattern {
	private static HashSet<String> validPatterns = new HashSet<String>();
	
	static {	
		//0
		validPatterns.add("00000000");
		
		//1
		validPatterns.add("10000000");
		validPatterns.add("00100000");
		validPatterns.add("00000100");
		validPatterns.add("00000001");
		
		//2
		validPatterns.add("11000000");
		validPatterns.add("00101000");
		validPatterns.add("00000011");
		validPatterns.add("00010100");
		//2ref
		validPatterns.add("01100000");
		validPatterns.add("00001001");
		validPatterns.add("00000110");
		validPatterns.add("10010000");
		
		//3
		validPatterns.add("11010000");
		validPatterns.add("01101000");
		validPatterns.add("00001011");
		validPatterns.add("00010110");
		
		//4
		validPatterns.add("11100000");
		validPatterns.add("00101001");
		validPatterns.add("00000111");
		validPatterns.add("10010100");
		
		//5
		validPatterns.add("11110000");
		validPatterns.add("01101001");
		validPatterns.add("00001111");
		validPatterns.add("10010110");
		//5ref
		validPatterns.add("11101000");
		validPatterns.add("00101011");
		validPatterns.add("00010111");
		validPatterns.add("11010100");
		
		//6
		validPatterns.add("01110100");
		validPatterns.add("11001001");
		validPatterns.add("00101110");
		validPatterns.add("10010011");
		
		//7
		validPatterns.add("11110100");
		validPatterns.add("11101001");
		validPatterns.add("00101111");
		validPatterns.add("10010111");
		
		//8
		validPatterns.add("11111100");
		validPatterns.add("11101011");
		validPatterns.add("00111111");
		validPatterns.add("11010111");
		//8ref
		validPatterns.add("11111001");
		validPatterns.add("01101111");
		validPatterns.add("10011111");
		validPatterns.add("11110110");
		
		//9
		validPatterns.add("11111101");
		validPatterns.add("11101111");
		validPatterns.add("10111111");
		validPatterns.add("11110111");
		
		//10
		validPatterns.add("01010000");
		validPatterns.add("01001000");
		validPatterns.add("00001010");
		validPatterns.add("00010010");
		
		//11
		validPatterns.add("01110000");
		validPatterns.add("01001001");
		validPatterns.add("00001110");
		validPatterns.add("10010010");
		//11ref
		validPatterns.add("11001000");
		validPatterns.add("00101010");
		validPatterns.add("00010011");
		validPatterns.add("01010100");
		
		//12
		validPatterns.add("01111100");
		validPatterns.add("11001011");
		validPatterns.add("00111110");
		validPatterns.add("11010011");
		//12ref
		validPatterns.add("11011001");
		validPatterns.add("01101110");
		validPatterns.add("10011011");
		validPatterns.add("01110110");
		
		//13
		validPatterns.add("11011100");
		validPatterns.add("11101010");
		validPatterns.add("00111011");
		validPatterns.add("01010111");
		//13ref
		validPatterns.add("01111001");
		validPatterns.add("01001111");
		validPatterns.add("10011110");
		validPatterns.add("11110010");
		
		//14
		validPatterns.add("01011100");
		validPatterns.add("11001010");
		validPatterns.add("00111010");
		validPatterns.add("01010011");
		//14ref
		validPatterns.add("01011001");
		validPatterns.add("01001110");
		validPatterns.add("10011010");
		validPatterns.add("01110010");
		
		//15
		validPatterns.add("01011000");
		validPatterns.add("01001010");
		validPatterns.add("00011010");
		validPatterns.add("01010010");

		//16
		validPatterns.add("11111000");
		validPatterns.add("01101011");
		validPatterns.add("00011111");
		validPatterns.add("11010110");
		
		//17
		validPatterns.add("01111000");
		validPatterns.add("01001011");
		validPatterns.add("00011110");
		validPatterns.add("11010010");
		//17ref
		validPatterns.add("11011000");
		validPatterns.add("01101010");
		validPatterns.add("00011011");
		validPatterns.add("01010110");
		
		//18
		validPatterns.add("01111101");
		validPatterns.add("11001111");
		validPatterns.add("10111110");
		validPatterns.add("11110011");
		//18ref
		validPatterns.add("11011101");
		validPatterns.add("11101110");
		validPatterns.add("10111011");
		validPatterns.add("01110111");
		
		//19
		validPatterns.add("01011101");
		validPatterns.add("11001110");
		validPatterns.add("10111010");
		validPatterns.add("01110011");
	}
	
	public static boolean isSafePoint(Point point, World world) {
		String pointStringPattern = translatePointIntoPattern(point, world);
		//	System.err.println("------- pattern string -------");		
		System.err.println("point" + point + "("+pointStringPattern+")");
		
		if (validPatterns.contains(pointStringPattern)) {
			System.err.print(" is safe spot");
			return true;
		}
		return false;
	}
	
	public static String translatePointIntoPattern (Point point, World world) {
		StringBuffer buffer = new StringBuffer();
		for(int y = -1; y<=1; y++) {
			for(int x = -1; x<=1; x++) {
				if(x == 0 && y == 0) {
					continue;
				}
				Point newPoint = new Point(point.getX()+x,point.getY()+y);
				if (isOccupiedPoint(newPoint, world)) {
					buffer.append("1");
				} else {
					buffer.append("0");
				}
			}
		}
		return buffer.toString();
	}
	
	public static boolean isOccupiedPoint (Point point, World world) {
		if (world.isWallAt(point)) {
			return true;
		}
		if (world.isBoxAt(point)) {
			return true;
		}
		if (world.isAgentAt(point)) {
			return true;
		}
		return false;
	}
}
