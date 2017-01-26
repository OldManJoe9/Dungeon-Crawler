package DungeonCrawler;
import java.util.ArrayList;

public enum Direction
	{
		LEFT("LEFT"), RIGHT("RIGHT"), UP("UP"), DOWN("DOWN"), WEST("WEST"), EAST("EAST"), NORTH("NORTH"), SOUTH("SOUTH");
		
		private char[] stringDirection;
		private Direction(String sc) {stringDirection=sc.toCharArray();}
		
		public static Direction fromString(String s)
		{
			char[] sChars = s.toUpperCase().toCharArray();
			ArrayList<Direction> allDirections = new ArrayList<Direction>();
			
			for(Direction c : Direction.values()) allDirections.add(c);
			
			for(int a=0;a<sChars.length;a++)
				for(int b=allDirections.size()-1; b>=0; b--)
				{
					char[] currentDirection = allDirections.get(b).stringDirection;
					if(a >= currentDirection.length || currentDirection[a] != sChars[a]) allDirections.remove(b);
				}
			
			if(allDirections.size() == 1) return allDirections.get(0);
			else return null;
		}
		
		public static Direction getOpposite(Direction d)
		{
			switch(d){
			case NORTH: return SOUTH;
			case UP: return DOWN;
			case EAST: return WEST;
			case RIGHT: return LEFT;
			case SOUTH: return NORTH;
			case DOWN: return UP;
			case WEST: return EAST;
			case LEFT: return RIGHT;
			}
			
			return null;
		}
		
		public static Direction getEquivalent(Direction d)
		{
			switch(d){
			case NORTH: return UP;
			case UP: return NORTH;
			case EAST: return RIGHT;
			case RIGHT: return EAST;
			case SOUTH: return DOWN;
			case DOWN: return SOUTH;
			case WEST: return LEFT;
			case LEFT: return WEST;
			}
			
			return null;
		}
	}