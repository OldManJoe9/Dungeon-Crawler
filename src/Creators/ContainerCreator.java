package Creators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import DungeonCrawler.ContainerGen;
import DungeonCrawler.EquipmentGen;
import DungeonCrawler.ItemGen;
import DungeonCrawler.SpecialAttributeGen;

public class ContainerCreator extends ContainerGen
{
	private static Console console;
	private static Path file = Paths.get("../data/Containers.txt");
	private static ArrayList<String> containers = new ArrayList<String>(); 
	
	public ContainerCreator() {super(new ItemGen(new SpecialAttributeGen(), new EquipmentGen()));}
	
	public static void main(String[] args) 
	{
		String name, lock, items;
		
		console = System.console();
		if(console == null)
		{
			System.err.println("No console.");
            System.exit(1);
        }
		
		readFile();
		
		for(;;)
		{
			//Desk, NONE
			//Bronze Chest, BRONZE
			console.printf("You are about to create a new Container%n"
					+ "What would you like to name it?%n");
			name = console.readLine();
			
			console.printf("%nWhat level of lock do you want?%n"
					+ "NONE, BRONZE, SILVER, GOLD%n");
			lock = console.readLine().toUpperCase();
			
			console.printf("%nWhat items do you want in this?%n"
					+ "Separate each item by a semicolon%n"
					+ "Example: Item1; Item2; Item3%n"
					+ "For random items, say random or just hit enter%n");
			items = console.readLine() + ";";
			if(items.equals(";")) items = "random;";
			
			String container = String.format(name + ", " + lock);
			
			if(!items.equalsIgnoreCase("random;")) container += String.format(", {" + items + "}");

			containers.add(container);
			
			console.printf("%nAre you done making containers? (Y|N)%n");
			if(console.readLine().equalsIgnoreCase("Y")) {break;}
		}
		
		sortContainers();
		writeFile();
	}
	
	private static void readFile()
	{
		try (BufferedReader reader = Files.newBufferedReader(file)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        if(!line.isEmpty()) containers.add(line);
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
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
		    for(String s : containers)
		    	writer.write(s + String.format("%n"));
		} 
		catch (IOException x) 
		{
		    System.err.format("IOException: %s%n", x);
		}
	}

	private static void sortContainers() {containers.sort((a,b) -> a.compareTo(b));}
}