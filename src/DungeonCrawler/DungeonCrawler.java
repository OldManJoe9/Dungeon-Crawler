package DungeonCrawler;

import java.io.Console;
import java.util.ArrayList;
import java.util.function.Consumer;

import Generators.AbilityGen;
import Generators.CharacterGen;
import Generators.ContainerGen;
import Generators.EquipmentGen;
import Generators.ItemGen;
import Generators.SpecialAttributeGen;

public class DungeonCrawler 
{
	private static Console console;
	private static Map map = new Map();
	private static Room currentRoom;
	private static String version = "Version: 0.0.10, Date: February 6, 2017";
	private static Character playerCharacter = new Character("playerCharacter",5,5,5,5,5,5,100,10,0,1);
	
	public static final SpecialAttributeGen specialAttributeGen = new SpecialAttributeGen();
	public static final EquipmentGen equipmentGen = new EquipmentGen();
	public static final ItemGen itemGen = new ItemGen(specialAttributeGen, equipmentGen);
	public static final AbilityGen abilityGen = new AbilityGen(specialAttributeGen);
	public static final CharacterGen characterGen = new CharacterGen(equipmentGen);
	public static final ContainerGen containerGen = new ContainerGen(itemGen);
	
	public static void main(String[] args)
	{
		Command command;
		String line;
		
		console = System.console();
		if(console == null)
		{
			System.err.println("No console.");
            System.exit(1);
        }
		
		map.spawnRoom(0, 0);
		currentRoom = map.getRoom(0, 0);
		
		console.printf("Welcome to this Text-Based Dungeon Crawler.%n"
				+ "%s%n"
				+ "Enjoy! For game help just type \"help\".%n%n", version);
		
		for(;;)
		{
			line = console.readLine();
			
			if(!line.isEmpty())
			{
				command = findCommand(line);
				String params = findParams(line);
			
				useCommand(command, params);
			}
		}
	}

	private static Command findCommand(String s)
	{
		int space = s.indexOf(' ');
		if(space == -1) return Command.fromString(s);
		return Command.fromString(s.substring(0, space));
	}
	private static String findParams(String s)
	{
		int space = s.indexOf(' ');
		if(space == -1) return "";
		return s.substring(space + 1);
	}
	
	private static void useCommand(Command c, String param)
	{
		if(c == null) {console.printf("Command not found%n%n");return;}
		
		switch(c){
		case MOVE: move(param); break;
		case SHOW: show(param); break;
		case DESCRIBE: case WHATIS: describe(param); break;
		case EQUIP: equip(param); break;
		case UNEQUIP: unequip(param); break;
		case USE: use(param); break;
		case OPEN: open(param); break;
		case UNLOCK: unlock(param); break;
		case SEARCH: search(param); break;
		case PICKUP: case TAKE: pickUp(param); break;
		case DROP: case DISCARD: drop(param); break;
		case HELP: help(param); break;
		case QUIT: case EXIT: quit(); break;
		case DEBUG: debug(); break;
		}
	}
	
	private static void move(String d)
	{
		Direction dir = Direction.fromString(d);
		
		if(d.isEmpty()) 
		{
			secondTry(try2 -> move(try2), 
				  "NORTH%n"
				+ "EAST%n"
				+ "SOUTH%n"
				+ "WEST%n%n"
				+ "Move where? ");
			return;
		}
		
		if(dir == null) 
		{
			console.printf("Could not read direction.%n%n");
			return;
		}
		
		if(currentRoom.getDoor(dir).getLockLevel().getLevel() > LockLevel.NONE.getLevel())
		{
			console.printf("That door is locked.%n%n");
			return;
		}
		
		if(currentRoom.getAdjacentRoom(dir) == null) spawnRoom(dir);
		currentRoom = currentRoom.getAdjacentRoom(dir);
		
		cycle();
	}
	private static void spawnRoom(Direction dir)
	{
		int x = currentRoom.getX(), y = currentRoom.getY();
		
		switch(dir){
		case NORTH: case UP:
			y--; break;
		case EAST: case RIGHT:
			x++; break;
		case SOUTH: case DOWN:
			y++; break;
		case WEST: case LEFT:
			x--; break;
		}
		
		map.spawnRoom(x, y);
	}
	
