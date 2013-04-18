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
	protected JPanel descriptionPanel;
	protected JPanel optionsPanel;
	protected JPanel buttonPanel;
	
	//description panel
	protected JLabel summaryJLabel;
	protected JTextArea descriptionJTextArea;
	protected JScrollPane descriptionJScrollPane; 
	
	//optionsPanel
	protected ArrayList<JTextField> optionFields; //is used by ClassWizardDriver to set the new option string
	protected JScrollPane optionsJScrollPane;
	/*
	 * The rest of the components are to be dynamically generated based on the size/number of arguments.
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
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
					descriptionPanel.setBorder(BorderFactory.createCompoundBorder(defaultBorder,BorderFactory.createEmptyBorder(10,10,10,10)));
					descriptionJScrollPane = new JScrollPane(descriptionPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

					if (options==null || options.length<=3) //if there are no options, make the description the main focus of the window
						mainPanel.add(descriptionJScrollPane,BorderLayout.CENTER);
					else //otherwise the description is delegated to the top of the window
						mainPanel.add(descriptionJScrollPane,BorderLayout.NORTH);
					
					{
						descriptionJTextArea = new JTextArea();
						descriptionJTextArea.setText(analyzerDesc);
						descriptionJTextArea.setEditable(false);
						descriptionJTextArea.setLineWrap(true);
						descriptionJTextArea.setWrapStyleWord(true);
						descriptionJTextArea.setPreferredSize(new Dimension(500,300));
						descriptionPanel.add(descriptionJTextArea,BorderLayout.CENTER);
					}
					{
						summaryJLabel = new JLabel();
						summaryJLabel.setText("<html><p>" +
								"<font size=12pt><b>Editing a Classifier</b></font><br>" +
								"To edit the arguments given to the classifier, simply change the values in the text field below.<br>" +
								"Clicking the \"Apply Changes\" button will change the arg string and close the window.<br>"+
								"Clicking the \"Cancel\" button will undo any changes.<br><br>" +
								"NOTE: Due to the way weka classifiers are coded, we do not yet support the editing<br>"+
								"&nbsp&nbsp&nbsp&nbsp&nbsp of arguments for all classifiers. You can still read the description and what each arg does, but note that<br>" +
								"&nbsp&nbsp&nbsp&nbsp&nbsp the default args in the textfield may be incorrect, and that trying to save them may not go as planned.<br>" +
								"<br>" +
								"<b>Classifiers with known issues:</b><br>" +
								"Multilayer Perceptron, NaiveBayes, J48, "+
								"<br></p></html>");
						descriptionPanel.add(summaryJLabel,BorderLayout.NORTH);
					}
				}
				{
					// =============
					// Options panel
					// =============
					
						
					optionsPanel = new JPanel(new GridLayout(0,1,0,0));
					optionsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
					optionsJScrollPane = new JScrollPane(optionsPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					optionsJScrollPane.setBorder(defaultBorder);
					if (options!=null && options.length>0){
						if (options.length==1){
							optionsPanel.setPreferredSize(new Dimension(500,50));
							mainPanel.add(optionsJScrollPane,BorderLayout.SOUTH);
						}
						else if (options.length==2){
							optionsPanel.setPreferredSize(new Dimension(500,100));
							mainPanel.add(optionsJScrollPane,BorderLayout.SOUTH);
						}
						else if (options.length==3){
							optionsPanel.setPreferredSize(new Dimension(500,150));
							mainPanel.add(optionsJScrollPane,BorderLayout.SOUTH);
						}
						else{
							optionsPanel.setPreferredSize(new Dimension(500,400));
							mainPanel.add(optionsJScrollPane,BorderLayout.CENTER);
						}
						
						//loop through options, adding a new option-description pair for each one
						for (int i=0; i<options.length/2;i++){
							
							if (optionsDesc[i]==null)
								break;
							JTextField tempLabel = new JTextField("\n"+optionsDesc[i].trim().replaceAll("\\s+", " "));
							tempLabel.setPreferredSize(new Dimension(550,50));
							tempLabel.setEditable(false);
							
							JTextField tempField = new JTextField(options[2*i+1]);
							tempField.setPreferredSize(new Dimension(550,25));
							
							optionFields.add(tempField); 							
							optionsPanel.add(tempLabel);
							optionsPanel.add(tempField);
						}
						
					}
					
					else{
						mainPanel.add(optionsPanel,BorderLayout.NORTH);
						{
							JLabel temp = new JLabel("<html><font color=\"FF0000\">This analyzer has no options/arguments to edit.</color></html>");
							optionsPanel.add(temp);
						}
					}
						
					{
						// ============
						// Button panel
						// ============
						if (options!=null && options.length!=0){
							buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
							optionsPanel.add(buttonPanel);
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
				}
			}
			
			if (options!=null && options.length!=0)
				ClassWizardDriver.initListeners(this);
			
			pack();
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	
}
