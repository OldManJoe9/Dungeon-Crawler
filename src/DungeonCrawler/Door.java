package DungeonCrawler;

import java.util.Random;

public class Door implements Unlockable
{
	private LockLevel lockLevel;
	private Room room1, room2;
	private Direction dir1, dir2;
	private Random randomGenerator = new Random();
	
	public Door(Room r1, Direction d1)
	{
		this(LockLevel.NONE, r1, d1, null, Direction.getOpposite(d1));
		
		int roll = randomGenerator.nextInt(100);
		
		if(roll < 5) lockLevel = LockLevel.SILVER;
		else if(roll < 15) lockLevel = LockLevel.BRONZE;
	}
	public Door(LockLevel l, Room r1, Direction d1, Room r2, Direction d2)
	{
		lockLevel = l;
		room1 = r1; room2 = r2;
		dir1 = d1; dir2 = d2;
	}
	
	public void unlock(Key k) {if(k.getLockLevel().getLevel() >= lockLevel.getLevel()) lockLevel = LockLevel.NONE;}
	public LockLevel getLockLevel() {return lockLevel;}
	public String getName() {return "Door";}
	
	public void setRoom(Direction dir, Room r) throws IncorrectDirectionException
	{
		if(dir == dir1 || dir == Direction.getEquivalent(dir1)) room1 = r;
		else if(dir == dir2 || dir == Direction.getEquivalent(dir2)) room2 = r;
		else throw new IncorrectDirectionException();
	}
	
	public Room getRoom(Direction dir) throws IncorrectDirectionException
	{
		if(dir == dir1 || dir == Direction.getEquivalent(dir1)) return room1;
		else if(dir == dir2 || dir == Direction.getEquivalent(dir2)) return room2;
		else throw new IncorrectDirectionException();
	}
	
	@SuppressWarnings("serial")
	public class IncorrectDirectionException extends Exception
	{
		public IncorrectDirectionException() {super("This door does not swing in that direction...");}
	}
	
	public String toString()
	{
		return String.format("%s <- %s Door %s -> %s", room1, dir1, dir2, room2);
	}
	
	public Direction getMySide(Room room)
	{
		if(room == room1) return dir1;
		else if(room == room2) return dir2;
		else return null;
	}
}
