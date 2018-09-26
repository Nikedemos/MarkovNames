package nikedemos.markovnames.generators;

import java.util.Random;

import nikedemos.markovnames.MarkovDictionary;

public class MarkovGenerator {

	public MarkovDictionary markov;
	public Random rng;
	
	public MarkovGenerator(Random rng)
	{
	this.rng = rng;
	this.markov = new MarkovDictionary("1000mostCommonEnglishWords.txt",rng);
	}
	
	public MarkovGenerator()
	{
		this(new Random());
	}
	
	public String fetch() {
		return markov.generateWord();
		
	}

	public String feminize(String element, boolean flag) {
		return element;
	}
}
