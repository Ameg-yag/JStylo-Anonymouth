package edu.drexel.psal.anonymouth.engine;

public class FeatureSwapper {
	
	public FeatureSwapper() {
		
		
	}
	
	
	/*
	 * need to get the cluster groups, 
	 * find the corresponding features in Weka's attributes for the document to anonymize,
	 * create new weka Instance objects for the each cluster group,
	 * and replace the original feature values with the target values from each cluster group.
	 * 
	 * Then, we take the trained classifier, and test each of the new instances against it. 
	 * Parse the results, and select the cluster group that returns either:
	 * 
	 * 		=> The lowest probability of authorship combined with the most evenly distributed  
	 * 		probabilities of ownership for all authors.
	 */
}
