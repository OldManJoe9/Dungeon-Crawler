package DungeonCrawler;

import java.util.ArrayList;
import java.util.Comparator;

import DungeonCrawler.Door.IncorrectDirectionException;

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
		
		Direction[] ds = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
		for(Direction d : ds) connectRoom(x, y, d);
	}
	
	public void removeRoom(int x, int y)
	{
		Room remove = getRoom(x, y);
		remove.onRemoval();
		allRooms.remove(remove);
	}
	
	private void connectRoom(int x, int y, Direction dir)
	{
		Room currentRoom = getRoom(x,y);
		
		switch(dir) {
		case NORTH: case UP:
			y++; break;
		case SOUTH: case DOWN:
			y--; break;
		case WEST: case LEFT:
			x++; break;
		case EAST: case RIGHT:
			x--; break;
		}
		
		Room adjacentRoom = getRoom(x, y);
		if(adjacentRoom != null)
		{
			try
			{
				adjacentRoom.addConnection(dir, currentRoom);
				Door adjacentDoor = adjacentRoom.getDoor(dir);
				adjacentDoor.setRoom(dir, currentRoom);
				currentRoom.addConnection(Direction.getOpposite(dir), adjacentDoor);
			}
			catch (IncorrectDirectionException e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}