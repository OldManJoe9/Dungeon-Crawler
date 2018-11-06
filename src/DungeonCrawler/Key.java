package DungeonCrawler;

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
}
