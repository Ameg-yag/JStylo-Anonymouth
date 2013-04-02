package edu.drexel.psal.anonymouth.suggestors;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.jgaap.eventDrivers.SentenceEventDriver;
import com.jgaap.generics.Document;
import com.jgaap.generics.EventGenerationException;
import com.jgaap.generics.EventSet;

import edu.drexel.psal.anonymouth.engine.FeatureList;
import edu.drexel.psal.jstylo.eventDrivers.SentenceCounterEventDriver;
import edu.drexel.psal.jstylo.eventDrivers.WordCounterEventDriver;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.drexel.psal.anonymouth.suggestors.UNIQUE_WORDS_COUNT;
import edu.drexel.psal.anonymouth.utils.POS;
import edu.drexel.psal.anonymouth.utils.POS.TheTags;

/**
 * makes highlighting maps
 * @author Andrew W.E. McDonald
 *
 */
public class HighlightMapMaker {
	
	public static String document;
	public static HashMap<Color,ArrayList<int[]>> highlightMap;
	//private Color[] colorChoices = {Color.yellow,Color.red,Color.orange,Color.pink,Color.green,Color.magenta,Color.lightGray,Color.cyan,Color.gray,Color.blue,Color.darkGray};
	private Color[] transColorChoices = {new Color(1.0f,1.0f,.2f,.4f), new Color(1.0f,0,0,.4f),  new Color(0f,1.0f,0f,.4f) , new Color(0f,0f,1.0f,.4f),new Color(1.0f,.4f,0,.4f), new Color(1.0f,.2f,1.0f,.4f)};
	private static int colorIndex = 0;
	private FeatureList calcToCall;
	public static double avgSentenceTargetValue;
	public static double avgCharTargetValue;
	 
	
	
	public void setDocument(String document){
		this.document = document.replaceAll("\\p{C}", " ");
	}
	
	/**
	 * Iterates through list of available highlight colors, each call updates the index
	 * @return
	 * 	the next color 
	 */
	public Color getNextColor(){
		int numColors = transColorChoices.length;
		colorIndex+=1;
		if(colorIndex >= numColors){
			colorIndex =1;
		}
		return transColorChoices[colorIndex-1];
	}
	
	/**
	 * sets the highlight map with indices of words that are shorter than the target value
	 * @param featureTargetValue
	 * @throws EventGenerationException 
	 */
	public void SHORTER_THAN_TARGET_WORDS() throws EventGenerationException{
		LinkedList<String> lessThan = new LinkedList<String>();
		String theDoc = document;
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");

		int i=0;
		Document doc = new Document();
		doc.setText(document.toCharArray());
		int numSentences = (int)new SentenceCounterEventDriver().getValue(doc); 
		
		
		@SuppressWarnings("unused")
		int numComplex =0;
		StringTokenizer st = new StringTokenizer(theDoc);
		String word;
		while(st.hasMoreTokens() == true){
			word = st.nextToken();
			if(word.length() < avgCharTargetValue)
				lessThan.push(word);
		highlightMap.put(getNextColor(), IndexFinder.findIndices(theDoc, lessThan));
		}
		
	}
	
	/**
	 * sets the highlight map with indices of words that a longer than the target value
	 * @param featureTargetValue
	 * @throws EventGenerationException 
	 */
	public void LONGER_THAN_TARGET_WORDS() throws EventGenerationException{
		LinkedList<String> greaterThan = new LinkedList<String>();
		String theDoc = document;
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");

		int i=0;
		Document doc = new Document();
		doc.setText(document.toCharArray());
		int numSentences = (int)new SentenceCounterEventDriver().getValue(doc); 
		
		
		@SuppressWarnings("unused")
		int numComplex =0;
		StringTokenizer st = new StringTokenizer(theDoc);
		String word;
		while(st.hasMoreTokens() == true){
			word = st.nextToken();
			if(word.length() > avgCharTargetValue)
				greaterThan.push(word);
		
		}
		highlightMap.put(getNextColor(), IndexFinder.findIndices(theDoc, greaterThan));	
	}
	
