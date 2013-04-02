package edu.drexel.psal.anonymouth.suggestors;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jgaap.JGAAPConstants;
import com.jgaap.generics.EventGenerationException;

import edu.drexel.psal.anonymouth.gooie.EditorTabDriver;
import edu.drexel.psal.anonymouth.projectDev.Attribute;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


/**
 * TheOracle is the (absract) superclass for all suggestion classes. It provides the structure and methods for the suggestion classes, leaving only the 'algorithm()'
 * method to be implemented in each suggestion (which requires setting both suggestions and the highlight map). 
 * @author Andrew W.E. McDonald
 *
 */
abstract public class TheOracle implements Runnable {
	
	protected static MaxentTagger mt = null;
	private static boolean taggerPresent = false;
	protected String featureName;
	protected String stringInBraces = "";
	protected double featureTargetValue;
	protected double featurePresentValue;
	protected boolean isTargetGreaterThanPresent;
	protected boolean  noChangeNeeded = false;
	protected boolean noSuggestionYet = false;
	private String suggestionHighToLow = "";
	private String suggestionLowToHigh = "";
	protected HashMap<Color,ArrayList<int[]>> highlightMap;
	protected boolean suggestionSet = false;
	protected boolean shouldNotHighlight = false;
	private static String theDocument;
	protected boolean doesNotGetSuggestion = false;
	//private Color[] colorChoices = {Color.yellow,Color.red,Color.orange,Color.pink,Color.green,Color.magenta,Color.lightGray,Color.cyan,Color.gray,Color.blue,Color.darkGray};
	private Color[] transColorChoices = {new Color(1.0f,1.0f,.2f,.4f), new Color(1.0f,0,0,.4f),  new Color(0f,1.0f,0f,.4f) , new Color(0f,0f,1.0f,.4f),new Color(1.0f,.4f,0,.4f), new Color(1.0f,.2f,1.0f,.4f)};

	private static int colorIndex = 0;
	protected Attribute attrib;
	protected StringFormulator sf;
	
	/**
	 * Constructor (empty)
	 */
	protected TheOracle(){
		
	}

	/**
	 * Initializer for TheOracle. Sets all values needed for the subclasses to effectively implement suggestions.
	 * @param attrib the attribute that will be getting evaluated / suggestions.
	 */
	public void initialize(Attribute attrib){
		if(taggerPresent == false)
			initMaxentTagger();
			
		Logger.logln("Oracle: suggeston being called for : "+attrib.getConcatGenNameAndStrInBraces()+",present value,"+attrib.getToModifyValue()+",target value,"+attrib.getTargetValue());
		this.attrib = attrib;
		highlightMap = new HashMap<Color,ArrayList<int[]>>();
		this.featureName = attrib.getGenericName().toString();
		this.stringInBraces = attrib.getStringInBraces();
		this.featureTargetValue = attrib.getTargetValue();
		this.featurePresentValue = attrib.getToModifyValue();
		if(featureName.contains("PERCENT")){
			if((100*featureTargetValue) > featurePresentValue)
				isTargetGreaterThanPresent = true;
			else if (featurePresentValue > (100*featureTargetValue))
				isTargetGreaterThanPresent = false;
		}
		else{
			if(featureTargetValue > featurePresentValue)
				isTargetGreaterThanPresent = true;
			else if (featurePresentValue > featureTargetValue)
				isTargetGreaterThanPresent = false;
		}
		suggestionHighToLow = "The present value of this feature is: '"+featurePresentValue+"' and it should be lowered to '"+featureTargetValue+"'. " +
				"Helpful insights (and possibly highlighting) that will aid in accomplishing this task are (or will soon be) in the works.";
		suggestionLowToHigh = "The present value of this feature is: '"+featurePresentValue+"' and it should be increased to '"+featureTargetValue+"'. " +
				"Helpful insights (and possibly highlighting) that will aid in accomplishing this task are (or will soon be) in the works.";
		Logger.logln("Feature suggestor for "+featureName+" has been inititialized.");
		Logger.logln("Target is greater than present (?): "+isTargetGreaterThanPresent);
		if(EditorTabDriver.isUsingNineFeatures == true){
			StringFormulator sf = new StringFormulator(EditorTabDriver.attribs);
			sf.organizeFeatures();
			sf.getGeneralNineFeatureAdvice();
			//System.out.println(sf.charSpaceSuggestion);
			this.sf = sf;
		}
	}
	