	private static void show(String param)
	{
		ArrayList<String> allOptions = new ArrayList<String>();
		
		allOptions.add("ABILITIES");
		allOptions.add("EQUIPMENT");
		allOptions.add("INVENTORY");
		
		if(param.isEmpty())
		{
			String prompt = "";
			
			for(String s : allOptions) prompt += s + "%n";
			
			prompt += "%nWhat do you want to show? ";
			
			secondTry(try2 -> show(try2), prompt); return;
		}
		
		char[] paramChars = param.toUpperCase().toCharArray();
		for(int a=0;a<paramChars.length;a++)
			for(int b=allOptions.size()-1; b>=0; b--)
			{
				char[] currentCommand = allOptions.get(b).toCharArray();
				if(a >= currentCommand.length || currentCommand[a] != paramChars[a]) allOptions.remove(b);
			}
		
		if(allOptions.size() == 1)
		{
			String display = "";
			switch(allOptions.get(0)){
			case "INVENTORY":
				for(Item i : playerCharacter.getAllItems()) display += i.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("No items in inventory.%n");
				break;
				
			case "EQUIPMENT":
				for(Equipment e : playerCharacter.getEquippedItems())
					if(e != null) display += e.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("No items equipped.%n");
				break;
				
			case "ABILITIES":
				for(Ability a : playerCharacter.getAllAbilities()) display += a.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("No abilities.%n");
				break;
			}
		}
		else console.printf("Incorrect syntax.%n");
		
		console.printf("%n");
	}
	
	private static void describe(String i)
	{
		if(i.isEmpty()) 
		{
			secondTry(try2 -> describe(try2), "%nWhat do you want to describe? ");
			return;
		}
		
		Item toDescribe = playerCharacter.getItem(s -> s.getName().equalsIgnoreCase(i));
		
		if(toDescribe == null) console.printf("Could not describe: %s%n%n", i);
		else console.printf("%s%n%n", toDescribe.getDescription());
	}
	
	private static void equip(String e)
	{
		if(e.isEmpty()) 
		{
			secondTry(try2 -> equip(try2), "%nWhat do you want to equip? ");
			return;
		}
		
		Item toEquip = playerCharacter.getItem(s -> s.getName().equalsIgnoreCase(e));
		
		if(toEquip == null) console.printf("Could not equip: %s%n%n", e);
		else if(toEquip.getClass() != Equipment.class) console.printf("%s is not a piece of equipment.%n%n", toEquip.getName()); 
		else
		{
			playerCharacter.equip((Equipment)toEquip);
			console.printf("%s%n%n", toEquip.onUse());
		}
	}

	private static void unequip(String e)
	{
		if(e.isEmpty()) 
		{
			secondTry(try2 -> unequip(try2), "%nWhat do you want to unequip? ");
			return;
		}
		
		Equipment toUnequip = playerCharacter.searchEquipment(s -> s.getName().equalsIgnoreCase(e));
		
		if(toUnequip == null) console.printf("Could not unequip: %s%n%n", e);
		else
		{
			playerCharacter.unequip(toUnequip);
			console.printf("%s%n%n", toUnequip.onUnequip());
		}
	}
	
	private static void use(String i)
	{
		if(i.isEmpty()) 
		{
			secondTry(try2 -> use(try2), "%nWhat do you want to use? ");
			return;
		}
		
		Item toUse = playerCharacter.getItem(s -> s.getName().equalsIgnoreCase(i));
		
		if(toUse == null) 
		{
			Ability toUse2 = playerCharacter.getAbility(s -> s.getName().equalsIgnoreCase(i));
			
			if(toUse2 == null) console.printf("Could not use: %s%n%n", i);
			else {
				toUse2.use(playerCharacter);
				console.printf("Used: %s on %s%n%n", i, playerCharacter);
			}
		}
		else {
			playerCharacter.useItem(toUse);
			console.printf("%s%n%n", toUse.onUse());
		}
	}
	
