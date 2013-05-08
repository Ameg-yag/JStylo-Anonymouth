package edu.drexel.psal.anonymouth.engine;

import java.util.Stack;

import edu.drexel.psal.anonymouth.gooie.DriverDocumentsTab;
import edu.drexel.psal.anonymouth.gooie.GUIMain;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;

public class VersionControl {
	
	private final int SIZECAP = 20;
	private GUIMain main;
	private Stack<TaggedDocument> undo;
	private Stack<TaggedDocument> redo;
	private TaggedDocument mostRecentState;
	
	public VersionControl(GUIMain main) {
		this.main = main;
		undo = new Stack<TaggedDocument>();
		redo = new Stack<TaggedDocument>();
	}
	
	public void addVersion(TaggedDocument taggedDoc) {
		if (undo.size() < SIZECAP) {
			main.editUndoMenuItem.setEnabled(true);
			TaggedDocument backup = new TaggedDocument(taggedDoc);
			undo.push(backup);
		}
	}
	
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
	
	public Boolean isUndoEmpty() {
		if (undo.isEmpty())
			return true;
		else
			return false;
	}
	
	public void setMostRecentState(TaggedDocument doc) {
		mostRecentState = doc;
	}
}
