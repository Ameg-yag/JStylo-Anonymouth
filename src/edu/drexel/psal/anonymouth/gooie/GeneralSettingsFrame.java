package edu.drexel.psal.anonymouth.gooie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.*;
import javax.swing.tree.*;

import net.miginfocom.swing.MigLayout;

import com.jgaap.generics.Document;

import weka.classifiers.*;

import edu.drexel.psal.jstylo.analyzers.WekaAnalyzer;

public class GeneralSettingsFrame extends JDialog {	
	
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
	protected JTextPane probSetTextPane;
	protected JCheckBox autoSave;
	protected JCheckBox warnQuit;
	
	public GeneralSettingsFrame(GUIMain main)
	{
		super(main, "General Settings", Dialog.ModalityType.APPLICATION_MODAL);
		init(main);
		setVisible(false);
	}
	
	private void init(final GUIMain main)
	{
		this.main = main;
		this.setIconImage(new ImageIcon(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_LOGO.png")).getImage());
		initTabs();

		this.add(tabbedPane);
		this.setSize(new Dimension(500, 500));
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
	}
	
	public void openWindow()
	{
		this.setVisible(true);
		this.setLocationRelativeTo(null); // makes it form in the center of the screen

//		prevPreprocessLocation = PropertiesUtil.getPreProcessTabLocation();
//		prevSuggestionsLocation = PropertiesUtil.getSuggestionsTabLocation();
//		prevTranslationsLocation = PropertiesUtil.getTranslationsTabLocation();
//		prevClustersLocation = PropertiesUtil.getAnonymityTabLocation();
	}
	
	public void closeWindow() {
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	private void showPanel(JPanel panel) {
//		mainPanel.removeAll();
//		mainPanel.add(panel);
//		mainPanel.revalidate();
//		mainPanel.repaint();
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
		MigLayout tabLocationsLayout = new MigLayout(
				"wrap",
				"grow, fill",
				"[30][grow, fill]");
		general.setLayout(tabLocationsLayout);
		{	

			JPanel generalMainPanel = new JPanel();
			generalMainPanel.setLayout(new MigLayout());
			{
				defaultClassifier = new JLabel("Set Default Classifier");
				classComboBox = new JComboBox<String>();
				for (int i = 0; i < main.classChoice.getItemCount(); i++)
					classComboBox.addItem(main.classChoice.getItemAt(i).toString());
				classComboBox.setSelectedItem(PropertiesUtil.getClassifier());
				
				defaultFeature = new JLabel("Set Default Feature");
				featComboBox = new JComboBox<String>();
				for (int i = 0; i < main.featuresSetJComboBox.getItemCount(); i++)
					featComboBox.addItem(main.featuresSetJComboBox.getItemAt(i).toString());
				featComboBox.setSelectedItem(PropertiesUtil.getFeature());
				
				defaultProbSet = new JLabel("Set Default Problem Set");
				selectProbSet = new JButton("Select");
				probSetTextPane = new JTextPane();
				probSetTextPane.setEditable(false);
				probSetTextPane.setText(PropertiesUtil.getProbSet());
				
				autoSave = new JCheckBox();
				autoSave.setText("Auto-Save anonymized documents upon exit");
				
				warnQuit = new JCheckBox();
				warnQuit.setText("Warn about unsaved changes upon exit");
				
				generalMainPanel.add(defaultClassifier, "wrap");
				generalMainPanel.add(classComboBox, "wrap");
				generalMainPanel.add(defaultFeature, "wrap");
				generalMainPanel.add(featComboBox, "wrap");
				generalMainPanel.add(defaultProbSet, "wrap");
				generalMainPanel.add(selectProbSet);
				generalMainPanel.add(probSetTextPane, "wrap");
				generalMainPanel.add(autoSave, "wrap");
				generalMainPanel.add(warnQuit, "wrap");
				
			}
			general.add(generalMainPanel);

//			initListeners();
		}
		
		tabbedPane.add(general);
	}
}
	
//	public static void initListeners()
//	{
//		ActionListener documentsAL;
//		ActionListener resultsAL;
//		ActionListener preprocessAL;
//		ActionListener suggestionsAL;
//		ActionListener translationsAL;
//		ActionListener clustersAL;
//		
//		documentsAL = new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				String item = (String)documentsLocationComboBox.getSelectedItem();
//				if (item == "Left")
//					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.LEFT);
//				if (item == "Top")
//					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.TOP);
//				if (item == "Right")
//					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.RIGHT);
//				if (item == "Bottom")
//					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.BOTTOM);
//			}
//		};
//		documentsLocationComboBox.addActionListener(documentsAL);
//		
//		resultsAL = new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				String item = (String)resultsLocationComboBox.getSelectedItem();
//				if (item == "Left")
//					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.LEFT);
//				if (item == "Top")
//					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.TOP);
//				if (item == "Right")
//					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.RIGHT);
//				if (item == "Bottom")
//					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.BOTTOM);
//			}
//		};
//		resultsLocationComboBox.addActionListener(resultsAL);
//		
//		preprocessAL = new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				String item = (String)preprocessLocationComboBox.getSelectedItem();
//				if (item == "Left")
//					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.LEFT);
//				if (item == "Top")
//					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.TOP);
//				if (item == "Right")
//					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.RIGHT);
//				if (item == "Bottom")
//					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.BOTTOM);
//			}
//		};
//		preprocessLocationComboBox.addActionListener(preprocessAL);
//		
//		suggestionsAL = new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				String item = (String)suggestionsLocationComboBox.getSelectedItem();
//				if (item == "Left")
//					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.LEFT);
//				if (item == "Top")
//					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.TOP);
//				if (item == "Right")
//					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.RIGHT);
//				if (item == "Bottom")
//					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.BOTTOM);
//			}
//		};
//		suggestionsLocationComboBox.addActionListener(suggestionsAL);
//		
//		translationsAL = new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				String item = (String)translationsLocationComboBox.getSelectedItem();
//				if (item == "Left")
//					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.LEFT);
//				if (item == "Top")
//					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.TOP);
//				if (item == "Right")
//					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.RIGHT);
//				if (item == "Bottom")
//					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.BOTTOM);
//			}
//		};
//		translationsLocationComboBox.addActionListener(translationsAL);
//		
//		clustersAL = new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				String item = (String)clustersLocationComboBox.getSelectedItem();
//				if (item == "Left")
//					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.LEFT);
//				if (item == "Top")
//					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.TOP);
//				if (item == "Right")
//					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.RIGHT);
//				if (item == "Bottom")
//					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.BOTTOM);
//			}
//		};
//		clustersLocationComboBox.addActionListener(clustersAL);
//	}
//}