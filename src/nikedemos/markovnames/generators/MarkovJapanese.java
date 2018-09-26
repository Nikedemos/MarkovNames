package nikedemos.markovnames.generators;

import java.util.Random;

import nikedemos.markovnames.MarkovDictionary;

public class MarkovJapanese extends MarkovGenerator {
	public MarkovDictionary markov2;
	public MarkovDictionary markov3;
	
	public MarkovJapanese(int seqlen, Random rng)
	{
		this.rng = rng;
		this.markov  = new MarkovDictionary("japanese_surnames.txt",seqlen,rng);
		this.markov2 = new MarkovDictionary("japanese_given_male.txt",seqlen,rng);
		this.markov3 = new MarkovDictionary("japanese_given_female.txt",seqlen,rng);
		
	}
	
	public MarkovJapanese(int seqlen)
	{
		this(seqlen,new Random());
		
	}

	public MarkovJapanese()
	{
		this(4, new Random()); //4 seems best suited for Japanese
	}

	@Override
	public String fetch(int gender)
	{
		
		String surname = markov.generateWord();
		String given = "";
		
		//check the gender.
		//0 = random gender, 1 = male, 2 = female
		//if there's no gender specified (0),
		//now it's time to pick it at random
		//
		if (gender==0)
		{
			gender = rng.nextBoolean()==true? 1 : 2;
		}
		
		if (gender==2)
			{
			given = markov3.generateWord();
			}
		else
			{
			given = markov2.generateWord();
			}
		
		return surname+" "+given;
	}
}
