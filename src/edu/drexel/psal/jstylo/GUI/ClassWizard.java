package edu.drexel.psal.jstylo.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import edu.drexel.psal.jstylo.generics.Analyzer;
import edu.drexel.psal.jstylo.generics.Logger;

public class ClassWizard extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	protected Font defaultLabelFont = new Font("Verdana",0,14); 
	protected static int cellPadding = 5;
	protected static Border defaultBorder = BorderFactory.createLineBorder(Color.BLACK);
	
	//data
	protected Analyzer tmpAnalyzer;
	protected GUIMain parent;
	protected String[] options;
	protected String[] optionsDesc;
	protected String analyzerDesc;
	protected String analyzerName;
	
	//panels
	protected JPanel mainPanel;
	protected JPanel summaryPanel; //TODO add this. put instructions as to what this window is and what information it displays
	protected JPanel descriptionPanel;
	protected JPanel optionsPanel;
	protected JPanel buttonPanel;
	
	//description panel
	protected JTextArea descriptionJTextArea;
	protected JScrollPane descriptionJScrollPane; 
	
	//optionsPanel
	protected ArrayList<JTextField> optionFields;
	protected JScrollPane optionsJScrollPane;
	/*
	 * To be dynamically generated based on the size/number of arguments.
	 * for each argument there will be either a JLabel or JTextArea description, and a JTextfield in which to edit the arg
	 */
	
	//button panels
	protected JButton applyJButton;
	protected JButton cancelJButton;
	
	public ClassWizard(GUIMain parent,Analyzer tmpAnalyzer){
		super(tmpAnalyzer.getName());
		this.parent = parent;
		this.tmpAnalyzer = tmpAnalyzer;
		analyzerName = this.tmpAnalyzer.getName();
		analyzerDesc = this.tmpAnalyzer.analyzerDescription();
		options = this.tmpAnalyzer.getOptions();
		optionsDesc = this.tmpAnalyzer.optionsDescription();
		optionFields = new ArrayList<JTextField>();
		initGUI();
		
	}
	
	protected void initGUI(){
		setPreferredSize(new Dimension(600,800));
		
		
		try{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			{
				
				mainPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
				mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
				add(mainPanel);
				
				{
					// =================
					// Description panel
					// =================
					descriptionPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
					descriptionPanel.setPreferredSize(new Dimension(550,300));
					descriptionPanel.setBorder(defaultBorder);
					descriptionJScrollPane = new JScrollPane(descriptionPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

					if (options==null) //if there are no options, make the description the main focus of the window
						mainPanel.add(descriptionJScrollPane,BorderLayout.CENTER);
					else //otherwise the description is delegated to the top of the window
						mainPanel.add(descriptionJScrollPane,BorderLayout.NORTH);
					
					{
						descriptionJTextArea = new JTextArea();
						descriptionJTextArea.setText(analyzerDesc);
						descriptionJTextArea.setEditable(false);
						descriptionJTextArea.setLineWrap(true);
						descriptionJTextArea.setWrapStyleWord(true);
						descriptionJTextArea.setBorder(defaultBorder);
						descriptionJTextArea.setFont(defaultLabelFont);
						descriptionJTextArea.setPreferredSize(new Dimension(500,300));
						descriptionPanel.add(descriptionJTextArea);
					}
				}
				{
					// =============
					// Options panel
					// =============
					
						
					optionsPanel = new JPanel(new GridLayout(0,1,0,0));
					optionsPanel.setBorder(defaultBorder);
					optionsJScrollPane = new JScrollPane(optionsPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					
					if (options!=null){
						optionsPanel.setPreferredSize(new Dimension(500,400));
						mainPanel.add(optionsJScrollPane,BorderLayout.CENTER);
						
						//loop through options, adding a new option-description pair for each one
						for (int i=0; i<options.length/2;i++){
							
							JTextField tempLabel = new JTextField(optionsDesc[i].trim().replaceAll("\\s+", " "));
							tempLabel.setPreferredSize(new Dimension(25,550));
							tempLabel.setEditable(false);
							
							JTextField tempField = new JTextField(options[2*i+1]);
							Logger.logln(" option i: "+i+" = "+options[2*i+1]+" flag: "+options[2*i]);
							Logger.logln("");
							tempField.setPreferredSize(new Dimension(25,550));
							tempField.setBorder(defaultBorder);
							
							optionFields.add(tempField); //TODO not sure if necessary
							
							optionsPanel.add(tempLabel);
							optionsPanel.add(tempField);
						}
						
					}
					
					else{
						mainPanel.add(optionsPanel,BorderLayout.NORTH);
						{
							JLabel temp = new JLabel("This analyzer has no options/arguments to edit.");
							optionsPanel.add(temp);
						}
					}
						
				}
				{
					// ============
					// Button panel
					// ============
					buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					mainPanel.add(buttonPanel,BorderLayout.SOUTH);
					{
						applyJButton = new JButton("Apply Changes");
						buttonPanel.add(applyJButton);
					}
					{
						cancelJButton = new JButton("Cancel");
						buttonPanel.add(cancelJButton);
					}
				}
			}
			
			ClassWizardDriver.initListeners(this);
			
			pack();
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	
}
