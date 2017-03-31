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

public class CharacterCreator
{
	private static Console console;
	private static Path file = Paths.get("../data/Characters.txt");
	private static ArrayList<String> characters = new ArrayList<String>(); 
	
	public static void main(String[] args) 
	{
		String name, strength, dexterity, wisdom, intelligence, endurance, luck, maxHealth, maxMana, experience, level, equipment;
		
		console = System.console();
		if(console == null)
		{
			System.err.println("No console.");
            System.exit(1);
        }
		
		readFile();
		
		for(;;)
		{
			//playerCharacter, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
			console.printf("You are about to create a new Character%n"
					+ "What would you like to name it?%n");
			name = console.readLine();
			
			//strength, dexterity, wisdom, intelligence, endurance, luck, health, maxHealth, mana, maxMana, experience, level
			console.printf("%nWhat is it's Strength?%n");
			strength = console.readLine();
			
			console.printf("%nWhat is it's dexterity?%n");
			dexterity = console.readLine();
			
			console.printf("%nWhat is it's wisdom?%n");
			wisdom = console.readLine();
			
			console.printf("%nWhat is it's intelligence?%n");
			intelligence = console.readLine();
			
			console.printf("%nWhat is it's endurance?%n");
			endurance = console.readLine();
			
			console.printf("%nWhat is it's luck?%n");
			luck = console.readLine();
			
			console.printf("%nWhat is it's Health?%n");
			maxHealth = console.readLine();
			
			console.printf("%nWhat is it's Mana?%n");
			maxMana = console.readLine();
			
			console.printf("%nWhat is it's experience?%n");
			experience = console.readLine();
			
			console.printf("%nWhat is it's level?%n");
			level = console.readLine();
			
			console.printf("%nWhat equipment does it have?%n"
					+ "Make sure this equipment is created.%n"
					+ "Separate each equipment by a semicolon.%n"
					+ "Example: Sword; Shield%n"
					+ "If none, just hit enter.%n");
			equipment = console.readLine() + ";";
			if(equipment.equals(";")) equipment = "";
			
			characters.add(name + ", " + strength + ", " + dexterity + ", " + wisdom + ", "
					+ intelligence + ", " + endurance + ", " + luck + ", " + maxHealth + ", "
					+ maxMana + ", " + experience + ", " + level + ", {" + equipment + "}");
			
			console.printf("%nAre you done making characters? (Y|N)%n");
			if(console.readLine().equalsIgnoreCase("Y")) break;
		}
		
		sortCharacters();
		writeFile();
	}
	
	private static void readFile()
	{
		try (BufferedReader reader = Files.newBufferedReader(file)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        if(!line.isEmpty()) characters.add(line);
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
		    for(String s : characters)
		    	writer.write(s + String.format("%n"));
		} 
		catch (IOException x) 
		{
		    System.err.format("IOException: %s%n", x);
		}
	}

	private static void sortCharacters() {characters.sort((a,b) -> a.compareTo(b));}
}