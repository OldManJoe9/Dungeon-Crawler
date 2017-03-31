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
import DungeonCrawler.ClassifiedArrayList;
import DungeonCrawler.ClassifiedArrayList.Classification;

public class EquipmentCreator
{
	private static Console console;
	private static Path file = Paths.get("../data/Equipment.txt");
	@SuppressWarnings("unchecked")
	private static ClassifiedArrayList<String>[] classifiedArrayLists = new ClassifiedArrayList[5];
	
	public static void main(String[] args) 
	{
		String name, value, atts, priceless, desc, onUse, equipmentPower, isWeapon, equipmentSlot, onUnequip;
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
			console.printf("You are about to forge a new piece of equipment%n"
					+ "What will you name it?%n");
			name = console.readLine();
			
			console.printf("%nWhat shall it be classified as?%n"
					+ "COMMON, UNCOMMON, RARE, SUPERRARE, or ULTRARARE?%n");
			classification = Classification.fromString(console.readLine());
			
			console.printf("%nHow much is this worth?%n");
			value = console.readLine();
			
			console.printf("%nWhat attributes does it affect and by how much?%n"
					+ "Separate the attribute from it's stat change by a comma and each attribute by a semicolon%n"
					+ "Example: STRENGTH, 1; ENDURANCE, 1");
			atts = console.readLine() + ";";
			if(atts.equals(";")) atts = "";
			
			console.printf("%nIs this equipment priceless? (true|false)%n"
					+ "Meaning should you be warned before selling it?%n");
			priceless = console.readLine();
			
			console.printf("%nHow should this equipment be described?%n");
			desc = console.readLine();
			
			console.printf("%nWhat should be said when equipping this equipment?%n");
			onUse = console.readLine();
			
			console.printf("%nHow strong is this piece of equipment you are equipping?%n");
			equipmentPower = console.readLine();
			
			console.printf("%nAre you going to use this piece of equipment to smash, slash, pummel,"
					+ " impale, crush, stab, shoot, blast, cut, burn, smite, or otherwise injure your foes? (true|false)%n");
			isWeapon = console.readLine();
			
			console.printf("%nHow should this piece of equipment be wielded?%n"
					+ "On your as a WEAPON, on your HEAD, BODY, LEGS, HANDS, FEET, as a SHEILD, NECKLACE, or RING?%n");
			equipmentSlot = console.readLine();
			
			console.printf("%nWhat should be said when removing this piece of equipment?%n");
			onUnequip = console.readLine();
			
			//Weapon1, 0, {STRENGTH, 1; ENDURANCE, 1;}, false, Weapon1 of Debugging, Slash1 through game bugs!, 
			//1, true, WEAPON, You can't slash game bugs without a weapon1!!
			for(ClassifiedArrayList<String> cal : classifiedArrayLists)
				if(cal.getClassification() == classification) {cal.add(name + ", " + value + ", {" + atts + ";}, "
						+ "" + priceless + ", " + desc + ", " + onUse + ", " + equipmentPower + ", " + isWeapon + ", "
						+ equipmentSlot + ", " + onUnequip + "\n"); break;}
			
			console.printf("Are you done making equipment? (Y|N)%n");
			if(console.readLine().equalsIgnoreCase("Y")) break;
		}
		
		sortEquipment();
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

	private static void sortEquipment()
	{
		for(ClassifiedArrayList<String> cal : classifiedArrayLists)
		{
			cal.sort((a,b) -> a.compareTo(b));
		}
	}
}