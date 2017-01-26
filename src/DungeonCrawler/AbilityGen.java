package DungeonCrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

public class AbilityGen implements Generator<Ability> {
	private static boolean generated = false;
	private static ArrayList<Ability> allAbilities = new ArrayList<Ability>();
	private static Random randomGenerator = new Random();
	private static Path file = Paths.get("../data/Abilities.txt");
	private static SpecialAttributeGen specialAttributeGen;
	
	public AbilityGen(SpecialAttributeGen sag)
	{
		specialAttributeGen = sag;
		if(!generated) {generate();generated=true;}
	}
	
	private void generate()
	{
		if(!Files.exists(file)) System.exit(1);
		
		try(BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII))
		{
			String line = null;
		    while ((line = reader.readLine()) != null && !line.isEmpty()) 
		    	allAbilities.add(fromLine(line));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Ability fromLine(String line) 
	{
		String name; int manaCost; SpecialAttribute[] specAtts;
		
		int index = 0; int stopIndex;
		
		//*1234567890
		//Ability1, 1, {Potion;}
		//String name
		stopIndex = line.indexOf(",");
		name = line.substring(index, stopIndex);
		index = stopIndex+2;
		
		//int manaCost
		stopIndex = line.indexOf(",", index);
		manaCost = Integer.parseInt(line.substring(index, stopIndex));
		index = stopIndex+3;
		
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
		
		specAtts = new SpecialAttribute[specAttsList.size()];
		for(int a=0; a<specAttsList.size(); a++) specAtts[a] = specAttsList.get(a); 
		
		return new Ability(name, manaCost, specAtts);
	}

	public Ability get(String s) 
	{
		Ability ability = null;
		
		for(Ability a : allAbilities)
			if(a.getName().equalsIgnoreCase(s)) {ability = a; break;}
		
		return ability;
	}

	public Ability getRandom() {return allAbilities.get(randomGenerator.nextInt(allAbilities.size()));}

	public Ability getRandom(Predicate<Ability> tester) 
	{
		ArrayList<Ability> randomAbilities = new ArrayList<Ability>();
		
		for(Ability a : allAbilities) if(tester.test(a)) randomAbilities.add(a);
		
		return randomAbilities.get(randomGenerator.nextInt(randomAbilities.size()));
	}

	public ArrayList<Ability> getAll() {return allAbilities;}
}
