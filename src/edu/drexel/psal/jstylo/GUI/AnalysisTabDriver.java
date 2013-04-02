package edu.drexel.psal.jstylo.GUI;

import edu.drexel.psal.jstylo.GUI.DocsTabDriver.ExtFilter;
import edu.drexel.psal.jstylo.analyzers.WekaAnalyzer;
import edu.drexel.psal.jstylo.analyzers.writeprints.WriteprintsAnalyzer;
import edu.drexel.psal.jstylo.generics.AnalyzerTypeEnum;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.WekaInstancesBuilder;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgaap.generics.Document;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class AnalysisTabDriver {

	/* ======================
	 * Analysis tab listeners
	 * ======================
	 */
	
	/**
	 * Initializes all listeners for the analysis tab.
	 */
	protected static void initListeners(final GUIMain main) {
		
		// calculate InfoGain checkbox
		// ===========================
		
		main.analysisCalcInfoGainJCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("Calculate InfoGain checkbox was clicked on the analysis tab.");
				
				// enable / disable the apply InfoGain option
				boolean selected = main.analysisCalcInfoGainJCheckBox.isSelected();
				Logger.logln("Calculate InfoGain option - " + (selected ? "selected" : "unselected"));
				main.analysisApplyInfoGainJCheckBox.setEnabled(selected);
				main.infoGainValueJTextField.setEnabled(selected);
			}
		});
		
		// apply InfoGain checkbox
		// =======================
		
		main.analysisApplyInfoGainJCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.logln("Apply InfoGain checkbox was clicked on the analysis tab.");
				
				// enable / disable apply InfoGain text field
				boolean selected = main.analysisApplyInfoGainJCheckBox.isSelected();
				Logger.logln("Apply InfoGain option - " + (selected ? "selected" : "unselected"));
				main.infoGainValueJTextField.setEnabled(selected);
			}
		});
		
		// export training to ARFF button
		// ==============================
		
		main.analysisExportTrainToARFFJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("'Training to ARFF...' button clicked on the analysis tab.");
				
				// check if not null
				if (main.wib.getTrainingSet() == null) {
					JOptionPane.showMessageDialog(main,
							"No analysis completed yet.",
							"Export Training Features Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// write to ARFF
				JFileChooser save = new JFileChooser(main.defaultLoadSaveDir);
				save.addChoosableFileFilter(new ExtFilter("Attribute-Relation File Format (*.arff)", "arff"));
				int answer = save.showSaveDialog(main);

				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".arff"))
						path += ".arff";
					boolean succeeded = WekaInstancesBuilder.writeSetToARFF(path, main.wib.getTrainingSet());
					if (succeeded) {
						Logger.log("Saved training features to arff: "+path);
						main.defaultLoadSaveDir = (new File(path)).getParent();
					} else {
						Logger.logln("Failed opening "+path+" for writing",LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving training features into:\n"+path,
								"Export Training Features Failure",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					Logger.logln("Export training features to ARFF canceled");
				}
			}
		});
		
		// export training to CSV button
		// =============================
		
		main.analysisExportTrainToCSVJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("'Training to CSV...' button clicked on the analysis tab.");
				
				// check if not null
				if (main.wib.getTrainingSet() == null) {
					JOptionPane.showMessageDialog(main,
							"No analysis completed yet.",
							"Export Training Features Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// write to CSV
				JFileChooser save = new JFileChooser(main.defaultLoadSaveDir);
				save.addChoosableFileFilter(new ExtFilter("Comma-separated values (*.csv)", "csv"));
				int answer = save.showSaveDialog(main);

				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".csv"))
						path += ".csv";
					boolean succeeded = WekaInstancesBuilder.writeSetToCSV(path, main.wib.getTrainingSet());
					if (succeeded) {
						Logger.log("Saved training features to csv: "+path);
						main.defaultLoadSaveDir = (new File(path)).getParent();
					} else {
						Logger.logln("Failed opening "+path+" for writing",LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving training features into:\n"+path,
								"Export Training Features Failure",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					Logger.logln("Export training features to CSV canceled");
				}
			}
		});
		
		// export test to ARFF button
		// ==========================
		
		main.analysisExportTestToARFFJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("'Test to ARFF...' button clicked on the analysis tab.");
				
				// check if not null
				if (main.wib.getTestSet() == null) {
					JOptionPane.showMessageDialog(main,
							"No analysis with test documents completed yet.",
							"Export Test Features Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// write to ARFF
				JFileChooser save = new JFileChooser(main.defaultLoadSaveDir);
				save.addChoosableFileFilter(new ExtFilter("Attribute-Relation File Format (*.arff)", "arff"));
				int answer = save.showSaveDialog(main);

				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".arff"))
						path += ".arff";
					boolean succeeded = WekaInstancesBuilder.writeSetToARFF(path, main.wib.getTestSet());
					if (succeeded) {
						Logger.log("Saved test features to arff: "+path);
						main.defaultLoadSaveDir = (new File(path)).getParent();
					} else {
						Logger.logln("Failed opening "+path+" for writing",LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving test features into:\n"+path,
								"Export Test Features Failure",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					Logger.logln("Export training features to ARFF canceled");
				}
			}
		});
		
		// export test to CSV button
		// =========================

		main.analysisExportTestToCSVJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("'Test to CSV...' button clicked on the analysis tab.");

				// check if not null
				if (main.wib.getTestSet() == null) {
					JOptionPane.showMessageDialog(main,
							"No analysis with test documents completed yet.",
							"Export Test Features Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// write to CSV
				JFileChooser save = new JFileChooser(main.defaultLoadSaveDir);
				save.addChoosableFileFilter(new ExtFilter("Comma-separated values (*.csv)", "csv"));
				int answer = save.showSaveDialog(main);

				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".csv"))
						path += ".csv";
					boolean succeeded = WekaInstancesBuilder.writeSetToARFF(path, main.wib.getTestSet());
					if (succeeded) {
						Logger.log("Saved test features to csv: "+path);
						main.defaultLoadSaveDir = (new File(path)).getParent();
					} else {
						Logger.logln("Failed opening "+path+" for writing",LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving test features into:\n"+path,
								"Export Test Features Failure",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					Logger.logln("Export training features to CSV canceled");
				}
			}
		});
		
		// run analysis button
		// ===================
		
		main.analysisRunJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.logln("'Run Analysis' button clicked in the analysis tab.");
				
				// check
				if (main.ps == null || main.ps.getAllTrainDocs().size() == 0) {
					JOptionPane.showMessageDialog(main,
							"Training corpus not set or empty.",
							"Run Analysis Error",
							JOptionPane.ERROR_MESSAGE);
					return;
					
				} else if (main.analysisClassTestDocsJRadioButton.isSelected() && main.ps.getTestDocs().isEmpty()) {
					JOptionPane.showMessageDialog(main,
							"Test documents not set.",
							"Run Analysis Error",
							JOptionPane.ERROR_MESSAGE);
					return;
					
				} else if (main.cfd == null || main.cfd.numOfFeatureDrivers() == 0) {
					JOptionPane.showMessageDialog(main,
							"Feature set not set or has no features.",
							"Run Analysis Error",
							JOptionPane.ERROR_MESSAGE);
					return;
					
				} else if (main.classifiers.isEmpty()) {
					JOptionPane.showMessageDialog(main,
							"No classifiers added.",
							"Run Analysis Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// lock
				lockUnlock(main, true);
				
				// start analysis thread
				//main.at = AnalyzerTypeEnum.WRITEPRINTS_ANALYZER;
				main.analysisThread = new Thread(new RunAnalysisThread(main));
				main.analysisThread.start();
			}
		});
		
		// stop analysis button
		// ====================
		
		main.analysisStopJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.logln("'Stop' button clicked in the analysis tab.");
				
				// confirm
				int answer = JOptionPane.showConfirmDialog(main,
						"Are you sure you want to abort analysis?",
						"Stop Analysis",
						JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					// stop run and update
					Logger.logln("Stopping analysis");
					
					main.analysisThread.stop();
					lockUnlock(main, false);
				}
			}
		});
		
		// save results button
		// ===================
		
		main.analysisSaveResultsJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln("'Save Results...' button clicked on the analysis tab.");

				// check there are results
				if (main.analysisResultsJTabbedPane.getTabCount() == 0) {
					JOptionPane.showMessageDialog(main,
							"No results available to save.",
							"Save Analysis Results Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// write to text file
				JFileChooser save = new JFileChooser(main.defaultLoadSaveDir);
				save.addChoosableFileFilter(new ExtFilter("Text files (*.txt)", "txt"));
				int answer = save.showSaveDialog(main);

				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".txt"))
						path += ".txt";
					
					BufferedWriter bw = null;
					try {
						int selected = main.analysisResultsJTabbedPane.getSelectedIndex();
						bw = new BufferedWriter(new FileWriter(path));
						bw.write(main.results.get(selected));
						bw.flush();
						bw.close();
						main.defaultLoadSaveDir = (new File(path)).getParent();
					} catch (Exception e) {
						Logger.logln("Failed opening "+path+" for writing",LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving analysis results into:\n"+path,
								"Save Analysis Results Failure",
								JOptionPane.ERROR_MESSAGE);
						if (bw != null) {
							try {
								bw.close();
							} catch (Exception e2) {}
						}
						return;
					}
					
					Logger.log("Saved analysis results: "+path);
					
				} else {
					Logger.logln("Export analysis results canceled");
				}
			}
		});
		
		// about button
		// ============
		
		main.analysisAboutJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIUpdateInterface.showAbout(main);
			}
		});
		
		// back button
		// ===========
		
		main.analysisBackJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.logln("'Back' button clicked in the analysis tab");
				main.mainJTabbedPane.setSelectedIndex(2);
			}
		});
	}
	
	/**
	 * Returns the timestamp when called.
	 */
	protected static String getTimestamp() {
		SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return tf.format(cal.getTime());
	}
	
	/**
	 * Locks / unlocks analysis tab during analysis / when done or stop is clicked.
	 */
	protected static void lockUnlock(GUIMain main, boolean lock) {
		// tabbed pane
		main.mainJTabbedPane.setEnabled(!lock);
		
		// all action buttons
		main.analysisClassTestDocsJRadioButton.setEnabled(!lock);
		main.analysisTrainCVJRadioButton.setEnabled(!lock);
		
		//main.analysisOutputAccByClassJCheckBox.setEnabled(!lock);
		//main.analysisOutputConfusionMatrixJCheckBox.setEnabled(!lock);
		main.analysisOutputFeatureVectorJCheckBox.setEnabled(!lock);
		main.analysisSparseInstancesJCheckBox.setEnabled(!lock);
		main.analysisCalcInfoGainJCheckBox.setEnabled(!lock);
		main.analysisApplyInfoGainJCheckBox.setEnabled(!lock);
		main.infoGainValueJTextField.setEnabled(!lock);
		
		main.analysisExportTrainToARFFJButton.setEnabled(!lock);
		main.analysisExportTestToARFFJButton.setEnabled(!lock);
		main.analysisExportTrainToCSVJButton.setEnabled(!lock);
		main.analysisExportTestToCSVJButton.setEnabled(!lock);

		main.analysisRunJButton.setEnabled(!lock);
		main.analysisStopJButton.setEnabled(lock);
		
		main.analysisSaveResultsJButton.setEnabled(!lock);
		
		// progress bar
		main.analysisJProgressBar.setIndeterminate(lock);
		
		// back button
		main.analysisBackJButton.setEnabled(!lock);
		
	}
	
	
	/* ==========================================
	 * Main thread class for running the analysis
	 * ==========================================
	 */
	
	public static class RunAnalysisThread implements Runnable {
		
		protected GUIMain main;
		protected JScrollPane scrollPane;
		protected JTextArea contentJTextArea;
		protected String content;
		
		public RunAnalysisThread(GUIMain main) {
			this.main = main;
		}

		public void run() {
			Logger.logln(">>> Run Analysis thread started.");
						
			// initialize results tab
			JPanel tab = new JPanel(new BorderLayout());
			main.analysisResultsJTabbedPane.addTab(getTimestamp(), tab);
			main.analysisResultsJTabbedPane.setSelectedIndex(main.analysisResultsJTabbedPane.getTabCount()-1);
			scrollPane = new JScrollPane();
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tab.add(scrollPane);
			contentJTextArea = new JTextArea();
			contentJTextArea.setFont(new Font("Courier New",0,12));
			scrollPane.setViewportView(contentJTextArea);
			contentJTextArea.setEditable(false);
			content = "";
			boolean classifyTestDocs = main.analysisClassTestDocsJRadioButton.isSelected();

			// update header
			// -------------
			content +=
					"============================ JStylo Analysis Output ============================\n" +
					"Started analysis on "+getTimestamp()+"\n" +
					(classifyTestDocs ? "Running test documents classification" : "Running 10-folds cross validation on training corpus")+"\n"+
					"\n";
			
			// training set
			content += "Training corpus:\n";
			Enumeration<DefaultMutableTreeNode> authors = ((DefaultMutableTreeNode) main.trainCorpusJTree.getModel().getRoot()).children();
			DefaultMutableTreeNode author;
			while (authors.hasMoreElements()) {
				author = authors.nextElement();
				content += "> "+author.getUserObject().toString()+" ("+author.getChildCount()+" documents)\n";
			}
			content += "\n";
			
			// test set
			if (classifyTestDocs) {
				content += "Test documents:\n";
				int numRows = main.testDocsJTable.getModel().getRowCount();
				String doc;
				for (int i=0; i<numRows; i++) {
					doc = main.testDocsJTable.getModel().getValueAt(i,0).toString();
					content += "> "+doc+"\n";
				}
				content += "\n";
			}
			
			// documents
			List<Document> trainingDocs = main.ps.getAllTrainDocs();
			List<Document> testDocs = main.ps.getTestDocs();
			int numTrainDocs = trainingDocs.size();
			int numTestDocs = testDocs.size();
			
			// feature set
			content += "Feature set: "+main.cfd.getName()+":\n";
			for (int i=0; i<main.cfd.numOfFeatureDrivers(); i++) {
				content += "> "+main.cfd.featureDriverAt(i).getName()+"\n";
			}
			content += "\n";
			
			// classifiers
			content += "Classifiers used:\n";
			for (Classifier c: main.classifiers) {
				content += "> "+String.format("%-50s", c.getClass().getName())+"\t"+ClassTabDriver.getOptionsStr(c.getOptions())+"\n";
			}

			content +=
					"\n"+
					"================================================================================\n"+
					"\n";
			
			contentJTextArea.setText(content);

			// feature extraction
			// ==================
			
			// pre-processing
			if (main.at == AnalyzerTypeEnum.WRITEPRINTS_ANALYZER) {
				Logger.logln("Applying analyzer feature-extraction pre-processing procedures...");
				content += getTimestamp() + "Applying analyzer feature-extraction pre-processing procedures...\n";
				
				// move all test documents to be training documents
				trainingDocs.addAll(testDocs);
				testDocs = new ArrayList<Document>();
				
				content += getTimestamp() + "done!\n\n";
			}
			
			// training set
			Logger.logln("Extracting features from training corpus...");
			
			main.wib.setSparse(main.analysisSparseInstancesJCheckBox.isSelected());
			
			content += getTimestamp()+" Extracting features from training corpus ("+(main.wib.isSparse() ? "" : "not ")+"using sparse representation)...\n";
			updateResultsView();
			
			try {
				main.wib.prepareTrainingSet(
						trainingDocs,
						main.cfd);
			} catch (Exception e) {
				Logger.logln("Could not extract features from training corpus!",LogOut.STDERR);
				e.printStackTrace();
				
				JOptionPane.showMessageDialog(main,
						"Could not extract features from training corpus:\n"+e.getMessage()+"\n"+"Aborting analysis.",
						"Analysis Error",
						JOptionPane.ERROR_MESSAGE);
				updateBeforeStop();
				Thread.currentThread().stop();
			}
			
			content += getTimestamp()+" done!\n\n";
			if (main.analysisOutputFeatureVectorJCheckBox.isSelected()) {
				content +=
						"Training corpus features (ARFF):\n" +
						"================================\n" +
						main.wib.getTrainingSet().toString()+"\n\n";
				updateResultsView();
			}

			// test set
			if (main.analysisClassTestDocsJRadioButton.isSelected()) {
				Logger.logln("Extracting features from test documents...");
				
				content += getTimestamp()+" Extracting features from test documents ("+(main.wib.isSparse() ? "" : "not ")+"using sparse representation)...\n";
				updateResultsView();

				try {
					main.wib.prepareTestSet(
							testDocs);
				} catch (Exception e) {
					Logger.logln("Could not extract features from test documents!",LogOut.STDERR);
					e.printStackTrace();

					JOptionPane.showMessageDialog(main,
							"Could not extract features from test documents:\n"+e.getMessage()+"\n"+"Aborting analysis.",
							"Analysis Error",
							JOptionPane.ERROR_MESSAGE);
					updateBeforeStop();
					Thread.currentThread().stop();
				}

				content += getTimestamp()+" done!\n\n";
				if (main.analysisOutputFeatureVectorJCheckBox.isSelected()) {
					content +=
							"Test documents features (ARFF):\n" +
							"===============================\n" +
							main.wib.getTestSet().toString()+"\n\n";
					updateResultsView();
				}
			}

			// post processing
			if (main.at == AnalyzerTypeEnum.WRITEPRINTS_ANALYZER &&
					main.analysisClassTestDocsJRadioButton.isSelected()) {
				
				Logger.logln("Applying analyzer feature-extraction post-processing procedures...");
				content += getTimestamp() + "Applying analyzer feature-extraction post-processing procedures...\n";
				
				// put test instances back in the test set
				Instances trainingSet = main.wib.getTrainingSet();
				Instances testSet = new Instances(
						trainingSet,
						numTrainDocs,
						numTestDocs);
				main.wib.setTestSet(testSet);
				int total = numTrainDocs + numTestDocs;
				for (int i = total - 1; i >= numTrainDocs; i--)
					trainingSet.delete(i);
				
				content += getTimestamp() + "done!\n\n";
			}
			
			
			// running InfoGain
			// ================
			
			if (main.analysisCalcInfoGainJCheckBox.isSelected()) {
				
				content += "Calculating InfoGain on the training set's features\n";
				content += "===================================================\n";
				
				int igValue = -1;
				try {
					igValue = Integer.parseInt(main.infoGainValueJTextField.getText());
				} catch (NumberFormatException e) {}
				
				try{
					boolean apply = main.analysisCalcInfoGainJCheckBox.isSelected() && main.analysisApplyInfoGainJCheckBox.isSelected();
					content += main.wib.applyInfoGain(apply,igValue);
				} catch (Exception e) {
					content += "ERROR! Could not calculate InfoGain!\n";
					e.printStackTrace();
				}
				
				content += "done!\n\n";
				updateResultsView();
			}
			
			// running classification
			// ======================
			
			if (main.analysisClassTestDocsJRadioButton.isSelected()) {
				// Training and testing
				// ====================
				
				Logger.logln("Starting training and testing phase...");
				
				content += getTimestamp()+" Starting training and testing phase...\n";
				content += "\n================================================================================\n\n";
				
				Classifier c;
				Map<String,Map<String, Double>> results;
				int numClass = main.classifiers.size();
				for (int i=0; i<numClass; i++) {
					c = main.classifiers.get(i);
					content += "Running analysis with classifier "+(i+1)+" out of "+numClass+":\n" +
							"> Classifier: "+c.getClass().getName()+"\n" +
							"> Options:    "+ClassTabDriver.getOptionsStr(c.getOptions())+"\n\n";
					
					main.wad = new WekaAnalyzer(c);
					content += getTimestamp()+" Starting classification...\n";
					Logger.log("Starting classification...");
					updateResultsView();
					
					// classify
					results = main.wad.classify(
							main.wib.getTrainingSet(),
							main.wib.getTestSet(),
							main.ps.getTestDocs());
					content += getTimestamp()+" done!\n\n";
					Logger.logln("Done!");
					updateResultsView();
					
					// print out results
					content +=
							"Results:\n" +
							"========\n";
					
					content += main.wad.getLastStringResults();
					updateResultsView();
					
				}
				
			} else {
				// Running cross-validation on training corpus
				// ===========================================
				
				Logger.logln("Starting training 10-folds CV phase...");
				
				content += getTimestamp()+" Starting 10-folds cross-validation on training corpus phase...\n";
				content += "\n================================================================================\n\n";
				
				Classifier c;
				int numClass = main.classifiers.size();
				for (int i=0; i<numClass; i++) {
					c = main.classifiers.get(i);
					content += "Running analysis with classifier "+(i+1)+" out of "+numClass+":\n" +
							"> Classifier: "+c.getClass().getName()+"\n" +
							"> Options:    "+ClassTabDriver.getOptionsStr(c.getOptions())+"\n\n";
					
					main.wad = new WekaAnalyzer(c);
					content += getTimestamp()+" Starting cross validation...\n";
					Logger.log("Starting cross validation...");
					updateResultsView();
					
					// run
					Object results = main.wad.runCrossValidation(main.wib.getTrainingSet(),10,0);
					content += getTimestamp()+" done!\n\n";
					Logger.logln("Done!");
					updateResultsView();
					
					// print out results
					switch (main.at) {
					case WEKA_ANALYZER:
						Evaluation eval = (Evaluation) results;
						content += eval.toSummaryString(false)+"\n";
						try {
							content +=
									eval.toClassDetailsString()+"\n" +
									eval.toMatrixString()+"\n" ;
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case WRITEPRINTS_ANALYZER:
						String strResults = (String) results;
						content += strResults + "\n";
						break;
					}
					updateResultsView();
				}
				
			}
			
			// unlock gui and update results
			updateBeforeStop();
			main.results.add(content);

			Logger.logln(">>> Run Analysis thread finished.");
		}
		
		public void updateBeforeStop() {
			lockUnlock(main, false);
		}
		
		/**
		 * Updates the current results tab
		 */
		public void updateResultsView() {
			contentJTextArea.setText(content);
			contentJTextArea.setCaretPosition(content.length());
		}
	}
}