	private static void open(String c) {
		if (c.isEmpty()) {
			secondTry(try2 -> unlock(try2), "%nWhat do you want to open? ");
			return;
		}
		
		if (c.contains("all")) unlock(c);
		
		ArrayList<Unlockable> unlockables = currentRoom.getUnlockables();
		
		for(Unlockable u : unlockables) {
			if(u.getClass() == Container.class) {
				String con = u.getName() + ((Container)u).getIdentifier();
				if(con.equalsIgnoreCase(c)) {
					unlock(c); search(c); break;
				}
			}
			else if(u.getClass() == Door.class) {
				String con = Direction.getOpposite(((Door)u).getMySide(currentRoom)) + " Door";
				if(con.equalsIgnoreCase(c)) {
					unlock(c); break;
				}
			}
			else
				if(u.getName().equalsIgnoreCase(c)) {
					unlock(c); break;
				}
		}
	}
	
	private static void unlock(String c) {
		if(c.isEmpty()) {
			secondTry(try2 -> unlock(try2), "%nWhat do you want to unlock? ");
			return;
		}

		ArrayList<Unlockable> unlockables = currentRoom.getUnlockables();
			
		if(c.equalsIgnoreCase("all")) 
			if(unlockables.isEmpty()) console.printf("There is nothing to unlock...%n%n");
			else console.printf(Action.unlockAll(unlockables, playerCharacter.getAllKeysFromInventory(), playerCharacter));
		else {
			if(c.contains("all ")) {
				String c2 = c.substring(c.indexOf(" ") + 1);
				ArrayList<Unlockable> unlockables2 = new ArrayList<Unlockable>();
				
				for(Unlockable u : unlockables)	
					if(u.getName().equalsIgnoreCase(c2)) 
						unlockables2.add(u);
				
				if(unlockables2.isEmpty()) console.printf("Could not unlock all: %s%n%n", c2);
				else console.printf(Action.unlockAll(unlockables2, playerCharacter.getAllKeysFromInventory(), playerCharacter));
			}
			else
			{
				boolean unlocked = false;
				for(Unlockable u : unlockables)
					if(u.getClass() == Container.class)
					{
						String con = u.getName() + ((Container)u).getIdentifier();
						if(con.equalsIgnoreCase(c)) 
						{
							console.printf(Action.unlock(u, playerCharacter.getKeyFromInventory(u.getLockLevel()), playerCharacter) + "%n");
							unlocked = true; break;
						}
					}
					else if(u.getClass() == Door.class)
					{
						String con = Direction.getOpposite(((Door)u).getMySide(currentRoom)) + " Door";
						if(con.equalsIgnoreCase(c)) 
						{
							console.printf(Action.unlock(u, playerCharacter.getKeyFromInventory(u.getLockLevel()), playerCharacter) + "%n");
							unlocked = true; break;
						}
					}
					else
						if(u.getName().equalsIgnoreCase(c)) 
						{
							console.printf(Action.unlock(u, playerCharacter.getKeyFromInventory(u.getLockLevel()), playerCharacter) + "%n");
							unlocked = true; break;
						}
				
				if(!unlocked) console.printf("Could not unlock: %s%n%n", c);
			}
		}
	}
	
	private static void search(String param) 
	{
		ArrayList<Item> items = null;
		boolean found = false;
		
		if(param.isEmpty()) console.printf(currentRoom.search());
		else
		{
			for(Unlockable con : currentRoom.getUnlockables())
				if(con.getClass() == Container.class)
				{
					String con1 = con.getName() + ((Container)con).getIdentifier();
					if(con1.equalsIgnoreCase(param))
					{
						items = ((Container)con).search();
						found = true;
						break;
					}
				}
			
			if(!found) console.printf("Could not search: %s%n%n", param);
			else if(items == null) console.printf("Container is locked!%n%n");
			else if(items.isEmpty()) console.printf("It is empty...%n%n");
			else
			{
				for(Item i : items) console.printf("%s%n", i.getName());
				console.printf("%n");
			}
			
		}
	}
	
