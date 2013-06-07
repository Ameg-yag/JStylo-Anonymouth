package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.drexel.psal.anonymouth.engine.DataAnalyzer;
import edu.drexel.psal.anonymouth.engine.DocumentMagician;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;
import edu.drexel.psal.anonymouth.utils.Tagger;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.ProblemSet;

public class BackendInterface {
	
	private final String NAME = "( "+this.getClass().getName()+" ) - ";
	private ProgressWindow pw;
	public static Boolean processed = false;
	
	protected static BackendInterface bei = new BackendInterface();
	
	public class GUIThread implements Runnable {
		GUIMain main;
		
		public GUIThread(GUIMain main) {
			
			this.main = main;
		}
		
		public void run() {}
	}
	
	/* ========================
	 * documents tab operations
	 * ========================
	 */
	
	// -- none --
	// all operations are fast, so no backend threads are ran.
	
	
	/**
	 * documents tab >> create new problem set
	 */
	protected static void docTabCreateNewProblemSet(GUIMain main) {
		Logger.logln("( BackendInterface ) - create new problem set");
		(new Thread(bei.new DocTabNewProblemSetButtonClick(main))).start();
	}
	
	public class DocTabNewProblemSetButtonClick extends GUIThread {
		
		public DocTabNewProblemSetButtonClick(GUIMain main) {
			super(main);
		}

		public void run() {
			Logger.logln(NAME+"Backend: create new problem set thread started.");
			
			// initialize probelm set
			main.ps = new ProblemSet();
			main.ps.setTrainCorpusName(main.defaultTrainDocsTreeName);
			GUIUpdateInterface.updateProblemSet(main);
			
			Logger.logln(NAME+"Backend: create new problem set thread finished.");
		}
	}
	
	protected static void runVerboseOutputWindow(GUIMain main){
		new Thread(bei.new RunVerboseOutputWindow(main)).start();
		
	}
	
	public class RunVerboseOutputWindow extends GUIThread{

		public RunVerboseOutputWindow(GUIMain main) {
			super(main);
		}

		public void run() {
			new Console();
		}

	}



	protected static void preTargetSelectionProcessing(GUIMain main,DataAnalyzer wizard, DocumentMagician magician){
		(new Thread(bei.new PreTargetSelectionProcessing(main,wizard,magician))).start();
	}
	
	public class PreTargetSelectionProcessing extends GUIThread {
		
		private DataAnalyzer wizard;
		private DocumentMagician magician;		
		
		public PreTargetSelectionProcessing(GUIMain main,DataAnalyzer wizard, DocumentMagician magician){
			super(main);
			this.wizard = wizard;
			this.magician = magician;
		}

		public String getDocFromCurrentTab()
		{
			return main.getDocumentPane().getText();
		}

