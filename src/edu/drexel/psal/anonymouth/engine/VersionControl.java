package edu.drexel.psal.anonymouth.engine;

import java.util.Stack;

import edu.drexel.psal.anonymouth.gooie.DriverDocumentsTab;
import edu.drexel.psal.anonymouth.gooie.GUIMain;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;

/**
 * Adds undo/redo functionality to the documents pane.
 * @author Marc Barrowclift
 *
 */
public class VersionControl {
	
	private final int SIZECAP = 30;
	private GUIMain main;
	private Stack<TaggedDocument> undo;
	private Stack<TaggedDocument> redo;
	private Stack<Integer> indicesUndo;
	private Stack<Integer> indicesRedo;
	private int undoSize = 0;
	private int redoSize = 0;
	
	/**
	 * Constructor
	 * @param main - Instance of GUIMain
	 */
	public VersionControl(GUIMain main) {
		this.main = main;
		undo = new Stack<TaggedDocument>();
		redo = new Stack<TaggedDocument>();
		indicesUndo = new Stack<Integer>();
		indicesRedo = new Stack<Integer>();
	}
	
	/**
	 * Must be called in DriverDocumentsTab or wherever you want a "version" to be backed up in the undo stack.
	 * @param taggedDoc - The TaggedDocument instance you want to capture.
	 */
	
	public void addVersion(TaggedDocument taggedDoc, int offset) {
		System.out.println("VERSION ADDED");
		if (undo.size() >= SIZECAP) {
			undo.remove(0);
		}

		undo.push(new TaggedDocument(taggedDoc));
		indicesUndo.push(offset);
		undoSize++;
		
		main.enableUndo(true);
		main.enableRedo(false);
		
		redo.clear();
		indicesRedo.clear();
		redoSize = 0;
	}
	
	public void addVersion(TaggedDocument taggedDoc) {
		addVersion(taggedDoc, main.getDocumentPane().getCaret().getDot());
	}
	
	/**
	 * Should be called in the program whenever you want a undo action to occur.
	 * 
	 * Swaps the current taggedDoc in DriverDocumentsTab with the version on the top of the undo stack, updates the document text pane with
	 * the new taggedDoc, and pushed the taggedDoc that was just on the undo stack to the redo one. 
	 */
	public void undo() {
		System.out.println("UNDO EXECUTED");
		System.out.println("undo.size() = " + undo.size());
		System.out.println("redo.size() = " + redo.size());
		redo.push(new TaggedDocument(DriverDocumentsTab.taggedDoc));
		indicesRedo.push(main.getDocumentPane().getCaret().getDot());
		System.out.println("redo.size() = " + redo.size());
		
		DriverDocumentsTab.ignoreVersion = true;
		DriverDocumentsTab.taggedDoc = undo.pop();
		DriverDocumentsTab.update(main, true);
		main.getDocumentPane().getCaret().setDot(indicesUndo.pop());
		DriverDocumentsTab.ignoreVersion = false;
		
		main.enableRedo(true);
		undoSize--;
		redoSize++;
		
		System.out.println("undo.size() = " + undo.size());
		System.out.println("undoSize = " + undoSize);
		System.out.println("redo.size() = " + redo.size());
		System.out.println("redoSize = " + redoSize);
		
		if (undoSize == 0) {
			main.enableUndo(false);
		}
	}
	
	/**
	 * Should be called in the program whenever you want a redo action to occur.
	 * 
	 * Swaps the current taggedDoc in DriverDocumentsTab with the version on the top of the redo stack, updates the document text pane with
	 * the new taggedDoc, and pushed the taggedDoc that was just on the redo stack to the undo one. 
	 */
	public void redo() {
		undo.push(new TaggedDocument(DriverDocumentsTab.taggedDoc));
		indicesUndo.push(main.getDocumentPane().getCaret().getDot());
		
		DriverDocumentsTab.ignoreVersion = true;
		DriverDocumentsTab.taggedDoc = redo.pop();
		DriverDocumentsTab.update(main, true);
		main.getDocumentPane().getCaret().setDot(indicesRedo.pop());
		DriverDocumentsTab.ignoreVersion = false;

		main.enableUndo(true);	
		undoSize++;
		redoSize--;
		
		if (redoSize == 0) {
			main.enableRedo(false);
		}
	}
	
	/**
	 * Clears all stacks, should be used only for pre-processed documents
	 */
	public void reset() {
		undo.clear();
		redo.clear();
		indicesUndo.clear();
		indicesRedo.clear();
		undoSize = 0;
		redoSize = 0;
	}
	
	public boolean isUndoEmpty() {
		return undo.isEmpty();
	}
}