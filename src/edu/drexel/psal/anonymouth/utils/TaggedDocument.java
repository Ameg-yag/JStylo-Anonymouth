package edu.drexel.psal.anonymouth.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jgaap.JGAAPConstants;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.DataAnalyzer;
import edu.drexel.psal.anonymouth.gooie.ErrorHandler;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;

enum TENSE {PAST,PRESENT,FUTURE};

enum POV {FIRST_PERSON,SECOND_PERSON,THIRD_PERSON};

enum CONJ {SIMPLE,PROGRESSIVE,PERFECT,PERFECT_PROGRESSIVE};

/**
 * 
 * @author Andrew W.E. McDonald
 * @author Joe Muoio
 * 
 */
public class TaggedDocument implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2258415935896292619L;
	private final String NAME = "( "+this.getClass().getSimpleName()+" ) - ";
	protected TaggedSentence currentLiveTaggedSentences;
	protected ArrayList<TaggedSentence> taggedSentences;
	//protected ArrayList<String> untaggedSentences;
	private static final Pattern EOS_chars = Pattern.compile("(([?!]+)|([.]){1})\\s*");
	
	protected String documentTitle = "None";
	protected String documentAuthor = "None";
	protected ArrayList<ArrayList<TENSE>> tenses;
	protected ArrayList<ArrayList<POV>> pointsOfView;
	protected ArrayList<ArrayList<CONJ>> conjugations;
	protected List<List<? extends HasWord>> sentencesPreTagging;
	protected transient Iterator<List<? extends HasWord>> preTagIterator;
	protected transient TreebankLanguagePack tlp = new PennTreebankLanguagePack(); 
	protected transient List<? extends HasWord> sentenceTokenized;
	protected transient Tokenizer<? extends HasWord> toke;
	protected final int PROBABLE_NUM_SENTENCES = 50;
	protected SentenceTools jigsaw;
	protected transient Iterator<String> strIter;
	private String ID; 
	private int totalSentences=0;
	private double baseline_percent_change_needed = 0; // This may end up over 100%. That's unimportant. This is used to gauge the change that the rest of the document needs -- this is normalized to 100%, effectivley.
	private boolean can_set_baseline_percent_change_needed = true;
	public EOSCharacterTracker eosTracker;

	/**
	 * Constructor for TaggedDocument
	 */
	public TaggedDocument(){
		jigsaw = new SentenceTools();
		eosTracker = new EOSCharacterTracker();
		taggedSentences = new ArrayList<TaggedSentence>(PROBABLE_NUM_SENTENCES);
		//currentLiveTaggedSentences = new ArrayList<TaggedSentence>(5); // Most people probably won't try to edit more than 5 sentences at a time.... if they do... they'll just have to wait for the array to grow.
	}
	
	/**
	 * Constructor for TaggedDocument, accepts an untagged string (a whole document), and makes sentence tokens out of it.
	 * @param untaggedDocument
	 */
	public TaggedDocument(String untaggedDocument){
		jigsaw = new SentenceTools();
		eosTracker = new EOSCharacterTracker();
		taggedSentences = new ArrayList<TaggedSentence>(PROBABLE_NUM_SENTENCES);
		//currentLiveTaggedSentences = new ArrayList<TaggedSentence>(5); 
		makeAndTagSentences(untaggedDocument, true);
		//consolidateFeatures(taggedSentences);
		//setHashMaps();
		//setWordsToAddRemove();
	}
	 
	/**
	 * 
	 * @param untaggedDocument
	 * @param docTitle
	 * @param author
	 */
	public TaggedDocument(String untaggedDocument, String docTitle, String author){
		this.documentTitle = docTitle;
		this.documentAuthor = author;
		this.ID = documentTitle+"_"+documentAuthor;
		//Logger.logln(NAME+"TaggedDocument ID: "+ID);
	//	currentLiveTaggedSentences = new ArrayList<TaggedSentence>(5); 
		jigsaw = new SentenceTools();
		taggedSentences = new ArrayList<TaggedSentence>(PROBABLE_NUM_SENTENCES);
		makeAndTagSentences(untaggedDocument, true);
		//consolidateFeatures(taggedSentences);
		//setHashMaps();
		//setWordsToAddRemove();
		//Logger.logln(NAME+"Top 100 wordsToRemove: "+wordsToRemove.toString());
	}
	
	/**
	 * returns the number of Words in the TaggedDocument
	 * @return
	 */
	public int getWordCount(){
		int wordCount = 0;
		for(TaggedSentence ts:taggedSentences){
			wordCount += ts.size();
		}
		return wordCount;
	}
	
	/**
	 * returns all Words in the TaggedDocument
	 * @return
	 */
	public ArrayList<Word> getWords(){
		int numWords = getWordCount();
		ArrayList<Word> theWords = new ArrayList<Word>(numWords);
		for(TaggedSentence ts: taggedSentences){
			theWords.addAll(ts.wordsInSentence);
		}
		return theWords;
	}
	
	/**
	 * consolidates features for an ArrayList of TaggedSentences (does both word level and sentence level features)
	 * @param alts
	 */
	public void consolidateFeatures(ArrayList<TaggedSentence> alts){
		
		for(TaggedSentence ts:alts){
			ConsolidationStation.featurePacker(ts);
		}
	}
		
	
	/**
	 * consolidates features for a single TaggedSentence object
	 * @param ts
	 */
	public void consolidateFeatures(TaggedSentence ts){
		ConsolidationStation.featurePacker(ts);
	}

		
	
	/**
	 * Takes a String of sentences (can be an entire document), breaks it up into individual sentences (sentence tokens), breaks those up into tokens, and then tags them (via MaxentTagger).
	 * Each tagged sentence is saved into a TaggedSentence object, along with its untagged counterpart.
	 * @param untagged String containing sentences to tag
	 * @param appendTaggedSentencesToGlobalArrayList if true, appends the TaggedSentence objects to the TaggedDocument's arraylist of TaggedSentences
	 * @return the TaggedSentences
	 */
	public ArrayList<TaggedSentence> makeAndTagSentences(String untagged, boolean appendTaggedSentencesToGlobalArrayList){
		ArrayList<String> untaggedSent = jigsaw.makeSentenceTokens(untagged); // IMPORTANT TODO update this to set EOS characters to different symbols to allow people to highlight things that were broken up into more than one sentence and select, "make this one sentence"
		ArrayList<TaggedSentence> taggedSentences = new ArrayList<TaggedSentence>(untaggedSent.size());
		//sentencesPreTagging = new ArrayList<List<? extends HasWord>>();
		strIter = untaggedSent.iterator();
		String tempSent;
		while(strIter.hasNext()){
			tempSent = strIter.next();
			TaggedSentence taggedSentence = new TaggedSentence(tempSent);
			toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(tempSent));
			sentenceTokenized = toke.tokenize();
			taggedSentence.setTaggedSentence(Tagger.mt.tagSentence(sentenceTokenized));
			consolidateFeatures(taggedSentence);
			
			// todo: put stuff here
			taggedSentences.add(taggedSentence); 
			
		}
		if(appendTaggedSentencesToGlobalArrayList == true){
			int i = 0;
			int len = taggedSentences.size();
			for(i=0;i<len;i++){
				totalSentences++;
				this.taggedSentences.add(taggedSentences.get(i)); 
			}
		}
		return taggedSentences;
	}
	
	/**
	 * returns the ArrayList of TaggedSentences
	 * @return
	 */
	public ArrayList<TaggedSentence> getTaggedDocument(){
		return taggedSentences;
	}
		

	/**
	 * returns the untagged sentences of the TaggedDocument
	 * @return
	 */
	public ArrayList<String> getUntaggedSentences()
	{
		ArrayList<String> sentences = new ArrayList<String>();
		for (int i=0;i<taggedSentences.size();i++)
			sentences.add(taggedSentences.get(i).getUntagged());
		return sentences;
	}
	
	

	/**
	 * adds the next sentence to the current one.
	 * @param The sentenceEditBox text
	 * @return the concatenation of the current sentence and the next sentence.
	 */
