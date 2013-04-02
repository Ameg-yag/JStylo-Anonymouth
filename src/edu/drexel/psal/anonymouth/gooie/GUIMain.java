package edu.drexel.psal.anonymouth.gooie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.jstylo.generics.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.*;
import javax.swing.tree.*;

import weka.classifiers.*;

import edu.drexel.psal.jstylo.analyzers.WekaAnalyzer;


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
public class GUIMain extends javax.swing.JFrame {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	// main instance
	public static GUIMain inst;

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

	protected String defaultTrainDocsTreeName = "Authors"; 
	protected Font defaultLabelFont = new Font("Verdana",0,16);
	protected static int cellPadding = 5;

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
	protected JLabel docPreviewJLabel;
	protected JButton newProblemSetJButton;
	protected JTextPane docPreviewJTextPane;
	protected JScrollPane docPreviewJScrollPane;
	protected JButton loadProblemSetJButton;
	protected JButton saveProblemSetJButton;
	protected JButton docTabNextJButton;
	protected JButton removeAuthorJButton;
	protected JButton removeTrainDocsJButton;
	protected JButton addTrainDocsJButton;
	protected JTree trainCorpusJTree;
	protected JTable testDocsJTable;
	protected DefaultTableModel testDocsTableModel;
	protected JLabel featuresToolsJLabel;
	protected JLabel docPreviewNameJLabel;
	protected JLabel corpusJLabel;
	protected JButton removeTestDocJButton;
	protected JButton addAuthorJButton;
	protected JButton addTestDocJButton;
	protected JButton clearDocPreviewJButton;
	protected JButton docsAboutJButton;
	protected JTable userSampleDocsJTable;
	protected DefaultTableModel userSampleDocsTableModel;
	protected JLabel userSampleDocsJLabel;
	protected JPanel buttons;
	protected JButton adduserSampleDocJButton;
	protected JButton removeuserSampleDocJButton;
	protected JButton userSampleDocPreviewJButton;

	// features tab
	protected JButton featuresNextJButton;
	protected JButton featuresBackJButton;
	protected JLabel featuresFeatureConfigJLabel;
	protected JLabel featuresFactorContentJLabel;
	protected JLabel featuresFeatureExtractorContentJLabel;
	protected JScrollPane featuresFeatureExtractorJScrollPane;
	protected JLabel featuresNormContentJLabel;
	protected JScrollPane featuresFeatureExtractorConfigJScrollPane;
	protected JScrollPane featuresCullConfigJScrollPane;
	protected JScrollPane featuresCanonConfigJScrollPane;
	protected JList featuresCullJList;
	protected DefaultComboBoxModel featuresCullJListModel;
	protected JButton refreshButtonEditor;
	private JPanel spacer2;
	private JLabel synonymsLabel;
	protected JTextPane addToSentencePane;
	private JScrollPane addToSentenceScrollPane;
	private JPanel spacer1;
	private JPanel jPanel5;
	private JPanel jPanel4;
	private JPanel jPanel3;
	protected JTextPane elementsToRemovePane;
	private JScrollPane elementsToRemoveScrollPane;
	protected JLabel jLabel1;

	protected JScrollPane featuresCullListJScrollPane;
	protected JScrollPane featuresCanonListJScrollPane;
	protected JList featuresCanonJList;
	protected DefaultComboBoxModel featuresCanonJListModel;
	protected JScrollPane featuresFeatureDescJScrollPane;
	protected JTextPane featuresFeatureDescJTextPane;
	protected JLabel featuresFeatureExtractorJLabel;
	protected JLabel featuresFactorJLabel;
	protected JLabel featuresNormJLabel;
	protected JLabel featuresFeatureDescJLabel;
	protected JTextField featuresFeatureNameJTextField;
	protected JLabel featuresFeatureNameJLabel;
	protected JLabel featuresCullJLabel;
	protected JLabel featuresCanonJLabel;
	protected JButton featuresEditJButton;
	protected JButton featuresRemoveJButton;
	protected JButton featuresAddJButton;
	protected JList featuresJList;
	protected DefaultComboBoxModel featuresJListModel;
	protected JLabel featuresFeaturesJLabel;
	protected JTextPane featuresSetDescJTextPane;
	protected JScrollPane featuresSetDescJScrollPane;
	protected JLabel featuresSetDescJLabel;
	protected JTextField featuresSetNameJTextField;
	protected JLabel featuresSetNameJLabel;
	protected JButton featuresNewSetJButton;
	protected JButton featuresSaveSetJButton;
	protected JButton featuresLoadSetFromFileJButton;
	protected JButton featuresAddSetJButton;
	protected JComboBox featuresSetJComboBox;
	protected DefaultComboBoxModel featuresSetJComboBoxModel;
	protected JLabel featuresSetJLabel;
	protected JButton featuresAboutJButton;

	// Calssifiers tab
	protected JTextField classAvClassArgsJTextField;
	protected JLabel classAvClassArgsJLabel;
	protected JComboBox classClassJComboBox;
	protected JLabel classAvClassJLabel;
	protected JButton classAddJButton;
	protected JTree classJTree;
	protected JTextField classSelClassArgsJTextField;
	protected JLabel classSelClassArgsJLabel;
	protected JScrollPane classSelClassJScrollPane;
	protected JList classJList;
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
	
	// Editor tab
	protected JScrollPane theEditorScrollPane;
	protected JTextPane suggestionBox;
	protected JTable suggestionTable;
	protected JTextPane editorBox;
	protected JTable resultsTable;
	protected JLabel classificationLabel;
	protected JLabel suggestionLabel;
	protected JButton addSentence;
	protected JTextPane elementsToAddPane;
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
	protected JScrollPane elementsToAddScrollPane;
	protected JScrollPane suggestionPane;
	protected JPanel editorInfoJPanel;
	protected JScrollPane editorInteractionScrollPane;
	protected JScrollPane EditorInfoScrollPane;
	protected JTabbedPane editTP;
	protected JLabel elementsToAddLabel;
	protected JScrollPane wordsToAddPane;
	protected JButton nextSentenceButton;
	protected JButton lastSentenceButton;
	protected JTextField searchInputBox;
	protected JComboBox highlightSelectionBox;
	protected JLabel highlightLabel;
	protected JPanel jPanel_IL3;
	protected JButton clearHighlightingButton;
	protected JProgressBar editorProgressBar;
	protected JLabel editingProgressBarLabel;
	protected JLabel featureNameLabel;
	protected JLabel targetValueLabel;
	protected JLabel presentValueLabel;
	protected JTextField targetValueField;
	protected JTextField presentValueField;
	protected JLabel suggestionListLabel;
	protected JButton saveButton;
	protected JButton exitButton;
	protected JButton processButton;
	protected JButton verboseButton;
	protected JButton dictButton;
	protected JLabel resultsTableLabel;
	protected JScrollPane resultsTablePane;
	protected JScrollPane editBox;
	protected JPanel editBoxPanel;
	protected JScrollPane suggestionListPane;

