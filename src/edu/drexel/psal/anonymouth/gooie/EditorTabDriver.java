package edu.drexel.psal.anonymouth.gooie;

import edu.drexel.psal.anonymouth.calculators.Computer;
import edu.drexel.psal.anonymouth.gooie.DocsTabDriver.ExtFilter;
import edu.drexel.psal.anonymouth.projectDev.Attribute;
import edu.drexel.psal.anonymouth.projectDev.DataAnalyzer;
import edu.drexel.psal.anonymouth.projectDev.DocumentMagician;
import edu.drexel.psal.anonymouth.projectDev.FeatureList;
import edu.drexel.psal.anonymouth.projectDev.TheMirror;
import edu.drexel.psal.anonymouth.suggestors.HighlightMapList;
import edu.drexel.psal.anonymouth.suggestors.HighlightMapMaker;
import edu.drexel.psal.anonymouth.suggestors.Prophecy;
import edu.drexel.psal.anonymouth.suggestors.TheOracle;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;
import edu.drexel.psal.anonymouth.utils.DocumentParser;
import edu.drexel.psal.anonymouth.utils.DocumentTagger;
import edu.drexel.psal.anonymouth.utils.FunctionWord;
import edu.drexel.psal.anonymouth.utils.SentenceTools;
import edu.drexel.psal.anonymouth.utils.SynonymReplaceTest;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;
import edu.drexel.psal.anonymouth.utils.Word;
import edu.drexel.psal.jstylo.generics.FeatureDriver;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.WekaInstancesBuilder;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.SwingWorker;


import com.jgaap.generics.Canonicizer;
import com.jgaap.generics.Document;




/**
 * editorTabDriver does the work for the editorTab (Editor) in the main GUI (GUIMain)
 * @author Andrew W.E. McDonald
 * @author Joe Muoio
 *
 */
public class EditorTabDriver {
	
	
	protected static SentenceTools sentenceTools;
	
	private static int highlightSelectionBoxSelectionNumber;
	public static boolean isUsingNineFeatures = false;
	protected static boolean hasBeenInitialized = false;
	private static final String SPECIFIC = "SPECIFIC";
	protected static String[] condensedSuggestions;
	protected static int numEdits = 0;
	private static int nextTabIndex = 1;
	protected static boolean isFirstRun = true; 
	protected static DataAnalyzer wizard;
	private static DocumentMagician magician;
	protected static TheMirror theMirror; // formerly known as 'theChief'
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
	private static int caretPosition;
	private static int oldCaretPosition;
	private static int thisCaretPosition = 0;
	protected static boolean okayToSelectSuggestion = false;
	private static boolean keyJustTyped = false;
	private static int mouseEndPosition;
	private static boolean checkForMouseInfluence =false;
	protected static ArrayList<EditorInnerTabSpawner> eitsList = new ArrayList<EditorInnerTabSpawner>();
	protected static EditorInnerTabSpawner eits;
	protected static int selectedIndexTP;
	protected static int sizeOfCfd;
	protected static boolean consoleDead = true;
	protected static boolean dictDead = true;
	protected static ArrayList<String> featuresInCfd;
	protected static String selectedFeature;
	protected static boolean shouldReset = false;
	protected static boolean isCalcHist = false;
	protected static ClassifyingProgressBar cpb;
	protected static ArrayList<FeatureList> noCalcHistFeatures;
	protected static ArrayList<FeatureList> yesCalcHistFeatures;
	protected static HighlightMapList[] highlightingOptions;
	protected static String searchBoxInputText;
	public static Attribute[] attribs;
	public static HashMap<FeatureList,Integer> attributesMappedByName;
	public static HashMap<Integer,Integer> suggestionToAttributeMap;
	protected static DocumentParser docParser;
	protected static ConsolidationStation consolidator;
	
	private static String cleanWordRegex=".*([\\.,!?])+";//REFINE THIS??

	protected static ArrayList<String> topToRemove;
	protected static ArrayList<String> topToAdd;
	
	protected static Highlighter editTracker;
	protected static Highlighter removeTracker;
	protected static Highlighter addTracker;
	protected static Highlighter.HighlightPainter painter;
	protected static Highlighter.HighlightPainter painter2;
	protected static Highlighter.HighlightPainter painter4;
	protected static Highlighter.HighlightPainter painter3;

	private static final Color HILIT_COLOR = Color.yellow;
	
	private static int numberTimesFixTabs;
	
	private final static String helpMessege="Edit the sentence in this box.\n" +
			"Go to the next/previous sentences by clicking the corresponding buttons.\n" +
			"To edit multiple sentences at once, you can append the next sentence using the respective button.\n" +
			"Clicking the features to the right will give you suggestions to help anonymize your paper.\n" +
			"Click any of the sentence buttons to continue.";
	
	protected static void signalTargetsSelected(GUIMain main, boolean goodToGo){
		if(goodToGo == true)
			BackendInterface.postTargetSelectionProcessing(main, wizard, magician, cpb);
	}
	
