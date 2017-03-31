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

public class SpecialAttributeCreator
{
	private static Console console;
	private static Path file = Paths.get("../data/SpecialAttributes.txt");
	private static ArrayList<String> specialAttributes = new ArrayList<String>(); 
	
	public static void main(String[] args) 
	{
		String name, atts, timer, stacking, special;
		
		console = System.console();
		if(console == null)
		{
			System.err.println("No console.");
            System.exit(1);
        }
		
		readFile();
		
		for(;;)
		{
			console.printf("You are about to create a new Special Attribute!%n"
					+ "What shall it be named?%n");
			name = console.readLine();
			
			console.printf("%nWhat attributes does it affect and by how much?%n"
					+ "STRENGTH, DEXTERITY, WISDOM, INTELLIGENCE, ENDURANCE, LUCK, HEALTH, MAXHEALTH, MANA, MAXMANA, EXPERIENCE, LEVEL%n"
					+ "Separate the attribute from it's stat change by a comma and each attribute by a semicolon%n"
					+ "Example: STRENGTH, 1; ENDURANCE, 1%n");
			atts = console.readLine() + ";";
			if(atts.equals(";")) atts = "";
			
			console.printf("%nIs this special attribute for a single instant use?%n"
					+ "Such as how a potion would normally be used. (Y|N)%n");
			if(console.readLine().equalsIgnoreCase("Y"))
			{
				timer = "1";
				stacking = "true";
			}
			else
			{
				console.printf("%nHow long should this special attribute last?%n");
				timer = console.readLine();
				
				console.printf("%nDoes this special attribute stack on its self or is it permanent?%n"
						+ "Such as a poison. (true|false)%n");
				stacking = console.readLine();
			}
			
			console.printf("%nDoes this cause some type of special attribute?%n"
					+ "Examples: stop, poison, sleep, invincible, immune\"\", none%n");
			special = console.readLine();
			
			//Potion, {HEALTH, 10;}, 1, true, none
			specialAttributes.add(name + ", {" + atts + ";}, " + timer + ", " + stacking + ", " + special + "\n");
			
			console.printf("Are you done making special attributes? (Y|N)%n");
			
			if(console.readLine().equalsIgnoreCase("Y")) break;
		}
		
		sortSpecialAttributes();
		writeFile();
	}
	
	private static void readFile()
	{
		try (BufferedReader reader = Files.newBufferedReader(file)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        if(!line.isEmpty()) specialAttributes.add(line);
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
		    for(String s : specialAttributes)
		    	writer.write(s + String.format("%n"));
		} 
		catch (IOException x) 
		{
		    System.err.format("IOException: %s%n", x);
		}
	}

	private static void sortSpecialAttributes()
	{
		//specialAttributes.sort(null);
	}
}