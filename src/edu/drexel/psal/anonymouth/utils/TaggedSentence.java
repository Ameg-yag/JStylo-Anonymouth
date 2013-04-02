package edu.drexel.psal.anonymouth.utils;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.jgaap.JGAAPConstants;

import edu.stanford.nlp.ling.TaggedWord;
import edu.drexel.psal.anonymouth.projectDev.Attribute;
import edu.drexel.psal.anonymouth.utils.POS.TheTags;

import edu.drexel.psal.anonymouth.utils.*;

import edu.drexel.psal.jstylo.generics.Logger;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * 
 * @author Joe Muoio
 * @author Andrew W.E. McDonald
 */

public class TaggedSentence {

	protected String untagged;
	protected ArrayList<Word> wordsInSentence;
	//protected ArrayList<TaggedWord> tagged=new ArrayList<TaggedWord>();
	//protected Iterator<TaggedWord> tagIter;
	//private TaggedWord taggedWord;
	//protected ArrayList<String> wordsToReturn;
	private int PROBABLE_MAX = 3;
	
	protected ArrayList<TENSE> tense = new ArrayList<TENSE>(PROBABLE_MAX);
	protected ArrayList<POV> pointOfView = new ArrayList<POV>(PROBABLE_MAX);
	protected ArrayList<CONJ> conj = new ArrayList<CONJ>(PROBABLE_MAX);
	/*
	protected ArrayList<String> functionWords=new ArrayList<String>(PROBABLE_MAX);//not sure if should have put PROBABLE_MAX
	protected ArrayList<String> misspelledWords=new ArrayList<String>(PROBABLE_MAX);
	protected ArrayList<String> punctuation =new ArrayList<String>(PROBABLE_MAX);
	protected ArrayList<String> specialChars =new ArrayList<String>(PROBABLE_MAX);
	protected ArrayList<String> digits =new ArrayList<String>(PROBABLE_MAX);
	protected ArrayList<Integer> wordLengths=new ArrayList<Integer>(PROBABLE_MAX);
	
	/*protected HashMap<String,Integer>  words=new HashMap<String,Integer>();
	protected HashMap<String,Integer>  wordBigrams=new HashMap<String,Integer>();
	protected HashMap<String,Integer>  wordTrigrams=new HashMap<String,Integer>();
	
	protected HashMap<String,Integer>  POS=new HashMap<String,Integer>();
	protected HashMap<String,Integer>  POSBigrams=new HashMap<String,Integer>();
	protected HashMap<String,Integer>  POSTrigrams=new HashMap<String,Integer>();
	
	protected HashMap<String,Integer> letters=new HashMap<String,Integer>();
	protected HashMap<String,Integer>  letterBigrams=new HashMap<String,Integer>();
	protected HashMap<String,Integer>  letterTrigrams=new HashMap<String,Integer>();
*/	
	//protected HashMap<String,Word> wordListMap=new HashMap<String,Word>(); 
	
	private static final Pattern punctuationRegex=Pattern.compile("[.?!,\'\";:]{1}");
	private static final Pattern specialCharsRegex=Pattern.compile("[~@#$%^&*-_=+><\\\\[\\\\]{}/\\|]+");
	private static final Pattern digit=Pattern.compile("[\\d]{1,}");
	
	protected List<? extends HasWord> sentenceTokenized;
	protected Tokenizer<? extends HasWord> toke;
	protected TreebankLanguagePack tlp = new PennTreebankLanguagePack(); 
	
	private String[] thirdPersonPronouns={"he","she","him", "her","it","his","hers","its","them","they","their","theirs"};
	private String[] firstPersonPronouns={"I","me","my","mine","we","us","our","ours"};
	private String[] secondPersonPronouns={"you","your","yours"};
	
	public TaggedSentence(String untagged){
		wordsInSentence = new ArrayList<Word>();
		this.untagged = untagged;
	}
	/*
	public TaggedSentence(String untagged, ArrayList<TaggedWord> tagged){
		wordsInSentence = new ArrayList<Word>(tagged.size());
		this.untagged = untagged;
		this.tagged = tagged;
		
		//setGrammarStats();
	//	Logger.logln("WordList"+wordList.toString());
	}
	*/
	
	public TaggedSentence(TaggedSentence taggedSentence) {//TODO make sure this doesnt need new objects.
		this.untagged=taggedSentence.untagged;
		this.wordsInSentence=taggedSentence.wordsInSentence;
		
	}

