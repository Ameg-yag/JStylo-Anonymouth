package edu.drexel.psal.anonymouth.gooie;

import edu.drexel.psal.jstylo.generics.CumulativeFeatureDriver;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.ProblemSet;
import edu.drexel.psal.jstylo.generics.WekaInstancesBuilder;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import weka.classifiers.Classifier;
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
 * Creates new inner panel for editor tab, broken up into three parts: get information from last clicked pane, create pane/set values, set main objects
 * @author Andrew W.E. McDonald
 *
 */
public class EditorInnerTabSpawner {
	
			protected JPanel editBoxPanel;
			protected JPanel sentenceAndDocumentPanel;
			protected JPanel sentenceLabelPanel;
			protected JScrollPane sentencePane;
			protected JButton removeWordsButton;
			protected JButton shuffleButton;
			private JPanel spacer1;
			protected JButton restoreSentenceButton;
			protected JLabel classificationLabel;
			protected JPanel resultsBoxPanel_InnerBottomPanel;
			protected JTable resultsTable;
			protected JScrollPane resultsTablePane;
			protected JPanel resultsBoxPanel;
			protected JLabel resultsTableLabel;
			protected JPanel resultsTableLabelPanel;
			protected JPanel resultsBoxAndResultsLabelPanel;
			protected JTextPane editorBox;
			protected JScrollPane editBox;
			protected JPanel editorBoxPanel;
			protected JLabel editBoxLabel;
			protected JPanel editBoxLabelPanel;
			protected JPanel editBoxAndEditLabelPanel;
			protected JTextPane sentenceEditPane;
			protected JLabel sentenceBoxLabel;
			protected JPanel sentencePanel;
			protected JPanel sentenceAndSentenceLabelPanel;
			private boolean tabMade = false;
			protected int resultsMaxIndex;
			protected String chosenAuthor;
	
			private String oldEditorBoxDoc = " ";
			private TableModel oldResultsTableModel = null;
			//private String oldResultsTableLabelString = " ";
			private String oldClassificationLabel = " ";
			private TableCellRenderer tcr = new DefaultTableCellRenderer();
	
			public EditorInnerTabSpawner(){
				
			}
			
			public EditorInnerTabSpawner(EditorInnerTabSpawner eits){
				oldEditorBoxDoc = eits.editorBox.getText().substring(0);
				oldResultsTableModel = eits.resultsTable.getModel();
				tcr = eits.resultsTable.getDefaultRenderer(Object.class);
				EditorTabDriver.numEdits++;
				Logger.logln("EditorInnerTabSpawner created.");
			}

