package edu.drexel.psal.anonymouth.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jgaap.generics.Document;

import edu.drexel.psal.anonymouth.gooie.ErrorHandler;
import edu.drexel.psal.jstylo.generics.Logger;

/**
 * Retrieves sentences from a text and places them into an arraylist.
 * @author Andrew W.E. McDonald
 *
 */
public class SentenceTools implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5007508872576011005L;
	private final String NAME = "( "+this.getClass().getName()+" ) - ";
	
	/**
	 * This pattern, "EOS_chars" matches:
	 * 		=> any number (but at least one) and combination of question marks and quotation marks, OR
	 *		=> EXACTLY four periods (because English dictates that if you end a sentence with ellipsis points, you must have four periods: one for the period, and three for the ellipsis points, OR
	 *		=> any period NOT behind another period AND NOT in front of another period (otherwise, ellipsis points will be matched)
	 ********
	 * (NOT YET, but will hopefully do something like this soon:
	 *	Then, it matches one or more spaces, followed by either a capital letter, or an end of line.
	 *
	 * something along these lines:
	 *	private static final Pattern EOS_chars = Pattern.compile("([?!]+)|([.]{4})|((?<!\\.)\\.(?!\\.))\\s+([A-Z]|$)"); 
	 *
	 *	)
	 *********
	 */
	private static final Pattern EOS_chars = Pattern.compile("([?!]+)|([.]{4,})|((?<!\\.)\\.(?!\\.))"); 
	private String EOS = ".?!";
	
	/**
	 * the "sentence_quote" pattern matches any number and combination of "?" and "!" characters, OR four periods, OR a <i>single</i> period (specifically, any period that isn't followed by, or that follows, another period). (because ellipses points don't indicate an end of sentence UNLESS there are 4 ellipses points [one for the period, and three for the ellipsis]),
	 * it then matches a single "double" quotation mark, followed by  the first group (see above line) (because some people think that: "The man said, "Hello!"." (using an EOS character pre and post quotation mark is acceptable...)
	 * Finally, it and will either match the end of the input, a capital letter (both which indicate that the current sentence is over), or a citation (the explanation of the citation regex is below... I just copied and pasted it onto the end of this one).
	 * 
	 * NOTE: in the written description above, space characters are not necessarily discussed.
	 */
	private static final Pattern sentence_quote = Pattern.compile("([?!]+|[.]{4}|(?<!\\.)\\.(?!\\.))\\s*\"\\s*([?!]+|[.]{4}|(?<!\\.)\\.(?!\\.))?\\s*($|[A-Z]|\\(((\\s*[A-Za-z.]*\\s*(et\\.?\\s*al\\.)?\\s*[0-9]*\\s*[-,]*\\s*[0-9]*\\s*)|(\\s*[0-9]*\\s*[-,]*\\s*[0-9]*\\s*[A-Za-z.]*\\s*(et\\.?\\s*al\\.)?\\s*))\\))"); 
	
	/**
	 * the pattern 'citation' forces the match to begin at the start of the input (via the anchor), and matches zero or one occurrences EOS character, and then searches for citations that begin with an opening parenthesis,
	 * match either a word (a name) followed by "et al." [or et. al.", even though it's wrong] (or not) followed by a number, or two numbers separated by a dash, and finishing with a closing parenthesis. 
	 * It will also match a swapped version, where the number / two numbers separated by a dash come before the name (and "et. al.", if it exists.)
	 * 
	 * NOTE: in the written description above, space characters are not necessarily discussed.
	 */
	private static final Pattern citation = Pattern.compile("^[?!.]?\\s*\\(((\\s*[A-Za-z.]*\\s*(et\\.?\\s*al\\.)?\\s*[0-9]*\\s*[-,]*\\s*[0-9]*\\s*)|(\\s*[0-9]*\\s*[-,]*\\s*[0-9]*\\s*[A-Za-z.]*\\s*(et\\.?\\s*al\\.)?\\s*))\\)"); 
	
	private static final String t_PERIOD_REPLACEMENT = ""; // XXX: Hopefully it is safe to assume no one sprinkles apple symbols in their paper
	// The below three "permanent" replacments are to mark EOS characters in text that the user has told us are not actually ending a sentence. 
	// DO NOT remove these... in order to get them back, you need to know the unicode code
	public static final String p_PERIOD_REPLACEMENT = "๏";
	public static final String p_EXCLAMATION_REPLACEMENT ="˩";//"ǃ";
	public static final String p_QUESTION_REPLACEMENT ="ʔ";
	private static int MAX_SENTENCES = 500;
	//private ArrayList<String> sentsToEdit = new ArrayList<String>(MAX_SENTENCES);
	private static int sentNumber = -1;
	private int totalSentences = 0;
	
	private String[] notEndsOfSentence = {"Dr.","Mr.","Mrs.","Ms.","St.","vs.","U.S.","Sr.","Sgt.","R.N.","pt.","mt.","mts.","M.D.","Ltd.","Jr.","Lt.","Hon.","i.e.","e.x.","e.g.","inc.",
			"et al.","est.","ed.","D.C.","B.C.","B.S.","Ph.D.","B.A.","A.B.","A.D.","A.M.","P.M.","Ln.","fig.","p.","pp.","ref.","r.b.i.","V.P.","yr.","yrs.","etc."};
	
	/**
	 * Takes a text (one String representing an entire document), and breaks it up into sentences. Tries to find true ends of sentences: shouldn't break up sentences containing quoted sentences, 
	 * checks for sentences ending in a quoted sentence (e.x. He said, "Hello." ), will not break sentences containing common abbreviations (such as Dr., Mr. U.S., etc.,e.x., i.e., and others), and 
	 * checks for ellipses points. However, It is probably not perfect.
	 * @param text
	 * @return
	 */
	public ArrayList<String[]> makeSentenceTokens(String text) {
		ArrayList<String> sents = new ArrayList<String>(MAX_SENTENCES);
		ArrayList<String[]> finalSents = new ArrayList<String[]>(MAX_SENTENCES);
		boolean mergeNext=false;
		boolean mergeWithLast=false;
		boolean forceNoMerge=false;
		int currentStart = 1;
		int currentStop = 0;
		String safeString_subbedEOS;
		int quoteAtEnd;
		int citationAtEnd;
		String temp;
		text = text.replaceAll("\u201C","\"");
		text = text.replaceAll("\u201D","\"");
		text = text.replaceAll("\\p{Cf}","");// replace unicode format characters that will ruin the regular expressions (because non-printable characters in the document still take up indices, but you won't know they're there untill you "arrow" though the document and have to hit the same arrow twice to move past a certain point.
		// Note that we must use "Cf" rather than "C". If we use "C" or "Cc" (which includes control characters), we remove our newline characters and this screws up the document.
		// "Cf" is "other, format". "Cc" is "other, control". Using "C" will match both of them.
		int lenText = text.length();
		int notEOSNumber = 0;
		int numNotEOS = notEndsOfSentence.length;
		String replacementString = "";
		String safeString = "";
		
		for (notEOSNumber = 0; notEOSNumber < numNotEOS; notEOSNumber++) {
			replacementString = notEndsOfSentence[notEOSNumber].replaceAll("\\.",t_PERIOD_REPLACEMENT);
			//System.out.println("REPLACEMENT: "+replacementString);
			safeString = notEndsOfSentence[notEOSNumber].replaceAll("\\.","\\\\.");
			//System.out.println(safeString);
			text = text.replaceAll("(?i)\\b"+safeString,replacementString); // the "(?i)" tells Java to do a case-insensitive search.
		}
		
		Matcher sent = EOS_chars.matcher(text);
		boolean foundEOS = sent.find(currentStart); // xxx TODO xxx take this EOS character, and if not in quotes, swap it for a permanent replacement, and create and add an EOS to the calling TaggedDocument's eosTracker.

		Matcher sentEnd;
		Matcher citationFinder;
		boolean hasCitation = false;
		int charNum = 0;
		int lenTemp = 0;
		int lastQuoteAt = 0;
		boolean foundQuote = false;
		boolean isSentence;
		boolean foundAtLeastOneEOS = foundEOS;
		
		/**
		 * Needed otherwise when the user has text like below:
		 * 		This is my sentence one. This is "My sentence?" two. This is the last sentence.
		 * and they begin to delete the EOS character as such:
		 * 		This is my sentence one. This is "My sentence?" two This is the last sentence.
		 * Everything gets screwed up. This is because the operations below operate as expected only when there actually is an EOS character
		 * at the end of the text, it expects it there in order to function properly. Now usually if there is no EOS character at the end it wouldn't
		 * matter since the while loop and !foundAtLeastOneEOS conditional are executed properly, BUT as you can see the quotes, or more notably the EOS character inside
		 * the quotes, triggers this initial test and thus the operation breaks. This is here just to make sure that does not happen.
		 */
		boolean EOSAtSentenceEnd = EOS.contains(text.substring(lenText-1, lenText));
		
		String currentEOS;
		while (foundEOS == true) {
			currentEOS = sent.group(0);
			currentStop = sent.end();
			//System.out.println("Start: "+currentStart+" and Stop: "+currentStop);
			temp = text.substring(currentStart-1,currentStop);
			//System.out.println(temp);
			lenTemp = temp.length();
			lastQuoteAt = 0;
			foundQuote = false;
			for(charNum =0; charNum < lenTemp; charNum++){
				if(temp.charAt(charNum) == '\"'){
					lastQuoteAt = charNum;
					if(foundQuote == true){
						foundQuote = false;
						
					}
					else{
						foundQuote = true;
					}
					//System.out.println("Found quote!!! here it is: "+temp.charAt(charNum)+" ... in position: "+lastQuoteAt+" ... foundQuote is: "+foundQuote);
				}
			}
			if(foundQuote == true && ((temp.indexOf("\"",lastQuoteAt+1)) == -1)){ // then we found an EOS character that shouldn't split a sentence because it's within an open quote.
				if((currentStop = text.indexOf("\"",currentStart +lastQuoteAt+1)) == -1){
					currentStop = text.length(); // if we can't find a closing quote in the rest of the input text, then we assume the author forgot to put a closing quote, and act like it's at the end of the input text.
				}
				else{
					currentStop +=1;
					mergeNext=true;// the EOS character we are looking for is not in this section of text (section being defined as a substring of 'text' between two EOS characters.)
				}
			}
			safeString = text.substring(currentStart-1,currentStop);
			
			//System.out.println("safeString: "+safeString);
			//sentEnd = sentence_quote.matcher(safeString);	
			quoteAtEnd = 0;
			citationAtEnd = 0;
			if (foundQuote) {
				sentEnd = sentence_quote.matcher(text);	
				isSentence = sentEnd.find(currentStop-2); // -2 so that we match the EOS character before the quotes (not -1 because currentStop is one greater than the last index of the string -- due to the way substring works, which is includes the first index, and excludes the end index: [start,end).)

				if (isSentence == true) { // If it seems that the text looks like this: He said, "Hello." Then she said, "Hi." 
					// Then we want to split this up into two sentences (it's possible to have a sentence like this: He said, "Hello.")
					//System.out.println("start: "+sentEnd.start()+" ... end: "+sentEnd.end());
					currentStop = text.indexOf("\"",sentEnd.start())+1;
					safeString = text.substring(currentStart-1,currentStop);
					forceNoMerge = true;
					mergeNext = false;
					quoteAtEnd = 1;
				}
			}
			//System.out.println("POST quote finder: "+safeString);
			// now check to see if there is a citation after the sentence (doesn't just apply to quotes due to paraphrasing)
			// The rule -- at least as of now -- is if after the EOS mark there is a set of parenthesis containing either one word (name) or a name and numbers (name 123) || (123 name) || (123-456 name) || (name 123-456) || etc..
			citationFinder = citation.matcher(text.substring(currentStop));	
			hasCitation = citationFinder.find(); // -2 so that we match the EOS character before the quotes (not -1 because currentStop is one greater than the last index of the string -- due to the way substring works, which is includes the first index, and excludes the end index: [start,end).)
			//System.out.println("RESULT OF citation matching: "+hasCitation+" ==> attempted to match: "+safeString+" ====> from: "+text);
			if (hasCitation == true) { // If it seems that the text looks like this: He said, "Hello." Then she said, "Hi." 
				// Then we want to split this up into two sentences (it's possible to have a sentence like this: He said, "Hello.")
				//System.out.println("start: "+(citationFinder.start()+currentStop)+" ... end: "+(citationFinder.end()+currentStop-1));
				currentStop = text.indexOf(")",citationFinder.start()+currentStop)+1;
				safeString = text.substring(currentStart-1,currentStop);
				mergeNext = false;
				citationAtEnd = citationFinder.group(0).length() - 1;// citationFinder will match either the last EOS character or "double" quote, so we subtract one to negate that
			}	
			
			if (mergeWithLast) {
				mergeWithLast=false;
				String prev=sents.remove(sents.size()-1);
				safeString=prev+safeString;
			}
			
			if (mergeNext && !forceNoMerge) {//makes the merge happen on the next pass through
				mergeNext=false;
				mergeWithLast=true;
			} else {
				forceNoMerge = false;
				//System.out.println("Actual: "+safeString);
				safeString_subbedEOS = subOutEOSChars(currentEOS, safeString, quoteAtEnd + citationAtEnd);
				//System.out.println("SubbedEOS: "+safeString_subbedEOS);
				safeString = safeString.replaceAll(t_PERIOD_REPLACEMENT,".");
				safeString_subbedEOS = safeString_subbedEOS.replaceAll(t_PERIOD_REPLACEMENT,".");
				//System.out.println(safeString);
				finalSents.add(new String[]{safeString, safeString_subbedEOS});
			}
		
			//System.out.println("---------------");
			sents.add(safeString);
			//// xxx xxx xxx return the safeString_subbedEOS too!!!!
			//System.out.println("start minus one: "+(currentStart-1)+" stop: "+currentStop);
			if (currentStart < 0 || currentStop < 0) {
				Logger.logln(NAME+"Something went really wrong making sentence tokens.");
				ErrorHandler.fatalError();
			}
			//System.out.println("The rest of the text: "+text.substring(currentStart));
			currentStart = currentStop+1;
			if (currentStart >= lenText) {
				foundEOS = false;
				continue;
			}
			
			foundEOS = sent.find(currentStart);
		}
		
		if (!foundAtLeastOneEOS || !EOSAtSentenceEnd) {
			ArrayList<String[]> wrapper = new ArrayList<String[]>(1);
			wrapper.add(new String[]{text,text});
			return wrapper;
		}
		
		return finalSents;
	}
	
	/**
	 * Substitutes the '.','?', and '!' (and any combination of any number of '?' and/or '!') characters at the END of a sentence.
	 * @param currentEOS
	 * @param needsSubbing
	 * @param quoteAndOrCitationAtEnd
	 * @return
	 * 	a version of the string with the EOS characters replaced. 
	 */
	public String subOutEOSChars(String currentEOS, String needsSubbing, int quoteAndOrCitationAtEnd){
		int numEOSes = currentEOS.length();
		int startOfEOS = needsSubbing.length() - numEOSes - quoteAndOrCitationAtEnd; // quoteAndOrCitationAtEnd will be '0' unless there is a quote after the EOS character(s), a citation, or both. This will be the length of (quote length + citation length).
		for (int currentEOSnum = 0; currentEOSnum < numEOSes; currentEOSnum++){
			switch(currentEOS.charAt(currentEOSnum)){
				case '.': 
					needsSubbing = needsSubbing.substring(0, startOfEOS + currentEOSnum) + p_PERIOD_REPLACEMENT + needsSubbing.substring(startOfEOS + currentEOSnum+1);
					break;
				case '?': 
					needsSubbing = needsSubbing.substring(0, startOfEOS + currentEOSnum) + p_QUESTION_REPLACEMENT + needsSubbing.substring(startOfEOS + currentEOSnum+1);
					break;
				case '!': 
					needsSubbing = needsSubbing.substring(0, startOfEOS + currentEOSnum) + p_EXCLAMATION_REPLACEMENT + needsSubbing.substring(startOfEOS + currentEOSnum+1);
					break;
			}
		}			
		return needsSubbing;
	}
	
	public static int getSentNumb(){
		return sentNumber;
	}
	/**
	 * Checks whether or not there are more unchecked sentences from the intial input text or not. True if there are,
	 * false if not.
	 * @return
	 * 	True if there are more sentences to check, false if not.
	 */
	public boolean moreToCheck(){
		if(sentNumber < totalSentences)
			return true;
		else
			return false;
	}
	
	public void setSentenceCounter(int sentNumber){
		SentenceTools.sentNumber = sentNumber;
	}
	
	public static Document removeUnicodeControlChars(Document dirtyDoc){
		Document cleanDoc = new Document();
		try {
			dirtyDoc.load();
			cleanDoc.setText((dirtyDoc.stringify()).replaceAll("\\p{C}&&[^\\t\\n\\r]"," ").toCharArray());
			cleanDoc.setAuthor(dirtyDoc.getAuthor());
			cleanDoc.setTitle(dirtyDoc.getTitle());
			return cleanDoc;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logln("(SentenceTools) - ERROR! Could not load document: "+dirtyDoc.getTitle()+" (SentenceTools.removeUnicodeControlChars)");
			return dirtyDoc;
		}
	}
		
	
