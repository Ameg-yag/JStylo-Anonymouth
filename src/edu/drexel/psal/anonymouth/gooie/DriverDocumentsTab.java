package edu.drexel.psal.anonymouth.gooie;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.DataAnalyzer;
import edu.drexel.psal.anonymouth.engine.DocumentMagician;
import edu.drexel.psal.anonymouth.engine.FeatureList;
import edu.drexel.psal.anonymouth.suggestors.HighlightMapList;
import edu.drexel.psal.anonymouth.suggestors.Prophecy;
import edu.drexel.psal.anonymouth.suggestors.TheOracle;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;
import edu.drexel.psal.anonymouth.utils.SentenceTools;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;
import edu.drexel.psal.jstylo.generics.Logger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import com.jgaap.generics.Canonicizer;
import com.jgaap.generics.Document;

/**
 * editorTabDriver does the work for the editorTab (Editor) in the main GUI (GUIMain)
 * @author Andrew W.E. McDonald
 * @author Joe Muoio
 * 
 */
public class DriverDocumentsTab {
	
	private final static String NAME = "( DriverDocumentsTab ) - ";
	
	protected static SentenceTools sentenceTools;
	
	public static boolean isUsingNineFeatures = false;
	protected static boolean hasBeenInitialized = false;
	protected static String[] condensedSuggestions;
	protected static int numEdits = 0;
	protected static boolean isFirstRun = true; 
	protected static DataAnalyzer wizard;
	private static DocumentMagician magician;
	protected static String[] theFeatures;
	protected static Prophecy utterance;
	protected static ArrayList<HighlightMapper> highlightedObjects = new ArrayList<HighlightMapper>();
	public static int resultsMaxIndex;
	public static Object maxValue;
	public static String chosenAuthor = "n/a";
	private static int numSuggestions = -1;
	protected static Attribute currentAttrib;
	public static boolean hasCurrentAttrib = false;
	public static boolean isWorkingOnUpdating = false;
	// It seems redundant to have these next four variables, but they are used in slightly different ways, and are all necessary.
	private static int currentCaretPosition = -1;
	private static int startSelection = -1;
	private static int endSelection = -1;
	private static int lastCaretPosition = -1;
	private static int thisKeyCaretPosition = -1;
	private static int lastKeyCaretPosition = -1;
	protected static boolean okayToSelectSuggestion = false;
	private static boolean keyJustTyped = false;
	private static boolean keyJustPressed = false;
	private static int mouseEndPosition;
	private static boolean checkForMouseInfluence =false;
	//protected static ArrayList<EditorInnerTabSpawner> eitsList = new ArrayList<EditorInnerTabSpawner>();
	//protected static EditorInnerTabSpawner eits;
	protected static int selectedIndexTP;
	protected static int sizeOfCfd;
	protected static boolean consoleDead = true;
	protected static boolean dictDead = true;
	protected static ArrayList<String> featuresInCfd;
	protected static String selectedFeature;
	protected static boolean shouldReset = false;
	protected static boolean isCalcHist = false;
	//protected static ClassifyingProgressBar cpb;
	protected static ArrayList<FeatureList> noCalcHistFeatures;
	protected static ArrayList<FeatureList> yesCalcHistFeatures;
	protected static HighlightMapList[] highlightingOptions;
	protected static String searchBoxInputText;
	public static Attribute[] attribs;
	public static HashMap<FeatureList,Integer> attributesMappedByName;
	public static HashMap<Integer,Integer> suggestionToAttributeMap;
	protected static ConsolidationStation consolidator;
	
	private static String cleanWordRegex=".*([\\.,!?])+";//REFINE THIS??

	protected static ArrayList<String> topToRemove;
	protected static ArrayList<String> topToAdd;
	
	private static final Color HILIT_COLOR = new Color(255,0,0,100);//Color.yellow; //new Color(50, 161,227);// Color.blue;
	protected static DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
	
	protected static Highlighter editTracker;
	protected static Highlighter removeTracker;
	protected static Highlighter addTracker;
	protected static Highlighter.HighlightPainter painter1;
	protected static Highlighter.HighlightPainter painter2;
	protected static Highlighter.HighlightPainter painter4;
	protected static Highlighter.HighlightPainter painter3;
	
	protected static Translation translator = new Translation();
	
