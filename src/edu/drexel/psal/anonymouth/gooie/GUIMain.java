package edu.drexel.psal.anonymouth.gooie;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.jstylo.generics.*;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;
import edu.drexel.psal.anonymouth.gooie.Translation;
import edu.drexel.psal.anonymouth.gooie.DriverClustersTab.alignListRenderer;
import edu.drexel.psal.anonymouth.gooie.DriverPreProcessTabDocuments.ExtFilter;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.table.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.*;

import net.miginfocom.swing.MigLayout;

import com.jgaap.generics.Document;

import weka.classifiers.*;

import edu.drexel.psal.jstylo.analyzers.WekaAnalyzer;
import edu.stanford.nlp.util.PropertiesUtils;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * JStylo main GUI class.
 * 
 * @author Andrew W.E. McDonald
 */
//This is a comment from Joe Muoio to see if he can commit changes.
public class GUIMain extends javax.swing.JFrame  {
	
	private final String NAME = "( "+this.getClass().getSimpleName()+" ) - ";

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		/*InfoNodeLookAndFeel info = new InfoNodeLookAndFeel();
		try {
			UIManager.setLookAndFeel(info);
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


	// main instance
	public static GUIMain inst;
	protected JPanel mainPanel;

	// ------------------------

	// data
	protected ProblemSet ps;
	protected CumulativeFeatureDriver cfd;
	protected List<CumulativeFeatureDriver> presetCFDs;
	protected WekaInstancesBuilder wib;
	protected WekaAnalyzer wad;
	protected List<Classifier> classifiers;
	protected Thread analysisThread;
	protected List<String> results;
	
	protected PreProcessSettingsFrame PPSP;
	protected GeneralSettingsFrame GSP;

	protected String defaultTrainDocsTreeName = "Authors"; 
	protected Font defaultLabelFont = new Font("Verdana",0,16);
	protected static int cellPadding = 5;
	
	protected final Color ready = new Color(0,255,128);
	protected final Color notReady = new Color(255,102,102);
	protected final Color tan = new Color(252,242,206);

	// tabs
	protected JTabbedPane mainJTabbedPane;
	protected JPanel docsTab;
	protected JPanel featuresTab;
	protected JPanel classTab;
	protected JPanel editorTab;
	
	// documents tab
	
	
	protected JLabel testDocsJLabel;
	protected JButton trainDocPreviewJButton;
	protected JButton testDocPreviewJButton;
	protected JButton trainNameJButton;
	protected JButton newProblemSetJButton;
	protected JButton loadProblemSetJButton;
	protected JButton saveProblemSetJButton;
	protected JButton docTabNextJButton;
	protected JButton removeAuthorJButton;
	protected JButton removeTrainDocsJButton;
	protected JButton addTrainDocsJButton;
	
	protected JTable testDocsJTable;
	protected DefaultTableModel testDocsTableModel;
	protected JLabel featuresToolsJLabel;
	protected JLabel docPreviewNameJLabel;
	protected JLabel corpusJLabel;
	protected JButton removeTestDocJButton;
	protected JButton addAuthorJButton;
	protected JButton addTestDocJButton;
	protected JPanel testDocBottom;
	protected JButton clearDocPreviewJButton;
	protected JButton docsAboutJButton;
	protected JTable userSampleDocsJTable;
	protected DefaultTableModel userSampleDocsTableModel;
	protected JLabel userSampleDocsJLabel;
	protected JPanel buttons;
	protected JButton adduserSampleDocJButton;
	protected JButton removeuserSampleDocJButton;
	protected JButton userSampleDocPreviewJButton;

	// Classifiers tab
	protected JTextField classAvClassArgsJTextField;
	protected JLabel classAvClassArgsJLabel;
	protected JComboBox classClassJComboBox;
	protected JLabel classAvClassJLabel;
	protected JButton classAddJButton;
	
	protected JTextField classSelClassArgsJTextField;
	protected JLabel classSelClassArgsJLabel;
	protected JScrollPane classSelClassJScrollPane;
	protected DefaultComboBoxModel classSelClassJListModel;
	protected JScrollPane classTreeScrollPane;
	protected JScrollPane classDescJScrollPane;
	protected JTextPane classDescJTextPane;
	protected JLabel classDescJLabel;
	protected JButton classBackJButton;
	protected JButton classNextJButton;
	protected JLabel classSelClassJLabel;
	protected JButton classRemoveJButton;
	protected JButton classAboutJButton;
	
	protected JList classList;
	protected JComboBox classChoice;
	
	// Editor tab
	
	
	protected JScrollPane theEditorScrollPane;
	protected JTable suggestionTable;
	protected JPanel editorRowTwoButtonBufferPanel;
	protected JPanel buttonBufferJPanel;
	protected JPanel editorBottomRowButtonPanel;
	protected JPanel editorTopRowButtonsPanel;
	protected JPanel editorButtonJPanel;
	protected JPanel editorInteractionWestPanel;
	protected JPanel editorInteractionJPanel;
	protected JPanel jPanel2;
	protected JPanel dummyPanelUpdatorLeftSide;
	protected JPanel elementsToAddBoxLabelJPanel;
	protected JPanel suggestionBoxLabelJPanel;
	protected JPanel jPanel1;
	protected JPanel valueLabelJPanel;
	protected JPanel valueBoxPanel;
	protected JPanel updaterJPanel;
	//-------------- HELP TAB PANE STUFF ---------
	protected JTabbedPane leftTabPane;
	
	protected JPanel preProcessPanel;
	protected JButton prepAdvButton;
		protected JPanel prepDocumentsPanel;
			protected JPanel prepMainDocPanel;
				protected JLabel prepDocLabel;
				protected JLabel mainLabel;
				protected JList prepMainDocList;
				protected JButton clearProblemSetJButton;
				protected JScrollPane prepMainDocScrollPane;
			protected JPanel prepSampleDocsPanel;
				protected JLabel sampleLabel;
				protected JList prepSampleDocsList;
				protected JScrollPane prepSampleDocsScrollPane;
			protected JPanel prepTrainDocsPanel;
				protected JLabel trainLabel;
				protected JTree trainCorpusJTree;
				protected JScrollPane trainCorpusJTreeScrollPane;
		protected JPanel prepFeaturesPanel;
			protected JLabel prepFeatLabel;
			protected JComboBox featuresSetJComboBox;
			protected DefaultComboBoxModel featuresSetJComboBoxModel;
		protected JPanel prepClassifiersPanel;
			protected JLabel prepClassLabel;
			protected JPanel prepAvailableClassPanel;
				protected JTree classJTree;
				protected JTextPane classTextPane;
				protected JComboBox<String> classComboBox;
				protected JScrollPane prepAvailableClassScrollPane;
			protected JPanel prepSelectedClassPanel;
				protected JList classJList;
				protected JScrollPane prepSelectedClassScrollPane;
	
	protected JPanel suggestionsPanel;
		protected JPanel elementsPanel;
		protected JPanel elementsToAddPanel;
		protected JLabel elementsToAddLabel;
		protected JTextPane elementsToAddPane;
		protected JScrollPane elementsToAddScrollPane;
		protected JPanel elementsToRemovePanel;
		protected JLabel elementsToRemoveLabel;
		protected JTextPane elementsToRemovePane;
		protected JScrollPane elementsToRemoveScrollPane;
		
	protected JPanel translationsPanel;
		protected JLabel translationsLabel;
		protected ScrollablePanel translationsHolderPanel;
		protected JScrollPane translationsScrollPane;
		protected JPanel progressPanel;
		protected JLabel translationsProgressLabel;
		protected JProgressBar translationsProgressBar;
	
	protected JPanel informationPanel;
		protected JLabel sentenceEditorLabel;
		protected JLabel documentViewerLabel;
		protected JLabel classificationResultsLabel;
		protected JTextPane descriptionPane;
		
		protected JPanel instructionsPanel;
		protected JLabel instructionsLabel;
		protected JTextPane instructionsPane;
		protected JScrollPane instructionsScrollPane;
		protected JPanel synonymsPanel;
		protected JLabel synonymsLabel;
		protected JTextPane synonymsPane;
		protected JScrollPane synonymsScrollPane;
	//--------------------------------------------
		
	//--------------- Editor Tab Pane stuff ----------------------
		protected JTabbedPane topTabPane;
		protected JPanel documentsPanel;
		protected JPanel sentenceAndDocumentPanel;
		protected JPanel sentenceLabelPanel;
		
		protected JPanel sentenceEditingPanel;
		protected JPanel documentPanel;
		//protected JPanel documentOptionsPanel;
//		protected JLabel docNameLabel;
		
//		protected JScrollPane sentencePane;
//		protected JPanel sentenceOptionsPanel;
//		protected JPanel translationOptionsPanel;
		protected JButton removeWordsButton;
		protected JButton shuffleButton;
		protected JButton SaveChangesButton;
		protected JButton copyToSentenceButton;
		private JPanel spacer1;
		protected JButton restoreSentenceButton;
		protected JLabel documentLabel;
		protected JTextPane documentPane;
		protected JScrollPane documentScrollPane;
//		public JTextPane sentenceEditPane; //============================================ PUBLIC
//		protected JLabel sentenceBoxLabel;
//		protected JPanel sentencePanel;
		protected JPanel sentenceAndSentenceLabelPanel;
		protected JLabel translationsBoxLabel;
		protected JScrollPane translationPane;
		protected JTextPane translationEditPane;
		
		private boolean tabMade = false;
		protected int resultsMaxIndex;
		protected String chosenAuthor;
		
//		protected JButton dictButton;
//		protected JButton appendSentenceButton;
//		protected JButton saveButton;
		protected JButton processButton;
//		protected JButton nextSentenceButton;
//		protected JButton prevSentenceButton;
//		protected JButton transButton;
	//---------------------------------------------------------------------
		protected JTabbedPane bottomTabPane;
//		protected JPanel resultsPanel;
		protected JPanel resultsOptionsPanel;
		protected JPanel resultsMainPanel;
		protected DefaultComboBoxModel displayComboBoxModel;
		protected JComboBox displayComboBox;
		protected JTextArea displayTextArea;
		//protected JLabel classificationLabel;
		protected JPanel resultsBoxPanel_InnerBottomPanel;
		protected JTable resultsTable;
		protected DefaultTableModel resultsTableModel;
		protected JScrollPane resultsTablePane;
		protected JPanel resultsBoxPanel;
		protected JLabel resultsTableLabel;
		protected JPanel resultsTableLabelPanel;
		protected JPanel resultsBoxAndResultsLabelPanel;
	//---------------------------------------------------------------------
		
		protected JTabbedPane rightTabPane;
		protected JPanel anonymityPanel;
		protected JLabel anonymityLabel;
		protected AnonymityDrawingPanel anonymityDrawingPanel;
		protected JTextPane anonymityDescription;
		
	//--------------------------------------------------------------------
		
		protected JPanel clustersPanel;
		protected JLabel clustersLabel;
		protected JPanel featuresPanel;
		protected JLabel legendLabel;
		protected JPanel legendPanel;
		private String oldEditorBoxDoc = " ";
		private TableModel oldResultsTableModel = null;
		private TableCellRenderer tcr = new DefaultTableCellRenderer();
		
		protected JScrollPane featuresListScrollPane;
		protected JList featuresList;
		protected DefaultListModel featuresListModel;
		protected JScrollPane subFeaturesListScrollPane;
		protected JList subFeaturesList;
		protected DefaultListModel subFeaturesListModel;
		protected JScrollPane clusterScrollPane;
		protected ScrollablePanel clusterHolderPanel;
		protected JPanel topPanel;
		protected JButton reClusterAllButton;
		protected JButton refreshButton;
		protected JButton selectClusterConfiguration;
		protected JPanel secondPanel;
		
	//----------------------------------------------------------------------
	
	protected JPanel editorInfoJPanel;
	protected JScrollPane editorInteractionScrollPane;
	protected JScrollPane EditorInfoScrollPane;
	protected JTabbedPane editTP;
	
	protected JScrollPane wordsToAddPane;
	protected JTextField searchInputBox;
	protected JComboBox highlightSelectionBox;
	protected JLabel highlightLabel;
	protected JPanel jPanel_IL3;
	protected JButton clearHighlightingButton;
	protected JLabel featureNameLabel;
	protected JLabel targetValueLabel;
	protected JLabel presentValueLabel;
	protected JTextField targetValueField;
	protected JTextField presentValueField;
	protected JLabel suggestionListLabel;
	protected JButton verboseButton;
	protected JScrollPane suggestionListPane;
	
	// Analysis tab
	protected JCheckBox analysisOutputAccByClassJCheckBox;
	protected JCheckBox analysisOutputConfusionMatrixJCheckBox;
	protected ButtonGroup analysisTypeButtonGroup;
	
	protected static ImageIcon iconNO;
	protected static ImageIcon iconFINISHED;
	public static ImageIcon icon;
	
	protected JMenuBar menuBar;
	protected JMenuItem settingsGeneralMenuItem;
	
	// random useful variables
	protected static Border rlborder = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder());
	protected static Font titleFont = new Font("Ariel", Font.BOLD, 12);
	protected static String titleHeight = "25";
	
	// used for translation of sentences
	protected static Translator GUITranslator;
	
	// not yet used, may be used to minimize the document, features, or classifiers part of the preprocess panel
	protected boolean docPPIsShowing = true;
	protected boolean featPPIsShowing = true;
	protected boolean classPPIsShowing = true;
	
	//used mostly for loading the main document without having to alter the main.ps.testDocAt(0) directly
	Document mainDocPreview;
	protected ArrayList<String> features = new ArrayList<String>();
	protected ArrayList<ArrayList<String>> subfeatures = new ArrayList<ArrayList<String>>();
	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void startGooie() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Logger.initLogFile();
				try {
					icon = new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_LOGO.png"),"logo");
					iconNO = new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_NO.png"), "my 'no' icon");
					iconFINISHED = new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_FINISHED.png"), "my 'finished' icon");
					//javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.err.println("Look-and-Feel error!");
				}
				inst = new GUIMain();
				GUITranslator = new Translator(inst);
				inst.setDefaultCloseOperation(EXIT_ON_CLOSE);
			
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public GUIMain() {
		super();
		initData();
		initGUI();
	}

	private void initData() {
		ProblemSet.setDummyAuthor("~* you *~");
		ps = new ProblemSet();
		ps.setTrainCorpusName(defaultTrainDocsTreeName);
		cfd = new CumulativeFeatureDriver();
		DriverPreProcessTabFeatures.initPresetCFDs(this);
		FeatureWizardDriver.populateAll();
		classifiers = new ArrayList<Classifier>();
		wib = new WekaInstancesBuilder(true);
		results = new ArrayList<String>();
		
		// properties file -----------------------------------
		BufferedReader propReader = null;
		
		if (!PropertiesUtil.propFile.exists())
		{
			try {PropertiesUtil.propFile.createNewFile();} 
			catch (IOException e1) {e1.printStackTrace();}
		}
		
		try {propReader = new BufferedReader (new FileReader(PropertiesUtil.propFileName));} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		try {PropertiesUtil.prop.load(propReader);}
		catch (IOException e) {e.printStackTrace();}
	}

	private void initGUI() {
		try 
		{
			setExtendedState(MAXIMIZED_BOTH);
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setSize(new Dimension((int)(screensize.width*.75), (int)(screensize.height*.75)));
			this.setTitle("Anonymouth");
			this.setIconImage(new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_LOGO.png")).getImage());
			
			menuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			JMenu settingsMenu = new JMenu("Settings");
			JMenu helpMenu = new JMenu("Help");
			JMenu settingsTabMenu = new JMenu("Tabs");
			settingsGeneralMenuItem = new JMenuItem("General...");
			JMenu settingsTabClustersMenu = new JMenu("Clusters");
			JMenu settingsTabPreprocessMenu = new JMenu("Pre-Process");
			JMenu settingsTabSuggestionsMenu = new JMenu("Suggestions");
			JMenu settingsTabTranslationsMenu = new JMenu("Translations");
			JMenu settingsTabDocumentsMenu = new JMenu("Documents");
			JMenu settingsTabResultsMenu = new JMenu("Results");
			JMenuItem filePrintMenuItem = new JMenuItem("Print...");
			JMenuItem helpAboutMenuItem = new JMenuItem("About Anonymouth");
			
			menuBar.add(fileMenu);
			menuBar.add(settingsMenu);
			menuBar.add(helpMenu);
			
			fileMenu.add(filePrintMenuItem);
			
			// ================== HAVE TO ADD ACTION LISTENERS TO THESE BUT NEED TO FIGURE OUT BEST WAY TO DO SO
			
			settingsMenu.add(settingsGeneralMenuItem);
			settingsMenu.add(settingsTabMenu);
				settingsTabMenu.add(settingsTabClustersMenu);
					settingsTabClustersMenu.add(new JMenuItem("Left"));
					settingsTabClustersMenu.add(new JMenuItem("Top"));
					settingsTabClustersMenu.add(new JMenuItem("Right"));
				settingsTabMenu.add(settingsTabPreprocessMenu);
					settingsTabPreprocessMenu.add(new JMenuItem("Left"));
					settingsTabPreprocessMenu.add(new JMenuItem("Right"));
				settingsTabMenu.add(settingsTabSuggestionsMenu);
					settingsTabSuggestionsMenu.add(new JMenuItem("Left"));
					settingsTabSuggestionsMenu.add(new JMenuItem("Right"));
				settingsTabMenu.add(settingsTabTranslationsMenu);
					settingsTabTranslationsMenu.add(new JMenuItem("Left"));
					settingsTabTranslationsMenu.add(new JMenuItem("Right"));
				settingsTabMenu.add(settingsTabDocumentsMenu);
					settingsTabDocumentsMenu.add(new JMenuItem("Top"));
				settingsTabMenu.add(settingsTabResultsMenu);
					settingsTabResultsMenu.add(new JMenuItem("Bottom"));
			
			helpMenu.add(helpAboutMenuItem);
			
			this.setJMenuBar(menuBar);
			
			// ----- create all the tabs based on tab location (for some)
			// ----- must be done first so the lists and tables below refer to a location (not null)
			leftTabPane = new JTabbedPane();
			topTabPane = new JTabbedPane();
			rightTabPane = new JTabbedPane();
			bottomTabPane = new JTabbedPane();
			createPPTab();
			createSugTab();
			createTransTab();
			createDocumentTab();
			createAnonymityTab();
			createClustersTab();
//			createResultsTab();
			
			setUpContentPane();
			
			// final property settings
			
			DriverDocumentsTab.setAllDocTabUseable(false, this);
			
			// init all settings panes
			
			PPSP = new PreProcessSettingsFrame(this);
			GSP = new GeneralSettingsFrame(this);
			
			//init default values
			setDefaultValues();
			
			// initialize listeners - except for EditorTabDriver!
			
			DriverMenu.initListeners(this);
			DriverClustersTab.initListeners(this);
			DriverDocumentsTab.initListeners(this);
			DriverPreProcessTab.initListeners(this);
			DriverResultsTab.initListeners(this);
			DriverSuggestionsTab.initListeners(this);
			DriverTranslationsTab.initListeners(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setDefaultValues() throws Exception {
		featuresSetJComboBox.setSelectedItem("WritePrints (Limited)");
		PPSP.featuresSetJComboBox.setSelectedItem("WritePrints (Limited)");
		cfd = presetCFDs.get(featuresSetJComboBox.getSelectedIndex());
		GUIUpdateInterface.updateFeatureSetView(this);
		GUIUpdateInterface.updateFeatPrepColor(this);
		
		classChoice.setSelectedItem("SMO");
		DriverPreProcessTabClassifiers.tmpClassifier = Classifier.forName(DriverPreProcessTabClassifiers.fullClassPath.get(classChoice.getSelectedItem().toString()), null);
		DriverPreProcessTabClassifiers.tmpClassifier.setOptions(DriverPreProcessTabClassifiers.getOptionsStr(DriverPreProcessTabClassifiers.tmpClassifier.getOptions()).split(" "));
		classifiers.add(DriverPreProcessTabClassifiers.tmpClassifier);
		PPSP.classSelClassArgsJTextField.setText(DriverPreProcessTabClassifiers.getOptionsStr(classifiers.get(0).getOptions()));
		PPSP.classDescJTextPane.setText(DriverPreProcessTabClassifiers.getDesc(classifiers.get(0)));
		GUIUpdateInterface.updateClassList(this);
		GUIUpdateInterface.updateClassPrepColor(this);
		DriverPreProcessTabClassifiers.tmpClassifier = null;

//		PropUtil.load.addChoosableFileFilter(new ExtFilter("XML files (*.xml)", "xml"));
//		if (PropUtil.prop.getProperty("recentProbSet") == null) {
//			PropUtil.setRecentProbSet();
//		} else {
//			String absPath = PropUtil.propFile.getAbsolutePath();
//			String problemSetDir = absPath.substring(0, absPath.indexOf("anonymouth_prop")-1) + "\\problem_sets\\";
//			PropUtil.load.setCurrentDirectory(new File(problemSetDir));
//			PropUtil.load.setSelectedFile(new File(PropUtil.prop.getProperty("recentProbSet")));
//		}
//		
//		int answer = 0;
//		answer = PropUtil.load.showDialog(this, "Load Problem Set");
//
//		if (answer == JFileChooser.APPROVE_OPTION) {
//			String path = PropUtil.load.getSelectedFile().getAbsolutePath();
//			String filename = PropUtil.load.getSelectedFile().getName();
//			//path = path.substring(path.indexOf("jsan_resources"));
//
//			PropUtil.setRecentProbSet(filename);
//
//			Logger.logln(NAME+"Trying to load problem set from "+path);
//			try {
//				ps = new ProblemSet(path);
//				GUIUpdateInterface.updateProblemSet(this);
//			} catch (Exception exc) {
//				Logger.logln(NAME+"Failed loading "+path, LogOut.STDERR);
//				Logger.logln(NAME+exc.toString(),LogOut.STDERR);
//				JOptionPane.showMessageDialog(null,
//						"Failed loading problem set from:\n"+path,
//						"Load Problem Set Failure",
//						JOptionPane.ERROR_MESSAGE);
//			}
//
//		} else {
//			Logger.logln(NAME+"Load problem set canceled");
//		}
	}

	/**
	 * Adds everything to the content pane.
	 * @throws Exception 
	 */
	protected void setUpContentPane() throws Exception
	{
		getContentPane().removeAll();
		
		// ------- initialize PARALLEL arrays for the panels, their names, and their locations
		ArrayList<String> panelNames = new ArrayList<String>();
		panelNames.add("Pre-Process");
		panelNames.add("Suggestions");
		panelNames.add("Translations");
		panelNames.add("Document");
		panelNames.add("Anonymity");
		panelNames.add("Clusters");
		panelNames.add("Results");
		
		HashMap<String, JPanel> panels = new HashMap<String, JPanel>(6);
		panels.put("Pre-Process", preProcessPanel);
		panels.put("Suggestions", suggestionsPanel);
		panels.put("Translations", translationsPanel);
		panels.put("Document", documentsPanel);
		panels.put("Anonymity", anonymityPanel);
		panels.put("Clusters", clustersPanel);
//		panels.put("Results", resultsPanel);
		
		ArrayList<PropertiesUtil.Location> panelLocations = new ArrayList<PropertiesUtil.Location>();
		panelLocations.add(PropertiesUtil.getPreProcessTabLocation());
		panelLocations.add(PropertiesUtil.getSuggestionsTabLocation());
		panelLocations.add(PropertiesUtil.getTranslationsTabLocation());
		panelLocations.add(PropertiesUtil.getDocumentsTabLocation());
		panelLocations.add(PropertiesUtil.getAnonymityTabLocation());
		panelLocations.add(PropertiesUtil.getClustersTabLocation());
		panelLocations.add(PropertiesUtil.getResultsTabLocation());
		
		// ----- form the column specifications
		String columnString = "";
		int columnNumber = 0;
		if (panelLocations.contains(PropertiesUtil.Location.LEFT))
		{
			columnString = columnString.concat("[]");
			columnNumber++;
		}
		if (panelLocations.contains(PropertiesUtil.Location.TOP) || panelLocations.contains(PropertiesUtil.Location.BOTTOM))
		{
			columnString = columnString.concat("[grow, growprio 110, fill]");
			columnNumber++;
		}
		if (panelLocations.contains(PropertiesUtil.Location.RIGHT))
		{
			columnString = columnString.concat("[]");
			columnNumber++;
		}
		
		// ----- form the row specifications
		String rowString = "";
		if (panelLocations.contains(PropertiesUtil.Location.TOP))
		{
			rowString = rowString.concat("[grow, fill]");
		}
		if (panelLocations.contains(PropertiesUtil.Location.BOTTOM))
		{
			rowString = rowString.concat("[150:25%:]");
		}
		
		// ------ set the content pane layout based on the tab locations
		getContentPane().setLayout(new MigLayout(
				"wrap " + columnNumber + ", gap 10 10", // layout constraints
				columnString, // column constraints
				rowString)); // row constraints)
		
		//------ fix all the layouts you need to
		fixLayouts();
		
		// ------ add all tabs to their correct tab panes
		for (int i = 0; i < panels.size(); i++)
		{
			if (panelLocations.get(i) == PropertiesUtil.Location.LEFT)
				leftTabPane.add(panelNames.get(i), panels.get(panelNames.get(i)));
			else if (panelLocations.get(i) == PropertiesUtil.Location.TOP)
				topTabPane.add(panelNames.get(i), panels.get(panelNames.get(i)));
			else if (panelLocations.get(i) == PropertiesUtil.Location.RIGHT)
				rightTabPane.add(panelNames.get(i), panels.get(panelNames.get(i)));
			else if (panelLocations.get(i) == PropertiesUtil.Location.BOTTOM)
				bottomTabPane.add(panelNames.get(i), panels.get(panelNames.get(i)));
			else
				throw new Exception();
		}
		
		// ------ add all tab panes, if they need to be added
		if (panelLocations.contains(PropertiesUtil.Location.LEFT))
			getContentPane().add(leftTabPane, "width 250!, spany");
		if (panelLocations.contains(PropertiesUtil.Location.TOP))
			getContentPane().add(topTabPane, "width 600:100%:, grow");
		if (panelLocations.contains(PropertiesUtil.Location.RIGHT))
			getContentPane().add(rightTabPane, "width ::353, spany"); // MUST be at LEAST 353 for Mac OS X. 
		if (panelLocations.contains(PropertiesUtil.Location.BOTTOM))
			getContentPane().add(bottomTabPane, "width 600:100%:, height 150:25%:");
		
		getContentPane().revalidate();
		getContentPane().repaint();
	}
	