	public void SENTENCES_SHORTER_THAN_TARGET() throws EventGenerationException{
		String theDoc = document;
		Document doc = new Document();
		doc.setText(theDoc.toCharArray());	
		SentenceEventDriver sentenceEventDriver = new SentenceEventDriver();
	    EventSet sentenceSet = sentenceEventDriver.createEventSet(doc);  
	    ArrayList<String> allSentences = new ArrayList<String>();
	    
	    for (int i = 0; i < sentenceSet.size(); i++){
	    	allSentences.add( sentenceSet.eventAt(i).getEvent() );
	    	//System.out.println(allSentences.get(i).toString());
	    }
	   
	    int numSentences = allSentences.size();
	    ArrayList<Double> sentenceWordCount = new ArrayList<Double>(numSentences);
	    for(int i=0;i<numSentences;i++){
	    		Document justASentence = new Document();
	    		justASentence.setText(allSentences.get(i).toCharArray());
		    WordCounterEventDriver  wordEventDriver = new WordCounterEventDriver();
		    double wordCount = wordEventDriver.getValue(justASentence);
		    sentenceWordCount.add(i,wordCount);
		    //System.out.println(sentenceWordCount.get(i));
	    } 
	    ArrayList<int[]> shortIndices = new ArrayList<int[]>();
		LinkedList<String> sentencesList = new LinkedList<String>(allSentences);
		
		ArrayList<int[]> allIndices = IndexFinder.sentenceIndexFinder(theDoc, sentencesList );
		
		for (int i = 0; i < allIndices.size(); i++)
		{
			int[] temp = allIndices.get(i);
			String sentence = allSentences.get(i);
			Double wordsInSentence = sentenceWordCount.get(i);
			//System.out.print("words: "+wordsInSentence+"    "+"target: "+featureTargetValue+"      ");
			if ( wordsInSentence <avgSentenceTargetValue ){
				shortIndices.add(new int[] {temp[0],temp[1]});
			}
		}
		highlightMap.put( getNextColor(),shortIndices );
	}
	
	public void SENTENCES_LONGER_THAN_TARGET() throws EventGenerationException{
		String theDoc = document;
		Document doc = new Document();
		doc.setText(theDoc.toCharArray());	
		SentenceEventDriver sentenceEventDriver = new SentenceEventDriver();
	    EventSet sentenceSet = sentenceEventDriver.createEventSet(doc);  
	    ArrayList<String> allSentences = new ArrayList<String>();
	    
	    for (int i = 0; i < sentenceSet.size(); i++){
	    	allSentences.add( sentenceSet.eventAt(i).getEvent() );
	    	//System.out.println(allSentences.get(i).toString());
	    }
	   
	    int numSentences = allSentences.size();
	    ArrayList<Double> sentenceWordCount = new ArrayList<Double>(numSentences);
	    for(int i=0;i<numSentences;i++){
	    		Document justASentence = new Document();
	    		justASentence.setText(allSentences.get(i).toCharArray());
		    WordCounterEventDriver  wordEventDriver = new WordCounterEventDriver();
		    double wordCount = wordEventDriver.getValue(justASentence);
		    sentenceWordCount.add(i,wordCount);
		    //System.out.println(sentenceWordCount.get(i));
	    } 
		ArrayList<int[]> longIndices = new ArrayList<int[]>();	    
		LinkedList<String> sentencesList = new LinkedList<String>(allSentences);
		
		ArrayList<int[]> allIndices = IndexFinder.sentenceIndexFinder(theDoc, sentencesList );
		
		for (int i = 0; i < allIndices.size(); i++)
		{
			int[] temp = allIndices.get(i);
			String sentence = allSentences.get(i);
			Double wordsInSentence = sentenceWordCount.get(i);
			//System.out.print("words: "+wordsInSentence+"    "+"target: "+featureTargetValue+"      ");
			if ( wordsInSentence > avgSentenceTargetValue ){
					longIndices.add(new int[] {temp[0],temp[1]});
					
			}
		}
		highlightMap.put( getNextColor(),longIndices );
		System.out.println("NEXT COLOR! (long sentences)");
	}
	
	public void EIGHT_SYLLABLE_WORDS(){
		syllableFinder(8);
	}
	
	public void SEVEN_SYLLABLE_WORDS(){
		syllableFinder(7);
	}
	
	public void SIX_SYLLABLE_WORDS(){
		syllableFinder(6);
	}
	
	public void FIVE_SYLLABLE_WORDS(){
		syllableFinder(5);
	}
	
	public void FOUR_SYLLABLE_WORDS(){
		syllableFinder(4);
	}
	
	public void THREE_SYLLABLE_WORDS(){
		syllableFinder(3);
	}
	
