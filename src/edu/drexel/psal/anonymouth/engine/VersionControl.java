package edu.drexel.psal.anonymouth.engine;

import java.util.ArrayList;

import edu.drexel.psal.anonymouth.gooie.GUIMain;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;

public class VersionControl {
	
	private final int SIZECAP = 20;
	private GUIMain main;
	private ArrayList<TaggedDocument> undo;
	private ArrayList<TaggedDocument> redo;
	
	public VersionControl(GUIMain main) {
		this.main = main;
		undo = new ArrayList<TaggedDocument>();
		redo = new ArrayList<TaggedDocument>();
	}
	
	public void addVersion(TaggedDocument taggedDoc) {
		if (undo.size() < SIZECAP) {
			main.editUndoMenuItem.setEnabled(true);
			
		}
	}
	
	public void undo() {
		main.editRedoMenuItem.setEnabled(true);
		
		if (undo.size() == 0)
			main.editUndoMenuItem.setEnabled(false);
	}
	
	public void redo() {
		if (redo.size() == 0)
			main.editRedoMenuItem.setEnabled(false);
	}
}
