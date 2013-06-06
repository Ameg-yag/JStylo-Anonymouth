package edu.drexel.psal.anonymouth.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import edu.drexel.psal.anonymouth.gooie.DriverDocumentsTab;
import edu.drexel.psal.anonymouth.gooie.ThePresident;
import edu.drexel.psal.jstylo.generics.*;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;

import edu.drexel.psal.jstylo.analyzers.WekaAnalyzer;
import edu.drexel.psal.anonymouth.utils.DocumentParser;
import edu.drexel.psal.anonymouth.utils.DocumentTagger;

import com.jgaap.generics.Document;

/**
 * Does the magic with the documents
 * @author Andrew W.E. McDonald
 *
 */
public class DocumentMagician {
	
	private final String NAME = "( "+this.getClass().getSimpleName()+" ) - ";
	
	private String writeDirectory = ThePresident.DOC_MAGICIAN_WRITE_DIR;
	
	/**
	 * private boolean variable, true if sparse data is expected, false otherwise
	 */
	private boolean isSparse;
	
	/**
	 * Create instance of CumulativeFeatureDriver
	 */
	CumulativeFeatureDriver theseFeaturesCfd;
	
	
	public static int numProcessRequests = 0;
	
	public static String sep = File.separator;

	ProblemSet jamDocSet;
	
	public static int numSampleAuthors;
	
	public static boolean classifier_saved = false;
	
	public static String classifier_path = "";
	
	private List<Document> trainSet;
	
	private List<Document> toModifySet;
	
	private List<Document> noAuthorTrainSet;
	
	private List<Document> authorSamplesSet;
	
	private Map<String,Map<String,Double>> wekaResultMap;
	
	private InstanceConstructor instanceSet; 
	
	private InstanceConstructor authorInstanceConstructor;
	
	private InstanceConstructor noAuthorTrainInstanceConstructor;
	
	private ArrayList<String> attributeSet;
	
	private ArrayList<String> noAuthorTrainAttributeSet;
	
	private ArrayList<String> authorAttributeSet;
	
	public ArrayList<String> getAttributeSet(){
		return attributeSet;
	}
	
	private Double[][] authorAndTrainingInstances;
	
	public Double[][] getAuthorAndTrainingInstances(){
		return authorAndTrainingInstances;
	}
	
	private Double[][] trainingInstances;
	
	public Double[][] getTrainingInstances(){
		return trainingInstances;
	}
	
	private Double[][] authorInstances;
	
	public Double[][] getAuthorInstances(){
		return authorInstances;
	}
	
	private Double[][] toModifyInstanceSet;
	
	public Double[][] getToModifyInstanceSet(){
		return toModifyInstanceSet;
	}
	
	public synchronized Map<String,Map<String,Double>> getWekaResultList(){
		return wekaResultMap;
	}
	
	/**
	 * Sets the weka analysis driver
	 * @param driver
	 * @return
	 */
	public String setWekaAnalysisDriver(String driver){// not used yet...
		Logger.logln(NAME+"called setWekaAnalysisDriver in DocumentMagician. Driver is: "+driver);
		return driver;
	}
	
	private static ArrayList<String> trainTitlesList;
	
	
	public static ArrayList<String> getTrainTitlesList(){
		return trainTitlesList;
	}
	
	private Set<String> trainSetAuthors;
	
	public Set<String> getTrainSetAuthors(){
		return trainSetAuthors;
	}
	
	private ArrayList<String> toModifyTitlesList;
	
	public ArrayList<String> getToModifyTitlesList(){
		return toModifyTitlesList;
	}
	
	private Instances authorAndTrainDat;
	
	public Instances getAuthorAndTrainDat(){
		return authorAndTrainDat;
	}
	
	private Instances toModifyDat;
	
	public Instances getToModifyDat(){
		return toModifyDat;
	}
	
	private Instances noAuthorTrainDat;
	
