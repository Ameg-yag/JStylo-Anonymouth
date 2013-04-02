package edu.drexel.psal.anonymouth.suggestors;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.FeatureList;
import edu.drexel.psal.jstylo.generics.Logger;

public class StringFormulator {
	
	
	private String[] aveSylls = {"    See if you can change your number or average syllables per word to '%.2f' (presently it is '%.2f') by removing some words with %s, and replace them with %s. The replacement words would preferably %s. The dictionary at the bottom of the pane may be of assistance in finding words.\n",
			"less than 3 syllables","3 or more syllables"};
	private String[] uniqueWords = {"    You should attempt to increase your unique word count to '%.2f' (from its present value of '%.2f') by replacing some words used more than once that have %s with unused words with %s (or, by just adding a few words in if they fit).\n",
		"    You should try to decrease your unique word count to %.2f (from its present value of '%.2f') by replacing some single use words with %s with words that have already been used and have %s.\n"};
	private String[] sentenceCount ={"    Increase your sentence count to '%.2f' (currently at '%.2f'). Try to break long complex/compound sentences up into smaller simple ones. Replacing " +
			"commas, semi-colons, and dashes may help.\n","    Decrease your sentence count to '%.2f' (currently at '%.2f'). Try to merge sentences on the same topic into a single compound/complex sentence. " +
					"Semi-colons, commas, and dashes can be used to do this. \n"};
	private String[] letterSpace = {"    Though this wouldnt be my focus, think about increasing the number of letters in your document to '%.2f' (now at '%.2f') by adding words that are %s. However, do not substitiute words " +
			"on a 1 to 1 basis.\n","    Though this wouldnt be my focus, consider decreasing the number of letters in your document to '%.2f' (now at '%.2f') by removing words containing %s.\n"};
	private String[] charSpace = {"    Adding a few additional thoughts to your document will increase its character count to '%.2f' (from '%.2f', which may be desireable). Note that you should do you best to %s sentences.\n", 
			"    Removing characters from your document could be helpful, try removing any superflous punctuation, or any unnecessary writing to get your character count down to '%.2f' (from '%.2f').\n"};
	
	//private String appendThis = "\n\n\tYou may see fewer features than were selected because it isn't necessary (or enjoyable) to attempt to individually alter every feature available. Certain higher level features " +
	//		"(e.g FOG Index) are comprised of lower level features which are more readily modified. Every time you \"process\" the document, however, every selected feature is included.\n";
	
	Attribute[] attribs;
	
	double[][] nineFeatPdf = new double[5][3];
	
	protected String averageSyllablesSuggestion;
	protected String uniqueWordsSuggestion;
	protected String sentenceCountSuggestion;
	protected String letterSpaceSuggestion;
	protected String charSpaceSuggestion;
	
	public StringFormulator(Attribute[] attribs){
		this.attribs = attribs;
	}
	
