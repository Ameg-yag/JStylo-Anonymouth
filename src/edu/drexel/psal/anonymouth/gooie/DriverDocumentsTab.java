package edu.drexel.psal.anonymouth.gooie;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.DataAnalyzer;
import edu.drexel.psal.anonymouth.engine.DocumentMagician;
import edu.drexel.psal.anonymouth.engine.FeatureList;
import edu.drexel.psal.anonymouth.engine.VersionControl;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;
import edu.drexel.psal.anonymouth.utils.SentenceTools;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;

import edu.drexel.psal.jstylo.GUI.DocsTabDriver.ExtFilter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
/**
 * editorTabDriver does the work for the editorTab (Editor) in the main GUI (GUIMain)
 * @author Andrew W.E. McDonald
 * @author Joe Muoio
 * @author Marc Barrowclift
 * 
 */
public class DriverDocumentsTab {
	
	private final static String NAME = "( DriverDocumentsTab ) - ";
	
	public final static int UNDOCHARACTERBUFFER = 5;
	public static int currentCharacterBuffer = 0;
	
	protected static SentenceTools sentenceTools;
	
	public static boolean isUsingNineFeatures = false;
	protected static boolean hasBeenInitialized = false;
	protected static String[] condensedSuggestions;
	protected static int numEdits = 0;
	protected static boolean isFirstRun = true; 
	protected static DataAnalyzer wizard;
	private static DocumentMagician magician;
	protected static String[] theFeatures;
	protected static ArrayList<HighlightMapper> highlightedObjects = new ArrayList<HighlightMapper>();
	public static int resultsMaxIndex;
	public static Object maxValue;
	public static String chosenAuthor = "n/a";
	protected static Attribute currentAttrib;
	public static boolean hasCurrentAttrib = false;
	public static boolean isWorkingOnUpdating = false;
	// It seems redundant to have these next four variables, but they are used in slightly different ways, and are all necessary.
	private static int currentCaretPosition = -1;
	public static int startSelection = -1;
	public static int endSelection = -1;
	private static int lastCaretPosition = -1;
	private static int thisKeyCaretPosition = -1;
	private static int lastKeyCaretPosition = -1;
	protected static boolean okayToSelectSuggestion = false;
	private static boolean keyJustTyped = false;
	private static boolean keyJustPressed = false;
	private static int mouseEndPosition;
	private static boolean checkForMouseInfluence =false;
	protected static int selectedIndexTP;
	protected static int sizeOfCfd;
	protected static boolean consoleDead = true;
	protected static boolean dictDead = true;
	protected static ArrayList<String> featuresInCfd;
	protected static String selectedFeature;
	protected static boolean shouldReset = false;
	protected static boolean isCalcHist = false;
	protected static ArrayList<FeatureList> noCalcHistFeatures;
	protected static ArrayList<FeatureList> yesCalcHistFeatures;
	protected static String searchBoxInputText;
	public static Attribute[] attribs;
	public static HashMap<FeatureList,Integer> attributesMappedByName;
	public static HashMap<Integer,Integer> suggestionToAttributeMap;
	protected static ConsolidationStation consolidator;
	
	private static String cleanWordRegex=".*([\\.,!?])+";//REFINE THIS??