	private static void pickUp(String i)
	{
		if(i.isEmpty())
		{
			secondTry(try2 -> pickUp(try2), "%nWhat do you want to pick up? ");
			return;
		}
		
		if(i.equalsIgnoreCase("all"))
		{
			ArrayList<Item> items = currentRoom.getAllItems();
			if(items.isEmpty()) console.printf("There is nothing to pick up...%n%n");
			else console.printf(Action.transferAll(currentRoom, playerCharacter, items, "Picked up: "));
		}
		else
		{
			if(i.contains(" from "))
			{
				String it = i.substring(0, i.indexOf(" from "));
				String con = i.substring(i.indexOf(" from ") + 6);
				Container container = null;
				Item item = null;
				ArrayList<Item> items = null;
				
				for(Unlockable u : currentRoom.getUnlockables())
					if(u.getClass() == Container.class)
					{
						String con1 = u.getName() + ((Container)u).getIdentifier();
						if(con1.equalsIgnoreCase(con)) {container = (Container)u; break;}
					}
				
				if(container == null) console.printf("Could not pick up from: %s%n%n", con);
				else 
				{
					if(it.equalsIgnoreCase("all"))
					{
						items = container.getAllItems();
						if(items.isEmpty()) console.printf("There is nothing to pick up...%n%n");
						else console.printf(Action.transferAll(container, playerCharacter, items, "Took: "));
					}
					else if(it.contains("all ")) 
					{
						String i2 = it.substring(it.indexOf("all ") + 4);
						items = container.getAllItems(item2 -> item2.getName().equalsIgnoreCase(i2));

						if(items.isEmpty()) console.printf("Could not pick up all: %s%n%n", i2);
						else console.printf(Action.transferAll(container, playerCharacter, items, "Picked up: "));
					}
					else
					{
						if((item = container.getItem(it2 -> it2.getName().equalsIgnoreCase(it))) == null) 
							console.printf("Could not pick up: %s%n%n", it);
						else console.printf(Action.transfer(container, playerCharacter, item, "Took: ") + "%n");
					}
				}
			}
			else if(i.contains("all ")) 
			{
				String i2 = i.substring(i.indexOf("all ") + 4);
				ArrayList<Item> items2 = currentRoom.getAllItems(item -> item.getName().equalsIgnoreCase(i2));

				if(items2.isEmpty()) console.printf("Could not pick up all: %s%n%n", i2);
				else console.printf(Action.transferAll(currentRoom, playerCharacter, items2, "Picked up: "));
			}
			else
			{
				Item it = currentRoom.getItem(item -> item.getName().equalsIgnoreCase(i));
				
				if(it == null) console.printf("Could not pick up: %s%n%n", i);
				else console.printf(Action.transfer(currentRoom, playerCharacter, it, "Picked up: ") + "%n");
			}
		}
	}
	
	private static void drop(String i)
	{
		if(i.isEmpty())
		{
			secondTry(try2 -> drop(try2), "%nDrop what? ");
			return;
		}
		
		Item toDrop = null;
		
		if(i.contains("all")) 
		{
			String i2 = i.substring(i.indexOf(" ") + 1);
			boolean called = false;
			
			while((toDrop = playerCharacter.getItem(s -> s.getName().equalsIgnoreCase(i2))) != null)
			{
				currentRoom.giveItem(toDrop);
				playerCharacter.removeItem(toDrop);
				console.printf("Dropped: %s%n", toDrop.getName());
				called = true;
			}
			
			if(!called) console.printf("Could not drop all: %s%n", i2);
			console.printf("%n");
		}
		else
		{
			toDrop = playerCharacter.getItem(s -> s.getName().equalsIgnoreCase(i));
			
			if(toDrop == null) console.printf("Could not drop: %s%n%n", i);
			else 
			{
				currentRoom.giveItem(toDrop);
				playerCharacter.removeItem(toDrop);
				console.printf("Dropped: %s%n%n", toDrop.getName());
			}
		}
	}
	
