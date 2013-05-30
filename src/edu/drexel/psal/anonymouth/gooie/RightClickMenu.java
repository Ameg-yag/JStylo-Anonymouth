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
//		combineSentences = new JMenuItem("Make a single sentence");
//		this.add(combineSentences);
//		this.main = main;
//		initListeners();
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
				
				System.out.println(size);
				for (int i = 0; i < size; i++) {
					length = sentences.get(i)[0].length() + pastLength;
					char character = main.getDocumentPane().getText().charAt(length-1+PopupListener.mark);
//					System.out.println("Character Check: \"" + character + "\"");
//					System.out.println("Surrounding: \"" + main.getDocumentPane().getText().substring(length-5+PopupListener.mark, length+1+PopupListener.mark) + "\"");
					if ((character == '.' || character == '!' || character == '?') && size-1 != i) {
//						DriverDocumentsTab.taggedDoc.specialCharTracker.setIgnore(length - 1 + PopupListener.mark, true);
					}
					
//					System.out.println("Sentence: " + main.getDocumentPane().getText().substring(pastLength+PopupListener.mark, length+PopupListener.mark));
					taggedSentences.add(DriverDocumentsTab.taggedDoc.getTaggedSentenceAt(length + PopupListener.mark));
					length += pastLength;
					pastLength = length;
				}
				
//				System.out.println(taggedSentences.size());
//				for (int j = 0; j < taggedSentences.size(); j++) {
//					System.out.println("\"" + taggedSentences.get(j).getUntagged(false) + "\"");
//				}
				
				InputFilter.ignoreTranslation = true;
				
				TaggedSentence replacement = DriverDocumentsTab.taggedDoc.concatSentences(taggedSentences);
				DriverDocumentsTab.taggedDoc.removeMultipleAndReplace(taggedSentences, replacement);
				DriverDocumentsTab.update(main, true);
				
				DriverDocumentsTab.ignoreHighlight = false;
//				DriverDocumentsTab.ignoreNumActions = 3;
//				System.out.println("PopupListener.mark = " + PopupListener.mark);
				int[] selectedSentInfo = DriverDocumentsTab.calculateIndicesOfSentences(PopupListener.mark)[0];
//				System.out.println("selectedSentInfo[0] = " + selectedSentInfo[0]);
//				System.out.println("selectedSentInfo[1] = " + selectedSentInfo[1]);
//				System.out.println("selectedSentInfo[2] = " + selectedSentInfo[2]);
				int space = 0;
				while (main.getDocumentPane().getText().charAt(selectedSentInfo[1] + space)  == ' ') {
					space++;
				}
				main.getDocumentPane().getCaret().setDot(selectedSentInfo[1]+space);
				DriverDocumentsTab.selectedSentIndexRange[0] = selectedSentInfo[1];
				DriverDocumentsTab.selectedSentIndexRange[1] = selectedSentInfo[2];
				DriverDocumentsTab.ignoreHighlight = false;
//				DriverDocumentsTab.ignoreNumActions = 0;
				DriverDocumentsTab.moveHighlight(main, DriverDocumentsTab.selectedSentIndexRange);
			}
		};
		combineSentences.addActionListener(combineSentencesListener);

		popupListener = new PopupListener(this, main, this);
		main.getDocumentPane().addMouseListener(popupListener);
	}
	
	public void enableCombineSentences(boolean b) {
		combineSentences.setEnabled(b);
	}
}

class PopupListener extends MouseAdapter {
	private JPopupMenu popup;
	private GUIMain main;
	private SentenceTools sentenceTools;
	private RightClickMenu rightClickMenu;
	public static int mark;

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
	
	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger() && main.getDocumentPane().isEnabled()) {
			mark = main.getDocumentPane().getCaret().getMark();
			String text = main.getDocumentPane().getText().substring(mark, main.getDocumentPane().getCaret().getDot());
			rightClickMenu.sentences = sentenceTools.makeSentenceTokens(text);
			
			if (rightClickMenu.sentences.size() > 1)
				rightClickMenu.enableCombineSentences(true);
			else
				rightClickMenu.enableCombineSentences(false);
			
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