	private static final Color HILIT_COLOR = new Color(255,0,0,100);//Color.yellow; //new Color(50, 161,227);// Color.blue;
	protected static DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255,255,0,128));
	protected static DefaultHighlighter.DefaultHighlightPainter painterRemove = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
	protected static DefaultHighlighter.DefaultHighlightPainter painterAdd = new DefaultHighlighter.DefaultHighlightPainter(new Color(0,255,0,128));

	protected static Translation translator = new Translation();
	
	public static TaggedDocument taggedDoc;
	protected static Map<String, TaggedSentence> originals = new HashMap<String, TaggedSentence>();
	protected static ArrayList<String> originalSents = new ArrayList<String>();
	private static int currentSentNum = 0;
	protected static int lastSentNum = -1;
	protected static int sentToTranslate = 0;
	protected static int[] selectedSentIndexRange = new int[]{-2,-2}; 
	protected static int[] lastSelectedSentIndexRange = new int[]{-3,-3};
	protected static int lastCaretLocation = -1;
	protected static int charsInserted = -1;
	protected static int charsRemoved = -1;
	protected static String currentSentenceString = "";
	protected static Object currentHighlight = null;
	protected static int ignoreNumActions = 0;
	protected static int caretPositionPriorToCharInsertion = 0;
	protected static int caretPositionPriorToCharRemoval = 0;
	protected static int caretPositionPriorToAction = 0;
	private static Boolean firstRun = true;
	private static int[] oldSelectionInfo = new int[3];
	protected static Map<String, int[]> wordsToRemove = new HashMap<String, int[]>();
	
	protected static SuggestionCalculator suggestionCalculator;
	protected static Boolean shouldRememberIndices = true;
	
	protected static ActionListener saveAsTestDoc;
	
	public static int getCurrentSentNum(){
		return currentSentNum;
	}
	
	protected static void signalTargetsSelected(GUIMain main, boolean goodToGo){
		if(goodToGo == true)
			BackendInterface.postTargetSelectionProcessing(main, wizard);
	}
	
	
	protected static void doTranslations(ArrayList<TaggedSentence> sentences, GUIMain main)
	{
		GUIMain.GUITranslator.load(sentences);
	}
	
	
	protected static boolean checkSentFor(String currentSent, String str) 
	{
		@SuppressWarnings("resource")
		Scanner parser = new Scanner(currentSent);
		boolean inSent = false;
		String tempStr;
		while(parser.hasNext())
		{
			tempStr = parser.next();
			if(tempStr.matches(cleanWordRegex))
				tempStr = tempStr.substring(0,tempStr.length()-1);
			
			if(tempStr.equalsIgnoreCase(str))
			{
				inSent = true;
				break;
			}
		}
		return inSent;
	}
	
	/**
	 * Sets all the components within the editor inner tab spawner to disabled, except for the Process button.
	 * @param b boolean determining if the components are enabled or disabled
	 * @param main GUIMain object
	 */
	public static void setAllDocTabUseable(boolean b, GUIMain main) {
		main.saveButton.setEnabled(b);
		main.fileSaveTestDocMenuItem.setEnabled(b);
		main.fileSaveAsTestDocMenuItem.setEnabled(b);
		main.viewClustersMenuItem.setEnabled(b);
		main.elementsToAddPane.setEnabled(b);
		main.elementsToRemovePane.setEnabled(b);
		main.getDocumentPane().setEnabled(b);
		main.getDocumentPane().setEditable(b);
	}
	
	/**
	 * Removes the sentence at index sentenceNumberToRemove in the current TaggedDocument, and replaces it with the sentenceToReplaceWith.
	 * Then, converts the updated TaggedDocument to a string, and puts the new version in the editor window.
	 * @param main
	 * @param sentenceNumberToRemove
	 * @param sentenceToReplaceWith
	 * @param shouldUpdate if true, it replaces the text in the JTextPane (documentPane) with the text in the TaggedDocument (taggedDoc).
	 */
	protected static void removeReplaceAndUpdate(GUIMain main, int sentenceNumberToRemove, String sentenceToReplaceWith, boolean shouldUpdate) {
		if (currentCharacterBuffer >= UNDOCHARACTERBUFFER) {
			main.versionControl.addVersion(taggedDoc);
			currentCharacterBuffer = 0;
		} else
			currentCharacterBuffer += 1;
		
		System.out.println("\n\ntaggedDoc (pre remove and replace [num sentences == "+taggedDoc.getNumSentences()+"]):\n\n"+taggedDoc.getUntaggedDocument(false)+"\n\n");
		taggedDoc.removeAndReplace(sentenceNumberToRemove, sentenceToReplaceWith);
		System.out.println("\n\ntaggedDoc (post remove and replace [num sentences == "+taggedDoc.getNumSentences()+"]):\n\n"+taggedDoc.getUntaggedDocument(false)+"\n\n");

		if (shouldUpdate){
			ignoreNumActions = 3;
			main.getDocumentPane().setText(taggedDoc.getUntaggedDocument(false)); // NOTE should be false after testing!!!
			main.getDocumentPane().getCaret().setDot(caretPositionPriorToAction);
			main.getDocumentPane().setCaretPosition(caretPositionPriorToAction);	
		}
		System.out.println("caretPositionPriorToAction (in removeReplaceAndUpdate): "+caretPositionPriorToAction);
		int[] selectionInfo = calculateIndicesOfSentences(currentCaretPosition)[0];
		currentSentNum = selectionInfo[0];
		selectedSentIndexRange[0] = selectionInfo[1]; //start highlight
		selectedSentIndexRange[1] = selectionInfo[2]; //end highlight
		System.out.printf("highlighting from %d to %d, selected sent. num is %d\n",selectionInfo[1],selectionInfo[2],selectionInfo[0]);
		moveHighlight(main,selectedSentIndexRange,true);
		
		main.versionControl.setMostRecentState(taggedDoc);
	}
	
	/**
	 * Does the same thing as <code>removeReplaceAndUpdate</code>, except it doesn't remove and replace. 
	 * It simply updates the text editor box with the contents of <code>taggedDoc</code>,
	 * sets the caret to <code>caretPositionPriorToCharInsertion</code>,
	 * and moves the highlight the sentence that the caret has been moved to.
	 * @param main
	 * @param shouldUpdate
	 */
	public static void update(GUIMain main, Boolean shouldUpdate) {
		if (shouldUpdate) {
			ignoreNumActions = 3;
			main.getDocumentPane().setText(taggedDoc.getUntaggedDocument(false));
			main.getDocumentPane().getCaret().setDot(caretPositionPriorToCharInsertion);
			main.getDocumentPane().setCaretPosition(caretPositionPriorToCharInsertion);	
		}
		
		int[] selectionInfo = calculateIndicesOfSentences(caretPositionPriorToCharInsertion)[0];
		currentSentNum = selectionInfo[0];
		selectedSentIndexRange[0] = selectionInfo[1]; //start highlight
		selectedSentIndexRange[1] = selectionInfo[2]; //end highlight

		moveHighlight(main,selectedSentIndexRange,true);
	}

	/**
	 * resets the highlight to a new start and end.
	 * @param main 
	 * @param start
	 * @param end
	 */
	protected static void moveHighlight(final GUIMain main, int[] bounds, boolean deleteCurrent){
		if (deleteCurrent){
			if (currentHighlight != null)
				main.getDocumentPane().getHighlighter().removeHighlight(currentHighlight);
			try {
				System.out.printf("Moving highlight to %d to %d\n", bounds[0],bounds[1]);
				currentHighlight = main.getDocumentPane().getHighlighter().addHighlight(bounds[0], bounds[1], painter);
			} 
			catch (BadLocationException err) {
				err.printStackTrace();
			}	
		}
		else{
			try {
				System.out.println("Changing highlight...");
				main.getDocumentPane().getHighlighter().changeHighlight(currentHighlight,bounds[0], bounds[1]);
			} 
			catch (BadLocationException err) {
				err.printStackTrace();
			}	
		}
	}

	/**
	 * Calcualtes the selected sentence number (index in TaggedDocument taggedDoc), start of that sentence in the documentPane, and end of the sentence in the documentPane. 
	 * Returns all three values in an int array.
	 * @param currentCaretPosition the positions in the document to return sentence indices for
	 * @return a 2d int array such that each row is an array such that: index 0 is the sentence index, index 1 is the beginning of the sentence (w.r.t. the whole document in the editor), and index 2 is the end of the sentence.
	 * {sentenceNumber, startHighlight, endHighlight} (where start and end Highlight are the starting and ending indices of the selected sentence). The rows correspond to the order of the input indices
	 * 
	 * If 'currentCaretPosition' is past the end of the document (greater than the number of characters in the document), then "null" will be returned.
	 */
	public static int[][] calculateIndicesOfSentences(int ... positions){
		// get the lengths of each of the sentences
		int[] sentenceLengths = taggedDoc.getSentenceLengths();
		int numSents = sentenceLengths.length;
		int positionNumber;
		int numPositions = positions.length;
		int currentPosition;
		int[][] results = new int[numPositions][3];
		for (positionNumber = 0; positionNumber < numPositions; positionNumber++){
			int i = 0;
			int lengthSoFar = 0;
			int[] lengthTriangle = new int[numSents]; // index '0' will be the length of sentence 0, index '1' will be the length of sentence '0' plus sentence '1', index '2' will be the lengths of the first three sentences added together, and so on. 
			int selectedSentence = 0;
			currentPosition = positions[positionNumber];
			if(currentPosition > 0){
				while (lengthSoFar <= currentPosition && i <= numSents){
					lengthSoFar += sentenceLengths[i];
					lengthTriangle[i] = lengthSoFar;
					i++;
				}
				selectedSentence = i - 1;// after exiting the loop, 'i' will be one greater than we want it to be.
			}
			int startHighlight = 0;
			int endHighlight = 0;
			if (selectedSentence >= numSents)
				return null; // don't do anything.
			else if (selectedSentence <= 0)
				endHighlight = sentenceLengths[0];
			else{
				startHighlight = lengthTriangle[selectedSentence-1]; // start highlighting JUST after the previous sentence stops
				endHighlight = lengthTriangle[selectedSentence]; // stop highlighting when the current sentence stops.
			}	
			results[positionNumber] = new int[]{selectedSentence, startHighlight, endHighlight};
		}
		return results; 
	}


	private static void displayEditInfo(DocumentEvent e) {
		javax.swing.text.Document document = (javax.swing.text.Document) e.getDocument();
		int changeLength = e.getLength();
		System.out.println(e.getType().toString() + ": " + changeLength + " character(s). Text length = " + document.getLength() + ".");
	}

	protected static void initListeners(final GUIMain main){

		/***********************************************************************************************************************************************
		 *############################################################################################################*
		 *###########################################  BEGIN EDITING HANDLERS  ###########################################*
		 *############################################################################################################*
		 ************************************************************************************************************************************************/	

		suggestionCalculator = new SuggestionCalculator();
		
		/*
		 * 
		 * xxx xxx xxx xxx xxx xxx xxx
		 * todo todo intercept keys with document thing
		 * xxx xxx xxx xxx xxx xxx xxx
		 * 
		 */

		main.getDocumentPane().addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				System.out.println("\n\n\ncaretUpdate fired.");
				if (ignoreNumActions > 0){
					System.out.println("ignoring caretUpdate...");
					charsInserted = 0;
					charsRemoved = 0;
					ignoreNumActions--;
				}
				else if (taggedDoc != null) { //main.documentPane.getText().length() != 0
					System.out.println("handling caretUpdate...");
					boolean setSelectionInfoAndHighlight = true;
					startSelection = e.getDot();
					endSelection = e.getMark();
					currentCaretPosition = startSelection;
					int[] currentSentSelectionInfo = null;
					caretPositionPriorToCharInsertion = currentCaretPosition - charsInserted;
					caretPositionPriorToCharRemoval = currentCaretPosition + charsRemoved;
					System.out.printf("caretPositionPriorToCharInsertion == %d, currentCaretPosition == %d, charsInserted == %d\n", caretPositionPriorToCharInsertion, currentCaretPosition, charsInserted);
					System.out.printf("caretPositionPriorToCharRemoval == %d, currentCaretPosition == %d, charsRemoved == %d\n", caretPositionPriorToCharRemoval, currentCaretPosition, charsRemoved);
					if (charsRemoved > 0){
						caretPositionPriorToAction = caretPositionPriorToCharRemoval;
						System.out.println("characters removed...");
						// update the EOSTracker, and from the value that it returns we can tell if sentences are being merged (EOS characters are being erased)
						boolean EOSesRemoved = taggedDoc.specialCharTracker.removeEOSesInRange( currentCaretPosition, caretPositionPriorToCharRemoval);
						if (EOSesRemoved){
							
							// xxx todo xxx put in a check for:
								// - if an EOS character was deleted inside of quotation marks, we don't want to delete anything.
								// - if an EOS character was deleted from a sentence that ends with "?!", we want to wait until the remove both EOS characters (and other similar situations)
							
							
							// note that 'currentCaretPosition' will always be less than 'caretPositionPriorToCharRemoval' if characters were removed!
							int[][] activatedSentenceInfo = calculateIndicesOfSentences(currentCaretPosition, caretPositionPriorToCharRemoval);
							int i;
							int j = 0;
							int numInfos = activatedSentenceInfo.length;
							int[] leftSentInfo = activatedSentenceInfo[0];
							int[] rightSentInfo = activatedSentenceInfo[1];
							int numToDelete = rightSentInfo[0] - (leftSentInfo[0]+1); // add '1' because we don't want to count the lower bound (e.g. if midway through sentence '6' down to midway through sentence '3' was deleted, we want to delete "6 - (3+1) = 2" TaggedSentences. 
							int[] taggedSentsToDelete = new int[numToDelete];
							
							// Now we list the indices of sentences that need to be removed, which are the ones between the left and right sentence (though not including either the left or the right sentence).
							for (i = (leftSentInfo[0] + 1); i < rightSentInfo[0]; i++){ 
								taggedSentsToDelete[j] = i;
								j++;
							}
							
							//First delete what we don't need anymore
							TaggedSentence[] taggedSentsJustDeleted = taggedDoc.removeTaggedSentences(taggedSentsToDelete); // XXX XXX can stop saving the return value after testing!!!!
							System.out.println("Just removed the following TaggedSentences:");
							for(i = 0; i < numToDelete; i++)
								System.out.println("TS #"+taggedSentsToDelete[i]+" ==> "+taggedSentsJustDeleted[i]);
							
							// Then read the remaining strings from "left" and "right" sentence:
								// for left: read from 'leftSentInfo[1]' (the beginning of the sentence) to 'currentCaretPosition' (where the "sentence" now ends)
								// for right: read from 'caretPositionPriorToCharRemoval' (where the "sentence" now begins) to 'rightSentInfo[2]' (the end of the sentence) 
							// Once we have the string, we call removeAndReplace, once for each sentence (String)
							String docText = main.getDocumentPane().getText();
							String leftSentCurrent = docText.substring(leftSentInfo[1],currentCaretPosition);
							taggedDoc.removeAndReplace(leftSentInfo[0], leftSentCurrent);
							String rightSentCurrent = docText.substring((caretPositionPriorToCharRemoval-charsRemoved), (rightSentInfo[2]-charsRemoved));//we need to shift our indices over by the number of characters removed.
							taggedDoc.removeAndReplace(rightSentInfo[0], rightSentCurrent);
							
							System.out.printf("Merging <%s> (indices: [ %d, %d]) and <%s> (indices: [ %d, %d])\n",leftSentCurrent, leftSentInfo[1], currentCaretPosition+1, rightSentCurrent, caretPositionPriorToCharRemoval, rightSentInfo[2]+1);
							
							// Now that we have internally gotten rid of the parts of left and right sentence that no longer exist in the editor box, we merge those two sentences so that they become a single TaggedSentence.
							taggedDoc.concatRemoveAndReplace( taggedDoc.getTaggedDocument().get(leftSentInfo[0]),leftSentInfo[0], taggedDoc.getTaggedDocument().get(rightSentInfo[0]), rightSentInfo[0]);
							System.out.printf("Now sentence number '%d' looks like <%s> in the TaggedDocument.\n",leftSentInfo[0],taggedDoc.getTaggedSentences().get(leftSentInfo[0]));
							
							// now update the EOSTracker
							System.out.println("---updating EOSTracker---");
							taggedDoc.specialCharTracker.shiftAllEOSChars(false, caretPositionPriorToCharRemoval, charsRemoved);
							
							// Then update the currentSentSelectionInfo, and fix variables
							currentSentSelectionInfo = calculateIndicesOfSentences(currentCaretPosition)[0];
							currentSentNum = currentSentSelectionInfo[0];
							selectedSentIndexRange[0] = currentSentSelectionInfo[1];
							selectedSentIndexRange[1] = currentSentSelectionInfo[2];
							
							// Now set the number of characters removed to zero because the action has been dealt with, and we don't want the statement further down to execute and screw up our indices. 
							charsRemoved = 0; 
							
						} else{
							// update the EOSTracker
							System.out.println("---updating EOSTracker---");
							taggedDoc.specialCharTracker.shiftAllEOSChars(false, caretPositionPriorToAction, charsRemoved);
						}
					}
					else if (charsInserted > 0){
						System.out.println("characters inserted...");
						caretPositionPriorToAction = caretPositionPriorToCharInsertion;
						// update the EOSTracker. First shift the current EOS objects, and then create a new one 
						System.out.println("---updating EOSTracker---");
						taggedDoc.specialCharTracker.shiftAllEOSChars(true, caretPositionPriorToAction, charsInserted);
						// MUST ADD EOS
						boolean EOSesAdded = false;// xxx this line is temporary.
						if (EOSesAdded){
							// then we want to wait before creating a new TaggedSentence to see if the user is going to enter more than one EOS character. Only once they stop inputting EOS characters do we want to replace the sentence in the taggedDoc.
							// This is because if someone enters a ".", and actually wants ellipsis points, "...", or if someone enters a "!", but plans on ending their sentence with "!?", or if someone has an open quote (CHECK FOR THIS), 
							// then we don't want to create a new sentence until they stop entering EOS characters, or close the quote.
						}
					}
					else{
						caretPositionPriorToAction = currentCaretPosition;
					}
					// Then update the selection information so that when we move the highlight, it highlights "both" sentences (well, what used to be both sentences, but is now a single sentence)
					currentSentSelectionInfo = calculateIndicesOfSentences(currentCaretPosition)[0];
					System.out.println("caretPositionPriorToAction == "+caretPositionPriorToAction);
					
					if (currentSentSelectionInfo == null)
						return; // don't do anything.
					
					if (shouldRememberIndices) { // regarding storing indices for 
						main.versionControl.updateIndices(startSelection, endSelection);
					}
					
					if (charsInserted > 2 || charsInserted < -2)
						main.versionControl.addVersion(taggedDoc);
					
					
					System.out.printf("previousSentenceNumber == %d\n", currentSentNum);
					System.out.printf("currentSentSelectionInfo: sentNum == %d, start == %d, end == %d\n",currentSentSelectionInfo[0], currentSentSelectionInfo[1], currentSentSelectionInfo[2]);
					System.out.printf("selectedSentIndexRange: start == %d,  end == %d\n", selectedSentIndexRange[0], selectedSentIndexRange[1]);
					lastSentNum = currentSentNum;
					currentSentNum = currentSentSelectionInfo[0];

					boolean inRange = false;
					
					//check to see if the current caret location is within the selectedSentIndexRange ([0] is min, [1] is max)
					if ( caretPositionPriorToAction >= selectedSentIndexRange[0] && caretPositionPriorToAction < selectedSentIndexRange[1]) {
						inRange = true;
						// Caret is inside range of presently selected sentence.
						// update from previous caret
						if (charsInserted > 0 ){// && lastSentNum != -1){
							System.out.println("selectedSentIndexRange[1] (pre addition) == "+selectedSentIndexRange[1]);
							selectedSentIndexRange[1] += charsInserted;
							System.out.println("selectedSentIndexRange[1] (post addition) == "+selectedSentIndexRange[1]);
							charsInserted = ~-1; // puzzle: what does this mean? (scroll to bottom of file for answer) - AweM
						}
						else if (charsRemoved > 0){// && lastSentNum != -1){
							System.out.println("selectedSentIndexRange[1] (pre subtraction) == "+selectedSentIndexRange[1]);
							selectedSentIndexRange[1] -= charsRemoved;
							System.out.println("selectedSentIndexRange[1] (post subtraction) == "+selectedSentIndexRange[1]);
							charsRemoved = 0;
						}
					}
					else if (!firstRun) {
						/**
						 * Exists for the sole purpose of pushing a sentence that has been edited and finished to the appropriate place in
						 * The Translation.java class so that it can be promptly translated. This will ONLY happen when the user has clicked
						 * away from the sentence they were editing to work on another one (the reason behind this being we don't want to be
						 * constantly pushing now sentences to be translated is the user's immediately going to replace them again, we only
						 * want to translate completed sentences).
						 */
						if (!originals.keySet().contains(main.getDocumentPane().getText().substring(selectedSentIndexRange[0],selectedSentIndexRange[1]))) {
							GUIMain.GUITranslator.replace(taggedDoc.getSentenceNumber(oldSelectionInfo[0]), originals.get(originalSents.get(oldSelectionInfo[0])));//new old
							main.anonymityDrawingPanel.updateAnonymityBar();
							originals.remove(originalSents.get(oldSelectionInfo[0]));
							originals.put(taggedDoc.getSentenceNumber(oldSelectionInfo[0]).getUntagged(false), taggedDoc.getSentenceNumber(oldSelectionInfo[0]));
							originalSents.remove(oldSelectionInfo[0]);
							originalSents.add(taggedDoc.getSentenceNumber(oldSelectionInfo[0]).getUntagged(false));
							SuggestionCalculator.placeSuggestions(main);
						}
					}
					
					// selectionInfo is an int array with 3 values: {selectedSentNum, startHighlight, endHighlight}
					
					// xxx todo xxx get rid of this check (if possible... BEI sets the selectedSentIndexRange)....
					
					
					if (firstRun){ //NOTE needed a way to make sure that the very first time a sentence is clicked (, we didn't break stuff... this may not be the best way...
						firstRun = false;
					} else {
						lastSelectedSentIndexRange[0] = selectedSentIndexRange[0];
						lastSelectedSentIndexRange[1] = selectedSentIndexRange[1];
						System.out.printf("lastSelectedSentIndexRange: start == %d,  end == %d\n", selectedSentIndexRange[0], selectedSentIndexRange[1]);
						currentSentenceString = main.getDocumentPane().getText().substring(lastSelectedSentIndexRange[0],lastSelectedSentIndexRange[1]);
						System.out.println("Current sentence String: \""+currentSentenceString+"\"");
						System.out.println("taggedDoc, sentNum == "+lastSentNum+": \"" + taggedDoc.getSentenceNumber(lastSentNum).getUntagged(false) + "\"");
						//If the sentence didn't change, we don't have to remove and replace it
						if (!taggedDoc.getSentenceNumber(lastSentNum).getUntagged(false).equals(currentSentenceString)) {
							removeReplaceAndUpdate(main, lastSentNum, currentSentenceString, false);
							main.anonymityDrawingPanel.updateAnonymityBar();
							setSelectionInfoAndHighlight = false;
							GUIMain.saved = false;
						}
						
					}
					if(setSelectionInfoAndHighlight){
						currentSentSelectionInfo = calculateIndicesOfSentences(caretPositionPriorToAction)[0];
						System.out.printf("currentSentSelectionInfo (post removeAndReplace): sentNum == %d, start == %d, end == %d\n",currentSentSelectionInfo[0], currentSentSelectionInfo[1], currentSentSelectionInfo[2]);
						selectedSentIndexRange[0] = currentSentSelectionInfo[1]; //start highlight
						selectedSentIndexRange[1] = currentSentSelectionInfo[2]; //end highlight
						if(!inRange)
							moveHighlight(main,selectedSentIndexRange,true);
						else
							moveHighlight(main,selectedSentIndexRange,false);
					}

					sentToTranslate = currentSentNum;
					if (!inRange)
						DriverTranslationsTab.showTranslations(taggedDoc.getSentenceNumber(sentToTranslate));
					oldSelectionInfo = currentSentSelectionInfo;
				}
			}
		});
		
		
		/**
		 * Key listener for the documentPane. Allows tracking the cursor while typing to make sure that indices of sentence start and ends 
		 */
		main.getDocumentPane().addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				//System.out.println("keyPressed"+System.currentTimeMillis());
				keyJustPressed = true;
				shouldRememberIndices = false;
				lastKeyCaretPosition = thisKeyCaretPosition;
			}
			
			/*
			 * TODO: make the highlighter and sentences track when people type. think about copy and paste and cut and paste too.
			 */			
			@Override
			public void keyReleased(KeyEvent arg0) {  
				/* 	Code		|	key
				 * ----------------
				 * 		8			|	Backspace
				 *		10			|	Enter
				 *		9			|	Tab
				 *		27			|	Escape
 				 *		32			|	Space
 				 *
 				 * Codes 
				 */
				thisKeyCaretPosition = main.getDocumentPane().getCaretPosition(); // todo maybe we dont need to call for this.. all we might have to do is get the dot position from the CaretListener
				//System.out.println("Caret postion resitered at keyreleased:  "+currentCaretPosition);
				if(keyJustTyped == true){
					keyJustTyped = false;
					char keyChar = arg0.getKeyChar();
					int keyCode = arg0.getKeyCode();
					int keyLocation = arg0.getKeyLocation();
					String keyText = KeyEvent.getKeyText(keyCode);
					//System.out.printf("key char: <%c> key code: <%d> key location: <%d> key text: <%s>\n",keyChar, keyCode, keyLocation, keyText);
					//System.out.printf("%c		%d		%d		%s\n",keyChar, keyCode, keyLocation, keyText);
					//System.out.println("Should start present features continuous present value update thread..");
					//BackendInterface.updatePresentFeatureNow(main, eits,theChief);
					//int caretPos =main.editorBox.getCaretPosition();
					//System.out.println("Old Caret Postion: "+oldCaretPosition+" and current CARET POSITION IS: "+currentCaretPosition);
					//Collections.sort(highlightedObjects);
					if(lastKeyCaretPosition < thisKeyCaretPosition){
						// cursor has advanced 
						Iterator<HighlightMapper> hloi = highlightedObjects.iterator();
						boolean isGone;
						while(hloi.hasNext()){
							isGone = false;
							HighlightMapper tempHm = hloi.next();
							if((tempHm.getStart() <= thisKeyCaretPosition) && (lastKeyCaretPosition <= tempHm.getEnd())){
								//System.out.println("FOUND object... start at: "+tempHm.getStart()+" end at: "+tempHm.getEnd());
								main.getDocumentPane().getHighlighter().removeHighlight(tempHm.getHighlightedObject());
								isGone = true;
							}	
							if ((lastKeyCaretPosition <= tempHm.getStart() && !isGone))
								tempHm.increment(thisKeyCaretPosition - lastKeyCaretPosition);
						}
					}
					else if(lastKeyCaretPosition > thisKeyCaretPosition){
						Iterator<HighlightMapper> hloi = highlightedObjects.iterator();
						boolean isGone;
						while(hloi.hasNext()){
							isGone = false;
							HighlightMapper tempHm = hloi.next();
							if((tempHm.getStart() <= thisKeyCaretPosition) && (thisKeyCaretPosition <= tempHm.getEnd())){
								//System.out.println("FOUND object ... start at: "+tempHm.getStart()+" end at: "+tempHm.getEnd());
								main.getDocumentPane().getHighlighter().removeHighlight(tempHm.getHighlightedObject());
								isGone = true;
							}	
							if ((lastKeyCaretPosition <= tempHm.getStart()) && !isGone)
								tempHm.decrement(lastKeyCaretPosition - thisKeyCaretPosition);
						}
					
					}
				}
			}
			

			@Override
			public void keyTyped(KeyEvent arg0) {
				keyJustTyped = true;
			}
		});
		
		
		main.getDocumentPane().getDocument().addDocumentListener(new DocumentListener(){
		
			@Override
			public void insertUpdate(DocumentEvent e) {
				charsInserted = e.getLength();		
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				charsRemoved = e.getLength();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				DriverDocumentsTab.displayEditInfo(e);
			}
		});
			
		
		main.getDocumentPane().addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent me) {
		
			}

			@Override
			public void mousePressed(MouseEvent me) {
				
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				
			}

			@Override
			public void mouseEntered(MouseEvent me) {
				
			}

			@Override
			public void mouseExited(MouseEvent me) {
				
			}
			
		});
		