	public void TWO_SYLLABLE_WORDS(){
		syllableFinder(2);
	}
	
	public void SINGLE_SYLLABLE_WORDS(){
		syllableFinder(1);
	}
		
	public void syllableFinder(int numSylls){
		LinkedList<String> sameNumSylls = new LinkedList<String>();
		
		String theVowels = "aeiouyAEIOUY";
	
		String theDoc = document;
	    //theDoc = theDoc.replaceAll("[\\r\\n\\t.?!\":\\-,;()\\[\\]\\\\]", " ");
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");
		
		StringTokenizer st = new StringTokenizer(theDoc);
		String word;
		while(st.hasMoreTokens() == true){
			word = st.nextToken();
	        int sylls = 0; //NOTE: this whole loop came from  JGAAP's source code.
	        for (int j = 0; j < word.length(); j++) {
	                if ((theVowels.indexOf(word.charAt(j)) != -1)
	                                && ((j == word.length() - 1) || (theVowels.indexOf(word.charAt(j + 1)) == -1))) {
	                       sylls++;
	                }
	        }
	        if (sylls == 0) {
	                sylls = 1; // handle words like "Dr" by setting to 1
	        }
	        // here is where we will collect the number of syllables in each word. 
	        if(sylls == numSylls)
	        		sameNumSylls.push(word);
	        
		}
		
        highlightMap.put(getNextColor(),IndexFinder.findIndices(theDoc,sameNumSylls));
	}
	
	public void ALL_DIGITS(){
		calcToCall = FeatureList.DIGITS_PERCENTAGE;
		String stringDoc = document;
		String regEx = "\\d{1}+";
		ArrayList<int[]> theIndices = IndexFinder.findIndices(stringDoc,regEx);
		highlightMap.put(getNextColor(), theIndices);
	}
	
	public void SPECIFIC_DIGITS(String digits){
		calcToCall = FeatureList.DIGITS;
		String stringDoc = document;
		ArrayList<int[]> theIndices = IndexFinder.findIndices(stringDoc, digits);
		highlightMap.put(getNextColor(), theIndices);
	}
	
	public void SPECIFIC_FUNCTION_WORDS(String word){
		calcToCall = FeatureList.FUNCTION_WORDS;
		String theDoc = document;
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");
		ArrayList<int[]> theIndices = IndexFinder.findIndices(theDoc, "\\b"+word.replaceAll("\\p{C}", "")+"\\b{1}+");
		highlightMap.put(getNextColor(), theIndices);
	}
	
	public ArrayList<int[]> findLetterIndices(String letter){	
		ArrayList<int[]> toHighlight = new ArrayList<int[]>();
		String stringToTest=document;
		String  theRegEx = letter.replaceAll("\\p{C}", " ");
		int lengthRegEx = theRegEx.length();
		Pattern p = Pattern.compile(theRegEx);
		Matcher m = p.matcher(stringToTest);
		int startIndex=-1;
		while(true){
		boolean found = m.find(startIndex+1);
		if(found == true){
			int index = m.start();
			System.out.println(index);
			toHighlight.add(new int[]{index,index+lengthRegEx});
			startIndex=index;
			m.reset();
		}
		else
			break;
		}
		return toHighlight;
		
	}
	
	public void SPECIFIC_LETTER_N_GRAMS(String gram){
		ArrayList<int[]> toHighlight = findLetterIndices(gram);
		Color highlightColor = getNextColor();
		highlightMap.put(highlightColor,toHighlight);
	}
	
	public void SPECIFIC_LETTER(String letter){
		String theDoc = document;
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");
		theDoc = theDoc.toLowerCase();
		StringTokenizer st = new StringTokenizer(theDoc);
		
		LinkedList<String> yesLetter = new LinkedList<String>();
		
		String word;
		while(st.hasMoreTokens()){
			word = st.nextToken();
			if(word.contains(letter))
				yesLetter.push(word);
			
		}
		highlightMap.put(getNextColor(),IndexFinder.findIndices(theDoc, yesLetter));
	}
	
	public void SPECIFIC_WITHOUT_LETTER(String letter){
		String theDoc = document;
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");
		theDoc = theDoc.toLowerCase();
		StringTokenizer st = new StringTokenizer(theDoc);
		
		LinkedList<String> noLetter = new LinkedList<String>();
		
		String word;
		while(st.hasMoreTokens()){
			word = st.nextToken();
			if(word.contains(letter) == false)
				noLetter.push(word);
		}
		highlightMap.put(getNextColor(),IndexFinder.findIndices(theDoc, noLetter));
	}
	