			public EditorInnerTabSpawner spawnTab(){
				Logger.logln("EditorInnerTabSpawner spawning tab");
				if(tabMade == false){
					editBoxPanel = new JPanel();
                    BorderLayout thisLayout = new BorderLayout();
                    editBoxPanel.setLayout(thisLayout);
                    editBoxPanel.setPreferredSize(new java.awt.Dimension(744, 563));
                    editBoxPanel.setMaximumSize(new java.awt.Dimension(1000,563));
                    {
                        sentenceAndDocumentPanel = new JPanel();
                        BorderLayout sentenceAndDocumentPanelLayout = new BorderLayout();
                        sentenceAndDocumentPanel.setLayout(sentenceAndDocumentPanelLayout);
                        editBoxPanel.add(sentenceAndDocumentPanel, BorderLayout.NORTH);
                        sentenceAndDocumentPanel.setPreferredSize(new java.awt.Dimension(744, 435));
                        {
                            sentenceAndSentenceLabelPanel = new JPanel();
                            BorderLayout sentenceAndSentenceLabelPanelLayout = new BorderLayout();
                            sentenceAndSentenceLabelPanel.setLayout(sentenceAndSentenceLabelPanelLayout);
                            sentenceAndDocumentPanel.add(sentenceAndSentenceLabelPanel, BorderLayout.NORTH);
                            sentenceAndSentenceLabelPanel.setPreferredSize(new java.awt.Dimension(744, 115));
                            {
                                sentencePanel = new JPanel();
                                BorderLayout sentencePanelLayout = new BorderLayout();
                                sentencePanel.setLayout(sentencePanelLayout);
                                sentenceAndSentenceLabelPanel.add(sentencePanel, BorderLayout.SOUTH);
                                sentencePanel.setPreferredSize(new java.awt.Dimension(744, 86));
                                {
                                    sentencePane = new JScrollPane();
                                    sentencePanel.add(sentencePane, BorderLayout.CENTER);
                                    sentencePane.setPreferredSize(new java.awt.Dimension(744, 74));
                                    {
                                    	StyleContext sc = new StyleContext();
                                        final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
                                        sentenceEditPane = new JTextPane(doc);
                                        sentencePane.setViewportView(sentenceEditPane);
                                        //makes font size 24
                                        Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
                                        Style editBoxStyle = sc.addStyle("EditBoxStyle", defaultStyle);
                                        StyleConstants.setFontSize(editBoxStyle, 14);
                                        sentenceEditPane.setText("Please press the Process button now.");
                                        sentenceEditPane.setEditable(false);
                                        sentenceEditPane.setPreferredSize(new java.awt.Dimension(740, 82));
                                        doc.setLogicalStyle(0, editBoxStyle);
                                    }
                                }
                            }
                            {
                            	sentenceLabelPanel = new JPanel();
                            	sentenceAndSentenceLabelPanel.add(sentenceLabelPanel, BorderLayout.NORTH);
                            	sentenceLabelPanel.setPreferredSize(new java.awt.Dimension(744, 28));
                            	{
                            		sentenceBoxLabel = new JLabel();
                            		sentenceLabelPanel.add(sentenceBoxLabel);
                            		sentenceLabelPanel.add(getSpacer1());
                            		sentenceLabelPanel.add(getRestoreSentenceButton());
                            		sentenceLabelPanel.add(getShuffleButton());
                            		sentenceLabelPanel.add(getRemoveWordsButton());
                            		sentenceBoxLabel.setText("Sentence You are Currently Editing:");
                            		sentenceBoxLabel.setPreferredSize(new java.awt.Dimension(196, 15));
                            	}
                            }
                        }
                        {
                            editBoxAndEditLabelPanel = new JPanel();
                            BorderLayout editBoxAndEditLabelPanelLayout = new BorderLayout();
                            editBoxAndEditLabelPanel.setLayout(editBoxAndEditLabelPanelLayout);
                            sentenceAndDocumentPanel.add(editBoxAndEditLabelPanel, BorderLayout.SOUTH);
                            editBoxAndEditLabelPanel.setPreferredSize(new java.awt.Dimension(744, 316));
                            {
                                editBoxLabelPanel = new JPanel();
                                editBoxAndEditLabelPanel.add(editBoxLabelPanel, BorderLayout.NORTH);
                                editBoxLabelPanel.setPreferredSize(new java.awt.Dimension(744, 29));
                                {
                                    editBoxLabel = new JLabel();
                                    editBoxLabelPanel.add(editBoxLabel);
                                    editBoxLabel.setText("The Latest Version of Your \"Document to Anonymize\":");
                                }
                            }
                            {
                                editorBoxPanel = new JPanel();
                                BorderLayout editorBoxPanelLayout = new BorderLayout();
                                editorBoxPanel.setLayout(editorBoxPanelLayout);
                                editBoxAndEditLabelPanel.add(editorBoxPanel, BorderLayout.SOUTH);
                                editorBoxPanel.setPreferredSize(new java.awt.Dimension(744, 289));
                                {
                                    editBox = new JScrollPane();
                                    editorBoxPanel.add(editBox, BorderLayout.CENTER);
                                    {
                                        editorBox = new JTextPane();
                                        editBox.setViewportView(editorBox);
                                        editorBox.setText("This is where the latest version of your document will be.");
                                        editorBox.setEditable(false);
                                    }
                                }
                            }
                        }
                    }
                    {
                        resultsBoxAndResultsLabelPanel = new JPanel();
                        BorderLayout resultsBoxAndResultsLabelPanelLayout = new BorderLayout();
                        resultsBoxAndResultsLabelPanel.setLayout(resultsBoxAndResultsLabelPanelLayout);
                        editBoxPanel.add(resultsBoxAndResultsLabelPanel, BorderLayout.SOUTH);
                        resultsBoxAndResultsLabelPanel.setPreferredSize(new java.awt.Dimension(744, 121));
                        {
                            resultsTableLabelPanel = new JPanel();
                            resultsBoxAndResultsLabelPanel.add(resultsTableLabelPanel, BorderLayout.NORTH);
                            resultsTableLabelPanel.setPreferredSize(new java.awt.Dimension(744, 27));
                            {
                                resultsTableLabel = new JLabel();
                                resultsTableLabelPanel.add(resultsTableLabel);
                                resultsTableLabel.setText("Below are the results of your document's classification");
                            }
                        }
                        {
                            resultsBoxPanel = new JPanel();
                            BorderLayout resultsBoxPanelLayout = new BorderLayout();
                            resultsBoxPanel.setLayout(resultsBoxPanelLayout);
                            resultsBoxAndResultsLabelPanel.add(resultsBoxPanel, BorderLayout.SOUTH);
                            resultsBoxPanel.setPreferredSize(new java.awt.Dimension(744, 91));
                            {
                                resultsTablePane = new JScrollPane();
                                resultsBoxPanel.add(resultsTablePane, BorderLayout.NORTH);
                                resultsTablePane.setPreferredSize(new java.awt.Dimension(744, 60));
                               {
                                	
                                    TableModel resultsTableModel = 
									new DefaultTableModel(
                                                          new String[][] { { "One", "Two" }, { "Three", "Four" } },
                                                          new String[] { "Column 1", "Column 2" });
                                    if (oldResultsTableModel!=null) {
                                    	resultsTableModel=oldResultsTableModel; 
                                    }
                                    resultsTable = new JTable();
                                    resultsTablePane.setViewportView(resultsTable);
                                    resultsTable.setModel(resultsTableModel);
                                    resultsTable.setPreferredSize(new java.awt.Dimension(741, 32));
                                   
                                }
                            }
                            {
                                resultsBoxPanel_InnerBottomPanel = new JPanel();
                                resultsBoxPanel.add(resultsBoxPanel_InnerBottomPanel, BorderLayout.SOUTH);
                                resultsBoxPanel_InnerBottomPanel.setPreferredSize(new java.awt.Dimension(744, 35));
                                {
                                    classificationLabel = new JLabel();
                                    resultsBoxPanel_InnerBottomPanel.add(classificationLabel);
                                    classificationLabel.setText("Your document's classification will be shown here");
                                }
                            }
                        }
                    }	
                    tabMade = true;
				}
				return this;
			}
			