	public Instances getNoAuthorTrainDat(){
		return noAuthorTrainDat;
	}
	
	private Instances authorOnlyDat;
	
	public Instances getAuthorOnlyDat(){
		return authorOnlyDat;
	}
	
	private String pathToModDoc;
	
	private String modifiedDocument;
	
	public void setModifiedDocument(String theDoc){
		modifiedDocument = theDoc;
	}
	
	public static String authorToRemove;
	
	private Classifier theClassifier;
	
	/**
	 * Constructor for DocumentMagician
	 * @param isSparse 
	 */
	public DocumentMagician(boolean isSparse){
		Logger.logln(NAME+"Created new DocumentMagician, is sparse?  ... "+isSparse);
		this.isSparse = isSparse;
	}
	
	/**
	 * Runs the primary document operations
	 * @param cfd
	 */
	public void runPrimaryDocOps(CumulativeFeatureDriver cfd){
		constructFeatureDrivers(cfd);
		buildTrainAndToModifyInstances();
		Logger.logln(NAME+"Done with runPrimaryDocOps");
	}
	
	/**
	 * Runs the secondary document operations
	 */
	public void runSecondaryDocOps(){
		buildAuthorAndNoAuthorTrainInstances();
		Logger.logln(NAME+"Done with runSecondaryDocOps");
	}
	
	/**
	 * Re-classifies the document that has been modified
	 */
	public void reRunModified(){ // this may be unnecessary - it may be possible to re-use 'instanceSet'... TODO: look into this.
		Logger.logln(NAME+"Called reRunModified (DocumentMagician)");
		InstanceConstructor oneAndDone = new InstanceConstructor(isSparse,theseFeaturesCfd,false);
		//System.out.println("**********OLD TEXT************** ");
		//System.out.print(toModifySet.get(0).stringify());
		String pathToTempModdedDoc = writeDirectory+ThePresident.sessionName+"_"+numProcessRequests+".txt";
		Logger.logln(NAME+"Saving temporary file: "+pathToTempModdedDoc);
		try {
			File tempModdedDoc = new File(pathToTempModdedDoc);
			if(ThePresident.SHOULD_KEEP_AUTO_SAVED_ANONYMIZED_DOCS == false)
				tempModdedDoc.deleteOnExit();
			FileWriter writer = new FileWriter(tempModdedDoc);
			writer.write(modifiedDocument);
			writer.close();
		} catch (IOException e) {
			//TODO: log this. 
			e.printStackTrace();
		}
		Document newModdedDoc = new Document(pathToTempModdedDoc,toModifySet.get(0).getAuthor(),toModifySet.get(0).getTitle());
		Logger.logln(NAME+"Document opened");
		while(!toModifySet.isEmpty())
			toModifySet.remove(0);
		toModifySet.add(0,newModdedDoc);
		oneAndDone.runInstanceBuilder(trainSet,toModifySet);
		authorAndTrainingInstances = oneAndDone.getTrainingInstances();
		toModifyInstanceSet = oneAndDone.getTestingInstances();
		authorAndTrainDat = oneAndDone.getFullTrainData();
		toModifyDat = oneAndDone.getFullTestData();
	}
	
	/**
	 * sets the CumulativeFeatureDriver
	 * @param cfd
	 */
	public void constructFeatureDrivers(CumulativeFeatureDriver cfd){
		Logger.logln(NAME+"Setting CumulativeFeatureDriver in DocumentMagician");
		theseFeaturesCfd = cfd;
	}
	