		public void run() {
			try {
				pw = new ProgressWindow("Processing...", main);
				pw.run();
				
				processed = true;
				DocumentMagician.numProcessRequests++;
				String tempDoc = "";

				if (DriverDocumentsTab.isFirstRun == true) {
					ConsolidationStation.functionWords.run();
					tempDoc = getDocFromCurrentTab();
					Logger.logln(NAME+"Process button pressed for first time (initial run) in editor tab");
					
					pw.setText("Extracting and Clustering Features...");
					try{
						wizard.runInitial(magician,main.cfd, main.classifiers.get(0));
						pw.setText("Initializing Tagger...");
						Tagger.initTagger();
						pw.setText("Initialize Cluster Viewer...");
						pw.setText("Classifying Documents...");
						magician.runWeka();
						wizard.runClusterAnalysis(magician);
						DriverClustersWindow.initializeClusterViewer(main,false);
					} catch(Exception e) {
						e.printStackTrace();
						ErrorHandler.fatalError();
					}
					
					Map<String,Map<String,Double>> wekaResults = magician.getWekaResultList();
					Logger.logln(NAME+" ****** WEKA RESULTS for session '"+ThePresident.sessionName+" process number : "+DocumentMagician.numProcessRequests);
					Logger.logln(NAME+wekaResults.toString());
					makeResultsChart(wekaResults, main);
				} else {
					Logger.logln(NAME+"Process button pressed to re-process document to modify.");
					tempDoc = getDocFromCurrentTab();
					if(tempDoc.equals("") == true) {
						JOptionPane.showMessageDialog(null,
								"It is not possible to process an empty document.",
								"Document processing error",
								JOptionPane.ERROR_MESSAGE,
								GUIMain.iconNO);
					} else {
						magician.setModifiedDocument(tempDoc);

						pw.setText("Extracting and Clustering Features...");
						try {
							wizard.reRunModified(magician);
							pw.setText("Initialize Cluster Viewer...");
							DriverClustersWindow.initializeClusterViewer(main,false);
							pw.setText("Classifying Documents...");
							magician.runWeka();
						} catch (Exception e) {
							e.printStackTrace();
							ErrorHandler.fatalError();
						}

						Map<String,Map<String,Double>> wekaResults = magician.getWekaResultList();
						Logger.logln(NAME+" ****** WEKA RESULTS for session '"+ThePresident.sessionName+" process number : "+DocumentMagician.numProcessRequests);
						Logger.logln(NAME+wekaResults.toString());
						makeResultsChart(wekaResults, main);
					}
				}


			ConsolidationStation.toModifyTaggedDocs.get(0).setBaselinePercentChangeNeeded(); // todo figure out why this and/or the two percent change needed calls in TaggedDocument affect AnonymityBar

			DriverDocumentsTab.theFeatures = wizard.getAllRelevantFeatures();
			Logger.logln(NAME+"The Features are: "+DriverDocumentsTab.theFeatures.toString());

			DriverDocumentsTab.okayToSelectSuggestion = true;
			
			if(DriverDocumentsTab.isFirstRun)
				ConsolidationStation.toModifyTaggedDocs.get(0).makeAndTagSentences(main.getDocumentPane().getText(), true);
			else
				ConsolidationStation.toModifyTaggedDocs.get(0).makeAndTagSentences(main.getDocumentPane().getText(), false);

			main.anonymityDrawingPanel.updateAnonymityBar();
			main.anonymityDrawingPanel.showPointer(true);

			for (int i = 0; i < DriverDocumentsTab.taggedDoc.getTaggedSentences().size(); i++)
				DriverDocumentsTab.originals.put(DriverDocumentsTab.taggedDoc.getUntaggedSentences(false).get(i), DriverDocumentsTab.taggedDoc.getTaggedSentences().get(i));

			DriverDocumentsTab.originalSents = DriverDocumentsTab.taggedDoc.getUntaggedSentences(false);
			SuggestionCalculator.placeSuggestions(main);
			GUIUpdateInterface.updateResultsPrepColor(main);
			
			DriverDocumentsTab.setAllDocTabUseable(true, main);		
			
			DriverDocumentsTab.ignoreNumActions = 1; // must be set to 1, otherwise "....setDot(0)" (2 lines down) will screw things up when it fires the caretUpdate listener.
			main.getDocumentPane().setText(DriverDocumentsTab.taggedDoc.getUntaggedDocument(false));// NOTE this won't fire the caretListener because (I THINK) this method isn't in a listener, because setting the text from within a listener (directly or indirectly) DOES fire the caretUpdate.
			main.getDocumentPane().getCaret().setDot(0); // NOTE However, THIS DOES fire the caretUpdate, because we are messing with the caret itself.
			main.getDocumentPane().setCaretPosition(0); // NOTE And then this, again, does not fire the caretUpdate
			DriverDocumentsTab.ignoreNumActions = 0; //We MUST reset this to 0 because, for whatever reason, sometimes setDot() does not fire the caret listener, so ignoreNumActions is never reset. This is to ensure it is.
			
			int[] selectedSentInfo = DriverDocumentsTab.calculateIndicesOfSentences(0)[0];
			DriverDocumentsTab.selectedSentIndexRange[0] = selectedSentInfo[1];
			DriverDocumentsTab.selectedSentIndexRange[1] = selectedSentInfo[2];
			DriverDocumentsTab.moveHighlight(main, DriverDocumentsTab.selectedSentIndexRange);
			

			GUIMain.GUITranslator.load(DriverDocumentsTab.taggedDoc.getTaggedSentences());
			DriverDocumentsTab.charsInserted = 0; // this gets updated when the document is loaded.
			DriverDocumentsTab.charsRemoved = 0;	
			DriverDocumentsTab.caretPositionPriorToCharInsertion = 0;
			DriverDocumentsTab.isFirstRun = false;	
			
			DictionaryBinding.init();//initializes the dictionary for wordNEt
			
			Logger.logln(NAME+"Finished in BackendInterface - postTargetSelection");

			main.processButton.setText("Re-Process");


			main.documentScrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
			main.versionControl.addVersion(DriverDocumentsTab.taggedDoc);
			
			GUIMain.processed = true; 
			
			pw.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
			// Get current size of heap in bytes
			long heapSize = Runtime.getRuntime().totalMemory();

			// Get maximum size of heap in bytes. The heap cannot grow beyond this size.
			// Any attempt will result in an OutOfMemoryException.
			long heapMaxSize = Runtime.getRuntime().maxMemory();

			// Get amount of free memory within the heap in bytes. This size will increase
			// after garbage collection and decrease as new objects are created.
			long heapFreeSize = Runtime.getRuntime().freeMemory();
			Logger.logln(NAME+"Something happend. Here are the total, max, and free heap sizes:");
			Logger.logln(NAME+"Total: "+heapSize+" Max: "+heapMaxSize+" Free: "+heapFreeSize);
		}
		
	}
}

	public static void makeResultsChart(Map<String,Map<String,Double>> resultMap, GUIMain main){
		
		Iterator<String> mapKeyIter = resultMap.keySet().iterator();
		Map<String,Double> tempMap = resultMap.get(mapKeyIter.next()); 
		
		int numAuthors = DocumentMagician.numSampleAuthors+1;
		
		Object[] authors = (tempMap.keySet()).toArray();
		Double[] predictions = new Double[authors.length];
		Map<Double, Object> predMap = new HashMap<Double, Object>();
		
		Object[] keyRing = tempMap.values().toArray();
		int maxIndex = 0;
		Double biggest = .01;
		for(int i = 0; i < numAuthors; i++){
			Double tempVal = ((Double)keyRing[i])*100;
			// compare PRIOR to rounding.
			if(biggest < tempVal){
				biggest = tempVal;
				maxIndex = i;
			}
			int precision = 100;
			tempVal = Math.floor(tempVal*precision+.5)/precision;	
			predictions[i] = tempVal;
			
			if (authors[i].equals(ThePresident.DUMMY_NAME)) {
				predMap.put(predictions[i], "You");
			} else
				predMap.put(predictions[i], authors[i]);
		}
		
		Arrays.sort(predictions);
		
		for (int i = numAuthors-1; i >= 0; i--){
			main.resultsWindow.addAttrib(predMap.get(predictions[i]).toString(), (int)(predictions[i] + .5));
		}
		
		DriverDocumentsTab.resultsMaxIndex = maxIndex;
		DriverDocumentsTab.chosenAuthor = (String)authors[maxIndex];
		DriverDocumentsTab.maxValue = (Object)biggest;
		
		main.resultsWindow.makeChart();
		main.resultsWindow.drawingPanel.repaint();
		main.resultsMainPanel.repaint();
	}
	
}