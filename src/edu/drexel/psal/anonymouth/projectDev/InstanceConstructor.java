package edu.drexel.psal.anonymouth.projectDev;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.drexel.psal.jstylo.generics.CumulativeFeatureDriver;
import edu.drexel.psal.jstylo.generics.Analyzer;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.WekaInstancesBuilder;
import com.jgaap.generics.*;

import weka.core.FastVector;
import weka.core.Instances;

/**
 * Constructs instances using Weka and JStylo. Features are extracted.
 * @author Andrew W.E. McDonald
 *
 */
public class InstanceConstructor {
	
	/**
	 * private variable to hold the attributes of the training documents.
	 */
	private ArrayList<String> setAttributes;

	/**
	 * public method to retrieve the attributes of the training documents.
	 * @return
	 * 	String array containing attributes of training documents, each index of array holds one attribute, with indices corresponding to indices of String[] returned by @getTrainingInstances .
	 */
	public ArrayList<String> getAttributeSet(){
		return setAttributes;
	}
	
	/**
	 * private variable to hold the instances of the training documents.
	 */
	private Double[][] trainingInstances;
	
	/**
	 * public method to retrieve the instances of the training documents.
	 * @return
	 * 	double array containing instances of training documents.
	 */
	public Double[][] getTrainingInstances(){
		return trainingInstances;
	}
	
	
	/**
	 * private variable to hold the instances of the testing document(s).
	 */
	private Double[][] testingInstances;
	
	/**
	 * public method to retrieve the instances of the training documents.
	 * @return
	 * 	double array containing instances of training documents.
	 */
	public Double[][] getTestingInstances(){
		return testingInstances;
	}
	
	private CumulativeFeatureDriver theseFeaturesCfd;
	
	
	Instances trainingDat,testingDat;
	
	private boolean printStuff;
	
	WekaInstancesBuilder wid;
	
	
	/**
	 * Constructor for InstanceConstructor, accepts boolean variable that tells WekaInstancesBuilder whether to expect sparse data or not. (if unsure, set false) 
	 * @param isSparse - boolean, true if expecting sparse data, false otherwise or if unsure.
	 * @param cfd - cumulative feature driver. Contains all features that will be extracted from the documents
	 */
	public InstanceConstructor(boolean isSparse, CumulativeFeatureDriver cfd, boolean printStuff){
		wid = new WekaInstancesBuilder(isSparse);
		theseFeaturesCfd = cfd;
		this.printStuff =printStuff;
		Logger.logln("InstanceConstuctor constructed");
	}
	
	
	/**
	 * method runInstanceBuilder uses an instance of WekaInstancesBuilder to extract the features of both the input
	 * trainDocs and testDoc(s).  
	 * @param trainDocs list of Document objects to train the Weka classifier on
	 * @param testDocs list (may be a single object list) of Document object(s) to classify.
	 * @return
	 * 	true if no errors
	 */
	public boolean runInstanceBuilder(List<Document> trainDocs,List<Document> testDocs){
		Logger.logln("Runnng JStylo WekaInstancesBuilder from runInstanceBuilder in InstanceConstructor");
			int eye = 0;
		if(printStuff == true){
			char[] cRay = testDocs.get(0).getProcessedText();
			System.out.println("PRE-INSTANCE BUILDING:\n");
			for(eye = 0;eye<cRay.length;eye++)
				System.out.print(cRay[eye]);
			System.out.println();
		}
		try{

		wid.prepareTrainingSet(trainDocs, theseFeaturesCfd);
	
		//if(overrideAttributes == true)
			//wid.forceAttributeList(forcedAttributes);
		//else
		//	forcedAttributes =(FastVector) wid.getActualAttributeList().copy();
		wid.prepareTestSet(testDocs);
		}
		catch(Exception e){
			Logger.logln("Could not prepare either test or training set.");
			e.printStackTrace();
			return false;
		}
		
		// Initialize two new instances to hold training and testing instances (attributes and data)
		trainingDat=wid.getTrainingSet();
		//System.out.println(trainingDat.toString());
		//System.out.println("okay");
		testingDat=wid.getTestSet();
		setAttributes=getAttributes(trainingDat);
		trainingInstances=getInstances(trainingDat);
		testingInstances=getInstances(testingDat);
		if(printStuff == true){
			char[] cRay = testDocs.get(0).getProcessedText();
			System.out.println("POST-INSTANCE BUILDING:\n");
			for(eye = 0;eye<cRay.length;eye++)
				System.out.print(cRay[eye]);
			System.out.println();
			System.out.println(testingDat.toString());
			System.exit(7);
		}
		return true;
	}
	