	/**
	 * Builds an instance set of the user's sample data and the 'other' sample data combined, and of the user's document to modify
	 */
	public void buildTrainAndToModifyInstances(){
		Logger.logln(NAME+"Building train (with author) and toModify instances");
		instanceSet = new InstanceConstructor(isSparse,theseFeaturesCfd,false);
		int i;
		int sizeTrainSet = trainSet.size();
		trainSetAuthors = new HashSet<String>(sizeTrainSet);
		for(i=0;i< sizeTrainSet ;i++){
			trainSetAuthors.add(trainSet.get(i).getAuthor());
		}
		String pathToTempModdedDoc = writeDirectory+ThePresident.sessionName+"_unmodified.txt";
		Logger.logln(NAME+"Saving temporary file: "+pathToTempModdedDoc);
		try {
			File tempModdedDoc = new File(pathToTempModdedDoc);
			if (!tempModdedDoc.exists())
				tempModdedDoc.createNewFile();
			FileWriter writer = new FileWriter(tempModdedDoc);
			writer.write(toModifySet.get(0).stringify());
			writer.close();
		} catch (IOException e) {
			//TODO: log this. 
			e.printStackTrace();
		}
		toModifySet.get(0).setAuthor(authorToRemove);
		instanceSet.runInstanceBuilder(trainSet,toModifySet);
		
		//System.out.println("THE toModifySet: "+toModifySet.toString());
		attributeSet = instanceSet.getAttributeSet();
		//System.out.println(instanceSet.getFullTestData().toString());
		authorAndTrainingInstances = instanceSet.getTrainingInstances();
		toModifyInstanceSet = instanceSet.getTestingInstances();
		authorAndTrainDat = instanceSet.getFullTrainData();
		toModifyDat = (instanceSet.getFullTestData());
		//System.out.println(toModifyDat.toString());
		//runWeka();
	}
	
	/**
	 * Builds instances of the user's sample documents, as well as the 'other' sample documents (each done separately) 
	 */
	public void buildAuthorAndNoAuthorTrainInstances(){
		Logger.logln(NAME+"Building author and no author train instances");
		authorInstanceConstructor = new InstanceConstructor(isSparse,theseFeaturesCfd,false);
		noAuthorTrainInstanceConstructor = new InstanceConstructor(isSparse,theseFeaturesCfd,false);
		int i;
		int authSampleSetSize = authorSamplesSet.size();
		//for (i=0;i<authSampleSetSize;i++){
			//System.out.println("Author: "+authorSamplesSet.get(i).getAuthor());
			//authorSamplesSet.get(i).setAuthor(dummyName);
		//}
		//for(i=0;i<noAuthorTrainSet.size();i++)
			//System.out.println("Author: "+noAuthorTrainSet.get(i).getAuthor());
		
		// build each train instance set seperately so that each attribute set will contain all features
		
		noAuthorTrainInstanceConstructor.onlyBuildTrain(noAuthorTrainSet);
		noAuthorTrainAttributeSet = noAuthorTrainInstanceConstructor.getAttributeSet();
		trainingInstances = noAuthorTrainInstanceConstructor.getTrainingInstances();
		noAuthorTrainDat = noAuthorTrainInstanceConstructor.getFullTrainData();
		
		
		authorInstanceConstructor.onlyBuildTrain(authorSamplesSet);
		authorAttributeSet = authorInstanceConstructor.getAttributeSet();
		authorInstances = authorInstanceConstructor.getTrainingInstances();
		authorOnlyDat = authorInstanceConstructor.getFullTrainData();
		for(i=0;i<authSampleSetSize;i++){
			if(authorSamplesSet.get(i).getAuthor().equals(ThePresident.DUMMY_NAME))
				authorSamplesSet.get(i).setAuthor(authorToRemove);
		}
	}
	
