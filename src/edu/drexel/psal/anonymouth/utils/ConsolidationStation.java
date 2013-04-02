package edu.drexel.psal.anonymouth.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import edu.drexel.psal.anonymouth.projectDev.Attribute;
import edu.drexel.psal.anonymouth.projectDev.DataAnalyzer;
import edu.drexel.psal.anonymouth.projectDev.FeatureList;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;
import edu.stanford.nlp.ling.TaggedWord;

/**
 * 
 * @author Andrew W.E. McDonald
 * @author Joe Muoio
 *
 */
public class ConsolidationStation {
	
	static HashMap<String,ArrayList<TreeData>> parsed;
	static ArrayList<Triple> toAdd=new ArrayList<Triple>(400);
	static ArrayList<Triple> toRemove=new ArrayList<Triple>(400);
	public static ArrayList<TaggedDocument> otherSampleTaggedDocs;//initialized in backendInterfaces.
	public static ArrayList<TaggedDocument> authorSampleTaggedDocs;
	public static ArrayList<TaggedDocument> toModifyTaggedDocs;//init in editor Tab Driver
	private static boolean allDocsTagged = false;
	public static FunctionWord functionWords=new FunctionWord();
	
	private HashMap<String,Word>wordsToAdd;
	private HashMap<String,Word>newWordsToAdd;
	private HashMap<String,Word>wordsInDocToMod;
	private HashMap<String,Word>wordsToRemove;
	
	/**
	 * constructor for ConsolidationStation. Depends on target values, and should not be called until they have been selected.
	 * @param attribs
	 * @param parsed
	 */
	public ConsolidationStation(){
		this.parsed = parsed;
		toAdd = new ArrayList<Triple>(400);
		toRemove = new ArrayList<Triple>(400);
		wordsToAdd=new HashMap<String,Word>();
		newWordsToAdd=new HashMap<String,Word>();
		wordsInDocToMod=new HashMap<String,Word>();
		wordsToRemove=new HashMap<String,Word>();
		
	}
	
	
	/**
	 * Starts the consolidation process
	 */
	public void beginConsolidation(){
		
		
	}
	
	public static void setAllDocsTagged(boolean allDocsTagged){
		ConsolidationStation.allDocsTagged = allDocsTagged;
	}
	
	/**
	 * Adds the features present to each word in the taggedSentence
	 * @param taggedSent the tagged Sentence with the Word List to update.
	 * @return the taggedSentence passed in.
	 */
	public static TaggedSentence featurePacker(TaggedSentence taggedSent){
		for(Word word:taggedSent.wordsInSentence){
			setWordFeatures(word);
		}
		return taggedSent;
	}
	
	
	public static void setWordFeatures(Word word){
		String wordString=word.word;
		int strSize=wordString.length(), tempNumber;
		int attribLen=DataAnalyzer.lengthTopAttributes;
		//for (Attribute attrib:attribs){
		for(int i=0;i<attribLen;i++){
			String stringInBrace=DataAnalyzer.topAttributes[i].getStringInBraces();
			int toAddLength=stringInBrace.length();
			if(toAddLength==0){
				//Logger.logln("THIS IS BAD",Logger.LogOut.STDERR);
			}
			else if(toAddLength<=strSize){//checks for a possible match
				tempNumber=0;
				for(int j=0;j<strSize-toAddLength;j++){
					if(wordString.substring(j, j+toAddLength).equals(stringInBrace)){
						tempNumber++;
					}
				}
				if(tempNumber>0){
					//add the feature to the word and have it appearing tempNumber times.
					//Logger.logln("AddNewReference from ConsolStation.featurePacker");
					//Logger.logln("Value i: "+i+" Value indexOf Attrib: "+DataAnalyzer.topAttributes[i].getIndexNumber()+" Attribute: "+DataAnalyzer.topAttributes[i].getFullName()+" the word: "+wordString);
					word.featuresFound.addNewReference(i, tempNumber);
					//Logger.logln("Added a feature: "+word.featuresFound.toString());
				}
			}
		}
	}
	
