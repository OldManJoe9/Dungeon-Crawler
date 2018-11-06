package DungeonCrawler;
import java.util.ArrayList;

public class Action
{
	public static String unlock(Unlockable u, Key k, ItemCarrier ic)
	{
		if(u.getLockLevel() != LockLevel.NONE) 
		{
			if(k == null) return "Could not unlock: " + u.getName() + ". You do not have the right key.%n";
			else
			{
				ic.removeItem(k);
				u.unlock(k);
				return "Unlocked: " + u.getName() + "%n";
			}
		}
		else return u.getName() + " does not need to be unlocked.%n";
	}
	public static String unlockAll(ArrayList<Unlockable> u, ArrayList<Key> k, ItemCarrier ic)
	{
		String display = "";
		
		u.sort((u1,u2) -> u2.getLockLevel().getLevel() - u1.getLockLevel().getLevel());
		k.sort((k1,k2) -> k2.getLockLevel().getLevel() - k1.getLockLevel().getLevel());
		
		for(int search = 0; search < 3; search++)
		{
			if(k.size() == 0) search = 2;
			
			for(Unlockable u2 : u)
			{
				if(search == 2 && u2.getLockLevel().getLevel() != 0) display += unlock(u2, null, ic);
				else
				{
					for(Key k2 : k)
					{
						if(k2.getLockLevel().getLevel() >= u2.getLockLevel().getLevel())
						{
							if(search == 0 && k2.getLockLevel() == u2.getLockLevel() || search == 1)
							{
								display += unlock(u2, k2, ic);
								k.remove(k2);
								break;
							}
						}
					}
				}
			}
		}
		
		return display + "%n";
	}

	public static String transfer(ItemCarrier takeFrom, ItemCarrier giveTo, Item i, String prompt)
	{
		takeFrom.removeItem(i);
		giveTo.giveItem(i);
		
		return prompt + i.getName() + "%n";
	}
	public static String transferAll(ItemCarrier takeFrom, ItemCarrier giveTo, ArrayList<Item> i, String prompt)
	{
		String display = "";
		
		for(int a=i.size()-1; a>-1; a--) display += transfer(takeFrom, giveTo, i.get(a), prompt);

		return display + "%n";
	}
}