	/**
	 * Runs Weka and classifies the document to modify based on the selected classifier, with the selected options. The user's document to modify is classified 
	 * against all sample documents (user's samples, and 'other' samples)
	 * @throws Exception
	 */
	public synchronized void runWeka(){
		Logger.logln(NAME+"Called runWeka");
		WekaAnalyzer waz = new WekaAnalyzer(theClassifier);
		// hack this is just for testing purposes
		if(ThePresident.CLASSIFIER_SAVED == false){
			wekaResultMap = waz.classifyAndSaveClassifier(authorAndTrainDat,toModifyDat,toModifySet, ThePresident.PATH_TO_CLASSIFIER);// ?
			ThePresident.CLASSIFIER_SAVED = true;
		}
		else{
			wekaResultMap = waz.classifyWithPretrainedClassifier(toModifyDat,toModifyTitlesList, trainSetAuthors);// ?
		}
		Logger.logln(NAME+"Weka Done");
	}
	
	
	/**
	 * Performs operations that turn the documents into data, and calls other methods in this class in order to do this.
	 * @param pSet
	 * @param cfd
	 * @param classifier
	 */
	public void initialDocToData(ProblemSet pSet,CumulativeFeatureDriver cfd, Classifier classifier ){//,List<Map<String,Document>> forTraining, List<Document> forTesting){
		Logger.logln(NAME+"Entered initialDocToData in DocumentMagician");
		theClassifier = classifier;
		//System.out.println(pSet.toString());
		ProblemSet pSetCopy = new ProblemSet(pSet);
		trainSet = pSetCopy.getAllTrainDocs();
		
		toModifySet = pSetCopy.getTestDocs(); // docToModify is the test doc already
		Logger.logln(NAME+"True test doc author: "+toModifySet.get(0).getAuthor()); //TODO: this is an issue...
		
		//String titleOfDocToModify = (pSet.getTestDocs().get(0)).getTitle();
		authorToRemove = ProblemSet.getDummyAuthor(); 
		Logger.logln(NAME+"Dummy author: "+authorToRemove);
		authorSamplesSet = pSetCopy.removeAuthor(authorToRemove);
		authorSamplesSet.remove(toModifySet.get(0));
		//dummyName = ProblemSet.getDummyAuthor();
		//System.out.println("TRAIN SET: "+trainSet.toString());
		//System.out.println("TO MODIFY SET: "+toModifySet.toString());
		//System.out.println("AUTHOR TO REMOVE: "+authorToRemove);
		//System.out.println("AUTHOR SAMPLES SET: "+authorSamplesSet.toString());
		noAuthorTrainSet = pSetCopy.getAllTrainDocs();
		/*
		boolean loadIfExists = false;
		DocumentTagger otherSampleTagger = new DocumentTagger(noAuthorTrainSet,loadIfExists);
		DocumentTagger authorSampleTagger = new DocumentTagger(authorSamplesSet,loadIfExists);
		DocumentTagger toModifyTagger = new DocumentTagger(toModifySet,loadIfExists);	
		otherSampleTagger.run();
		authorSampleTagger.run();
		toModifyTagger.run();
		*/
		/*EditorTabDriver.otherSampleTagger.setDocList(noAuthorTrainSet,loadIfExists);
		EditorTabDriver.authorSampleTagger.setDocList(authorSamplesSet,loadIfExists);
		EditorTabDriver.toModifyTagger.setDocList(toModifySet, loadIfExists);
		EditorTabDriver.otherSampleTagger.run();
		EditorTabDriver.authorSampleTagger.run();
		EditorTabDriver.toModifyTagger.run();
		/*
		Logger.logln(NAME+"Attempting to load and parse documents...");
		try {
			DocumentParser.setDocs(noAuthorTrainSet,authorSamplesSet,toModifySet);
		} catch (Exception e) {
			Logger.logln(NAME+"ERROR: Could not load documents or for parsing!!!",LogOut.STDERR);
			System.out.println("docToModify (in DocumentMagician: "+toModifySet.get(0).getFilePath());
			e.printStackTrace();
		}
		Logger.logln(NAME+"Documents successfully loaded and/or parsed...");
		*/
		int i = 0;
		int lenTSet = noAuthorTrainSet.size();
		trainTitlesList = new ArrayList<String>(lenTSet);
		System.out.println("Training document titles:");
		for (i=0;i<lenTSet;i++){
			trainTitlesList.add(i,noAuthorTrainSet.get(i).getTitle());
		}
		
		int lenTMSet = toModifySet.size();
		toModifyTitlesList = new ArrayList<String>(lenTMSet);
		for(i=0; i<lenTMSet; i++){
			toModifyTitlesList.add(i,toModifySet.get(i).getTitle());
		}
		numSampleAuthors = pSetCopy.getAuthors().size();
		//System.out.println("NO AUTHOR TRAIN SET: "+noAuthorTrainSet.toString());
		Logger.logln(NAME+"Calling runPrimaryDocOps");
		runPrimaryDocOps(cfd);
		Logger.logln(NAME+"Calling runSecondaryDocOps");
		runSecondaryDocOps();
		Logger.logln(NAME+"Exiting initialDocToData in DocumentMagician");
	}
	
