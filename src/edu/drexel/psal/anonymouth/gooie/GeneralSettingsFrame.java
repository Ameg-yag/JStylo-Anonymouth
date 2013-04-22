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
	
	protected JSplitPane splitPane;
	protected JScrollPane treeScrollPane;
	protected JScrollPane mainScrollPane;
	protected JScrollPane bottomScrollPane;
	
	protected JPanel treePanel;
	protected JTree tree;
	protected DefaultMutableTreeNode top;
	
	protected JPanel mainPanel;
	
	protected JPanel tabLocationsPanel;
	protected JPanel tabLocationsMainPanel;
	protected static JComboBox documentsLocationComboBox;
	protected static JComboBox resultsLocationComboBox;
	protected static JComboBox preprocessLocationComboBox;
	protected static JComboBox suggestionsLocationComboBox;
	protected static JComboBox translationsLocationComboBox;
	protected static JComboBox clustersLocationComboBox;
	protected DefaultComboBoxModel documentsLocationComboBoxModel;
	protected DefaultComboBoxModel resultsLocationComboBoxModel;
	protected DefaultComboBoxModel preprocessLocationComboBoxModel;
	protected DefaultComboBoxModel suggestionsLocationComboBoxModel;
	protected DefaultComboBoxModel translationsLocationComboBoxModel;
	protected DefaultComboBoxModel clustersLocationComboBoxModel;
	
	protected JPanel bottomPanel;
	protected JButton okButton;
	protected JButton cancelButton;
	
	// all "previous" variables which hold the values of variables when the settings frame was opened
	protected PropertiesUtil.Location prevDocumentsLocation;
	protected PropertiesUtil.Location prevResultsLocation;
	protected PropertiesUtil.Location prevPreprocessLocation;
	protected PropertiesUtil.Location prevSuggestionsLocation;
	protected PropertiesUtil.Location prevTranslationsLocation;
	protected PropertiesUtil.Location prevClustersLocation;
	
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
		initPanels();
		getContentPane().setLayout(new MigLayout(
				"fill, wrap 1, ins 0, gap 0 0",
				"fill, grow",
				"[grow][grow, shrink 0, 40!]"));
		{
			// main panel must be created before the tree panel, but added to content pane after the tree panel
			// when tree is initialized it selects the first leaf and needs the main panel to be created
			mainPanel = new JPanel();
			mainPanel.setLayout(new MigLayout(
					"fill, wrap 1, ins 0, gap 0 0",
					"fill",
					"fill"));
			mainPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
			
			treePanel = new JPanel();
			treePanel.setLayout(new MigLayout(
					"",
					"fill",
					"fill"));
			treePanel.setBackground(Color.WHITE);
			treeScrollPane = new JScrollPane(treePanel);
			{
				top = new DefaultMutableTreeNode("Pre-Process");
				tree = new JTree(top);
				initializeTree(tree, top);
			}
			treePanel.add(tree);
			
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new MigLayout(
					"right",
					"right",
					"bottom"));
			bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
			{
				okButton = new JButton("Ok");
				okButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						try {
							main.setUpContentPane();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						closeWindow();
					}
				});
				bottomPanel.add(okButton);
				
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						PropertiesUtil.setPreProcessTabLocation(prevPreprocessLocation);
						PropertiesUtil.setSuggestionsTabLocation(prevSuggestionsLocation);
						PropertiesUtil.setTranslationsTabLocation(prevTranslationsLocation);
						PropertiesUtil.setAnonymityTabLocation(prevClustersLocation);
						
						closeWindow();
					}
				});
				bottomPanel.add(cancelButton);
			}
			getContentPane().add(treePanel, "split 2, growy, shrinkx 0");
			getContentPane().add(mainPanel, "grow");
			getContentPane().add(bottomPanel, "h 40!, span 2, shrinky 0");
		}
		
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(new Dimension((int)(screensize.width*.9), (int)(screensize.height*.9)));
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
	}
	
	private void initializeTree(JTree tree, DefaultMutableTreeNode top) 
	{
	    DefaultMutableTreeNode section = null;
	    DefaultMutableTreeNode subSection = null;
	    
	    section = new DefaultMutableTreeNode("Tabs");
	    subSection = new DefaultMutableTreeNode("Location");
	    top.add(section);
	    section.add(subSection);
	    
	    TreeSelectionListener treeListener = new TreeSelectionListener()
    	{
			@Override
			public void valueChanged(TreeSelectionEvent e) 
			{
				String name = e.getPath().getLastPathComponent().toString();
				if (name.equals("Location"))
				{
					showPanel(tabLocationsPanel);
				}
				else
				{}
			}
		};
		
		tree.addTreeSelectionListener(treeListener);
		tree.expandRow(0);
		//tree.setSelectionRow(1);
	}
	
	public void openWindow()
	{
		this.setVisible(true);
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
		
		prevDocumentsLocation = PropertiesUtil.Location.TOP;
		prevResultsLocation = PropertiesUtil.Location.BOTTOM;
		prevPreprocessLocation = PropertiesUtil.getPreProcessTabLocation();
		prevSuggestionsLocation = PropertiesUtil.getSuggestionsTabLocation();
		prevTranslationsLocation = PropertiesUtil.getTranslationsTabLocation();
		prevClustersLocation = PropertiesUtil.getAnonymityTabLocation();
	}
	
	public void closeWindow() 
	{
		//main.setEnabled(true);
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	private void showPanel(JPanel panel)
	{
//		if (docPanel == null || featPanel == null || classPanel == null)
//			makePanels();
		mainPanel.removeAll();
		mainPanel.add(panel);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	/**
	 * Initializes all of the panels on the tree. Must be called before the tree is created, because when the tree
	 * is initialized it selects the first leaf in the tree which causes the panel to be shown.
	 */
	private void initPanels()
	{
		//==========================================================================================
		//================================ Tabs Location Panel =========================================
		//==========================================================================================
		tabLocationsPanel = new JPanel();
		
		MigLayout tabLocationsLayout = new MigLayout(
				"wrap",
				"grow, fill",
				"[30][grow, fill]");
		tabLocationsPanel.setLayout(tabLocationsLayout);
		{
			// Documents Label-----------------------------
			JLabel tabLocationsLabel = new JLabel("Tab Locations:");
			tabLocationsLabel.setFont(new Font("Ariel", Font.BOLD, 15));
			tabLocationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
			tabLocationsLabel.setBorder(BorderFactory.createRaisedBevelBorder());
			tabLocationsLabel.setOpaque(true);
			tabLocationsLabel.setBackground(main.tan);
			
			tabLocationsMainPanel = new JPanel();
			tabLocationsMainPanel.setLayout(new MigLayout(
					"wrap",
					"[150]",
					"20"));
			{
				JPanel docMainTopPanel = new JPanel();
				docMainTopPanel.setLayout(new MigLayout());
				{
					JLabel documentsLocationLabel = new JLabel("Documents Tab:");
					documentsLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
					documentsLocationLabel.setBorder(BorderFactory.createRaisedBevelBorder());
					documentsLocationLabel.setOpaque(true);
					documentsLocationLabel.setBackground(main.tan);
					documentsLocationComboBoxModel = new DefaultComboBoxModel(new String[]{"Top"});
					documentsLocationComboBox = new JComboBox(documentsLocationComboBoxModel);
					
					JLabel resultsLocationLabel = new JLabel("Results Tab:");
					resultsLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
					resultsLocationLabel.setBorder(BorderFactory.createRaisedBevelBorder());
					resultsLocationLabel.setOpaque(true);
					resultsLocationLabel.setBackground(main.tan);
					resultsLocationComboBoxModel = new DefaultComboBoxModel(new String[]{"Left", "Right", "Bottom"});
					resultsLocationComboBox = new JComboBox(resultsLocationComboBoxModel);
					
					JLabel preprocessLocationLabel = new JLabel("Pre-Process Tab:");
					preprocessLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
					preprocessLocationLabel.setBorder(BorderFactory.createRaisedBevelBorder());
					preprocessLocationLabel.setOpaque(true);
					preprocessLocationLabel.setBackground(main.tan);
					preprocessLocationComboBoxModel = new DefaultComboBoxModel(new String[]{"Left", "Right"});
					preprocessLocationComboBox = new JComboBox(preprocessLocationComboBoxModel);
					
					JLabel suggestionsLocationLabel = new JLabel("Suggestions Tab:");
					suggestionsLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
					suggestionsLocationLabel.setBorder(BorderFactory.createRaisedBevelBorder());
					suggestionsLocationLabel.setOpaque(true);
					suggestionsLocationLabel.setBackground(main.tan);
					suggestionsLocationComboBoxModel = new DefaultComboBoxModel(new String[]{"Left", "Right"});
					suggestionsLocationComboBox = new JComboBox(suggestionsLocationComboBoxModel);
					
					JLabel translationsLocationLabel = new JLabel("Translations Tab:");
					translationsLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
					translationsLocationLabel.setBorder(BorderFactory.createRaisedBevelBorder());
					translationsLocationLabel.setOpaque(true);
					translationsLocationLabel.setBackground(main.tan);
					translationsLocationComboBoxModel = new DefaultComboBoxModel(new String[]{"Left", "Right"});
					translationsLocationComboBox = new JComboBox(translationsLocationComboBoxModel);
					
					JLabel clustersLocationLabel = new JLabel("Clusters Tab:");
					clustersLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
					clustersLocationLabel.setBorder(BorderFactory.createRaisedBevelBorder());
					clustersLocationLabel.setOpaque(true);
					clustersLocationLabel.setBackground(main.tan);
					clustersLocationComboBoxModel = new DefaultComboBoxModel(new String[]{"Left", "Top", "Right"});
					clustersLocationComboBox = new JComboBox(clustersLocationComboBoxModel);
					
					tabLocationsMainPanel.add(documentsLocationLabel, "grow");
					tabLocationsMainPanel.add(documentsLocationComboBox, "gaptop 0, grow");
					tabLocationsMainPanel.add(resultsLocationLabel, "grow");
					tabLocationsMainPanel.add(resultsLocationComboBox, "gaptop 0, grow");
					tabLocationsMainPanel.add(preprocessLocationLabel, "grow");
					tabLocationsMainPanel.add(preprocessLocationComboBox, "gaptop 0, grow");
					tabLocationsMainPanel.add(suggestionsLocationLabel, "grow");
					tabLocationsMainPanel.add(suggestionsLocationComboBox, "gaptop 0, grow");
					tabLocationsMainPanel.add(translationsLocationLabel, "grow");
					tabLocationsMainPanel.add(translationsLocationComboBox, "gaptop 0, grow");
					tabLocationsMainPanel.add(clustersLocationLabel, "grow");
					tabLocationsMainPanel.add(clustersLocationComboBox, "gaptop 0, grow");
				}
			}
			tabLocationsPanel.add(tabLocationsLabel, "h 30!");
			tabLocationsPanel.add(tabLocationsMainPanel);
			
			initListeners();
		}
	}
	
	public static void initListeners()
	{
		ActionListener documentsAL;
		ActionListener resultsAL;
		ActionListener preprocessAL;
		ActionListener suggestionsAL;
		ActionListener translationsAL;
		ActionListener clustersAL;
		
		documentsAL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String item = (String)documentsLocationComboBox.getSelectedItem();
				if (item == "Left")
					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.LEFT);
				if (item == "Top")
					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.TOP);
				if (item == "Right")
					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.RIGHT);
				if (item == "Bottom")
					PropertiesUtil.setDocumentsTabLocation(PropertiesUtil.Location.BOTTOM);
			}
		};
		documentsLocationComboBox.addActionListener(documentsAL);
		
		resultsAL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String item = (String)resultsLocationComboBox.getSelectedItem();
				if (item == "Left")
					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.LEFT);
				if (item == "Top")
					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.TOP);
				if (item == "Right")
					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.RIGHT);
				if (item == "Bottom")
					PropertiesUtil.setResultsTabLocation(PropertiesUtil.Location.BOTTOM);
			}
		};
		resultsLocationComboBox.addActionListener(resultsAL);
		
		preprocessAL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String item = (String)preprocessLocationComboBox.getSelectedItem();
				if (item == "Left")
					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.LEFT);
				if (item == "Top")
					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.TOP);
				if (item == "Right")
					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.RIGHT);
				if (item == "Bottom")
					PropertiesUtil.setPreProcessTabLocation(PropertiesUtil.Location.BOTTOM);
			}
		};
		preprocessLocationComboBox.addActionListener(preprocessAL);
		
		suggestionsAL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String item = (String)suggestionsLocationComboBox.getSelectedItem();
				if (item == "Left")
					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.LEFT);
				if (item == "Top")
					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.TOP);
				if (item == "Right")
					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.RIGHT);
				if (item == "Bottom")
					PropertiesUtil.setSuggestionsTabLocation(PropertiesUtil.Location.BOTTOM);
			}
		};
		suggestionsLocationComboBox.addActionListener(suggestionsAL);
		
		translationsAL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String item = (String)translationsLocationComboBox.getSelectedItem();
				if (item == "Left")
					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.LEFT);
				if (item == "Top")
					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.TOP);
				if (item == "Right")
					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.RIGHT);
				if (item == "Bottom")
					PropertiesUtil.setTranslationsTabLocation(PropertiesUtil.Location.BOTTOM);
			}
		};
		translationsLocationComboBox.addActionListener(translationsAL);
		
		clustersAL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String item = (String)clustersLocationComboBox.getSelectedItem();
				if (item == "Left")
					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.LEFT);
				if (item == "Top")
					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.TOP);
				if (item == "Right")
					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.RIGHT);
				if (item == "Bottom")
					PropertiesUtil.setAnonymityTabLocation(PropertiesUtil.Location.BOTTOM);
			}
		};
		clustersLocationComboBox.addActionListener(clustersAL);
	}
}