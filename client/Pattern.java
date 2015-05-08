package client;

import java.util.HashMap;

public class Pattern {
	private static HashMap<String, Integer> validPatterns = new HashMap<String, Integer>();
	
	static {	
		//0
		validPatterns.put("00000000", 1);
		
		//1
		validPatterns.put("10000000", 4);
		validPatterns.put("00100000", 4);
		validPatterns.put("00000100", 4);
		validPatterns.put("00000001", 4);
		
		//2
		validPatterns.put("11000000", 4);
		validPatterns.put("00101000", 4);
		validPatterns.put("00000011", 4);
		validPatterns.put("00010100", 4);
		//2ref
		validPatterns.put("01100000", 4);
		validPatterns.put("00001001", 4);
		validPatterns.put("00000110", 4);
		validPatterns.put("10010000", 4);
		
		//3
		validPatterns.put("11010000", 3);
		validPatterns.put("01101000", 3);
		validPatterns.put("00001011", 3);
		validPatterns.put("00010110", 3);
		
		//4
		validPatterns.put("11100000", 4);
		validPatterns.put("00101001", 4);
		validPatterns.put("00000111", 4);
		validPatterns.put("10010100", 4);
		
		//5
		validPatterns.put("11110000", 3);
		validPatterns.put("01101001", 3);
		validPatterns.put("00001111", 3);
		validPatterns.put("10010110", 3);
		//5ref                      
		validPatterns.put("11101000", 3);
		validPatterns.put("00101011", 3);
		validPatterns.put("00010111", 3);
		validPatterns.put("11010100", 3);
		
		//6
		validPatterns.put("01110100", 3);
		validPatterns.put("11001001", 3);
		validPatterns.put("00101110", 3);
		validPatterns.put("10010011", 3);
		
		//7
		validPatterns.put("11110100", 3);
		validPatterns.put("11101001", 3);
		validPatterns.put("00101111", 3);
		validPatterns.put("10010111", 3);
		
		//8
		validPatterns.put("11111100", 2);
		validPatterns.put("11101011", 2);
		validPatterns.put("00111111", 2);
		validPatterns.put("11010111", 2);
		//8ref                      
		validPatterns.put("11111001", 2);
		validPatterns.put("01101111", 2);
		validPatterns.put("10011111", 2);
		validPatterns.put("11110110", 2);
		
		//9
		validPatterns.put("11111101", 2);
		validPatterns.put("11101111", 2);
		validPatterns.put("10111111", 2);
		validPatterns.put("11110111", 2);
		
		//10
		validPatterns.put("01010000", 3);
		validPatterns.put("01001000", 3);
		validPatterns.put("00001010", 3);
		validPatterns.put("00010010", 3);
		                            
		//11                        
		validPatterns.put("01110000", 3);
		validPatterns.put("01001001", 3);
		validPatterns.put("00001110", 3);
		validPatterns.put("10010010", 3);
		//11ref
		validPatterns.put("11001000", 3);
		validPatterns.put("00101010", 3);
		validPatterns.put("00010011", 3);
		validPatterns.put("01010100", 3);
		
		//12
		validPatterns.put("01111100", 2);
		validPatterns.put("11001011", 2);
		validPatterns.put("00111110", 2);
		validPatterns.put("11010011", 2);
		//12ref                     
		validPatterns.put("11011001", 2);
		validPatterns.put("01101110", 2);
		validPatterns.put("10011011", 2);
		validPatterns.put("01110110", 2);
		
		//13
		validPatterns.put("11011100", 2);
		validPatterns.put("11101010", 2);
		validPatterns.put("00111011", 2);
		validPatterns.put("01010111", 2);
		//13ref                     
		validPatterns.put("01111001", 2);
		validPatterns.put("01001111", 2);
		validPatterns.put("10011110", 2);
		validPatterns.put("11110010", 2);
		
		//14                        
		validPatterns.put("01011100", 2);
		validPatterns.put("11001010", 2);
		validPatterns.put("00111010", 2);
		validPatterns.put("01010011", 2);
		//14ref                     
		validPatterns.put("01011001", 2);
		validPatterns.put("01001110", 2);
		validPatterns.put("10011010", 2);
		validPatterns.put("01110010", 2);
		
		//15
		validPatterns.put("01011000", 2);
		validPatterns.put("01001010", 2);
		validPatterns.put("00011010", 2);
		validPatterns.put("01010010", 2);

		//16
		validPatterns.put("11111000", 2);
		validPatterns.put("01101011", 2);
		validPatterns.put("00011111", 2);
		validPatterns.put("11010110", 2);
		
		//17
		validPatterns.put("01111000", 2);
		validPatterns.put("01001011", 2);
		validPatterns.put("00011110", 2);
		validPatterns.put("11010010", 2);
		//17ref                     
		validPatterns.put("11011000", 2);
		validPatterns.put("01101010", 2);
		validPatterns.put("00011011", 2);
		validPatterns.put("01010110", 2);
		
		//18
		validPatterns.put("01111101", 2);
		validPatterns.put("11001111", 2);
		validPatterns.put("10111110", 2);
		validPatterns.put("11110011", 2);
		//18ref                     
		validPatterns.put("11011101", 2);
		validPatterns.put("11101110", 2);
		validPatterns.put("10111011", 2);
		validPatterns.put("01110111", 2);
		
		//19
		validPatterns.put("01011101", 2);
		validPatterns.put("11001110", 2);
		validPatterns.put("10111010", 2);
		validPatterns.put("01110011", 2);
	}
	
	public static boolean isSafePoint(SafePoint spoint, World world) {
		String pointStringPattern = translatePointIntoPattern(spoint, world);
		Logger.logLine("point" + spoint + "("+pointStringPattern+")");
		
		if (validPatterns.containsKey(pointStringPattern)) {
			Logger.logLine(" is safe spot");
			spoint.setPriority(validPatterns.get(pointStringPattern));
			return true;
		}
		return false;
	}
	
	public static String translatePointIntoPattern (SafePoint spoint, World world) {
		StringBuffer buffer = new StringBuffer();
		for(int y = -1; y<=1; y++) {
			for(int x = -1; x<=1; x++) {
				if(x == 0 && y == 0) {
					continue;
				}
				Point newPoint = new Point(spoint.getX()+x,spoint.getY()+y);
				ObjectType objectType = getObjectTypeAtPoint(newPoint, world);
				if (objectType==ObjectType.wall || objectType==ObjectType.box) {
					buffer.append("1");
					spoint.increaseObjectCounter(objectType);
				} else {
					buffer.append("0");
				}
			}
		}
		return buffer.toString();
	}
	
	public static ObjectType getObjectTypeAtPoint (Point point, World world) {
		if (world.isWallAt(point)) {
			return ObjectType.wall;
		}
		if (world.isBoxAt(point)) {
			return ObjectType.box;
		}
//		if (world.isAgentAt(point)) {
//			return ObjectType.agent;
//		}
		return ObjectType.free;
	}
}
