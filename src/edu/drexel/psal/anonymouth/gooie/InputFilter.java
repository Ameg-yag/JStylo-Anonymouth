package edu.drexel.psal.anonymouth.gooie;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import edu.drexel.psal.jstylo.generics.Logger;

/**
 * @author Marc Barrowclift
 * @author Andrew W.E. McDonald
 * 
 * Supported actions:
 * 
 * 1) Adding/removing ellipsis (along with more than one EOS character and variations like "???", "!?", "....", "...", etc)
 * 		-TODO: "This...... another sentence" will break up the sentence into two. It used to break it up into three but
 * 				I modified the regEx "EOS_chars" slightly in SentenceTools to just split into two as if it was "This.... another sentence"
 * 				instead. I sadly can't figure out how to have it keep it a full sentence though while keeping the "This.... another sentence"
 * 				splitting functionality.
 * 2) Adding/removing abbreviations.
 * 3) TODO Adding quotes needs to be handled properly
 */
public class InputFilter extends DocumentFilter{
	
	private final String NAME = "( InputFilter ) - ";
	private String EOS = ".?!"; //Quick and dirty way to identify EOS characters.
	private Boolean watchForEOS = false; //Lets us know if the previous character(s) were EOS characters.
	public static Boolean isEOS = false; //keeps track of whether or not the current character is an EOS character.
	private String[] notEndsOfSentence = {"U.S.","R.N.","M.D.","i.e.","e.x.","e.g.","D.C.","B.C.","B.S.","Ph.D.","B.A.","A.B.","A.D.","A.M.","P.M.","r.b.i.","V.P."}; //we only need to worry about these kinds of abbreviations since SentenceTools takes care of the others
	
	/*
	 * 
	 * After this InputFilter (DocumentFilter) has been added to the documentPane (via main.getDocumentPane().setDocumentFilter(<intance of this class>)),
	 * All text entered will go through here. 
	 * 
	 * We want to do things like:
	 * 		-> Know if someone is inputting more than one EOS character (e.g. "???", "!?", "....", "...") -- 4 periods is ellipsis with period, 3 is just ellipsis
	 * 		-> Know if someone is editing within quotes, or within parenthesis
	 * 		-> Know if someone is removing more than one EOS character (see example above)
	 * 		-> *** Know if someone is adding in an abbreviation (see list of abbreviations in SentenceTools) ***
	 * 		-> Other edit things that we think of (there is a piece of paper near my area titled "Editing Issues" or something. That has a list of everything I could think of EXCEPT the abbreviations thing -- I just thought of that now)
	 * 
	 * When we see the user inputting or removing characters that could cause problems for our SentenceTools/TaggedSentence/TaggedDocument classes,
	 * we want to intercept the keys being input, and wait until we can determine what the user is inputting, so we can deliver the most complete sentence possible to our SentenceTools class.
	 * 
	 * If we don't do this, we'll have sentences (TaggedSentences) in our TaggedDocument that are single periods and other things that clearly aren't sentences.
	 * 
	 * Also, As you add support for handing various types of things (like multiple periods), create a list at the top of the class (just do it in the class Javadoc thing under "Supported actions:") 
	 * that says what types of actions this filter will support, so it's easier to keep track of what is supported and what needs to be added.
	 * 
	 * NOTE : Finally, for each bit of code you write that deals with certain actions, please please add comments that say what each bit does. 
	 * (see my code in DriverDocumentsTab from line 383 to 432). I (and other people) would very much appricate it if you could get into the habbit of always documenting your code like that.
	 * 
	 */
	
	/**
	 * If the user types a character or pastes in text this will get called BEFORE updating the documentPane and firing the listeners.
	 */
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {	
		if (text.length() == 1) { //If the user is just typing (single character)
			DriverDocumentsTab.shouldUpdate = false;
			
			checkAddingEllipses(text);
			checkAddingAbbreviations(text);
		} else { //If the user pasted in text of length greater than a single character
			DriverDocumentsTab.shouldUpdate = true; //If the user pasted in a massive chunk of text we want to update no matter what.
			Logger.logln(NAME + "User pasted in text, will update");
		}
		
		fb.replace(offset, length, text, attr);
	}
	
