package namegen;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Markov {
	private Random rng;
	private int sequenceLen=1;

	private HashMap2D<String,String,Integer> occurrences = new HashMap2D<String,String,Integer>();
	
	public Markov(Random rng) {
	this.rng = rng;

	applyDictionary("1000mostCommonEnglishWords.txt", 3);
	System.out.println(generateWord());
	}
	
	public Markov()
	{
		this(new Random());
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
		String word="";
		
		//let's pick the first element, from which further picking shall proceed.
		//first, we need to know how many top-level sequences (sequenceLen length) strings
		//we have. So just take into account those surrounded by "_" - and count them.
		//From that we'll get the weights.
		
		
		int allEntries=0;
		
		//first iteration: we count top level entries. There's just no other way.
		Iterator i = occurrences.mMap.entrySet().iterator();
		
		while (i.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)i.next();
	        
	        String k=(String) pair.getKey();
	        
	        if (k.substring(0,1).equals("_") && k.substring(k.length()-1).equals("_")) //dealing with meta entry here
	        	{
		        //System.out.println(k+" YES!");
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        	
		        //great! now we're looking for "[" only
	        	
	        	if (k.substring(1,2).equals("["))
	        		{
	        		allEntries++;
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
		
		Iterator it = occurrences.mMap.entrySet().iterator();
		
		String sequence="";
		
	    while (it.hasNext()) {
	        HashMap.Entry pair = (HashMap.Entry)it.next();
	        
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
	        		sequence=k.substring(1, sequenceLen+1); //removing the underscores
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
	    
	    word = sequence; //now we're gonna use firstElement to keep the sequence
	    
	    int cursorPos=0;
	    
	    while (!sequence.substring(sequence.length()-1).equals("]"))
	    	{
	    	//sequence is now your HashMap key for the 1st dimension.
	    	//for that sequence:
	    	// - get total elements that are not meta (not surrounded by underscores)
	    	//   and count their total occurrences
	    	int subSize = 0;
	    	
	    	Iterator j = occurrences.mMap.get(sequence).entrySet().iterator();
	    	
	    	while (j.hasNext())
	    		{
	    		HashMap.Entry entry = (HashMap.Entry)j.next();
	    		subSize+=occurrences.get(sequence, (String)entry.getKey());
	    		}
	    	
	    	//and now, the last iterator, with a random, just like before
	    	randomNumber=rng.nextInt(subSize);
	    	
	    	Iterator k = occurrences.mMap.get(sequence).entrySet().iterator();
	    	
	    	String chosen="";
	    	
	    	while (k.hasNext())
	    		{
	    		HashMap.Entry entry = (HashMap.Entry)k.next();
	    		int occu = occurrences.get(sequence,  (String)entry.getKey());
	    		
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
    		word = word + chosen;
    		
    		//delete the first character of the sequence,
    		//and also append it with chosen character.
    		//So if the Sequence is ABC, and chosen is D,
    		//it now becomes BCD.
    		sequence = sequence.substring(1, sequence.length()-1)+chosen;
    		
	    	}
	    
		return word;
	}
	
	public void applyDictionary(String dictionaryFile, int seqLen)
	{
	String input="";
	
	try {
		input = readFile("src/dictionary/"+dictionaryFile);
	} catch (IOException e) {
		
		e.printStackTrace();
	}
	//if seqLen != this.sequenceLen, we must clear occurrences
	
	if (this.sequenceLen!=seqLen)
		{
		sequenceLen = seqLen;
		this.occurrences.clear();
		}
	
	if (input!="")
	{
	input.toLowerCase(); //XXXXXXXX
	
	//turn all newline/whitespace characters into inter-word separators
	input = input.replaceAll("[\\t\\n\\r\\s]+","][");
	
	//turn all the whitespace characters
	//into spaces so they can act as terminators
	//input = input.replaceAll("\\s+"," ");
	
	//now, turn all the spaces into "]["
	//input = input.replaceAll(" ", "");
	
	//and add "[" at the beginning and "]" at the end
	input = "["+input;
	
	//at the end (as a terminator), add one
	input = input+"]";
	
	//System.out.print(input);
	
	int cursor=0;
	int maxCursorPos = input.length()-1-sequenceLen;
	
	for (int i=0; i<=maxCursorPos; i++)
		{
		String seqCurr = input.substring(i, i+(sequenceLen)); // i plus 2 characters next to it
		String seqNext = input.substring(i+sequenceLen,i+sequenceLen+1); //next character after that
		incrementSafe(seqCurr, seqNext);
		//aux counters
		String aux1="_"+seqCurr+"_";
		incrementSafe(aux1, "_TOTAL_");
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
