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

public class SpecialAttributeGen implements Generator<SpecialAttribute> 
{
	private static boolean generated = false;
	private static ArrayList<SpecialAttribute> allSpecialAttributes = new ArrayList<SpecialAttribute>();
	private static Random randomGenerator = new Random();
	private static Path file = Paths.get("../data/SpecialAttributes.txt");

	public SpecialAttributeGen() {if(!generated) {generate(); generated=true;}}
	
	private void generate()
	{
		if(!Files.exists(file)) System.exit(1);
		
		try(BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII))
		{
			String line = null;
		    while ((line = reader.readLine()) != null && !line.isEmpty()) 
		    	allSpecialAttributes.add(fromLine(line));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SpecialAttribute fromLine(String line) 
	{
		String name; Attribute[] affectedAttributes; int timer; boolean stacking; String special;
		
		int index = 0; int stopIndex;
		
		//---------*1234567890
		//Potion, {HEALTH, 10;}, 1, true, none
		//String name
		stopIndex = line.indexOf(",");
		name = line.substring(index, stopIndex);
		index = stopIndex+3;
		
		//Attribute[] affectedAttributes;
		ArrayList<Attribute> attsList = new ArrayList<Attribute>();
		boolean done = false;
		if(line.charAt(index) == '}') index++;
		else while(!done)
		{
			stopIndex = line.indexOf(";", index);
			if(stopIndex==-1) {done=true;}
			else 
			{
				attsList.add(Attribute.fromString(line.substring(index, stopIndex)));
				index = stopIndex+2;
			}
		}
		index+=2;
		
		affectedAttributes = new Attribute[attsList.size()];
		for(int a=0; a<affectedAttributes.length; a++) affectedAttributes[a] = attsList.get(a);
		
		//int timer
		stopIndex = line.indexOf(",", index);
		timer = Integer.parseInt(line.substring(index, stopIndex));
		index = stopIndex+2;
		
		//boolean stacking
		stopIndex = line.indexOf(",", index);
		stacking = Boolean.parseBoolean(line.substring(index, stopIndex));
		index = stopIndex+2;
		
		//String special
		special = line.substring(index);
		
		return new SpecialAttribute(name, affectedAttributes, timer, stacking, special);
	}

	public SpecialAttribute get(String s) 
	{
		SpecialAttribute specialAttribute = null;
		
		for(SpecialAttribute sa : allSpecialAttributes)
			if(sa.getName().equalsIgnoreCase(s)) {specialAttribute = sa; break;}
		
		return specialAttribute;
	}

	public SpecialAttribute getRandom() {return allSpecialAttributes.get(randomGenerator.nextInt(allSpecialAttributes.size()));}

	public SpecialAttribute getRandom(Predicate<SpecialAttribute> tester) 
	{
		ArrayList<SpecialAttribute> randomSpecialAttribute = new ArrayList<SpecialAttribute>();
	
		for(SpecialAttribute sa : allSpecialAttributes) if(tester.test(sa)) randomSpecialAttribute.add(sa);
		
		return randomSpecialAttribute.get(randomGenerator.nextInt(randomSpecialAttribute.size()));
	}
	
	public ArrayList<SpecialAttribute> getAll() {return allSpecialAttributes;}

}
