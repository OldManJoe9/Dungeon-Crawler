package DungeonCrawler;
import java.io.Console;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

public class MapSquare implements ItemCarrier
{
	private ArrayList<Item> items = new ArrayList<Item>();
	private ArrayList<Container> containers = new ArrayList<Container>();
	//private Door[] doors;
	private int[] coords = new int[2];
	private Random randomGenerator = new Random();
	private Character creature = null;
		
	public MapSquare() {generate();}
	
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
		
		//Direction[] ds = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
		//doors = ds;
	}
	
	public MapSquare getAdjacentMapSquare(MapSquare[][] map, Direction d)
	{
		switch(d){
		case NORTH:
		case UP:  if(coords[1]-1 != -1) return map[coords[0]][coords[1]-1]; else break;
		case EAST:
		case RIGHT: if(coords[0]+1 != map.length) return map[coords[0]+1][coords[1]]; else break;
		case SOUTH:
		case DOWN: if(coords[1]+1 != map[0].length) return map[coords[0]][coords[1]+1]; else break;
		case WEST:
		case LEFT: if(coords[0]-1 != -1) return map[coords[0]-1][coords[1]]; else break;
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
		else
			return itemList.get(randomGenerator.nextInt(itemList.size()));
	}
	
	public Character getCreature() {return creature;}
	
	public ArrayList<Unlockable> getUnlockables() 
	{
		ArrayList<Unlockable> unlockables = new ArrayList<Unlockable>();
		
		for(Container con : containers)	unlockables.add(con);
		
		return unlockables;
	}
	
	public void unlock(Container c, Key k) {c.unlock(k);}
	
	public int[] getCoordinates() {return coords;}
	public void addX() {coords[0]++;}
	public void addY() {coords[1]++;}
}