	protected static TaggedDocument taggedDoc;
	protected static int currentSentNum = -1;
	protected static int lastSentNum = -1;
	protected static int[] selectedSentIndexRange = new int[]{-2,-2}; 
	protected static int[] lastSelectedSentIndexRange = new int[]{-3,-3};
	protected static int lastCaretLocation = -1;
	protected static int charsInserted = -1;
	protected static int charsRemoved = -1;
	protected static String currentSentenceString = "";
	protected static Object currentHighlight = null;
	
	
	protected static void signalTargetsSelected(GUIMain main, boolean goodToGo){
		if(goodToGo == true)
			BackendInterface.postTargetSelectionProcessing(main, wizard, magician);
	}
	
	
	protected static void doTranslations(ArrayList<TaggedSentence> sentences, GUIMain main)
	{
		main.GUITranslator.load(sentences);
	}
	
	
	private static boolean checkSentFor(String currentSent, String str) 
	{
		// TODO Auto-generated method stub
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
	public static void setAllDocTabUseable(boolean b, GUIMain main)
	{
		//main.processButton.setEnabled(b);
//		main.appendSentenceButton.setEnabled(b);
//		main.nextSentenceButton.setEnabled(b);
//		main.prevSentenceButton.setEnabled(b);
//		main.transButton.setEnabled(b);
//		main.sentenceEditPane.setEditable(b);
//		main.translationEditPane.setEditable(b);
		main.resultsTable.setEnabled(b);
//		main.restoreSentenceButton.setEnabled(b);
//		main.SaveChangesButton.setEnabled(b);
//		main.copyToSentenceButton.setEnabled(b);
//		main.saveButton.setEnabled(b);
//		main.dictButton.setEnabled(b);
//		main.editorHelpTabPane.setEnabled(b);
	}
	
	
	/**
	 * resets the highlight to a new start and end.
	 * @param main 
	 * @param start
	 * @param end
	 */
	protected static void moveHighlight(final GUIMain main, int[] bounds, boolean deleteCurrent){
		if (deleteCurrent){
			main.documentPane.getHighlighter().removeAllHighlights();
	        try {
				currentHighlight = main.documentPane.getHighlighter().addHighlight(bounds[0], bounds[1], painter);
	        } 
	        catch (BadLocationException err) {
				err.printStackTrace();
	        }	
		}
		else{
			try {
				main.documentPane.getHighlighter().changeHighlight(currentHighlight,bounds[0], bounds[1]);
	        } 
	        catch (BadLocationException err) {
				err.printStackTrace();
	        }	
		}
	}
	
	/**
	 * Calcualtes the selected sentence number (index in TaggedDocument taggedDoc), start of that sentence in the documentPane, and end of the sentence in the documentPane. 
	 * Returns all three values in an int array.
	 * @param currentCaretPosition the position of the caret
	 * @return an int array such that index 0 is the sentence index, index 1 is the beginning of the sentence (w.r.t. the whole document in the editor), and index 2 is the end of the sentence.
	 * {sentenceNumber, startHighlight, endHighlight} (where start and end Highlight are the starting and ending indices of the selected sentence).
	 * 
	 * If 'currentCaretPosition' is past the end of the document (greater than the number of characters in the document), then "null" will be returned.
	 */
	public static int[] calculateIndicesOfSelectedSentence(int currentCaretPosition){
		// get the lengths of each of the sentences
		int[] sentenceLengths = taggedDoc.getSentenceLengths();
		int i = 0;
		int numSents = sentenceLengths.length;
		int lengthSoFar = 0;
		int[] lengthTriangle = new int[numSents]; // index '0' will be the length of sentence 0, index '1' will be the length of sentence '0' plus sentence '1', index '2' will be the lengths of the first three sentences added together, and so on. 
		while (lengthSoFar <= currentCaretPosition && i < numSents){
			//System.out.printf("Sentence # %d has length: %d\n",i,sentenceLengths[i]);
			lengthSoFar += sentenceLengths[i];
			lengthTriangle[i] = lengthSoFar;
			i++;
		}
		int selectedSentence = i - 1;// after exiting the loop, 'i' will be one greater than we want it to be.
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
		return new int[]{selectedSentence, startHighlight, endHighlight};
	}

	   
	   private static void displayEditInfo(DocumentEvent e) {
		     javax.swing.text.Document document = (javax.swing.text.Document) e.getDocument();
		     int changeLength = e.getLength();
		     System.out.println(e.getType().toString() + ": " + changeLength + " character(s). Text length = " + document.getLength() + ".");
		   }
/*
	   protected void addBindings() {
		   InputMap inputMap = textPane.getInputMap();

		   // Ctrl-b to go backward one character
		   KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
		   inputMap.put(key, DefaultEditorKit.backwardAction);

		   // Ctrl-f to go forward one character
		   key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
		   inputMap.put(key, DefaultEditorKit.forwardAction);

		   // Ctrl-p to go up one line
		   key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
		   inputMap.put(key, DefaultEditorKit.upAction);

		   // Ctrl-n to go down one line
		   key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
		   inputMap.put(key, DefaultEditorKit.downAction);
		 }
*/	
	
	protected static void initListeners(final GUIMain main){
		
/***********************************************************************************************************************************************
 *############################################################################################################*
 *###########################################  BEGIN EDITING HANDLERS  ###########################################*
 *############################################################################################################*
 ************************************************************************************************************************************************/	
		
		
		main.documentPane.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				if (taggedDoc != null){
					startSelection = e.getDot();
					endSelection = e.getMark();
					currentCaretPosition = startSelection;
					int caretPositionPriorToCharInsert = currentCaretPosition - charsInserted;
					int[] selectionInfo = calculateIndicesOfSelectedSentence(caretPositionPriorToCharInsert); 
					if (selectionInfo == null)
						return; // don't do anything.
					if (startSelection == endSelection){
						// no "selection"
					}
					else if (startSelection < endSelection) {
						// clicked then dragged right
						
					}
					else if (startSelection > endSelection) {
						// clicked then dragged left
						
					}
						
					System.out.printf("CARET INFO: %d to %d\n", startSelection, endSelection);
					boolean inRange = false;
					/*
					 * put in a check to see if the current caret location is within the selectedSentIndexRange ([0] is min, [1] is max)
					 */
					if ( caretPositionPriorToCharInsert >= selectedSentIndexRange[0] && caretPositionPriorToCharInsert <= selectedSentIndexRange[1]){
						inRange = true;
						// Caret is inside range of presently selected sentence.
						// update from previous caret
						if (charsInserted > 0  && lastSentNum != -1){
							keyJustPressed = false;
							System.out.println("Chars inserted");
							selectedSentIndexRange[1] += charsInserted;
							//moveHighlight(main,selectedSentIndexRange,false);
							charsInserted = ~-1; // puzzle: what does this mean? (scroll to bottom of file for answer) - AweM
						}
						else if (charsRemoved > 0 && lastSentNum != -1){
							keyJustPressed = false;
							System.out.println("Chars removed");
							selectedSentIndexRange[1] -= charsRemoved;
							//moveHighlight(main,selectedSentIndexRange,false);
							charsRemoved = 0;
						}
						System.out.printf("selectedSentIndexRange: %d - %d\n",selectedSentIndexRange[0], selectedSentIndexRange[1]);
						//currentSentenceString = main.documentPane.getText().substring(selectedSentIndexRange[0],selectedSentIndexRange[1]+1);
						System.out.println("currentSentenceString: "+currentSentenceString);
					}
					//UNCOMMENT
//					else {
//						main.GUITranslator.isSentenceChange(currentSentenceString);
//					}
					
					// selectionInfo is an int array with 3 values: {selectedSentNum, startHighlight, endHighlight}
					
					lastSentNum = currentSentNum;
					currentSentNum = selectionInfo[0];
					
					if (lastSentNum != -1){ //NOTE needed a way to make sure that the first time a sentence is clicked, we didn't break stuff... this may not be the best way...
						lastSelectedSentIndexRange[0] = selectedSentIndexRange[0];
						lastSelectedSentIndexRange[1] = selectedSentIndexRange[1];
						currentSentenceString = main.documentPane.getText().substring(lastSelectedSentIndexRange[0],lastSelectedSentIndexRange[1]);
						taggedDoc.removeAndReplace(lastSentNum, currentSentenceString);
						selectionInfo = calculateIndicesOfSelectedSentence(caretPositionPriorToCharInsert);
					}
					selectedSentIndexRange[0] = selectionInfo[1]; //start highlight
					selectedSentIndexRange[1] = selectionInfo[2]; //end highlight
					System.out.printf("Moving highlight to range %d-%d\n", selectionInfo[1], selectionInfo[2]);
					main.documentPane.setText(taggedDoc.getUntaggedDocument(false));
					if(!inRange)
						moveHighlight(main,selectedSentIndexRange,true);
					else
						moveHighlight(main,selectedSentIndexRange,false);
					
					//Commented by Andrew, uncomment to get translations back for testing
					//"currentSentNum" used to be "i", but there is no local i variable. I'm assuming I'm
					//supposed to pass the current sentence number.
					DriverTranslationsTab.showTranslations(taggedDoc.getSentenceNumber(currentSentNum));	
				}
			}
			
		});
		
		
		/**
		 * Key listener for the documentPane. Allows tracking the cursor while typing to make sure that indices of sentence start and ends 
		 */
		main.documentPane.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				//System.out.println("keyPressed"+System.currentTimeMillis());
				keyJustPressed = true;
				// TODO Auto-generated method stub
				/*
				if(checkForMouseInfluence == true){
					if(mouseEndPosition > currentCaretPosition)
						oldCaretPosition = currentCaretPosition - (mouseEndPosition-currentCaretPosition);
					else if(mouseEndPosition < currentCaretPosition)
						oldCaretPosition = currentCaretPosition +(currentCaretPosition - mouseEndPosition);
					else
						oldCaretPosition = currentCaretPosition;
				}
				else
				*/
					lastKeyCaretPosition = thisKeyCaretPosition;
						
					
				
				//System.out.println("Caret postion registered at keypressed: old: "+oldCaretPosition);
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
				thisKeyCaretPosition = main.documentPane.getCaretPosition(); // todo maybe we dont need to call for this.. all we might have to do is get the dot position from the CaretListener
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
						//System.out.println("Cursor advanced");
						
						
					}
					else if(lastKeyCaretPosition > thisKeyCaretPosition){
						// cursor has gone back
						//System.out.println("Cursor gone back");
					
					}
				}
			}
			

			@Override
			public void keyTyped(KeyEvent arg0) {
				keyJustTyped = true;
			}
				
			
		
		});
		
		main.documentPane.getDocument().addDocumentListener(new DocumentListener(){
		/*	
			javax.swing.text.Document document = (javax.swing.text.Document) e.getDocument();
		     int changeLength = e.getLength();
		     System.out.println(e.getType().toString() + ": " + changeLength + " character(s). Text length = " + document.getLength() + ".");
		*/
			@Override
			public void insertUpdate(DocumentEvent e) {
				//System.out.println("InsertUpdate"+System.currentTimeMillis());
				DriverDocumentsTab.displayEditInfo(e);
				charsInserted = e.getLength();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				//System.out.println("removeUpdate"+System.currentTimeMillis());
				DriverDocumentsTab.displayEditInfo(e);
				charsRemoved = e.getLength();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				//System.out.println("changedUpdate"+System.currentTimeMillis());
				DriverDocumentsTab.displayEditInfo(e);
			}
			
			
		});
		
			
		main.documentPane.addMouseListener(new MouseListener(){

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
						if(isFirstRun==true)
						{
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
						}
						else
							Logger.logln(NAME+"Repeat processing starting....");
						
						int wekaIsRunningAnswer = wekaIsRunning();
						if(wekaIsRunningAnswer != -1)
						{
							charsInserted = 0; // this gets updated when the document is loaded.
							charsRemoved = 0;	
							main.documentPane.getHighlighter().removeAllHighlights();
							highlightedObjects.clear();
							TheOracle.resetColorIndex();
							main.resultsTablePane.setOpaque(false);
							main.resultsTable.setOpaque(false);
							highlightedObjects.clear();
							okayToSelectSuggestion = false;
							wizard.setNumFeaturesToReturn(wekaIsRunningAnswer);
							Logger.logln(NAME+"calling backendInterface for preTargetSelectionProcessing");
							BackendInterface.preTargetSelectionProcessing(main,wizard,magician);
						}
					}
				}
			}
		});
		
