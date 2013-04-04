package edu.drexel.psal.anonymouth.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.drexel.psal.anonymouth.suggestors.HighlightMapList;



public class Mapper {
	
	private final String NAME = "( "+this.getClass().getName()+" ) - ";
	
	public static final Map<FeatureList,List<HighlightMapList>> fhmMap;
	static{
		Map<FeatureList,List<HighlightMapList>> fhm = new HashMap<FeatureList,List<HighlightMapList>>();
		// missing avg char per word
		fhm.put(FeatureList.LETTERS,(List<HighlightMapList>) Arrays.asList(HighlightMapList.SPECIFIC_LETTER,HighlightMapList.SPECIFIC_WITHOUT_LETTER));
		fhm.put(FeatureList.AVERAGE_SYLLABLES_IN_WORD,(List<HighlightMapList>)Arrays.asList(HighlightMapList.EIGHT_SYLLABLE_WORDS, HighlightMapList.SEVEN_SYLLABLE_WORDS,HighlightMapList.SIX_SYLLABLE_WORDS,HighlightMapList.FIVE_SYLLABLE_WORDS,HighlightMapList.FOUR_SYLLABLE_WORDS,HighlightMapList.THREE_SYLLABLE_WORDS,HighlightMapList.TWO_SYLLABLE_WORDS,HighlightMapList.SINGLE_SYLLABLE_WORDS));
		fhm.put(FeatureList.AVERAGE_SENTENCE_LENGTH,(List<HighlightMapList>)Arrays.asList(HighlightMapList.SENTENCES_SHORTER_THAN_TARGET,HighlightMapList.SENTENCES_LONGER_THAN_TARGET));
		fhm.put(FeatureList.UNIQUE_WORDS_COUNT,(List<HighlightMapList>)Arrays.asList(HighlightMapList.UNIQUE_WORDS, HighlightMapList.REPEATED_WORDS));
		//fhm.put(FeatureList.AVERAGE_SENTENCE_LENGTH, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SHORTER_THAN_TARGET_WORDS, HighlightMapList.LONGER_THAN_TARGET_WORDS));
		fhm.put(FeatureList.DIGITS_PERCENTAGE, (List<HighlightMapList>)Arrays.asList(HighlightMapList.ALL_DIGITS));
		fhm.put(FeatureList.DIGITS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_DIGITS));
		fhm.put(FeatureList.FUNCTION_WORDS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_FUNCTION_WORDS));
		fhm.put(FeatureList.WORDS,(List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_WORDS));
		fhm.put(FeatureList.WORD_BIGRAMS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_WORD_BIGRAMS));
		fhm.put(FeatureList.WORD_TRIGRAMS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_WORD_TRIGRAMS));
		//fhm.put(FeatureList.POS_TAGS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_POS_TAGS));
		//fhm.put(FeatureList.POS_BIGRAMS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_POS_BIGRAMS));
		//fhm.put(FeatureList.POS_TRIGRAMS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_POS_TRIGRAMS));
		fhm.put(FeatureList.MISSPELLED_WORDS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_FUNCTION_WORDS));
		fhm.put(FeatureList.TOP_LETTER_BIGRAMS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_LETTER_BIGRAMS));
		fhm.put(FeatureList.TOP_LETTER_TRIGRAMS, (List<HighlightMapList>)Arrays.asList(HighlightMapList.SPECIFIC_LETTER_TRIGRAMS));
		fhmMap = Collections.unmodifiableMap(fhm);
	}
	
	

}

	
	
	/*
	hightlight map...
	
	SPECIFIC_MISSPELLED_WORDS, SPECIFIC_POS_BIGRAMS, SPECIFIC_POS_TAGS, SPECIFIC_POS_TRIGRAMS, SPECIFIC_LETTER_BIGRAMS, 
	SPECIFIC_LETTER_TRIGRAMS, UNIQUE_WORDS,  REPEATED_WORDS, SPECIFIC_WORD_BIGRAMS, SPECIFIC_WORD_TRIGRAMS, SPECIFIC_WORDS,
	
	features...
	
	SENTENCE_COUNT, LETTER_N_GRAMS,
	FUNCTION_WORDS, LETTERS_PERCENTAGE, CHARACTER_COUNT, MISSPELLED_WORDS, WORD_TRIGRAMS, WORD_BIGRAMS, WORDS,
	POS_TRIGRAMS, POS_BIGRAMS, POS_TAGS, PUNCTUATION, WORD_LENGTHS, THREE_DIGIT_NUMBERS, TWO_DIGIT_NUMBERS, DIGITS, UPPERCASE_LETTERS_PERCENTAGE,
	TOP_LETTER_TRIGRAMS, DIGITS_PERCENTAGE, TOP_LETTER_BIGRAMS, LETTERS, AVERAGE_CHARACTERS_PER_WORD, SPECIAL_CHARACTERS
	
	*/
	
	