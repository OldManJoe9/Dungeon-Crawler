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

import DungeonCrawler.Character;
import DungeonCrawler.Equipment;

public class CharacterGen implements Generator<Character> 
{
	private static boolean generated = false;
	private static ArrayList<Character> allCharacters = new ArrayList<Character>();
	private static Random randomGenerator = new Random();
	private static Path file = Paths.get("../data/Characters.txt");
	private static EquipmentGen equipmentGen;

	public CharacterGen(EquipmentGen eg)
	{
		equipmentGen = eg;
		if(!generated) {generate(); generated=true;}
	}
	
	private void generate()
	{
		if(!Files.exists(file)) System.exit(1);
		
		try(BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII))
		{
			String line = null;
		    while ((line = reader.readLine()) != null && !line.isEmpty()) 
		    	allCharacters.add(fromLine(line));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Character fromLine(String line) 
	{
		String name; int[] atts = new int[10]; Equipment[] equipment;
		
		int index = 0; int stopIndex;
		
		//String name
		stopIndex = line.indexOf(",");
		name = line.substring(index, stopIndex);
		index = stopIndex+2;
		
		//---------------*1234567890
		//playerCharacter, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, {}
		for(int a=0; a<atts.length; a++)
		{
			stopIndex = line.indexOf(",", index);
			if(stopIndex==-1) stopIndex = line.length();
			atts[a] = Integer.parseInt(line.substring(index, stopIndex));
			index = stopIndex+2;
		}
		
		//Equipment[] equipment
		ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();
		boolean done = false;
		if(line.charAt(index) == '}') index++;
		else while(!done)
		{
			stopIndex = line.indexOf(";", index);
			if(stopIndex==-1) done=true;
			else 
			{
				equipmentList.add(equipmentGen.get(line.substring(index, stopIndex)));
				index = stopIndex+2;
			}
		}
		
		equipment = new Equipment[equipmentList.size()];
		for(int a=0; a<equipmentList.size(); a++) equipment[a] = equipmentList.get(a); 
		
		return new Character(name, atts, equipment);
	}

	public Character get(String s) 
	{
		Character character = null;
		
		for(Character a : allCharacters)
			if(a.getName().equalsIgnoreCase(s)) {character = a; break;}
		
		return character;
	}

	public Character getRandom() 
	{
		return allCharacters.get(randomGenerator.nextInt(allCharacters.size()));
	}

	public Character getRandom(Predicate<Character> tester) 
	{
		ArrayList<Character> randomCharacters = new ArrayList<Character>();
		
		for(Character a : allCharacters) if(tester.test(a)) randomCharacters.add(a);
		
		return randomCharacters.get(randomGenerator.nextInt(randomCharacters.size()));
	}

	public ArrayList<Character> getAll() 
	{
		return allCharacters;
	}

}
