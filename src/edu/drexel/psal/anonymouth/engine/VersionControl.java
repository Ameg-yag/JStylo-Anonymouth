package edu.drexel.psal.anonymouth.engine;

import java.util.Stack;

import edu.drexel.psal.anonymouth.gooie.DriverDocumentsTab;
import edu.drexel.psal.anonymouth.gooie.GUIMain;
import edu.drexel.psal.anonymouth.gooie.ThePresident;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;

/**
 * Adds undo/redo functionality to the documents pane.
 * @author Marc Barrowclift
 *
 */
public class VersionControl {
	
	private final int SIZECAP = 20;
	private GUIMain main;
	private Stack<TaggedDocument> undo;
	private Stack<TaggedDocument> redo;
	private Stack<int[]> indicesUndo;
	private Stack<int[]> indicesRedo;
	private int[] mostRecentIndices;
	private TaggedDocument mostRecentState;
	private Boolean firstRun = true;
	private Boolean newRedo = true;
	private boolean undoRedoExecuted = false;
	
	/**
	 * Constructor
	 * @param main - Instance of GUIMain
	 */
	public VersionControl(GUIMain main) {
		this.main = main;
		undo = new Stack<TaggedDocument>();
		redo = new Stack<TaggedDocument>();
		indicesUndo = new Stack<int[]>();
		indicesRedo = new Stack<int[]>();
		mostRecentIndices = new int[2];
	}
	
	/**
	 * Must be called in DriverDocumentsTab or wherever you want a "version" to be backed up in the undo stack.
	 * @param taggedDoc - The TaggedDocument instance you want to capture.
	 */
	
	public void addVersion(TaggedDocument taggedDoc, int start, int end) {
		if (undo.size() >= SIZECAP) {
			undo.remove(0);
		}

		//Needed so we get the first version of the document in the stack and also keep undo disabled (since there's no changes yet).
		if (!firstRun)
			main.editUndoMenuItem.setEnabled(true);
		else
			firstRun = !firstRun;
		
		TaggedDocument backup = new TaggedDocument(taggedDoc);
		undo.push(backup);
		
		int[] temp = {start, end};
		indicesUndo.push(temp);
	}
	
	public void addVersion(TaggedDocument taggedDoc) {
		addVersion(taggedDoc, main.getDocumentPane().getCaret().getDot(), main.getDocumentPane().getCaret().getMark());
	}
	
	/**
	 * Should be called in the program whenever you want a undo action to occur.
	 * 
	 * Swaps the current taggedDoc in DriverDocumentsTab with the version on the top of the undo stack, updates the document text pane with
	 * the new taggedDoc, and pushed the taggedDoc that was just on the undo stack to the redo one. 
	 */
	public void undo() {
		undoRedoExecuted = true;

		//First go: true and not null
		//second go: false and not null
		if ((newRedo == false || redo.isEmpty()) && mostRecentState == null) {
			redo.push(undo.pop());
			indicesRedo.push(indicesUndo.pop());
		}

		if (newRedo == false)
			newRedo = true;
		
		main.editRedoMenuItem.setEnabled(true);
		
		TaggedDocument doc = undo.pop();
		int[] indices = indicesUndo.pop();
		
		DriverDocumentsTab.taggedDoc = doc;
		DriverDocumentsTab.update(main, true);
		main.getDocumentPane().setSelectionStart(indices[0]);
		main.getDocumentPane().setSelectionEnd(indices[1]);
		
		if (undo.size() == 0)
			main.editUndoMenuItem.setEnabled(false);

		if (mostRecentState != null) {
			redo.push(mostRecentState);
			redo.push(doc);
			
			indicesRedo.push(mostRecentIndices);
			indicesRedo.push(indices);
			mostRecentState = null;
		} else {
			redo.push(doc);
			indicesRedo.push(indices);
		}
	}
	
	/**
	 * Should be called in the program whenever you want a redo action to occur.
	 * 
	 * Swaps the current taggedDoc in DriverDocumentsTab with the version on the top of the redo stack, updates the document text pane with
	 * the new taggedDoc, and pushed the taggedDoc that was just on the redo stack to the undo one. 
	 */
	public void redo() {
		undoRedoExecuted = true;
		
		if (newRedo || undo.isEmpty()) {
			newRedo = false;
			undo.push(redo.pop());
			indicesUndo.push(indicesRedo.pop());
		}
		
		TaggedDocument doc = redo.pop();
		int[] indices = indicesRedo.pop();
		
		DriverDocumentsTab.taggedDoc = doc;
		DriverDocumentsTab.update(main, true);
		
		if (redo.isEmpty()) {
			main.getDocumentPane().setSelectionStart(mostRecentIndices[0]);
			main.getDocumentPane().setSelectionEnd(mostRecentIndices[1]);
		} else {
			main.getDocumentPane().setSelectionStart(indices[0]);
			main.getDocumentPane().setSelectionEnd(indices[1]);
		}
		
		undo.push(doc);
		indicesUndo.push(indices);
		
		if (undo.size() > 0)
			main.editUndoMenuItem.setEnabled(true);
		
		if (redo.size() == 0)
			main.editRedoMenuItem.setEnabled(false);
	}
	
	/**
	 * Saves a copy of the "most recent state" separate from the stacks so that when the user decides to start undo/redo-ing, they always
	 * have to option to change their mind and revert back exactly like it was before they began undoing.
	 * @param doc - The instance of taggedDoc you want to capture.
	 */
	public void setMostRecentState(TaggedDocument doc) {
		mostRecentState = new TaggedDocument(doc);
		mostRecentIndices[0] = main.getDocumentPane().getCaret().getDot();
		mostRecentIndices[1] = main.getDocumentPane().getCaret().getMark();
	}
	
	/**
	 * Keeps track of the carat location for each undo/redo saved.
	 * NOTE: Still needs a good deal of work.
	 * @param start - the location of the carat
	 * @param end - the end of the highlight (if any, if none pass same value as start)
	 */
	public void updateIndices(int start, int end) {
		if (indicesUndo.isEmpty())
			return;
	
		indicesUndo.get(0)[0] = start;
		indicesUndo.get(0)[1] = end;
	}
	
	/**
	 * Clears all stacks, should be used only for pre-processed documents
	 */
	public void reset() {
		undo.clear();
		redo.clear();
		indicesUndo.clear();
		indicesRedo.clear();
		mostRecentIndices = new int[2];
		mostRecentState = null;
		firstRun = true;
		newRedo = true;
	}

	public boolean isUndoOrRedoExecuted() {
		boolean returnValue = undoRedoExecuted;
		undoRedoExecuted = false;
		return returnValue;
	}
}