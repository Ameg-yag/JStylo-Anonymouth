package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.jstylo.generics.*;
import edu.drexel.psal.anonymouth.gooie.DriverPreProcessTabDocuments.ExtFilter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

/**
 * The main preferences window for Anonymouth. Stores under a different pull down menu depending on the system (if Mac, it's put under
 * Anonymouth > Preferences to keep with the Mac L&F, and if Windows/Linux it's put under Settings > Preferences.
 * @author Marc Barrowclift
 *
 */
public class GeneralSettingsFrame extends JDialog {	

	private static final long serialVersionUID = 1L;
	private final String NAME = "( "+this.getClass().getName()+" ) - ";

	protected GUIMain main;
	public boolean panelsAreMade = false;
	protected JTabbedPane tabbedPane;
	
	//General tab
	protected JPanel general;
	protected JLabel defaultClassifier;
	protected JLabel defaultFeature;
	protected JLabel defaultProbSet;
	protected JComboBox<String> classComboBox;
	protected JComboBox<String> featComboBox;
	protected JButton selectProbSet;
	protected JTextArea probSetTextPane;
	protected JScrollPane probSetScrollPane;
	protected JCheckBox autoSave;
	protected JLabel autoSaveNote;
	protected JCheckBox warnQuit;
	protected JCheckBox translations;
	
	//Advanced tab
	protected JPanel advanced;
	protected JLabel maxFeatures;
	protected JSlider maxFeaturesSlider;
	protected JLabel maxFeaturesNote;
	protected JLabel numOfThreads;
	protected JSlider numOfThreadsSlider;
	protected JLabel numOfThreadsNote;
	protected JButton reset;
	
	/**
	 * CONSTRUCTOR
	 * @param main - Instance of GUIMain
	 */
	public GeneralSettingsFrame(GUIMain main) {
		super(main, "Preferences", Dialog.ModalityType.APPLICATION_MODAL);
		init(main);
		setVisible(false);
	}
	