	/**
	 * returns a mapping of the name of the 2d array of instance data to the array; only done for transfer purposes.
	 * @return
	 */
	public HashMap<String,Double[][]> getPackagedInstanceData(){
		HashMap<String,Double[][]> dataForAnalysis = new HashMap<String,Double[][]>(3);
		dataForAnalysis.put("training",trainingInstances);
		dataForAnalysis.put("author",authorInstances);
		dataForAnalysis.put("modify",toModifyInstanceSet);
		return dataForAnalysis;
	}

	/**
	 * returns a mapping of the Instances objects names, to their respective unmodified Instances objects; only done for transfer purposes.
	 * @return
	 */
	public HashMap<String,Instances> getPackagedFullInstances(){
		HashMap<String,Instances> InstancesForAnalysis = new HashMap<String,Instances>(4);
		InstancesForAnalysis.put("authorAndTrain",authorAndTrainDat);
		InstancesForAnalysis.put("noAuthorTrain",noAuthorTrainDat);
		//System.out.println("AUTHOR:\n"+authorOnlyDat.toString());
		//System.out.println("Train:\n"+authorAndTrainDat.toString());
		//System.out.println(noAuthorTrainDat.toString());
		InstancesForAnalysis.put("toModify",toModifyDat);
		InstancesForAnalysis.put("authorOnly",authorOnlyDat);
		//System.out.println(toModifyDat.toString());
		//System.out.println(Arrays.deepToString(toModifyInstanceSet));
		//System.out.println(attributeSet.toString());
		//System.out.println(authorAttributeSet.toString());
		return InstancesForAnalysis;
		
	}
	
	/**
	 * Returns all three sets of documents used. 
	 * @return
	 */
	public ArrayList<List<Document>> getDocumentSets(){
		ArrayList<List<Document>> theDocs = new ArrayList<List<Document>>(3);
		theDocs.add(noAuthorTrainSet); // index 0
		theDocs.add(authorSamplesSet); // 1
		theDocs.add(toModifySet); // 2
		return theDocs;
	}
	
	/**
	 * Returns all three attribute sets. One attribute set is from the 'buildTrainAndToModify()' method, and the other two are from the 'buildAuthorAndNoAuthorTrainInstances()' method.
	 * There have to be three separate attribute sets because of the way that JStylo creates the Training and Test sets: If a given attribute does not appear in the training set of documents,
	 * it is ignored in the test set. To make sure all available/necessary attributes (features) are extracted, some of the extractions are done independently as training sets without test sets. 
	 * @return
	 */
	public ArrayList<ArrayList<String>> getAllAttributeSets(){
		ArrayList<ArrayList<String>> allAttributeSets = new ArrayList<ArrayList<String>>(3);
		allAttributeSets.add(0,attributeSet); //this attribute set is used for the authorAndTrain set and toModify set
		allAttributeSets.add(1,noAuthorTrainAttributeSet);
		allAttributeSets.add(2,authorAttributeSet);
		return allAttributeSets;
	}
	
	/**
	 * returns path to document to modify (this may not be used)
	 * @return
	 */
	public String getPathToModDoc(){
		return pathToModDoc;
	}
}


