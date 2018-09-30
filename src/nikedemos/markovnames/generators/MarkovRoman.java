package nikedemos.markovnames.generators;

import java.util.Random;

import net.minecraft.util.text.TextComponentTranslation;
import nikedemos.markovnames.MarkovDictionary;

public class MarkovRoman extends MarkovGenerator {
	public MarkovDictionary markov2;
	public MarkovDictionary markov3;

	public MarkovRoman(int seqlen, Random rng)
	{
		this.rng = rng;
		this.markov  = new MarkovDictionary("roman_praenomina.txt",seqlen,rng);
		this.markov2 = new MarkovDictionary("roman_nomina.txt",seqlen,rng);
		this.markov3 = new MarkovDictionary("roman_cognomina.txt",seqlen,rng);
	}
	
	public MarkovRoman(int seqlen)
	{
		this(seqlen,new Random());
		
	}
	
	public MarkovRoman()
	{
		this(3, new Random());
	}
	
	@Override
	public String feminize(String element, boolean flag)
	{
		//change "us" into "a" and "o" at the end into "a"
		//check if the last two characters are "us"
		String lastTwoChars = element.substring(element.length()-2);
		String lastChar = element.substring(element.length()-1);
		
		if (lastTwoChars.equals("us"))
		{
		lastTwoChars="a";
		//delete last two chars from the element and append with "a"
		element = new StringBuilder(element.substring(0, element.length()-2)).append(lastTwoChars).toString();
		}
		else
		{
		if (lastChar=="o")
			{
			lastChar="a";
			
			element = new StringBuilder(element.substring(0, element.length()-1)).append(lastChar).toString();
			}
		}
		
		return element;
	}
	@Override
	public String stylize(String str)
	{
	//Okay, so what can we do to make a Roman name fancy?
	//First, convert to UPPERCASE.
	//Then, convert all "U" into "V".
	//Then - replace with Full-Width form.
	//Full-width forms fit the Roman theme quite nicely in MC!
	/*	
	str = str.toUpperCase();

	//str = str.replaceAll("U", "V");
	
	//now make it full-width
	String oldChars = new String("QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789");
	String newChars = new String("ＱＷＥＲＴＹＵＩＯＰＡＳＤＦＧＨＪＫＬＺＸＣＶＢＮＭｑｗｅｒｔｙｕｉｏｐａｓｄｆｇｈｊｋｌｚｘｃｖｂｎｍ０１２３４５６７８９");
	
	//oh btw this looks like quite a useful generic function.
	//I sure miss being able to just replace arrays of characters with another array of characters
	//in "vanilla" PHP strings...
	
	for (int c=0; c<oldChars.length(); c++)
	{
		String oc = oldChars.substring(c, c+1);
		String nc = newChars.substring(c, c+1);
		str = str.replaceAll(oc, nc);
	}
	*/
	return str;
	}
	
	@Override
	public String fetch(int gender)
	{
		
		String seq1 = markov.generateWord();
		String seq2 = markov2.generateWord();
		String seq3 = markov3.generateWord();
		
		//check the gender.
		//0 = random gender, 1 = male, 2 = female
		//if there's no gender specified (0),
		//now it's time to pick it at random
		//
		if (gender==0)
		{
			gender = rng.nextBoolean()==true? 1 : 2;
		}
		
		//now if it's 2 - a lady - feminize the 3 sequences
		if (gender==2)
		{
		seq1 = feminize(seq1, false);
		seq2 = feminize(seq2, false);
		seq3 = feminize(seq3, true);
		}
		
		return stylize(new StringBuilder(seq1).append(" ").append(seq2).append(" ").append(seq3).toString());
	}
}