	/**
	 * Readies the window.
	 * @param main
	 */
	private void init(final GUIMain main) {
		this.main = main;
		this.setIconImage(new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_LOGO.png")).getImage());
		initTabs();

		this.add(tabbedPane);
		this.setSize(new Dimension(500, 350));
		this.setResizable(false);
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
	}
	
	/**
	 * Displays the window
	 */
	public void openWindow() {
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
		this.setVisible(true);
	}
	
	/**
	 * Initializes all of the panels on the tree. Must be called before the tree is created, because when the tree
	 * is initialized it selects the first leaf in the tree which causes the panel to be shown.
	 */
	private void initTabs() {
		//==========================================================================================
		//================================ Tabs Location Panel =========================================
		//==========================================================================================
		tabbedPane = new JTabbedPane();
		general = new JPanel();
		advanced = new JPanel();

		MigLayout generalLayout = new MigLayout();
		
		general.setLayout(generalLayout);
		{
			defaultClassifier = new JLabel("Set Default Classifier:");
			classComboBox = new JComboBox<String>();
			for (int i = 0; i < main.classChoice.getItemCount(); i++)
				classComboBox.addItem(main.classChoice.getItemAt(i).toString());
			classComboBox.setSelectedItem(PropertiesUtil.getClassifier());

			defaultFeature = new JLabel("Set Default Feature:");
			featComboBox = new JComboBox<String>();
			for (int i = 0; i < main.featuresSetJComboBox.getItemCount(); i++)
				featComboBox.addItem(main.featuresSetJComboBox.getItemAt(i).toString());
			featComboBox.setSelectedItem(PropertiesUtil.getFeature());

			defaultProbSet = new JLabel("Set Default Problem Set:");
			selectProbSet = new JButton("Select");
			probSetTextPane = new JTextArea();
			probSetTextPane.setEditable(false);
			probSetTextPane.setText(PropertiesUtil.getProbSet());
			probSetTextPane.setWrapStyleWord(false);
			probSetScrollPane = new JScrollPane(probSetTextPane);
			probSetScrollPane.setPreferredSize(new Dimension(420, 20));
			probSetScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			probSetScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			warnQuit = new JCheckBox();
			warnQuit.setText("Warn about unsaved changes upon exit");
			if (PropertiesUtil.getWarnQuit())
				warnQuit.setSelected(true);
			
			autoSave = new JCheckBox();
			autoSave.setText("Auto-Save anonymized documents upon exit");
			if (PropertiesUtil.getAutoSave()) {
				autoSave.setSelected(true);
				warnQuit.setEnabled(false);
			}
			
			translations = new JCheckBox();
			translations.setText("Send sentences to Microsoft Bing© for translation");
			if (PropertiesUtil.getDoTranslations())
				translations.setSelected(true);
			
			autoSaveNote = new JLabel("Note: Will overwrite working document with changes.");
			autoSaveNote.setForeground(Color.GRAY);

			general.add(defaultClassifier, "wrap");
			general.add(classComboBox, "wrap");
			general.add(defaultFeature, "wrap");
			general.add(featComboBox, "wrap");
			general.add(defaultProbSet, "wrap");
			general.add(selectProbSet, "split 2");
			general.add(probSetScrollPane, "wrap");
			
			JSeparator test = new JSeparator(JSeparator.HORIZONTAL);
			test.setPreferredSize(new Dimension(484, 15));
			general.add(test, "alignx 50%, wrap");
			general.add(autoSave, "wrap");
			general.add(autoSaveNote, "alignx 50%, wrap");
			general.add(warnQuit, "wrap");
			general.add(translations);
		}
		
		MigLayout advancedLayout = new MigLayout();
		advanced.setLayout(advancedLayout);
		{
			maxFeatures = new JLabel("Maximum Features Used = " + PropertiesUtil.getMaximumFeatures());
			maxFeaturesSlider = new JSlider();
			maxFeaturesSlider.setPreferredSize(new Dimension(300, 20));
			maxFeaturesSlider.setMajorTickSpacing(1);
			maxFeaturesSlider.setMinorTickSpacing(1);
			maxFeaturesSlider.setMaximum(1000);
			maxFeaturesSlider.setMinimum(200);
			maxFeaturesSlider.setSnapToTicks(true);
			maxFeaturesSlider.setValue(PropertiesUtil.getMaximumFeatures());
			maxFeaturesSlider.setOrientation(SwingConstants.HORIZONTAL);
			
			maxFeaturesNote = new JLabel("Note: The recommended number is 1000 for best results");
			maxFeaturesNote.setForeground(Color.GRAY);
			
			numOfThreads = new JLabel("Number of Threads for Features Extraction = " + PropertiesUtil.getThreadCount());
			
			numOfThreadsSlider = new JSlider();
			numOfThreadsSlider.setPreferredSize(new Dimension(300, 20));
			numOfThreadsSlider.setMajorTickSpacing(1);
			numOfThreadsSlider.setMaximum(8);
			numOfThreadsSlider.setMinimum(1);
			numOfThreadsSlider.setMinorTickSpacing(1);
			numOfThreadsSlider.setOrientation(SwingConstants.HORIZONTAL);
			numOfThreadsSlider.setSnapToTicks(true);
			numOfThreadsSlider.setValue(PropertiesUtil.getThreadCount());
			
			numOfThreadsNote = new JLabel("Note: The recommended number of threads to use is 4");
			numOfThreadsNote.setForeground(Color.GRAY);
			
			reset = new JButton("Reset Preferences");
			reset.setToolTipText("Reset all user preferences back to their default values");
			
			advanced.add(maxFeatures, "wrap");
			advanced.add(maxFeaturesSlider, "alignx 50%, wrap");
			advanced.add(maxFeaturesNote, "alignx 50%, wrap");
			advanced.add(numOfThreads, "wrap");
			advanced.add(numOfThreadsSlider, "alignx 50%, wrap");
			advanced.add(numOfThreadsNote, "alignx 50%, wrap");
			
			JSeparator test = new JSeparator(JSeparator.HORIZONTAL);
			test.setPreferredSize(new Dimension(484, 30));
			advanced.add(test, "gaptop 30, alignx 50%, wrap");
			advanced.add(reset, "gaptop 10, alignx 50%");
		}

		initListeners();
		tabbedPane.add("General", general);
		tabbedPane.add("Advanced", advanced);
	}
	
	/**
	 * Initializes all the listeners needed for each tab of the preferences window.
	 */
	public void initListeners() {
		ActionListener classifierListener;
		ActionListener featureListener;
		ActionListener probSetListener;
		ActionListener autoSaveListener;
		ActionListener warnQuitListener;
		ChangeListener maxFeaturesListener;
		ChangeListener numOfThreadsListener;
		ActionListener resetListener;
		ActionListener translationsListener;
		
		classifierListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PropertiesUtil.setClassifier(classComboBox.getSelectedItem().toString());
			}
		};
		classComboBox.addActionListener(classifierListener);
		
		featureListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PropertiesUtil.setFeature(featComboBox.getSelectedItem().toString());
			}
		};
		featComboBox.addActionListener(featureListener);
		
		probSetListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Logger.logln(NAME+"'Select' Problem Set button clicked on the Preferences window");

					int answer = 0;
					
					PropertiesUtil.load.addChoosableFileFilter(new ExtFilter("XML files (*.xml)", "xml"));
					if (PropertiesUtil.getProbSet() != null) {
						String absPath = PropertiesUtil.propFile.getAbsolutePath();
						String problemSetDir = absPath.substring(0, absPath.indexOf("anonymouth_prop")-1) + "\\problem_sets\\";
						PropertiesUtil.load.setCurrentDirectory(new File(problemSetDir));
						PropertiesUtil.load.setSelectedFile(new File(PropertiesUtil.prop.getProperty("recentProbSet")));
					}
					
					answer = PropertiesUtil.load.showDialog(main, "Load Problem Set");

					if (answer == JFileChooser.APPROVE_OPTION) {
						String path = PropertiesUtil.load.getSelectedFile().getAbsolutePath();
						PropertiesUtil.setProbSet(path);
						
						probSetTextPane.setText(path);
					} else {
						Logger.logln(NAME+"Set default problem set canceled");
					}
				} catch (NullPointerException arg)
				{
					arg.printStackTrace();
				}
			}
		};
		selectProbSet.addActionListener(probSetListener);
		
		autoSaveListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln(NAME+"Auto-save checkbox clicked");
				
				if (autoSave.isSelected()) {
					PropertiesUtil.setAutoSave(true);
					warnQuit.setSelected(false);
					PropertiesUtil.setWarnQuit(false);
					warnQuit.setEnabled(false);
				} else {
					PropertiesUtil.setAutoSave(false);
					warnQuit.setEnabled(true);
				}
			}
		};
		autoSave.addActionListener(autoSaveListener);
		
		warnQuitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln(NAME+"Warn on quit checkbox clicked");
				
				if (warnQuit.isSelected())
					PropertiesUtil.setWarnQuit(true);
				else
					PropertiesUtil.setWarnQuit(false);
			}
		};
		warnQuit.addActionListener(warnQuitListener);
		
		translationsListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln(NAME+"Translations checkbox clicked");
				
				if (translations.isSelected()) {
					PropertiesUtil.setDoTranslations(true);
					
					if (BackendInterface.processed)
						main.notTranslated.setText("You have turned translations back on.\n\nPlease re-process the document to recieve translations.");
					else
						main.notTranslated.setText("Please process your document to recieve translation suggestions.");
				} else {
					PropertiesUtil.setDoTranslations(false);
					main.notTranslated.setText("You have turned translations off.");
					main.translationsHolderPanel.add(main.notTranslated, "");
				}
			}
		};
		translations.addActionListener(translationsListener);
		
		maxFeaturesListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				PropertiesUtil.setMaximumFeatures(maxFeaturesSlider.getValue());
				maxFeatures.setText("Maximum Features Used = " + PropertiesUtil.getMaximumFeatures());
			}	
		};
		maxFeaturesSlider.addChangeListener(maxFeaturesListener);
		
		numOfThreadsListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				PropertiesUtil.setThreadCount(numOfThreadsSlider.getValue());
				numOfThreads.setText("Number of Threads for Features Extraction = " + PropertiesUtil.getThreadCount());
			}
		};
		numOfThreadsSlider.addChangeListener(numOfThreadsListener);
		
		resetListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Logger.logln(NAME+"Reset button clicked");
				
				int answer = 0;
				
				answer = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to reset all preferences?\nThis will override your changes.",
						"Reset Preferences",
						JOptionPane.WARNING_MESSAGE,
						JOptionPane.YES_NO_CANCEL_OPTION);
				
				if (answer == 0) {
					try {
						Logger.logln(NAME+"Reset progressing...");
						PropertiesUtil.reset();
						numOfThreadsSlider.setValue(PropertiesUtil.getThreadCount());
						maxFeaturesSlider.setValue(PropertiesUtil.getMaximumFeatures());
						warnQuit.setSelected(PropertiesUtil.getWarnQuit());
						autoSave.setSelected(PropertiesUtil.getAutoSave());
						probSetTextPane.setText(PropertiesUtil.getProbSet());
						featComboBox.setSelectedItem(PropertiesUtil.getFeature());
						classComboBox.setSelectedItem(PropertiesUtil.getClassifier());
						Logger.logln(NAME+"Reset complete");
					} catch (Exception e) {
						Logger.logln(NAME+"Error occurred during reset");
					}
				} else {
					Logger.logln(NAME+"User cancelled reset");
				}
			}
		};
		reset.addActionListener(resetListener);
	}
}