	/**
	 * run method - once class has been instantiated and initialized, this method may be called to compute the suggestion 
	 */
	public void run(){
		try {
			algorithm();
		} catch (EventGenerationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a copy of the document to a subclass of TheOracle 
	 * @return
	 * 	String, theDocument (copy)
	 */
	protected String getDocument(){
		return theDocument.substring(0);
	}
	
	/**
	 * abstract method to implement suggestion algorithm. The 'initialize' method MUST be run prior to calling this. Should set Strings 'suggestionLowToHigh' and 'suggestionHighToLow', as well as set the HashMap<Color,String[]> 'highlightMap'
	 * which will map a Color (to be used for highlighting) to an array of strings (which will be highlighted by the specified Color (the 'key').
	 */
	protected abstract void algorithm() throws EventGenerationException;
	
	
	/**
	 * Returns String suggestion to bring the present features value up to the target value 
	 * @return
	 * 	String, suggestion
	 */
	public String getLowToHigh(){
		return suggestionLowToHigh;
	}
	
	/**
	 * Sets the suggestion for going from low to high
	 * @param suggestionLowToHigh
	 * @return true
	 */
	public boolean setSuggestionLowToHigh(String suggestionLowToHigh){
		this.suggestionLowToHigh = suggestionLowToHigh;
		return true;
	}
	
	/**
	 * Sets suggestion for going from high to low
	 * @param suggestionHighToLow
	 * @return
	 */
	public boolean setSuggestionHighToLow(String suggestionHighToLow){
		this.suggestionHighToLow = suggestionHighToLow;
		return true;
	}
	
	/**
	 *Returns String suggestion to bring the present feature's value down to the target value 
	 * @return
	 * 	String, suggestion
	 */
	public String getHighToLow(){
		return suggestionHighToLow;
	}

	/**
	 * Returns the appropriate suggestion for the target/present value pair. (i.e. if the present value is lower than the target value, 'suggestionLowToHigh' is 
	 * returned; if the present value is higher than the target value, 'suggestionHighToLow' is returned.
	 * @return
	 * 	String, suggestion
	 */
	public String getSuggestion(){
		if(isTargetGreaterThanPresent){
			return suggestionLowToHigh.substring(0);
		}
		else if (isTargetGreaterThanPresent == false){
			return suggestionHighToLow.substring(0);
		}
		else
			return "noChangeNeeded";
	}
	
	/**
	 * Returns the HashMap that maps a color to the various substrings of the document to be highlighted by the specified color (the key)
	 * @return
	 */
	public HashMap<Color,ArrayList<int[]>> getHighlightMap(){
		return highlightMap;
	}
	
	/**
	 * The 'getProphecy()' method creates and returns an instance of 'Prophecy' - which contains the feature name (a String), the suggestion (a String),
	 * and the highlightMap (a HashMap<Color,String[]>)
	 * @return
	 * 	Prophecy object to be used by 'editorTabDriver'
	 */
	public Prophecy getProphecy(){
		String suggestion = "If you see this message, something didn't work properly.";
		if(noChangeNeeded == false)
			suggestion = getSuggestion();
		else{
			if(noChangeNeeded == true){
				suggestion = "It Appears as if the present value of this feature in your document is within the acceptable range of potential target values." +
						" However, because of the interrelated nature of stylometic features, it may be useful to re-visit this feature after you have made changes" +
						" to other features.\n\n\nNote: Because of the clustering algorithm and method to choose the optimal target value used, if your writing style is similar to the styles of\n" +
						" the sample authors whos documents you submitted, or if you have more sample documents than each of your sample authors do,\n" +
						" it is possible that many features will naturally fall into this catagory.";
			}
			//if (noChangeNeeded == true || shouldNotHighlight == true){
			//	ArrayList<int[]> empty = new ArrayList<int[]>(1);
			//	empty.add(new int[]{0,0});
			//	highlightMap.put(Color.white,empty);
			//}
		}
		Prophecy utterance = new Prophecy(featureName,suggestion,highlightMap,noChangeNeeded);
		suggestionSet = false;
		return utterance;
	}
	
	/**
	 * Sets the document to be used by the suggestion algorithms
	 * @param docString - the document (as a String) 
	 */
	public static void setTheDocument(String docString){
		theDocument = docString.replaceAll("\\p{C}", " ");;
	}
	
	/**
	 * Iterates through list of available highlight colors, each call updates the index
	 * @return
	 * 	the next color 
	 */
	public Color getNextColor(){
		int numColors = transColorChoices.length;
		colorIndex+=1;
		if(colorIndex >= numColors){
			colorIndex =1;
		}
		return transColorChoices[colorIndex-1];
	}
	
	/**
	 * returns the current color, does not iterate to next color (should generally be used after 'getNextColor()' to get that color again)
	 * @return
	 * 	the current color
	 */
	public Color getCurrentColor(){
		return transColorChoices[colorIndex-1];
	}
	
	/**
	 * Resets color index
	 * @return 
	 * 	true
	 */
	public static boolean resetColorIndex(){
		colorIndex = 0;
		return true;
	}
	
	/**
	 * sets whether a change is needed in the present value of the feature in question
	 * @param noChangeNeeded true if no change is needed
	 */
	public void setNoChangeNeeded(boolean noChangeNeeded){
		this.noChangeNeeded = noChangeNeeded;
	}

	/**
	 *	returns whether or not a change is needed in the present value of the feature 
	 * @return
	 * 	boolean, true if no change is needed, false if it is needed.
	 */
	public boolean getNoChangeNeeded(){
		return noChangeNeeded;
	}
	
	private void initMaxentTagger(){	
		try {
			mt = new MaxentTagger("."+JGAAPConstants.JGAAP_RESOURCE_PACKAGE+"models/postagger/english-left3words-distsim.tagger");
			//mt = new MaxentTagger(".lib/external/MaxentTagger/left3words-wsj-0-18.tagger");
			taggerPresent = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}	

