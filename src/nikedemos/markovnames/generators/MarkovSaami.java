package nikedemos.markovnames.generators;

import java.util.Random;

import net.minecraft.util.text.TextComponentTranslation;
import nikedemos.markovnames.MarkovDictionary;

public class MarkovSaami extends MarkovGenerator {

	public MarkovDictionary markov2;

	public MarkovSaami(int seqlen, Random rng)
	{
		this.rng = rng;
		this.markov  = new MarkovDictionary("saami_bothgenders.txt",seqlen,rng);
	}
	
	public MarkovSaami(int seqlen)
	{
		this(seqlen,new Random());
		
	}
	
	public MarkovSaami()
	{
		this(3, new Random()); //3 seems best-suited for Saami
	}

	@Override
	public String fetch(int gender) //Saami names are genderless
	{
		return markov.generateWord();
	}
}