/***********************************************************************************************************************************************
 *############################################################################################################*
 *###########################################   END EDITING HANDLERS   ########################################### *
 *############################################################################################################*
 ************************************************************************************************************************************************/
		
		/**
		 * ActionListener for process button (bar).
		 */
		main.processButton.addActionListener(new ActionListener() 
		{
			@Override
			public synchronized void actionPerformed(ActionEvent event) 
			{
				// ----- check if all requirements for processing are met
				String errorMessage = "";
				if (!main.mainDocReady())
					errorMessage += "Main document not provided.\n";
				if (!main.sampleDocsReady())
					errorMessage += "Sample documents not provided.\n";
				if (!main.trainDocsReady())
					errorMessage += "Train documents not provided.\n";
				if (!main.featuresAreReady())
					errorMessage += "Feature set not chosen.\n";
				if (!main.classifiersAreReady())
					errorMessage += "Classifier not chosen.\n";
				
				// ----- display error message if there are errors
				if (errorMessage != "") {
					JOptionPane.showMessageDialog(main, errorMessage, "Settings Error!",
						    JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					main.leftTabPane.setSelectedIndex(0);
					// ----- confirm they want to process
					if (true) // ---- can be a confirm dialog to make sure they want to process.
					{
						// ----- if this is the first run, do everything that needs to be ran the first time
						if(isFirstRun) {
							// ----- create the main document and add it to the appropriate array list.
							// ----- may not need the arraylist in the future since you only really can have one at a time
							TaggedDocument taggedDocument = new TaggedDocument();
							ConsolidationStation.toModifyTaggedDocs=new ArrayList<TaggedDocument>();
							ConsolidationStation.toModifyTaggedDocs.add(taggedDocument);
							taggedDoc = ConsolidationStation.toModifyTaggedDocs.get(0);

							Logger.logln(NAME+"Initial processing starting...");
							
							// initialize all arraylists needed for feature processing
							sizeOfCfd = main.cfd.numOfFeatureDrivers();
							featuresInCfd = new ArrayList<String>(sizeOfCfd);
							noCalcHistFeatures = new ArrayList<FeatureList>(sizeOfCfd);
							yesCalcHistFeatures = new ArrayList<FeatureList>(sizeOfCfd);
							
							for(int i = 0; i < sizeOfCfd; i++)
							{
								String theName = main.cfd.featureDriverAt(i).getName();
								
								// capitalize the name and replace all " " and "-" with "_"
								theName = theName.replaceAll("[ -]","_").toUpperCase(); 
								if(isCalcHist == false)
								{
									isCalcHist = main.cfd.featureDriverAt(i).isCalcHist();
									yesCalcHistFeatures.add(FeatureList.valueOf(theName));
								} 
								else 
								{
									// these values will go in suggestion list... PLUS any 	
									noCalcHistFeatures.add(FeatureList.valueOf(theName));
								}
								featuresInCfd.add(i,theName);
							}
							wizard = new DataAnalyzer(main.ps,ThePresident.sessionName);
							magician = new DocumentMagician(false);
						} else {
							Logger.logln(NAME+"Repeat processing starting....");
							resetAll(main);
						}

						setAllDocTabUseable(false, main);
						main.getDocumentPane().getHighlighter().removeAllHighlights();
						highlightedObjects.clear();
						highlightedObjects.clear();
						okayToSelectSuggestion = false;
						Logger.logln(NAME+"calling backendInterface for preTargetSelectionProcessing");
						
						BackendInterface.preTargetSelectionProcessing(main,wizard,magician);
					}
				}
			}
		});

		saveAsTestDoc = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				Logger.logln(NAME+"Save As document button clicked.");
				JFileChooser save = new JFileChooser();
				save.setSelectedFile(new File("anonymizedDoc.txt"));
				save.addChoosableFileFilter(new ExtFilter("txt files (*.txt)", "txt"));
				int answer = save.showSaveDialog(main);
				
				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".txt"))
						path += ".txt";
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(path));
						bw.write(main.getDocumentPane().getText());
						bw.flush();
						bw.close();
						Logger.log("Saved contents of current tab to "+path);

						GUIMain.saved = true;
					} catch (IOException exc) {
						Logger.logln(NAME+"Failed opening "+path+" for writing",LogOut.STDERR);
						Logger.logln(NAME+exc.toString(),LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving contents of current tab into:\n"+path,
								"Save Problem Set Failure",
								JOptionPane.ERROR_MESSAGE);
					}
				} 
				else
		            Logger.logln(NAME+"Save As contents of current tab canceled");
			}
		};
		main.saveButton.addActionListener(saveAsTestDoc);
	}

	/**
	 * Resets everything to their default values, to be used before reprocessing
	 * @param main - An instance of GUIMain
	 */
	public static void resetAll(GUIMain main) {
		currentCaretPosition = -1;
		startSelection = -1;
		endSelection = -1;
		thisKeyCaretPosition = -1;
		lastKeyCaretPosition = -1;
		keyJustTyped = false;
		noCalcHistFeatures.clear();
		yesCalcHistFeatures.clear();

		originals.clear();
		originalSents.clear();
		currentSentNum = 0;
		lastSentNum = -1;
		sentToTranslate = 0;
		selectedSentIndexRange = new int[]{-2,-2}; 
		lastSelectedSentIndexRange = new int[]{-3,-3};
		lastCaretLocation = -1;
		charsInserted = -1;
		charsRemoved = -1;
		currentSentenceString = "";
		ignoreNumActions = 0;
		caretPositionPriorToCharInsertion = 0;
		caretPositionPriorToCharRemoval = 0;
		caretPositionPriorToAction = 0;
		oldSelectionInfo = new int[3];
		wordsToRemove.clear();
		
		GUIMain.GUITranslator.reset();	
		DriverTranslationsTab.reset();
		main.versionControl.reset();
		main.anonymityDrawingPanel.reset();
		main.resultsWindow.reset();
		GUIUpdateInterface.updateResultsPrepColor(main);
		main.elementsToRemove.removeAllElements();
		main.elementsToRemove.add(0, "Re-processing, please wait");
		main.elementsToAdd.removeAllElements();
		main.elementsToAdd.add(0, "Re-processing, please wait");
	}
	
	public static void save(GUIMain main) {
		Logger.logln(NAME+"Save document button clicked.");

		String path = main.ps.getTestDocs().get(0).getFilePath();

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(main.getDocumentPane().getText());
			bw.flush();
			bw.close();
			Logger.log("Saved contents of document to "+path);

			GUIMain.saved = true;
		} catch (IOException exc) {
			Logger.logln(NAME+"Failed opening "+path+" for writing",LogOut.STDERR);
			Logger.logln(NAME+exc.toString(),LogOut.STDERR);
			JOptionPane.showMessageDialog(null,
					"Failed saving contents of current tab into:\n"+path,
					"Save Problem Set Failure",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static int getSelection(JOptionPane oPane){
		Object selectedValue = oPane.getValue();

		if(selectedValue != null){
			Object options[] = oPane.getOptions();
			if (options == null){
				return ((Integer) selectedValue).intValue();
			}
			else{
				int i;
				int j;
				for(i=0, j= options.length; i<j;i++){
					if(options[i].equals(selectedValue))
						return i;
				}	
			}
		}
		return 0;
	}
} 

class TheHighlighter extends DefaultHighlighter.DefaultHighlightPainter{
	public TheHighlighter(Color color){
		super(color);
	}
}

class SuggestionCalculator {

	private final static String PUNCTUATION = "?!,.\"`'";
	protected static Highlighter editTracker;
	protected static ArrayList<String> topToRemove;
	protected static ArrayList<String> topToAdd;

	/*
	 * Highlights the sentence that is currently in the editor box in the main document
	 * no return
	 */
	protected static void placeSuggestions(GUIMain main) {
		//We must first clear any existing highlights the user has and remove all existing suggestions
		Highlighter highlight = main.getDocumentPane().getHighlighter();
		int highlightedObjectsSize = DriverDocumentsTab.highlightedObjects.size();

		for (int i = 0; i < highlightedObjectsSize; i++)
			highlight.removeHighlight(DriverDocumentsTab.highlightedObjects.get(i).getHighlightedObject());
		DriverDocumentsTab.highlightedObjects.clear();

		main.elementsToRemove.removeAllElements();
		main.elementsToAdd.removeAllElements();

		//Adding new suggestions
		editTracker = new DefaultHighlighter();
		main.getDocumentPane().setHighlighter(editTracker);

		topToRemove=ConsolidationStation.getPriorityWords(ConsolidationStation.toModifyTaggedDocs, true, .2);
		topToAdd=ConsolidationStation.getPriorityWords(ConsolidationStation.authorSampleTaggedDocs, false, .02);

		System.out.println("topToRemove.size() = " + topToRemove.size());
		System.out.println("topToAdd.size() = " + topToAdd.size());
		
		main.elementsToRemove.removeAllElements();

		int arrSize = topToRemove.size();

		for (int i=0;i<arrSize;i++) {//loops through top to remove list
			if (!topToRemove.get(i).equals("''") && !topToRemove.get(i).equals("``")) {
				if (PUNCTUATION.contains(topToRemove.get(i).trim()))
					main.elementsToRemove.add(i, "Reduce the number of " + topToRemove.get(i) + "'s you use");
				else
					main.elementsToRemove.add(i, topToRemove.get(i));
			}		
		}

		main.elementsToRemovePane.clearSelection();

		main.elementsToAdd.removeAllElements();

		arrSize = topToAdd.size();
		for(int i=0;i<arrSize;i++)
			main.elementsToAdd.add(i, topToAdd.get(i));

		main.elementsToAddPane.clearSelection();
	}


//
//		/**
//		 * Finds the synonyms of the words to remove in the words to add list
//		 */
		protected static void findSynonyms(GUIMain main,String currentSent) {
			String[] tempArr;
			//addTracker = new DefaultHighlighter();
			// TODO make new painter!!! (this one doesn't exist) anymore // painter3 = new DefaultHighlighter.DefaultHighlightPainter(new Color(0,0,255,128));
			String setString,tempStr,synSetString = "";
//			main.addToSentencePane.setHighlighter(addTracker);
			//addTracker.removeAllHighlights();

//			main.elementsToAddPane.repaint();

			setString="";
			int arrSize=topToAdd.size(), index;
			main.elementsToAdd.removeAllElements();
			
			for(int i=0;i<arrSize;i++){//Sets the topToAddElements box
//				setString+=topToAdd.get(i)+"\n";
				main.elementsToAdd.add(i, topToAdd.get(i));
			}
			main.elementsToAddPane.clearSelection();
//			main.elementsToAddPane.setText(setString);
//			synSetString="";
//			boolean inSent;
//			Scanner parser;
//			HashMap<String,Integer> indexMap=new HashMap<String,Integer>();
//			for(String str:topToRemove){
//				tempArr=DictionaryBinding.getSynonyms(str);
//				if(tempArr!=null){
//					//inSent=currentSent.contains(str);
//					inSent = DriverDocumentsTab.checkSentFor(currentSent,str);
//
//					if(inSent)
//						synSetString+=str+"=>";
//					for(int i=0;i<tempArr.length;i++){//looks through synonyms
//						tempStr=tempArr[i];
//						if(inSent){
//							synSetString+=tempStr+", ";
//							for(String addString:topToAdd){
//								if(addString.equalsIgnoreCase(tempStr)){
//									index=synSetString.indexOf(tempStr);
//									indexMap.put(tempStr, index);
//								}
//							}
//						}
//					}
//					if(inSent)
//						synSetString=synSetString.substring(0, synSetString.length()-2)+"\n";
//				}
//			}
//			Scanner sentParser=new Scanner(currentSent);
//			String wordToSearch, wordSynMatch;
//			HashMap<String,String>wordsWithSynonyms=new HashMap<String,String>();
//			boolean added=false;
//			synSetString="";
//			while(sentParser.hasNext()){//loops through every word in the sentence
//				wordToSearch=sentParser.next();
//				tempArr=DictionaryBinding.getSynonyms(wordToSearch);
//				wordSynMatch="";
//
//				if(!wordsWithSynonyms.containsKey(wordToSearch.toLowerCase().trim())){
//					if(tempArr!=null){
//						for(int i=0;i<tempArr.length;i++){//looks through synonyms
//							tempStr=tempArr[i];
//							wordSynMatch+=tempStr+" ";
//							added=false;
//							for(String addString:topToAdd){//loops through the toAdd list
//								if(addString.trim().equalsIgnoreCase(tempStr.trim())){//there is a match in topToAdd!
//									if(!synSetString.contains(wordToSearch))
//										synSetString+=wordToSearch+" => ";
//									else{
//										Logger.logln(NAME+"Did not add this again: "+wordToSearch);
//									}
//									synSetString=synSetString+addString+", ";
//									//index=synSetString.indexOf(tempStr);
//									//indexMap.put(tempStr, index);
//									added=true;
//									break;
//								}
//							}
//
//							if(added){
//								//do something if the word was added like print to the box.
//								synSetString=synSetString.substring(0, synSetString.length()-2)+"\n";
//							}
//						}
//						if(wordSynMatch.length()>2)
//							wordsWithSynonyms.put(wordToSearch.toLowerCase().trim(), wordSynMatch.substring(0, wordSynMatch.length()-1));
//						else
//							wordsWithSynonyms.put(wordToSearch.toLowerCase().trim(), "NO Synonyms");
//					}
//				}
//			}
//			String tempStrToAdd;
//			Word possibleToAdd;
//			double topAnon=0;
//			for(String wordToRem:topToRemove){//adds ALL the synonyms in the wordsToRemove
//				if(wordsWithSynonyms.containsKey(wordToRem)){
//					tempStr=wordsWithSynonyms.get(wordToRem);
//					tempStrToAdd="";
//					parser=new Scanner(tempStr);
//					topAnon=0;
//					while(parser.hasNext()){
//						possibleToAdd=new Word(parser.next().trim());
//						ConsolidationStation.setWordFeatures(possibleToAdd);
//						if(possibleToAdd.getAnonymityIndex()>topAnon){
//							tempStrToAdd=possibleToAdd.getUntagged()+", ";//changed for test
//							topAnon=possibleToAdd.getAnonymityIndex();
//						}
//					}
//					synSetString+=wordToRem+" => "+tempStrToAdd+"\n";
//				}
//			}
//			//main.addToSentencePane.setText(synSetString);
//			//main.addToSentencePane.setCaretPosition(0);
//
//			Iterator iter=indexMap.keySet().iterator();
//			String key;
//
//			while(iter.hasNext()){
//				key=(String) iter.next();
//				index=indexMap.get(key);
//				try {
//					addTracker.addHighlight(index, index+key.length(), painter3);
//				} catch (BadLocationException e) {
//					Logger.logln(NAME+"Problem highlighting the words To add list");
//					e.printStackTrace();
//				}
//			}

			//SynonymReplaceTest.replaceWords(eits);
		}
}

	
/*
 * Answer to puzzle:
 * The "~" is a bitwise "NOT". "-1" (in binary) is represented by all 1's. So, a bitwise 'NOT' makes it equivalent to '0':
 *  
 * ~-1 == 0
 */
	
