package DungeonCrawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Predicate;

/*
 * All characters in the game.
 * 
 * Attributes
 * 		Strength, Dexterity,
 * 		Wisdom, Intelligence,
 * 		Endurance, Luck,
 * 		Health, MaxHealth, 
 * 		Mana, MaxMana,
 * 		experience, level,
 * 
 * Inventory List - all items
 * Weapons List - Equipped Weapons
 * Armor List - Equipped Armor
 * Abilities List - All Abilities
 * Special Attributes List - special characteristics such as stop, poison, sleep, invincible, immune""...
 */

public class Character implements ItemCarrier
{
	private Random randomGenerator = new Random();
	private String name;
	//strength, dexterity, wisdom, intelligence, endurance, luck, health, maxHealth, mana, maxMana, experience, level
	public static final Attribute.AttributeEnum[] ATTRIBUTES = Attribute.AttributeEnum.values(); 
	private Attribute[] attributeList = new Attribute[ATTRIBUTES.length];
	
	private ArrayList<Item> inventory = new ArrayList<Item>();
	private ArrayList<Ability> abilities = new ArrayList<Ability>();
	//Weapon, Head, Body, Legs, Hands, Feet, Shield, Necklace, Ring
	private Equipment[] equippedItems = new Equipment[9];
	private ArrayList<SpecialAttribute> specialAttributes = new ArrayList<SpecialAttribute>();
	
	public Character(Character copy)
	{
		name = copy.getName();
		
		for(int a=0;a<attributeList.length;a++)
			attributeList[a] = new Attribute(ATTRIBUTES[a], copy.getAllAttributes()[a].getValue());
		
		equippedItems = copy.getEquippedItems();
	}
	public Character(String n, int s, int d, int w, int i, int e, int l, int h, int m, int ex, int lv)
	{
		name=n;
		
		int[] atts = {s,d,w,i,e,l,h,h,m,m,ex,lv};
		
		for(int a=0;a<attributeList.length;a++)
			attributeList[a] = new Attribute(ATTRIBUTES[a], atts[a]);
		
		Arrays.fill(equippedItems, null);
	}
	public Character(String n) {this(n, 0,0,0,0,0,0,1,0,0,0);}
	public Character(String n, int[] atts, Equipment[] equipment)
	{
		name=n;
		int h = 0; int m = 0;
		
		for(int a=0; a<atts.length; a++)
		{
			attributeList[a+h+m] = new Attribute(ATTRIBUTES[a+h+m], atts[a]);
			if(a==6) {h=1; attributeList[a+h+m] = new Attribute(ATTRIBUTES[a+h+m], atts[a]);}
			if(a==7) {m=1; attributeList[a+h+m] = new Attribute(ATTRIBUTES[a+h+m], atts[a]);}
		}
		
		for(Equipment e : equipment)
			equip(e);
	}
	
	public String getName() {return name;}
	public void setName(String n) {name=n;}
	
	public Attribute getAttribute(Attribute.AttributeEnum a)
	{
		for(Attribute b : attributeList) if(b.getName().equals(a)) return b;
		
		return null;
	}
	public void addAttribute(Attribute.AttributeEnum a, int v) {for(Attribute b : attributeList) if(b.getName().equals(a)) {b.addValue(v);return;}}
	public void addAttribute(Attribute a) {for(Attribute b : attributeList) if(b.getName().equals(a.getName())) {b.addValue(a);return;}}
	public void subtractAttribute(Attribute.AttributeEnum a, int v) {for(Attribute b : attributeList) if(b.getName().equals(a)) {b.subtractValue(v);return;}}
	public void subtractAttribute(Attribute a) {for(Attribute b : attributeList) if(b.getName().equals(a.getName())) {b.subtractValue(a);return;}}
	public Attribute[] getAllAttributes() {return attributeList;}
	
