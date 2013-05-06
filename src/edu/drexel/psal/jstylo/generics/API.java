package edu.drexel.psal.jstylo.generics;

import java.util.List;

import weka.core.*;

import com.jgaap.generics.*;

public interface API {

	/*
	
	public void prepareTrainingSet(List<Document> knownDocs, CumulativeFeatureDriver cfd)
	public void prepareTestSet(List<Document> unknownDocs)
	public String applyInfoGain(boolean changeAttributes, int N)
	private void normInstances(Instances insts)
	
	 */
	
	// feature extraction - training set
	
	/**
	 * Extracts events sets from the given document using the given cumulative
	 * feature driver.
	 * @param document
	 * @param cumulativeFeatureDriver
	 * @return
	 */
	public List<EventSet> extractEventSets(Document document,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception;
	
	/**
	 * 
	 * @param eventSets
	 * @param cumulativeFeatureDriver
	 * @return
	 * @throws Exception
	 */
	public List<List<EventSet>> cull(List<List<EventSet>> eventSets,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception;
	
	/**
	 * 
	 * @param culledEventSets
	 * @param cumulativeFeatureDriver
	 * @return
	 * @throws Exception
	 */
	public List<EventSet> getRelevantEvents(List<List<EventSet>> culledEventSets,
			CumulativeFeatureDriver cumulativeFeatureDriver) throws Exception;
	
	/**
	 * 
	 * @param culledEventSets
	 * @return
	 * @throws Exception
	 */
	public List<Attribute> getAttributeList(
			List<List<EventSet>> culledEventSets) throws Exception;
	
	/**
	 * 
	 * @param attributes
	 * @param cumulativeFeatureDriver
	 * @param documentData
	 * @return
	 * @throws Exception
	 */
	public Instance createInstance(List<Attribute> attributes,
			CumulativeFeatureDriver cumulativeFeatureDriver,
			List<EventSet> documentData, boolean isSparse) throws Exception;
	
	// initialize global baselines
	
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
	 * 
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

