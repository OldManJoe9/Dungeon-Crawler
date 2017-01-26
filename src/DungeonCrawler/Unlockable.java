package DungeonCrawler;

public interface Unlockable 
{
	public void unlock(Key k);
	public Key.LockLevel getLockLevel();
	public String getName();
}
