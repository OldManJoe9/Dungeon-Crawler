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

public class AbilityCreator
{
	private static Console console;
	private static Path file = Paths.get("../data/Abilities.txt");
	private static ArrayList<String> abilities = new ArrayList<String>(); 
	
	public static void main(String[] args) 
	{
		String name, manaCost, specAtts;
		
		console = System.console();
		if(console == null)
		{
			System.err.println("No console.");
            System.exit(1);
        }
		
		readFile();
		
		for(;;)
		{
			console.printf("%nYou are about to create a new Ability.%n"
					+ "What would you like to name it?%n");
			name = console.readLine();
			
			console.printf("%nWhat is the mana cost?%n");
			manaCost = console.readLine();
			
			console.printf("%nWhat special attributes does it have, make sure these are already created, or make sure you create them.%n"
					+ "Separate each Special Attribute by a semicolon."
					+ "Example: SpecialAttribute1; SpecialAttribute2; SpecialAttribute%n");
			specAtts = console.readLine() + ";";
			
			abilities.add(name + ", " + manaCost + ", {" + specAtts + "}");
			
			console.printf("%nAre you done making abilities? (Y|N)%n");
			if(console.readLine().equalsIgnoreCase("Y")) break;
		}
		
		sortAbilities();
		writeFile();
	}
	
	private static void readFile()
	{
		try (BufferedReader reader = Files.newBufferedReader(file)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        if(!line.isEmpty()) abilities.add(line);
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
		    for(String s : abilities)
		    	writer.write(s + String.format("%n"));
		} 
		catch (IOException x) 
		{
		    System.err.format("IOException: %s%n", x);
		}
	}

	private static void sortAbilities() {abilities.sort((a,b) -> a.compareTo(b));}
}