	/**
	 * Keeps track of whether or not the user may be typing ellipses and only removeReplaceAndUpdate's when we are sure they have completed
	 * Typing EOS characters and are beginning a new sentence.
	 * @param text - The text the user typed
	 */
	private void checkAddingEllipses(String text) {
		isEOS = EOS.contains(text); //Checks to see if the character is an EOS character.

		if (isEOS) {
			watchForEOS = true;
			//For whatever reason, startSelection must be subtracted by 1, and refuses to work otherwise.
			DriverDocumentsTab.taggedDoc.specialCharTracker.addEOS(text.charAt(0), DriverDocumentsTab.startSelection-1);
		} else if (!isEOS && !watchForEOS) { //If the user isn't typing an EOS character and they weren't typing one previously, then it's just a normal character, update.
			DriverDocumentsTab.shouldUpdate = true;
		}

		//if the user previously entered an EOS character and the new character is not an EOS character, then we should update
		if (watchForEOS && !isEOS) {
			watchForEOS = false;
			/**
			 * NOTE: We must NOT call removeReplaceAndUpdate() directly since the currentSentenceString variable that's used for the
			 * call's parameter is not updated yet (for example, the text here in InputFilter my read "TEST.... A sentence", but the
			 * currentSentenceString variable, and the documentPane, only read TEST....A sentence. The quickest and easiest way to fix
			 * this is just have a little flag at the end of the caret listener that calls removeReplaceAndUpdate only when we command
			 * it to from the InputFilter.
			 */
			DriverDocumentsTab.shouldUpdate = true;
		}
	}
	
	/**
	 * Keeps track of whether or not the user is entering an abbreviation or not and will only call removeReplaceAndUpdate when we are sure they are in fact
	 * not typing an abbreviation and want to end the sentence.
	 * @param text - The text the user typed
	 */
	private void checkAddingAbbreviations(String text) {
		String textBeforePeriod = GUIMain.inst.getDocumentPane().getText().substring(DriverDocumentsTab.startSelection-2, DriverDocumentsTab.startSelection);
		if (textBeforePeriod.substring(1, 2).equals(".") && !EOS.contains(text)) {			
			for (int i = 0; i < notEndsOfSentence.length; i++) {
				if (notEndsOfSentence[i].contains(textBeforePeriod))
					DriverDocumentsTab.shouldUpdate = false;
			}
		}
	}
	
	/**
	 * If the user deletes a character or a section of text this will get called BEFORE updating the documentPane and firing the listeners.
	 */
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
		if (length == 1) { //If the user is just deleting character by character
			DriverDocumentsTab.shouldUpdate = false;
			DriverDocumentsTab.EOSesRemoved = false;
			
			checkRemoveEllipses(offset);
			checkRemoveAbbreviations(offset);
		} else { //If the user selected and deleted a section of text greater than a single character
			/**
			 * I know this looks goofy, but without some sort of check to make sure that the document is done processing, this would fire
			 * removeReplaceAndUpdate() in DriverDocumentsTab and screw all the highlighting up. There may be a better way to do this...
			 */
			if (GUIMain.processed) {
				DriverDocumentsTab.shouldUpdate = true; //We want to update no matter what since the user is dealing with a chunk of text
				Logger.logln(NAME + "User deleted multiple characters in text, will update");
			}
		}
		
		fb.remove(offset, length);
	}
	
	/**
	 * Pretty much the same thing as checkAddingEllipses only it's not receiving text, but checking the document pane at the indices given for
	 * the text instead of getting it as a parameter. Essentially checkAddingEllipses but backwards.
	 * @param offset
	 */
	private void checkRemoveEllipses(int offset) {
		isEOS = EOS.contains(GUIMain.inst.getDocumentPane().getText().substring(offset, offset+1)); //checks to see if the deleted character is an EOS character
		
		if (isEOS && EOS.contains(GUIMain.inst.getDocumentPane().getText().substring(offset-1, offset))) { //if it was AND the character before it is ALSO an EOS character...
			watchForEOS = true;
		} else if (!isEOS && !watchForEOS) { //The user deleted a character and didn't delete one previously, nothing to do, update.
			DriverDocumentsTab.shouldUpdate = true;
		}
		
		if (watchForEOS && !isEOS) { //if the user previously deleted an EOS character AND the one they just deleted is not an EOS character, we should update.
			watchForEOS = false;
			DriverDocumentsTab.shouldUpdate = true;
		}
	}
	
	/**
	 * Checks to see if the text we're deleting is an abbreviation, and only updates when ready.
	 * @param offset
	 */
	private void checkRemoveAbbreviations(int offset) {
		String textBeforeDeletion = GUIMain.inst.getDocumentPane().getText().substring(offset-2, offset+1);

		for (int i = 0; i < notEndsOfSentence.length; i++) {
			if (notEndsOfSentence[i].contains(textBeforeDeletion))
				DriverDocumentsTab.shouldUpdate = false;
		}		
	}
}