//	public String addNextSentence(String boxText) {
//		if(sentNumber <totalSentences-1 && sentNumber>=0){
//			//have to add the next sentence to this one otherwise the appended sentence will not be taken into calculations.
//			totalSentences--;
//			ArrayList<TaggedSentence> tempTaggedSentences=new ArrayList<TaggedSentence>(2);
//			tempTaggedSentences.add(taggedSentences.get(sentNumber));
//			tempTaggedSentences.add(taggedSentences.get(sentNumber+1));
//			currentLiveTaggedSentences=concatSentences(tempTaggedSentences);
//			
//			TaggedSentence newSent= new TaggedSentence(boxText);
//			int position=0;
//			while(position<boxText.length()){
//				Matcher sent = EOS_chars.matcher(boxText);
//				if(!sent.find(position)){//checks to see if there is a lack of an end of sentence character.
//					Logger.logln(NAME+"User tried submitting an incomplete sentence.");//THIS DOES NOT KEEP TAGS. 
//					//--------------------This is because you cannot pass in incomplete sent to parser
//					TaggedSentence tagSentNext=removeTaggedSentence(sentNumber+1);
//					removeTaggedSentence(sentNumber);
//					newSent.untagged=newSent.getUntagged()+tagSentNext.getUntagged();
//					addTaggedSentence(newSent,sentNumber);//--------possible improvement needed to parser?-----
//					//ErrorHandler.incompleteSentence();
//					//for(int i=0;i<currentLiveTaggedSentences.size();i++)
//					
//					Logger.logln(NAME+currentLiveTaggedSentences.untagged);
//					updateReferences(currentLiveTaggedSentences,newSent);
//					
//					return newSent.getUntagged();
//				}
//				position=sent.end();
//			}
//			ArrayList<TaggedSentence> taggedSents=makeAndTagSentences(boxText,false);
//			TaggedSentence nextSent=taggedSentences.remove(sentNumber+1);
//			taggedSents.add(nextSent);
//			taggedSentences.remove(sentNumber);
//			newSent=concatSentences(taggedSents);
//			taggedSentences.add(sentNumber, newSent);
//			
//			updateReferences(currentLiveTaggedSentences,newSent);
//			
//			//for(int i=0;i<currentLiveTaggedSentences.size();i++)
//			Logger.logln(NAME+currentLiveTaggedSentences.untagged);
//			return newSent.getUntagged();
//		}
//		if(sentNumber<0){
//			sentNumber=0;
//		}
//		//currentLiveTaggedSentences=taggedSentences.get(sentNumber);
//		//for(int i=0;i<currentLiveTaggedSentences.size();i++)
//		Logger.logln(NAME+currentLiveTaggedSentences.untagged);
//		return taggedSentences.get(sentNumber).getUntagged();
//	}
//	
	/**
	 * updates the referenced Attributes 'toModifyValue's (present value) with the amount that must be added/subtracted from each respective value 
	 * @param oldSentence The pre-editing version of the sentence(s)
	 * @param newSentence The post-editing version of the sentence(s)
	 */
	private void updateReferences(TaggedSentence oldSentence, TaggedSentence newSentence){
		//Logger.logln(NAME+"Old Sentence: "+oldSentence.toString()+"\nNew Sentence: "+newSentence.toString());
		SparseReferences updatedValues = newSentence.getOldToNewDeltas(oldSentence);
		//Logger.logln(NAME+updatedValues.toString());
		for(Reference ref:updatedValues.references){
			//Logger.logln(NAME+"Attribute: "+DataAnalyzer.topAttributes[ref.index].getFullName()+" pre-update value: "+DataAnalyzer.topAttributes[ref.index].getToModifyValue());
			if(DataAnalyzer.topAttributes[ref.index].getFullName().contains("Percentage")){
				//then it is a percentage.
				Logger.logln(NAME+"Attribute: "+DataAnalyzer.topAttributes[ref.index].getFullName()+"Is a percentage! ERROR!",Logger.LogOut.STDERR);
			}
			else if(DataAnalyzer.topAttributes[ref.index].getFullName().contains("Average")){
				//then it is an average
				Logger.logln(NAME+"Attribute: "+DataAnalyzer.topAttributes[ref.index].getFullName()+"Is an average! ERROR!",Logger.LogOut.STDERR);
			}
			else{
				DataAnalyzer.topAttributes[ref.index].setToModifyValue((DataAnalyzer.topAttributes[ref.index].getToModifyValue() + ref.value));
				//Logger.logln(NAME+"Updated attribute: "+DataAnalyzer.topAttributes[ref.index].getFullName());
			}
				
			//Logger.logln(NAME+"Attribute: "+DataAnalyzer.topAttributes[ref.index].getFullName()+" post-update value: "+DataAnalyzer.topAttributes[ref.index].getToModifyValue());
		}
	}
	
	
	
	/**
	 * accepts a variable number of TaggedSentences and returns a single TaggedSentence, preserving all original Word objects
	 * @param taggedSentences a variable number of TaggedSentences
	 * @return returns a single tagged sentences with the properties of all the sentences in the list.
	 */
	private TaggedSentence concatSentences(TaggedSentence ... taggedSentences){//ArrayList<TaggedSentence> taggedList){
		TaggedSentence toReturn =new TaggedSentence(taggedSentences[0]);
		int numSents = taggedSentences.length;
		int i, j;
		for (i=1;i<numSents;i++){
				toReturn.wordsInSentence.addAll(taggedSentences[i].wordsInSentence);
				toReturn.untagged += taggedSentences[i].untagged;
		}
		return toReturn;
	}
	
	/**
	 * accepts an ArrayList of TaggedSentences and returns a single TaggedSentence, preserving all original Word objects
	 * @param taggedSentences an ArrayList of TaggedSentences
	 * @return returns a single tagged sentences with the properties of all the sentences in the list.
	 */
	private TaggedSentence concatSentences(ArrayList<TaggedSentence> taggedSentences){//ArrayList<TaggedSentence> taggedList){
		TaggedSentence toReturn =new TaggedSentence(taggedSentences.get(0));
		int numSents = taggedSentences.size();
		int i, j;
		TaggedSentence thisTaggedSent;
		for (i=1;i<numSents;i++){
			thisTaggedSent = taggedSentences.get(i);
			toReturn.wordsInSentence.addAll(thisTaggedSent.wordsInSentence);
			toReturn.untagged += thisTaggedSent.untagged;
			toReturn.sentenceLevelFeaturesFound.merge(thisTaggedSent.sentenceLevelFeaturesFound);
		}
		return toReturn;
	}	
	
	
	/**
	 * Returns an integer array with the lengths of each sentence (TaggedSentence) in this TaggedDocument. 
	 * Array indices are such that index '0' holds the length of the first sentence, index '1' holds the length of the second sentence, ect..
	 * @return
	 */
	public int[] getSentenceLengths(){
		int i =0;
		int numSents = taggedSentences.size();
		int[] lengthsToReturn = new int[numSents];
		for(i = 0; i < numSents; i++){
			lengthsToReturn[i] = taggedSentences.get(i).getLength();
		}
		return lengthsToReturn;
	}
	
	/**
	 * returns TaggedSentence number 'i' (first sentence is index '0')
	 * @param number the index of the sentence you want 
	 * @return
	 */
	public TaggedSentence getSentenceNumber(int number){
		return taggedSentences.get(number);
	}
		
	
	/**
	 * returns the size of the ArrayList holding the TaggedSentences (i.e. the number of sentences in the document)
	 * @return
	 */
	public int getNumSentences(){
		return taggedSentences.size();
	}

	/**
	 * Adds sentToAdd at placeToAdd in this TaggedDocument
	 * @param sentToAdd
	 * @param placeToAdd
	 */
	public void addTaggedSentence(TaggedSentence sentToAdd, int placeToAdd){
		taggedSentences.add(placeToAdd,sentToAdd);
	}
	
	/**
	 * removes TaggedSentence at indexToRemove from this TaggedDocument
	 * @param indexToRemove
	 * @return the removed TaggedSentence
	 */
	public TaggedSentence removeTaggedSentence(int indexToRemove){
		return taggedSentences.remove(indexToRemove);
	}

	
	/**
	 * 
	 * @param sentsToAdd a String representing the sentence(s) from the editBox
	 * @return 1 if everything worked as expected. 0 if user deleted a sentence. -1 if user submitted an incomplete sentence
	 */
	public int removeAndReplace(int sentNumber, String sentsToAdd){//, int indexToRemove, int placeToAdd){
		TaggedSentence toReplace = taggedSentences.get(sentNumber);
		Logger.logln(NAME+"removing: "+toReplace.toString());
		Logger.logln(NAME+"adding: "+sentsToAdd);
		if(sentsToAdd.matches("\\s*")){//checks to see if the user deleted the current sentence
			//CALL COMPARE
			removeTaggedSentence(sentNumber);
			Logger.logln(NAME+"User deleted a sentence.");
			updateReferences(toReplace,new TaggedSentence(""));//all features must be deleted
			totalSentences--;
			return 0;
		}
		ArrayList<TaggedSentence> taggedSentsToAdd = makeAndTagSentences(sentsToAdd,false);
		Scanner s = new Scanner(System.in);
		removeTaggedSentence(sentNumber);
		addTaggedSentence(taggedSentsToAdd.get(0),sentNumber);
		//call compare
		int i;
		int len = taggedSentsToAdd.size();
		for(i=1;i<len;i++){
			sentNumber++;
			//removeTaggedSentence(sentNumber);
			addTaggedSentence(taggedSentsToAdd.get(i),sentNumber);
			totalSentences++;
		}
		TaggedSentence concatted = concatSentences(taggedSentsToAdd);
		System.out.println("TaggedSent to add: "+taggedSentsToAdd.get(0).toString());
		System.out.println("TaggedSent to remove: "+toReplace.toString());
		updateReferences(toReplace,concatted);
		return 1;
		
	}
	
	/**
	 * Returns the ArrayList holding all TaggedSentences in this TaggedDocument
	 * @return
	 */
	public ArrayList<TaggedSentence> getTaggedSentences(){
		return taggedSentences;
	}
	
	/**
	 * Returns the TaggedDocument as a string
	 * @return
	 */
	public String getUntaggedDocument(){
		String str = "";
		for (int i=0;i<totalSentences;i++){
			str+=taggedSentences.get(i).getUntagged();
		}
		return str;
	}
	
	public String getUntaggedDocument(boolean returnSubEOS){
		String str = "";
		if (returnSubEOS){
			for (int i=0;i<totalSentences;i++){
				str+=taggedSentences.get(i).getUntagged(true);
			}
		}
		else{
			for (int i=0;i<totalSentences;i++){
				str+=taggedSentences.get(i).getUntagged();
			}
		}
		return str;
			
	}
	
	/**
	 * Loops through all topAttribute Attributes in DataAnalyzer, and returns the average percent change needed. This is a first stab at some
	 * way to deliver a general sense of the degree of anonymity achived at any given point. This method must be called before any changes are made to set 
	 * a baseline percent change. That number is what everything from that point on gets compared (normalized) to. 
	 * 
	 * It is important to note that this does not take into consideration the information gain of any feature. So, the less important features will have the same effect on this number
	 * as the most important features. This should probably change...
	 * @param is_initial 'true' if this is the first time the function is being called for this document (basically, if you are calling it to set the document's baseline percent change needed, this should be true. If you want to know how much the document has changed, this should be false. This will be false all the time, except for the first time it's called).
	 * @return
	 * The overall percent change that is needed. 
	 */
	public double getAvgPercentChangeNeeded(boolean is_initial){
		int total_attribs = 0;
		double total_percent_change = 0;
		for (Attribute attrib : DataAnalyzer.topAttributes){
			total_percent_change += Math.abs(attrib.getPercentChangeNeeded());
			total_attribs ++;
		}
		double avg_percent_change = total_percent_change/total_attribs;
		if (is_initial)
			return avg_percent_change;
		else{
			double percent_change_needed = 1 - (Math.abs(avg_percent_change - baseline_percent_change_needed)/baseline_percent_change_needed);
			return percent_change_needed;
		}
	}
	
	/**
	 * Sets baseline_percent_change_needed. This is the ONLY time that 'getAvgPercentChangeNeeded' will be called with 'true'.
	 */
	public void setBaselinePercentChangeNeeded(){
		if (can_set_baseline_percent_change_needed){
			baseline_percent_change_needed = getAvgPercentChangeNeeded(true);
			can_set_baseline_percent_change_needed = false;
		}
	}
	
	public String toString(){
		String toReturn = "Document Title: "+documentTitle+" Author: "+documentAuthor+"\n";
		int len = taggedSentences.size();
		int i =0;
		for(i=0;i<len;i++){
			toReturn += taggedSentences.get(i).toString()+"\n";
		}
		return toReturn;
	}
	
	/*
	public static void main(String[] args){
		String text1 = "people enjoy coffee, especially in the mornings, because it helps to wake me up. My dog is fairly small, but she seems not to realize it when she is around bigger dogs. This is my third testing sentence. I hope this works well.";
		TaggedDocument testDoc = new TaggedDocument(text1);
		System.out.println(testDoc.toString());			
		//System.out.println(testDoc.getFunctionWords());
		
	}
	*/
	
	
}
	
