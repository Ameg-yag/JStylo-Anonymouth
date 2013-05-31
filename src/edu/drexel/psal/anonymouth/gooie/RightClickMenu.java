package edu.drexel.psal.anonymouth.gooie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.drexel.psal.anonymouth.utils.SentenceTools;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;

/**
 * Provides the framework for a right-click menu in the editor.
 * 
 * @author Marc Barrowclift
 */
public class RightClickMenu extends JPopupMenu {

	private JMenuItem combineSentences;
	private GUIMain main;
	private ActionListener combineSentencesListener;
	private MouseListener popupListener;
	public ArrayList<String[]> sentences;

	private static final long serialVersionUID = 1L;

	/**
	 * CONSTRUCTOR
	 */
	public RightClickMenu(GUIMain main) {
		combineSentences = new JMenuItem("Make a single sentence");
		
		this.add(combineSentences);
		this.main = main;
		
		initListeners();
	}

	/**
	 * Readies all the listeners for each menu item
	 */
	public void initListeners() {
		combineSentencesListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int size = sentences.size();
				int pastLength = 0;
				int length = 0;
				ArrayList<TaggedSentence> taggedSentences = new ArrayList<TaggedSentence>();
				
				//Goes through the selected sentences and for each EOS character we find (EXCLUDING the EOS character at the end of the last sentence) marks them as ignorable.
				for (int i = 0; i < size; i++) {
					length = sentences.get(i)[0].length();
					char character = main.getDocumentPane().getText().charAt(length-1+PopupListener.mark+pastLength);

					if ((character == '.' || character == '!' || character == '?') && size-1 != i) {
						DriverDocumentsTab.taggedDoc.specialCharTracker.setIgnore(length - 1 + PopupListener.mark+pastLength, true);
					}
										
					taggedSentences.add(DriverDocumentsTab.taggedDoc.getTaggedSentenceAt(length + PopupListener.mark + pastLength));
					pastLength += length;
				}
				
				//We're borrowing a variable used by translations to solve a similar purpose, we do not want the InputFilter to fire removeReplaceAndUpdate in the DriverDocumentsTab.
				InputFilter.ignoreTranslation = true;
				
				TaggedSentence replacement = DriverDocumentsTab.taggedDoc.concatSentences(taggedSentences);
				DriverDocumentsTab.taggedDoc.removeMultipleAndReplace(taggedSentences, replacement);
				DriverDocumentsTab.update(main, true);
				
				DriverDocumentsTab.ignoreHighlight = false;

				int[] selectedSentInfo = DriverDocumentsTab.calculateIndicesOfSentences(PopupListener.mark)[0];

				//We want to make sure we're setting the caret at the actual start of the sentence and not in white space (so it gets highlighted)
				int space = 0;
				while (main.getDocumentPane().getText().charAt(selectedSentInfo[1] + space)  == ' ') {
					space++;
				}
				
				main.getDocumentPane().getCaret().setDot(selectedSentInfo[1]+space);
				DriverDocumentsTab.selectedSentIndexRange[0] = selectedSentInfo[1];
				DriverDocumentsTab.selectedSentIndexRange[1] = selectedSentInfo[2];
				DriverDocumentsTab.ignoreHighlight = false;
				DriverDocumentsTab.moveHighlight(main, DriverDocumentsTab.selectedSentIndexRange);
			}
		};
		combineSentences.addActionListener(combineSentencesListener);
		
		popupListener = new PopupListener(this, main, this);
		main.getDocumentPane().addMouseListener(popupListener);
	}
	
	/**
	 * Enables or disables the combine sentences action.
	 * @param b - Whether or not to enable to menu item.
	 */
	public void enableCombineSentences(boolean b) {
		combineSentences.setEnabled(b);
	}
}

/**
 * The MouseAdapter that handles displaying the right-click menu and decides whether or not it's appropriate to enable the combining sentences option (i.e., if the user is only
 * selecting one sentence we can't possible combine it).
 * @author Marc Barrowclift
 *
 */
class PopupListener extends MouseAdapter {
	private JPopupMenu popup;
	private GUIMain main;
	private SentenceTools sentenceTools;
	private RightClickMenu rightClickMenu;
	public static int mark;

	/**
	 * CONSTRUCTOR
	 * @param popupMenu - An instance of the menu desired to present when the user right clicks.
	 * @param main - An instance of GUIMain.
	 * @param rightClickMenu - An instance of RightClickMenu.
	 */
	public PopupListener(JPopupMenu popupMenu, GUIMain main, RightClickMenu rightClickMenu) {
		popup = popupMenu;
		this.main = main;
		sentenceTools = new SentenceTools();
		this.rightClickMenu = rightClickMenu;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	/**
	 * Displays the right-click menu. Also checks whether or not the user has selected acceptable text and enables/disables combining sentences based on that.
	 * @param e - MouseEvent
	 */
	private void maybeShowPopup(MouseEvent e) {
		/*
		 * While it does seem a bit silly, we need a check to make sure the documentPane is enabled since this method will get called during the processing stage and
		 * we don't want anything below to be fired during so. This checks to make sure the user has selected appropriate text for the combine sentences option to be
		 * enabled.
		 */
		if (e.isPopupTrigger() && main.getDocumentPane().isEnabled()) {
			mark = main.getDocumentPane().getCaret().getMark();
			int dot = main.getDocumentPane().getCaret().getDot();
			
			System.out.println(dot + " " + mark);
			if (dot == mark) {
				rightClickMenu.enableCombineSentences(false);
			} else {
				String text = main.getDocumentPane().getText().substring(mark, dot);
				rightClickMenu.sentences = sentenceTools.makeSentenceTokens(text);
				
				System.out.println("\"" + text + "\"");
				System.out.println(rightClickMenu.sentences.size());
				if (rightClickMenu.sentences.size() > 1)
					rightClickMenu.enableCombineSentences(true);
				else
					rightClickMenu.enableCombineSentences(false);
			}
			
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