//		main.nextSentenceButton.addActionListener(new ActionListener(){
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0)
//			{
//				TaggedSentence sentence = ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence();
//				if (sentence.hasTranslations())
//				{
//					for (int i = 0; i < main.translationsTable.getRowCount(); i++)
//						main.translationsTable.setValueAt(sentence.getTranslations().get(i).getUntagged(), i, 0);
//				}
//				else
//				{
//					for (int i = 0; i < main.translationsTable.getRowCount(); i++)
//						main.translationsTable.setValueAt("", i, 0);
//				}
//				main.translationEditPane.setText("Current Translation.");
//				main.sentenceEditPane.setText(sentence.getUntagged().trim());
//				main.sentenceEditPane.setCaretPosition(0);
//				highlightSentence(sentence, main, "next");
//			}
//			
//		});
		/*main.refreshButtonEditor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				if(!main.sentenceEditPane.isEditable()){
					if(!main.sentenceEditPane.getText().equals(helpMessege)){
						spawnNew(main);
					}
					else{
						main.sentenceEditPane.setEditable(true);
						main.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
						trackEditSentence(main);
					}
				}
				else{
					Logger.logln(NAME+"Refresh button pressed.");
					if(ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(main.getSentenceEditPane().getText())!=-1){
						trackEditSentence(main);
					}
				}
			}			
			
		});*/
//		main.prevSentenceButton.addActionListener(new ActionListener()
//		{
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0)
//			{
//				TaggedSentence sentence = ConsolidationStation.toModifyTaggedDocs.get(0).getPrevSentence();
//				if (sentence.hasTranslations())
//				{
//					for (int i = 0; i < main.translationsTable.getRowCount(); i++)
//						main.translationsTable.setValueAt(sentence.getTranslations().get(i).getUntagged(), i, 0);
//				}
//				else
//				{
//					for (int i = 0; i < main.translationsTable.getRowCount(); i++)
//						main.translationsTable.setValueAt("", i, 0);
//				}
//				main.translationEditPane.setText("Current Translation.");
//				main.sentenceEditPane.setText(sentence.getUntagged().trim());
//				main.sentenceEditPane.setCaretPosition(0);
//				highlightSentence(sentence, main, "prev");
//			}
//			
//		});
		
//		main.transButton.addActionListener(new ActionListener()
//		{
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0)
//			{
//				TaggedSentence sentence = ConsolidationStation.toModifyTaggedDocs.get(0).getCurrentSentence();
//				if (sentence.hasTranslations())
//				{
//					for (int i = 0; i < main.translationsTable.getRowCount(); i++)
//						main.translationsTable.setValueAt(sentence.getTranslations().get(i).getUntagged(), i, 0);
//				}
//				else
//				{
//					doTranslations(sentence, main);
//				}
////				main.sentenceEditPane.setText(sentence.getUntagged().trim());
//			}
//			
//		});
		
//		main.restoreSentenceButton.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				Logger.logln(NAME+"Sentence at index " + ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber() + " restored.");
//				main.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getCurrentSentence().getUntagged().trim());
//			}
//			
//		});	
//		
//		main.SaveChangesButton.addActionListener(new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				Logger.logln(NAME+"Changes to sentence at index " + taggedDoc.getSentNumber() + " saved to TaggedDocument.");
//				
//				// need to know which sentences are the beginnings of paragraphs
//				TaggedSentence sentence = new TaggedSentence(" " + main.sentenceEditPane.getText());
//				taggedDoc.removeTaggedSentence(taggedDoc.getSentNumber());
//				taggedDoc.addTaggedSentence(sentence, taggedDoc.getSentNumber());
//				
//				for (int i = 0; i < main.translationsTable.getRowCount(); i++)
//					main.translationsTable.setValueAt("", i, 0);
//				main.translationEditPane.setText("Current Translation.");
//				sentence = taggedDoc.getCurrentSentence();
//				main.documentPane.setText(taggedDoc.getUntaggedDocument());
//				main.sentenceEditPane.setText(sentence.getUntagged().trim());
//				highlightSentence(sentence, main, "none");
//				
//					//main.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getCurrentLiveTaggedSentence());
//			}
//			
//		});
//		
//		main.copyToSentenceButton.addActionListener(new ActionListener()
//		{
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0)
//			{
//				String translation = main.translationEditPane.getText();
//				if (!translation.equals("Current Translation."))
//				{
//					main.sentenceEditPane.setText(translation);
//				}
//			}
//			
//		});
		
		
//		main.appendSentenceButton.addActionListener(new ActionListener(){
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0)
//			{
//					Logger.logln(NAME+"Add sentence button pressed.");
//					String tempSent = ConsolidationStation.toModifyTaggedDocs.get(0).addNextSentence(main.sentenceEditPane.getText());
//					//ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(main.getSentenceEditPane().getText());
//					main.sentenceEditPane.setText(tempSent);
//					trackEditSentence(main);
//			}
//			
//		});
		
