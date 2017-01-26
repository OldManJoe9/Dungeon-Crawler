package DungeonCrawler;

import java.util.ArrayList;
import java.util.function.Predicate;

public interface ItemCarrier 
{
	public ArrayList<Item> getAllItems();
	public void removeItem(Item i);
	public void giveItem(Item i);
	public Item getItem(Predicate<Item> tester);
	public Item getItem(Item i);
	
	default public ArrayList<Item> getAllItems(Predicate<Item> tester)
	{
		ArrayList<Item> items = new ArrayList<Item>();
		
		for(Item it : getAllItems())
				if(tester.test(it)) items.add(it);
		
		return items;
	}
}
