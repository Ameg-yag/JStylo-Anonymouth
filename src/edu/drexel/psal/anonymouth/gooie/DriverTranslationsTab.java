package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

import edu.drexel.psal.anonymouth.utils.TaggedSentence;

public class DriverTranslationsTab implements ActionListener {
	private static GUIMain main;
	protected static JPanel[] finalPanels;
	protected static JLabel[] languageLabels;
	protected static Map<String, TaggedSentence> translationsMap;
	protected static JTextPane[] translationTextAreas;
	protected static JButton[] translationButtons;
	protected static int numTranslations;
	protected static TaggedSentence current;
	private static ImageIcon arrow_up;
	private static ImageIcon arrow_down;

	/**
	 * Displays the translations of the given sentence in the translations holder panel.
	 * @param sentence - the TaggedSentence to show the translations of
	 */
	public static void showTranslations(TaggedSentence sentence) {
		main = GUIMain.inst;
		DriverTranslationsTab inst = new DriverTranslationsTab();
		
		// remove all the current translations shown
		main.translationsHolderPanel.removeAll();
		
		if (Translator.noInternet) {
			main.notTranslated.setText("Translations unavailable: No Internet connection\n\n" +
					"If you wish to recieve translation suggestions you must connect to the internet" +
					"and re-process your document.");
			main.translationsHolderPanel.add(main.notTranslated, "");
		} else if (Translator.accountsUsed) {
			main.notTranslated.setText("The account used for translations has expired.\n\n" +
					"In order to continue recieving translations, you must restart in order for the " +
					"account change to be reflected.");
			main.translationsHolderPanel.add(main.notTranslated, "");
		} else if (!PropertiesUtil.getDoTranslations()) {
			main.notTranslated.setText("You have turned translations off.");
			main.translationsHolderPanel.add(main.notTranslated, "");
		} else if (Translator.translatedSentences.contains(sentence.getUntagged(false))) {
			arrow_up = GUIMain.arrow_up;
			arrow_down = GUIMain.arrow_down;
			
			current = sentence;

			// retrieve the translation information
			ArrayList<String> translationNames = current.getTranslationNames();
			ArrayList<TaggedSentence> translations = current.getTranslations();
			translationsMap = new HashMap<String, TaggedSentence>();
			numTranslations = translations.size();

			// initialize the GUI components
			translationTextAreas = new JTextPane[numTranslations];// everySingleCluster.size()
			languageLabels = new JLabel[numTranslations];
			finalPanels = new JPanel[numTranslations];
			translationButtons = new JButton[numTranslations];
			
			// for each translation, initialize a title label, and a text area that will hold the translation
			// then add those two to a final panel, which will be added to the translation list panel.
			for (int i = 0; i < numTranslations; i++)
			{
				// set up title label
				languageLabels[i] = new JLabel(translationNames.get(i));
				translationsMap.put(translationNames.get(i), translations.get(i));
				languageLabels[i].setFont(GUIMain.titleFont);
				languageLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
				languageLabels[i].setBorder(GUIMain.rlborder);
				languageLabels[i].setOpaque(true);
				languageLabels[i].setBackground(main.tan);

				// set up translation text area
				translationTextAreas[i] = new JTextPane();
				translationTextAreas[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(1,3,1,3)));
				translationTextAreas[i].setText(translations.get(i).getUntagged(false).trim());
				translationTextAreas[i].setEditable(false);

				translationButtons[i] = new JButton();
				translationButtons[i].setIcon(arrow_up);
				translationButtons[i].setPressedIcon(arrow_down);
				translationButtons[i].setToolTipText("Click to replace selected sentence with this translation");
				translationButtons[i].setBorderPainted(false);
				translationButtons[i].setContentAreaFilled(false);
				translationButtons[i].setActionCommand(translationNames.get(i));
				translationButtons[i].addActionListener(inst);

				// set up final panel, which will hold the previous two components
				MigLayout layout = new MigLayout(
						"wrap, ins 0",
						"",
						"");
				finalPanels[i] = new JPanel(layout);
				finalPanels[i].add(languageLabels[i], "grow, h 20!, north");
				finalPanels[i].add(translationButtons[i], "west, wmax 30, wmin 30"); //50 //25
				finalPanels[i].add(translationTextAreas[i], "east, wmin 283, wmax 283"); //263 //288

				// add final panel to the translations list panel
				main.translationsHolderPanel.add(finalPanels[i], "");
			}
			
		} else {
			main.notTranslated.setText("Sentence has not been translated yet, please wait or work on already translated sentences.");
			main.translationsHolderPanel.add(main.notTranslated, "");
		}
			
		// revalidates and repaints so the GUI updates
		main.translationsHolderPanel.revalidate();
		main.translationsHolderPanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		main.versionControl.addVersion(DriverDocumentsTab.taggedDoc);
		DriverDocumentsTab.currentCharacterBuffer = 0;
		
		GUIMain.saved = false;
		DriverDocumentsTab.removeReplaceAndUpdate(main, DriverDocumentsTab.sentToTranslate, translationsMap.get(e.getActionCommand()).getUntagged(false), true);
		GUIMain.GUITranslator.replace(DriverDocumentsTab.taggedDoc.getSentenceNumber(DriverDocumentsTab.sentToTranslate), current);
		
		main.anonymityDrawingPanel.updateAnonymityBar();
		SuggestionCalculator.placeSuggestions(main);
		
		main.translationsHolderPanel.removeAll();
		main.notTranslated.setText("Sentence has not been translated yet, please wait or work on already translated sentences.");
		main.translationsHolderPanel.add(main.notTranslated, "");
		main.translationsHolderPanel.revalidate();
		main.translationsHolderPanel.repaint();
		
		main.versionControl.setMostRecentState(DriverDocumentsTab.taggedDoc);
	}
	
	/**
	 * Resets all variables and clears panel of all translations, to be used for re-processing
	 */
	public static void reset() {
		main.translationsHolderPanel.removeAll();
		if (PropertiesUtil.getDoTranslations()) {
			main.notTranslated.setText("Document re-processing, please wait.");
			main.translationsHolderPanel.add(main.notTranslated, "");
		}
	}
}