	public void getGeneralNineFeatureAdvice(){
		String outString = "";
		int uniqueWordPos,sentenceCountPos,letterSpacePos,charSpacePos;
		String uniqueWords_s1, uniqueWords_s2, aveSylls_s1, aveSylls_s2, aveSylls_s3,letterSpace_s1,charSpace_s1,charSpace_s2;
		if(nineFeatPdf[0][1] > 0){
			uniqueWordPos = 0;
			aveSylls_s3 = "not be in another part of the document";
		}
		else{
			uniqueWordPos=1;
			aveSylls_s3 = "already have been used in the document";
		}	
		if(nineFeatPdf[1][1] > 0){
			uniqueWords_s1 = aveSylls[1];
			uniqueWords_s2 = aveSylls[2];
			aveSylls_s1 = aveSylls[1];
			aveSylls_s2 = aveSylls[2];
			letterSpace_s1 = aveSylls[2];
		}
		else{
			uniqueWords_s1 = aveSylls[2];
			uniqueWords_s2 = aveSylls[1];
			aveSylls_s1 = aveSylls[2];
			aveSylls_s2 = aveSylls[1];
			letterSpace_s1 = aveSylls[1];
		}
		if(nineFeatPdf[2][1] > 0){
			sentenceCountPos = 0;
			charSpace_s1 = "add";
		}
		else{
			sentenceCountPos = 1;
			charSpace_s1 = "NOT add";
		}
			
		if(nineFeatPdf[3][1] >0)
			letterSpacePos = 0;
		else
			letterSpacePos = 1;
			
		if(nineFeatPdf[4][1] > 0)
			charSpacePos = 0;
		else
			charSpacePos = 1;
		
		uniqueWordsSuggestion = String.format(uniqueWords[uniqueWordPos],nineFeatPdf[0][2],nineFeatPdf[0][0],uniqueWords_s1,uniqueWords_s2);
		averageSyllablesSuggestion = String.format(aveSylls[0],nineFeatPdf[1][2],nineFeatPdf[1][0],aveSylls_s1,aveSylls_s2,aveSylls_s3);
		sentenceCountSuggestion = String.format(sentenceCount[sentenceCountPos],nineFeatPdf[2][2],nineFeatPdf[2][0]);
		letterSpaceSuggestion = String.format(letterSpace[letterSpacePos],nineFeatPdf[3][2],nineFeatPdf[3][0],letterSpace_s1);
		charSpaceSuggestion = String.format(charSpace[charSpacePos],nineFeatPdf[4][2],nineFeatPdf[4][0],charSpace_s1);
		//System.out.println("SentenceCount....."+nineFeatPdf[0][0]+", "+nineFeatPdf[0][1]+", "+nineFeatPdf[0][2]+" -> "+sentenceCountSuggestion);
	}
	
	
	public double[] setNineFeatPdfVals(int i){ // PresentDeltaFuture
		double[] temp = new double[3];
		temp[0] = attribs[i].getToModifyValue();
		temp[1] = attribs[i].getDeltaValue();
		temp[2] = attribs[i].getTargetValue();
		Logger.logln(attribs[i].getGenericName()+" ... P: "+attribs[i].getToModifyValue()+" ... D: "+attribs[i].getDeltaValue()+" ... F: "+attribs[i].getTargetValue());
		return temp;
	}
	
	public void organizeFeatures(){
		int i = 0;
		for(i=0;i<9;i++){
			FeatureList feat = attribs[i].getGenericName();
			switch(feat){
				case UNIQUE_WORDS_COUNT:
					nineFeatPdf[0] = setNineFeatPdfVals(i);
					break;
				case AVERAGE_SYLLABLES_IN_WORD:
					nineFeatPdf[1] = setNineFeatPdfVals(i);
					break;
				case SENTENCE_COUNT: 
					nineFeatPdf[2] = setNineFeatPdfVals(i);
					break;
				case LETTER_SPACE:
					nineFeatPdf[3] = setNineFeatPdfVals(i);
					break;
				case CHARACTER_SPACE:
					nineFeatPdf[4] = setNineFeatPdfVals(i);
					break;
//				case AVERAGE_SENTENCE_LENGTH:
//					pdf[5] = setPdfVals(i);
//					break;
//				case COMPLEXITY:
//					pdf[6] = setPdfVals(i);
//					break;
//				case GUNNING_FOG_READABILITY_INDEX:
//					pdf[7] = setPdfVals(i);
//					break;
//				case FLESCH_READING_EASE_SCORE:
//					pdf[8] = setPdfVals(i);
//					break;
				default:
					break;
				}
			}
		}
	
/*	
	public static void main (String[] args){
		StringFormulator sf = new StringFormulator();
		System.out.println(sf.getGeneralNineFeatureAdvice(new double[]{-1.0,1.0,1.0,-1.0,-1.0}));
		
		
		System.out.println(sf.getGeneralNineFeatureAdvice(new double[]{1.0,-1.0,-1.0,1.0,1.0}));
	}
*/	
}
