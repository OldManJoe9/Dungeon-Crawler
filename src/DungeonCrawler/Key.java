package DungeonCrawler;
import java.util.ArrayList;

public class Key extends Item 
{
	private LockLevel lockLevel;
		
	public Key(LockLevel l) {this(l,0);}
	public Key(LockLevel l, int v) 
	{
		super(l.getName() + " Key", null, v, false, "A key to unlock " + l.getName() + " or lower locks.", "Key's cannot be used on their own.", ClassifiedArrayList.Classification.fromInt(l.getLevel()));
		lockLevel = l;
	}

	public void use(Character c) {}
	public LockLevel getLockLevel() {return lockLevel;}
	
	public enum LockLevel
	{
		BRONZE("Bronze", 1), SILVER("Silver", 2), GOLD("Gold", 3), NONE("None", 0);
		
		private String lockLevelString;
		private int level;
		private LockLevel(String s, int l) {lockLevelString=s; level=l;}
		
		static LockLevel fromString(String s)
		{
			char[] sChars = s.toUpperCase().toCharArray();
			ArrayList<LockLevel> allLockLevels = new ArrayList<LockLevel>();
			
			for(LockLevel c : LockLevel.values()) allLockLevels.add(c);
			
			for(int a=0;a<sChars.length;a++)
				for(int b=allLockLevels.size()-1; b>=0; b--)
				{
					char[] currentLockLevel = allLockLevels.get(b).lockLevelString.toUpperCase().toCharArray();
					if(a >= currentLockLevel.length || currentLockLevel[a] != sChars[a]) allLockLevels.remove(b);
				}
			
			if(allLockLevels.size() == 1) return allLockLevels.get(0);
			else return null;
		}
		
		public int getLevel() {return level;}
		public String getName() {return lockLevelString;}
		public static LockLevel getNextLevel(LockLevel l)
		{
			switch(l){
			case NONE: return BRONZE;
			case BRONZE: return SILVER;
			case SILVER: return GOLD;
			case GOLD: return null;
			default: return null;
			}
		}
	}
}