	public ArrayList<Item> getAllItems() {return inventory;}
	public void useItem(Item i) 
	{
		if(i.getClass() == Equipment.class)
			equip((Equipment)i);
		
		i.use(this);
	}
	public void giveItem(Item i) {inventory.add(i);}
	public void removeItem(Item i)
	{
		for(int a=0; a < inventory.size(); a++) 
			if(inventory.get(a).equals(i)) {inventory.remove(a); return;}
	}
	public Item getItem(Item i) {return getItem(i2 -> i2.equals(i));}
	public Item getItem(Predicate<Item> tester)
	{
		ArrayList<Item> itemList = new ArrayList<Item>();
		
		for(Item i : inventory)
			if(tester.test(i)) itemList.add(i);
		
		if(itemList.size() == 1) return itemList.get(0);
		else if(itemList.size() == 0) return null;
		else
			return itemList.get(randomGenerator.nextInt(itemList.size()));
	}
	public Key getKeyFromInventory(Key.LockLevel lockLevel)
	{
		Key key = null;
		
		do
		{
			for(Item it : getAllItems()) if(it.getClass() == Key.class && ((Key)it).getLockLevel() == lockLevel) {key = (Key)it; break;}
			
			if(key == null)
			{
				lockLevel = Key.LockLevel.getNextLevel(lockLevel);
				if(lockLevel == null) break;
			}
		} while(key == null);
		
		return key;
	}
	public ArrayList<Key> getAllKeysFromInventory()
	{
		ArrayList<Key> keys = new ArrayList<Key>();
		
		for(Item i : getAllItems()) if(i.getClass() == Key.class) keys.add((Key)i);
		
		return keys;
	}

	public ArrayList<Ability> getAllAbilities() {return abilities;}
	public Ability getAbility(Predicate<Ability> tester)
	{
		for(Ability a : getAllAbilities())
			if(tester.test(a)) return a;
		
		return null;
	}
	public void addAbility(Ability a) {abilities.add(a);}
	public void removeAbility(String a)
	{
		for(int b=0; b<abilities.size(); b++)
			if(abilities.get(b).getName().equalsIgnoreCase(a))
			{
				abilities.remove(b);
				return;
			}
	}
	
	public Equipment[] getEquippedItems() {return equippedItems;}
	public void equip(Equipment e)
	{
		Equipment.EquipmentSlot es = e.getEquipmentSlot();
		
		int sn = es.getSlotNumber();
		
		if(equippedItems[sn] != null) unequip(equippedItems[sn]);
		
		e.equip(this);
		equippedItems[sn] = e;
	}
	public void unequip(Equipment e)
	{
		int sn = e.getEquipmentSlot().getSlotNumber();
		
		if(equippedItems[sn].equals(e))
		{
			e.unequip(this);
			equippedItems[sn] = null;
		}
	}
	public Equipment getEquippedWeapon() {return equippedItems[0];}
	public ArrayList<Equipment> getEquippedArmor()
	{
		ArrayList<Equipment> armor = new ArrayList<Equipment>();
		
		for(Equipment e : equippedItems) if(e != null && e.isArmor()) armor.add(e);
		
		return armor;
	}
	public Equipment searchEquipment(Predicate<Equipment> tester)
	{
		Equipment[] equipment = getEquippedItems();
		
		for(Equipment eq : equipment)
			if(eq != null && tester.test(eq)) return eq;
		
		return null;
	}
	public int getWeaponPower() 
	{
		if(equippedItems[0] != null) return equippedItems[0].getEquipmentPower();
		else return 0;
	}
	public int getArmorPower()
	{
		int power=0;
		for(Equipment e : getEquippedArmor()) power+=e.getEquipmentPower();
		return power;
	}
	
	public ArrayList<SpecialAttribute> getSpecialAttributes() {return specialAttributes;}
	public void addSpecialAttribute(SpecialAttribute sa) {specialAttributes.add(sa);}
	public void removeSpecialAttribute(SpecialAttribute sa)
	{
		for(int b=0; b < specialAttributes.size(); b++)
			if(specialAttributes.get(b).equals(sa))
			{
				specialAttributes.remove(b);
				return;
			}
	}

	public void cycle()
	{
		for(int a = specialAttributes.size() - 1; a > -1; a--)
		{
			specialAttributes.get(a).cycle(this);
		}
	}
}