			public JScrollPane getSentencePane() {
				if(sentencePane == null) {
					sentencePane = new JScrollPane();
					sentencePane.setViewportView(getSentenceEditPane());
				}
				return sentencePane;
			}
			
			public JTextPane getSentenceEditPane() {
				if(sentenceEditPane == null) {
					sentenceEditPane = new JTextPane();
					sentenceEditPane.setText("Edit here");
					sentenceEditPane.setVisible(true);
				}
				return sentenceEditPane;
			}
			
			public JButton getRestoreSentenceButton() {
				if(restoreSentenceButton == null) {
					restoreSentenceButton = new JButton();
					restoreSentenceButton.setText("Restore Sentence");
					restoreSentenceButton.setEnabled(false);
				}
				return restoreSentenceButton;
			}
			
			public JPanel getSpacer1() {
				if(spacer1 == null) {
					spacer1 = new JPanel();
					spacer1.setPreferredSize(new java.awt.Dimension(76, 10));
				}
				return spacer1;
			}
			
			private JButton getShuffleButton() {
				if(shuffleButton == null) {
					shuffleButton = new JButton();
					shuffleButton.setText("Shuffle!");
					shuffleButton.setPreferredSize(new java.awt.Dimension(100, 23));
					shuffleButton.setEnabled(false);
				}
				return shuffleButton;
			}
			
			private JButton getRemoveWordsButton() {
				if(removeWordsButton == null) {
					removeWordsButton = new JButton();
					removeWordsButton.setText("Remove Red Words");
					shuffleButton.setEnabled(false);
				}
				return removeWordsButton;
			}

}