	private static void help(String c)
	{
		if(c.isEmpty()) 
		{
			console.printf("%n");
			for(Command c2 : Command.values()) if(c2 != Command.DEBUG) console.printf(c2.toString() + "%n");
			
			console.printf("%n");
			return;
		}
		
		Command command = Command.fromString(c);
		
		if(command == null) {console.printf("Command not found%n"); return;}
		
		switch(command){
		case MOVE:
			console.printf("Syntax: move \"direction\"%n"
					+ "direction: north, south, east, or west%n"
					+ "Moves your character in the specified direction.%n%n");
			break;
		case SHOW:
			console.printf("Syntax: show \"abilities/equipment/inventory\"%n"
					+ "abilites: shows your abilites."
					+ "equipment: shows items currently equipped%n"
					+ "inventory: shows items not currently equipped%n"
					+ "Shows abilities or items either equipped or still in the backpack.%n%n");
			break;
		case DESCRIBE: case WHATIS:
			console.printf("Syntax: describe/whatis \"item_name\"%n"
					+ "item_name: the item you wish described%n"
					+ "Describes an item.%n%n");
			break;
		case EQUIP:
			console.printf("Syntax: equip \"item_name\"%n"
					+ "item_name: the name of the item you wish to equip%n"
					+ "Equips an item that is in your inventory.%n%n");
			break;
		case UNEQUIP:
			console.printf( "Syntax: unequip \"item_name\"%n"
					+ "item_name: the name of the item you wish to unequip%n"
					+ "Unquips an item that is on your character.%n%n");
			break;
		case USE:
			console.printf("Syntax: use \"item_name/ability_name\"%n"
					+ "item_name: the name of the item you wish to use%n"
					+ "ability_name: the name of the ability you wish to use%n"
					+ "Uses an item or ability that is in your inventory or skill set. Equipment will be equipped or unequipped if it is on your character.%n%n");
			break;
		case OPEN:
			console.printf("Syntax: use \"object_name\"%n"
					+ "if using the word \"all\" will unlock all objects or all specified objects%n"
					+ "object_name: the name of the container, door, or other unlockable and/or searchable object that you wish to unlock and/or search%n"
					+ "Unlockes an unlockable object and then searches it if it is searchable.%n%n");
			break;
		case UNLOCK:
			console.printf("Syntax: unlock [all] \"object_name\"%n"
					+ "if using the word \"all\" will unlock all objects or all specified objects%n"
					+ "object_name: the name of the container, door, or other unlockable object that you wish to unlock%n"
					+ "Unlocks a container, door, or other unlockable object.%n%n");
			break;
		case SEARCH:
			console.printf("Syntax: search \"object_name\"%n"
					+ "object_name: the name of the object you wish to search, leave blank to search the room.%n"
					+ "Searches the area or room you are in for items.%n%n");
			break;
		case PICKUP: case TAKE:
			console.printf("Syntax: pickup/take [all] \"item_name\" [from \"object_name\"]%n"
					+ "if using the word \"all\" will pick up all items or all specified items can be used with from \"object_name\"%n"
					+ "item_name: the name of the item you wish to pick up%n"
					+ "to take an item from a container use the word from and then the object_name%n"
					+ "Picks up an item.%n%n");
			break;
		case DROP: case DISCARD:
			console.printf("Syntax: drop/discard \"item_name\"%n"
					+ "item_name: the name of the item you wish to drop%n"
					+ "Drops or Discards an unwanted item.%n%n");
			break;
		case HELP:
			console.printf("Syntax: help \"command_name\"%n"
					+ "command_name: the name of the command you wish explained%n"
					+ "Explains a command, kind of like what it's doing right now, incase you didn't realize...%n%n");
			break;
		case QUIT: case EXIT:
			console.printf("Syntax: quit/exit%n"
					+ "Ends the game, no paramaters are needed. I suggest saving before you quit, just saying...%n%n");
			break;
		case DEBUG:
			console.printf("Syntax: debug%n"
					+ "Runs the debug command%n%n");
			break;
		}
	}
	
