package edu.drexel.psal.jstylo.generics;

import java.util.List;

import weka.core.*;

import com.jgaap.generics.*;


//TODO update the repository to the latest version of weka

public interface API {

	/*
	
	public void prepareTrainingSet(List<Document> knownDocs, CumulativeFeatureDriver cfd)
	public void prepareTestSet(List<Document> unknownDocs)
	public String applyInfoGain(boolean changeAttributes, int N)
	private void normInstances(Instances insts)
	
	 */
	
	// feature extraction - training set
	
	/**
	 * Extracts the List of EventSets from a document using the provided CumulativeFeatureDriver
	 * @param document the document to have features extracted and made into event sets
	 * @param cumulativeFeatureDriver the driver containing the features to be extracted and the functionality to do so
	 * @return the List\<EventSet\> for the document
	 */ 
	public List<EventSet> extractEventSets(Document document,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception;
	
	/**
	 * Determines which EventSets to use for the given documents
	 * @param eventSets A List which contains Lists of EventSets (represents a list of documents' EventSets0
	 * @param cumulativeFeatureDriver the driver with the culling functionality
	 * @return The culled List\<List\<EventSet\>\> created from eventSets
	 * @throws Exception
	 */
	public List<List<EventSet>> cull(List<List<EventSet>> eventSets,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception;
	
	/**
	 * Goes over the culled List<List<EventSet>> and determines which events are histograms and which have a single<br>
	 * numerical value. Uses the information to prepare a List\<EventSet\> to extract from the test document(s)
	 * @param culledEventSets The culled List<List<EventSet>>
	 * @param cumulativeFeatureDriver The driver used to extract the EventSets
	 * @return The List\<EventSet\> to extract from the test document(s) 
	 * @throws Exception
	 */
	public List<EventSet> getRelevantEvents(List<List<EventSet>> culledEventSets,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception;
	//Any time there is a single numeric value, use "null"
	
	/**
	 * Generates the List\<Attributes\> from the List\<List\<EventSet\>\> that will be used to create the Instances object.
	 * @param culledEventSets The culled list of EventSets that have been gathered from the document set
	 * @return A List\<Attribute\> which will be used to create the Instances object 
	 * @throws Exception
	 */
	public List<Attribute> getAttributeList(
			List<List<EventSet>> culledEventSets) throws Exception;
	
	/**
	 * 
	 * @param attributes the data used to construct the Instance object
	 * @param cumulativeFeatureDriver 
	 * @param documentData
	 * @return
	 * @throws Exception
	 */
	public Instance createInstance(List<Attribute> attributes,
			CumulativeFeatureDriver cumulativeFeatureDriver,
			List<EventSet> documentData, boolean isSparse) throws Exception;
	
	//TODO remove global normalization options
	
	/**
	 * Does not support global normalization baselines!
	 * @param cumulativeFeatureDriver
	 * @param instance
	 * @throws Exception
	 */
	public void normInstance(CumulativeFeatureDriver cumulativeFeatureDriver,
			Instance instance) throws Exception;
	
	// the full training Instances object is generated
	
	/**
	 * returns list of indices of the top N features
	 * @param insts
	 * @param apply
	 * @param N
	 * @return
	 * @throws Exception
	 */
	public List<Integer> calcInfoGain(Instances insts, int N) throws Exception;
	
	/**
	 * Modifies the insts object
	 * @param chosenFeatures
	 * @param insts
	 * @throws Exception
	 */
	public void applyInfoGain(List<Integer> chosenFeatures, Instances insts)
			throws Exception;
	
	
	// feature extraction - test set
	
	// extractEventSets - same as for training documents
	
	/**
	 * 
	 * @param relevantEvents
	 * @param eventSetsToCull
	 * @return
	 * @throws Exception
	 */
	public List<EventSet> cullWithRespectToTraining(
			List<EventSet> relevantEvents, List<EventSet> eventSetsToCull)
			throws Exception;
	
	// createInstance - same as for training documents
	
	// normInstance - same as for training documents
	
	// applyInfoGain - same as for training documents
}