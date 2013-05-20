package edu.drexel.psal.anonymouth.gooie;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author Marc Barrowclift
 * @author Andrew W.E. McDonald
 * 
 * Supported actions:
 * 
 * 1) ...
 * 2) ...
 * ...
 */
public class InputFilter extends DocumentFilter{
	
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
	
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
		fb.replace(offset, length, text.toUpperCase(), attr);
	}
}