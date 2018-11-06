package Generators;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

import DungeonCrawler.ClassifiedArrayList;
import DungeonCrawler.Equipment;
import DungeonCrawler.Key;
import DungeonCrawler.Item;
import DungeonCrawler.LockLevel;
import DungeonCrawler.SpecialAttribute;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemGen implements Generator<Item>
{
	private static boolean generated = false;
	private static ArrayList<ClassifiedArrayList<Item>> allItemLists = new ArrayList<ClassifiedArrayList<Item>>();
	private static Random randomGenerator = new Random();
	private static Path file = Paths.get("../data/Items.txt");
	private static SpecialAttributeGen specialAttributeGen;
	private static EquipmentGen equipmentGen;
	
	public ItemGen(SpecialAttributeGen sag, EquipmentGen eg)
	{
		specialAttributeGen = sag; equipmentGen = eg;
		if(!generated) {generate();generated=true;}
	}
	
	private void generate()
	{
		ClassifiedArrayList.Classification classification = null;
		
		if(!Files.exists(file)) System.exit(1);
		
		try(BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII))
		{
			String line = null;
			int count = -1;
		    while ((line = reader.readLine()) != null) 
		    {
		    	if(!line.isEmpty())
			    	if(ClassifiedArrayList.Classification.fromString(line) != null) 
			    	{
			    		classification = ClassifiedArrayList.Classification.fromString(line);
			    		allItemLists.add(new ClassifiedArrayList<Item>(ClassifiedArrayList.Classification.fromString(line)));
			    		count++;
			    	}
			    	else 
			    		allItemLists.get(count).add(fromLine(line, classification));
		    }
		    
		    addKeys();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void addKeys()
	{
		for(ClassifiedArrayList<Item> nal : allItemLists)
		{
			switch(nal.getClassification()){
			case COMMON: break;
			case UNCOMMON: nal.add(new Key(LockLevel.BRONZE)); break;
			case RARE: nal.add(new Key(LockLevel.SILVER)); break;
			case VERYRARE: break;
			case ULTRARARE: nal.add(new Key(LockLevel.GOLD)); break;
			}
		}
	}

	public Item fromLine(String line) {return null;}
	public Item fromLine(String line, ClassifiedArrayList.Classification classification)
	{
		String name; SpecialAttribute[] specAtts; int value; boolean isPriceless; String desc; String onUse;
		
		int index = 0; int stopIndex;
		
		//String name
		stopIndex = line.indexOf(",");
		name = line.substring(index, stopIndex);
		index = stopIndex+3;
		
		//Potion, {}, 0, false, Potion for finding game bugs., You are now more likely to find any bugs. If they exist...
		//Potion, {Potion;}, 0, false, Potion for finding game bugs., You are now more likely to find any bugs. If they exist...
		//---------*1234567890
		//Potion, {Potion; Potion;}, 1, true, none;}, 0, false, Potion for finding game bugs., You are now more likely to find any bugs. If they exist...
		//SpecialAttribute[] specAtts
		ArrayList<SpecialAttribute> specAttsList = new ArrayList<SpecialAttribute>();
		boolean done = false;
		if(line.charAt(index) == '}') index++;
		else while(!done)
		{
			stopIndex = line.indexOf(";", index);
			if(stopIndex==-1) done=true;
			else 
			{
				specAttsList.add(specialAttributeGen.get(line.substring(index, stopIndex)));
				index = stopIndex+2;
			}
		}
		index+=2;
		
		specAtts = new SpecialAttribute[specAttsList.size()];
		for(int a=0; a<specAtts.length; a++) specAtts[a] = specAttsList.get(a);
		
		//int value
		stopIndex = line.indexOf(",", index);
		value = Integer.parseInt(line.substring(index, stopIndex));
		index = stopIndex+2;
		
		//boolean isPriceless
		stopIndex = line.indexOf(",", index);
		isPriceless = Boolean.parseBoolean(line.substring(index, stopIndex));
		index = stopIndex+2;
		
		//String desc
		stopIndex = line.indexOf(",", index);
		desc = line.substring(index, stopIndex);
		index = stopIndex+2;
		
		//String onUse
		onUse = line.substring(index);
		
		return (new Item(name,specAtts,value,isPriceless,desc,onUse,classification));
	}
		
	public Item get(String s) 
	{
		for(ClassifiedArrayList<Item> i : allItemLists)
			for(Item i2 : i)
				if(i2.getName().equalsIgnoreCase(s)) return i2;
		
		return null;
	}
	public Item getRandom()
	{
		ArrayList<ClassifiedArrayList<Equipment>> allEquipmentLists = equipmentGen.getAllLists();
		int num = 0;
		int size = allItemLists.size();
		
		int[] classes = new int[size + allEquipmentLists.size()];
		
		for(int a=0; a<size; a++) 
		{
			num += allItemLists.get(a).size() * allItemLists.get(a).getMultiplier();
			classes[a] = num;
		}
		
		for(int b=0; b<allEquipmentLists.size(); b++) 
		{
			num += allEquipmentLists.get(b).size() * allEquipmentLists.get(b).getMultiplier();
			classes[b+size] = num;
		}
		
		int ran = randomGenerator.nextInt(num);
		
		for(int c=0; c<classes.length; c++)
			if(ran < classes[c]) 
			{
				if(c < allItemLists.size()) return getRandom(allItemLists.get(c).getClassification());
				else return equipmentGen.getRandom(allEquipmentLists.get(c-size).getClassification());
			}
		
		return null;
	}
	public Item getRandom(ClassifiedArrayList.Classification c)
	{
		ArrayList<Item> allItems = new ArrayList<Item>();
		
		for(ClassifiedArrayList<Item> i : allItemLists) if(i.getClassification().equals(c)) allItems = i;
		
		return allItems.get(randomGenerator.nextInt(allItems.size()));
	}
	public Item getRandom(Predicate<Item> tester) 
	{
		ArrayList<Item> randomItems = new ArrayList<Item>();
		
		for(Item i : getAll()) if(tester.test(i)) randomItems.add(i);
		
		return randomItems.get(randomGenerator.nextInt(randomItems.size()));
	}
	public ArrayList<Item> getAll()
	{
		ArrayList<Item> allItems = new ArrayList<Item>();
		
		for(ClassifiedArrayList<Item> eq : allItemLists)
			for(int a=0; a<eq.size(); a++)
				allItems.add(eq.get(a));
		
		return allItems;
	}
	public ArrayList<Item> getAll(ClassifiedArrayList.Classification c)
	{
		ArrayList<Item> allItems = new ArrayList<Item>();
		
		for(ClassifiedArrayList<Item> eq : allItemLists)
			if(eq.getClassification() == c)
			{
				for(int a=0; a<eq.size(); a++)
					allItems.add(eq.get(a));
				
				break;
			}
		
		return allItems;
	}
}
