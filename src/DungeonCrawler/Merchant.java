package DungeonCrawler;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

import Generators.ItemGen;

public class Merchant
{
	private static ItemGen itemGen;
	private static Console console;
	private static Character player, merchant;
	private static Random randomGenerator = new Random();
	private static final Item nothing = new Item("Nothing", null, 0, false, "Nothing", "Nothing", ClassifiedArrayList.Classification.COMMON);
	
	public static void trade(ItemGen ig, Console c, Character p, Character m)
	{
		itemGen = ig; console = c; player = p; merchant = m;
		if(merchant.getAllItems().size() == 0) getShopInventory();
		String line; Command command;
		displayInventory();
		
		for(;;)
		{
			line = console.readLine();
			
			if(!line.isEmpty())
			{
				command = findCommand(line);
				String params = findParams(line);
			
				useCommand(command, params);
				if(command == Command.LEAVE) break;
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
		case BUY: buy(param); break;
		case HELP: help(param); break;
		case LEAVE: leave(); break;
		case SELL: sell(param); break;
		case SHOW: show(param); break;
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
	
	private static void buy(String i)
	{
		if(i.isEmpty()) 
		{
			secondTry(try2 -> buy(try2), "%nWhat do you want to buy? ");
			return;
		}
		
		Item item = merchant.getItem(i2 -> i2.getName().equalsIgnoreCase(i));
		if(item != null) console.printf(Action.transfer(merchant, player, item, "You purchased: ") + "%n");
		else console.printf("This Merchant does not have a(n) %s%n", i);
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
		case BUY:
			console.printf("Syntax: buy \"item_name\"%n"
					+ "item_name: the name of the item you wish to purchase%n"
					+ "Purchases an item from the merchant.%n%n");
			break;
		case HELP:
			console.printf("Syntax: help \"command_name\"%n"
					+ "command_name: the name of the command you wish explained%n"
					+ "Explains a command, kind of like what it's doing right now, incase you didn't realize...%n%n");
			break;
		case LEAVE:
			console.printf("Syntax: leave%n"
					+ "Leaves the merchant.%n%n");
			break;
		case SELL:
			console.printf("Syntax: sell \"item_name\"%n"
					+ "item_name: the name of hte item you wish to sell%n"
					+ "Sells an item from your backpack.%n%n");
			break;
		case SHOW:
			console.printf("Syntax: show \"shop/backpack\"%n"
					+ "shop: shows the inventory left in the shop.%n"
					+ "backpack: shows items in your backpack.%n"
					+ "Displays either your inventory or that of the merchant.%n%n");
			break;
		case QUIT: case EXIT:
			console.printf("Syntax: quit/exit%n"
					+ "Ends the game, no paramaters are needed. I suggest saving before you quit, just saying...%n%n");
			break;
		}
	}
	
	private static void leave() {if(merchant.getAllItems().size() == 0) merchant.giveItem(nothing);}
	
	private static void quit() {if(console.readLine("Are you sure you wish to quit? [y/n] ").equalsIgnoreCase("Y")) System.exit(0);}
	
	private static void sell(String i)
	{
		if(i.isEmpty()) 
		{
			secondTry(try2 -> sell(try2), "%nWhat do you want to sell? ");
			return;
		}
		
		Item item = player.getItem(i2 -> i2.getName().equalsIgnoreCase(i));
		if(item != null)
		{
			if(!item.isPriceless() || console.readLine("Are you sure you want to sell this priceless item? (Y|N) ").equalsIgnoreCase("Y"))
				console.printf(Action.transfer(player, merchant, item, "You sold: ") + "%n");
			
			merchant.removeItem(nothing);
		}
		else console.printf("You do not have a(n) %s%n", i);
	}

	private static void show(String param)
	{
		ArrayList<String> allOptions = new ArrayList<String>();
		
		allOptions.add("SHOP");
		allOptions.add("BACKPACK");
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
			case "BACKPACK":
				for(Item i : player.getAllItems()) display += i.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("No items in backpack.%n");
				break;
				
			case "SHOP":
				for(Item i : merchant.getAllItems()) if(i != null) display += i.getName() + "%n";
				
				if(!display.isEmpty()) console.printf(display);
				else console.printf("This merchant doesn't have anything for sale.%n");
				break;
			}
		}
		else console.printf("Incorrect syntax.%n");
		
		console.printf("%n");
	}
	
	private static void getShopInventory() 
	{
		ArrayList<Item> allItems = getAllItemsInClass();
		int shopSize = randomGenerator.nextInt(3 * allItems.get(0).getClassification().getMultiplier() + 1); //max size of any shop is 3 * classification mutiplier
		
		for(int a=0;a<shopSize;a++)
			merchant.giveItem(allItems.get(randomGenerator.nextInt(allItems.size())));
	}
	private static ArrayList<Item> getAllItemsInClass()
	{
		ClassifiedArrayList.Classification[] classes = ClassifiedArrayList.Classification.values();
		ArrayList<ClassifiedArrayList.Classification> classes2 = new ArrayList<ClassifiedArrayList.Classification>();
		
		for(ClassifiedArrayList.Classification c : classes)
			for(int a=0;a<c.getMultiplier();a++)
				classes2.add(c);
		
		return itemGen.getAll(classes2.get(randomGenerator.nextInt(classes2.size())));
	}

	private static void displayInventory()
	{
		console.printf("%nYou have encountered a Merchant.%n"
				+ "You may buy stuff, if you have the gold.%n"
				+ "Or sell unnecessary items.%n%n");
		
		if(merchant.getItem(nothing) == null)
		{
			for(Item i : merchant.getAllItems()) console.printf("%s%n", i.getName());
			
			console.printf("%nType \"Buy\" and the name of the item you want to buy.%n");
		}
		else console.printf("This merchant doesn't have anything for sale.%n%n");
		
		console.printf("Type \"Sell\" and the name of the item you wish to sell.%n"
				+ "And type \"Leave\" when you are done%n%n");
	}

	private enum Command
	{
		BUY("BUY"), EXIT("EXIT"), HELP("HELP"), LEAVE("LEAVE"), QUIT("QUIT"), SELL("SELL"), SHOW("SHOW");
		
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
