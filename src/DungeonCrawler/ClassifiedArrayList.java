package DungeonCrawler;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ClassifiedArrayList<T> extends ArrayList<T>
{
	private Classification classification;
	public ClassifiedArrayList(Classification c) {classification=c;}
	
	public Classification getClassification(){return classification;}
	public int getMultiplier() {return classification.getMultiplier();}
	
	public enum Classification
	{
		COMMON("COMMON", 5), UNCOMMON("UNCOMMON", 4), RARE("RARE", 3), VERYRARE("VERYRARE", 2), ULTRARARE("ULTRARARE", 1);
		
		private char[] classificationString;
		private int multiplier;
		private Classification(String s, int m) {classificationString=s.toCharArray(); multiplier=m;}
		
		public static Classification fromString(String s)
		{
			char[] sChars = s.toUpperCase().toCharArray();
			ArrayList<Classification> allClassifications = new ArrayList<Classification>();
			
			for(Classification c : Classification.values()) allClassifications.add(c);
			
			for(int a=0;a<sChars.length;a++)
				for(int b=allClassifications.size()-1; b>=0; b--)
				{
					char[] currentClassification = allClassifications.get(b).classificationString;
					if(a >= currentClassification.length || currentClassification[a] != sChars[a]) allClassifications.remove(b);
				}
			
			if(allClassifications.size() == 1) return allClassifications.get(0);
			else return null;
		}
		public static Classification fromInt(int i)
		{
			for(Classification c : Classification.values())
				if(i == c.getMultiplier()) return c;
			
			return null;
		}
		
		public String toString() {return new String(classificationString);}
		
		public int getMultiplier() {return multiplier;}
	}
}