	/**
	 * Adjusts a tabs layout based on its location property. If this is not done, the tab will be arranged for the wrong location.
	 */
	public void fixLayouts()
	{
		PropertiesUtil.Location clustersLocation = PropertiesUtil.getClustersTabLocation();
		if (clustersLocation == PropertiesUtil.Location.LEFT || clustersLocation == PropertiesUtil.Location.RIGHT)
		{
			clustersPanel.setLayout(new MigLayout(
					"wrap, ins 0, gap 0 0",
					"grow, fill",
					"[][grow, fill][]"));
			
			clustersPanel.removeAll();
			clustersPanel.add(clustersLabel);
			clustersPanel.add(clusterScrollPane);
			clustersPanel.add(featuresPanel, "h 250!");
		}
		else if (clustersLocation == PropertiesUtil.Location.TOP)
		{
			clustersPanel.setLayout(new MigLayout(
					"wrap 2, fill, ins 0, gap 0",
					"[70%][30%]",
					"[][][grow, fill]"));
			
			clustersPanel.removeAll();
			clustersPanel.add(clustersLabel, "grow, h " + titleHeight + "!");
			clustersPanel.add(legendLabel, "grow, h " + titleHeight + "!");
			clustersPanel.add(clusterScrollPane, "grow, spany");
			clustersPanel.add(legendPanel, "grow");
			clustersPanel.add(featuresPanel, "spany, grow");
		}
		
		PropertiesUtil.Location anonymityLocation = PropertiesUtil.getAnonymityTabLocation();
		if (anonymityLocation == PropertiesUtil.Location.LEFT || anonymityLocation == PropertiesUtil.Location.RIGHT)
		{
			anonymityPanel.setLayout(new MigLayout(
					"wrap, ins 0, gap 0 0",
					"grow, fill",
					"[][grow, fill][]"));
			
			anonymityPanel.removeAll();
			anonymityPanel.add(anonymityLabel, "spanx, grow, h " + titleHeight + "!");
			anonymityPanel.add(anonymityDrawingPanel, "h 515!");
			anonymityPanel.add(anonymityDescription, "h 70!");
			anonymityPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
			anonymityPanel.add(resultsMainPanel, "grow");
		}
		else if (anonymityLocation == PropertiesUtil.Location.TOP)
		{
			anonymityPanel.setLayout(new MigLayout(
					"wrap 2, fill, ins 0, gap 0",
					"[70%][30%]",
					"[][][grow, fill]"));
			
			anonymityPanel.removeAll();
			anonymityPanel.add(anonymityLabel, "spanx, grow, h " + titleHeight + "!");
			anonymityPanel.add(anonymityDrawingPanel, "h 515!");
			anonymityPanel.add(anonymityDescription, "h 70!");
			anonymityPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
			anonymityPanel.add(resultsMainPanel, "grow");
		}
		
//		PropUtil.Location resultsLocation = PropUtil.getResultsTabLocation();
//		if (resultsLocation == PropUtil.Location.LEFT || resultsLocation == PropUtil.Location.RIGHT)
//		{
//			resultsPanel.setLayout(new MigLayout(
//					"wrap, ins 0, gap 0 0",
//					"grow, fill",
//					"[][grow, fill][]"));
//			
//			resultsPanel.removeAll();
//			resultsPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
//			resultsPanel.add(resultsMainPanel, "grow");
//			resultsPanel.add(new JScrollPane(displayTextArea), "h 150!");
//		}
//		else if (resultsLocation == PropUtil.Location.BOTTOM)
//		{
//			resultsPanel.setLayout(new MigLayout(
//					"wrap 2, ins 0, gap 0 0",
//					"[100:20%:][grow, fill]",
//					"[][grow, fill]"));
//			
//			resultsPanel.removeAll();
//			resultsPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
//			resultsPanel.add(new JScrollPane(displayTextArea), "grow");
//			resultsPanel.add(resultsMainPanel, "grow");
//		}
	}
	
