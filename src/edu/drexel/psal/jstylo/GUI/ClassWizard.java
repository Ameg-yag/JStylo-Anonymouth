package edu.drexel.psal.jstylo.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import edu.drexel.psal.jstylo.generics.Analyzer;

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
	protected JPanel descriptionPanel;
	protected JPanel optionsPanel;
	protected JPanel buttonPanel;
	
	//description panel
	protected JTextArea descriptionJTextArea;
	protected JLabel descriptionJLabel;
	
	//optionsPanel
	/*
	 * To be dynamically generated based on the size/number of arguments.
	 * for each argument there will be either a JLabel or JTextArea description, and a JTextfield in which to edit the arg
	 */
	
	//button panels
	protected JButton okJButton;
	protected JButton cancelJButton;
	
	public ClassWizard(GUIMain parent,Analyzer tmpAnalyzer){
		super();
		this.parent = parent;
		this.tmpAnalyzer = tmpAnalyzer;
		analyzerName = this.tmpAnalyzer.getName();
		analyzerDesc = this.tmpAnalyzer.analyzerDescription();
		options = this.tmpAnalyzer.getOptions();
		optionsDesc = this.tmpAnalyzer.optionsDescription();
		
		initGUI();
		
	}
	
	protected void initGUI(){
		try{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			{
				
				mainPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
				add(mainPanel);
				
				{
					// =============
					// description panel
					// =============
					JPanel descriptionPanel = new JPanel(new BorderLayout(cellPadding,cellPadding));
					if (options==null)
						mainPanel.add(descriptionPanel,BorderLayout.CENTER);
					else 
						mainPanel.add(descriptionPanel,BorderLayout.NORTH);
					{
						descriptionJLabel = new JLabel(analyzerName);
						descriptionPanel.add(descriptionJLabel,BorderLayout.NORTH);
					}
					{
						descriptionJTextArea = new JTextArea(analyzerDesc);
						descriptionJTextArea.setEditable(false);
						descriptionPanel.add(descriptionJTextArea);
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