	// Cluster tab
	protected JScrollPane theScrollPane;
	protected JPanel examplePlotPanel;
	protected JPanel topPanel;
	protected JPanel secondPanel;
	protected JPanel holderPanel;
	protected JButton refreshButton;
	protected JTabbedPane clusterTab;
	protected JCheckBox shouldCloseBox;
	protected JButton selectClusterConfiguration;
	protected JComboBox clusterConfigurationBox;
	protected JPanel Targets;
	protected JButton reClusterAllButton;
	protected JLayeredPane[] clusterViewerLayoverPanes;
	protected JPanel[] namePanels;
	
	// Analysis tab
	protected JCheckBox analysisOutputAccByClassJCheckBox;
	protected JCheckBox analysisOutputConfusionMatrixJCheckBox;
	protected ButtonGroup analysisTypeButtonGroup;
	
	protected static ImageIcon iconNO;
	protected static ImageIcon iconFINISHED;
	public static ImageIcon icon;
	
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
					javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.err.println("Look-and-Feel error!");
				}
				inst = new GUIMain();
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
		FeaturesTabDriver.initPresetCFDs(this);
		FeatureWizardDriver.populateAll();
		classifiers = new ArrayList<Classifier>();
		wib = new WekaInstancesBuilder(true);
		results = new ArrayList<String>();
	}

	private void initGUI() {
		try {
			
			setSize(1024, 768);
			this.setTitle("Anonymouth");
			this.setIconImage(new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_LOGO.png")).getImage());
			
			
			{
				mainJTabbedPane = new JTabbedPane();
				getContentPane().add(mainJTabbedPane, BorderLayout.CENTER);
				
				/* =============
				 * Documents tab
				 * =============
				 */
				docsTab = new JPanel(new BorderLayout(cellPadding,cellPadding));
				mainJTabbedPane.addTab("Documents", docsTab);

				// problem set buttons
				// ===================
				{
					JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					docsTab.add(panel,BorderLayout.NORTH);
					{
						newProblemSetJButton = new JButton();
						panel.add(newProblemSetJButton);
						newProblemSetJButton.setText("New Problem Set");
					}
					{
						saveProblemSetJButton = new JButton();
						panel.add(saveProblemSetJButton);
						saveProblemSetJButton.setText("Save Problem Set...");
					}
					{
						loadProblemSetJButton = new JButton();
						panel.add(loadProblemSetJButton);
						loadProblemSetJButton.setText("Load Problem Set...");
					}
				}
				{
					JPanel centerPanel = new JPanel(new GridLayout(2,1,cellPadding,cellPadding));
					docsTab.add(centerPanel,BorderLayout.CENTER);
					JPanel topPanel = new JPanel(new GridLayout(1,3,cellPadding,cellPadding));
					centerPanel.add(topPanel);
					JPanel testDocsPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
					JPanel userSampleDocsPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
					JPanel trainDocsPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
					topPanel.add(testDocsPanel);
					topPanel.add(userSampleDocsPanel);
					topPanel.add(trainDocsPanel);
					JPanel bottomPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
					centerPanel.add(bottomPanel);

					// test documents
					// ==============
					{
						testDocsJLabel = new JLabel();
						testDocsPanel.add(testDocsJLabel,BorderLayout.NORTH);
						testDocsJLabel.setText("Your Document to Anonymize");
						testDocsJLabel.setFont(defaultLabelFont);
					}
					{
						testDocsTableModel = new DefaultTableModel();
						testDocsTableModel.addColumn("Title");
						testDocsTableModel.addColumn("Path");
						testDocsJTable = new JTable(testDocsTableModel){
							public boolean isCellEditable(int rowIndex, int colIndex) {
								return false;
							}
						};
						testDocsJTable.getTableHeader().setReorderingAllowed(false);
						testDocsJTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						JScrollPane scrollPane = new JScrollPane(testDocsJTable);
						testDocsPanel.add(scrollPane,BorderLayout.CENTER);
					}
					{
						JPanel buttons = new JPanel(new GridLayout(2,3,cellPadding,cellPadding));
						testDocsPanel.add(buttons,BorderLayout.SOUTH);
						{
							addTestDocJButton = new JButton();
							buttons.add(addTestDocJButton);
							addTestDocJButton.setText("Add Document...");
						}
						{
							removeTestDocJButton = new JButton();
							buttons.add(removeTestDocJButton);
							removeTestDocJButton.setText("Remove Document");
						}
						{
							testDocPreviewJButton = new JButton();
							buttons.add(testDocPreviewJButton);
							testDocPreviewJButton.setText("Preview Document");
						}
						buttons.add(new JPanel());
						buttons.add(new JPanel());
						buttons.add(new JPanel());
					}
					
					{ ///////////////////////////////////
						
						{
							userSampleDocsJLabel = new JLabel();
							userSampleDocsPanel.add(userSampleDocsJLabel,BorderLayout.NORTH);
							userSampleDocsJLabel.setText("Your Sample Documents");
							userSampleDocsJLabel.setFont(defaultLabelFont);
						}
						{
							userSampleDocsTableModel = new DefaultTableModel();
							userSampleDocsTableModel.addColumn("Title");
							userSampleDocsTableModel.addColumn("Path");
							userSampleDocsJTable = new JTable(userSampleDocsTableModel){
								public boolean isCellEditable(int rowIndex, int colIndex) {
									return false;
								}
							};
							userSampleDocsJTable.getTableHeader().setReorderingAllowed(false);
							userSampleDocsJTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
							JScrollPane scrollPane = new JScrollPane(userSampleDocsJTable);
							userSampleDocsPanel.add(scrollPane,BorderLayout.CENTER);
						}
						{
							buttons = new JPanel(new GridLayout(2,3,cellPadding,cellPadding));
							userSampleDocsPanel.add(buttons,BorderLayout.SOUTH);
							{
								adduserSampleDocJButton = new JButton();
								buttons.add(adduserSampleDocJButton);
								adduserSampleDocJButton.setText("Add Document...");
							}
							{
								removeuserSampleDocJButton = new JButton();
								buttons.add(removeuserSampleDocJButton);
								removeuserSampleDocJButton.setText("Remove Document");
							}
							{
								userSampleDocPreviewJButton = new JButton();
								buttons.add(userSampleDocPreviewJButton);
								userSampleDocPreviewJButton.setText("Preview Document");
							}
							buttons.add(new JPanel());
							buttons.add(new JPanel());
							buttons.add(new JPanel());
						}
						
					} ////////////////////////////////////

					// training documents
					// ==================
					{
						corpusJLabel = new JLabel();
						trainDocsPanel.add(corpusJLabel,BorderLayout.NORTH);
						corpusJLabel.setText("Other Sample Documents");
						corpusJLabel.setFont(defaultLabelFont);
					}
					{
						DefaultMutableTreeNode top = new DefaultMutableTreeNode(ps.getTrainCorpusName());
						trainCorpusJTree = new JTree(top);
						JScrollPane scrollPane = new JScrollPane(trainCorpusJTree);
						trainDocsPanel.add(scrollPane,BorderLayout.CENTER);
					}
					{
						JPanel buttons = new JPanel(new GridLayout(2,3,cellPadding,cellPadding));
						trainDocsPanel.add(buttons,BorderLayout.SOUTH);
						{
							addAuthorJButton = new JButton();
							buttons.add(addAuthorJButton);
							addAuthorJButton.setText("Add Author...");
						}
						{
							addTrainDocsJButton = new JButton();
							buttons.add(addTrainDocsJButton);
							addTrainDocsJButton.setText("Add Document(s)...");
						}
						{
							trainNameJButton = new JButton();
							buttons.add(trainNameJButton);
							trainNameJButton.setText("Edit Name...");
						}
						{
							removeAuthorJButton = new JButton();
							buttons.add(removeAuthorJButton);
							removeAuthorJButton.setText("Remove Author(s)");
						}
						{
							removeTrainDocsJButton = new JButton();
							buttons.add(removeTrainDocsJButton);
							removeTrainDocsJButton.setText("Remove Document(s)");
						}
						{
							trainDocPreviewJButton = new JButton();
							buttons.add(trainDocPreviewJButton);
							trainDocPreviewJButton.setText("Preview Document");
						}
					}

					// preview documents
					// =================
					{
						JPanel preview = new JPanel(new FlowLayout(FlowLayout.LEFT));
						bottomPanel.add(preview,BorderLayout.NORTH);
						{
							docPreviewJLabel = new JLabel();
							preview.add(docPreviewJLabel);
							docPreviewJLabel.setText("Document Preview");
							docPreviewJLabel.setFont(defaultLabelFont);
						}
						{
							docPreviewNameJLabel = new JLabel();
							preview.add(docPreviewNameJLabel);
							docPreviewNameJLabel.setFont(defaultLabelFont);
						}
					}
					{
						docPreviewJTextPane = new JTextPane();
						docPreviewJTextPane.setEditable(false);
						docPreviewJTextPane.setPreferredSize(new java.awt.Dimension(413, 261));
						docPreviewJScrollPane = new JScrollPane(docPreviewJTextPane);
						bottomPanel.add(docPreviewJScrollPane,BorderLayout.CENTER);
					}
					{
						JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
						bottomPanel.add(p,BorderLayout.SOUTH);
						clearDocPreviewJButton = new JButton();
						p.add(clearDocPreviewJButton);
						clearDocPreviewJButton.setText("Clear Preview");
					}
				}

				// bottom toolbar buttons
				// ======================
				{
					JPanel bottomToolbar = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
					docsTab.add(bottomToolbar,BorderLayout.SOUTH);
					//{
						//JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
						//bottomToolbar.add(p);
						//docsAboutJButton = new JButton("About...");
						//p.add(docsAboutJButton);
					//}
					{
						JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
						bottomToolbar.add(p);
						docTabNextJButton = new JButton();
						p.add(docTabNextJButton);
						docTabNextJButton.setText("Next");
					}
				}

				/* ============
				 * Features tab
				 * ============
				 */
				featuresTab = new JPanel(new BorderLayout(cellPadding,cellPadding));
				mainJTabbedPane.addTab("Features", featuresTab);
				{
					// top of the features tab
					// =======================
					JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,cellPadding,cellPadding));
					featuresTab.add(panel,BorderLayout.NORTH);
					panel.setPreferredSize(new java.awt.Dimension(1003, 42));
					{
						featuresSetJLabel = new JLabel();
						featuresSetJLabel.setFont(defaultLabelFont);
						panel.add(featuresSetJLabel);
						featuresSetJLabel.setText("Feature Set");
					}
					{
						String[] presetCFDsNames = new String[presetCFDs.size() + 1];
						presetCFDsNames[0] = "";
						for (int i=0; i<presetCFDs.size(); i++)
							presetCFDsNames[i+1] = presetCFDs.get(i).getName();

						featuresSetJComboBoxModel = new DefaultComboBoxModel(presetCFDsNames);
						featuresSetJComboBox = new JComboBox();
						panel.add(featuresSetJComboBox);
						featuresSetJComboBox.setModel(featuresSetJComboBoxModel);
						featuresSetJComboBox.setPreferredSize(new java.awt.Dimension(200, 20));
					}
					{
						featuresAddSetJButton = new JButton();
						panel.add(featuresAddSetJButton);
						featuresAddSetJButton.setText("Add Feature Set");
					}
					{
						featuresLoadSetFromFileJButton = new JButton();
						panel.add(featuresLoadSetFromFileJButton);
						featuresLoadSetFromFileJButton.setText("Import from XML...");
					}
					{
						featuresSaveSetJButton = new JButton();
						panel.add(featuresSaveSetJButton);
						featuresSaveSetJButton.setText("Export to XML...");
					}
					{
						featuresNewSetJButton = new JButton();
						panel.add(featuresNewSetJButton);
						featuresNewSetJButton.setText("New Feature Set");
					}
				}
				{
					// center of the features tab
					// ==========================

					JPanel main = new JPanel(new BorderLayout(cellPadding,cellPadding));
					featuresTab.add(main,BorderLayout.CENTER);
					{
						// name and description
						// ====================

						JPanel nameDescPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
						main.add(nameDescPanel,BorderLayout.NORTH);
						{
							JPanel north = new JPanel(new BorderLayout(cellPadding,cellPadding));
							nameDescPanel.add(north,BorderLayout.NORTH);
							{
								featuresSetNameJLabel = new JLabel();
								featuresSetNameJLabel.setVerticalAlignment(JLabel.TOP);
								featuresSetNameJLabel.setFont(defaultLabelFont);
								featuresSetNameJLabel.setPreferredSize(new Dimension(200,20));
								north.add(featuresSetNameJLabel,BorderLayout.WEST);
								featuresSetNameJLabel.setText("Feature Set Name");
							}
							{
								featuresSetNameJTextField = new JTextField();
								north.add(featuresSetNameJTextField,BorderLayout.CENTER);
							}
						}
						{
							JPanel center = new JPanel(new BorderLayout(cellPadding,cellPadding));
							nameDescPanel.add(center,BorderLayout.CENTER);
							{
								featuresSetDescJLabel = new JLabel();
								featuresSetDescJLabel.setVerticalAlignment(JLabel.TOP);
								center.add(featuresSetDescJLabel,BorderLayout.WEST);
								featuresSetDescJLabel.setText("Feature Set Description");
								featuresSetDescJLabel.setFont(defaultLabelFont);
								featuresSetDescJLabel.setPreferredSize(new Dimension(200,20));
							}
							{
								featuresSetDescJScrollPane = new JScrollPane();
								featuresSetDescJScrollPane.setPreferredSize(new Dimension(featuresSetNameJTextField.getWidth(),100));
								center.add(featuresSetDescJScrollPane,BorderLayout.CENTER);
								{
									featuresSetDescJTextPane = new JTextPane();
									featuresSetDescJScrollPane.setViewportView(featuresSetDescJTextPane);
								}
							}
						}
					}

					{
						// all the rest
						// ============
						JPanel remainderPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
						main.add(remainderPanel,BorderLayout.CENTER);

						{
							// north - header
							// ==============
							{
								featuresFeaturesJLabel = new JLabel();
								remainderPanel.add(featuresFeaturesJLabel,BorderLayout.NORTH);
								featuresFeaturesJLabel.setText("Features");
								featuresFeaturesJLabel.setFont(defaultLabelFont);
							}
						}
						{
							// west - feature list
							//====================

							JPanel west = new JPanel(new BorderLayout(cellPadding,cellPadding));
							remainderPanel.add(west,BorderLayout.WEST);
							{
								JScrollPane featuresListJScrollPane = new JScrollPane();
								west.add(featuresListJScrollPane,BorderLayout.CENTER);
								{
									featuresJListModel = 
											new DefaultComboBoxModel();
									featuresJList = new JList();
									featuresListJScrollPane.setViewportView(featuresJList);
									featuresJList.setModel(featuresJListModel);
									featuresJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
								}
							}
							{
								JPanel buttons = new JPanel(new GridLayout(1,3,cellPadding,cellPadding));
								west.add(buttons,BorderLayout.SOUTH);
								{
									featuresAddJButton = new JButton();
									buttons.add(featuresAddJButton);
									featuresAddJButton.setText("Add...");
									featuresAddJButton.setVisible(false);
								}
								{
									featuresRemoveJButton = new JButton();
									buttons.add(featuresRemoveJButton);
									featuresRemoveJButton.setText("Remove");
									featuresRemoveJButton.setVisible(false);
								}
								{
									featuresEditJButton = new JButton();
									buttons.add(featuresEditJButton);
									featuresEditJButton.setText("Edit...");
									featuresEditJButton.setVisible(false);
								}
							}
						}
						{
							// center - feature configuration
							// ==============================

							JPanel center = new JPanel();
							center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
							remainderPanel.add(center,BorderLayout.CENTER);
							Dimension labelDim = new Dimension(200,20);

							{
								// feature name
								// ============

								JPanel name = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(name);
								{
									featuresFeatureNameJLabel = new JLabel();
									featuresFeatureNameJLabel.setVerticalAlignment(JLabel.TOP);
									featuresFeatureNameJLabel.setPreferredSize(labelDim);
									name.add(featuresFeatureNameJLabel,BorderLayout.WEST);
									featuresFeatureNameJLabel.setText("Feature Name");
									featuresFeatureNameJLabel.setFont(defaultLabelFont);
								}
								{
									featuresFeatureNameJTextField = new JTextField();
									featuresFeatureNameJTextField.setEditable(false);
									featuresFeatureNameJTextField.setPreferredSize(new Dimension(0,20));
									name.add(featuresFeatureNameJTextField,BorderLayout.CENTER);
								}
							}
							{
								// feature description
								// ===================

								JPanel desc = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(desc);
								{
									featuresFeatureDescJLabel = new JLabel();
									featuresFeatureDescJLabel.setVerticalAlignment(JLabel.TOP);
									featuresFeatureDescJLabel.setPreferredSize(labelDim);
									desc.add(featuresFeatureDescJLabel, BorderLayout.WEST);
									featuresFeatureDescJLabel.setText("Feature Description");
									featuresFeatureDescJLabel.setFont(defaultLabelFont);
								}
								{
									featuresFeatureDescJScrollPane = new JScrollPane();
									desc.add(featuresFeatureDescJScrollPane,BorderLayout.CENTER);
									{
										featuresFeatureDescJTextPane = new JTextPane();
										featuresFeatureDescJTextPane.setEditable(false);
										featuresFeatureDescJScrollPane.setViewportView(featuresFeatureDescJTextPane);
									}
								}
							}
							{
								// configuration headers
								// =====================

								JPanel configHeaders = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(configHeaders);
								{
									JLabel stub = new JLabel();
									stub.setPreferredSize(new Dimension(labelDim));
									configHeaders.add(stub,BorderLayout.WEST);
								}
								{
									JPanel headers = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
									configHeaders.add(headers,BorderLayout.CENTER);
									{
										featuresToolsJLabel = new JLabel();
										headers.add(featuresToolsJLabel);
										featuresToolsJLabel.setText("Tools");
										featuresToolsJLabel.setFont(defaultLabelFont);
									}
									{
										featuresFeatureConfigJLabel = new JLabel();
										headers.add(featuresFeatureConfigJLabel);
										featuresFeatureConfigJLabel.setText("Configuration");
										featuresFeatureConfigJLabel.setFont(defaultLabelFont);
									}
								}
							}
							{
								// feature extractor
								// =================

								JPanel extractor = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(extractor);
								{
									featuresFeatureExtractorJLabel = new JLabel();
									featuresFeatureExtractorJLabel.setVerticalAlignment(JLabel.TOP);
									featuresFeatureExtractorJLabel.setPreferredSize(labelDim);
									extractor.add(featuresFeatureExtractorJLabel,BorderLayout.WEST);
									featuresFeatureExtractorJLabel.setText("Feature Extractor");
									featuresFeatureExtractorJLabel.setFont(defaultLabelFont);
								}
								{
									JPanel config = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
									extractor.add(config,BorderLayout.CENTER);
									{
										featuresFeatureExtractorJScrollPane = new JScrollPane();
										config.add(featuresFeatureExtractorJScrollPane);
										{
											featuresFeatureExtractorContentJLabel = new JLabel();
											featuresFeatureExtractorContentJLabel.setVerticalAlignment(JLabel.TOP);
											featuresFeatureExtractorJScrollPane.setViewportView(featuresFeatureExtractorContentJLabel);
										}
									}
									{
										featuresFeatureExtractorConfigJScrollPane = new JScrollPane();
										config.add(featuresFeatureExtractorConfigJScrollPane);
									}
								}
							}
							{
								// canonicizers
								// ============

								JPanel canons = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(canons);
								{
									featuresCanonJLabel = new JLabel();
									featuresCanonJLabel.setVerticalAlignment(JLabel.TOP);
									featuresCanonJLabel.setPreferredSize(labelDim);
									canons.add(featuresCanonJLabel,BorderLayout.WEST);
									featuresCanonJLabel.setText("Text Pre-Processing");
									featuresCanonJLabel.setFont(defaultLabelFont);
								}
								{
									JPanel config = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
									canons.add(config,BorderLayout.CENTER);
									{
										featuresCanonListJScrollPane = new JScrollPane();
										config.add(featuresCanonListJScrollPane);
										{
											featuresCanonJListModel = 
													new DefaultComboBoxModel();
											featuresCanonJList = new JList();
											featuresCanonListJScrollPane.setViewportView(featuresCanonJList);
											featuresCanonJList.setModel(featuresCanonJListModel);
										}
									}
									{
										featuresCanonConfigJScrollPane = new JScrollPane();
										config.add(featuresCanonConfigJScrollPane);
									}
								}
							}
							{
								// cullers
								// =======

								JPanel cullers = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(cullers);
								{
									featuresCullJLabel = new JLabel();
									featuresCullJLabel.setVerticalAlignment(JLabel.TOP);
									featuresCullJLabel.setPreferredSize(labelDim);
									cullers.add(featuresCullJLabel,BorderLayout.WEST);
									featuresCullJLabel.setText("Feature Post-Processing");
									featuresCullJLabel.setFont(defaultLabelFont);
								}
								{
									JPanel config = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
									cullers.add(config,BorderLayout.CENTER);
									{
										featuresCullListJScrollPane = new JScrollPane();
										config.add(featuresCullListJScrollPane);
										{
											featuresCullJListModel = 
													new DefaultComboBoxModel();
											featuresCullJList = new JList();
											featuresCullListJScrollPane.setViewportView(featuresCullJList);
											featuresCullJList.setModel(featuresCullJListModel);
										}
									}
									{
										featuresCullConfigJScrollPane = new JScrollPane();
										config.add(featuresCullConfigJScrollPane);
									}
								}
							}
							{
								// normalization
								// =============

								JPanel norm = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(norm);
								{
									featuresNormJLabel = new JLabel();
									featuresNormJLabel.setVerticalAlignment(JLabel.TOP);
									featuresNormJLabel.setPreferredSize(labelDim);
									norm.add(featuresNormJLabel,BorderLayout.WEST);
									featuresNormJLabel.setText("Normalization");
									featuresNormJLabel.setFont(defaultLabelFont);
								}
								{
									featuresNormContentJLabel = new JLabel();
									norm.add(featuresNormContentJLabel,BorderLayout.CENTER);
									featuresNormContentJLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
								}
							}
							{
								// feature factor
								// ==============

								JPanel factor = new JPanel(new BorderLayout(cellPadding,cellPadding));
								center.add(factor);
								{
									featuresFactorJLabel = new JLabel();
									featuresFactorJLabel.setVerticalAlignment(JLabel.TOP);
									featuresFactorJLabel.setPreferredSize(labelDim);
									factor.add(featuresFactorJLabel,BorderLayout.WEST);
									featuresFactorJLabel.setText("Factor");
									featuresFactorJLabel.setFont(defaultLabelFont);
								}
								{
									featuresFactorContentJLabel = new JLabel();
									factor.add(featuresFactorContentJLabel,BorderLayout.CENTER);
									featuresFactorContentJLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
								}
							}
						}
					}

				}
				{
					// bottom toolbar buttons
					// ======================
					{
						JPanel bottomToolbar = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
						featuresTab.add(bottomToolbar,BorderLayout.SOUTH);
						//{
							//JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
							//bottomToolbar.add(p);
							//featuresAboutJButton = new JButton("About...");
							//p.add(featuresAboutJButton);
						//}
						{
							JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
							bottomToolbar.add(p);
							{
								featuresBackJButton = new JButton();
								p.add(featuresBackJButton);
								featuresBackJButton.setText("Back");
							}
							{
								featuresNextJButton = new JButton();
								p.add(featuresNextJButton);
								featuresNextJButton.setText("Next");
							}
						}
					}
				}

				/* ===============
				 * Classifiers tab
				 * ===============
				 */
				classTab = new JPanel(new BorderLayout(cellPadding,cellPadding));
				mainJTabbedPane.addTab("Classifiers", classTab);
				{
					// main center
					// ===========
					
					JPanel center = new JPanel(new GridLayout(2,1,cellPadding,cellPadding));
					classTab.add(center,BorderLayout.CENTER);
					
					{
						// available and selected classifiers
						// ==================================
						
						JPanel top = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
						center.add(top);
						
						{
							// available
							// =========
							JPanel av = new JPanel(new BorderLayout(cellPadding,cellPadding));
							top.add(av);
							{
								classAvClassJLabel = new JLabel();
								classAvClassJLabel.setFont(defaultLabelFont);
								av.add(classAvClassJLabel,BorderLayout.NORTH);
								classAvClassJLabel.setText("Available WEKA Classifiers");
							}
							{
								classTreeScrollPane = new JScrollPane();
								av.add(classTreeScrollPane,BorderLayout.CENTER);
								{
									classJTree = new JTree();
									classJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
									classTreeScrollPane.setViewportView(classJTree);
									ClassTabDriver.initWekaClassifiersTree(this);
								}
							}
							{
								JPanel config = new JPanel(new GridLayout(3,1,cellPadding,cellPadding));
								av.add(config,BorderLayout.SOUTH);
								{
									classAvClassArgsJLabel = new JLabel();
									config.add(classAvClassArgsJLabel);
									classAvClassArgsJLabel.setText("Classifier Arguments");
									classAvClassArgsJLabel.setFont(defaultLabelFont);
								}
								{
									classAvClassArgsJTextField = new JTextField();
									config.add(classAvClassArgsJTextField);
								}
								{
									classAddJButton = new JButton();
									config.add(classAddJButton);
									classAddJButton.setText("Add");
								}
							}
						}
						{
							// selected
							// ========
							JPanel sel = new JPanel(new BorderLayout(cellPadding,cellPadding));
							top.add(sel);
							{
								classSelClassJLabel = new JLabel();
								sel.add(classSelClassJLabel,BorderLayout.NORTH);
								classSelClassJLabel.setText("Selected WEKA Classifiers");
								classSelClassJLabel.setFont(defaultLabelFont);
							}
							{
								classSelClassJScrollPane = new JScrollPane();
								sel.add(classSelClassJScrollPane,BorderLayout.CENTER);
								{
									classSelClassJListModel = 
											new DefaultComboBoxModel();
									classJList = new JList();
									classJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
									classSelClassJScrollPane.setViewportView(classJList);
									classJList.setModel(classSelClassJListModel);
								}
							}
							{
								JPanel config = new JPanel(new GridLayout(3,1,cellPadding,cellPadding));
								sel.add(config,BorderLayout.SOUTH);
								{
									classSelClassArgsJLabel = new JLabel();
									config.add(classSelClassArgsJLabel);
									classSelClassArgsJLabel.setText("Classifier Arguments");
									classSelClassArgsJLabel.setFont(defaultLabelFont);
								}
								{
									classSelClassArgsJTextField = new JTextField();
									config.add(classSelClassArgsJTextField);
								}
								{
									classRemoveJButton = new JButton();
									config.add(classRemoveJButton);
									classRemoveJButton.setText("Remove");
								}
							}
						}
					}
					
					{
						// classifier description
						// ======================
						
						JPanel bottom = new JPanel(new BorderLayout(cellPadding,cellPadding));
						center.add(bottom);
						{
							classDescJLabel = new JLabel();
							bottom.add(classDescJLabel,BorderLayout.NORTH);
							classDescJLabel.setText("Classifier Description");
							classDescJLabel.setFont(defaultLabelFont);
						}
						{
							classDescJScrollPane = new JScrollPane();
							bottom.add(classDescJScrollPane,BorderLayout.CENTER);
							{
								classDescJTextPane = new JTextPane();
								classDescJTextPane.setEditable(false);
								classDescJScrollPane.setViewportView(classDescJTextPane);
							}
						}
					}
				}
				{
					// bottom toolbar buttons
					// ======================
					{
						JPanel bottomToolbar = new JPanel(new GridLayout(1,2,cellPadding,cellPadding));
						classTab.add(bottomToolbar,BorderLayout.SOUTH);
						//{
							//JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
							//bottomToolbar.add(bottomLeft);
							//classAboutJButton = new JButton("About...");
							//bottomLeft.add(classAboutJButton);
						//}
						{
							JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
							bottomToolbar.add(bottomRight,BorderLayout.SOUTH);
							{
								classBackJButton = new JButton();
								bottomRight.add(classBackJButton);
								classBackJButton.setText("Back");
							}
							{
								classNextJButton = new JButton();
								bottomRight.add(classNextJButton);
								classNextJButton.setText("Next");
							}
						}
					}
					
					
				}
				
				//editor
				/* ============
				 * Cluster tab
				 * ============
				 */
				
				//editorTab = new JPanel(new BorderLayout(cellPadding,cellPadding));
				//mainJTabbedPane.addTab("Clusters",editorTab);
				//clusterTab = new JTabbedPane();
				//getContentPane().add(clusterTab, BorderLayout.NORTH);
				//clusterTab.setPreferredSize(new java.awt.Dimension(900, 578));
				{
					theEditorScrollPane = new JScrollPane();
					mainJTabbedPane.addTab("Editor", null, theEditorScrollPane, null);
					theEditorScrollPane.setOpaque(true);
					//theScrollPane.setPreferredSize(new java.awt.Dimension(518, 369));
					theEditorScrollPane.setSize(900, 600);
					theEditorScrollPane.setPreferredSize(new java.awt.Dimension(953, 670));
					{
						
						editorTab = new JPanel(new BorderLayout(cellPadding,cellPadding));
						BorderLayout editorTabLayout = new BorderLayout();
						editorTab.setLayout(editorTabLayout);
						theEditorScrollPane.setViewportView(editorTab);
						editorTab.setPreferredSize(new java.awt.Dimension(950, 654));
						{
							editorInteractionScrollPane = new JScrollPane();
							editorTab.add(editorInteractionScrollPane, BorderLayout.SOUTH);
							editorInteractionScrollPane.setPreferredSize(new java.awt.Dimension(999, 84));
							{
								editorInteractionJPanel = new JPanel();
								BorderLayout editorInteractionJPanelLayout = new BorderLayout();
								editorInteractionJPanel.setLayout(editorInteractionJPanelLayout);
								editorInteractionScrollPane.setViewportView(editorInteractionJPanel);
								editorInteractionJPanel.setPreferredSize(new java.awt.Dimension(995, 63));
								{
									editorInteractionWestPanel = new JPanel();
									BorderLayout editorInteractionWestPanelLayout = new BorderLayout();
									editorInteractionWestPanel.setLayout(editorInteractionWestPanelLayout);
									editorInteractionJPanel.add(editorInteractionWestPanel, BorderLayout.WEST);
									editorInteractionWestPanel.setPreferredSize(new java.awt.Dimension(352, 87));
									{
										editingProgressBarLabel = new JLabel();
										editorInteractionWestPanel.add(editingProgressBarLabel, BorderLayout.CENTER);
										editingProgressBarLabel.setText("Progress...");
									}
									{
										editorProgressBar = new JProgressBar();
										editorInteractionWestPanel.add(editorProgressBar, BorderLayout.NORTH);
										editorProgressBar.setPreferredSize(new java.awt.Dimension(352, 20));
									}
								}
								{
									editorButtonJPanel = new JPanel();
									BorderLayout editorButtonJPanelLayout = new BorderLayout();
									editorButtonJPanel.setLayout(editorButtonJPanelLayout);
									editorInteractionJPanel.add(editorButtonJPanel, BorderLayout.CENTER);
									editorButtonJPanel.setPreferredSize(new java.awt.Dimension(640, 57));
									{
										editorBottomRowButtonPanel = new JPanel();
										editorButtonJPanel.add(editorBottomRowButtonPanel, BorderLayout.SOUTH);
										editorBottomRowButtonPanel.setPreferredSize(new java.awt.Dimension(643, 34));
										{
											dictButton = new JButton();
											editorBottomRowButtonPanel.add(dictButton);
											dictButton.setText("Phrase and Word Synonym Dictionary");
										}
										{
											saveButton = new JButton();
											editorBottomRowButtonPanel.add(saveButton);
											saveButton.setText("Save...");
										}
										{
											editorRowTwoButtonBufferPanel = new JPanel();
											editorBottomRowButtonPanel.add(editorRowTwoButtonBufferPanel);
											editorRowTwoButtonBufferPanel.setPreferredSize(new java.awt.Dimension(170, 9));
										}
										{
											exitButton = new JButton();
											editorBottomRowButtonPanel.add(exitButton);
											exitButton.setText("Close");
											exitButton.setPreferredSize(new java.awt.Dimension(81, 27));
										}
									}
									{
										editorTopRowButtonsPanel = new JPanel();
										editorButtonJPanel.add(editorTopRowButtonsPanel, BorderLayout.NORTH);
										editorTopRowButtonsPanel.setPreferredSize(new java.awt.Dimension(647, 36));
										{
											lastSentenceButton = new JButton();
											editorTopRowButtonsPanel.add(lastSentenceButton);
											lastSentenceButton.setText("Last Sentence");
										}
										{
											refreshButtonEditor = new JButton();
											editorTopRowButtonsPanel.add(refreshButtonEditor);
											refreshButtonEditor.setText("Refresh");
										}
										{
											nextSentenceButton = new JButton();
											editorTopRowButtonsPanel.add(nextSentenceButton);
											nextSentenceButton.setText("Next Sentence");
										}
										{
											addSentence = new JButton();
											editorTopRowButtonsPanel.add(addSentence);
											addSentence.setText("Append Next Sentence");
											addSentence.setPreferredSize(new java.awt.Dimension(146, 23));
										}
										{
											buttonBufferJPanel = new JPanel();
											editorTopRowButtonsPanel.add(buttonBufferJPanel);
											buttonBufferJPanel.setPreferredSize(new java.awt.Dimension(64, 5));
										}
										{
											processButton = new JButton();
											editorTopRowButtonsPanel.add(processButton);
											processButton.setText("Process");
											processButton.setPreferredSize(new java.awt.Dimension(118, 29));
										}
									}
								}
							}
						}
						{
							editTP = new JTabbedPane();
							editorTab.add(editTP, BorderLayout.CENTER);
							editTP.setPreferredSize(new java.awt.Dimension(634, 612));
							{
								
								EditorInnerTabSpawner eits = (new EditorInnerTabSpawner()).spawnTab();
								EditorTabDriver.eitsList.add(0,eits);
								EditorTabDriver.eits = EditorTabDriver.eitsList.get(0);
								eits.editorBox.setEnabled(false);
								editTP.addTab("Original",eits.editBoxPanel);
							}
						}
						{
							EditorInfoScrollPane = new JScrollPane();
							editorTab.add(EditorInfoScrollPane, BorderLayout.EAST);
							EditorInfoScrollPane.setPreferredSize(new java.awt.Dimension(365, 616));
							{
								editorInfoJPanel = new JPanel();
								BorderLayout editorInfoJPanelLayout = new BorderLayout();
								editorInfoJPanel.setLayout(editorInfoJPanelLayout);
								EditorInfoScrollPane.setViewportView(editorInfoJPanel);
								editorInfoJPanel.setPreferredSize(new java.awt.Dimension(326, 617));
								{
									updaterJPanel = new JPanel();
									editorInfoJPanel.add(updaterJPanel, BorderLayout.SOUTH);
									BorderLayout updaterJPanelLayout = new BorderLayout();
									updaterJPanel.setLayout(updaterJPanelLayout);
									updaterJPanel.setPreferredSize(new java.awt.Dimension(346, 156));
									{
										//	valueBoxPanel = new JPanel();
										//updaterJPanel.add(valueBoxPanel, BorderLayout.EAST);
										//valueBoxPanel.setPreferredSize(new java.awt.Dimension(131, 76));
										{
											//presentValueField = new JTextField();
											//valueBoxPanel.add(presentValueField);
											//presentValueField.setText("null");
											//presentValueField.setPreferredSize(new java.awt.Dimension(76, 27));
										}
										{
											//targetValueField = new JTextField();
											//valueBoxPanel.add(targetValueField);
											//targetValueField.setText("null");
											//targetValueField.setPreferredSize(new java.awt.Dimension(76, 27));
										}
									}
									{
										//valueLabelJPanel = new JPanel();
										//FlowLayout valueLabelJPanelLayout = new FlowLayout();
										//valueLabelJPanel.setLayout(valueLabelJPanelLayout);
										//updaterJPanel.add(valueLabelJPanel, BorderLayout.WEST);
										//valueLabelJPanel.setPreferredSize(new java.awt.Dimension(180, 76));
										{
											//presentValueLabel = new JLabel();
											//	valueLabelJPanel.add(presentValueLabel);
											//	presentValueLabel.setText("Present Value:");
										}
										{
											//dummyPanelUpdatorLeftSide = new JPanel();
											//valueLabelJPanel.add(dummyPanelUpdatorLeftSide);
											//dummyPanelUpdatorLeftSide.setPreferredSize(new java.awt.Dimension(154, 8));
										}
										{
											//targetValueLabel = new JLabel();
											//valueLabelJPanel.add(targetValueLabel);
											//targetValueLabel.setText("Target Value:");
										}
									}
									
									{
										jPanel2 = new JPanel();
										updaterJPanel.add(jPanel2, BorderLayout.NORTH);
										jPanel2.setPreferredSize(new java.awt.Dimension(346, 23));
										{
											synonymsLabel = new JLabel();
											jPanel2.add(synonymsLabel);
											synonymsLabel.setText("Synonyms of Red Words in the Current Sentence: ");
										}
										{
											//	suggestionListLabel = new JLabel();
											//jPanel2.add(suggestionListLabel);
											//suggestionListLabel.setText("Clickable Feature List");
											//suggestionListLabel.setPreferredSize(new java.awt.Dimension(146, 16));
										}
										{
											//suggestionListPane = new JScrollPane();
											//jPanel2.add(suggestionListPane);
											//	suggestionListPane.setPreferredSize(new java.awt.Dimension(315, 155));
											/*{
												TableModel suggestionTableModel = 
														new DefaultTableModel(
																new String[][] { { "One", "Two" }, { "Three", "Four" } },
																new String[] { "Column 1", "Column 2" });
												suggestionTable = new JTable();
												suggestionListPane.setViewportView(suggestionTable);
												suggestionTable.setModel(suggestionTableModel);
											}*/
										}
									}
									{
										addToSentenceScrollPane = new JScrollPane();
										updaterJPanel.add(addToSentenceScrollPane, BorderLayout.CENTER);
										addToSentenceScrollPane.setPreferredSize(new java.awt.Dimension(346, 65));
										{
											addToSentencePane = new JTextPane();
											addToSentenceScrollPane.setViewportView(addToSentencePane);
											addToSentencePane.setText("This is where the words to Add to the curent sentence will go.");
											addToSentencePane.setPreferredSize(new java.awt.Dimension(344, 95));
										}
									}
									{
										spacer2 = new JPanel();
										updaterJPanel.add(spacer2, BorderLayout.SOUTH);
										spacer2.setPreferredSize(new java.awt.Dimension(346, 32));
									}
								}
								{
									jPanel1 = new JPanel();
									editorInfoJPanel.add(jPanel1, BorderLayout.NORTH);
									jPanel1.setPreferredSize(new java.awt.Dimension(346, 463));
									{
										suggestionBoxLabelJPanel = new JPanel();
										jPanel1.add(suggestionBoxLabelJPanel);
										suggestionBoxLabelJPanel.setPreferredSize(new java.awt.Dimension(314, 22));
										{
											suggestionLabel = new JLabel();
											suggestionBoxLabelJPanel.add(suggestionLabel);
											suggestionLabel.setText("Suggestion / Instructions:");
										}
									}
									{
										suggestionPane = new JScrollPane();
										jPanel1.add(suggestionPane);
										suggestionPane.setPreferredSize(new java.awt.Dimension(317, 104));
										{
											suggestionBox = new JTextPane();
											suggestionPane.setViewportView(suggestionBox);
											suggestionBox.setText("Edit the sentence in the top window to the left by trying to rewrite it without the highlighted words. Try to add some of the 'elements to add' from the window below.\n" +
													"Things highlighted to remove include (delimited by \"|\"): | Noun, plural | | , |\n" +
															"Your percentage of letters is too low. Consider using less: | \" |");
											suggestionBox.setPreferredSize(new java.awt.Dimension(315, 114));
										}
									}
									{
										elementsToAddBoxLabelJPanel = new JPanel();
										jPanel1.add(elementsToAddBoxLabelJPanel);
										elementsToAddBoxLabelJPanel.setPreferredSize(new java.awt.Dimension(314, 24));
										{
											elementsToAddLabel = new JLabel();
											elementsToAddBoxLabelJPanel.add(elementsToAddLabel);
											elementsToAddLabel.setText("Elements to (try to) add:");
										}
										{
											jPanel4 = new JPanel();
											elementsToAddBoxLabelJPanel.add(jPanel4);
											jPanel4.setPreferredSize(new java.awt.Dimension(8, 28));
										}
										{
											jPanel5 = new JPanel();
											elementsToAddBoxLabelJPanel.add(jPanel5);
											jPanel5.setPreferredSize(new java.awt.Dimension(6, 10));
										}
										{
											jLabel1 = new JLabel();
											elementsToAddBoxLabelJPanel.add(jLabel1);
											jLabel1.setText("Elements to (try to) remove:");
													jLabel1.setPreferredSize(new java.awt.Dimension(145, 14));
										}
									}
									{
										elementsToAddScrollPane = new JScrollPane();
										jPanel1.add(elementsToAddScrollPane);
										elementsToAddScrollPane.setPreferredSize(new java.awt.Dimension(123, 281));
										elementsToAddScrollPane.setSize(306, 157);
										{
											elementsToAddPane = new JTextPane();
											elementsToAddScrollPane.setViewportView(elementsToAddPane);
											elementsToAddPane.setText("This is where the words that you want to try to add to your document will be displayed.");
													elementsToAddPane.setPreferredSize(new java.awt.Dimension(121, 271));
										}
									}
									{
										jPanel3 = new JPanel();
										jPanel1.add(jPanel3);
										jPanel3.setPreferredSize(new java.awt.Dimension(16, 140));
									}
									{
										elementsToRemoveScrollPane = new JScrollPane();
										jPanel1.add(elementsToRemoveScrollPane);
										elementsToRemoveScrollPane.setPreferredSize(new java.awt.Dimension(128, 282));
										{
											elementsToRemovePane = new JTextPane();
											elementsToRemoveScrollPane.setViewportView(elementsToRemovePane);
											
											elementsToRemovePane.setText("This is where the words that you want to try to remove from your document will be displayed.");
													elementsToRemovePane.setPreferredSize(new java.awt.Dimension(126, 278));
										}
									}
									{
										spacer1 = new JPanel();
										jPanel1.add(spacer1);
										spacer1.setPreferredSize(new java.awt.Dimension(10, 50));
									}
								}
							}
						}
					}
				}
				{
					theScrollPane = new JScrollPane();
					mainJTabbedPane.addTab("Clusters", null, theScrollPane, null);
					theScrollPane.setOpaque(true);
					//theScrollPane.setPreferredSize(new java.awt.Dimension(518, 369));
					theScrollPane.setSize(900, 600);
					{
						holderPanel = new JPanel();
						BoxLayout holderPanelLayout = new BoxLayout(holderPanel, javax.swing.BoxLayout.Y_AXIS);
						//holderPanel.setPreferredSize(new java.awt.Dimension(775,528));
						holderPanel.setAutoscrolls(true);
						holderPanel.setOpaque(true);
						theScrollPane.setViewportView(holderPanel);
						holderPanel.setLayout(holderPanelLayout);
						{
							topPanel = new JPanel();
							{
								reClusterAllButton = new JButton();
								topPanel.add(reClusterAllButton);
								reClusterAllButton.setText("find different green ovals");
								reClusterAllButton.setRolloverEnabled(true);
							}
							{
								refreshButton = new JButton();
								topPanel.add(refreshButton);
								refreshButton.setText("show me where my black dot is right now");
								refreshButton.setEnabled(true);
								refreshButton.setVisible(true);
							}
							secondPanel = new JPanel();
							holderPanel.add(secondPanel);
							holderPanel.add(topPanel);
							/*
							{
								ComboBoxModel clusterConfigurationBoxModel = 
										new DefaultComboBoxModel(
												new String[] { " ", " " });
								clusterConfigurationBox = new JComboBox();
								secondPanel.add(clusterConfigurationBox);
								clusterConfigurationBox.setModel(clusterConfigurationBoxModel);
								
								
							}
							*/
							{
								selectClusterConfiguration = new JButton();
								secondPanel.add(selectClusterConfiguration);
								selectClusterConfiguration.setText("help me move the black dots to the center of the green ovals");
							}
							
						}
						{
							LegendPanel legend = new LegendPanel();
							legend.setPreferredSize(new Dimension(800,50));
							holderPanel.add(legend);
							
							
						}
					}
				}
				}
			
				
					// bottom toolbar buttons
					// ======================
				
		

			// initialize listeners - except for EditorTabDriver!
			DocsTabDriver.initListeners(this);
			FeaturesTabDriver.initListeners(this);
			ClassTabDriver.initListeners(this);
				EditorTabDriver.initListeners(this);
			EditorTabDriver.initEditorInnerTabListeners(this);
			ClusterViewerDriver.initListeners(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JLabel getHighlightLabel() {
		return highlightLabel;
	}
	
	public JLabel getHighlightLabelx() {
		return highlightLabel;
	}
	
	public JComboBox getHighlightSelectionBox() {
		return highlightSelectionBox;
	}
	
	public JTextField getSearchInputBox() {
		return searchInputBox;
	}
	
	public JButton getLastSentenceButton() {
		return lastSentenceButton;
	}
	
	public JButton getNextSentenceButton() {
		return nextSentenceButton;
	}
	
	public JScrollPane getWordsToAddPane() {
		return wordsToAddPane;
	}
	
	public JLabel getElementsToAddLabel() {
		return elementsToAddLabel;
	}

}
