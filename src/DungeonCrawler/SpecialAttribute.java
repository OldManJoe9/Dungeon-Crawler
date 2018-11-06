package DungeonCrawler;

public class SpecialAttribute
{
	/*
	 * Only one special per SpecialAttribute Object. For more than one special make more than one SpecialAttribute Object.
	 * 
	 * stop = cannot act
	 * poison = drains health
	 * sleep = cannot act until hit
	 * invincible = cannot take damage
	 * immune"" = cannot be hit with a special attribute that does ""
	 * none = does nothing
	 */
	private String name, special;
	private Attribute[] affectedAttributes;
	private int timer;
	private boolean stacking, called = false;
	
	public SpecialAttribute(String n, Attribute[] a, int t, boolean s, String sp)
	{
		name=n; affectedAttributes=a; timer=t; stacking=s; special=sp;
	}
	
	public String getName() {return name;}
	public String getSpecial() {return special;}
	
	private void act(Character c) {
		for(int b=0; b < affectedAttributes.length; b++)
			c.changeAttribute(affectedAttributes[b], true);
	}
	private void unact(Character c) {
		for(int b=0; b < affectedAttributes.length; b++)
			c.changeAttribute(affectedAttributes[b], false);
	}
	
	/* 
	 * items that are used instantly
	 * timer = 1;
	 * stacking = true;
	 */
	public void cycle(Character c)
	{
		timer--; 
		if(timer == 0) 
		{
			if(!stacking) unact(c);
			
			c.removeSpecialAttribute(this);
		}
		
		if(stacking || !called) {act(c); called = true;}
	}
}
