package Creators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import DungeonCrawler.ItemGen;
import DungeonCrawler.SpecialAttributeGen;
import DungeonCrawler.EquipmentGen;
import DungeonCrawler.ClassifiedArrayList;
import DungeonCrawler.ClassifiedArrayList.Classification;

public class ItemCreator extends ItemGen
{
	private static Console console;
	private static Path file = Paths.get("../data/Items.txt");
	@SuppressWarnings("unchecked")
	private static ClassifiedArrayList<String>[] classifiedArrayLists = new ClassifiedArrayList[5];
	
	public ItemCreator()
	{
		super(new SpecialAttributeGen(), new EquipmentGen());
	}
	
	public static void main(String[] args) 
	{
		String name, specAtts, value, priceless, desc, onUse;
		Classification classification;
		
		console = System.console();
		if(console == null)
		{
			System.err.println("No console.");
            System.exit(1);
        }
		
		readFile();
		
		for(;;)
		{
			console.printf("You are about to create a new item.%n"
					+ "What shall it be named?%n");
			name = console.readLine();
			
			console.printf("%nWhat shall it be classified as?%n"
					+ "COMMON, UNCOMMON, RARE, SUPERRARE, or ULTRARARE?%n");
			classification = Classification.fromString(console.readLine());
			
			console.printf("%nWhat special attributes does it give, make sure these are already created, or make sure you create them.%n"
					+ "Separate each Special Attribute by a semicolon."
					+ "Example: SpecialAttribute1; SpecialAttribute2; SpecialAttribute%n");
			specAtts = console.readLine() + ";";
			if(specAtts.equals(";")) specAtts = "";
			
			console.printf("%nHow much is this worth?%n");
			value = console.readLine();
			
			console.printf("%nIs this item priceless? (true|false)%n"
					+ "Meaning should you be warned before selling it?%n");
			priceless = console.readLine();
			
			console.printf("%nHow should this item be described?%n");
			desc = console.readLine();
			
			console.printf("%nWhat should be said when using this item?%n");
			onUse = console.readLine();
			
			//Potion, {}, 0, false, Potion for finding game bugs., You are now more likely to find any bugs. If they exist...
			//Potion, {Potion;}, 0, false, Potion for finding game bugs., You are now more likely to find any bugs. If they exist...
			//Potion, {Potion; Potion;}, 1, true, none;}, 0, false, Potion for finding game bugs., You are now more likely to find any bugs. If they exist...
			for(ClassifiedArrayList<String> cal : classifiedArrayLists)
				if(cal.getClassification() == classification)
				{
					cal.add(name + ", {" + specAtts + "}, " + value
						+ ", " + priceless + ", " + desc + ", " + onUse + "\n");
					break;
				}
			
			console.printf("Are you done making items? (Y|N)%n");
			if(console.readLine().equalsIgnoreCase("Y")) break;
		}
		
		sortItems();
		writeFile();
	}
	
	private static void readFile()
	{
		try(BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII))
		{
			String line = null;
			int count = -1;
		    while ((line = reader.readLine()) != null) 
		    {
		    	if(!line.isEmpty())
			    	if(Classification.fromString(line) != null) 
			    	{
			    		count++;
			    		classifiedArrayLists[count] = (new ClassifiedArrayList<String>(Classification.fromString(line)));
			    	}
			    	else
			    		classifiedArrayLists[count].add(line);
		    }
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeFile()
	{
		try
		{
		    Files.deleteIfExists(file);
		}
		catch (SecurityException x)
		{
		    System.err.println(x);
		}
		catch (DirectoryNotEmptyException x)
		{
		    System.err.format("%s not empty%n", file);
		}
		catch (IOException x)
		{
		    // File permission problems are caught here.
		    System.err.println(x);
		}
		
		try 
		{
			Files.createFile(file);
		}
		catch (FileAlreadyExistsException e)
		{
			System.err.format("file named %s already exists%n", file);
		}
		catch (IOException e) 
		{
			System.err.format("createFile error: %s%n", e);
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(file)) 
		{
			for(ClassifiedArrayList<String> cal : classifiedArrayLists)
			{
				writer.write(cal.getClassification().toString() + String.format("%n"));
			    for(String s : cal)
			    	writer.write(s + String.format("%n"));
			    
			    writer.write(String.format("%n"));
			}
		} 
		catch (IOException x) 
		{
		    System.err.format("IOException: %s%n", x);
		}
	}

	private static void sortItems()
	{
		for(ClassifiedArrayList<String> cal : classifiedArrayLists)
		{
			cal.sort((a,b) -> a.compareTo(b));
		}
	}
}