//		main.dictButton.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Logger.logln(NAME+"dictionary button clicked.");
//				if(dictDead = true){
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						DictionaryConsole inst = new DictionaryConsole();
//						inst.setLocationRelativeTo(null);
//						inst.setVisible(true);
//					}
//				});
//				dictDead = false;
//				}
//			}
//			
//			
//		});
		
//		main.saveButton.addActionListener(new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent e) 
//			{
//				Logger.logln(NAME+"Save document button clicked.");
//				JFileChooser save = new JFileChooser();
//				save.addChoosableFileFilter(new ExtFilter("txt files (*.txt)", "txt"));
//				int answer = save.showSaveDialog(main);
//				
//				if (answer == JFileChooser.APPROVE_OPTION) {
//					File f = save.getSelectedFile();
//					String path = f.getAbsolutePath();
//					if (!path.toLowerCase().endsWith(".txt"))
//						path += ".txt";
//					try {
//						BufferedWriter bw = new BufferedWriter(new FileWriter(path));
//						bw.write(main.documentPane.getText());
//						bw.flush();
//						bw.close();
//						Logger.log("Saved contents of current tab to "+path);
//					} catch (IOException exc) {
//						Logger.logln(NAME+"Failed opening "+path+" for writing",LogOut.STDERR);
//						Logger.logln(NAME+exc.toString(),LogOut.STDERR);
//						JOptionPane.showMessageDialog(null,
//								"Failed saving contents of current tab into:\n"+path,
//								"Save Problem Set Failure",
//								JOptionPane.ERROR_MESSAGE);
//					}
//				} 
//				else
//		            Logger.logln(NAME+"Save contents of current tab canceled");
//			}
//		});
	}
		
		
		public static int wekaIsRunning(){
			if(isUsingNineFeatures == true)
				return 9;
			else{
				numSuggestions = 200;
				return numSuggestions;
			}
		}	
			
		public static int getSelection(JOptionPane oPane){
			Object selectedValue = oPane.getValue();
			//System.out.println("Selected value in Weka is running message is: "+selectedValue+" and cancel option is: "+JOptionPane.CANCEL_OPTION);
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
	
		/*main.exitButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln(NAME+"Exit button pressed within edit tab.");
				main.dispose();
				System.exit(0);
			}
			
		});*/	
		
		/*
		main.clearHighlightingButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln(NAME+"All highlights cleared in GUI editor box.");
				main.documentPane.getHighlighter().removeAllHighlights();
				//main.featureNameLabel.setText("Feature Name: ");
				//main.targetValueField.setText("null");
				//main.presentValueField.setText("null");
				highlightedObjects.clear();
				TheOracle.resetColorIndex();
				
			}
			
		});
		*/
		/*main.editTP.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(shouldReset == false){
				selectedIndexTP = main.editTP.getSelectedIndex();
				Logger.logln(NAME+"Selected inner editor tab number : "+selectedIndexTP);
				eits = eitsList.get(selectedIndexTP);
				
				}
			}
			 
			
		});*/
		/*
		main.getHighlightSelectionBox().addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent act){
				highlightSelectionBoxSelectionNumber = main.getHighlightSelectionBox().getSelectedIndex();
				Logger.logln(NAME+"highlight selection box activity: selection number '"+highlightSelectionBoxSelectionNumber+"'");
				if(main.documentPane.isEditable() == false){
					spawnNew(main);
				}
				
				if (highlightingOptions[highlightSelectionBoxSelectionNumber].toString().contains(SPECIFIC) == true){
					main.searchInputBox.setEnabled(true);
					main.searchInputBox.setText("value");
					main.searchInputBox.setSelectionStart(0);
					main.searchInputBox.setSelectionEnd(main.searchInputBox.getText().length());
					main.searchInputBox.setSelectionColor(Color.yellow);
				} else{
					
					HighlightMapMaker.document = main.documentPane.getText();
					try {
						
						theMirror.highlightRequestedNotSpecific(highlightingOptions[highlightSelectionBoxSelectionNumber].toString());
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					
					
				}
			}
		});
		
		
			
			
			
		/*
		main.searchInputBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent act){
				HighlightMapMaker.document = main.documentPane.getText();

				searchBoxInputText = main.searchInputBox.getText();
				searchBoxInputText = searchBoxInputText.trim();
				searchBoxInputText = searchBoxInputText.replaceAll("\\s+", " ");
				Logger.logln(NAME+"String entered in searchInputBox : "+searchBoxInputText);
				String s ="";
				int index;
				boolean hadSpaces = false;
				while(searchBoxInputText.contains(" ")){
					s += "(";
					index = searchBoxInputText.indexOf(" ");
					s +=searchBoxInputText.substring(0,index)+")-"; 
					searchBoxInputText = searchBoxInputText.substring(index+1);
					hadSpaces = true;
				}
				if(hadSpaces == true){	
					s += "("+searchBoxInputText+")";
				}
				else
					s = searchBoxInputText;
				Logger.logln(NAME+"SEARCH STRING POST PROCESSING: "+s);
				try {
					theMirror.highlightRequestedSpecific(highlightingOptions[highlightSelectionBoxSelectionNumber].toString(), s);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
		});
	*/
		
		/*main.suggestionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			
			@Override
			public void valueChanged(ListSelectionEvent selection) {
				//System.out.println("selection model value is adjusting: "+main.suggestionTable.getSelectionModel().getValueIsAdjusting());
				
				if(okayToSelectSuggestion == true){
					
					//System.out.println("about to call suggestor.");
					if(!main.suggestionTable.getSelectionModel().getValueIsAdjusting()){
						okayToSelectSuggestion =false;
						if(main.sentenceEditPane.isEditable() == false){
							spawnNew(main);
						}
						//System.out.println("Table clicked");
						int suggestionNumber = main.suggestionTable.getSelectedRow();//only one suggestion at a time.
						Logger.logln(NAME+"table row: '"+suggestionNumber+"' selected for suggestion.");
						if(suggestionNumber == - 1)
							main.suggestionTable.clearSelection();
						else{
							if(isUsingNineFeatures == true)
								attribs = wizard.getAttributes();
							main.processButton.setEnabled(false);
							SuggestionCalculator sc = new SuggestionCalculator(main,eits,suggestionNumber);
							sc.run();
						}
					}
				}
			}
			
		});*/
		/*
		main.verboseButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
					Logger.logln(NAME+"Verbose console button clicked");
					if(consoleDead = true){
					BackendInterface.runVerboseOutputWindow(main);
					consoleDead = false;
					}
					
			}
			
			
		});
		*/
		
		
		/*main.editTP.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				int theAnswer = -1;
				boolean okayToDelete = true;
				if(e.getButton() != 1){
					if(main.editTP.getSelectedIndex() == 0){
						JOptionPane.showMessageDialog(main, "You cannot delete your original document.", "Can't Delete Original!",JOptionPane.ERROR_MESSAGE,GUIMain.iconNO);
						okayToDelete = false;
					}
					else{
					theAnswer = JOptionPane.showConfirmDialog(main,"Really delete current tab? \n\nNote: this action effects the current tab","Delete Current Tab",
							JOptionPane.YES_NO_OPTION);
					}
				}
				if(theAnswer == 0 && okayToDelete == true){
					int selectionNumber = main.editTP.getSelectedIndex();
					main.editTP.remove(main.editBoxPanel);
					nextTabIndex--;
					Logger.logln(NAME+"Inner editor tab number '"+selectionNumber+"' deleted.");
					//main.editTP.setSelectedIndex(nextTabIndex);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			
		});*/
//		
//	}
//	
//	public static void spawnNew(GUIMain main){//spawns xtra tabs
//		if(!isFirstRun){
//			int answer = JOptionPane.showConfirmDialog(main, "Create new tab to edit document?\n\n" +
//					"Note: Once a version of your document has been processed,\n" +
//					"it may no longer be edited. However, by clicking on the text you wish\n" +
//					"to edit, you may spawn a new tab containing a copy of that text.");  
//			if( answer == 0){
//				Logger.logln(NAME+"Creating new editor inner tab");
//				//System.out.println("EDIT TABBED PANE SELECTED INDEX at spawn: "+EditorTabDriver.selectedIndexTP);
//			
//				String nameFirstHalf = main.editTP.getTitleAt(selectedIndexTP);
//				if(!nameFirstHalf.equals("Original"))
//					nameFirstHalf = nameFirstHalf.substring(nameFirstHalf.indexOf("->")+2);
//				eitsList.add(nextTabIndex,(new EditorInnerTabSpawner(eits).spawnTab()));
//				main.editTP.addTab(nameFirstHalf+"->"+Integer.toString(numEdits), eitsList.get(nextTabIndex).editBoxPanel);
//				main.editTP.setSelectedIndex(nextTabIndex);
//				initEditorInnerTabListeners(main);
//				main.processButton.setEnabled(true);
//				/* todo I commented this block out to test the translated sentence functionality -- AweM
//				main.documentPane.setEnabled(false);
//				ConsolidationStation.toModifyTaggedDocs.get(0).setSentenceCounter(-1);
//				main.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
//				main.sentenceEditPane.setEnabled(true);
//				//main.sentenceEditPane.setText(helpMessege);
//				main.sentenceEditPane.setEditable(true);
//				trackEditSentence(main);
//				Logger.logln(NAME+ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedDocument());
//				main.documentPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedDocument());
//				*/
//				nextTabIndex++;
//			}
//			else
//				Logger.logln(NAME+"User Chose not to create a new tab when prompted.");
//			
//		}
//	}
	
//	public static void resetAll(GUIMain main){
//		Logger.logln(NAME+"Resetting all values");
//		main.editTP.removeAll();
//		EditorTabDriver.eitsList.clear();
//		EditorInnerTabSpawner eits = (new EditorInnerTabSpawner()).spawnTab();
//		EditorTabDriver.eitsList.add(0,eits);
//		EditorTabDriver.eits = EditorTabDriver.eitsList.get(0);
//		EditorTabDriver.main.classificationLabel.setText("Please process your document in order to recieve a classification result.");
//		main.documentPane.setEnabled(true);
//		main.editTP.addTab("Original",main.editBoxPanel);
//		main.editTP.setSelectedIndex(0);
//		//initEditorInnerTabListeners(main);
//		main.suggestionTable.setModel(new DefaultTableModel(
//				new String[][] { { "" }, { "" } },
//				new String[] { "", "" }));
//		main.featureNameLabel.setText("-");
//		main.presentValueField.setText(" - ");
//		main.targetValueField.setText(" - ");
//		main.processButton.setText("Process");
//		main.processButton.setEnabled(true);
//		main.processButton.setSelected(true);
//		main.instructionsPane.setText("");
//		
//		main.elementsToAddPane.setText("");
//		main.elementsToRemovePane.setText("");//not sure if needed to reset..
//		
//		EditorTabDriver.hasBeenInitialized = false;
//		EditorTabDriver.hasCurrentAttrib = false;
//		EditorTabDriver.isWorkingOnUpdating = false;
//		EditorTabDriver.currentCaretPosition = 0;
//		EditorTabDriver.okayToSelectSuggestion = false;
//		EditorTabDriver.keyJustTyped = false;
//		EditorTabDriver.checkForMouseInfluence =false;
//		EditorTabDriver.consoleDead = true;
//		EditorTabDriver.dictDead = true;
//		EditorTabDriver.isCalcHist = false;
//		EditorTabDriver.featuresInCfd.clear();
//		EditorTabDriver.chosenAuthor = "n/a";
//		EditorTabDriver.numSuggestions = -1;
//		EditorTabDriver.numEdits = 0;
//		EditorTabDriver.nextTabIndex = 1;
//		EditorTabDriver.selectedIndexTP = 0;
//		//System.out.println("EDIT TABBED PANE SELECTED INDEX at reset: "+EditorTabDriver.selectedIndexTP);
//		
//	}
//	
//	public static void initEditorInnerTabListeners(final GUIMain main){
//		
//		
//		//main.shuffleButton.setEnabled(true);
//		//main.restoreSentenceButton.setEnabled(true);
//		
//		/*main.shuffleButton.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				Logger.logln(NAME+"Shuffle button pressed by User.");
//				//shuffle current sentence
//				if(!main.sentenceEditPane.getText().startsWith(helpMessege)&&!main.sentenceEditPane.getText().equals("Please press the Process button now.")){
//					if(main.sentenceEditPane.getText().matches(".*([?!]+)|.*([.]){1}\\s*")){//EOS "([?!]+)|([.]){1}\\s*"
//						ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(main.sentenceEditPane.getText());
//					}
//					TaggedSentence currentSentence=ConsolidationStation.toModifyTaggedDocs.get(0).getTaggedSentences().get(ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber());
//					ArrayList<String> untaggedWords=new ArrayList<String>(currentSentence.size());
//					ArrayList<Word> wordArr=currentSentence.getWordsInSentence();
//					for(Word word:wordArr){//if theres an EOS char then the sentence should be saved
//						System.out.println("Word: "+word.getUntagged());
//						if(word.getUntagged().matches("[\\w&&[^\\d]]*")){
//							//if(!ConsolidationStation.functionWords.searchListFor(word.getUntagged())){//TODO: make sure this works
//							//if(!topToRemove.contains(word.getUntagged())){
//								untaggedWords.add(word.getUntagged());//This excludes ALL function words and punctuation
//								//System.out.println(word.getUntagged());
//							//}
//							//else if((word.getUntagged())){
//								
//							//}
//						}
//					}
//					//does the shuffling
//					int sizeOfWordList=untaggedWords.size(),randNum;
//					String toReturn="",temp;
//					for(int i=0;i<sizeOfWordList;i++){
//						randNum=(int) ((Math.random())*(untaggedWords.size()-1)+.5);
//						temp=untaggedWords.remove(randNum);
//						toReturn+=temp+" ";
//						//Logger.logln(NAME+toReturn);
//					}
//					main.sentenceEditPane.setText(toReturn);
//				}
//			}
//			
//		});	*/
//		/*main.removeWordsButton.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				Logger.logln(NAME+"Previous sentence restored.");
//				if(!main.sentenceEditPane.getText().startsWith(helpMessege)&&!main.sentenceEditPane.getText().equals("Please press the Process button now.")){
//					if(main.sentenceEditPane.getText().matches(".*([?!]+)|.*([.]){1}\\s*")){//EOS "([?!]+)|([.]){1}\\s*"
//						ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(main.sentenceEditPane.getText());
//					}
//					TaggedSentence currentSentence=ConsolidationStation.toModifyTaggedDocs.get(0).getTaggedSentences().get(ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber());
//					ArrayList<String> untaggedWords=new ArrayList<String>(currentSentence.size());
//					ArrayList<Word> wordArr=currentSentence.getWordsInSentence();
//					for(Word word:wordArr){//if theres an EOS char then the sentence should be saved
//						System.out.println("Word: "+word.getUntagged());
//						//if(word.getUntagged().matches("[\\w&&[^\\d]]*")){
//							//if(!ConsolidationStation.functionWords.searchListFor(word.getUntagged())){//TODO: make sure this works
//							if(!topToRemove.contains(word.getUntagged())){
//								untaggedWords.add(word.getUntagged());//This excludes ALL function words and punctuation
//								//System.out.println(word.getUntagged());
//							}
//						//}
//					}
//					int sizeOfWordList=untaggedWords.size();
//					String toReturn="";
//					for(int i=0;i<sizeOfWordList;i++){
//						toReturn+=untaggedWords.get(i)+" ";
//						//Logger.logln(NAME+toReturn);
//					}
//					main.sentenceEditPane.setText(toReturn);
//				}
//			}
//			
//		});	*/
//		
//		main.documentPane.addMouseListener(new MouseListener(){
//
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				if(!main.sentenceEditPane.isEditable()){
//					spawnNew(main);
//				}
//				
//				
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void mouseExited(MouseEvent arg0) {
//			}
//
//			@Override
//			public void mousePressed(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				currentCaretPosition = main.documentPane.getCaretPosition();
//				mouseEndPosition =0;
//				checkForMouseInfluence = true;
//				//System.out.println("Mouse press registered at index: "+currentCaretPosition);
//				
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				mouseEndPosition = main.documentPane.getCaretPosition();
//				//System.out.println("Caret position registered at mousereleased: "+mouseEndPosition);
//			}
//			
//		});
//
//		
//		
//		

//	
//		
//		
//		
//	}
//	
//	public static void dispHighlights(){
//		Highlighter highlight = GUIMain.inst.documentPane.getHighlighter();
//		HashMap<Color,ArrayList<int[]>> currentMap = HighlightMapMaker.highlightMap;
//		int i = 0;
//		if(!currentMap.isEmpty()){
//			Set<Color> theseColors = currentMap.keySet();
//			Iterator<Color> colorsIter = theseColors.iterator();
//			while(colorsIter.hasNext()){
//				Color currentColor = colorsIter.next();
//				TheHighlighter thisPen = new TheHighlighter(currentColor);
//				ArrayList<int[]> theseIndices = currentMap.get(currentColor);
//				for(i=0;i<theseIndices.size();i++){
//					try {
//						int[] tempHighlightIndices =theseIndices.get(i);
//						HighlightMapper hm =new HighlightMapper(tempHighlightIndices[0],tempHighlightIndices[1],highlight.addHighlight(theseIndices.get(i)[0],theseIndices.get(i)[1],thisPen));
//						DriverDocumentsTab.highlightedObjects.add(hm);
//					} catch (BadLocationException e) {
//						Logger.logln(NAME+"Error displaying highlights.");// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		
//		
//	}
//	
//	
} 

	class TheHighlighter extends DefaultHighlighter.DefaultHighlightPainter{
		public TheHighlighter(Color color){
			super(color);
		}
	}
///*	
//	class PredictionRenderer implements TableCellRenderer {
//
//		  public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
//
//		  public Component getTableCellRendererComponent(JTable table, Object value,
//		      boolean isSelected, boolean hasFocus, int row, int column) {
//		    Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(
//		        table, value, isSelected, hasFocus, row, column);
//		    ((JLabel) renderer).setOpaque(true);
//		    Color foreground, background;
//		    
//		      if ((column  == editorTabDriver.resultsMaxIndex) && (row==0)) {
//			    	 if(editorTabDriver.chosenAuthor.equals(DocumentMagician.authorToRemove)){
//			        foreground = Color.black;
//			        background = Color.red;
//			      } else {
//			        foreground = Color.black;
//			        background = Color.green;
//			      }
//		      }
//		      else{
//		    	  	foreground = Color.black;
//		    	  	background = Color.white;
//		      }
//		    
//		    renderer.setForeground(foreground);
//		    renderer.setBackground(background);
//		    return renderer;
//		  }
//		}	
//	
//	*/
	
/*			
	 class SuggestionCalculator implements Runnable{
		//TODO: need to process sentence to find most salient features
		 
		 private final String NAME = "( "+this.getClass().getSimpleName()+" ) - ";

		 
		 GUIMain main;
		 int suggestionNumber;
		 
		 public SuggestionCalculator(GUIMain main, int sel){
			Logger.logln(NAME+"Entered SuggestionCalculator.");
			this.main = main;
			this.suggestionNumber = sel;
		 }
		 
		public void run(){ 
			int i;
			boolean noChangeNeeded= false;
			//System.out.println("SUGGESTION NUMBER IS: "+suggestionNumber);
			DriverDocumentsTab.selectedFeature = DriverDocumentsTab.theFeatures[suggestionNumber].toString();
			//Logger.logln(NAME+"Suggestion selected:"+EditorTabDriver.selectedFeature);
			//EditorTabDriver.currentAttrib = EditorTabDriver.wizard.runSelectedFeature(suggestionNumber,false);
			DriverDocumentsTab.currentAttrib = DriverDocumentsTab.wizard.getAttributes()[DriverDocumentsTab.suggestionToAttributeMap.get(suggestionNumber)];
			DriverDocumentsTab.hasCurrentAttrib = true;
			FeatureDriver theOneToUpdate = main.cfd.featureDriverAt(DriverDocumentsTab.featuresInCfd.indexOf(DriverDocumentsTab.currentAttrib.getGenericName().toString()));
			Logger.logln(NAME+"Retrieved feature driver for currentAttrib");
			Document currDoc = new Document();
			currDoc.setText(main.documentPane.getText().toCharArray());
			List<Canonicizer> canonList = theOneToUpdate.getCanonicizers();
			try{
				Iterator<Canonicizer> canonIter = canonList.iterator();
				while(canonIter.hasNext())
					currDoc.addCanonicizer(canonIter.next());
			} catch(NullPointerException npe){
			}
			Computer.setTheDocument(currDoc);
			TheOracle.setTheDocument(currDoc.stringify());
			Logger.logln(NAME+"Set document text to Computer and TheOracle");

			Highlighter highlight = main.documentPane.getHighlighter();
			HashMap<Color,ArrayList<int[]>> currentMap = new HashMap<Color,ArrayList<int[]>>();
			try{
				Logger.logln(NAME+"Getting suggestion from Suggestor...");
				DriverDocumentsTab.utterance = DriverDocumentsTab.theMirror.callRelevantSuggestor(DriverDocumentsTab.currentAttrib);
				main.instructionsPane.setText(DriverDocumentsTab.utterance.getSuggestion());
				if((DriverDocumentsTab.currentAttrib.getGenericName().toString()).contains("PERCENT"))
					main.targetValueField.setText(Double.toString((Math.floor(DriverDocumentsTab.currentAttrib.getTargetValue()*10000+.5)/10000)*100));
				else
					main.targetValueField.setText(Double.toString((Math.floor(DriverDocumentsTab.currentAttrib.getTargetValue()*10000+.5)/10000)));
				main.presentValueField.setText(Double.toString((Math.floor(DriverDocumentsTab.currentAttrib.getToModifyValue()*10000+.5)/10000)));
				main.featureNameLabel.setText(DriverDocumentsTab.currentAttrib.getGenericName().toString()+" "+DriverDocumentsTab.currentAttrib.getStringInBraces()+":");
				currentMap= DriverDocumentsTab.utterance.getHighlightMap();
				noChangeNeeded = DriverDocumentsTab.utterance.getNoChangeNeeded();
				Logger.logln(NAME+"Suggestion obtained.");
			} catch (Exception e){
				e.printStackTrace();
				DriverDocumentsTab.hasCurrentAttrib = false;
				JOptionPane.showMessageDialog(null, 
						"Someone did something they shouldn't have, and an exception was thrown.\n" +
								"If I were you, I'd point my finger at the person who wrote this.\n" +
								"Regardless of who's fault this is though, it isn't yours, and I apologize for having done this.",
								"Oops!",
								JOptionPane.ERROR_MESSAGE,
								GUIMain.iconNO);
			}
			//	if(theFeatures[suggestionNumber] == FeatureList.AVERAGE_SENTENCE_LENGTH){
			
			if(!currentMap.isEmpty()){
				Logger.logln(NAME+"HighlightMap is not empty - will begin highlighting");
				Set<Color> theseColors = currentMap.keySet();
				Iterator<Color> colorsIter = theseColors.iterator();
				while(colorsIter.hasNext()){
					Color currentColor = colorsIter.next();
					TheHighlighter thisPen = new TheHighlighter(currentColor);
					ArrayList<int[]> theseIndices = currentMap.get(currentColor);
					for(i=0;i<theseIndices.size();i++){
						try {
							int[] tempHighlightIndices =theseIndices.get(i);
							HighlightMapper hm =new HighlightMapper(tempHighlightIndices[0],tempHighlightIndices[1],highlight.addHighlight(theseIndices.get(i)[0],theseIndices.get(i)[1],thisPen));
							DriverDocumentsTab.highlightedObjects.add(hm);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			else
				Logger.logln(NAME+"Highlight map empty - nothing to highlight. Finished in SuggestionCalculator");
			main.processButton.setEnabled(true);
			DriverDocumentsTab.okayToSelectSuggestion = true;
			//System.out.println("Should exit Suggestion Calculator.");
		} 
	 }
*/
	
	/*
	 * Highlights the sentence that is currently in the editor box in the main document
	 * no return
	protected static void trackEditSentence(GUIMain main){
		editTracker = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
		int startHighlight=0, endHighlight=0;
		int sentNum=ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber();
		ArrayList<String> sentences=ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedSentences();
		main.documentPane.setHighlighter(editTracker);
		String newText=ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedDocument();
		main.documentPane.setText(newText);
		boolean fixTabs=false;
		numberTimesFixTabs=0;
		for (int i=0;i<sentNum+1;i++){
			if(i<sentNum){
				startHighlight+=sentences.get(i).length();
			}
			else if(i==sentNum){
				endHighlight=startHighlight+sentences.get(i).length()-1;
			}
			if (fixTabs){
				fixTabs=false;
				startHighlight-=1;
				numberTimesFixTabs++;
			}
			if(sentences.get(i).startsWith("\n")||sentences.get(i).startsWith("\n")||sentences.get(i).startsWith("\r")){
				fixTabs=true;
				//Logger.logln(NAME+"FOUND CHARACTER");
				//startHighlight++;
			}
		}
		topToRemove=ConsolidationStation.getPriorityWords(ConsolidationStation.toModifyTaggedDocs, true, .2);
		topToAdd=ConsolidationStation.getPriorityWords(ConsolidationStation.authorSampleTaggedDocs, false, .02);
		
		//TaggedDocument taggedDoc=ConsolidationStation.toModifyTaggedDocs.get(0);
		int lenPrevSentences=0;
		String sentence=sentences.get(sentNum);
		
		//removeTracker = new DefaultHighlighter();
		painter2 = new DefaultHighlighter.DefaultHighlightPainter(new Color(255,0,0,128));

		startHighlight = startHighlight;
		
		ArrayList<ArrayList<Integer>> indexArray=new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tempArray;
		//ArrayList<String> toRemoveInSentence;
		int indexOfTemp;
		boolean added=false;
		String setString="",tempString;
		int arrSize=topToRemove.size(),fromIndex=0;
		for(int i=0;i<arrSize;i++){//loops through top to remove list
			setString+=topToRemove.get(i)+"\n";//sets the string to return
//	
//			Scanner parser=new Scanner(sentence);
//			fromIndex=0;
//			while(parser.hasNext()){//finds if the given word to remove is in the current sentence
//				//loops through current sentence
//				tempString=parser.next();
//				if(tempString.matches(cleanWordRegex)){//TODO: refine this.
//					
//					tempString=tempString.substring(0,tempString.length()-1);
//					//Logger.logln(NAME+"replaced a period in: "+tempString);
//				}
//				if(tempString.equals(topToRemove.get(i))){
//					tempArray=new ArrayList<Integer>(2);
//					
//					indexOfTemp=sentence.indexOf(tempString,fromIndex);
//					tempArray.add(indexOfTemp+startHighlight);//-numberTimesFixTabs
//					tempArray.add(indexOfTemp+tempString.length()+startHighlight);
//					//Logger.logln(NAME+"fromIndex: "+fromIndex+" startHighlight: "+startHighlight);
//					//Logger.logln(NAME+"Word: "+tempString+" start: "+tempArray.get(0)+" end: "+tempArray.get(1),Logger.LogOut.STDERR);
//					added=false;
//					for(int j=0;j<indexArray.size();j++){
//						if(indexArray.get(j).get(0)>tempArray.get(0)){
//							indexArray.add(j,tempArray);
//							added=true;
//							break;
//						}
//					}
//					if(!added)
//						indexArray.add(tempArray);
//					//fromIndex=tempArray.get(1);
//				}
//				fromIndex+=tempString.length()+1;
//				
//			}
//			
		}
		
		main.elementsToRemovePane.setText(setString);
		main.elementsToRemovePane.setCaretPosition(0);
		findSynonyms(main,sentence);
		
		editTracker.removeAllHighlights();
		main.documentPane.repaint();
		int innerArrSize,outerArrSize=indexArray.size(), currentStart,currentEnd;
		currentStart=startHighlight;
		//Logger.logln(NAME+"indexArr "+indexArray.toString(),Logger.LogOut.STDERR);
		try {
			for(int i=0;i<outerArrSize;i++){
				currentEnd=indexArray.get(i).get(0);
				//Logger.logln(NAME+"before first addhighlight: currentStart: "+currentStart+" currentEnd: "+currentEnd);
				//if(currentStart<currentEnd)
					editTracker.addHighlight(currentStart,currentEnd, painter);
				currentStart=currentEnd;
				currentEnd=indexArray.get(i).get(1);
				//Logger.logln(NAME+"currentEnd: "+currentEnd+" currentStart: "+currentStart);
				//if(currentStart<currentEnd)
					editTracker.addHighlight(currentStart, currentEnd, painter2);
				currentStart=currentEnd;
				//Logger.logln(NAME+"currentEnd: "+currentEnd+" currentStart: "+currentStart);
			}
			editTracker.addHighlight(currentStart,endHighlight, painter);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logln(NAME+"Error highlighting the block");
		}
	}
*/	
	
	/**
	 * Finds the synonyms of the words to remove in the words to add list
	 * 
	protected static void findSynonyms(GUIMain main,String currentSent){
		String[] tempArr;
		addTracker = new DefaultHighlighter();
		painter3 = new DefaultHighlighter.DefaultHighlightPainter(new Color(0,0,255,128));
		String setString,tempStr,synSetString = "";
		main.addToSentencePane.setHighlighter(addTracker);
		addTracker.removeAllHighlights();
		
		main.elementsToAddPane.repaint();
		
		setString="";
		int arrSize=topToAdd.size(), index;
		for(int i=0;i<arrSize;i++){//Sets the topToAddElements box
			setString+=topToAdd.get(i)+"\n";
		}
		main.elementsToAddPane.setText(setString);
		main.elementsToAddPane.setCaretPosition(0);
		synSetString="";
		boolean inSent;
		Scanner parser;
		HashMap<String,Integer> indexMap=new HashMap<String,Integer>();
		/*for(String str:topToRemove){
			tempArr=DictionaryBinding.getSynonyms(str);
			if(tempArr!=null){
				//inSent=currentSent.contains(str);
				inSent=checkSentFor(currentSent,str);
				
				if(inSent)
					synSetString+=str+"=>";
				for(int i=0;i<tempArr.length;i++){//looks through synonyms
					tempStr=tempArr[i];
					if(inSent){
						synSetString+=tempStr+", ";
						for(String addString:topToAdd){
							if(addString.equalsIgnoreCase(tempStr)){
								index=synSetString.indexOf(tempStr);
								indexMap.put(tempStr, index);
							}
						}
					}
				}
				if(inSent)
					synSetString=synSetString.substring(0, synSetString.length()-2)+"\n";
			}
		}*//*
		Scanner sentParser=new Scanner(currentSent);
		String wordToSearch, wordSynMatch;
		HashMap<String,String>wordsWithSynonyms=new HashMap<String,String>();
		boolean added=false;
		synSetString="";
		while(sentParser.hasNext()){//loops through every word in the sentence
			wordToSearch=sentParser.next();
			tempArr=DictionaryBinding.getSynonyms(wordToSearch);
			wordSynMatch="";
			
			if(!wordsWithSynonyms.containsKey(wordToSearch.toLowerCase().trim())){
				if(tempArr!=null){
					for(int i=0;i<tempArr.length;i++){//looks through synonyms
						tempStr=tempArr[i];
						wordSynMatch+=tempStr+" ";
						added=false;
						for(String addString:topToAdd){//loops through the toAdd list
							if(addString.trim().equalsIgnoreCase(tempStr.trim())){//there is a match in topToAdd!
								if(!synSetString.contains(wordToSearch))
									synSetString+=wordToSearch+" => ";
								else{
									Logger.logln(NAME+"Did not add this again: "+wordToSearch);
								}
								synSetString=synSetString+addString+", ";
								//index=synSetString.indexOf(tempStr);
								//indexMap.put(tempStr, index);
								added=true;
								break;
							}
						}
						
						if(added){
							//do something if the word was added like print to the box.
							synSetString=synSetString.substring(0, synSetString.length()-2)+"\n";
						}
					}
					if(wordSynMatch.length()>2)
						wordsWithSynonyms.put(wordToSearch.toLowerCase().trim(), wordSynMatch.substring(0, wordSynMatch.length()-1));
					else
						wordsWithSynonyms.put(wordToSearch.toLowerCase().trim(), "NO Synonyms");
				}
			}
		}
		String tempStrToAdd;
		Word possibleToAdd;
		double topAnon=0;
		for(String wordToRem:topToRemove){//adds ALL the synonyms in the wordsToRemove
			if(wordsWithSynonyms.containsKey(wordToRem)){
				tempStr=wordsWithSynonyms.get(wordToRem);
				tempStrToAdd="";
				parser=new Scanner(tempStr);
				topAnon=0;
				while(parser.hasNext()){
					possibleToAdd=new Word(parser.next().trim());
					ConsolidationStation.setWordFeatures(possibleToAdd);
					if(possibleToAdd.getAnonymityIndex()>topAnon){
						tempStrToAdd=possibleToAdd.getUntagged()+", ";//changed for test
						topAnon=possibleToAdd.getAnonymityIndex();
					}
				}
				synSetString+=wordToRem+" => "+tempStrToAdd+"\n";
			}
		}
		main.addToSentencePane.setText(synSetString);
		main.addToSentencePane.setCaretPosition(0);
		
		Iterator iter=indexMap.keySet().iterator();
		String key;
		
		while(iter.hasNext()){
			key=(String) iter.next();
			index=indexMap.get(key);
			try {
				addTracker.addHighlight(index, index+key.length(), painter3);
			} catch (BadLocationException e) {
				Logger.logln(NAME+"Problem highlighting the words To add list");
				e.printStackTrace();
			}
		}
		
		//SynonymReplaceTest.replaceWords(eits);
	}
	*/
	
/*
 * Answer to puzzle:
 * The "~" is a bitwise "NOT". "-1" (in binary) is represented by all 1's. So, a bitwise 'NOT' makes it equivalent to '0':
 *  
 * ~-1 == 0
 */
	
