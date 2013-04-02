package edu.drexel.psal.jstylo.generics;

import java.util.*;

import weka.classifiers.Evaluation;
import weka.core.*;

import com.jgaap.generics.*;

/**
 * Abstract class for analyzers - classification routines to be applied on test sets, given a training set.
 * The data representation is based on Weka's Instances object.
 * 
 * @author Ariel Stolerman
 */
public abstract class Analyzer {
	
	/* ======
	 * fields
	 * ======
	 */
	
	/**
	 * The Weka Instances dataset to hold the extracted training data.
	 */
	protected Instances trainingSet;
	
	/**
	 * The Weka Instances dataset to hold the extracted test data.
	 */
	protected Instances testSet;
	
	/**
	 * Mapping of test documents to distribution classification results for each unknown document.
	 */
	protected Map<String,Map<String, Double>> results;
	
	/**
	 * List of authors.
	 */
	protected List<String> authors;
	
	
	/* ============
	 * constructors
	 * ============
	 */
	
	// -- none --
	
	/* ==========
	 * operations
	 * ==========
	 */
	
	
	/**
	 * Classifies the given test set based on the given training set. Should update the following fields along the classification:
	 * trainingSet, testSet, results and authors.
	 * Returns list of distributions of classification probabilities per instance.
	 * @param trainingSet
	 * 		The Weka Instances dataset of the training instances.
	 * @param testSet
	 * 		The Weka Instances dataset of the test instances.
	 * @param unknownDocs
	 * 		The list of test documents to deanonymize.
	 * @return
	 * 		The mapping from test documents to distributions of classification probabilities per instance, or
	 * 		null if prepare was not previously called.
	 * 		Each result in the list is a mapping from the author to its corresponding
	 * 		classification probability.
	 */
	public abstract Map<String,Map<String, Double>> classify(
			Instances trainingSet, Instances testSet, List<Document> unknownDocs);
	
	/**
	 * Runs cross validation with given number of folds on the given Instances object.
	 * @param data
	 * 		The data to run CV over.
	 * @param folds
	 * 		The number of folds to use.
	 * @param randSeed
	 * 		Random seed to be used for fold generation.
	 *  @return
	 * 		Some object containing the cross validation results (e.g. Evaluation for Weka
	 * 		classifiers CV results), or null if failed running.		
	 */
	public abstract Object runCrossValidation(Instances data, int folds, long randSeed);
	
	/* =======
	 * getters
	 * =======
	 */
	
	/**
	 * Returns the string representation of the last classification results.
	 * @return
	 * 		The string representation of the classification results.
	 */
	public String getLastStringResults() {
		// if there are no results yet
		if (results == null)
			return "No results!";		
		
		String res = "";
		Formatter f = new Formatter();
		f.format("%-14s |", "doc \\ author");
		
		List<String> actualAuthors = new ArrayList<String>(authors);
		
		for (String author: actualAuthors)
			f.format(" %-14s |",author);
		res += f.toString()+"\n";
		for (int i=0; i<actualAuthors.size(); i++)
			res += "-----------------";
		res += "----------------\n";
		
		for (String testDocTitle: results.keySet()) {
			f = new Formatter();
			f.format("%-14s |", testDocTitle);
			Map<String,Double> currRes = results.get(testDocTitle);
			
			String resAuthor = "";
			double maxProb = 0, oldMaxProb;
			for (String author: currRes.keySet()) {
				oldMaxProb = maxProb;
				maxProb = Math.max(maxProb, currRes.get(author).doubleValue());
				if (maxProb > oldMaxProb)
					resAuthor = author;
			}
			
			for (String author: actualAuthors) {
				char c;
				if (author.equals(resAuthor))
					c = '+';
				else c = ' ';
				f.format(" %2.6f %c     |",currRes.get(author).doubleValue(),c);
			}
			res += f.toString()+"\n";
		}
		
		res += "\n";
		return res;
	}
	
	/**
	 * Returns the fraction of rightly classified instances in the test case, or -1 if no prior
	 * classification was applied.
	 * @param unknownDocs
	 * 		The list of the unknown documents on which the classification was applied. 
	 * @return
	 * 		The fraction of rightly classified instances in the test case, or -1 if no prior
	 * 		classification was applied.
	 */
	public double getClassificationAccuracy(List<Document> unknownDocs) {
		// if there are no results yet
		if (results == null) return -1;
		
		int numOfInstances = testSet.numInstances();
		String[] unknownDocAuthors = new String[numOfInstances];
		for (int i=0; i<unknownDocAuthors.length; i++)
			unknownDocAuthors[i] = unknownDocs.get(i).getAuthor();
		
		int rightClassifications = 0;
		double maxProb;
		Iterator<Double> currRes;
		String currAuthor;
		
		for (int i=0; i<numOfInstances; i++) {
			currAuthor = unknownDocAuthors[i];

			// get the highest probability of all authors for current document
			maxProb = 0;
			currRes = results.get(i).values().iterator();
			for (;currRes.hasNext();)
				maxProb = Math.max(maxProb, currRes.next());
			
			// increase rightly classified count by 1 when true
			rightClassifications += (results.get(i).get(currAuthor) == maxProb) ? 1 : 0;
		}
		
		return ((double) rightClassifications)/numOfInstances;
	}
	
	/**
	 * Returns the last training Weka Instances set that was used for classification.
	 * @return
	 * 		The last training Weka Instances set that was used for classification.
	 */
	public Instances getLastTrainingSet() {
		return trainingSet;
	}
	
	/**
	 * Returns the last test Weka Instances set that was used for classification.
	 * @return
	 * 		The last test Weka Instances set that was used for classification.
	 */
	public Instances getLastTestSet() {
		return testSet;
	}
	
	/**
	 * Returns the entire data in one Weka Instances set.
	 * @return
	 * 		The entire data in one Weka Instances set.
	 */
	public Instances getAllInstances() {
		Instances all = new Instances(trainingSet);
		for (int i=0; i<testSet.numInstances(); i++) {
			all.add(testSet.instance(i));
		}
		return all;
	}
	
	/**
	 * Returns the last list of author names.
	 * @return
	 * 		The last list of author names.
	 */
	public List<String> getLastAuthors() {
		return authors;
	}
	
	/**
	 * Returns the last classification results or null if no classification was applied.
	 * @return
	 * 		The classification results or null if no classification was applied.
	 */
	public Map<String,Map<String, Double>> getLastResults() {
		return results;
	}
}