	/**
	 * Set's the TaggedSentence which is an ArrayList of Word objects
	 * @param tagged the tagged sentence as output by the Standford POS Tagger
	 * @return
	 */
	public boolean setTaggedSentence(ArrayList<TaggedWord> tagged){
		int numTagged = tagged.size();
		for (int i=0;i< numTagged;i++){
			Word newWord=new Word(tagged.get(i).word(),tagged.get(i).tag());
			//newWord=ConsolidationStation.getWordFromString(tagged.get(i).word());
			//newWord.setPOS(tagged.get(i).tag());
			//addToWordList(tagged.get(i).word(),newWord);
			wordsInSentence.add(newWord);
		}	
		//setGrammarStats();
		
		//Logger.logln("WordList"+wordList.toString());
		
		return true;
	}
/*
	public HashMap<String, Word> getWordList(){
		return wordListMap;
	}
	*/
	
	
	/**
	 * Retrieves all Reference objects associated with each word in the sentence, and merges them into a single instance of SparseReferences
	 * @return
	 */
	public SparseReferences getReferences(){
		int numWords = this.size();
		SparseReferences allRefs = new SparseReferences(numWords*5); // just a guess - I don't think well have more than 5 distinct features per word (as an average)
		for(Word w: wordsInSentence){
			allRefs.merge(w.featuresFound);
		}
		return allRefs;
	}
	
	/**
	 * returns the length of the sentence in Words
	 * @return
	 */
	public int size(){
		return wordsInSentence.size();
	}
	public ArrayList<Word> getWordsInSentence(){
		return wordsInSentence;
	}
	
	/**
	 * returns a SparseReference object containing the index of each attribute who's value needs to be updated, along with the amount
	 * it must be changed by (if positive, the present value should increase, if negative, it should decrease. Therefore, you only need to 
	 * add the 'value' of each Reference to the 'index'th Attribute's presentValue in the Attribute[] array.  
	 * 
	 * note: the reason this is done at the sentence level rather than the document level, is that users will generally only edit one sentence at a time; so only that part 
	 * of the document can change.
	 * @param oldOne
	 * @return
	 */
	public SparseReferences getOldToNewDeltas(TaggedSentence oldOne){
		SparseReferences oldRefs = oldOne.getReferences();
		return this.getReferences().leftMinusRight(oldRefs); 	
	}
	
	public ArrayList<TENSE> getTense(){
		return tense;
	}
	public ArrayList<POV> getPov(){
		return pointOfView;
	}
	public ArrayList<CONJ> getConj(){
		return conj;
	}
	
	public void setWordList(){
	
	}
	
	/*
	private void addTowordListMap(String str,Word word){
		if(wordListMap.containsKey(str)){
			word.adjustVals(0, wordListMap.get(str).infoGainSum,wordListMap.get(str).percentChangeNeededSum);//check on this
			wordListMap.put(str,word);
		}
		else {
			wordListMap.put(str, word);
		}
	}
	*/
	
/*	
	
	 * sets the ArrayLists, Tense, Pow, and Conj.
	 * @param tagged
	 
	public void setGrammarStats(){
		//setwordListMap();
		FunctionWord fWord=new FunctionWord();
		MisspelledWords mWord=new MisspelledWords();
		for (int twCount=0;twCount<tagged.size();twCount++){
			TaggedWord temp=tagged.get(twCount);
			//System.out.println(temp.tag());
			if(temp.word().matches("[\\w&&\\D]+")){//fixes the error with sentenceAppend button
				if(fWord.searchListFor(temp.word())){
					//functionWords.add(temp.word());
				}
				else if(mWord.searchListFor(temp.word())){
					misspelledWords.add(temp.word());
				}
				java.util.regex.Matcher wordToSearch=punctuationRegex.matcher(temp.word());
				if(wordToSearch.find()){
					punctuation.add(temp.word().substring(wordToSearch.start(), wordToSearch.end()));
				}
				//adds digits
				wordToSearch=digit.matcher(temp.word());
				if(wordToSearch.find()){
					String digitSubStr=temp.word().substring(wordToSearch.start(), wordToSearch.end());
					for (int count=0;count<digitSubStr.length();count++){
						if(count-2>=0){
							digits.add(digitSubStr.substring(count-2, count));
							digits.add(digitSubStr.substring(count-1, count));
						}
						else if(count-1>=0){
							digits.add(digitSubStr.substring(count-1, count));
						}//not sure if necessary...digits bi/trigrams
						digits.add(digitSubStr.substring(count, count));
					}	
				}	
				wordToSearch=specialCharsRegex.matcher(temp.word());
				if(wordToSearch.find()){
					specialChars.add(temp.word().substring(wordToSearch.start(), wordToSearch.end()));
				}
				
				wordLengths.add(temp.word().length());
				//setHashMap(POS,temp.tag());
				//setHashMap(words,temp.word());
				
				/*if(twCount-2>=0){//addsTrigrams&Bigrams
					setHashMap(POSTrigrams,tagged.get(twCount-2).tag()+tagged.get(twCount-1).tag()+tagged.get(twCount).tag());
					setHashMap(wordTrigrams,tagged.get(twCount-2).word()+tagged.get(twCount-1).word()+tagged.get(twCount).word());
					setHashMap(POSBigrams,tagged.get(twCount-1).tag()+tagged.get(twCount).tag());//I feel that doing it this way with if/elif would speed up code
					setHashMap(wordBigrams,tagged.get(twCount-1).word()+tagged.get(twCount).word());
				}
				else if(twCount-1>=0){//addsBigrams
					setHashMap(POSBigrams,tagged.get(twCount-1).tag()+tagged.get(twCount).tag());
					setHashMap(wordBigrams,tagged.get(twCount-1).word()+tagged.get(twCount).word());
				}
				char[] untaggedWord=temp.word().toLowerCase().toCharArray();
				for(int letterIndex=0;letterIndex<untaggedWord.length;letterIndex++){
					setHashMap(letters,untaggedWord[letterIndex]+"");
					if(letterIndex-2>=0){
						setHashMap(letterBigrams,untaggedWord[letterIndex-1]+untaggedWord[letterIndex]+"");
						setHashMap(letterTrigrams,untaggedWord[letterIndex-2]+untaggedWord[letterIndex-1]+untaggedWord[letterIndex]+"");
					}
					else if(letterIndex-1>=0){
						setHashMap(letterBigrams,untaggedWord[letterIndex-1]+untaggedWord[letterIndex]+"");
					}
				}
				
				
			}	//This somehow overwrite the taggedDocument.
				
				
		}
			
	}
	*/
		
