package DungeonCrawler;

public interface Unlockable 
{
	public void unlock(Key k);
	public LockLevel getLockLevel();
	public String getName();
}