	private static void quit() {
		if(console.readLine("Are you sure you wish to quit? [y/n] ").equalsIgnoreCase("Y")) System.exit(0);
	}
	
	private static void debug()
	{
		console.printf("%s%n", currentRoom.getDoor(Direction.NORTH));
		try
		{
			console.printf("%s%n", currentRoom.getAdjacentRoom(Direction.NORTH).getDoor(Direction.SOUTH));
			console.printf("%s%n%n", currentRoom.getDoor(Direction.NORTH) == currentRoom.getAdjacentRoom(Direction.NORTH).getDoor(Direction.SOUTH));
		}
		catch(NullPointerException e) {console.printf("%n");}
		
		console.printf("%s%n", currentRoom.getDoor(Direction.EAST));
		try
		{
			console.printf("%s%n", currentRoom.getAdjacentRoom(Direction.EAST).getDoor(Direction.WEST));
			console.printf("%s%n%n", currentRoom.getDoor(Direction.EAST) == currentRoom.getAdjacentRoom(Direction.EAST).getDoor(Direction.WEST));
		}
		catch(NullPointerException e) {console.printf("%n");}
		
		console.printf("%s%n", currentRoom.getDoor(Direction.SOUTH));
		try
		{
			console.printf("%s%n", currentRoom.getAdjacentRoom(Direction.SOUTH).getDoor(Direction.NORTH));
			console.printf("%s%n%n", currentRoom.getDoor(Direction.SOUTH) == currentRoom.getAdjacentRoom(Direction.SOUTH).getDoor(Direction.NORTH));
		}
		catch(NullPointerException e) {console.printf("%n");}
		
		console.printf("%s%n", currentRoom.getDoor(Direction.WEST));
		try
		{
			console.printf("%s%n", currentRoom.getAdjacentRoom(Direction.WEST).getDoor(Direction.EAST));
			console.printf("%s%n%n", currentRoom.getDoor(Direction.WEST) == currentRoom.getAdjacentRoom(Direction.WEST).getDoor(Direction.EAST));
		}
		catch(NullPointerException e) {console.printf("%n");}
	}
	
	private static void cycle()
	{
		Character creature = currentRoom.getCreature();
		
		playerCharacter.cycle();
		if(creature != null)
			if(!creature.getName().equals("Merchant")) Combat.start(playerCharacter, creature, console);
			else Merchant.trade(itemGen, console, playerCharacter, creature);
	}
	
	private static void secondTry(Consumer<String> attempt, String prompt)
	{
		console.printf(prompt);
		
		String try2 = console.readLine();
		if(try2.isEmpty()) console.printf("%n");
		else attempt.accept(try2);
	}
	
	private enum Command
	{
		MOVE("MOVE"), SHOW("SHOW"), DESCRIBE("DESCRIBE"), WHATIS("WHATIS"), EQUIP("EQUIP"), UNEQUIP("UNEQUIP"),
		USE("USE"), OPEN("OPEN"), UNLOCK("UNLOCK"), SEARCH("SEARCH"), PICKUP("PICKUP"), TAKE("TAKE"), DROP("DROP"), DISCARD("DISCARD"),
		HELP("HELP"), QUIT("QUIT"), EXIT("EXIT"), DEBUG("DEBUG");
		
		private char[] commandString;
		private Command(String sc) {commandString=sc.toCharArray();}
		
		static Command fromString(String s)
		{
			char[] sChars = s.toUpperCase().toCharArray();
			ArrayList<Command> allCommands = new ArrayList<Command>();
			
			for(Command c : Command.values()) allCommands.add(c);
			
			for(int a=0;a<sChars.length;a++)
				for(int b=allCommands.size()-1; b>=0; b--)
				{
					char[] currentCommand = allCommands.get(b).commandString;
					if(a >= currentCommand.length || currentCommand[a] != sChars[a]) allCommands.remove(b);
				}
			
			if(allCommands.size() == 1) return allCommands.get(0);
			else return null;
		}
	}
}