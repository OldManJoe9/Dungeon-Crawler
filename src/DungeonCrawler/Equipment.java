package DungeonCrawler;
import java.util.ArrayList;

public class Equipment extends Item 
{
	private Attribute[] attributes;
	private boolean isWeapon;
	private EquipmentSlot equipmentSlot;
	private int equipmentPower;
	private String onUnequip;
	
	public Equipment(String n, int v, Attribute[] a, boolean isP, String d, String oe, int ep, boolean isW, EquipmentSlot e, String ou, ClassifiedArrayList.Classification c)
	{
		super(n,null,v,isP,d,oe,c);
		attributes=a; equipmentPower=ep; isWeapon=isW; equipmentSlot=e; onUnequip=ou;
	}
	
	public void use(Character c) {}
	public void equip(Character c) 
	{
		for(Attribute a : attributes) c.addAttribute(a);
		
		c.removeItem(this);
	}
	public void unequip(Character c) 
	{
		for(Attribute a : attributes) c.subtractAttribute(a);
	
		c.giveItem(this);
	}

	public int getEquipmentPower() {return equipmentPower;}
	public boolean isWeapon() {return isWeapon;}
	public boolean isArmor() {return !isWeapon;}
	public String onUnequip() {return onUnequip;}
	public EquipmentSlot getEquipmentSlot() {return equipmentSlot;}
	
	public enum EquipmentSlot 
	{
		WEAPON("WEAPON", 0), HEAD("HEAD", 1), BODY("BODY", 2), LEGS("LEGS", 3), HANDS("HANDS", 4),
		FEET("FEET", 5), SHEILD("SHEILD", 6), NECKLACE("NECKLACE", 7), RING("RING", 8);
		
		private char[] equipmentSlotString;
		private int slotNumber;
		private EquipmentSlot(String s, int sn) {equipmentSlotString=s.toCharArray(); slotNumber=sn;}
		
		public static EquipmentSlot fromString(String s)
		{
			char[] sChars = s.toUpperCase().toCharArray();
			ArrayList<EquipmentSlot> allEquipmentSlots = new ArrayList<EquipmentSlot>();
			
			for(EquipmentSlot c : EquipmentSlot.values()) allEquipmentSlots.add(c);
			
			for(int a=0;a<sChars.length;a++)
				for(int b=allEquipmentSlots.size()-1; b>=0; b--)
				{
					char[] currentCommand = allEquipmentSlots.get(b).equipmentSlotString;
					if(a >= currentCommand.length || currentCommand[a] != sChars[a]) allEquipmentSlots.remove(b);
				}
			
			if(allEquipmentSlots.size() == 1) return allEquipmentSlots.get(0);
			else return null;
		}
		
		public int getSlotNumber() {return slotNumber;}
	}
}