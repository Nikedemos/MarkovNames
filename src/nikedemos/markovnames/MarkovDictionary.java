package nikedemos.markovnames;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class MarkovDictionary {
	private Random rng;
	private int sequenceLen=3;

	private HashMap2D<String,String,Integer> occurrences = new HashMap2D<String,String,Integer>();
	
	public MarkovDictionary(String dictionary, int seqlen, Random rng) {
	this.rng = rng;

	try {
		applyDictionary(dictionary, seqlen);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	public MarkovDictionary(String dictionary, int seqlen)
	{
		this(dictionary, seqlen, new Random());
	}
	
	public MarkovDictionary(String dictionary)
	{
		this(dictionary, 3, new Random()); //3 is the default, anyway
	}
	
	public MarkovDictionary(String dictionary, Random rng)
	{
		this(dictionary, 3, rng); //3 is the default, anyway
	}
	
	//Blatantly copied from EnderIO: https://github.com/SleepyTrousers/EnderIO/blob/master/enderio-base/src/main/java/crazypants/enderio/base/config/recipes/RecipeFactory.java#L44-L57
	//Thanks again for your help, Henry Loenwind!
	
	private InputStream getResource(ResourceLocation resourceLocation) {
		    final ModContainer container = Loader.instance().activeModContainer();
		    if (container != null) {
		      final String resourcePath = String.format("/%s/%s/%s", "assets", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
		      InputStream resourceAsStream = null;
		      
		      try {
		      resourceAsStream = container.getMod().getClass().getResourceAsStream(resourcePath);
		      }
		      catch (Exception e)
		      {
		    	  e.printStackTrace();  
		      }
		      
		      
		      if (resourceAsStream != null) {
		        return resourceAsStream;
		      } else {
		        throw new RuntimeException("Could not find resource " + resourceLocation);
		      }
		    } else {
		      throw new RuntimeException("Failed to find current mod while looking for resource " + resourceLocation);
		    }
		}
	
	public String getCapitalized(String str) {
		StringBuilder build = new StringBuilder(str);
		String capital = build.substring(0,1).toUpperCase();
		
		build = build.replace(0, 1, capital);
		
		return build.toString();		
	}
	
	public static String readFile(String path) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, StandardCharsets.UTF_8);
	}
	
	public void incrementSafe(String str1, String str2) {
		
		if (occurrences.containsKeys(str1, str2)) //so for instance, "_DUP_AUX", "_TOTAL" will count how many "DUP" seqCurrs we have
		{
		int curr = occurrences.get(str1, str2).intValue();
		occurrences.put(str1, str2, Integer.valueOf(curr+1));
		}
	else
		{
		occurrences.put(str1, str2, Integer.valueOf(1));
		}
	}
	
	public String generateWord()
	{

		
		//let's pick the first element, from which further picking shall proceed.
		//first, we need to know how many top-level sequences (sequenceLen length) strings
		//we have. So just take into account those surrounded by "_" - and count them.
		//From that we'll get the weights.
		
		
		int allEntries=0;
		
		//first iteration: we count top level entries. There's just no other way.
		Iterator<Entry<String, Map<String, Integer>>> i = occurrences.mMap.entrySet().iterator();
		
		while (i.hasNext()) {
			Map.Entry<String, Map<String, Integer>> pair = (Entry<String, Map<String, Integer>>) i.next();
	        
	        String k=(String) pair.getKey();
	        
	        if (k.substring(0,1).equals("_") && k.substring(k.length()-1).equals("_")) //dealing with meta entry here
	        	{
		        //System.out.println(k+" YES!");
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        	
		        //great! now we're looking for "["
	        	
	        	if (k.substring(1,2).equals("["))
	        		{
	        		allEntries+=occurrences.get(k,"_TOTAL_");
	        		}
	        	}
			}
		int topLevelEntries;
		
		int randomNumber = rng.nextInt(allEntries);
		
		//ok, so how does this weighted random work?
		//easy. Check if the randomNumber is LESS than
		//topLevelEntries variable for that entry (see below).
		//If it is, break the while loop - we got our first element,
		//from which we will go further. Just remember to
		//remove the underscores at both ends!
		
		Iterator<Entry<String, Map<String, Integer>>> it = occurrences.mMap.entrySet().iterator();
		
		StringBuilder sequence=new StringBuilder("");
		
	    while (it.hasNext()) {
	        Map.Entry<String, Map<String, Integer>> pair = (Entry<String, Map<String, Integer>>)it.next();
	        
	        String k=(String) pair.getKey();
	        
	        if (k.substring(0,1).equals("_") && k.substring(k.length()-1).equals("_")) //dealing with meta entry here
	        	{
		        //System.out.println(k+" YES!");
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        	
		        //great! now we're looking for "[" only
	        	
	        	if (k.substring(1,2).equals("["))
	        	{
	        	topLevelEntries=occurrences.get(k, "_TOTAL_");
	        	
	        	if (randomNumber<topLevelEntries)
	        		{
	        		sequence = sequence.append(k.substring(1, sequenceLen+1)); //removing the underscores
	        		break;
	        		}
	        	else
	        		{
	        		//keep going
	        		randomNumber-=topLevelEntries;
	        		}
	        	}
	        	}
	    }
	    //System.out.println(sequence); 
		
	    //great! now that we have the first element, time for some generic iterations.
	    //in a very similar manner. Basically - perform this loop till you encounter "]" at the end
		StringBuilder word=new StringBuilder("");
		
	    word = word.append(sequence); //now we're gonna use firstElement to keep the sequence
	    
	    while (!sequence.substring(sequence.length()-1).equals("]"))
	    	{
	    	//sequence is now your HashMap key for the 1st dimension.
	    	//for that sequence:
	    	// - get total elements that are not meta (not surrounded by underscores)
	    	//   and count their total occurrences
	    	int subSize = 0;
	    	
	    	//System.out.println(sequence);
	    	
	    	Iterator<?> j = occurrences.mMap.get(sequence.toString()).entrySet().iterator();
	    	
	    	while (j.hasNext())
	    		{
	    		@SuppressWarnings("unchecked")
				Map.Entry<String, Integer> entry = (Entry<String, Integer>)j.next();
	    		subSize+=entry.getValue();
	    		}
	    	
	    	//and now, the last iterator, with a random, just like before
	    	randomNumber=rng.nextInt(subSize);
	    	
	    	Iterator<Entry<String, Integer>> k = occurrences.mMap.get(sequence.toString()).entrySet().iterator();
	    	
	    	String chosen="";
	    	
	    	while (k.hasNext())
	    		{
	    		HashMap.Entry<String, Integer> entry = (Entry<String, Integer>)k.next();
	    		int occu = occurrences.get(sequence.toString(),  (String)entry.getKey());
	    		
	    		if (randomNumber<occu)
	    			{
	    			chosen = (String)entry.getKey();
	    			break;
	    			}
	    		else
	    			{ //keep going!
	    			randomNumber-=occu;
	    			}
	    		}
    		//now, append the word...
    		word = word.append(chosen);
    		
    		//delete the first character of the sequence,
    		//and also append it with chosen character.
    		//So if the Sequence is ABC, and chosen is D,
    		//it now becomes BCD.
    		sequence = sequence.delete(0, 1);
    		sequence = sequence.append(chosen);
    		//System.out.println("FINAL: "+sequence);
    		//System.out.println("CHOSEN: "+chosen);
    		
	    	}
	    //and now remove the square brackets surrounding it.
	    word = word.delete(0, 1); //delete first char
	    word = word.delete(word.length()-1,word.length()); //delete last char
		return getCapitalized(word.toString());
	}
	
	public void applyDictionary(String dictionaryFile, int seqLen) throws IOException
	{
	StringBuilder input=new StringBuilder();
	
		
		String canonical = new StringBuilder("customnpcs:markovnames/dictionary/").append(dictionaryFile).toString();
		
		BufferedReader readIn = new BufferedReader(new InputStreamReader(getResource(new ResourceLocation(canonical)), "UTF-8"));
		//Thread.currentThread().getContextClassLoader().getResourceAsStream("path/to/resource/file.ext");
		
		for (String line = readIn.readLine(); line != null; line = readIn.readLine())
		{
	    input = input.append(line).append(" ");
		}
		
		readIn.close();
		
	/*
	String canonical = new StringBuilder(dictionaryFile).toString();
	
	/*
    BufferedInputStream result = (BufferedInputStream) 
            MarkovDictionary.class.getClassLoader().getResourceAsStream(canonical);
	
    byte [] b = new byte[256];
    int val = 0;
    
    do {
        try {
            val = result.read(b);
            if (val > 0) {
                input.append(new String(b, 0, val));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    } while (val > -1);
	
	//StringBuilder path = new StringBuilder("src/main/java/nikedemos/markovnames/dictionary/").append(dictionaryFile);
	/*
	try {
		input = readFile(path.toString());
	} catch (IOException e) {
		
		e.printStackTrace();
	}
	*/
	//if seqLen != this.sequenceLen, we must clear occurrences
	
	if (this.sequenceLen!=seqLen)
		{
		sequenceLen = seqLen;
		this.occurrences.clear();
		}
	
	String input_str=input.toString();
	
	if (input_str!="")
	{
		input_str = input_str.toLowerCase(); //XXXXXXXX
	
	//turn all newline/whitespace characters into inter-word separators
		input_str = input_str.replaceAll("[\\t\\n\\r\\s]+","][");
	
	//turn all the whitespace characters
	//into spaces so they can act as terminators
	//input = input.replaceAll("\\s+"," ");
	
	//now, turn all the spaces into "]["
	//input = input.replaceAll(" ", "");
	
	//and add "[" at the beginning and "]" at the end
	
	StringBuilder input_brackets = new StringBuilder("[").append(input_str).append("]");
	
	input_str = input_brackets.toString();
	
	int maxCursorPos = input_str.length()-1-sequenceLen;
	
	for (int i=0; i<=maxCursorPos; i++)
		{
		String seqCurr = input_str.substring(i, i+(sequenceLen)); // i plus 2 characters next to it
		String seqNext = input_str.substring(i+sequenceLen,i+sequenceLen+1); //next character after that
		incrementSafe(seqCurr, seqNext);
		//aux counters
		
		StringBuilder meta = new StringBuilder("_").append(seqCurr).append("_");
		
		//String aux1="_"+seqCurr+"_";
		incrementSafe(meta.toString(), "_TOTAL_");
		//String aux2="_"+seqNext+"_";
		//incrementSafe(aux1, aux2);
		
		//debug, feel free to uncomment
		
		/*
		System.out.println("<"+seqCurr+"><"+seqNext+"> => "+occurrences.get(seqCurr, seqNext));
		System.out.println("<"+aux1+"><"+"_TOTAL_"+"> => "+occurrences.get(aux1, "_TOTAL_"));
		System.out.println("<"+aux1+"><"+aux2+"> => "+occurrences.get(aux1, aux2));
		System.out.println("");
		*/
		
		}
	//by now, we should have 
	}
	}
}
