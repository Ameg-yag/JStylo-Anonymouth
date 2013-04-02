
package edu.drexel.psal.anonymouth.projectDev;

import edu.drexel.psal.anonymouth.gooie.EditorTabDriver;
import edu.drexel.psal.anonymouth.suggestors.HighlightMapMaker;
import edu.drexel.psal.anonymouth.suggestors.Prophecy;
import edu.drexel.psal.jstylo.generics.Logger;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;




/**
 * Class TheMirror is responsible for dynamically creating instances (and invoking their methods) of the suggestion classes. (therefore, the chief)
 * It also updates the present value of each feature within the Attribute object prior to computing a suggestion; this helps ensure that each suggestion will 
 * deliver accurate responses without the need for re=processing/ re=classifying the document after a change is made.
 * @author Andrew W.E. McDonald
 *
 */
public class TheMirror {

	private String suggestorPackage = "edu.drexel.psal.anonymouth.suggestors";
	private String calculatorsPackage = "edu.drexel.psal.anonymouth.calculators";
	private String projectDevPackage = "edu.drexel.psal.anonymouth.projectDev";
	
	Method method;
	
	public void highlightRequestedNotSpecific(String methodName) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Logger.logln("called non-specific highlighter for: "+methodName);
		//Class<?> klass = Class.forName(suggestorPackage+"."+"HighlightMapMaker");
		HighlightMapMaker.highlightMap = new HashMap<Color,ArrayList<int[]>>();
		HighlightMapMaker hmm = new HighlightMapMaker();
		method = hmm.getClass().getDeclaredMethod(methodName,(Class<?>[])null);
		method.invoke(hmm,(Object[])null);//{new String(pac.getName()), temp[0],temp[1]});
		
		EditorTabDriver.dispHighlights();
		Logger.logln("Highlighting complete.");
	}
	
	public void highlightRequestedSpecific(String methodName, String specifier) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Logger.logln("called specific highlighter for : "+methodName+" with specifier: "+specifier);
		HighlightMapMaker.highlightMap = new HashMap<Color,ArrayList<int[]>>();
		HighlightMapMaker hmm = new HighlightMapMaker();
		method = hmm.getClass().getDeclaredMethod(methodName, String.class);
		method.invoke(hmm,specifier);//{new String(pac.getName()), temp[0],temp[1]});
		
		EditorTabDriver.dispHighlights();	
		Logger.logln("Highlighting complete.");
		
	}
		

	/**
	 * Uses the values in the Attribute object to call the appropriate suggestor, initialize it, and receive the suggestion.
	 * @param attrib
	 * @return
	 * 	 a Prophecy object containing a suggestion and color map.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Prophecy callRelevantSuggestor(Attribute attrib) throws SecurityException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException{
		Logger.logln("called callRelevantSuggestor for : "+attrib.getConcatGenNameAndStrInBraces());
		Prophecy utterance;
		Object someObject;
		Method[] theThreeMethods = new Method[3];
		
		try{
		updatePresentValue(attrib);
		} catch (Exception e){
			Logger.logln("Update present value failed. This is probably because a calculator hasn't been built for the specific attribute yet.");
		}
		if(attrib.getTargetValue() <0)
			attrib.setTargetValue(attrib.getToModifyValue());
		// the methods we need to run on the suggestors are: initialize, run, and getProphecy
		Class<?> klass = Class.forName(suggestorPackage+"."+attrib.getGenericName());
		//System.out.println("Generic Name (theChief): "+attrib.getGenericName());
		Class<?> theAttributeClass = Class.forName(projectDevPackage+"."+"Attribute");
		theThreeMethods[0] = klass.getSuperclass().getDeclaredMethod("initialize", theAttributeClass);
		theThreeMethods[1] = klass.getSuperclass().getDeclaredMethod("run",(Class<?>[])null);
		theThreeMethods[2] = klass.getSuperclass().getDeclaredMethod("getProphecy",(Class<?>[])null);
				
		Object awbjeckt = klass.newInstance();
		someObject = awbjeckt;
		theThreeMethods[0].invoke(someObject,new Object[] {attrib});//{new String(pac.getName()), temp[0],temp[1]});
		theThreeMethods[1].invoke(someObject,(Object[])null);
		utterance = (Prophecy) theThreeMethods[2].invoke(someObject,(Object[])null);
		Logger.logln("Suggestor called, utterance saved.");
		return utterance;
	}
	
	
	
	/**
	 * Updates the present value of the input Attribute
	 * @param attrib
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public double updatePresentValue(Attribute attrib) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object thisComputer;
		Method[] theTasks = new Method[3]; 
		
		
		Class<?> klass = Class.forName(calculatorsPackage+".CALC_"+attrib.getGenericName());
		//Class<?> theAttributeClass = Class.forName("projectDev.Attribute");//TODO: KEEP THIS
		theTasks[0] = klass.getSuperclass().getDeclaredMethod("initialize",new Class[] {double.class,String.class,String.class});// because canonicizers will need to be included, this may change to only 'theAttributeClass'
		theTasks[1] = klass.getSuperclass().getDeclaredMethod("run", (Class<?>[])null);
		theTasks[2] = klass.getSuperclass().getDeclaredMethod("getPresentValue", (Class<?>[])null);
		
		thisComputer = klass.newInstance();
		theTasks[0].invoke(thisComputer, new Object[] {attrib.getToModifyValue(),attrib.getGenericName().toString(),attrib.getStringInBraces()});
		theTasks[1].invoke(thisComputer, (Object[])null);
		double presentVal =(Double) theTasks[2].invoke(thisComputer, (Object[])null);
		attrib.setToModifyValue(presentVal);
		return presentVal;
	}
	
}