	public boolean documentsAreReady()
	{
		boolean ready = true;
		try {
			if (!mainDocReady())
				ready = false;
			if (!sampleDocsReady())
				ready = false;
			if (!trainDocsReady())
				ready = false;
		}
		catch (Exception e){
			return false;
		}
		
		return ready;
	}
	
	public boolean mainDocReady()
	{
		if (inst.ps.hasTestDocs())
			return true;
		else
			return false;
	}
	
	public boolean sampleDocsReady()
	{
		try
		{
			if (!inst.ps.getTrainDocs(ProblemSet.getDummyAuthor()).isEmpty())
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean trainDocsReady()
	{
		try
		{
			boolean result = true;
			ProblemSet ps = inst.ps;
			if (ps.getAuthors().size() == 0)
				result = false;
			else {
				for (int i = 0; i < ps.getAuthors().size(); i++)
				{
					String author = (String)inst.ps.getAuthors().toArray()[i];
					Set<String> authors = inst.ps.getAuthors();
					for (String curAuthor : authors) {
						if (inst.ps.getTrainDocs(curAuthor).isEmpty()) {
							result = false;
							break;
						}
					}
					if (!author.equals(ProblemSet.getDummyAuthor())) {
						if (ps.numTrainDocs(author) < 1) {
							result = false;
							break;
						}
					} else if (ps.getAuthors().size() == 1) {
						result = false;
						break;
					}
				}
			}
			
			return result;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean featuresAreReady()
	{
		boolean ready = true;
		
		try {
			if (cfd.numOfFeatureDrivers() == 0)
				ready = false;
		}
		catch (Exception e){
			return false;
		}
		
		return ready;
	}
	
	public boolean classifiersAreReady()
	{
		boolean ready = true;
		
		try {
			if (classifiers.isEmpty())
				ready = false;
		}
		catch (Exception e){
			return false;
		}
		
		return ready;
	}
	
	public void addClusterFeatures (String[] names)
	{
		Arrays.sort(names);
		// add the holder at top
		subfeatures.add(new ArrayList<String>());
		for (int i = 0; i < names.length; i++)
		{
			String feature = null;
			String subfeature = null;
			
			// get the feature and subfeature from the name
			if (names[i].contains("--"))
			{
				feature = names[i].substring(0, names[i].indexOf("--"));
				subfeature = names[i].substring(names[i].indexOf("--")+2, names[i].length());
			}
			else
				feature = names[i];
			
			// if the feature doesnt exist yet, add it to the feature list
			if (!features.contains(feature))
			{
				features.add(feature);
				subfeatures.add(new ArrayList<String>());
				if (subfeature != null)
					subfeatures.get(features.indexOf(feature)).add(subfeature);
					
			}
			else // if the feature does exist, add its subfeature to the subfeature list
			{
				if (subfeature != null)
					subfeatures.get(features.indexOf(feature)).add(subfeature);
			}
		}
		featuresListModel = new DefaultListModel();
		for (int i = 0; i < features.size(); i++)
			featuresListModel.addElement(features.get(i));
		featuresList.setModel(featuresListModel);
	}
	
	/**
	 * Creates a Pre-Process panel that can be added to the "help area".
	 * @return editorHelpSettingsPanel
	 */
	protected void createPPTab()
	{
		preProcessPanel = new JPanel();
		//editorHelpPrepPanel.setMaximumSize(editorHelpPrepPanel.getPreferredSize());
		MigLayout settingsLayout = new MigLayout(
				"fill, wrap 1, ins 0",
				"fill, grow",
				"fill, grow");
		preProcessPanel.setLayout(settingsLayout);
		prepDocumentsPanel = new JPanel();
		MigLayout documentsLayout = new MigLayout(
				"fill, wrap 4, ins 0, gap 0 5",
				"grow 25, fill, center");
		prepDocumentsPanel.setLayout(documentsLayout);
		//prepDocumentsPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
		{
			// Advanced Button
			prepAdvButton = new JButton("Advanced");
			
			// Documents Label
			prepDocLabel = new JLabel("Documents:");
			prepDocLabel.setFont(titleFont);
			prepDocLabel.setHorizontalAlignment(SwingConstants.CENTER);
			prepDocLabel.setBorder(rlborder);
			prepDocLabel.setOpaque(true);
			prepDocLabel.setBackground(notReady);
			
			// Save Problem Set button
			saveProblemSetJButton = new JButton("Save");
			
			// load problem set button
			loadProblemSetJButton = new JButton("Load");
			
			// Save Problem Set button
			clearProblemSetJButton = new JButton("Clear");
			
			// main label
			mainLabel = new JLabel("Main:");
			mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			// sample label
			sampleLabel = new JLabel("Sample:");
			sampleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			// main documents list
			DefaultListModel mainDocListModel = new DefaultListModel();
			prepMainDocList = new JList(mainDocListModel);
			prepMainDocList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			prepMainDocList.setCellRenderer(new DriverClustersTab.alignListRenderer(SwingConstants.CENTER));
			prepMainDocScrollPane = new JScrollPane(prepMainDocList);
			
			// sample documents list
			DefaultListModel sampleDocsListModel = new DefaultListModel();
			prepSampleDocsList = new JList(sampleDocsListModel);
			prepSampleDocsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			prepSampleDocsList.setCellRenderer(new DriverClustersTab.alignListRenderer(SwingConstants.CENTER));
			prepSampleDocsScrollPane = new JScrollPane(prepSampleDocsList);
			
			// main add button
			addTestDocJButton = new JButton("+");
			
			// main delete button
			removeTestDocJButton = new JButton("-");
			
			// sample add button
			adduserSampleDocJButton = new JButton("+");
			
			// sample delete button
			removeuserSampleDocJButton = new JButton("-");
			
			// train label
			trainLabel = new JLabel("Other Authors:");
			trainLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			// train tree
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(ps.getTrainCorpusName(), true);
			trainCorpusJTree = new JTree(top, true);
			trainCorpusJTreeScrollPane = new JScrollPane(trainCorpusJTree);
			
			// train add button
			addTrainDocsJButton = new JButton("+");
			
			// train delete button
			removeTrainDocsJButton = new JButton("-");
			
//			testDocBottom = new JPanel(new FlowLayout());
//			prepMainDocList.setPreferredSize(new Dimension(100, 15));
//			prepMainDocList.setMaximumSize(new Dimension(100, 15));
//			testDocBottom.add(prepMainDocList);
//			testDocBottom.add(prepSampleDocsScrollPane);
//			testDocBottom.add(addTestDocJButton);
//			testDocBottom.add(removeTestDocJButton);
			
			prepDocumentsPanel.add(prepDocLabel, "span, h " + titleHeight + "!");
			prepDocumentsPanel.add(saveProblemSetJButton, "span 4, split 3");
			prepDocumentsPanel.add(loadProblemSetJButton);
			prepDocumentsPanel.add(clearProblemSetJButton);
			prepDocumentsPanel.add(mainLabel, "span 2");
			prepDocumentsPanel.add(sampleLabel, "span 2");
			prepDocumentsPanel.add(prepMainDocScrollPane, "span 2, growy, h 60::180, w 0::150");
			
//			prepDocumentsPanel.add(prepMainDocList, "span 2, top, growy, h 60::20, w 0::160");
			prepDocumentsPanel.add(prepSampleDocsScrollPane, "span 2, growy, h 60::180, w 0::150");
			prepDocumentsPanel.add(addTestDocJButton);
			prepDocumentsPanel.add(removeTestDocJButton);
			
//			prepDocumentsPanel.add(testDocBottom, "span 2, growy, h 60::180");
//			prepDocumentsPanel.add(prepSampleDocsScrollPane, "span 2, growy, h 60::180");
//			prepDocumentsPanel.add(adduserSampleDocJButton, "skip 2");
			prepDocumentsPanel.add(adduserSampleDocJButton);
			prepDocumentsPanel.add(removeuserSampleDocJButton);
			prepDocumentsPanel.add(trainLabel, "span");
			prepDocumentsPanel.add(trainCorpusJTreeScrollPane, "span, growy, h 120::");
			prepDocumentsPanel.add(addTrainDocsJButton, "span 2");
			prepDocumentsPanel.add(removeTrainDocsJButton, "span 2");
		}
		
		prepFeaturesPanel = new JPanel();
		MigLayout featuresLayout = new MigLayout(
				"fill, wrap 2, ins 0, gap 0 5",
				"fill");
		prepFeaturesPanel.setLayout(featuresLayout);
		{
			prepFeatLabel = new JLabel("Features:");
			prepFeatLabel.setOpaque(true);
			prepFeatLabel.setFont(titleFont);
			prepFeatLabel.setHorizontalAlignment(SwingConstants.CENTER);
			prepFeatLabel.setBorder(rlborder);
			prepFeatLabel.setBackground(notReady);
			
			JLabel label = new JLabel("Feature Set:");
			
			String[] presetCFDsNames = new String[presetCFDs.size()];
			for (int i=0; i<presetCFDs.size(); i++)
				presetCFDsNames[i] = presetCFDs.get(i).getName();
			
			featuresSetJComboBoxModel = new DefaultComboBoxModel(presetCFDsNames);
			featuresSetJComboBox = new JComboBox();
			featuresSetJComboBox.setModel(featuresSetJComboBoxModel);
			
			prepFeaturesPanel.add(prepFeatLabel, "span 2, h " + titleHeight + "!");
			prepFeaturesPanel.add(label);
			prepFeaturesPanel.add(featuresSetJComboBox);
		}
		
		prepClassifiersPanel = new JPanel();
		MigLayout classLayout = new MigLayout(
				"fill, wrap 2, ins 0, gap 0 5",
				"center, fill, grow",
				"grow, fill");
		prepClassifiersPanel.setLayout(classLayout);
		{
			prepClassLabel = new JLabel("Classifiers:");
			prepClassLabel.setOpaque(true);
			prepClassLabel.setFont(titleFont);
			prepClassLabel.setHorizontalAlignment(SwingConstants.CENTER);
			prepClassLabel.setBorder(rlborder);
			prepClassLabel.setBackground(notReady);
			
//			JLabel availLabel = new JLabel("Available:");
//			availLabel.setHorizontalAlignment(SwingConstants.CENTER);
//			
//			JLabel selectedLabel = new JLabel("Selected:");
//			selectedLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
//			classJTree = new JTree();
//			classJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//			prepAvailableClassScrollPane = new JScrollPane(classJTree);
//			DriverPreProcessTabClassifiers.initMainWekaClassifiersTree(this);
			
			classChoice = new JComboBox();
			
//			prepAvailableClassScrollPane = new JScrollPane(classChoice);
			DriverPreProcessTabClassifiers.initMainWekaClassifiersTree(this);
			
//			DefaultListModel selectedListModel = new DefaultListModel();
//			classJList = new JList(selectedListModel);
//			classJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//			classJList.setCellRenderer(new DriverClustersTab.alignListRenderer(SwingConstants.CENTER));
//			prepSelectedClassScrollPane = new JScrollPane(classJList);
			
//			classAddJButton = new JButton("Select");
//			
//			classRemoveJButton = new JButton("Remove");
			
			prepClassifiersPanel.add(prepClassLabel, "span 2, h " + titleHeight + "!");
//			prepClassifiersPanel.add(availLabel);
//			prepClassifiersPanel.add(selectedLabel);
			prepClassifiersPanel.add(classChoice);
//			prepClassifiersPanel.add(prepAvailableClassScrollPane, "grow, h 150:360:, w 50%::");
//			prepClassifiersPanel.add(prepSelectedClassScrollPane, "grow, h 150:360:");
//			prepClassifiersPanel.add(classAddJButton, "gaptop 0, growy 0");
//			prepClassifiersPanel.add(classRemoveJButton, "gaptop 0, growy 0");
		}
		preProcessPanel.add(prepDocumentsPanel, "growx");
		preProcessPanel.add(prepFeaturesPanel, "growx");
		preProcessPanel.add(prepClassifiersPanel, "growx");
	}
	
//	private JPanel createInfoTab()
//	{
//		//for word wrapping, generally width in style should be 100 less than width of component
//		String html1 = "<html><body style='width: ";
//        String html2 = "px'>";
//		
//		editorHelpInfoPanel = new JPanel();
//		editorHelpInfoPanel.setPreferredSize(new java.awt.Dimension(290, 660));
//		
//		GridBagLayout infoLayout = new GridBagLayout();
//		editorHelpInfoPanel.setLayout(infoLayout);
//		GridBagConstraints IPConst = new GridBagConstraints();
//		{ //=========== Information Tab ====================
//			//---------- Instructions Panel ----------------------
//			instructionsPanel = new JPanel();
//			instructionsPanel.setPreferredSize(new java.awt.Dimension(290, 660));
//			
//			GridBagLayout instructionsLayout = new GridBagLayout();
//			editorHelpInfoPanel.setLayout(instructionsLayout);
//			
//			IPConst.gridx = 0;
//			IPConst.gridy = 0;
//			IPConst.gridheight = 1;
//			IPConst.gridwidth = 1;
//			editorHelpInfoPanel.add(instructionsPanel, IPConst);
//			Font titleFont = titleFont;
//			Font answerFont = new Font("Ariel", Font.PLAIN, 11);
//			{// ---------- Question One ----------------------
//				JLabel questionOneTitle = new JLabel();
//				questionOneTitle.setText("What is this tab?");
//				questionOneTitle.setFont(titleFont);
//				questionOneTitle.setPreferredSize(new java.awt.Dimension(290, 20));
//				IPConst.gridx = 0;
//				IPConst.gridy = 0;
//				IPConst.gridheight = 1;
//				IPConst.gridwidth = 3;
//				instructionsPanel.add(questionOneTitle, IPConst);
//			}
//			{
//				JLabel questionOneAnswer = new JLabel();
//				String s = "This is the <b>\"Editor Tab.\"</b> Here is where you edit the document you wish to anonymize. The goal is to edit your document to a point where it is not recognized as your writing, and Anonymouth is here to help you acheive that.";
//				questionOneAnswer.setText(html1+"170"+html2+s);
//				questionOneAnswer.setFont(answerFont);
//				questionOneAnswer.setVerticalAlignment(SwingConstants.TOP);
//				
//				questionOneAnswer.setPreferredSize(new java.awt.Dimension(270, 60));
//				IPConst.gridx = 0;
//				IPConst.gridy = 1;
//				IPConst.gridheight = 1;
//				IPConst.gridwidth = 2;
//				instructionsPanel.add(questionOneAnswer, IPConst);
//			}
//			{// ---------- Question Two ----------------------
//				JLabel questionTwoTitle = new JLabel();
//				questionTwoTitle.setText("What should I do first?");
//				questionTwoTitle.setFont(titleFont);
//				questionTwoTitle.setPreferredSize(new java.awt.Dimension(290, 20));
//				IPConst.gridx = 0;
//				IPConst.gridy = 2;
//				IPConst.gridheight = 1;
//				IPConst.gridwidth = 3;
//				instructionsPanel.add(questionTwoTitle, IPConst);
//			}
//			{
//				JLabel questionTwoAnswer = new JLabel();
//				String s = "If you have not processed your document yet, do so now by pressing the <b>\"Process\"</b> button. This will let us figure out how anonymous your document currently is.";
//				questionTwoAnswer.setText(html1+"170"+html2+s);
//				questionTwoAnswer.setFont(answerFont);
//				questionTwoAnswer.setVerticalAlignment(SwingConstants.TOP);
//				
//				questionTwoAnswer.setPreferredSize(new java.awt.Dimension(270, 60));
//				IPConst.gridx = 0;
//				IPConst.gridy = 3;
//				IPConst.gridheight = 1;
//				IPConst.gridwidth = 2;
//				instructionsPanel.add(questionTwoAnswer, IPConst);
//			}
//			{// ---------- Question Three ----------------------
//				JLabel questionThreeTitle = new JLabel();
//				questionThreeTitle.setText("How do I go about editing my document?");
//				questionThreeTitle.setFont(titleFont);
//				questionThreeTitle.setPreferredSize(new java.awt.Dimension(290, 20));
//				IPConst.gridx = 0;
//				IPConst.gridy = 4;
//				IPConst.gridheight = 1;
//				IPConst.gridwidth = 3;
//				instructionsPanel.add(questionThreeTitle, IPConst);
//			}
//			{
//				JLabel questionThreeAnswer = new JLabel();
//				String s = "<p>After you've processed the document, the first sentence should be highlighted and will appear in the <b>\"Sentence\"</b> box."
//						+ " Edit each sentence one by one, saving your changes as you go. Use the arrow buttons for navigation.</p>"
//						+ "<br><p>Once you are satisfied with your changes, process the document to see how the anonymity has been affected.</p>";
//				questionThreeAnswer.setText(html1+"170"+html2+s);
//				questionThreeAnswer.setFont(answerFont);
//				questionThreeAnswer.setVerticalAlignment(SwingConstants.TOP);
//				
//				questionThreeAnswer.setPreferredSize(new java.awt.Dimension(270, 120));
//				IPConst.gridx = 0;
//				IPConst.gridy = 5;
//				IPConst.gridheight = 1;
//				IPConst.gridwidth = 2;
//				instructionsPanel.add(questionThreeAnswer, IPConst);
//			}
//			return editorHelpInfoPanel;
//		} // =========== End Information Tab ==================
//	}
	
	private JPanel createSugTab()
	{
		suggestionsPanel = new JPanel();
		MigLayout settingsLayout = new MigLayout(
				"fill, wrap 1, ins 0, gap 0 0",
				"grow, fill",
				"[][grow, fill][][grow, fill]");
		suggestionsPanel.setLayout(settingsLayout);
		{//================ Suggestions Tab =====================
			//--------- Elements to Add Label ------------------
			elementsToAddLabel = new JLabel("Elements To Add:");
			elementsToAddLabel.setHorizontalAlignment(SwingConstants.CENTER);
			elementsToAddLabel.setFont(titleFont);
			elementsToAddLabel.setOpaque(true);
			elementsToAddLabel.setBackground(tan);
			elementsToAddLabel.setBorder(rlborder);
			
			//--------- Elements to Add Text Pane ------------------
			elementsToAddPane = new JTextPane();
			elementsToAddScrollPane = new JScrollPane(elementsToAddPane);
			elementsToAddPane.setText("Process the document...");
			
			//--------- Elements to Remove Label  ------------------
			elementsToRemoveLabel = new JLabel("Elements To Remove:");
			elementsToRemoveLabel.setHorizontalAlignment(SwingConstants.CENTER);
			elementsToRemoveLabel.setFont(titleFont);
			elementsToRemoveLabel.setOpaque(true);
			elementsToRemoveLabel.setBackground(tan);
			elementsToRemoveLabel.setBorder(rlborder);
			
			//--------- Elements to Remove Text Pane ------------------
			elementsToRemovePane = new JTextPane();
			elementsToRemoveScrollPane = new JScrollPane(elementsToRemovePane);
			elementsToRemovePane.setText("Process the document...");
			
			suggestionsPanel.add(elementsToAddLabel, "h " + titleHeight + "!");
			suggestionsPanel.add(elementsToAddScrollPane);
			suggestionsPanel.add(elementsToRemoveLabel, "h " + titleHeight + "!");
			suggestionsPanel.add(elementsToRemoveScrollPane);
		}//============ End Suggestions Tab =================
	return suggestionsPanel;
	}
	
	private JPanel createTransTab()
	{
		translationsPanel = new JPanel();
		translationsPanel.setLayout(new MigLayout(
					"wrap, ins 0, gap 0 0",
					"grow, fill",
					"[][grow, fill][]"));
		{ // --------------cluster panel components
			translationsLabel = new JLabel("Translations:");
			translationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
			translationsLabel.setFont(titleFont);
			translationsLabel.setOpaque(true);
			translationsLabel.setBackground(tan);
			translationsLabel.setBorder(rlborder);
			
			translationsHolderPanel = new ScrollablePanel()
			{
				public boolean getScrollableTracksViewportWidth()
				{
					return true;
				}
			};
			translationsHolderPanel.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PIXELS, 74);
			translationsHolderPanel.setAutoscrolls(true);
			translationsHolderPanel.setOpaque(true);
			translationsHolderPanel.setLayout(new MigLayout(
					"wrap, ins 0, gap 0",
					"grow, fill",
					""));
			translationsScrollPane = new JScrollPane(translationsHolderPanel);
			translationsScrollPane.setOpaque(true);
			
			progressPanel = new JPanel();
			progressPanel.setLayout(new MigLayout(
					"wrap, fill, ins 0",
					"grow, fill",
					"[][][20]"));
			{
				JLabel translationsProgressTitleLabel = new JLabel("Progress:");
				translationsProgressTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
				translationsProgressTitleLabel.setFont(titleFont);
				translationsProgressTitleLabel.setOpaque(true);
				translationsProgressTitleLabel.setBackground(tan);
				translationsProgressTitleLabel.setBorder(rlborder);
				
				translationsProgressLabel = new JLabel("No Translations Pending.");
				translationsProgressLabel.setHorizontalAlignment(SwingConstants.CENTER);
				
				translationsProgressBar = new JProgressBar();
				
				progressPanel.add(translationsProgressTitleLabel, "grow, h 25!");
				progressPanel.add(translationsProgressLabel, "grow");
				progressPanel.add(translationsProgressBar, "grow");
			}
			
			translationsPanel.add(translationsLabel, "grow, h 25!");
			translationsPanel.add(translationsScrollPane, "grow");
			translationsPanel.add(progressPanel, "grow");
		}
		return translationsPanel;
	}
	
	private JPanel createDocumentTab()
	{
		Logger.logln(NAME+"Creating Documents Tab...");
		if(tabMade == false)
		{
			Font normalFont = new Font("Ariel", Font.PLAIN, 11);
			
			documentsPanel = new JPanel();
			MigLayout EBPLayout = new MigLayout(
					"fill, wrap, ins 0, gap 0 0",
					"[grow, fill]",
					"[][grow, fill][]");
			documentsPanel.setLayout(EBPLayout);
			{
//            	sentenceBoxLabel = new JLabel("Sentence:");
//            	sentenceBoxLabel.setHorizontalAlignment(SwingConstants.CENTER);
//            	sentenceBoxLabel.setFont(titleFont);
//            	sentenceBoxLabel.setOpaque(true);
//            	sentenceBoxLabel.setBackground(tan);
//            	sentenceBoxLabel.setBorder(rlborder);
//                
//                sentencePane = new JScrollPane();
//                sentenceEditPane = new JTextPane();
//                sentenceEditPane.setText("Current Sentence.");
//                sentenceEditPane.setFont(normalFont);
//                sentenceEditPane.setEditable(true);
//                sentencePane.setViewportView(sentenceEditPane);
//                
//                sentenceOptionsPanel = new JPanel();
//            	sentenceOptionsPanel.setBackground(optionsColor);
//            	sentenceOptionsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//            	MigLayout sentOptLayout = new MigLayout(
//            			"fill, wrap 1, gap 0 0, ins 0 n 0 n",
//            			"fill",
//            			"10:20:20");
//            	sentenceOptionsPanel.setLayout(sentOptLayout);
//            	{
//                 	JLabel sentOptionsLabel = new JLabel("Sentence Options:");
//                 	sentOptionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
//                 	sentOptionsLabel.setFont(titleFont);
//                   
//                 	restoreSentenceButton = new JButton("Restore");
// 					restoreSentenceButton.setToolTipText("Restores the sentence in the \"Current Sentence Box\"" +
// 														" back to what is highlighted in the document below, reverting any changes.");
// 					
//                 	SaveChangesButton = new JButton("Save Changes");
//                 	SaveChangesButton.setToolTipText("Saves what is in the \"Current Sentence Box\" to the document below.");
//                 	
//                 	sentenceOptionsPanel.add(sentOptionsLabel);
// 					sentenceOptionsPanel.add(restoreSentenceButton);
//                 	sentenceOptionsPanel.add(SaveChangesButton);
//                }
//                
//                translationsBoxLabel = new JLabel("Translation:");
//                translationsBoxLabel.setHorizontalAlignment(SwingConstants.CENTER);
//                translationsBoxLabel.setFont(titleFont);
//                translationsBoxLabel.setOpaque(true);
//                translationsBoxLabel.setBackground(tan);
//                translationsBoxLabel.setBorder(rlborder);
//                
//                translationPane = new JScrollPane();
//                translationEditPane = new JTextPane();
//                translationEditPane.setText("Current Translation.");
//                translationEditPane.setFont(normalFont);
//                translationEditPane.setEditable(true);
//                translationPane.setViewportView(translationEditPane);
//            	
//            	translationOptionsPanel = new JPanel();
//            	translationOptionsPanel.setBackground(optionsColor);
//            	translationOptionsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//            	MigLayout transOptLayout = new MigLayout(
//            			"fill, wrap 1, gap 0 0, ins 0 n 0 n",
//            			"fill",
//            			"10:20:20");
//            	translationOptionsPanel.setLayout(transOptLayout);
//            	{
//                    JLabel transOptionsLabel = new JLabel("Translation Options:");
//                    transOptionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
//                    transOptionsLabel.setFont(new Font("Ariel", Font.BOLD, 11));
//                    
//                	copyToSentenceButton = new JButton("Copy To Sentence");
//                	copyToSentenceButton.setToolTipText("Copies the translation in the \"Translation Box\"" +
//														" to the \"Current Sentence Box\". Press the \"Restore\" button to undo this.");
//                    
//                    JLabel filler = new JLabel();
//                    
//                    translationOptionsPanel.add(transOptionsLabel);
//                    translationOptionsPanel.add(copyToSentenceButton);
//                    translationOptionsPanel.add(filler);
//                }
            	
                documentLabel = new JLabel("Document:");
                documentLabel.setHorizontalAlignment(SwingConstants.CENTER);
                documentLabel.setFont(titleFont);
                documentLabel.setOpaque(true);
                documentLabel.setBackground(tan);
                documentLabel.setBorder(rlborder);
                
                documentScrollPane = new JScrollPane();
                documentPane = new JTextPane();
                documentPane.setDragEnabled(false);
                documentPane.setText("This is where the latest version of your document will be.");
                documentPane.setFont(normalFont);
                documentPane.setEnabled(true);
                documentPane.setEditable(true);
                documentScrollPane.setViewportView(documentPane);
                
//                documentOptionsPanel = new JPanel();
//                documentOptionsPanel.setBackground(tan);
//                documentOptionsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//                MigLayout DOPLayout = new MigLayout(
//            			"fill, wrap 1",
//            			"fill",
//            			"[20][20][20][20][20][20][20][]");
//            	documentOptionsPanel.setLayout(DOPLayout);
//        		{
//        			JLabel docNameTitleLabel = new JLabel("Name:");
//        			docNameTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        			docNameTitleLabel.setFont(titleFont);
//                    
//                    docNameLabel = new JLabel(" "); // space is so it doesn't shrivel up
//                    docNameLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//                    docNameLabel.setBackground(Color.WHITE);
//                    docNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
//                    docNameLabel.setOpaque(true);
//        			
//                    JLabel docOptionsLabel = new JLabel("Document Options:");
//                    docOptionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
//                    docOptionsLabel.setFont(titleFont);
//                    
//                	transButton = new JButton("Translate");
//                	transButton.setToolTipText("Translates the currently highlighted sentence.");
//                	
//                	appendSentenceButton = new JButton("Append Next");
//                	appendSentenceButton.setToolTipText("Appends the next sentence onto the current sentence.");
//                	
//                	dictButton = new JButton("Synonym Dictionary");
//                	dictButton.setToolTipText("Phrase and Synonym Dictionary.");
//                	
//                	saveButton = new JButton("Save To File");
//                	saveButton.setToolTipText("Saves what is in the document view to it's source file.");
//                	
                	processButton = new JButton("Process");
                	processButton.setToolTipText("Processes the document.");
//                    
////        			prevSentenceButton = new JButton("<--");
////        			prevSentenceButton.setHorizontalTextPosition(SwingConstants.CENTER);
////                    
////                	nextSentenceButton = new JButton("-->");
////                	nextSentenceButton.setHorizontalTextPosition(SwingConstants.CENTER);
//                    
//                    documentOptionsPanel.add(docNameTitleLabel);
//                    documentOptionsPanel.add(docNameLabel);
//                    documentOptionsPanel.add(docOptionsLabel);
//                	documentOptionsPanel.add(transButton);
//                	documentOptionsPanel.add(appendSentenceButton);
//                	documentOptionsPanel.add(dictButton);
//                	documentOptionsPanel.add(saveButton);
//                	documentOptionsPanel.add(processButton, "pushy, bottom, h 40!");
////        			documentOptionsPanel.add(prevSentenceButton, "split 2");
////                    documentOptionsPanel.add(nextSentenceButton);
//        		}
        		
//                documentsPanel.add(sentenceBoxLabel, "span, grow");
//                documentsPanel.add(sentencePane, "grow");
//            	documentsPanel.add(sentenceOptionsPanel, "grow, gapleft 0");
//                documentsPanel.add(translationsBoxLabel, "span, growx");
//                documentsPanel.add(translationPane, "grow");
//            	documentsPanel.add(translationOptionsPanel, "grow, gapleft 0");
                documentsPanel.add(documentLabel, "grow, h " + titleHeight + "!");
                //documentsPanel.add(processButton, "grow");
                documentsPanel.add(documentScrollPane, "grow");
                documentsPanel.add(processButton, "right");
            	//documentsPanel.add(documentOptionsPanel, "grow");
			}
            tabMade = true;
		}
		return documentsPanel;
	}
	
	private JPanel createClustersTab() throws Exception
	{
		PropertiesUtil.Location location = PropertiesUtil.getClustersTabLocation();
		clustersPanel = new JPanel();
		if (location == PropertiesUtil.Location.LEFT || location == PropertiesUtil.Location.RIGHT)
			clustersPanel.setLayout(new MigLayout(
					"wrap, ins 0",
					"grow, fill",
					"0[]0[grow, fill][]0"));
		else if (location == PropertiesUtil.Location.TOP)
			clustersPanel.setLayout(new MigLayout(
					"wrap 2, fill, ins 0, gap 0",
					"[70%][30%]",
					"[][][grow, fill]"));
		else
			throw new Exception();
		
		{ // --------------cluster panel components
			clustersLabel = new JLabel("Clusters:");
			clustersLabel.setHorizontalAlignment(SwingConstants.CENTER);
			clustersLabel.setFont(titleFont);
			clustersLabel.setOpaque(true);
			clustersLabel.setBackground(tan);
			clustersLabel.setBorder(rlborder);
			
			clusterHolderPanel = new ScrollablePanel()
			{
				public boolean getScrollableTracksViewportWidth()
				{
					return true;
				}
			};
			clusterHolderPanel.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PIXELS, 74);
			clusterHolderPanel.setAutoscrolls(true);
			clusterHolderPanel.setOpaque(true);
			BoxLayout clusterHolderPanelLayout = new BoxLayout(clusterHolderPanel, javax.swing.BoxLayout.Y_AXIS);
			clusterHolderPanel.setLayout(clusterHolderPanelLayout);
			clusterScrollPane = new JScrollPane(clusterHolderPanel);
			clusterScrollPane.setOpaque(true);
			
			legendLabel = new JLabel("Legend:");
			legendLabel.setHorizontalAlignment(SwingConstants.CENTER);
			legendLabel.setFont(titleFont);
			legendLabel.setOpaque(true);
			legendLabel.setBackground(tan);
			legendLabel.setBorder(rlborder);
			
			legendPanel = new JPanel();
			legendPanel.setLayout(new MigLayout(
					"wrap 2",
					"20[][100]",
					"grow, fill"));
			
			{ // --------------------legend panel components
				JLabel presentValueLabel = new JLabel("Present Value:");
				
				JPanel presentValuePanel = new JPanel();
				presentValuePanel.setBackground(Color.black);
				
				JLabel normalRangeLabel = new JLabel("Normal Range:");
				
				JPanel normalRangePanel = new JPanel();
				normalRangePanel.setBackground(Color.red);
				
				JLabel safeZoneLabel = new JLabel("Safe Zone:");
				
				JPanel safeZonePanel = new JPanel();
				safeZonePanel.setBackground(Color.green);
				
				legendPanel.add(presentValueLabel, "grow");
				legendPanel.add(presentValuePanel, "grow");
				legendPanel.add(normalRangeLabel, "grow");
				legendPanel.add(normalRangePanel, "grow");
				legendPanel.add(safeZoneLabel, "grow");
				legendPanel.add(safeZonePanel, "grow");
			}
			
			featuresPanel = new JPanel();
				featuresPanel.setLayout(new MigLayout(
						"wrap, fill, ins 0, gap 0 0",
						"grow, fill",
						"[][grow, fill][][grow, fill]"));
			{ // --------------------legend panel components
				JLabel featuresLabel = new JLabel("Feature Search:");
				featuresLabel.setHorizontalAlignment(SwingConstants.CENTER);
				featuresLabel.setFont(titleFont);
				featuresLabel.setOpaque(true);
				featuresLabel.setBackground(tan);
				featuresLabel.setBorder(rlborder);
				
				featuresListModel = new DefaultListModel();
				featuresList = new JList(featuresListModel);
				featuresListScrollPane = new JScrollPane(featuresList);
				
				JLabel subFeaturesLabel = new JLabel("Sub-Feature Search:");
				subFeaturesLabel.setHorizontalAlignment(SwingConstants.CENTER);
				subFeaturesLabel.setFont(titleFont);
				subFeaturesLabel.setOpaque(true);
				subFeaturesLabel.setBackground(tan);
				subFeaturesLabel.setBorder(rlborder);
				
				subFeaturesListModel = new DefaultListModel();
				subFeaturesList = new JList(subFeaturesListModel);
				subFeaturesList.setEnabled(false);
				subFeaturesListScrollPane = new JScrollPane(subFeaturesList);
				
				featuresPanel.add(featuresLabel, "grow, h " + titleHeight + "!");
				featuresPanel.add(featuresListScrollPane, "grow");
				featuresPanel.add(subFeaturesLabel, "grow, h " + titleHeight + "!");
				featuresPanel.add(subFeaturesListScrollPane, "grow");
			}
			
			if (location== PropertiesUtil.Location.LEFT || location == PropertiesUtil.Location.RIGHT)
			{
				//clustersPanel.add(legendPanel);
				clustersPanel.add(clustersLabel);
				clustersPanel.add(clusterScrollPane);
				clustersPanel.add(featuresPanel, "h 250!");
			}
			else if (location == PropertiesUtil.Location.TOP)
			{
				clustersPanel.add(clustersLabel, "grow, h " + titleHeight + "!");
				clustersPanel.add(legendLabel, "grow, h " + titleHeight + "!");
				clustersPanel.add(clusterScrollPane, "grow, spany");
				clustersPanel.add(legendPanel, "grow");
				clustersPanel.add(featuresPanel, "spany, grow");
			}
			else
				throw new Exception();
		}
		return clustersPanel;
	}
	
	private JPanel createAnonymityTab() throws Exception
	{
		PropertiesUtil.Location location = PropertiesUtil.getAnonymityTabLocation();
		anonymityPanel = new JPanel();
		if (location == PropertiesUtil.Location.LEFT || location == PropertiesUtil.Location.RIGHT)
			anonymityPanel.setLayout(new MigLayout(
					"wrap, ins 0",
					"grow, fill",
					"0[]0[grow, fill][]0"));
		else if (location == PropertiesUtil.Location.TOP)
			anonymityPanel.setLayout(new MigLayout(
					"wrap 2, fill, ins 0, gap 0",
					"[70%][30%]",
					"[][][grow, fill]"));
		else
			throw new Exception();
		
		{ // --------------cluster panel components
			anonymityLabel = new JLabel("Anonymity:");
			anonymityLabel.setHorizontalAlignment(SwingConstants.CENTER);
			anonymityLabel.setFont(titleFont);
			anonymityLabel.setOpaque(true);
			anonymityLabel.setBackground(tan);
			anonymityLabel.setBorder(rlborder);
			
			anonymityDrawingPanel = new AnonymityDrawingPanel();
			
			anonymityDescription = new JTextPane();
			anonymityDescription.setDragEnabled(false);
			anonymityDescription.setEditable(false);
			anonymityDescription.setFocusable(false);
			anonymityDescription.setFont(new Font("Helvatica", Font.PLAIN, 15));
			anonymityDescription.setText("Test document must be processed to recieve results");
			
			StyledDocument doc = anonymityDescription.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
			
			resultsTableLabel = new JLabel("Classification Results:");
			resultsTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
			resultsTableLabel.setFont(titleFont);
			resultsTableLabel.setOpaque(true);
			resultsTableLabel.setBackground(tan);
			resultsTableLabel.setBorder(rlborder);
			
			resultsMainPanel = new JPanel();
			{
				makeResultsTable();
			}
			
			if (location== PropertiesUtil.Location.LEFT || location == PropertiesUtil.Location.RIGHT)
			{
				//anonymityPanel.add(legendPanel);
				anonymityPanel.add(anonymityLabel, "spanx, grow, h " + titleHeight + "!");
				anonymityPanel.add(anonymityDrawingPanel, "h 515!");
				anonymityPanel.add(anonymityDescription, "h 70!");
				anonymityPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
				anonymityPanel.add(resultsMainPanel, "grow");
			}
			else if (location == PropertiesUtil.Location.TOP)
			{
				anonymityPanel.add(anonymityLabel, "spanx, grow, h " + titleHeight + "!");
				anonymityPanel.add(anonymityDrawingPanel, "h 515!");
				anonymityPanel.add(anonymityDescription, "h 70!");
				anonymityPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
				anonymityPanel.add(resultsMainPanel, "grow");
			}
			else
				throw new Exception();
		}
		return anonymityPanel;
	}
	
	
	
//	private JPanel createResultsTab() throws Exception
//	{
//		resultsPanel = new JPanel();
//		PropUtil.Location location = PropUtil.getResultsTabLocation();
//		if (location == PropUtil.Location.LEFT || location == PropUtil.Location.RIGHT)
//			resultsPanel.setLayout(new MigLayout(
//					"wrap, ins 0, gap 0 0",
//					"grow, fill",
//					"[][grow, fill][]"));
//		else if (location == PropUtil.Location.BOTTOM)
//			resultsPanel.setLayout(new MigLayout(
//					"wrap 2, ins 0, gap 0 0",
//					"[100:20%:][grow, fill]",
//					"[][grow, fill]"));
//		else
//			throw new Exception();
//		{
//			resultsTableLabel = new JLabel("Classification Results:");
//			resultsTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
//			resultsTableLabel.setFont(titleFont);
//			resultsTableLabel.setOpaque(true);
//			resultsTableLabel.setBackground(tan);
//			resultsTableLabel.setBorder(rlborder);
//			
//			displayTextArea = new JTextArea();
//			
//			resultsMainPanel = new JPanel();
//			{
//				makeResultsTable();
//			}
//			
//			if (location == PropUtil.Location.LEFT || location == PropUtil.Location.RIGHT)
//			{
//				resultsPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
//				resultsPanel.add(resultsMainPanel, "grow");
//				resultsPanel.add(new JScrollPane(displayTextArea), "h 150!");
//			}
//			else if (location == PropUtil.Location.BOTTOM)
//			{
//				resultsPanel.add(resultsTableLabel, "spanx, grow, h " + titleHeight + "!");
//				resultsPanel.add(new JScrollPane(displayTextArea), "grow");
//				resultsPanel.add(resultsMainPanel, "grow");
//			}
//			else
//				throw new Exception();
//		}
//        
//        return resultsPanel;
//	}
	
	private void makeResultsTable()
	{
		resultsMainPanel.setLayout(new MigLayout(
				"wrap, ins 0",
				"grow, fill",
				"grow, fill"));
		
		String[][] row = new String[1][1];
		row[0] = new String[] {"Waiting", "..."};
    	String[] header = {"Author:", "Ownership Probability"};
		
		// feature description pane--------------------------------------------------
		resultsTableModel = new DefaultTableModel(row, header){
			public boolean isCellEditable(int rowIndex, int mColIndex) {
		        return false;
		    }
		};
		
		resultsTable = new JTable(resultsTableModel);
		try {
			resultsTable.setDefaultRenderer(String.class, new alignCellRenderer(resultsTable, JLabel.CENTER, "cell"));
			resultsTable.getTableHeader().setDefaultRenderer(new alignCellRenderer(resultsTable, JLabel.CENTER, "header"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultsTable.setRowSelectionAllowed(false);
		resultsTable.setColumnSelectionAllowed(false);
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(75);
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		resultsTable.getTableHeader().setReorderingAllowed(false);
		resultsTablePane = new JScrollPane(resultsTable);
	    resultsMainPanel.add(resultsTablePane, "grow");
	}
	
	/**\
	 * Aligns the table header and cells to the specified alignment.
	 * @param table - The table you want to apply this too.
	 * @param alignment - the alignment you want. E.G. JLabel.CENTER or JLabel.RIGHT
	 * @param type - String, either "cell" to make the cells aligned, or "header" to make the header aligned
	 */
	public static class alignCellRenderer implements TableCellRenderer {

	    DefaultTableCellRenderer defaultRenderer;
	    DefaultTableCellRenderer headerRenderer;
	    String type;

		public alignCellRenderer(JTable table, int alignment, String type) throws Exception 
		{
			this.type = type;
			if (type == "cell")
			{
		        defaultRenderer = (DefaultTableCellRenderer)table.getDefaultRenderer(String.class);
		        defaultRenderer.setHorizontalAlignment(alignment);
			}
			else if (type == "header")
			{
				headerRenderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
		        headerRenderer.setHorizontalAlignment(alignment);
			}
			else
				throw new Exception();
	    }

	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int col) 
	    {
	    	// bad input is caught in constructor
	    	if (type == "cell")
	    		return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    	else
	    		return headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    }
	}
	
	/**
	 * Class for resizing table columns to fit the data/header.
	 * Call ColumnsAutoSizer.sizeColumnsToFit(table); in tabledChanged when adding a TableModelListener.
	 * @author Jeff
	 *
	 */
	
	//http://bosmeeuw.wordpress.com/2011/08/07/java-swing-automatically-resize-table-columns-to-their-contents/
	public static class ColumnsAutoSizer 
	{
	    public static void sizeColumnsToFit(JTable table) 
	    {
	        sizeColumnsToFit(table, 5);
	    }
	    
	    public static void sizeColumnsToFit(JTable table, int columnMargin) 
	    {
	        JTableHeader tableHeader = table.getTableHeader();
	        if(tableHeader == null) 
	        {
	            // can't auto size a table without a header
	            return;
	        }
	        FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
	        int[] minWidths = new int[table.getColumnCount()];
	        int[] maxWidths = new int[table.getColumnCount()];
	        for(int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) 
	        {
	            int headerWidth = headerFontMetrics.stringWidth(table.getColumnName(columnIndex));
	            minWidths[columnIndex] = headerWidth + columnMargin;
	            int maxWidth = getMaximalRequiredColumnWidth(table, columnIndex, headerWidth);
	            maxWidths[columnIndex] = Math.max(maxWidth, minWidths[columnIndex]) + columnMargin;
	        }
	        adjustMaximumWidths(table, minWidths, maxWidths);
	        for(int i = 0; i < minWidths.length; i++) 
	        {
	            if(minWidths[i] > 0) 
	            {
	                table.getColumnModel().getColumn(i).setMinWidth(minWidths[i]);
	            }
	            if(maxWidths[i] > 0) 
	            {
	                table.getColumnModel().getColumn(i).setMaxWidth(maxWidths[i]);
	                table.getColumnModel().getColumn(i).setWidth(maxWidths[i]);
	            }
	        }
	    }
	    private static void adjustMaximumWidths(JTable table, int[] minWidths, int[] maxWidths) 
	    {
	        if(table.getWidth() > 0) {
	            // to prevent infinite loops in exceptional situations
	            int breaker = 0;
	            // keep stealing one pixel of the maximum width of the highest column until we can fit in the width of the table
	            while(sum(maxWidths) > table.getWidth() && breaker < 10000) 
	            {
	                int highestWidthIndex = findLargestIndex(maxWidths);
	                maxWidths[highestWidthIndex] -= 1;
	                maxWidths[highestWidthIndex] = Math.max(maxWidths[highestWidthIndex], minWidths[highestWidthIndex]);
	                breaker++;
	            }
	        }
	    }
	    private static int getMaximalRequiredColumnWidth(JTable table, int columnIndex, int headerWidth) 
	    {
	        int maxWidth = headerWidth;
	        TableColumn column = table.getColumnModel().getColumn(columnIndex);
	        TableCellRenderer cellRenderer = column.getCellRenderer();
	        if(cellRenderer == null) 
	        {
	            cellRenderer = new DefaultTableCellRenderer();
	        }
	        for(int row = 0; row < table.getModel().getRowCount(); row++) 
	        {
	            Component rendererComponent = cellRenderer.getTableCellRendererComponent(table,
	                table.getModel().getValueAt(row, columnIndex),
	                false,
	                false,
	                row,
	                columnIndex);
	            double valueWidth = rendererComponent.getPreferredSize().getWidth();
	            maxWidth = (int) Math.max(maxWidth, valueWidth);
	        }
	        return maxWidth;
	    }
	    private static int findLargestIndex(int[] widths) 
	    {
	        int largestIndex = 0;
	        int largestValue = 0;
	        for(int i = 0; i < widths.length; i++) 
	        {
	            if(widths[i] > largestValue) 
	            {
	                largestIndex = i;
	                largestValue = widths[i];
	            }
	        }
	        return largestIndex;
	    }

	    private static int sum(int[] widths) 
	    {
	        int sum = 0;
	        for(int width : widths) 
	        {
	            sum += width;
	        }
	        return sum;
	    }
	}

}
