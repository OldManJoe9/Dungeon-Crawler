package DungeonCrawler;

/*
 * player uses an ability:
 * 
 * ability can be:
 * Magic ~ mana cost
 * non-magic ~ no mana cost
 * 
 * abilities can target:
 * player ~ healing or to improve attributes
 * opponent ~ damaging or to reduce attributes
 * 
 * healing abilities just heal HP
 * damaging abilities just take HP
 * attribute changing abilities can improve more than one attribute, including health and mana.
 */

public class Ability 
{
	private String name;
	private int manaCost;
	private SpecialAttribute[] specialAttributes;
	
	public Ability(String n, int mc, SpecialAttribute[] sa)
	{
		name=n; manaCost=mc; specialAttributes=sa;
	}
	
	public String getName() {return name;}
	public int getManaCost() {return manaCost;}
	
	public void use(Character c) {for(SpecialAttribute sa : specialAttributes) {c.addSpecialAttribute(sa); sa.cycle(c);}}
}