	public void SPECIFIC_MISSPELLED_WORDS(String word){
		String theDoc = document;
		highlightMap.put(getNextColor(), IndexFinder.findIndices(theDoc, word));
	}
	
	/**
	 * 
	 * @param posBigram should be the exact string taken from 'stringInBraces'
	 */
	public void SPECIFIC_POS_BIGRAMS(String posBigram){ 
		String theDoc = document;
		LinkedList<String> wordsToHighlight = new LinkedList<String>();
		MaxentTagger mt= null;
		try {
			mt = new MaxentTagger("./external/MaxentTagger/left3words-wsj-0-18.tagger");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String beenTagged = mt.tagString(theDoc);
		//System.out.println(beenTagged);
		String theTags = posBigram.replaceAll("\\p{C}", " ");
		//System.out.println(theTags);
		int startTag = theTags.indexOf("(")+1;
		int endTag = theTags.indexOf(")"); 
		String tagOne = theTags.substring(startTag,endTag);
		String tagTwo = theTags.substring(theTags.indexOf("(",startTag)+1,theTags.indexOf(")",endTag+1));
		String safeTagOne=tagOne;
		String safeTagTwo=tagTwo;
		if(safeTagOne.contains("$"))
			safeTagOne = safeTagOne.replace("$", "\\$");
		if(safeTagOne.contains("."))
			safeTagOne = safeTagOne.replace(".", "\\.");
		if(safeTagTwo.contains("$"))
			safeTagTwo = safeTagTwo.replace("$","\\$");
		if(safeTagTwo.contains("."))
			safeTagTwo = safeTagTwo.replace(".", "\\.");
		
		System.out.println("The POS tags are: "+tagOne+" (safe is: "+safeTagOne+") and "+tagTwo+" (safe is: "+safeTagTwo+")");
		
		int start = -1;
		Pattern posTag = Pattern.compile("(\\s|\\b)[^\\s]+/"+safeTagOne+"(\\b|\\s)[^\\s]+/"+safeTagTwo+"(\\b|\\s)");
		Matcher wordFinder = posTag.matcher(beenTagged);
		int secondIndex;
		double numFound = 0;
		boolean isFound;
		while(true){
			isFound = wordFinder.find(start+1);
			if(isFound == false)
				break;
			numFound += 1;
			if(wordFinder.group(1).matches("\\s"))
				start=wordFinder.start()+1;
			else
				start=wordFinder.start();
			secondIndex = beenTagged.indexOf("/"+tagOne,start);
			String wordOne = beenTagged.substring(start,secondIndex);
			String wordTwo = beenTagged.substring(secondIndex+2+tagOne.length(),beenTagged.indexOf("/"+tagTwo,secondIndex));
			System.out.println(wordOne+" & "+wordTwo);
			wordsToHighlight.push(wordOne);
			wordsToHighlight.push(wordTwo);
			//TheTags thisTag = POSmap.TheTags.valueOf(theTag);
			//System.out.print("    ---> "+POSmap.tagToDescription(thisTag));
			
		}	
		
		
		String tagOneDef;
		String tagTwoDef;
		try{
			tagOneDef= POS.tagToDescription(TheTags.valueOf(tagOne));
		} catch(IllegalArgumentException iae){
			tagOneDef = "punctuation/symbol";
		}
		try{
			tagTwoDef= POS.tagToDescription(TheTags.valueOf(tagTwo));
		} catch(IllegalArgumentException iae){
			tagTwoDef = "punctuation/symbol";
		}
		
		
		highlightMap.put(getNextColor(),IndexFinder.findWordBigramIndices(theDoc, wordsToHighlight));
	}
	
	
	public void SPECIFIC_POS_TAGS(String inPosTag){
		String theDoc = document;
		LinkedList<String> wordsToHighlight = new LinkedList<String>();
		MaxentTagger mt= null;
		try {
			mt = new MaxentTagger("./external/MaxentTagger/left3words-wsj-0-18.tagger");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String beenTagged = mt.tagString(theDoc);
		//System.out.println(beenTagged);
		String theTag = inPosTag.replaceAll("\\p{C}", " ");
		String safeTag = "";
		if(theTag.contains("$"))
			safeTag = theTag.replace("$", "\\$");
		else
			safeTag= theTag;
		
		System.out.println("The POS tag is: "+theTag);
		int start = -1;
		Pattern posTag = Pattern.compile("(\\s|\\b)[^\\s]+/"+safeTag+"(\\b|\\s)");
		Matcher wordFinder = posTag.matcher(beenTagged);
		double numFound = 0;
		boolean isFound;
		while(true){
			isFound = wordFinder.find(start+1);
			if(isFound == false)
				break;
			numFound+= 1;
			if(wordFinder.group(1).matches("\\s"))
				start=wordFinder.start()+1;
			else
				start=wordFinder.start();
			System.out.println(beenTagged.substring(start,beenTagged.indexOf("/"+theTag,start)));
			wordsToHighlight.push(beenTagged.substring(start,beenTagged.indexOf("/"+theTag,start)));
			//TheTags thisTag = POSmap.TheTags.valueOf(theTag);
			//System.out.print("    ---> "+POSmap.tagToDescription(thisTag));
			
		}	
		
		String tagDef;
		try{
			tagDef= POS.tagToDescription(TheTags.valueOf(theTag));
		} catch(IllegalArgumentException iae){
			tagDef = "punctuation/symbol";
		}
		if(tagDef.equals("punctuation/symbol"))
			highlightMap.put(getNextColor(), IndexFinder.findSymbolIndices(theDoc, wordsToHighlight));
		else
			highlightMap.put(getNextColor(),IndexFinder.findIndices(theDoc, wordsToHighlight));
	}
	
	public void SPECIFIC_POS_TRIGRAMS(String posTrigram){
		String theDoc = document;
		LinkedList<String> wordsToHighlight = new LinkedList<String>();
		MaxentTagger mt= null;
		try {
			mt = new MaxentTagger("./external/MaxentTagger/left3words-wsj-0-18.tagger");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String beenTagged = mt.tagString(theDoc);
		//System.out.println(beenTagged);
		String theTags = posTrigram.replaceAll("\\p{C}", " ");
		//System.out.println(theTags);
		int startTag = theTags.indexOf("(")+1;
		int endTag = theTags.indexOf(")"); 
		String tagOne = theTags.substring(startTag,endTag);
		int startTagTwo = theTags.indexOf("(",startTag)+1;
		int endTagTwo = theTags.indexOf(")",endTag+1);
		String tagTwo = theTags.substring(startTagTwo,endTagTwo);
		String tagThree = theTags.substring(theTags.indexOf("(",startTagTwo)+1,theTags.indexOf(")",endTagTwo+1));
		String safeTagOne=tagOne;
		String safeTagTwo=tagTwo;
		String safeTagThree=tagThree;
		if(safeTagOne.contains("$"))
			safeTagOne = safeTagOne.replace("$", "\\$");
		if(safeTagOne.contains("."))
			safeTagOne = safeTagOne.replace(".", "\\.");
		if(safeTagTwo.contains("$"))
			safeTagTwo = safeTagTwo.replace("$","\\$");
		if(safeTagTwo.contains("."))
			safeTagTwo = safeTagTwo.replace(".", "\\.");
		if(safeTagThree.contains("$"))
			safeTagThree = safeTagThree.replace("$","\\$");
		if(safeTagThree.contains("."))
			safeTagThree = safeTagThree.replace(".", "\\.");
		
		System.out.println("The POS tags are: "+tagOne+" (safe is: "+safeTagOne+") and "+tagTwo+" (safe is: "+safeTagTwo+") and "+tagThree+" safe is: "+safeTagThree+")");
		
		int start = -1;
		Pattern posTag = Pattern.compile("(\\s|\\b)[^\\s]+/"+safeTagOne+"(\\b|\\s)[^\\s]+/"+safeTagTwo+"(\\b|\\s)[^\\s]+/"+safeTagThree+"(\\b|\\s)");
		Matcher wordFinder = posTag.matcher(beenTagged);
		int secondIndex;
		int thirdIndex;
		double numFound = 0;
		boolean isFound;
		while(true){
			isFound = wordFinder.find(start+1);
			if(isFound == false)
				break;
			numFound += 1;
			if(wordFinder.group(1).matches("\\s"))
				start=wordFinder.start()+1;
			else
				start=wordFinder.start();
			
			secondIndex = beenTagged.indexOf("/"+tagOne,start);
			String wordOne = beenTagged.substring(start,secondIndex);
			
			thirdIndex = beenTagged.indexOf("/"+tagTwo,secondIndex);
			String wordTwo = beenTagged.substring(secondIndex+2+tagOne.length(),thirdIndex);
			
			String wordThree = beenTagged.substring(thirdIndex+2+tagTwo.length(),beenTagged.indexOf("/"+tagThree,thirdIndex));
			
			System.out.println(wordOne+" & "+wordTwo+" & "+wordThree);
			wordsToHighlight.push(wordOne);
			wordsToHighlight.push(wordTwo);
			wordsToHighlight.push(wordThree);
			//TheTags thisTag = POSmap.TheTags.valueOf(theTag);
			//System.out.print("    ---> "+POSmap.tagToDescription(thisTag));
			
		}	
		
		String tagOneDef;
		String tagTwoDef;
		String tagThreeDef;
		try{
			tagOneDef= POS.tagToDescription(TheTags.valueOf(tagOne));
		} catch(IllegalArgumentException iae){
			tagOneDef = "punctuation/symbol";
		}
		try{
			tagTwoDef= POS.tagToDescription(TheTags.valueOf(tagTwo));
		} catch(IllegalArgumentException iae){
			tagTwoDef = "punctuation/symbol";
		}
		try{
			tagThreeDef= POS.tagToDescription(TheTags.valueOf(tagThree));
		} catch(IllegalArgumentException iae){
			tagThreeDef = "punctuation/symbol";
		}
		
		
		highlightMap.put(getNextColor(),IndexFinder.findWordTrigramIndices(theDoc, wordsToHighlight));
		
	}
	
	public void SPECIFIC_LETTER_BIGRAMS(String letterBigram){
		ArrayList<int[]> toHighlight = findLetterIndices(letterBigram);
		Color highlightColor = getNextColor();
		highlightMap.put(highlightColor,toHighlight);
	}
	
	public void SPECIFIC_LETTER_TRIGRAMS(String letterTrigram){
		ArrayList<int[]> toHighlight = findLetterIndices(letterTrigram);
		Color highlightColor = getNextColor();
		highlightMap.put(highlightColor,toHighlight);
	}
	
	public void UNIQUE_WORDS(){
		String theDoc = document; 
        String cleanDoc = theDoc.replaceAll("[\\r\\n\\t*.?!\",;()\\[\\]\\\\]", " ");
        
        StringTokenizer tok = new StringTokenizer(cleanDoc);
        HashMap<String,Integer> theWords = new HashMap<String,Integer>();
        while( tok.hasMoreTokens() )
        {
            String thisWord = tok.nextToken();
            if( theWords.containsKey(thisWord) ){
            		int temp = theWords.get(thisWord)+1;
            		theWords.put(thisWord, temp+1);
            } 
            else
            		theWords.put(thisWord,1);
        }
        int numWords = theWords.size();
        Set<String> wordSet = theWords.keySet();
        Iterator<String> wsIter = wordSet.iterator();
        UniqueWord[] uw = new UniqueWord[numWords];
        int index = 0;
        LinkedList<String> theWordsToHighlight = new LinkedList<String>();
        while(wsIter.hasNext()){
        		String key = wsIter.next();
        		Integer value = theWords.get(key);
        		if (value == 1)
        			theWordsToHighlight.push(key);
        }
        
       
    		ArrayList<int[]> theOnesToHighlight = IndexFinder.findIndices(cleanDoc, theWordsToHighlight);
    		highlightMap.put(getNextColor(), theOnesToHighlight);
	}	
	
	
	public void REPEATED_WORDS(){
		String theDoc = document; 
        String cleanDoc = theDoc.replaceAll("[\\r\\n\\t*.?!\",;()\\[\\]\\\\]", " ");
        
        StringTokenizer tok = new StringTokenizer(cleanDoc);
        
        HashMap<String,Integer> theWords = new HashMap<String,Integer>();
        while( tok.hasMoreTokens() )
        {
            String thisWord = tok.nextToken();
            if( theWords.containsKey(thisWord) ){
            		int temp = theWords.get(thisWord)+1;
            		theWords.put(thisWord, temp+1);
            } 
            else
            		theWords.put(thisWord,1);
        }
        int numWords = theWords.size();
        Set<String> wordSet = theWords.keySet();
        Iterator<String> wsIter = wordSet.iterator();
        UniqueWord[] uw = new UniqueWord[numWords];
        int numOccurs = 0;
        LinkedList<String> theWordsToHighlight = new LinkedList<String>();
        while(wsIter.hasNext()){
        		String key = wsIter.next();
        		Integer value = theWords.get(key);
        		if(value > 1){
        			numOccurs = value;
        			while (numOccurs > 0){
        				theWordsToHighlight.push(key);
        				numOccurs --;
        			}
		    		
        		}
        }
        ArrayList<int[]> theOnesToHighlight = IndexFinder.findIndices(cleanDoc, theWordsToHighlight);
		highlightMap.put(getNextColor(), theOnesToHighlight);
		//theWordsToHighlight.clear();
        
	}
	
	public void SPECIFIC_WORD_BIGRAMS(String wordBigram){
		String theDoc = document;
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");

		String theWords = wordBigram.replaceAll("\\p{C}", " ");
		System.out.println(theWords);
		int startWord = theWords.indexOf("(")+1;
		int endWord = theWords.indexOf(")"); 
		String wordOne = theWords.substring(startWord,endWord);
		String wordTwo = theWords.substring(theWords.indexOf("(",startWord)+1,theWords.indexOf(")",endWord+1));
		
		if(wordOne.contains("."))
			wordOne = wordOne.replace(".","\\.");
		if(wordOne.contains("?"))
			wordOne = wordOne.replace("?","\\?");
		if(wordTwo.contains("."))
			wordTwo = wordTwo.replace(".","\\.");
		if(wordTwo.contains("?"))
			wordTwo = wordTwo.replace("?","\\?");
		
		highlightMap.put(getNextColor(), IndexFinder.findIndices(theDoc,"\\b"+wordOne+"\\s*"+wordTwo+"\\b{1}+"));
	}
	
	
	public void SPECIFIC_WORD_TRIGRAMS(String wordTrigram){
		String theDoc = document;
		theDoc = theDoc.replaceAll("[“”‘’„˚˙‚’‘`*$%@#~\\r\\n\\t.?!\",;:()\\[\\]\\\\]"," ");

		String theWords = wordTrigram.replaceAll("\\p{C}", " ");
		System.out.println(theWords);
		int startWord = theWords.indexOf("(")+1;
		int endWord = theWords.indexOf(")"); 
		String wordOne = theWords.substring(startWord,endWord);
		int startWordTwo = theWords.indexOf("(",startWord)+1;
		int endWordTwo = theWords.indexOf(")",endWord+1);
		String wordTwo = theWords.substring(startWordTwo,endWordTwo);
		String wordThree = theWords.substring(theWords.indexOf("(",startWordTwo)+1,theWords.indexOf(")",endWordTwo+1));
		if(wordOne.contains("."))
			wordOne = wordOne.replace(".","\\.");
		if(wordOne.contains("?"))
			wordOne = wordOne.replace("?","\\?");
		if(wordTwo.contains("."))
			wordTwo = wordTwo.replace(".","\\.");
		if(wordTwo.contains("?"))
			wordTwo = wordTwo.replace("?","\\?");
		if(wordThree.contains("."))
			wordThree = wordThree.replace(".","\\.");
		if(wordThree.contains("."))
			wordThree = wordThree.replace("?","\\?");
		
		highlightMap.put(getNextColor(), IndexFinder.findIndices(theDoc,"\\b"+wordOne+"\\s*"+wordTwo+"\\s*"+wordThree+"\\b{1}+"));
	}
	
	public void SPECIFIC_WORDS(String word){
		String theDoc = document;
		highlightMap.put(getNextColor(), IndexFinder.findIndices(theDoc,"\\b"+word+"\\b"));
	}
	
}
//	
//	/**
//	 * Holds a single unique word, with number of appearances
//	 * @author Andrew W.E. McDonald
//	 *
//	 */
//	class UniqueWord{
//		private String word;
//		private Integer count;
//		
//		/**
//		 * Constructor
//		 * @param word the word
//		 * @param count number of appearances
//		 */
//		UniqueWord(String word, Integer count){
//			this.word = word;
//			this.count = count;
//		}
//		
//		/**
//		 * Returns the word
//		 * @return
//		 *  the word
//		 */
//		public String getWord(){
//			return word;
//		}
//		
//		/**
//		 * Returns the count (number of appearances of the word)
//		 * @return
//		 *  number of appearances
//		 */
//		public Integer getCount(){
//			return count;
//		}
//	}
