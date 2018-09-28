package nikedemos.markovnames;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import nikedemos.markovnames.generators.MarkovGenerator;
import nikedemos.markovnames.generators.MarkovRoman;

@Mod.EventBusSubscriber
public class MarkovHandler {
    
    /*
    Random sequence generators, using MarkovNames lib by Nikedemos
    https://github.com/Nikedemos/MarkovNames
	*/
    public static final MarkovGenerator[] MARKOV_GENERATOR = {
    	new MarkovRoman(3),
    	//new MarkovJapanese(4),
    	//new MarkovSlavic(3),
    	//new MarkovWelsh(3),
    	//new MarkovSaami(3),
    	//new MarkovOldNorse(4),
    	//new MarkovAncientGreek(3),
    	//new MarkovAztec(3)
    	};
    	@EventHandler
    	public void init(FMLInitializationEvent event){
    		
    	}
    

}