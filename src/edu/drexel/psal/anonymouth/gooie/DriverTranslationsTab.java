package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.awt.Image;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;

public class DriverTranslationsTab
{
	private static GUIMain main;
	protected static JPanel[] finalPanels;
	protected static JLabel[] languageLabels;
	protected static JTextPane[] translationTextAreas;
	protected static int numTranslations;
	protected static TaggedSentence current;
	private static Image arrow;

	/**
	 * Displays the translations of the given sentence in the translations holder panel.
	 * @param sentence - the TaggedSentence to show the translations of
	 */
	public static void showTranslations(TaggedSentence sentence)
	{
		main = GUIMain.inst;
		arrow = main.arrow;
		
		// remove all the current translations shown
		main.translationsHolderPanel.removeAll();

		current = sentence;

//		ArrayList<TaggedSentence> taggedSentences = DriverDocumentsTab.taggedDoc.getTaggedSentences();
//		for (TaggedSentence tSent: taggedSentences)
//		{
//			if (tSent.getUntagged().trim().equals(sentence))
//			{
//				current = tSent;
//				break;
//			}
//		}

		// retrieve the translation information
		ArrayList<String> translationNames = current.getTranslationNames();
		ArrayList<TaggedSentence> translations = current.getTranslations();
		numTranslations = translations.size();

		// initialize the GUI components
		translationTextAreas = new JTextPane[numTranslations];// everySingleCluster.size()
		languageLabels = new JLabel[numTranslations];
		finalPanels = new JPanel[numTranslations];

		// for each translation, initialize a title label, and a text area that will hold the translation
		// then add those two to a final panel, which will be added to the translation list panel.
		for (int i = 0; i < numTranslations; i++)
		{
			// set up title label
			languageLabels[i] = new JLabel(translationNames.get(i));
			languageLabels[i].setFont(main.titleFont);
			languageLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
			languageLabels[i].setBorder(main.rlborder);
			languageLabels[i].setOpaque(true);
			languageLabels[i].setBackground(main.tan);

			// set up translation text area
			translationTextAreas[i] = new JTextPane();
			translationTextAreas[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
			translationTextAreas[i].setText(translations.get(i).getUntagged().trim());
			translationTextAreas[i].setEditable(false);

			// set up final panel, which will hold the previous two components
			MigLayout layout = new MigLayout(
					"wrap, ins 0",
					"fill, grow",
					"[20]0[fill, grow]");
			finalPanels[i] = new JPanel(layout);
			finalPanels[i].add(languageLabels[i], "grow, h 20!");
			finalPanels[i].add(translationTextAreas[i], "grow");

			// add final panel to the translations list panel
			main.translationsHolderPanel.add(finalPanels[i], "grow");
		}	
		// revalidates and repaints so the GUI updates
		main.translationsHolderPanel.revalidate();
		main.translationsHolderPanel.repaint();
	}

	protected static void initListeners(final GUIMain main)
	{
		
		// Mouse listener taken from early version of anonymouth to listen for right clicks. Should use this as a template for code to allow right-clicking a translation and 
		// a response asking use if they want to swap this translation in for the highlighted sentence. If they agree, then the translation should be swapped in for the highlighted sentence.
	/*	
		main.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				int theAnswer = -1;
				boolean okayToDelete = true;
				if(e.getButton() != 1){
					if(main.editTP.getSelectedIndex() == 0){
						JOptionPane.showMessageDialog(main, "You cannot delete your original document.", "Can't Delete Original!",JOptionPane.ERROR_MESSAGE,GUIMain.iconNO);
						okayToDelete = false;
					}
					else{
					theAnswer = JOptionPane.showConfirmDialog(main,"Really delete current tab? \n\nNote: this action effects the current tab","Delete Current Tab",
							JOptionPane.YES_NO_OPTION);
					}
				}
				if(theAnswer == 0 && okayToDelete == true){
					main.editTP.remove(eits.editBoxPanel);
					nextTabIndex--;
					//main.editTP.setSelectedIndex(nextTabIndex);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		*/
	}
	
}