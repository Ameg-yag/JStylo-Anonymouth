package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.Cluster;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;
import edu.drexel.psal.jstylo.eventDrivers.*;
import edu.drexel.psal.jstylo.generics.*;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;
import edu.drexel.psal.jstylo.GUI.DocsTabDriver.ExtFilter;
import edu.drexel.psal.jstylo.canonicizers.*;

import com.jgaap.canonicizers.*;
import com.jgaap.generics.*;

public class DriverTranslationsTab {
	
	private final static String NAME = "( DriverTranslationsTab ) - ";

	private static GUIMain main;
	private static ListSelectionListener selListener;
	protected static JPanel[] finalPanels;
	protected static JLabel[] languageLabels;
	protected static JTextPane[] translationTextAreas;
	protected static int numTranslations;
	protected static TaggedSentence current;
	
	public static void showTranslations(String sentence)
	{
		main = GUIMain.inst;
		
		main.translationsHolderPanel.removeAll();
		
		ArrayList<TaggedSentence> taggedSentences = DriverDocumentsTab.taggedDoc.getTaggedSentences();
		for (TaggedSentence tSent: taggedSentences)
		{
			if (tSent.getUntagged().trim().equals(sentence))
			{
				current = tSent;
				break;
			}
		}
		
		ArrayList<String> translationNames = current.getTranslationNames();
		ArrayList<TaggedSentence> translations = current.getTranslations();
		numTranslations = translations.size();
		translationTextAreas = new JTextPane[numTranslations];// everySingleCluster.size()
		languageLabels = new JLabel[numTranslations];
		finalPanels = new JPanel[numTranslations];
		for (int i = 0; i < numTranslations; i++)
		{
			languageLabels[i] = new JLabel(translationNames.get(i)); // for if you want to edit the label in any way
			languageLabels[i].setFont(main.titleFont);
			languageLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
			languageLabels[i].setBorder(main.rlborder);
			languageLabels[i].setOpaque(true);
			languageLabels[i].setBackground(main.tan);
			
			translationTextAreas[i] = new JTextPane();
			translationTextAreas[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
			translationTextAreas[i].setText(translations.get(i).getUntagged().trim());
			
//			languageLabels[i].setPreferredSize(new Dimension(800,20));
//			translationTextAreas[i].setPreferredSize(new Dimension(800,40));
//			finalPanels[i].setPreferredSize(new Dimension(800,60));
			
			MigLayout layout = new MigLayout(
					"wrap, ins 0",
					"fill, grow",
					"[20]0[]");
			finalPanels[i] = new JPanel(layout);
			finalPanels[i].add(languageLabels[i], "grow, h 25!");
			finalPanels[i].add(translationTextAreas[i], "growx");
			
//			translationTextAreas[i].setPreferredSize(new Dimension(800,40));
//			finalPanels[i].setPreferredSize(new Dimension(800,60));
			main.translationsHolderPanel.add(finalPanels[i]);
		}
		main.translationsHolderPanel.revalidate();
		main.translationsHolderPanel.repaint();
	}
	
	protected static void initListeners(final GUIMain main)
	{
		 selListener = new ListSelectionListener()
         {
         	public void valueChanged(ListSelectionEvent e) 
         	{
//         		int row = main.translationsTable.getSelectedRow();
// 				String sentence = ConsolidationStation.toModifyTaggedDocs.get(0).getCurrentSentence().getTranslations().get(row).getUntagged();
// 				main.translationEditPane.setText(sentence);
             }
         };
//         main.translationsTable.getSelectionModel().addListSelectionListener(selListener);
	}
}