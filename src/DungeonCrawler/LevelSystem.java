package DungeonCrawler;

import DungeonCrawler.Attribute.AttributeEnum;

public class LevelSystem 
{
	private static final int MAX_LEVEL = 100;
	private static int[] levels = new int[MAX_LEVEL];
	private static boolean levelsSet = false;
	
	private static void setLevels()
	{
		for(int a=0;a<levels.length;a++)
			levels[a] = (int)(Math.pow(a, 3) + a * 300);
	}
	
	public static void checkLevel(Character p)
	{
		if(!levelsSet) setLevels();
		
		int experience = p.getAttribute(AttributeEnum.EXPERIENCE).getValue(),
				level = p.getAttribute(AttributeEnum.LEVEL).getValue();
		
		checkLevel(p, experience, level);
	}
	
	private static void checkLevel(Character p, int e, int l)
	{
		if(l < MAX_LEVEL && e >= levels[l + 1])
			p.addAttribute(AttributeEnum.LEVEL, 1);
		else return;
		
		checkLevel(p, e, l);
	}
}
