package DungeonCrawler;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EquipmentGen implements Generator<Equipment>
{
	private static boolean generated = false;
	private static ArrayList<ClassifiedArrayList<Equipment>> allEquipmentLists = new ArrayList<ClassifiedArrayList<Equipment>>();
	private static Random randomGenerator = new Random();
	private static Path file = Paths.get("../data/Equipment.txt");

	public EquipmentGen()
	{
		if(!generated) {generate(); generated=true;}
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
			    		allEquipmentLists.add(new ClassifiedArrayList<Equipment>(ClassifiedArrayList.Classification.fromString(line)));
			    		count++;
			    	}
			    	else
			    		allEquipmentLists.get(count).add(fromLine(line, classification));
		    }
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Equipment fromLine(String line) {return null;}
	public Equipment fromLine(String line, ClassifiedArrayList.Classification classification)
	{
		String name; int value; Attribute[] atts; boolean isPriceless; String desc; String onUse;
		int equipmentPower; boolean isWeapon; Equipment.EquipmentSlot equipmentSlot; String onUnequip;
		
		int index = 0; int stopIndex;
		
		//String name
		stopIndex = line.indexOf(",");
		name = line.substring(index, stopIndex);
		index = stopIndex+2;
		
		//int value
		stopIndex = line.indexOf(",", index);
		value = Integer.parseInt(line.substring(index, stopIndex));
		index = stopIndex+3;
		
		//-------------*1234567890
		//Weapon1, 0, {}, false, ...
		//Weapon1, 0, {STRENGTH, 1; ENDURANCE, 1;}, false, Weapon1 of Debugging, Slash1 through game bugs!, 1, true, WEAPON, You can't slash game bugs without a weapon1!!
		//Attribute[] atts
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
		
		atts = new Attribute[attsList.size()];
		for(int a=0; a<atts.length; a++) atts[a] = attsList.get(a);
		
		//boolean isPriceless
		stopIndex = line.indexOf(",", index);
		isPriceless = Boolean.parseBoolean(line.substring(index, stopIndex));
		index = stopIndex+2;
		
		//String desc
		stopIndex = line.indexOf(",", index);
		desc = line.substring(index, stopIndex);
		index = stopIndex+2;
		
		//String onUse
		stopIndex = line.indexOf(",", index);
		onUse = line.substring(index, stopIndex);
		index = stopIndex+2;
		
		//int equipmentPower
		stopIndex = line.indexOf(",", index);
		equipmentPower = Integer.parseInt(line.substring(index, stopIndex));
		index = stopIndex+2;
		
		//boolean isWeapon
		stopIndex = line.indexOf(",", index);
		isWeapon = Boolean.parseBoolean(line.substring(index, stopIndex));
		index = stopIndex+2;
				
		//EquipmentSlot equipmentSlot
		stopIndex = line.indexOf(",", index);
		equipmentSlot = Equipment.EquipmentSlot.fromString(line.substring(index, stopIndex));
		index = stopIndex+2;
		
		//String onUnequip
		onUnequip = line.substring(index);
		
		return (new Equipment(name,value,atts,isPriceless,desc,onUse,equipmentPower,isWeapon,equipmentSlot,onUnequip, classification));
	}
	
	public Equipment get(String s) 
	{
		Equipment equipment = null;
		
		for(ClassifiedArrayList<Equipment> eq : allEquipmentLists)
			for(int a=0; a<eq.size(); a++)
				if(eq.get(a).getName().equalsIgnoreCase(s)) {equipment = eq.get(a); break;}
		
		return equipment;
	}

	public Equipment getRandom() 
	{
		int num = 0;
		
		int[] classes = new int[allEquipmentLists.size()];
		
		for(int a=0; a<allEquipmentLists.size(); a++) 
		{
			num += allEquipmentLists.get(a).size() * allEquipmentLists.get(a).getMultiplier();
			classes[a] = num;
		}
		
		int ran = randomGenerator.nextInt(num);
		
		for(int b=0; b<classes.length; b++)
			if(ran < classes[b]) 
				return getRandom(allEquipmentLists.get(b).getClassification());
		
		return null;
	}
	
	public Equipment getRandom(ClassifiedArrayList.Classification c)
	{
		ArrayList<Equipment> allEquipment = new ArrayList<Equipment>();
		
		for(ClassifiedArrayList<Equipment> eq : allEquipmentLists) if(eq.getClassification().equals(c)) allEquipment = eq;
		
		return allEquipment.get(randomGenerator.nextInt(allEquipment.size()));
	}

	public Equipment getRandom(Predicate<Equipment> tester) 
	{
		ArrayList<Equipment> randomEquipment = new ArrayList<Equipment>();
		
		for(Equipment eq : getAll()) if(tester.test(eq)) randomEquipment.add(eq);
		
		return randomEquipment.get(randomGenerator.nextInt(randomEquipment.size()));
	}
	
	public ArrayList<Equipment> getAll()
	{
		ArrayList<Equipment> allEquipment = new ArrayList<Equipment>();
		
		for(ClassifiedArrayList<Equipment> eq : allEquipmentLists)
			for(int a=0; a<eq.size(); a++)
				allEquipment.add(eq.get(a));
		
		return allEquipment;
	}

	public ArrayList<ClassifiedArrayList<Equipment>> getAllLists() {return allEquipmentLists;}
}
