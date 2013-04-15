/**
 * 
 */
package edu.drexel.psal.anonymouth.utils;

import java.util.ArrayList;

/**
 * @author Andrew W.E. McDonald
 *
 */
public class EOSCharacterTracker {
	
	// Basically parallel arrays... we use the replacement characters instead of the corresponding eos characters. 
	// Doing this allows us to break sentences only where we are sure we want to break them, and will allow the user more flexibility.
	private char[] realEOS = {'.', '?', '!'};
	private char[] replacementEOS = {'๏', 'ʔ', 'ǃ'};
	private ArrayList<EOS> eoses;

	public EOSCharacterTracker(){
		eoses = new ArrayList<EOS>(100); //note at this point, it's unlikely that we'll have more than 100 sentences.. but this should eventually be changed to some global parameter than is relative to the length of the document or something.
	}
	
	public void addEOS(EOS eos){
		eoses.add(eos);
	}
	
	public void removeEOS(){
		
	}
	
	/**
	 * Shifts the locations of all stored EOS objects by shiftAmount. If shiftRight is true, then it adds shiftAmount to each location.
	 * If shiftRight is false, then it subtracts shiftAmount from each location. However, any locations that are less than startIndex, won't be touched.
	 * The reason is, when you begin typing, everything behind the caret will stay where it is; but everything in front of the caret will be pushed.
	 * The same thing happens when you delete characters; anything behind your caret either stays put or is deleted, and anything in front of it is dragged backward. 
	 * @param shiftRight true to add to each EOS location, false to subtract from each EOS location
	 * @param startIndex ignore any locations that are less than this. 
	 * @param shiftAmount number to add to each location (locations past startIndex)
	 */
	public void shiftAllEOSChars(boolean shiftRight, int startIndex, int shiftAmount){
		// note: right now, we'll just loop through the whole ArrayList of EOS objects, and check each one to see if its location is >= startIndex. 
		//There is almost certainly a more efficient way to do this, but as it's a small list, and I just want to get something working, I'm going to leave it like this for now.
		int i;
		int numEOSes = eoses.size();
		if (shiftRight){ // add shiftAmount
			for (i = 0; i < numEOSes; i++){
				EOS thisEOS = eoses.get(i);
				if (thisEOS.location >= startIndex)
					eoses.get(i).location += shiftAmount;
			}
		}
		else{ // subtract shiftAmount
			for (i = 0; i < numEOSes; i++){
			EOS thisEOS = eoses.get(i);
			if (thisEOS.location >= startIndex)
				eoses.get(i).location -= shiftAmount;
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// NOTE Auto-generated method stub
		EOSCharacterTracker ect = new EOSCharacterTracker();
		ect.addEOS(new EOS('.',5));
		ect.addEOS(new EOS('!',7));
		ect.addEOS(new EOS('?',9));
		System.out.println(ect.toString());
		ect.shiftAllEOSChars(true, 4, 5);
		System.out.println(ect.toString());
	}
	
	/**
	 * Returns a string representation of this EOSCharacterTracker
	 */
	public String toString(){
		int i;
		int numEOSes = eoses.size();
		String toReturn = "[ ";
		for(i = 0; i < numEOSes; i++){
			toReturn += eoses.get(i).toString() + ", ";
		}
		toReturn = toReturn.substring(0,toReturn.length()-1) + "]";
		return toReturn;
	}

}

/**
 * Holds the EOS character at a given location in a document, with respect to the beginning of the document.
 * @author Andrew W.E. McDonald
 *
 */
class EOS {
	protected char eos;
	protected int location;
	
	/**
	 * Constructor
	 * @param eos 
	 * @param location
	 */
	public EOS( char eos, int location){
		this.eos = eos;
		this.location = location;
	}
	
	public String toString(){
		return "[ "+eos+", "+location+" ]";
	}
	
}
	