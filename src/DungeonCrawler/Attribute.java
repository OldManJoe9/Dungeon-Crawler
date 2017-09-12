package DungeonCrawler;
import java.util.ArrayList;

public class Attribute 
{
	public int value; 
	private AttributeEnum name;
	
	public Attribute(AttributeEnum n, int v) {name = n; value = v;}
	
	public void addValue(int v) {value += v;}
	public void addValue(Attribute a) {value += a.value;}
	public void subtractValue(int v) {value -= v;}
	public void subtractValue(Attribute a) {value -= a.value;}
	
	public static Attribute fromString(String s)
	{
		int commaIndex = s.indexOf(",");
		return new Attribute(AttributeEnum.fromString(s.substring(0, commaIndex)), Integer.parseInt(s.substring(commaIndex + 2)));
	}
	
	public AttributeEnum getName() {return name;}
	
	public enum AttributeEnum 
	{
		STRENGTH("STRENGTH"), DEXTERITY("DEXTERITY"),  WISDOM("WISDOM"), INTELLIGENCE("INTELLIGENCE"),
		ENDURANCE("ENDURANCE"),LUCK("LUCK"), HEALTH("HEALTH"), MAXHEALTH("MAXHEALTH"), MANA("MANA"),
		MAXMANA("MAXMANA"), EXPERIENCE("EXPERIENCE"), LEVEL("LEVEL");
		
		private char[] attributeEnumString;
		
		private AttributeEnum(String s){attributeEnumString=s.toCharArray();}
		
		public static AttributeEnum fromString(String s)
		{
			char[] sChars = s.toUpperCase().toCharArray();
			ArrayList<AttributeEnum> allAttributeEnums = new ArrayList<AttributeEnum>();
			
			for(AttributeEnum c : AttributeEnum.values()) allAttributeEnums.add(c);
			
			for(int a=0;a<sChars.length;a++)
				for(int b=allAttributeEnums.size()-1; b>=0; b--)
				{
					char[] currentCommand = allAttributeEnums.get(b).attributeEnumString;
					if(a >= currentCommand.length || currentCommand[a] != sChars[a]) allAttributeEnums.remove(b);
				}
			
			if(allAttributeEnums.size() == 1) return allAttributeEnums.get(0);
			else return null;
		}
		
		public String toString()
		{
			return new String(attributeEnumString);
		}
	}
}