	private void setHashMap(HashMap <String,Integer> hashMap, String key){
		if(hashMap.containsKey(key)){
			hashMap.put(key, (hashMap.get(key).intValue()+1));
		}
		else {
			hashMap.put(key, 1);
		}
	}
	
	
	/**
	 * returns the untagged version of the sentence
	 * @return
	 */
	public String getUntagged(){
		return untagged;
	}
	
	public String toString(){
		return "[ untagged: "+untagged+" ||| tagged: "+wordsInSentence.toString()+" ||| SparseReferences Object: "+getReferences().toString()+" ]";
		//||| tense: "+tense.toString()+" ||| point of view: "+pointOfView.toString()+" conjugation(s): "+conj.toString()+" ]";// ||| functionWords : "+functionWords.toString()+" ]";
	}
	
	/* TODO: 'tagged' no longer holds tagged words.
	public ArrayList<String> getWordsWithTag(TheTags tag){
		wordsToReturn = new ArrayList<String>(tagged.size());// Can't return more words than were tagged
		tagIter = tagged.iterator();
		while (tagIter.hasNext()){
			taggedWord = tagIter.next();
			System.out.println(taggedWord.value());
			System.out.println(taggedWord.tag());
		}
		return wordsToReturn;
	}
	*/
	
}

/*Stuff for tenses
if(temp.tag().startsWith("VB")){
	//it is a verb 
	switch(TheTags.valueOf((temp.tag()))){
	case VB: conj.add(CONJ.SIMPLE);//"Verb, base form";
	case VBD: tense.add(TENSE.PAST);
				conj.add(CONJ.SIMPLE); // "Verb, past tense";
	//case "VBG": // "Verb, gerund or present participle";
	//case "VBN": // "Verb, past participle";
	case VBP: tense.add(TENSE.PRESENT);// "Verb, non-3rd person singular present";
	case VBZ: tense.add(TENSE.PRESENT);// "Verb, 3rd person singular present";
	}
}
else if (temp.tag().startsWith("PR")){//this is a pronoun
	String tempWord=temp.word();
	for(int j=0;j<firstPersonPronouns.length;j++){
		if(firstPersonPronouns[j].equalsIgnoreCase(tempWord)){
			if(!pointOfView.contains(POV.FIRST_PERSON))//will not add POVs twice
				pointOfView.add(POV.FIRST_PERSON);
		}
	}
	for(int j=0;j<secondPersonPronouns.length;j++){
		if(secondPersonPronouns[j].equalsIgnoreCase(tempWord)){
			if(!pointOfView.contains(POV.SECOND_PERSON))
				pointOfView.add(POV.SECOND_PERSON);
		}
	}
	for(int j=0;j<thirdPersonPronouns.length;j++){
		if(thirdPersonPronouns[j].equalsIgnoreCase(tempWord)){
			if(!pointOfView.contains(POV.THIRD_PERSON))
				pointOfView.add(POV.THIRD_PERSON);
		}
	}
}
/*else if(temp.word().equalsIgnoreCase("shall")||temp.word().equalsIgnoreCase("will")){
	tense.add(TENSE.FUTURE);
}actually, this is not necessarily true.*/