	/*
	 * Highlights the sentence that is currently in the editor box in the main document
	 * no return
	 */
	protected static void trackEditSentence(GUIMain main){
		editTracker = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
		int startHighlight=0, endHighlight=0;
		int sentNum=ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber();
		ArrayList<String> sentences=ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedSentences();
		eits.editorBox.setHighlighter(editTracker);
		String newText=ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedDocument();
		eits.editorBox.setText(newText);
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
				//Logger.logln("FOUND CHARACTER");
				//startHighlight++;
			}
		}
		topToRemove=ConsolidationStation.getPriorityWords(ConsolidationStation.toModifyTaggedDocs, true, .2);//this aint working
		topToAdd=ConsolidationStation.getPriorityWords(ConsolidationStation.authorSampleTaggedDocs, false, .02);
		
		//TaggedDocument taggedDoc=ConsolidationStation.toModifyTaggedDocs.get(0);
		int lenPrevSentences=0;
		String sentence=sentences.get(sentNum);
		
		//removeTracker = new DefaultHighlighter();
		painter2 = new DefaultHighlighter.DefaultHighlightPainter(new Color(255,0,0,128));

		startHighlight=startHighlight;
		
		ArrayList<ArrayList<Integer>> indexArray=new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tempArray;
		//ArrayList<String> toRemoveInSentence;
		int indexOfTemp;
		boolean added=false;
		String setString="",tempString;
		int arrSize=topToRemove.size(),fromIndex=0;
		for(int i=0;i<arrSize;i++){//loops through top to remove list
			setString+=topToRemove.get(i)+"\n";//sets the string to return
			Scanner parser=new Scanner(sentence);
			fromIndex=0;
			while(parser.hasNext()){//finds if the given word to remove is in the current sentence
				//loops through current sentence
				tempString=parser.next();
				if(tempString.matches(cleanWordRegex)){//TODO: refine this.
					
					tempString=tempString.substring(0,tempString.length()-1);
					//Logger.logln("replaced a period in: "+tempString);
				}
				if(tempString.equals(topToRemove.get(i))){
					tempArray=new ArrayList<Integer>(2);
					
					indexOfTemp=sentence.indexOf(tempString,fromIndex);
					tempArray.add(indexOfTemp+startHighlight);//-numberTimesFixTabs
					tempArray.add(indexOfTemp+tempString.length()+startHighlight);
					//Logger.logln("fromIndex: "+fromIndex+" startHighlight: "+startHighlight);
					//Logger.logln("Word: "+tempString+" start: "+tempArray.get(0)+" end: "+tempArray.get(1),Logger.LogOut.STDERR);
					added=false;
					for(int j=0;j<indexArray.size();j++){
						if(indexArray.get(j).get(0)>tempArray.get(0)){
							indexArray.add(j,tempArray);
							added=true;
							break;
						}
					}
					if(!added)
						indexArray.add(tempArray);
					//fromIndex=tempArray.get(1);
				}
				fromIndex+=tempString.length()+1;
				
			}
		}
		
		main.elementsToRemovePane.setText(setString);
		main.elementsToRemovePane.setCaretPosition(0);
		findSynonyms(main,sentence);
		
		editTracker.removeAllHighlights();
		eits.editorBox.repaint();
		int innerArrSize,outerArrSize=indexArray.size(), currentStart,currentEnd;
		currentStart=startHighlight;
		//Logger.logln("indexArr "+indexArray.toString(),Logger.LogOut.STDERR);
		try {
			for(int i=0;i<outerArrSize;i++){
				currentEnd=indexArray.get(i).get(0);
				//Logger.logln("before first addhighlight: currentStart: "+currentStart+" currentEnd: "+currentEnd);
				//if(currentStart<currentEnd)
					editTracker.addHighlight(currentStart,currentEnd, painter);
				currentStart=currentEnd;
				currentEnd=indexArray.get(i).get(1);
				//Logger.logln("currentEnd: "+currentEnd+" currentStart: "+currentStart);
				//if(currentStart<currentEnd)
					editTracker.addHighlight(currentStart, currentEnd, painter2);
				currentStart=currentEnd;
				//Logger.logln("currentEnd: "+currentEnd+" currentStart: "+currentStart);
			}
			editTracker.addHighlight(currentStart,endHighlight, painter);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logln("Error highlighting the block");
		}
	}
	/**
	 * Finds the synonyms of the words to remove in the words to add list
	 * 
	 */
	protected static void findSynonyms(GUIMain main,String currentSent){
		/*
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
									Logger.logln("Did not add this again: "+wordToSearch);
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
				Logger.logln("Problem highlighting the words To add list");
				e.printStackTrace();
			}
		}
		
		*/
		SynonymReplaceTest.replaceWords(eits);
	}
	
	
	private static boolean checkSentFor(String currentSent, String str) {
		// TODO Auto-generated method stub
		Scanner parser=new Scanner(currentSent);
		boolean inSent=false;
		String tempStr;
		while(parser.hasNext()){
			tempStr=parser.next();
			if(tempStr.matches(cleanWordRegex))
				tempStr=tempStr.substring(0,tempStr.length()-1);
			if(tempStr.equalsIgnoreCase(str)){
				inSent=true;
				break;
			}
		}
		return inSent;
	}

	public static String getHelpMessege(){
		return helpMessege;
	}
	
	protected static void initListeners(final GUIMain main){
		
		Action refresh = new Action() {
		    public void actionPerformed(ActionEvent e) {
		    	if(!eits.sentenceEditPane.isEditable()){
					if(!eits.sentenceEditPane.getText().equals(helpMessege)){
						spawnNew(main);
					}
					else{
						eits.sentenceEditPane.setEditable(true);
						//eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
						trackEditSentence(main);
						
					}
				}
				else{
					Logger.logln("next sentence button pressed.");
					if(ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(eits.getSentenceEditPane().getText())!=-1){
						//sentenceTools.replaceCurrentSentence(eits.getSentenceEditPane().getText());
						trackEditSentence(main);
					}
				}
		    }

			@Override
			public void addPropertyChangeListener(
					PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object getValue(String key) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void putValue(String key, Object value) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removePropertyChangeListener(
					PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setEnabled(boolean b) {
				// TODO Auto-generated method stub
				
			}
		};
		main.theEditorScrollPane.getInputMap().put(KeyStroke.getKeyStroke("F2"),
		                            "refresh");
		main.theEditorScrollPane.getActionMap().put("refresh",
		                             refresh);
		
		
		main.processButton.setToolTipText("Click this first to run and to get the results of the initial classification of your document.");
		
		main.processButton.addActionListener(new ActionListener() {
			@Override
			public synchronized void actionPerformed(ActionEvent event) {
				main.processButton.setEnabled(false);
				if(isFirstRun==true){
					//sentenceTools = new SentenceTools();
					TaggedDocument taggedDocument = new TaggedDocument();//eits.editorBox.getText();
					ConsolidationStation.toModifyTaggedDocs=new ArrayList<TaggedDocument>();
					ConsolidationStation.toModifyTaggedDocs.add(taggedDocument);
					//isFirstRun=false;
					Logger.logln("Intial processing starting...");
					int i =0;
					sizeOfCfd = main.cfd.numOfFeatureDrivers();
					featuresInCfd = new ArrayList<String>(sizeOfCfd);
					noCalcHistFeatures = new ArrayList<FeatureList>(sizeOfCfd);
					yesCalcHistFeatures = new ArrayList<FeatureList>(sizeOfCfd);
					for(i =0; i<sizeOfCfd;i++){
						String theName = main.cfd.featureDriverAt(i).getName();
						theName = theName.replaceAll("[ -]","_").toUpperCase();
						if(isCalcHist == false){
							isCalcHist =main.cfd.featureDriverAt(i).isCalcHist();
							yesCalcHistFeatures.add(FeatureList.valueOf(theName));
						} else {
						// these values will go in suggestion list... PLUS any 	
							noCalcHistFeatures.add(FeatureList.valueOf(theName));
						}
						//System.out.println(theName);
						featuresInCfd.add(i,theName);
						
					}
					//System.exit(89);
					wizard = new DataAnalyzer(main.ps,ThePresident.sessionName);
					magician = new DocumentMagician(false);
					theMirror = new TheMirror();
					docParser = new DocumentParser();
					//consolidator=new ConsolidationStation(wizard.getAttributes());
					main.mainJTabbedPane.getComponentAt(4).setEnabled(false);
					
				}
				else
					Logger.logln("Repeat processing starting....");
				cpb = new ClassifyingProgressBar(main);
				cpb.setText("Waiting for Number of Features Desired...");
				int i =0;
				JPanel[] firstThreePanels = new JPanel[3];
				for(i=0;i<3;i++)
					firstThreePanels[i] = (JPanel) main.holderPanel.getComponent(i);
				main.holderPanel.removeAll();
				for(i=0;i<3;i++)
					main.holderPanel.add(firstThreePanels[i]);
				int wekaIsRunningAnswer = wekaIsRunning();
				if(wekaIsRunningAnswer != -1){
					cpb.setText("Waiting for Number of Features Desired... OK");
					cpb.setText("Initializing...");
					cpb.run();
					eits.editorBox.getHighlighter().removeAllHighlights();
					highlightedObjects.clear();
					TheOracle.resetColorIndex();
					eits.resultsTablePane.setOpaque(false);
					eits.resultsTable.setOpaque(false);
					//main.editTP.setEnabled(false);
					highlightedObjects.clear();
					//main.suggestionTable.clearSelection();
					okayToSelectSuggestion = false;
					wizard.setNumFeaturesToReturn(wekaIsRunningAnswer);
					cpb.setText("Initializing... Done");
					Logger.logln("calling backendInterface for preTargetSelectionProcessing");
					BackendInterface.preTargetSelectionProcessing(main,wizard,magician,cpb);
				}
				else
					main.processButton.setEnabled(true);

				}	
				
			
		});
		
		main.nextSentenceButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0){
				
				if(!eits.sentenceEditPane.isEditable()){
					if(!eits.sentenceEditPane.getText().equals(helpMessege)){
						spawnNew(main);
					}
					else{
						eits.sentenceEditPane.setEditable(true);
						eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
						trackEditSentence(main);
						
					}
				}
				else{
					Logger.logln("next sentence button pressed.");
					if(ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(eits.getSentenceEditPane().getText())!=-1){
						//sentenceTools.replaceCurrentSentence(eits.getSentenceEditPane().getText());
						String tempSent=ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence();
						if(tempSent!=null)
							eits.getSentenceEditPane().setText(tempSent);
						trackEditSentence(main);
					}
				}
				//updateAddRemoveBoxes(main);
			}
			
		});
		main.refreshButtonEditor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				if(!eits.sentenceEditPane.isEditable()){
					if(!eits.sentenceEditPane.getText().equals(helpMessege)){
						spawnNew(main);
					}
					else{
						eits.sentenceEditPane.setEditable(true);
						eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
						trackEditSentence(main);
					}
				}
				else{
					Logger.logln("Refresh button pressed.");
					if(ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(eits.getSentenceEditPane().getText())!=-1){
						trackEditSentence(main);
					}
				}
			}			
			
		});
		main.lastSentenceButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0){
				
				if(!eits.sentenceEditPane.isEditable()){
					if(!eits.sentenceEditPane.getText().equals(helpMessege)){
						spawnNew(main);
					}
					else{
						eits.sentenceEditPane.setEditable(true);
						eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
						trackEditSentence(main);
					}
				}
				else{
					Logger.logln("last sentence button pressed.");
					if(ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(eits.getSentenceEditPane().getText())!=-1){
						
						String tempSent=ConsolidationStation.toModifyTaggedDocs.get(0).getLastSentence();
						if(tempSent!=null)
							eits.getSentenceEditPane().setText(tempSent);
						trackEditSentence(main);
					}
				}
			//	updateAddRemoveBoxes(main);
				
			}
			
		});
		
		main.addSentence.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0){//FIX THIS
				if(!eits.sentenceEditPane.isEditable()){
					if(!eits.sentenceEditPane.getText().equals(helpMessege)){
						spawnNew(main);
					}
					else{
						eits.sentenceEditPane.setEditable(true);
						eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
						trackEditSentence(main);
					}
				}
				else{
					Logger.logln("Add sentence button pressed.");
					String tempSent=ConsolidationStation.toModifyTaggedDocs.get(0).addNextSentence(eits.getSentenceEditPane().getText());
					//ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(eits.getSentenceEditPane().getText());
					eits.getSentenceEditPane().setText(tempSent);
					trackEditSentence(main);
					
				}
				//updateAddRemoveBoxes(main);
			}
			
		});
	
		main.exitButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("Exit button pressed within edit tab.");
				main.dispose();
				System.exit(0);
			}
			
		});	
		
		/*
		main.clearHighlightingButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("All highlights cleared in GUI editor box.");
				eits.editorBox.getHighlighter().removeAllHighlights();
				//main.featureNameLabel.setText("Feature Name: ");
				//main.targetValueField.setText("null");
				//main.presentValueField.setText("null");
				highlightedObjects.clear();
				TheOracle.resetColorIndex();
				
			}
			
		});
		*/
		main.editTP.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(shouldReset == false){
				selectedIndexTP = main.editTP.getSelectedIndex();
				Logger.logln("Selected inner editor tab number : "+selectedIndexTP);
				eits = eitsList.get(selectedIndexTP);
				
				}
			}
			 
			
		});
		/*//uncommented from here and it broke everything so I am not touching this code.
		main.getHighlightSelectionBox().addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent act){
				highlightSelectionBoxSelectionNumber = main.getHighlightSelectionBox().getSelectedIndex();
				Logger.logln("highlight selection box activity: selection number '"+highlightSelectionBoxSelectionNumber+"'");
				if(eits.editorBox.isEditable() == false){
					spawnNew(main);
				}
				
				if (highlightingOptions[highlightSelectionBoxSelectionNumber].toString().contains(SPECIFIC) == true){
					main.searchInputBox.setEnabled(true);
					main.searchInputBox.setText("value");
					main.searchInputBox.setSelectionStart(0);
					main.searchInputBox.setSelectionEnd(main.searchInputBox.getText().length());
					main.searchInputBox.setSelectionColor(Color.yellow);
				} else{
					
					HighlightMapMaker.document = eits.editorBox.getText();
					try {
						
						theMirror.highlightRequestedNotSpecific(highlightingOptions[highlightSelectionBoxSelectionNumber].toString());
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
		});
		
		
			
			
			
		/*
		main.searchInputBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent act){
				HighlightMapMaker.document = eits.editorBox.getText();

				searchBoxInputText = main.searchInputBox.getText();
				searchBoxInputText = searchBoxInputText.trim();
				searchBoxInputText = searchBoxInputText.replaceAll("\\s+", " ");
				Logger.logln("String entered in searchInputBox : "+searchBoxInputText);
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
				Logger.logln("SEARCH STRING POST PROCESSING: "+s);
				try {
					theMirror.highlightRequestedSpecific(highlightingOptions[highlightSelectionBoxSelectionNumber].toString(), s);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
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
						if(eits.sentenceEditPane.isEditable() == false){
							spawnNew(main);
						}
						//System.out.println("Table clicked");
						int suggestionNumber = main.suggestionTable.getSelectedRow();//only one suggestion at a time.
						Logger.logln("table row: '"+suggestionNumber+"' selected for suggestion.");
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
					Logger.logln("Verbose console button clicked");
					if(consoleDead = true){
					BackendInterface.runVerboseOutputWindow(main);
					consoleDead = false;
					}
					
			}
			
			
		});
		*/
		main.dictButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.logln("dictionary button clicked.");
				if(dictDead = true){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DictionaryConsole inst = new DictionaryConsole();
						inst.setLocationRelativeTo(null);
						inst.setVisible(true);
					}
				});
				dictDead = false;
				}
			}
			
			
		});
		
		main.saveButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.logln("Save document button clicked.");
				JFileChooser save = new JFileChooser();
				save.addChoosableFileFilter(new ExtFilter("txt files (*.txt)", "txt"));
				int answer = save.showSaveDialog(main);
				
				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".txt"))
						path += ".txt";
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(path));
						bw.write(eits.editorBox.getText());
						bw.flush();
						bw.close();
						Logger.log("Saved contents of current tab to "+path);
					} catch (IOException exc) {
						Logger.logln("Failed opening "+path+" for writing",LogOut.STDERR);
						Logger.logln(exc.toString(),LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving contents of current tab into:\n"+path,
								"Save Problem Set Failure",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
		            Logger.logln("Save contents of current tab canceled");
		        }
			}
		
		});
		
		
		main.editTP.addMouseListener(new MouseListener(){

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
					main.editTP.remove(eits.editBoxPanel);
					nextTabIndex--;
					Logger.logln("Inner editor tab number '"+selectionNumber+"' deleted.");
					//main.editTP.setSelectedIndex(nextTabIndex);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
	}
	
	
	public static int wekaIsRunning(){
		if(isUsingNineFeatures == true)
			return 9;
		else{
			/*Logger.logln("Asking user for desired number of features to return suggestions for.");
			JOptionPane theMessage = new JOptionPane();
			JTextField jtf = new JTextField();
			if(numSuggestions != -1)
				jtf.setText(Integer.toString(numSuggestions));
			else
				jtf.setText(Integer.toString(sizeOfCfd));
			jtf.setSelectionStart(0);
			jtf.setSelectionEnd(jtf.getText().length());
			int maxReturned;
			if(isCalcHist == true)
				maxReturned = 500;
			else
				maxReturned = sizeOfCfd;
			Object[] actualMessage = {"Please enter the desired number of features to consider\n" +
					"while coming up with suggestions:\n" +
					"(maximum : "+maxReturned+")\n\n", jtf,
					"\n\nNote:\nDepending on the features and classifier selected,\n" +
							"It may take a few mintues (or more) to classify your document.\n" +
							"If you would rather try a different classifier and/or choose\n" +
			"different features, click 'Cancel'."};
			theMessage.setMessage(actualMessage);	
			theMessage.setIcon(ThePresident.LOGO);
			theMessage.setMessageType(JOptionPane.INFORMATION_MESSAGE);
			Object[] twoChoices = new Object[]{"cancel","proceed"};
			theMessage.setOptions(twoChoices);
			JDialog JDLog;
			JDLog = theMessage.createDialog("How Many Suggestions Would You Like?");
			JDLog.setVisible(true);
			theMessage.setInitialSelectionValue("proceed");

			int theSelection = getSelection(theMessage);
			if(theSelection == 0)
				return -1;
			String strSuggestions = jtf.getText();
			int inputNum;
			try{
				inputNum = Integer.valueOf(strSuggestions);
				if(inputNum == 0)
					inputNum = -1;
				if(inputNum>maxReturned)
					throw new Exception();
			}
			catch(Exception e){
				inputNum = -1;
				Logger.logln("Invalid Selection.");
				JOptionPane.showMessageDialog(null, 
						"Please enter a single number greater than '0', and less than or equal to "+maxReturned+".\n" +
								"Keep in mind that you cannot return more suggestions than there are features.\n", 
								"Invalid Number",
								JOptionPane.ERROR_MESSAGE,
								GUIMain.iconNO);
				return -1;
			}/**/
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
	
	public static void spawnNew(GUIMain main){//spawns xtra tabs
		if(!isFirstRun){
			int answer = JOptionPane.showConfirmDialog(main, "Create new tab to edit document?\n\n" +
					"Note: Once a version of your document has been processed,\n" +
					"it may no longer be edited. However, by clicking on the text you wish\n" +
					"to edit, you may spawn a new tab containing a copy of that text.");  
			if( answer == 0){
				Logger.logln("Creating new editor inner tab");
				//System.out.println("EDIT TABBED PANE SELECTED INDEX at spawn: "+EditorTabDriver.selectedIndexTP);
			
				String nameFirstHalf = main.editTP.getTitleAt(selectedIndexTP);
				if(!nameFirstHalf.equals("Original"))
					nameFirstHalf = nameFirstHalf.substring(nameFirstHalf.indexOf("->")+2);
				eitsList.add(nextTabIndex,(new EditorInnerTabSpawner(eits).spawnTab()));
				main.editTP.addTab(nameFirstHalf+"->"+Integer.toString(numEdits), eitsList.get(nextTabIndex).editBoxPanel);
				main.editTP.setSelectedIndex(nextTabIndex);
				initEditorInnerTabListeners(main);
				main.processButton.setEnabled(true);
				eits.editorBox.setEnabled(false);
				ConsolidationStation.toModifyTaggedDocs.get(0).setSentenceCounter(-1);
				eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getNextSentence());
				eits.sentenceEditPane.setEnabled(true);
				//eits.sentenceEditPane.setText(helpMessege);
				eits.sentenceEditPane.setEditable(true);
				trackEditSentence(main);
				Logger.logln(ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedDocument());
				eits.editorBox.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedDocument());
				nextTabIndex++;
			}
			else
				Logger.logln("User Chose not to create a new tab when prompted.");
			
		}
	}
	
	public static void resetAll(GUIMain main){
		Logger.logln("Resetting all values");
		main.editTP.removeAll();
		EditorTabDriver.eitsList.clear();
		EditorInnerTabSpawner eits = (new EditorInnerTabSpawner()).spawnTab();
		EditorTabDriver.eitsList.add(0,eits);
		EditorTabDriver.eits = EditorTabDriver.eitsList.get(0);
		EditorTabDriver.eits.classificationLabel.setText("Please process your document in order to recieve a classification result.");
		eits.editorBox.setEnabled(false);
		main.editTP.addTab("Original",eits.editBoxPanel);
		main.editTP.setSelectedIndex(0);
		//initEditorInnerTabListeners(main);
		main.suggestionTable.setModel(new DefaultTableModel(
				new String[][] { { "" }, { "" } },
				new String[] { "", "" }));
		main.featureNameLabel.setText("-");
		main.presentValueField.setText(" - ");
		main.targetValueField.setText(" - ");
		main.processButton.setText("Process");
		main.processButton.setEnabled(true);
		main.processButton.setSelected(true);
		main.suggestionBox.setText("");
		
		main.elementsToAddPane.setText("");
		main.elementsToRemovePane.setText("");//not sure if needed to reset..
		
		EditorTabDriver.hasBeenInitialized = false;
		EditorTabDriver.hasCurrentAttrib = false;
		EditorTabDriver.isWorkingOnUpdating = false;
		EditorTabDriver.thisCaretPosition = 0;
		EditorTabDriver.okayToSelectSuggestion = false;
		EditorTabDriver.keyJustTyped = false;
		EditorTabDriver.checkForMouseInfluence =false;
		EditorTabDriver.consoleDead = true;
		EditorTabDriver.dictDead = true;
		EditorTabDriver.isCalcHist = false;
		EditorTabDriver.featuresInCfd.clear();
		EditorTabDriver.chosenAuthor = "n/a";
		EditorTabDriver.numSuggestions = -1;
		EditorTabDriver.numEdits = 0;
		EditorTabDriver.nextTabIndex = 1;
		EditorTabDriver.selectedIndexTP = 0;
		//System.out.println("EDIT TABBED PANE SELECTED INDEX at reset: "+EditorTabDriver.selectedIndexTP);
		
	}
	
	public static void initEditorInnerTabListeners(final GUIMain main){
		
		
		eits.shuffleButton.setEnabled(true);
		eits.restoreSentenceButton.setEnabled(true);
		
		eits.shuffleButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("Shuffle button pressed by User.");
				//shuffle current sentence
				if(!eits.sentenceEditPane.getText().startsWith(helpMessege)&&!eits.sentenceEditPane.getText().equals("Please press the Process button now.")){
					if(eits.sentenceEditPane.getText().matches(".*([?!]+)|.*([.]){1}\\s*")){//EOS "([?!]+)|([.]){1}\\s*"
						ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(eits.sentenceEditPane.getText());
					}
					TaggedSentence currentSentence=ConsolidationStation.toModifyTaggedDocs.get(0).getTaggedSentences().get(ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber());
					ArrayList<String> untaggedWords=new ArrayList<String>(currentSentence.size());
					ArrayList<Word> wordArr=currentSentence.getWordsInSentence();
					for(Word word:wordArr){//if theres an EOS char then the sentence should be saved
						System.out.println("Word: "+word.getUntagged());
						if(word.getUntagged().matches("[\\w&&[^\\d]]*")){
							//if(!ConsolidationStation.functionWords.searchListFor(word.getUntagged())){//TODO: make sure this works
							//if(!topToRemove.contains(word.getUntagged())){
								untaggedWords.add(word.getUntagged());//This excludes ALL function words and punctuation
								//System.out.println(word.getUntagged());
							//}
							//else if((word.getUntagged())){
								
							//}
						}
					}
					//does the shuffling
					int sizeOfWordList=untaggedWords.size(),randNum;
					String toReturn="",temp;
					for(int i=0;i<sizeOfWordList;i++){
						randNum=(int) ((Math.random())*(untaggedWords.size()-1)+.5);
						temp=untaggedWords.remove(randNum);
						toReturn+=temp+" ";
						//Logger.logln(toReturn);
					}
					eits.sentenceEditPane.setText(toReturn);
				}
			}
			
		});	
		eits.restoreSentenceButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("Previous sentence restored.");
				if(!eits.sentenceEditPane.getText().startsWith(helpMessege)&&!eits.sentenceEditPane.getText().equals("Please press the Process button now.")){
					eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getUntaggedSentences().get(ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber()));
					//eits.sentenceEditPane.setText(ConsolidationStation.toModifyTaggedDocs.get(0).getCurrentLiveTaggedSentence());
				}
			}
			
		});	
		eits.removeWordsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("Previous sentence restored.");
				if(!eits.sentenceEditPane.getText().startsWith(helpMessege)&&!eits.sentenceEditPane.getText().equals("Please press the Process button now.")){
					if(eits.sentenceEditPane.getText().matches(".*([?!]+)|.*([.]){1}\\s*")){//EOS "([?!]+)|([.]){1}\\s*"
						ConsolidationStation.toModifyTaggedDocs.get(0).removeAndReplace(eits.sentenceEditPane.getText());
					}
					TaggedSentence currentSentence=ConsolidationStation.toModifyTaggedDocs.get(0).getTaggedSentences().get(ConsolidationStation.toModifyTaggedDocs.get(0).getSentNumber());
					ArrayList<String> untaggedWords=new ArrayList<String>(currentSentence.size());
					ArrayList<Word> wordArr=currentSentence.getWordsInSentence();
					for(Word word:wordArr){//if theres an EOS char then the sentence should be saved
						System.out.println("Word: "+word.getUntagged());
						//if(word.getUntagged().matches("[\\w&&[^\\d]]*")){
							//if(!ConsolidationStation.functionWords.searchListFor(word.getUntagged())){//TODO: make sure this works
							if(!topToRemove.contains(word.getUntagged())){
								untaggedWords.add(word.getUntagged());//This excludes ALL function words and punctuation
								//System.out.println(word.getUntagged());
							}
						//}
					}
					int sizeOfWordList=untaggedWords.size();
					String toReturn="";
					for(int i=0;i<sizeOfWordList;i++){
						toReturn+=untaggedWords.get(i)+" ";
						//Logger.logln(toReturn);
					}
					eits.sentenceEditPane.setText(toReturn);
				}
			}
			
		});	
		
		eits.editorBox.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!eits.sentenceEditPane.isEditable()){
					spawnNew(main);
				}
				
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				thisCaretPosition = eits.editorBox.getCaretPosition();
				mouseEndPosition =0;
				checkForMouseInfluence = true;
				//System.out.println("Mouse press registered at index: "+thisCaretPosition);
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mouseEndPosition = eits.editorBox.getCaretPosition();
				//System.out.println("Caret position registered at mousereleased: "+mouseEndPosition);
			}
			
		});

		
		
		
		eits.editorBox.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				/*
				if(checkForMouseInfluence == true){
					if(mouseEndPosition > thisCaretPosition)
						oldCaretPosition = thisCaretPosition - (mouseEndPosition-thisCaretPosition);
					else if(mouseEndPosition < thisCaretPosition)
						oldCaretPosition = thisCaretPosition +(thisCaretPosition - mouseEndPosition);
					else
						oldCaretPosition = thisCaretPosition;
				}
				else
				*/
					oldCaretPosition = thisCaretPosition;
						
					
				
				//System.out.println("Caret postion registered at keypressed: old: "+oldCaretPosition);
			}
		

			@Override
			public void keyReleased(KeyEvent arg0) {
				
				thisCaretPosition = eits.editorBox.getCaretPosition();
				//System.out.println("Caret postion resitered at keyreleased:  "+thisCaretPosition);
				if(keyJustTyped == true){
					keyJustTyped = false;
					//System.out.println("Should start present features continuous present value update thread..");
					BackendInterface.updatePresentFeatureNow(main, eits,theMirror);
					//int caretPos =main.editorBox.getCaretPosition();
					//System.out.println("Old Caret Postion: "+oldCaretPosition+" and current CARET POSITION IS: "+thisCaretPosition);
					//Collections.sort(highlightedObjects);
					if(oldCaretPosition < thisCaretPosition){
						
						Iterator<HighlightMapper> hloi = highlightedObjects.iterator();
						boolean isGone;
						while(hloi.hasNext()){
							isGone = false;
							HighlightMapper tempHm = hloi.next();
							if((tempHm.getStart() <= thisCaretPosition) && (oldCaretPosition <= tempHm.getEnd())){
								//System.out.println("FOUND object... start at: "+tempHm.getStart()+" end at: "+tempHm.getEnd());
								eits.editorBox.getHighlighter().removeHighlight(tempHm.getHighlightedObject());
								isGone = true;
							}	
							if ((oldCaretPosition <= tempHm.getStart() && !isGone))
								tempHm.increment(thisCaretPosition - oldCaretPosition);
						}
						
						
					}
					else if(oldCaretPosition > thisCaretPosition){
						Iterator<HighlightMapper> hloi = highlightedObjects.iterator();
						boolean isGone;
						while(hloi.hasNext()){
							isGone = false;
							HighlightMapper tempHm = hloi.next();
							if((tempHm.getStart() <= oldCaretPosition) && (thisCaretPosition <= tempHm.getEnd())){
								//System.out.println("FOUND object ... start at: "+tempHm.getStart()+" end at: "+tempHm.getEnd());
								eits.editorBox.getHighlighter().removeHighlight(tempHm.getHighlightedObject());
								isGone = true;
							}	
							if ((thisCaretPosition <= tempHm.getStart()) && !isGone)
								tempHm.decrement(oldCaretPosition - thisCaretPosition);
						}
					
					}
				}
			}
			

			@Override
			public void keyTyped(KeyEvent arg0) {
				keyJustTyped = true;
			}
				
			
		
		});
	
		
		
		
	}
	
	public static void dispHighlights(){
		Highlighter highlight = eits.editorBox.getHighlighter();
		HashMap<Color,ArrayList<int[]>> currentMap = HighlightMapMaker.highlightMap;
		int i = 0;
		if(!currentMap.isEmpty()){
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
						EditorTabDriver.highlightedObjects.add(hm);
					} catch (BadLocationException e) {
						Logger.logln("Error displaying highlights.");// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		
	}
	
	
} 

	class TheHighlighter extends DefaultHighlighter.DefaultHighlightPainter{
		public TheHighlighter(Color color){
			super(color);
		}
	}
