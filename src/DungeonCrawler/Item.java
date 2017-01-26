package DungeonCrawler;
public class Item 
{
	private boolean isPriceless;
	private ClassifiedArrayList.Classification classification;
	private int value;
	private String name, description, onUse;
	private SpecialAttribute[] specialAttributes;
		
	public Item(String n, SpecialAttribute[] sa, int v, boolean isP, String d, String ou, ClassifiedArrayList.Classification c) 
	{
		name=n; specialAttributes=sa; value=v; isPriceless=isP; description=d; onUse=ou; classification = c;
	}
	
	public void use(Character c) 
	{
		for(SpecialAttribute sa : specialAttributes) 
		{
			c.addSpecialAttribute(sa);
			sa.cycle(c);
		}
		
		c.removeItem(this);
	}
	
	public int getValue() {return value;}
	public boolean isPriceless() {return isPriceless;}
	public ClassifiedArrayList.Classification getClassification() {return classification;}
	public String getDescription() {return description;}
	public String getName() {return name;}
	public String onUse() {return onUse;}
}
