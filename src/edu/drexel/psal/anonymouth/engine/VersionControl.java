package edu.drexel.psal.anonymouth.engine;

import java.util.Stack;

import edu.drexel.psal.anonymouth.gooie.DriverDocumentsTab;
import edu.drexel.psal.anonymouth.gooie.GUIMain;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;

/**
 * Adds undo/redo functionality to the documents pane.
 * NOTE: In it's current state the undo will only pertain to sentence-level changes. This means the first time you hit undo, it will
 * revert the sentence you were working on back to the state it was in before you started editing it, and if you hit undo again it undoes
 * the sentence you worked on before, and so on.
 * 
 * The reason behind this decision is the nature of the data we are storing. We're not just storing the text in the document window, but
 * a deep copy of each TaggedDocument. This means it takes a lot of data and we can only safely store a small amount (at this point, 20).
 * This means if made the undo work at the character level or word level the user would barely be able to undo anything at all. We want them
 * to be able to go back as far as they possibly can with the undo functionality, and this is the best way to do it. Not to mention I expect
 * users would want to revert a sentence back when they undo in Anonymouth instead of undo a word or character, though we should test this to
 * make sure.
 * @author Marc Barrowclift
 *
 */
public class VersionControl {
	
	private final int SIZECAP = 20;
	private GUIMain main;
	private Stack<TaggedDocument> undo;
	private Stack<TaggedDocument> redo;
	private TaggedDocument mostRecentState;
	
	/**
	 * Constructor
	 * @param main - Instance of GUIMain
	 */
	public VersionControl(GUIMain main) {
		this.main = main;
		undo = new Stack<TaggedDocument>();
		redo = new Stack<TaggedDocument>();
	}
	
	/**
	 * Must be called in DriverDocumentsTab or wherever you want a "version" to be backed up in the undo stack.
	 * @param taggedDoc - The TaggedDocument instance you want to capture.
	 */
	public void addVersion(TaggedDocument taggedDoc) {
		if (undo.size() >= SIZECAP) {
			undo.remove(0);
		}
		
		main.editUndoMenuItem.setEnabled(true);
		TaggedDocument backup = new TaggedDocument(taggedDoc);
		undo.push(backup);
	}
	
	/**
	 * Should be called in the program whenever you want a undo action to occur.
	 * 
	 * Swaps the current taggedDoc in DriverDocumentsTab with the version on the top of the undo stack, updates the document text pane with
	 * the new taggedDoc, and pushed the taggedDoc that was just on the undo stack to the redo one. 
	 */
	public void undo() {
		if (redo.isEmpty() && mostRecentState == null)
			redo.push(undo.pop());
		main.editRedoMenuItem.setEnabled(true);
		
		TaggedDocument doc = undo.pop();
		
		DriverDocumentsTab.taggedDoc = doc;
		DriverDocumentsTab.update(main, true);
		
		if (undo.size() == 0)
			main.editUndoMenuItem.setEnabled(false);

		if (mostRecentState != null) {
			redo.push(mostRecentState);
			redo.push(doc);
			mostRecentState = null;
		} else
			redo.push(doc);
	}
	
	/**
	 * Should be called in the program whenever you want a redo action to occur.
	 * 
	 * Swaps the current taggedDoc in DriverDocumentsTab with the version on the top of the redo stack, updates the document text pane with
	 * the new taggedDoc, and pushed the taggedDoc that was just on the redo stack to the undo one. 
	 */
	public void redo() {
		if (undo.isEmpty())
			undo.push(redo.pop());
		TaggedDocument doc = redo.pop();
		DriverDocumentsTab.taggedDoc = doc;
		DriverDocumentsTab.update(main, true);
		
		undo.push(doc);
		
		if (undo.size() > 0)
			main.editUndoMenuItem.setEnabled(true);
		
		if (redo.size() == 0)
			main.editRedoMenuItem.setEnabled(false);
	}
	
	/**
	 * Saves a copy of the "most recent state" separate from the stacks so that when the user decides to start undo/redo-ing, they always
	 * have to option to change their mind and revert back exactly like it was before they began undoing.
	 * @param doc - The instance of taggedDoc you want to capture. Should ONLY be set at the end of removeReplaceAndUpdate, do NOT call anywhere else
	 */
	public void setMostRecentState(TaggedDocument doc) {
		mostRecentState = doc;
	}
}
