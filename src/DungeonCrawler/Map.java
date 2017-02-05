package DungeonCrawler;

import java.util.ArrayList;
import java.util.Comparator;

public class Map
{
	private ArrayList<Room> allRooms = new ArrayList<Room>();
	
	public Room getRoom(int x, int y)
	{
		for(Room room : allRooms)
			if(room.getX() == x && room.getY() == y) return room;
		
		return null;
	}
	
	public void spawnRoom(int x, int y)
	{
		allRooms.add(new Room(x, y));
		
		Comparator<Room> c = (r1, r2) -> r1.getX() - r2.getX();
		c.thenComparing((r1, r2) -> r1.getY() - r2.getY());
		
		allRooms.sort(c);
		
		connectRoom(x, y);
	}
	
	public void removeRoom(int x, int y)
	{
		Room remove = getRoom(x, y);
		remove.onRemoval();
		allRooms.remove(remove);
	}
	
	private void connectRoom(int x, int y)
	{
		Room currentRoom = getRoom(x,y), adjacentRoom = getRoom(x, y-1);
		if(adjacentRoom != null)
		{
			adjacentRoom.addConnection(currentRoom, Direction.SOUTH);
			currentRoom.addConnection(adjacentRoom, Direction.NORTH);
		}
		
		adjacentRoom = getRoom(x, y+1);
		if(adjacentRoom != null)
		{
			adjacentRoom.addConnection(currentRoom, Direction.NORTH);
			currentRoom.addConnection(adjacentRoom, Direction.SOUTH);
		}
		
		adjacentRoom = getRoom(x-1, y);
		if(adjacentRoom != null)
		{
			adjacentRoom.addConnection(currentRoom, Direction.EAST);
			currentRoom.addConnection(adjacentRoom, Direction.WEST);
		}
		
		adjacentRoom = getRoom(x+1, y);
		if(adjacentRoom != null)
		{
			adjacentRoom.addConnection(currentRoom, Direction.WEST);
			currentRoom.addConnection(adjacentRoom, Direction.EAST);
		}
	}
}