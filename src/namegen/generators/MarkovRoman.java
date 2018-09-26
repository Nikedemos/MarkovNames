package namegen.generators;

import java.util.Random;

import namegen.MarkovDictionary;

public class MarkovRoman extends MarkovGenerator {
	public MarkovDictionary markov2;
	public MarkovDictionary markov3;

	public MarkovRoman(Random rng)
	{
		this.rng = rng;
		this.markov  = new MarkovDictionary("roman_praenomina.txt",rng);
		this.markov2 = new MarkovDictionary("roman_nomina.txt",rng);
		this.markov3 = new MarkovDictionary("roman_cognomina.txt",rng);
		
	}
	
	public MarkovRoman()
	{
		this(new Random());
		
	}
	
	@Override
	public String feminize(String element, boolean flag)
	{
		//change "us" into "a" and "o" at the end into "a"
		//check if the last two characters are "us"
		String lastTwoChars = element.substring(element.length()-2);
		String lastChar = element.substring(element.length());
		
		if (lastTwoChars.equals("us"))
		{
		lastTwoChars="a"; //if it's a cognominum, the ending is in a different grammatical case

		//delete last two chars from the element
		
		element = element.substring(0, element.length()-2)+lastTwoChars;
		}
		else
		{
		if (lastChar=="o")
			{
			lastChar="a";
			
			element = element.substring(0, element.length()-1)+lastChar;
			}
		}
		
		return element;
	}
	
	@Override
	public String fetch()
	{
		return fetch(0); //0 = random gender, 1 = male, 2 = female
	}
	
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
		
		return seq1+" "+seq2+" "+seq3;
	}
}
