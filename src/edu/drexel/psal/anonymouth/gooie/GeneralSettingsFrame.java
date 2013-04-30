package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.jstylo.generics.*;
import edu.drexel.psal.anonymouth.gooie.DriverPreProcessTabDocuments.ExtFilter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

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
	protected JCheckBox warnQuit;
	
	//Advanced tab
	protected JPanel advanced;
	protected JLabel maxFeatures;
	protected JSlider maxFeaturesSlider;
	protected JLabel note;
	protected JLabel numOfThreads;
	protected JSlider numOfThreadsSlider;
	protected JLabel note2;
	protected JButton reset;
	
	public GeneralSettingsFrame(GUIMain main) {
		super(main, "Preferences", Dialog.ModalityType.APPLICATION_MODAL);
		init(main);
		setVisible(false);
	}
	
	private void init(final GUIMain main) {
		this.main = main;
		this.setIconImage(new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_LOGO.png")).getImage());
		initTabs();

		this.add(tabbedPane);
		this.setSize(new Dimension(500, 325));
		this.setResizable(false);
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
	}
	
	public void openWindow() {
		this.setVisible(true);
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
	}
	
	public void closeWindow() {
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
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
			
			autoSave = new JCheckBox();
			autoSave.setText("Auto-Save anonymized documents upon exit");
			if (PropertiesUtil.getAutoSave() == true)
				autoSave.setSelected(true);

			warnQuit = new JCheckBox();
			warnQuit.setText("Warn about unsaved changes upon exit");
			if (PropertiesUtil.getWarnQuit() == true)
				warnQuit.setSelected(true);

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
			general.add(warnQuit);
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
			
			note = new JLabel("Note: The recommended number is 1000 for best results");
			note.setForeground(Color.GRAY);
			
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
			
			note2 = new JLabel("Note: The recommended number of threads to use is 4");
			note2.setForeground(Color.GRAY);
			
			reset = new JButton("Reset Preferences");
			reset.setToolTipText("Reset all user preferences back to their default values");
			
			advanced.add(maxFeatures, "wrap");
			advanced.add(maxFeaturesSlider, "alignx 50%, wrap");
			advanced.add(note, "alignx 50%, wrap");
			advanced.add(numOfThreads, "wrap");
			advanced.add(numOfThreadsSlider, "alignx 50%, wrap");
			advanced.add(note2, "alignx 50%, wrap");
			
			JSeparator test = new JSeparator(JSeparator.HORIZONTAL);
			test.setPreferredSize(new Dimension(484, 30));
			advanced.add(test, "gaptop 30, alignx 50%, wrap");
			advanced.add(reset, "gaptop 10, alignx 50%");
		}

		initListeners();
		tabbedPane.add("General", general);
		tabbedPane.add("Advanced", advanced);
	}
	
	public void initListeners() {
		ActionListener classifierListener;
		ActionListener featureListener;
		ActionListener probSetListener;
		ActionListener autoSaveListener;
		ActionListener warnQuitListener;
		ChangeListener maxFeaturesListener;
		ChangeListener numOfThreadsListener;
		ActionListener resetListener;
		
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
				
				if (autoSave.isSelected())
					PropertiesUtil.setAutoSave(true);
				else
					PropertiesUtil.setAutoSave(false);
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