	/**
	 * Goes through all Words in all TaggedSentences in all TaggedDocuments, sorts them from least to greatest in terms of Anonymity Index, and returns either the lowest ranked or
	 * highest ranked percent as strings.
	 * NOTE:
	 * 	* percentToReturn, the percent of highest or lowest ranked words (String) to return, should be a number between 0 and 1.
	 * * if findTopToRemove is false, the highest ranked Words will be returned (as Strings) (these would then be the most important words to ADD to the documentToAnonymize)
	 * * if findTopToRemove is true, the lowest ranked Words will be returned (as String) (these would then be the most important words to REMOVE from the documentToAnonymize)
	 * @param docsToConsider the TaggedDocuments to extract Words from
	 * @param findTopToRemove true to find the top words to remove, false to find the top words to add
	 * @param percentToReturn the percent of words found to return (should probably be MUCH smaller if finding top words to add, because this will look at all otherSampleDocuments
	 * @return
	 */
	public static ArrayList<String> getPriorityWords(ArrayList<TaggedDocument> docsToConsider, boolean findTopToRemove, double percentToReturn){
		int totalWords = 0;
		ArrayList<Word> words = new ArrayList<Word>(totalWords);
		for(TaggedDocument td:docsToConsider){
			totalWords += td.getWordCount();
			words.addAll(td.getWords());
		}
		//System.out.println("-----------------------Printing word list-------------------------");
		/*for(Word w:words){
			System.out.println(w.toString());
		}*/
		
		int numToReturn = (int)(totalWords*percentToReturn);
		ArrayList<String> toReturn = new ArrayList<String>(numToReturn);
		words = removeDuplicateWords(words);
		
		Collections.sort(words);// sort the words in INCREASING anonymityIndex
		
		int mergedNumWords = words.size();
		if (mergedNumWords <= numToReturn){
			Logger.logln("The number of priority words to return is greater than the number of words available. Only returning what is available");
			numToReturn = mergedNumWords;
		}
		Word tempWord;
		if(findTopToRemove){ // then start from index 0, and go up to index (numToReturn-1) words (inclusive)]
			for(int i = 0; i<numToReturn; i++){
				System.out.println(words.get(i).word+" "+words.get(i).getAnonymityIndex()); 	
				if((tempWord=words.get(i)).getAnonymityIndex()<0)
					toReturn.add(tempWord.word);//+" ("+tempWord.getAnonymityIndex()+")");
				else 
					break;
			}
		}
		else{ // start at the END of the list, and go down to (END-numToReturn) (inclusive)
			System.out.println("GOt here");
			int startIndex = mergedNumWords - 1;
			int stopIndex = startIndex - numToReturn;
			for(int i = startIndex; i> stopIndex; i--){
				//System.out.println("Got here..");
				//System.out.println(words.get(i).word+" "+words.get(i).getAnonymityIndex());
				if((tempWord=words.get(i)).getAnonymityIndex()>0)
					toReturn.add(tempWord.word);//+" ("+tempWord.getAnonymityIndex()+")");
				else 
					break;
			}	
		}
		System.out.println(toReturn);
		return toReturn;
	}
	
	
	public static ArrayList<Word> removeDuplicateWords(ArrayList<Word> unMerged){
		HashMap<String,Word> mergingMap = new HashMap<String,Word>((unMerged.size()));//Guessing there will be at least an average of 3 duplicate words per word -> 1/3 of the size is needed
		for(Word w: unMerged){
			if(mergingMap.containsKey(w.word) == true){
				//Word temp = mergingMap.get(w.word);
				//temp.mergeWords(w);
				//mergingMap.put(w.word,temp);
				if(w.equals(mergingMap.get(w.word))){
					//check is sparse ref the same
					if(!w.featuresFound.equals(mergingMap.get(w.word).featuresFound)){
						Logger.logln("The featuresFound in the words are not equal.",Logger.LogOut.STDERR);
					}
				}
				else{
					Logger.logln("Problem in mergeWords--Words objects not equal",Logger.LogOut.STDERR);
				}
				
			}
			else{
				mergingMap.put(w.word,new Word(w));
			}
		}
		Set<String> mergedWordKeys = mergingMap.keySet();
		ArrayList<Word> mergedWords = new ArrayList<Word>(mergedWordKeys.size());
		for(String s: mergedWordKeys){
			mergedWords.add(mergingMap.get(s));
		}
		return mergedWords;
	}
	
	
	public static Word getWordFromString(String str){
		Word newWord=new Word(str);
		for (int i=0;i<toAdd.size();i++){//toaddList loop
			Logger.logln("TOADD: "+toAdd.get(i).getStringInBraces());
			int toAddLength=toAdd.get(i).getStringInBraces().length();
			if(toAddLength<=str.length()){//checks if it can be a possible match
				int tempNumber=0;
				double featureInfoGain=toAdd.get(i).getInfoGain();
				double featurePercentChange = toAdd.get(i).getInfoGain();
				for(int j=0;j<str.length()-toAddLength;j++){//loops through word to check if/howManyTimes the stringInBraces is found in the word.
					if(str.substring(j, j+toAddLength).equals((String)toAdd.get(i).stringInBraces)){
						tempNumber++;
					}
				}
				//newWord.adjustVals(tempNumber, featureInfoGain,featurePercentChange);
			}
		}
		for (int i=0;i<toRemove.size();i++){//toaddList loop
			Logger.logln("TOREMOVE: "+toRemove.get(i).getStringInBraces());
			int toAddLength=toRemove.get(i).getStringInBraces().length();
			if(toAddLength<=str.length()){//checks if it can be a possible match
				int tempNumber=0;
				double featureInfoGain=toRemove.get(i).getInfoGain();
				double featurePercentChange = toRemove.get(i).getInfoGain();
				for(int j=0;j<str.length()-toAddLength;j++){//loops through word to check if/howManyTimes the stringInBraces is found in the word.
					if(str.substring(j, j+toAddLength).equals((String)toRemove.get(i).stringInBraces)){
						tempNumber++;
					}
				}
				//newWord.adjustVals(tempNumber, featureInfoGain, featurePercentChange);//respresents a word to remove, so it should be negative
			}
		}
	//	Logger.logln("NEW WORD"+newWord.toString());
		return newWord;
	}
	
/*	
	 * runs through all attributes in attribs and pulls out the stringInBraces if it is there, and the percent (positive and negative)  
	 * change needed
	 * 
	 
	public static void findIndicesOfAttribsWithStringInBraces(){
		for(Attribute attrib:attribs){
			if (attrib.getCalcHist() == false)
				continue; // ignore single valued features
			String tempID;
			FeatureList feature;
			double tempInfoGain;
			feature = attrib.getGenericName();
			tempID = attrib.getStringInBraces();
			double tempPercentChange=attrib.getPercentChangeNeeded();
			tempInfoGain = attrib.getInfoGain();
			if (tempPercentChange > 0){
				Triple trip = new Triple(tempID,tempPercentChange,tempInfoGain);
				toAdd.add(trip);
			}
			else if(tempPercentChange < 0){
				Triple trip = new Triple(tempID,tempPercentChange,tempInfoGain);
				toRemove.add(trip);
			}
		}			
	}
*/	
	
/*
	public void findWordsToAdd(){//Only loops through otherSampleTaggedDocs.
		// TODO I think this should take a global (to this function) hashmap of String -> Word objects, and run through all features in the 'toAdd' list, checking them 
		// against each TaggedWord words in otherSampleTaggedWords. When it finds that one of the TaggedWord words contains the feature its checking, it should
		// find out how many times that feature appears in the TaggedWord word, and then:
			// If the hashmap contains the Word, read the value from the map, adjustVals, and replace it
			// else, create new entry in hashmap
		int taggedDocsIndex, toAddIndex;
		ArrayList<Runnable> threads=new ArrayList<Runnable>();
		for(toAddIndex=0;toAddIndex<toAdd.size();toAddIndex++){//loops through toAddTriple
			if(toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.WORD_BIGRAMS)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getWordBigrams(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getWordBigrams(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			else if(toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.WORD_TRIGRAMS)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getWordTrigrams(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getWordTrigrams(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			else if(toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.WORDS)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getWords(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getWords(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			else if(toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.POS_BIGRAMS)){//thread
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					HashMap<String,Word> tempHashMap=new HashMap<String,Word>();
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getPOSBigrams(),toAdd.get(toAddIndex),tempHashMap);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getPOSBigrams(),toAdd.get(toAddIndex),wordsInDocToMod);
					Runnable csHelper=new ConsolidationStationHelper(wordsToAdd, tempHashMap);
					threads.add(csHelper);
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.POS_TRIGRAMS)){//thread
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					HashMap<String,Word> tempHashMap=new HashMap<String,Word>();
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getPOSTrigrams(),toAdd.get(toAddIndex),tempHashMap);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getPOSTrigrams(),toAdd.get(toAddIndex),wordsInDocToMod);
					Runnable csHelper=new ConsolidationStationHelper(wordsToAdd, tempHashMap);
					threads.add(csHelper);
				}
			}
			else if(toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.POS_TAGS)){//thread
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
//					HashMap<String,Word> tempHashMap=new HashMap<String,Word>();
//					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getPOS(),toAdd.get(toAddIndex),tempHashMap);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getPOS(),toAdd.get(toAddIndex),wordsInDocToMod);
//					Runnable csHelper=new ConsolidationStationHelper(wordsToAdd, tempHashMap);
//					threads.add(csHelper);
				}
			}
			else if(toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.LETTERS)){//what is letterNGrams?
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					HashMap<String,Word> tempHashMap=new HashMap<String,Word>();
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getLetters(),toAdd.get(toAddIndex),tempHashMap);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getLetters(),toAdd.get(toAddIndex),wordsInDocToMod);
					Runnable csHelper=new ConsolidationStationHelper(wordsToAdd, tempHashMap);
					threads.add(csHelper);
				}
			}
			else if(toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.TOP_LETTER_BIGRAMS)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					HashMap<String,Word> tempHashMap=new HashMap<String,Word>();
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getLetterBigrams(),toAdd.get(toAddIndex),tempHashMap);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getLetterBigrams(),toAdd.get(toAddIndex),wordsInDocToMod);
					Runnable csHelper=new ConsolidationStationHelper(wordsToAdd, tempHashMap);
					threads.add(csHelper);
					
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.TOP_LETTER_TRIGRAMS)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					HashMap<String,Word> tempHashMap=new HashMap<String,Word>();
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getLetterTrigrams(),toAdd.get(toAddIndex),tempHashMap);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getLetterTrigrams(),toAdd.get(toAddIndex),wordsInDocToMod);
					Runnable csHelper=new ConsolidationStationHelper(wordsToAdd, tempHashMap);
					threads.add(csHelper);
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.FUNCTION_WORDS)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getFunctionWords(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getFunctionWords(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.PUNCTUATION)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getPunctuation(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getPunctuation(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.WORD_LENGTHS)){//thread
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					HashMap<Integer,Word> tempHashMap=new HashMap<Integer,Word>();
					findAttributeLength(otherSampleTaggedDocs.get(taggedDocsIndex).getWordLengths(),toAdd.get(toAddIndex),tempHashMap);
					Runnable csHelper=new ConsolidationStationHelper(wordsToAdd, tempHashMap,true);
					threads.add(csHelper);
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.DIGITS)){
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getDigits(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getDigits(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.MISSPELLED_WORDS)){
				//findMisspelledWords();
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getMisspelledWords(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getMisspelledWords(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			else if (toAdd.get(toAddIndex).getFeatureName().equals(FeatureList.SPECIAL_CHARACTERS)){
				//findSpecialChars();
				for(taggedDocsIndex=0;taggedDocsIndex<otherSampleTaggedDocs.size();taggedDocsIndex++){
					findAttribute(otherSampleTaggedDocs.get(taggedDocsIndex).getSpecialChars(),toAdd.get(toAddIndex),wordsToAdd);
					//findAttribute(toModifyTaggedDocs.get(taggedDocsIndex).getSpecialChars(),toAdd.get(toAddIndex),wordsInDocToMod);
				}
			}
			
		}
		ArrayList<Thread>startedThreads=new ArrayList<Thread>();
		for (int i=0;i<threads.size();i++){//starts threads
			Thread t=new Thread(threads.get(i));
			t.start();
			startedThreads.add(t);
		}
		for (int i=0;i<startedThreads.size();i++){
			try {
				startedThreads.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//compareMaps(wordsToAdd,wordsInDocToMod);
		Logger.logln("LOG OF CONSOLSTAT WORDSTOADD"+wordsToAdd.toString());
	}
	

	private void findAttribute(HashMap<String,Integer> hashMap,Triple toAddTriple,HashMap<String,Word> listToAddTo) {
		Iterator iter=hashMap.keySet().iterator();
		while(iter.hasNext()){
			Word newWord=new Word(hashMap.keySet().iterator().next());
			//newWord.mergeWords(hashMap.get(newWord.word).intValue(), toAddTriple.getInfoGain());
			addToHashMap(listToAddTo,newWord);
		}
	}
	
	private void findAttributeLength(HashMap<Integer,Integer> hashMap,Triple toAddTriple,HashMap<Integer,Word> listToAddTo) {
		Iterator iter=hashMap.keySet().iterator();
		while(iter.hasNext()){
			Integer integer=(Integer) iter.next();
			Word newWord=new Word(integer);
			//newWord.adjustVals(hashMap.get(newWord.word).intValue(), toAddTriple.getInfoGain());
			if (listToAddTo.containsKey(newWord.word)){
				//listToAddTo.get(newWord.word).adjustVals(newWord.rank, newWord.infoGainSum);
			}
			else{
				listToAddTo.put(integer, newWord);
			}
		}
	}
	
	private HashMap<String,Word> compareMaps(HashMap<String,Word> hashmap1, HashMap<String,Word>hashmap2){
		HashMap<String,Word> newHashMap=new HashMap<String,Word>();
		while(hashmap1.keySet().iterator().hasNext()){
			
		}	
		return newHashMap;
	}

	
	
	 * Checks to see if the wordToAdd exists in the map. If it doesn't then it adds it. Otherwise it updates the 
	 * word at that location to have more weight
	 * @param hashMap the hashmap to add the feature to.
	 * @param wordToAdd the word that is added
	 * 
	 
	
	public void addToHashMap(HashMap <String,Word> hashMap, Word wordToAdd){
		
		if (hashMap.containsKey(wordToAdd.word)){
			//hashMap.get(wordToAdd.word).adjustVals(wordToAdd.rank, wordToAdd.infoGainSum);
		}
		else{
			hashMap.put(wordToAdd.word, wordToAdd);
		}		
	}
	
	//Note: removed a lot of old code.
	
	//public void findWordsToRemove(){
		//TODO Should do the same as above, but ONLY with the toModifyTaggedDocs -- obviously in a separate hashmap.
	//}	
	*/
	
}