	public boolean onlyBuildTrain(List<Document> trainDocs){
		Logger.logln("Only building train set");
		try{
			wid.prepareTrainingSet(trainDocs, theseFeaturesCfd);
		}
		catch(Exception e){
			Logger.logln("Could not prepare training set");
			e.printStackTrace();
		}
		trainingDat=wid.getTrainingSet();
		setAttributes=getAttributes(trainingDat);
		trainingInstances=getInstances(trainingDat);
		return true;
		
	}
	
	/**
	 * returns full set of training data in arff style formatting (contains list of attributes and data)
	 * @return
	 * 	Instances object containing training data
	 */
	public Instances getFullTrainData(){
		return trainingDat;
	}
	
	/**
	 * returns full set of test data in arff style formatting (contains list of attributes and data)
	 * @return
	 * 	Instances object containing testing data
	 */
	public Instances getFullTestData(){
		return testingDat;
	}
	
	
	/**
	 * Accepts Weka Instances object and returns the stripped attributes. Stripping performed by 'AttributeStripper'
	 * @param currentInstance - Weka Instances object (arff format)
	 * @return
	 */
	public ArrayList<String> getAttributes(Instances currentInstance){
		int i=0;
		String tempString;
		ArrayList<String> tempAttrib= new ArrayList<String>(currentInstance.numAttributes());
		//System.out.println("Number of attributes= "+currentInstance.numAttributes());
		//System.out.println("length of new string array: "+tempAttrib.length);
		for(i=0;i<currentInstance.numAttributes();i++){
			//System.out.println("i<currentInstance.numAttributes() => "+currentInstance.numAttributes()+" i = " +i);
			tempString = currentInstance.attribute(i).toString();
			if(tempString.contains("authorName")){
					tempAttrib.add(i,tempString);
					continue;
			}
			tempAttrib.add(i,AttributeStripper.strip(tempString));
			//System.out.println("TrainingAttribs: "+tempAttrib[i]);
		}
		return tempAttrib;
	}
	
	/**
	 * Accepts JSylo's Instances object and returns the instances (@data) portion of the ".arff file" (not really a file at this point though).
	 * @param currentInstance - JStylo's Instances object (arff format)
	 * @return
	 */
	public Double[][] getInstances(Instances currentInstance){
		int i=0;
		int j=0;
		int placeHolder;
		int numAttribs = setAttributes.size();
		int numInstances = currentInstance.numInstances();
		String tempString;
		String otherTempString;
		Double[][] tempInstance;
		
		tempInstance= new Double[numInstances][numAttribs];
		int skip = 0;
		for(i=0;i<numAttribs;i++){
			if(currentInstance.attribute(i).toString().contains("authorName")){
				skip=i;
				break;
			}
		}
		for(i=0;i<numInstances;i++){
				j=0;
				tempString =currentInstance.instance(i).toString()+",";
				//System.out.println("Original line: "+tempString);
				//tempInstance[i][j] = null; // set author name/ID to null rather than simply omit - allows attribute lists indices to stay synchronized.
				//j++;
				while(!tempString.equals("")){
					placeHolder =tempString.indexOf(",");
					//System.out.println(placeHolder);
					otherTempString = tempString.substring(0,placeHolder);
					//System.out.println(otherTempString);
					if(j==skip){
						tempInstance[i][j] = null; // set author name/ID to null rather than simply omit - allows attribute lists indices to stay synchronized
					}
					else{
						tempInstance[i][j] = Double.valueOf(otherTempString).doubleValue(); 
					}
					tempString = tempString.substring(placeHolder+1);	
					//System.out.print(" | " +tempInstance[i][j]+" | ");
					j++;
				}
		}
		return tempInstance;
	}
	

	
}
