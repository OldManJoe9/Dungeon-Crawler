package DungeonCrawler;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

public class Container implements Unlockable, ItemCarrier
{
	private String name;
	private ArrayList<Item> items = new ArrayList<Item>();
	private LockLevel lockLevel;
	private Random randomGenerator = new Random();
	private boolean searched = false;
	private int identifier=1;
	
	public Container(String n, LockLevel l) {name=n; lockLevel=l; generate();}
	public Container(String n, LockLevel l, ArrayList<Item> i) {name=n;lockLevel=l; generate(i);}
	
	private void generate()
	{
		int count = randomGenerator.nextInt(3);
		while(count > 0)
		{
			items.add(DungeonCrawler.itemGen.getRandom());
			count--;
		}
	}
	private void generate(ArrayList<Item> i) {for(Item it : i) if(randomGenerator.nextInt(2) == 1) items.add(it);}
	
	public String getName() {return name;}
	public int getIdentifierInt() {return identifier;}
	public String getIdentifier() 
	{
		if(identifier==1) return "";
		else return Integer.toString(identifier);
	}
	public void setIdentifier(int i) {identifier=i;}
	public LockLevel getLockLevel() {return lockLevel;}
	public void unlock(Key k) {if(k.getLockLevel().getLevel() >= lockLevel.getLevel()) lockLevel = LockLevel.NONE;}
	public ArrayList<Item> getAllItems() {return items;}
	public void removeItem(Item i) 
	{
		for(int a=0; a < items.size(); a++) 
			if(items.get(a).equals(i)) {items.remove(a); return;}	
	}	
	public void giveItem(Item i) {}
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
	
	public ArrayList<Item> search() {if(lockLevel == LockLevel.NONE) {searched = true; return items;} return null;}
	public boolean isSearched() {return searched;}
}