/*	
	class PredictionRenderer implements TableCellRenderer {

		  public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

		  public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {
		    Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(
		        table, value, isSelected, hasFocus, row, column);
		    ((JLabel) renderer).setOpaque(true);
		    Color foreground, background;
		    
		      if ((column  == editorTabDriver.resultsMaxIndex) && (row==0)) {
			    	 if(editorTabDriver.chosenAuthor.equals(DocumentMagician.authorToRemove)){
			        foreground = Color.black;
			        background = Color.red;
			      } else {
			        foreground = Color.black;
			        background = Color.green;
			      }
		      }
		      else{
		    	  	foreground = Color.black;
		    	  	background = Color.white;
		      }
		    
		    renderer.setForeground(foreground);
		    renderer.setBackground(background);
		    return renderer;
		  }
		}	
	
	*/
	
	 class ClassifyingProgressBar implements Runnable {

		GUIMain main;
		
		public ClassifyingProgressBar(GUIMain main){
			this.main = main;
			new Thread(this,"ClassifyingProgressBar").start();
		}

		@Override
		public void run() {
			main.editorProgressBar.setEnabled(true);
			main.editorProgressBar.setIndeterminate(true);	
		}
		
		public void stop(){
			main.editorProgressBar.setIndeterminate(false);
			main.editorProgressBar.setEnabled(false);
		}
		
		public void setText(String s){
			main.editingProgressBarLabel.setText(s);
		}
	}
			
	 class SuggestionCalculator implements Runnable{
		//TODO: need to process sentence to find most salient features
		 
		 GUIMain main;
		 EditorInnerTabSpawner eits;
		 int suggestionNumber;
		 
		 public SuggestionCalculator(GUIMain main, EditorInnerTabSpawner eits, int sel){
			Logger.logln("Entered SuggestionCalculator.");
			this.main = main;
			this.suggestionNumber = sel;
			this.eits = eits;
		 }
		 
		public void run(){ 
			int i;
			boolean noChangeNeeded= false;
			//System.out.println("SUGGESTION NUMBER IS: "+suggestionNumber);
			EditorTabDriver.selectedFeature = EditorTabDriver.theFeatures[suggestionNumber].toString();
			//Logger.logln("Suggestion selected:"+EditorTabDriver.selectedFeature);
			//EditorTabDriver.currentAttrib = EditorTabDriver.wizard.runSelectedFeature(suggestionNumber,false);
			EditorTabDriver.currentAttrib = EditorTabDriver.wizard.getAttributes()[EditorTabDriver.suggestionToAttributeMap.get(suggestionNumber)];
			EditorTabDriver.hasCurrentAttrib = true;
			FeatureDriver theOneToUpdate = main.cfd.featureDriverAt(EditorTabDriver.featuresInCfd.indexOf(EditorTabDriver.currentAttrib.getGenericName().toString()));
			Logger.logln("Retrieved feature driver for currentAttrib");
			Document currDoc = new Document();
			currDoc.setText(eits.editorBox.getText().toCharArray());
			List<Canonicizer> canonList = theOneToUpdate.getCanonicizers();
			try{
				Iterator<Canonicizer> canonIter = canonList.iterator();
				while(canonIter.hasNext())
					currDoc.addCanonicizer(canonIter.next());
			} catch(NullPointerException npe){
			}
			Computer.setTheDocument(currDoc);
			TheOracle.setTheDocument(currDoc.stringify());
			Logger.logln("Set document text to Computer and TheOracle");

			Highlighter highlight = eits.editorBox.getHighlighter();
			HashMap<Color,ArrayList<int[]>> currentMap = new HashMap<Color,ArrayList<int[]>>();
			try{
				Logger.logln("Getting suggestion from Suggestor...");
				EditorTabDriver.utterance = EditorTabDriver.theMirror.callRelevantSuggestor(EditorTabDriver.currentAttrib);
				main.suggestionBox.setText(EditorTabDriver.utterance.getSuggestion());
				if((EditorTabDriver.currentAttrib.getGenericName().toString()).contains("PERCENT"))
					main.targetValueField.setText(Double.toString((Math.floor(EditorTabDriver.currentAttrib.getTargetValue()*10000+.5)/10000)*100));
				else
					main.targetValueField.setText(Double.toString((Math.floor(EditorTabDriver.currentAttrib.getTargetValue()*10000+.5)/10000)));
				main.presentValueField.setText(Double.toString((Math.floor(EditorTabDriver.currentAttrib.getToModifyValue()*10000+.5)/10000)));
				main.featureNameLabel.setText(EditorTabDriver.currentAttrib.getGenericName().toString()+" "+EditorTabDriver.currentAttrib.getStringInBraces()+":");
				currentMap= EditorTabDriver.utterance.getHighlightMap();
				noChangeNeeded = EditorTabDriver.utterance.getNoChangeNeeded();
				Logger.logln("Suggestion obtained.");
			} catch (Exception e){
				e.printStackTrace();
				EditorTabDriver.hasCurrentAttrib = false;
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
				Logger.logln("HighlightMap is not empty - will begin highlighting");
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
							EditorTabDriver.highlightedObjects.add(hm);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			else
				Logger.logln("Highlight map empty - nothing to highlight. Finished in SuggestionCalculator");
			main.processButton.setEnabled(true);
			EditorTabDriver.okayToSelectSuggestion = true;
			//System.out.println("Should exit Suggestion Calculator.");
		} 
	 }
