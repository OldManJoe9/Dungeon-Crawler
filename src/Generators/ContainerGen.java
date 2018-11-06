package Generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

import DungeonCrawler.Container;
import DungeonCrawler.Item;
import DungeonCrawler.LockLevel;

public class ContainerGen implements Generator<Container>
{
	private static boolean generated = false;
	private static Random randomGenerator = new Random();
	private static int totalContainers = 0;
	private static Path file = Paths.get("../data/Containers.txt");
	private static ItemGen itemGen;
	
	public ContainerGen(ItemGen ig)
	{
		itemGen = ig;
		if(!generated) generate();
	}
	
	public void generate()
	{
		if(!Files.exists(file)) System.exit(1);
		
		try(BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII))
		{
			int count = 0;
			String line = null;
		    while ((line = reader.readLine()) != null && !line.isEmpty()) 
		    	count++;
		    
		    totalContainers = count;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Container fromLine(String line) 
	{
		String name; LockLevel lockLevel; ArrayList<Item> items = new ArrayList<Item>();
		
		int index = 0; int stopIndex;
		//String name
		stopIndex = line.indexOf(",");
		name = line.substring(index, stopIndex);
		index = stopIndex+2;
		
		//Key.LockLevel lockLevel
		stopIndex = line.indexOf(",", index);
		if(stopIndex != -1) lockLevel = LockLevel.fromString(line.substring(index, stopIndex));
		else {lockLevel = LockLevel.fromString(line.substring(index)); return new Container(name, lockLevel);}
		index = stopIndex+3;
		
		//Desk, NONE, {CommonPotion;}
		//-------------*1234567890
		//Bronze Chest, BRONZE
		//ArrayList<Item> items;		
		boolean done = false;
		while(!done)
		{
			stopIndex = line.indexOf(";", index);
			if(stopIndex==-1) done=true;
			else 
			{
				items.add(itemGen.get(line.substring(index, stopIndex)));
				index = stopIndex+2;
			}
		}
		
		return new Container(name, lockLevel, items);
	}

	public Container get(String s) 
	{
		return null;
	}

	public Container getRandom()
	{
		try(BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII))
		{
			String line = null;
			for(int a=-1; a<randomGenerator.nextInt(totalContainers); a++) line = reader.readLine();
			return fromLine(line);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Container getRandom(Predicate<Container> tester) 
	{
		return null;
	}

	public ArrayList<Container> getAll() 
	{
		return null;
	}
}
