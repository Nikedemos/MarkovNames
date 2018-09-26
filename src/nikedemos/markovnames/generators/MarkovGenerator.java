package nikedemos.markovnames.generators;

import java.util.Random;

import nikedemos.markovnames.MarkovDictionary;

public class MarkovGenerator {

	public MarkovDictionary markov;
	public Random rng;
	
	public MarkovGenerator(int seqlen, Random rng)
	{
	this.rng = rng;
	//this.markov = new MarkovDictionary("1000mostCommonEnglishWords.txt",seqlen,rng);
	}
	
	public MarkovGenerator(int seqlen)
	{
		this(seqlen, new Random());
	}
	
	public MarkovGenerator()
	{
		this(3, new Random());
	}

	public String fetch(int gender) {
		return markov.generateWord();
	}
	
	public String fetch()
	{
		return fetch(0); //0 = random gender, 1 = male, 2 = female
	}
	

	public String feminize(String element, boolean flag) {
		return element;
	}
}