//}	
	
	/*
	public String editBySentence(){
		while(moreToCheck()){
			String editedSentence = JOptionPane.showInputDialog("Edit this: ",getNext().getSentence());
			editedText += editedSentence+" ";
		}
		editedText = editedText.substring(0,editedText.length()-1);
		return editedText;
	}
	*/
	public static void main(String[] args) throws IOException{
		SentenceTools ss = new SentenceTools();
		//String testText = "This is a test text. I said, \"this, is a test text.\", didn't you hear me? You said, \"I didn't hear you!\"... well, did you? Or, did you not!? I am hungry.";
		String testText = "This sentence, \"has many eos characters. However, they are mostly within a single quote. just to check! check what? Check that this whole quote will be treated as one sentence.\". (McDonald 123) But this, should not be in the first sentence. (123 - 345  Andrew et. al.) Nor should this!";
		//String testText = "There are many issues with the\n concept of intelligence and the way it is tested in people. As stated by David Myers, intelligence is the �mental quality consisting of the ability. to learn from experience�, solve problems, and use knowledge �to adapt. to new situations� (2010). Is there really just one intelligence? According to many psychologists, there exists numerous intelligences. One such psychologist, Sternberg, believes there are three: Analytical Intelligence, Creative Intelligence, and Practical Intelligence. Analytical Intelligence is the intelligence assessed by intelligence tests which presents well-defined problems with set answers and predicts school grades reasonably well and to a lesser extent, job success! \n \tCreative Intelligence is demonstrated by the way one reacts to certain unforeseen situations in �new� ways. The last of the three is Practical intelligence which is the type of intelligence required for everyday tasks. This is what is used by business managers and the like to manage and motivate people, promote themselves, and delegate tasks efficiently. In contrast to this idea of 3 separate intelligences is the idea of just one intelligence started by Charles Spearman. He thought we had just one intelligence that he called �General Intelligence� which is many times shortened to just: �G�. This G factor was an underlying factor in all areas of our intelligence. Spearman was the one who also developed factor analysis which is a statistics method which allowed him to track different clusters of topics being tested in an intelligence test which showed that those who score higher in one area are more likely to score higher in another. This is the reason why he believed in this concept of G.";
		//String testText = "Hello?, Dr., this! is my \"t!est?\"ing tex\"t?\".\nI need!? to. See if it \"correctly (i.e. nothing goes wrong) ... and finds the first, and every other sentence, etc.. These quotes are silly, and it is 1 A.m.! a.m.? just for testing purposes?\" No! Okay, yes. What? that isn't a \"real\" \"quote\".";
		//testText = " Or maybe, he did understand, but had more to share with humanity before his inevitable death. Maybe still, he was forecasting his own suicide twenty-eight years before it happened. No matter what Hemingway might have felt at the time, the deep nothingness that he shows in 'A Clean Well-Lighted Place,' is a nothingness that pervades the story and becomes more apparent to the characters as they age as humans do not last forever. Ernest Hemingway wrote much about the struggle to cope with the nothingness in the world, but eventually succumbed to the nothingness that he wrote about.";
		//testText=" After living so long, the old man lacks some of the gifts that people are born with that the young man takes for granted. The old man�s long life shows that as humans age, the length of time they have been around not only ages their body, but it ages their soul.";
		ArrayList<String[]> sTok=ss.makeSentenceTokens(testText);

		Object[] arr = sTok.toArray();
		try {
			OutputStreamWriter outStream=new OutputStreamWriter(System.out,"UTF8");
			Writer out=outStream;
			for (int i = 0; i<arr.length; i++){
				for(int j=0;j<arr[i].toString().length();j++){
					//System.out.println(arr[i].toString().charAt(j));
					//out.write("Character Coding of the output Stream is " + outStream.getEncoding()+"\n");
					//out.flush();
				}
			}
			
			 out.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("End");
		
	}
	
}


