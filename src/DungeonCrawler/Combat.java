package DungeonCrawler;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class Combat 
{
	private static Character player, opponent;
	private static Console console;
	private static boolean isPlayerTurn;
	private static Random randomGenerator = new Random();
	
	public static void start(Character p, Character o, Console c)
	{
		if(o.getAttribute(Attribute.AttributeEnum.HEALTH).getValue() == 0) return;
		
		player = p; opponent = o; console = c;
		String line; Command command;
		
		console.printf("%nYou encountered a %s%n%n", opponent.getName());
		
		isPlayerTurn = decideInitiative();
		
		while(player.getAttribute(Attribute.AttributeEnum.HEALTH).getValue() > 0 && opponent.getAttribute(Attribute.AttributeEnum.HEALTH).getValue() > 0)
		{
			if(isPlayerTurn)
			{
				line = console.readLine();
				
				if(!line.isEmpty())
				{
					command = findCommand(line);
					String params = findParams(line);
				
					useCommand(command, params);
				}
			}
			else attack(opponent, player);
		}
		
		end();
	}
	private static void end()
	{
		if(player.getAttribute(Attribute.AttributeEnum.HEALTH).getValue() <= 0) 
			console.printf("You were defeated by the %s.%n%n", opponent.getName());
		else if(opponent.getAttribute(Attribute.AttributeEnum.HEALTH).getValue() <= 0)
		{
			player.addAttribute(opponent.getAttribute(Attribute.AttributeEnum.EXPERIENCE));
			LevelSystem.checkLevel(player);
			console.printf("You defeated the %s.%n%n", opponent.getName());
		}
	}
	
	private static boolean decideInitiative()
	{
		int playerLuck = player.getAttribute(Attribute.AttributeEnum.LUCK).getValue();
		int playerDexterity = player.getAttribute(Attribute.AttributeEnum.DEXTERITY).getValue();
		int playerLevel = player.getAttribute(Attribute.AttributeEnum.LEVEL).getValue();
		
		int opponentLuck = opponent.getAttribute(Attribute.AttributeEnum.LUCK).getValue();
		int opponentDexterity = opponent.getAttribute(Attribute.AttributeEnum.DEXTERITY).getValue();
		int opponentLevel = opponent.getAttribute(Attribute.AttributeEnum.LEVEL).getValue();
		
		if (playerLevel > opponentLevel + opponentDexterity) return true;
		else if(playerLevel + playerDexterity > opponentLevel) return true;
		else if(playerLuck + playerDexterity + playerLevel > opponentLuck + opponentDexterity + opponentLevel) return true;
		else if(playerLuck + playerDexterity > opponentLuck + opponentDexterity) return true;
		else if(playerLuck > opponentLuck) return true;
		else return false;
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
		case ATTACK: attack(player, opponent); break;
		case HELP: help(param); break;
		case USE: use(param); break;
		case SHOW: show(param); break;
		case EQUIP: equip(param); break;
		case UNEQUIP: unequip(param); break;
		case QUIT: case EXIT: quit(); break;
		}
	}
	private static void secondTry(Consumer<String> attempt, String prompt)
	{
		console.printf(prompt);
		
		String try2 = console.readLine();
		if(try2.isEmpty()) console.printf("%n");
		else attempt.accept(try2);
	}
	
	private static void attack(Character attacker, Character target)
	{
		int attackerDexLuck = attacker.getAttribute(Attribute.AttributeEnum.DEXTERITY).getValue() + attacker.getAttribute(Attribute.AttributeEnum.LUCK).getValue();
		int targetDexLuck = target.getAttribute(Attribute.AttributeEnum.DEXTERITY).getValue() + target.getAttribute(Attribute.AttributeEnum.LUCK).getValue();
		
		int attack = attacker.getWeaponPower() + attacker.getAttribute(Attribute.AttributeEnum.STRENGTH).getValue()
				- target.getArmorPower() - target.getAttribute(Attribute.AttributeEnum.ENDURANCE).getValue();
		if(attack < 1) attack = 1;
		
		if(randomGenerator.nextInt(attackerDexLuck + 5) > randomGenerator.nextInt(targetDexLuck + 5))
		{
			target.subtractAttribute(Attribute.AttributeEnum.HEALTH, attack);
			if(isPlayerTurn) console.printf("You hit the %s.%n", target.getName());
			else console.printf("You were struck by %s.%n", attacker.getName());
		}
		else
		{
			if(isPlayerTurn) console.printf("You missed the %s.%n", target.getName());
			else console.printf("You were able to dodge the attack.%n");
		}

		cycle();
	}
	
	private static void help(String c)
	{
		if(c.isEmpty()) 
		{
			console.printf("%n");
			for(Command c2 : Command.values()) console.printf(c2.toString() + "%n");
			
			console.printf("%n");
			return;
		}
		
		Command command = Command.fromString(c);
		
		if(command == null) {console.printf("Command not found%n"); return;}
		
		switch(command){
		case ATTACK:
			console.printf("Syntax: attack%n"
					+ "Attacks the opponent.");
			break;
		case HELP:
			console.printf("Syntax: help \"command_name\"%n"
					+ "command_name: the name of the command you wish explained%n"
					+ "Explains a command, kind of like what it's doing right now, incase you didn't realize...%n%n");
			break;
		case USE:
			console.printf("Syntax: use \"item_name/ability_name\" on \"target\"%n"
					+ "item_name: the name of the item you wish to use%n"
					+ "ability_name: the name of the ability you wish to use%n"
					+ "target: the name of your opponent or me or self%n"
					+ "Uses an item or ability that is in your inventory or skill set. Equipment will be equipped or unequipped if it is on your character.%n%n");
			break;
		case SHOW:
			console.printf("Syntax: show \"abilities/equipment/inventory\"%n"
					+ "abilites: shows your abilites."
					+ "equipment: shows items currently equipped%n"
					+ "inventory: shows items not currently equipped%n"
					+ "Shows abilities or items either equipped or still in the backpack.%n%n");
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
		case QUIT: case EXIT:
			console.printf("Syntax: quit/exit%n"
					+ "Ends the game, no paramaters are needed. I suggest saving before you quit, just saying...%n%n");
			break;
		}
	}
	
	private static void quit() {if(console.readLine("Are you sure you wish to quit? [y/n] ").equalsIgnoreCase("Y")) System.exit(0);}
	
	private static void use(String i)
	{
		Character caster = null, target = null;
		
		if(isPlayerTurn) caster = player;
		else caster = opponent;
		
		if(i.isEmpty()) 
		{
			secondTry(try2 -> use(try2), "%nWhat do you want to use? ");
			return;
		}
		
		if(!i.contains(" on "))
		{
			console.printf("Incorrect Syntax.%n");
			return;
		}
		
		String it = i.substring(0, i.indexOf(" on "));
		String t = i.substring(i.indexOf(" on ") + 4);

		ArrayList<String> allOptions = new ArrayList<String>();
		
		allOptions.add("ME");
		allOptions.add("SELF");
		allOptions.add("OPPONENT");
		allOptions.add("CREATURE");
		allOptions.add(opponent.getName());
		
		char[] paramChars = t.toUpperCase().toCharArray();
		for(int a=0;a<paramChars.length;a++)
			for(int b=allOptions.size()-1; b>=0; b--)
			{
				char[] currentCommand = allOptions.get(b).toCharArray();
				if(a >= currentCommand.length || currentCommand[a] != paramChars[a]) allOptions.remove(b);
			}
		
		if(allOptions.size() == 1)
		{
			if(allOptions.get(0).equals(opponent.getName()))
				target = opponent;
			else
				switch(allOptions.get(0))
				{
				case "ME":
				case "SELF": target = player;
					break;
					
				case "OPPONENT":
				case "CREATURE": target = opponent;
					break;
				}
		
			Item toUse = caster.getItem(s -> s.getName().equalsIgnoreCase(it));
			
			if(toUse == null) 
			{
				Ability toUse1 = caster.getAbility(s -> s.getName().equalsIgnoreCase(i));
				
				if(toUse1 == null) console.printf("Could not use: %s%n%n", i);
				else 
				{
					toUse1.use(target);
					console.printf("Used: %s on %s%n%n", i, target);
				}
			}
			else 
			{
				caster.useItem(toUse);
				console.printf("%s%n%n", toUse.onUse());
			}
		}
		else console.printf("Could not use %s on %s", it, t);
		
		cycle();
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
				for(Item i : player.getAllItems()) display += i.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("No items in inventory.%n");
				break;
				
			case "EQUIPMENT":
				for(Equipment e : player.getEquippedItems())
					if(e != null) display += e.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("No items equipped.%n");
				break;
				
			case "ABILITIES":
				for(Ability a : player.getAllAbilities()) display += a.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("No abilities.%n");
				break;
			}
		}
		else console.printf("Incorrect syntax.%n");
		
		console.printf("%n");
	}
	
	private static void equip(String e)
	{
		if(e.isEmpty()) 
		{
			secondTry(try2 -> equip(try2), "%nWhat do you want to equip? ");
			return;
		}
		
		Item toEquip = player.getItem(s -> s.getName().equalsIgnoreCase(e));
		
		if(toEquip == null) console.printf("Could not equip: %s%n%n", e);
		else if(toEquip.getClass() != Equipment.class) console.printf("%s is not a piece of equipment.%n%n", toEquip.getName()); 
		else
		{
			player.equip((Equipment)toEquip);
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
		
		Equipment toUnequip = player.searchEquipment(s -> s.getName().equalsIgnoreCase(e));
		
		if(toUnequip == null) console.printf("Could not unequip: %s%n%n", e);
		else
		{
			player.unequip(toUnequip);
			console.printf("%s%n%n", toUnequip.onUnequip());
		}
	}
	
	private static void cycle()
	{
		player.cycle();
		opponent.cycle();
		isPlayerTurn = !isPlayerTurn;
	}
	
	private enum Command
	{
		ATTACK("ATTACK"), EQUIP("EQUIP"), HELP("HELP"), SHOW("SHOW"), UNEQUIP("UNEQUIP"), USE("USE"), QUIT("QUIT"), EXIT("EXIT");
		
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
