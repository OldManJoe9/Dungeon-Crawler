package DungeonCrawler;
import java.io.Console;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

public class Room implements ItemCarrier
{
	private ArrayList<Item> items = new ArrayList<Item>();
	private ArrayList<Container> containers = new ArrayList<Container>();
	private Door[] doors = new Door[4];
	private int x, y;
	private Random randomGenerator = new Random();
	private Character creature = null;
		
	public Room(int x, int y)
	{
		this.x = x;
		this.y = y;
		generate();
	}
	
	private void generate()
	{
		int count = randomGenerator.nextInt(3);
		
		for(int a=count;a>0;a--)
			items.add(DungeonCrawler.itemGen.getRandom());
		
		count = randomGenerator.nextInt(3);
		
		for(int a=count;a>0;a--)
			containers.add(DungeonCrawler.containerGen.getRandom());
			
		for(int a=0;a<containers.size();a++)
			for(int b=a+1;b<containers.size();b++)
				if(containers.get(a).getName().equals(containers.get(b).getName())) 
				{
					containers.get(b).setIdentifier(containers.get(a).getIdentifierInt()+1);
					break;
				}
		
		if(randomGenerator.nextInt(100) < 25) creature = new Character(DungeonCrawler.characterGen.getRandom());
		
		Direction[] ds = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
		for(int a=0; a<4; a++) doors[a] = new Door(this, ds[a]);
	}
	
	public Room getAdjacentRoom(Direction dir)
	{
		try
		{
			switch(dir){
			case NORTH: case UP:
				return doors[0].getRoom(dir);
			case EAST: case RIGHT:
				return doors[1].getRoom(dir);
			case SOUTH: case DOWN:
				return doors[2].getRoom(dir);
			case WEST: case LEFT:
				return doors[3].getRoom(dir);
			}
		}
		catch(Door.IncorrectDirectionException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		return null;
	}
	public Door getDoor(Direction dir)
	{
		switch(dir){
		case NORTH: case UP:
			return doors[0];
		case EAST: case RIGHT:
			return doors[1];
		case SOUTH: case DOWN:
			return doors[2];
		case WEST: case LEFT:
			return doors[3];
		}
		
		return null;
	}

	public void search(Console c) 
	{
		String display = "";
		for(Item i : items) display += i.getName() + "%n";
		
		for(Container con : containers) 
			if(con.getAllItems().isEmpty() && con.isSearched()) display += con.getName() + con.getIdentifier() + "%n";
			else display += "*" + con.getName() + con.getIdentifier() + "%n";
		
		for(Door door : doors)
			if(door.getLockLevel() != LockLevel.NONE)
			{
				String side = Direction.getOpposite(door.getMySide(this)).toString();
				display += String.format("%C%s Door%n", side.charAt(0), side.substring(1).toLowerCase()); 
			}
		
		if(display.isEmpty()) c.printf("The room is empty...%n%n");
		else c.printf(display + "%n");
	}
	public ArrayList<Item> getAllItems() {return items;}
	public void removeItem(Item i)
	{
		for(int a=0; a < items.size(); a++) 
		{
			Item it = items.get(a);
			if(it.equals(i)) {items.remove(a); return;}
		}
	}
	public void giveItem(Item i) {items.add(i);}
	public Item getItem(Item i) {return getItem(i2 -> i2.equals(i));}
	public Item getItem(Predicate<Item> tester)
	{
		ArrayList<Item> itemList = new ArrayList<Item>();
		
		for(Item i : items)
			if(tester.test(i)) itemList.add(i);
		
		if(itemList.size() == 1) return itemList.get(0);
		else if(itemList.size() == 0) return null;
		else return itemList.get(randomGenerator.nextInt(itemList.size()));
	}
	
	public Character getCreature() {return creature;}
	
	public ArrayList<Unlockable> getUnlockables() 
	{
		ArrayList<Unlockable> unlockables = new ArrayList<Unlockable>();
		
		unlockables.addAll(containers);
		for(Door d : doors) unlockables.add(d);
		
		return unlockables;
	}
	
	public void unlock(Container c, Key k) {c.unlock(k);}
	
	public int getX() {return x;}
	public int getY() {return y;}
	
	public void onRemoval()
	{
		try
		{
			if(getAdjacentRoom(Direction.NORTH) != null)
				getAdjacentRoom(Direction.NORTH).doors[2].setRoom(Direction.SOUTH, null);
			
			if(getAdjacentRoom(Direction.EAST) != null)
				getAdjacentRoom(Direction.EAST).doors[3].setRoom(Direction.WEST, null);
			
			if(getAdjacentRoom(Direction.SOUTH) != null)
				getAdjacentRoom(Direction.SOUTH).doors[0].setRoom(Direction.NORTH, null);
			
			if(getAdjacentRoom(Direction.WEST) != null)
				getAdjacentRoom(Direction.WEST).doors[1].setRoom(Direction.EAST, null);
		}
		catch(Door.IncorrectDirectionException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void addConnection(Direction dir, Door door)
	{
		switch(dir){
		case NORTH: case UP: 
			doors[0] = door; break;
		case EAST: case RIGHT:
			doors[1] = door; break;
		case SOUTH: case DOWN:
			doors[2] = door; break;
		case WEST: case LEFT:
			doors[3] = door; break;
		}
	}
	public void addConnection(Direction dir, Room adjacentRoom)
	{
		try
		{
			switch(dir){
			case NORTH: case UP: 
				doors[0].setRoom(Direction.NORTH, adjacentRoom); break;
			case EAST: case RIGHT:
				doors[1].setRoom(Direction.EAST, adjacentRoom); break;
			case SOUTH: case DOWN:
				doors[2].setRoom(Direction.SOUTH, adjacentRoom); break;
			case WEST: case LEFT:
				doors[3].setRoom(Direction.WEST, adjacentRoom); break;
			}
		}
		catch(Door.IncorrectDirectionException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public String toString()
	{
		return String.format("Room x: %s, y: %s", x, y);
	}
}
