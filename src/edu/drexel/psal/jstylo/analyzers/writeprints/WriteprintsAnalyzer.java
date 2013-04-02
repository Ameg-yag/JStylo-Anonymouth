package edu.drexel.psal.jstylo.analyzers.writeprints;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

import Jama.Matrix;

import com.jgaap.JGAAPConstants;
import com.jgaap.generics.*;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Attribute;
import weka.core.Instances;
import edu.drexel.psal.jstylo.generics.*;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * Implementation of the Writeprints method (supervised).
 * For more details see:<br>
 * Abbasi, A., Chen, H. (2008). Writeprints: A stylometric approach to identity-level identification
 * and similarity detection in cyberspace. ACM Trans. Inf. Syst., 26(2), 129.
 * 
 * @author Ariel Stolerman
 *
 */
public class WriteprintsAnalyzer extends Analyzer {
	
	/* ======
	 * fields
	 * ======
	 */
	
	/**
	 * The prefix given to any author of a test document.
	 */
	public static final String TEST_AUTHOR_NAME_PREFIX = "_test_";
	
	/**
	 * The list of training author data, including feature, basis and writeprint matrices.
	 */
	private List<AuthorWPData> trainAuthorData = new ArrayList<AuthorWPData>();
	
	/**
	 * The list of training author data, including feature, basis and writeprint matrices.
	 */
	private List<AuthorWPData> testAuthorData = new ArrayList<AuthorWPData>();
	
	/**
	 * Whether to average all feature vectors per author ending up with one feature vector
	 * or not. Increases performance but may reduce accuracy.
	 */
	private boolean averageFeatureVectors = true;
	
	/**
	 * Local logger
	 */
	public static MultiplePrintStream log = new MultiplePrintStream();
	
	/* ===================
	 * Getters and Setters
	 * ===================
	 */
	
	public boolean averageFeatureVectors()
	{
		return averageFeatureVectors;
	}
	
	public void setAverageFeatureVectors(boolean averageFeatureVectors)
	{
		this.averageFeatureVectors = averageFeatureVectors;
	}
	
	
	/* ==========
	 * operations
	 * ==========
	 */
	
	@Override
	public Map<String,Map<String, Double>> classify(Instances trainingSet,
			Instances testSet, List<Document> unknownDocs) {
		log.println(">>> classify started");
		
		/* ========
		 * LEARNING
		 * ========
		 */
		log.println("> Learning");
		
		trainAuthorData.clear();
		testAuthorData.clear();
		
		// initialize features, basis and writeprint matrices
		Attribute classAttribute = trainingSet.classAttribute();
		int numAuthors = classAttribute.numValues();
		String authorName;
		AuthorWPData authorData;
		// training set
		log.println("Initializing training authors data:");
		for (int i = 0; i < numAuthors; i++) {
			authorName = classAttribute.value(i);
			authorData = new AuthorWPData(authorName);
			log.println("- " + authorName);
			authorData.initFeatureMatrix(trainingSet, averageFeatureVectors);
			trainAuthorData.add(authorData);
			authorData.initBasisAndWriteprintMatrices();
		}
		// test set
		int numTestInstances = testSet.numInstances();
		// train-test mode
		if (unknownDocs != null) {
			log.println("Initializing test authors data (author per test document):");
			for (int i = 0; i < numTestInstances; i++) {
				authorName =
						TEST_AUTHOR_NAME_PREFIX +
						String.format("%03d", i) + "_" +
						unknownDocs.get(i).getTitle();
				authorData = new AuthorWPData(authorName);
				log.println("- " + authorName);
				authorData.initFeatureMatrix(testSet, i, averageFeatureVectors);
				testAuthorData.add(authorData);				
				authorData.initBasisAndWriteprintMatrices();
			}
		}
		// CV mode
		else {
			log.println("Initializing test authors data (CV mode):");
			for (int i = 0; i < numAuthors; i++) {
				authorName = classAttribute.value(i);
				authorData = new AuthorWPData(authorName);
				log.println("- " + authorName);
				authorData.initFeatureMatrix(testSet, averageFeatureVectors);
				testAuthorData.add(authorData);
				authorData.initBasisAndWriteprintMatrices();
			}
		}
		
		// initialize result set
		results = new HashMap<String,Map<String,Double>>(trainAuthorData.size());
		
		// calculate information-gain over only training authors
		log.println("Calculating information gain over training authors data");
		double[] IG = null;
		int numFeatures = trainingSet.numAttributes() - 1;
		try {
			IG = calcInfoGain(trainingSet, numFeatures);
		} catch (Exception e) {
			System.err.println("Error evaluating information gain.");
			e.printStackTrace();
			return null;
		}
		
		// initialize synonym count mapping
		log.println("Initializing word synonym count");
		Map<Integer,Integer> wordsSynCount = calcSynonymCount(trainingSet,numFeatures);
		
		
		/* =======
		 * TESTING
		 * =======
		 */
		log.println("> Testing");
		
		Matrix testPattern, trainPattern;
		double dist1, dist2, totalDist;
		AuthorWPData testDataCopy, trainDataCopy;
		for (AuthorWPData testData: testAuthorData) {
			Map<String,Double> testRes = new HashMap<String,Double>();
			log.println("Test author: " + testData.authorName);
			for (AuthorWPData trainData: trainAuthorData) {
				testDataCopy = testData.halfClone();
				trainDataCopy = trainData.halfClone();
				
				// compute pattern matrices BEFORE adding pattern disruption
				testPattern = AuthorWPData.generatePattern(trainData, testData);
				trainPattern = AuthorWPData.generatePattern(testData, trainData);
				
				// add pattern disruptions
				testDataCopy.addPatternDisruption(trainData, IG, wordsSynCount, trainPattern);
				trainDataCopy.addPatternDisruption(testData, IG, wordsSynCount, testPattern);
				
				// compute pattern matrices AFTER adding pattern disruption
				testPattern = AuthorWPData.generatePattern(trainDataCopy, testDataCopy);
				trainPattern = AuthorWPData.generatePattern(testDataCopy, trainDataCopy);
				
				// compute distances
				dist1 = sumEuclideanDistance(testPattern, trainDataCopy.writeprint);
				dist2 = sumEuclideanDistance(trainPattern, testDataCopy.writeprint);
				
				// save the inverse to maintain the smallest distance as the best fit
				totalDist = - (dist1 + dist2);
				testRes.put(trainData.authorName, totalDist);
				//log.println("- " + trainData.authorName + ": " + totalDist);
			}
			results.put(testData.authorName,testRes);
		}
		log.println(">>> classify finished");
		return results;
	}

	@Override
	public String runCrossValidation(Instances data, int folds,
			long randSeed) {
		log.println(">>> runCrossValidation started");
		
		// setup
		data.setClass(data.attribute("authorName"));
		Instances randData = new Instances(data);
		Random rand = new Random(randSeed);
		randData.randomize(rand);
		randData.stratify(folds);
		
		// prepare folds
		Instances[] foldData = new Instances[folds];
		for (int i = 0; i < folds; i ++)
			foldData[i] = randData.testCV(folds, i);
		int half = (folds / 2) + (folds % 2);
		
		// run CV - use half the folds for training, half for testing
		// E.g. for 10 folds, use 1-5 for training, 6-10 for testing; 2-6 for training, 1 + 7-10 for testing, etc.
		Instances train = new Instances(data,0);
		Instances test = new Instances(data,0);
		Instances tmp;
		int tmpSize;
		Map<String,Map<String,Double>> results;
		Map<String,Double> instResults;
		double success;
		double total = 0;
		double max;
		String selected;
		for (int i = 0; i < folds; i ++) {
			log.println("Running experiment " + (i + 1) + " out of " + folds);
			
			// initialize
			train.delete();
			test.delete();
			
			// prepare training set
			for (int j = i; j < i + half; j++) {
				tmp = foldData[j % folds];
				tmpSize = tmp.numInstances();
				for (int k = 0; k < tmpSize; k++)
					train.add(tmp.instance(k));
			}
			// prepare test set
			for (int j = i + half; j < i + folds; j++) {
				tmp = foldData[j % folds];
				tmpSize = tmp.numInstances();
				for (int k = 0; k < tmpSize; k++)
					test.add(tmp.instance(k));
			}
			
			// classify
			results = classify(train, test, null);
			success = 0;
			selected = null;
			for (String testInstAuthor: results.keySet()) {
				max = Double.NEGATIVE_INFINITY;
				instResults = results.get(testInstAuthor);
				for (String key: instResults.keySet()) {
					if (max < instResults.get(key)) {
						max = instResults.get(key);
						selected = key;
					}
				}
				log.println(testInstAuthor + ": " + selected);
				if (testInstAuthor.equals(selected))
					success++;
			}
			success = 100 * success / results.size();
			log.printf("- Accuracy for experiment %d: %.2f\n", (i + 1), success);
			total += success;
		}
		total /= folds;
		log.println("========================");
		log.printf("Total Accuracy: %.2f\n", total);
		log.println(">>> runCrossValidation finished");
		return null;
	}
	
	@Override
	/**
	 * TODO
	 */
	public Object runCrossValidation(Instances data, int folds, long randSeed,
			int relaxFactor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Calculates and returns a mapping of authors to the number of documents of
	 * the corresponding author in the first dataset, the second dataset, and 
	 * the distances measured between them.<br>
	 * Both datasets must have the same features, including the same set of
	 * authors in the class attribute.
	 * @param dataset1
	 * @param dataset2
	 * @return
	 */
	public SortedMap<String, double[]> getCrossDatasetsDistances(
			Instances dataset1, Instances dataset2) {
		log.println(">>> getCrossDatasetsDistances started");
		
		/* ========
		 * LEARNING
		 * ========
		 */
		log.println("> Learning");
		
		List<AuthorWPData> dataset1AuthorData = new LinkedList<AuthorWPData>();
		List<AuthorWPData> dataset2AuthorData = new LinkedList<AuthorWPData>();
				
		// initialize features, basis and writeprint matrices
		Attribute classAttribute = dataset1.classAttribute();
		int numAuthors = classAttribute.numValues();
		String authorName;
		AuthorWPData authorData;
		
		// dataset1
		log.println("Initializing dataset1 authors data:");
		for (int i = 0; i < numAuthors; i++) {
			authorName = classAttribute.value(i);
			authorData = new AuthorWPData(authorName);
			log.println("- " + authorName);
			authorData.initFeatureMatrix(dataset1, averageFeatureVectors);
			dataset1AuthorData.add(authorData);
			authorData.initBasisAndWriteprintMatrices();
		}
		
		// dataset2
		log.println("Initializing dataset2 authors data:");
		for (int i = 0; i < numAuthors; i++) {
			authorName = classAttribute.value(i);
			authorData = new AuthorWPData(authorName);
			log.println("- " + authorName);
			authorData.initFeatureMatrix(dataset2, averageFeatureVectors);
			dataset2AuthorData.add(authorData);
			authorData.initBasisAndWriteprintMatrices();
		}
		
		// initialize result set
		SortedMap<String, double[]> results = new TreeMap<String,double[]>();
		
		// calculate information-gain
		log.println("Calculating information gain over dataset1");
		double[] IG1 = null;
		int numFeatures = dataset1.numAttributes() - 1;
		try {
			IG1 = calcInfoGain(dataset1, numFeatures);
		} catch (Exception e) {
			System.err.println("Error evaluating information gain.");
			e.printStackTrace();
			return null;
		}
		log.println("Calculating information gain over dataset1");
		double[] IG2 = null;
		try {
			IG2 = calcInfoGain(dataset2, numFeatures);
		} catch (Exception e) {
			System.err.println("Error evaluating information gain.");
			e.printStackTrace();
			return null;
		}
		
		// initialize synonym count mapping
		log.println("Initializing word synonym count");
		Map<Integer,Integer> wordsSynCount =
				calcSynonymCount(dataset1,numFeatures);
		
		
		/* =====================
		 * CALCULATING DISTANCES
		 * =====================
		 */
		log.println("> Calculating distances");
		
		Matrix dataset1Pattern, dataset2Pattern;
		double dist1, dist2, avgDist;
		AuthorWPData dataset1DataCopy, dataset2DataCopy;
		double min = Double.MAX_VALUE;
		String minAuthor = "";
		for (AuthorWPData dataset1Data: dataset1AuthorData) {
			log.println("dataset1 author: " + dataset1Data.authorName);
			for (AuthorWPData dataset2Data: dataset2AuthorData)
			{
				//log.print("- dataset2 author: " + dataset2Data.authorName + ": ");
				
				dataset1DataCopy = dataset1Data.halfClone();
				dataset2DataCopy = dataset2Data.halfClone();

				// compute pattern matrices BEFORE adding pattern disruption
				dataset1Pattern = AuthorWPData.generatePattern(dataset2Data, dataset1Data);
				dataset2Pattern = AuthorWPData.generatePattern(dataset1Data, dataset2Data);

				// add pattern disruptions
				dataset1DataCopy.addPatternDisruption(dataset2Data, IG2, wordsSynCount, dataset2Pattern);
				dataset2DataCopy.addPatternDisruption(dataset1Data, IG1, wordsSynCount, dataset1Pattern);

				// compute pattern matrices AFTER adding pattern disruption
				dataset1Pattern = AuthorWPData.generatePattern(dataset2DataCopy, dataset1DataCopy);
				dataset2Pattern = AuthorWPData.generatePattern(dataset1DataCopy, dataset2DataCopy);

				// compute distances
				dist2 = sumEuclideanDistance(dataset1Pattern, dataset2DataCopy.writeprint);
				dist1 = sumEuclideanDistance(dataset2Pattern, dataset1DataCopy.writeprint);

				// compute the average distance
				avgDist = (dist1 + dist2) / 2;
				results.put(dataset1Data.authorName + "," + dataset2Data.authorName,
						new double[]{
						dataset1Data.numAuthorInstances,
						dataset2Data.numAuthorInstances,
						avgDist
				});
				if (min > avgDist)
				{
					min = avgDist;
					minAuthor = dataset2Data.authorName;
				}
				//log.println(avgDist);
			}
			log.println("minimum distance author: " + minAuthor +
					", distance: " + min);
		}
		log.println(">>> getCrossDatasetsDistances finished");
		return results;
	}
	
	/* ===============
	 * utility methods
	 * ===============
	 */
	
	private static WordNetDatabase wndb = null;
	
	/**
	 * Initializes the Wordnet database.
	 * @throws IOException 
	 */
	private static void initWordnetDB() {
		URL url = Thread.currentThread().getClass().getResource(
				JGAAPConstants.JGAAP_RESOURCE_PACKAGE+"wordnet");
		System.setProperty("wordnet.database.dir", url.getPath());
		wndb = WordNetDatabase.getFileInstance();
	}

	/**
	 * Used to identify word-based features.
	 */
	private static String[] wordFeatures = {
		"Function-Words",
		"Words",
		"Word-Bigrams",
		"Word-Trigrams",
		"Misspelled-Words"
	};
	
	/**
	 * Constructs a mapping from all word-based feature indices to the number of their synonyms.
	 * The synonym counted are only those belonging to synsets of the most common part-of-speech
	 * synset-type. If the feature is an n-gram feature, the synonym count is the multiplication
	 * of synonym count values of each word in the n-gram.
	 * @param trainingSet
	 * 		The training set from which to extract the features.
	 * @param numFeatures
	 * 		The number of features.
	 * @return
	 * 		A mapping from the word feature indices of the given training set to the synonym count.
	 */
	private static Map<Integer,Integer> calcSynonymCount(Instances trainingSet, int numFeatures) {
		
		// initialize
		Map<Integer,Integer> synCountMap = new HashMap<Integer,Integer>(numFeatures);
		if (wndb == null)
			initWordnetDB();
		
		boolean isWordFeature;
		Attribute feature;
		String featureName;
		String[] words;
		int synCount;
		Synset[] synsets, tmpSynsets;
		SynsetType[] allTypes = SynsetType.ALL_TYPES;
		Set<String> synonyms;
		for (int j = 0; j < numFeatures; j ++) {
			feature = trainingSet.attribute(j);
			featureName = feature.name();
			
			// check whether it is a word feature, else continue
			isWordFeature = false;
			for (String wordFeature: wordFeatures)
				if (featureName.startsWith(wordFeature)) {
					isWordFeature = true;
					break;
				}
			if (!isWordFeature)
				continue;
			
			// find synonym count for all word features
			// multiply synonym-count for n-gram features
			synCount = 1;
			words = getWordsFromFeatureName(featureName);
			for (String word: words) {
				// find the SynsetType with the maximum number of synsets
				synsets = wndb.getSynsets(word, allTypes[0]);
				for (int i = 1; i < allTypes.length; i++) {
					tmpSynsets = wndb.getSynsets(word, allTypes[i]);
					if (tmpSynsets.length > synsets.length)
						synsets = tmpSynsets;
				}

				// count synonyms
				synonyms = new HashSet<String>();
				for (Synset synset: synsets)
					synonyms.addAll(Arrays.asList(synset.getWordForms()));
				if (!synonyms.isEmpty())
					synCount *= synonyms.size();
			}
			synCountMap.put(j, synCount);
		}

		return synCountMap;
	}
	
	/**
	 * Extracts the words from the given feature name and returns them in an
	 * array.
	 * @param featureName
	 * 		The feature name, of the form <code>FEATURE-TYPE-{WORDS}</code>.
	 * @return
	 */
	private static String[] getWordsFromFeatureName(String featureName) {
		String content = featureName.replaceAll(".*\\{", "").replace("}", "");
		if (!featureName.contains("grams"))
			return new String[]{content};
		else {
			content = content.substring(1, content.length() - 1);
			return content.split("\\)-\\(");
		}
	}
	
	/**
	 * Calculates and returns the information gain vector for all features
	 * based on the given training set.
	 * @param trainingSet
	 * 		The training set to calculate information gain on.
	 * @param numFeatures
	 * 		The number of features.
	 * @return
	 * 		The information gain vector for all features based on the given
	 * 		training set.
	 * @throws Exception
	 * 		If an error is encountered during information gain evaluation.
	 */
	private static double[] calcInfoGain(Instances trainingSet, int numFeatures) throws Exception {		
		InfoGainAttributeEval ig = new InfoGainAttributeEval();
		ig.buildEvaluator(trainingSet);
		double[] IG = new double[numFeatures];
		for (int j = 0; j < numFeatures; j++)
			IG[j] = ig.evaluateAttribute(j);
		return IG;
	}
	
	/**
	 * Returns the average of the Euclidean distance between every 
	 * pair of columns (corresponding to document feature values)
	 * of the given matrices.
	 * @param a
	 * 		The first matrix.
	 * @param b
	 * 		The second matrix.
	 * @return
	 */
	private static double sumEuclideanDistance(Matrix a, Matrix b) {
		double sum = 0;
		double colsDiff, tmp;
		int numACols = a.getColumnDimension();
		int numBCols = b.getColumnDimension();
		int total = numACols * numBCols;
		int numFeatures = a.getRowDimension();
		for (int i = 0; i < numACols; i++) {
			for (int j = 0; j < numBCols; j++) {
				colsDiff = 0;
				for (int k = 0; k < numFeatures; k++) {
					tmp = a.get(k,i) - b.get(k, j);
					colsDiff += tmp * tmp;
				}
				sum += Math.sqrt(colsDiff) / total;
			}
		}
		return sum;
	}
	
	
	// ============================================================================================
	// ============================================================================================
	
	
	/**
	 * Main for testing.
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// initialize log
		PrintStream logPS = new PrintStream(new File("./log/" + MultiplePrintStream.getLogFilename()));
		log = new MultiplePrintStream(System.out, logPS);
		
		WriteprintsAnalyzer wa = new WriteprintsAnalyzer();
		
		//ProblemSet ps = new ProblemSet(JSANConstants.JSAN_PROBLEMSETS_PREFIX + "drexel_1_train_test.xml");
		//ProblemSet ps = new ProblemSet(JSANConstants.JSAN_PROBLEMSETS_PREFIX + "drexel_1.xml");
		//ProblemSet ps = new ProblemSet(JSANConstants.JSAN_PROBLEMSETS_PREFIX + "amt.xml");
		/*
		CumulativeFeatureDriver cfd =
				new CumulativeFeatureDriver(JSANConstants.JSAN_FEATURESETS_PREFIX + "writeprints_feature_set_limited.xml");
		WekaInstancesBuilder wib = new WekaInstancesBuilder(false);
		List<Document> trainingDocs = ps.getAllTrainDocs();
		List<Document> testDocs = ps.getTestDocs();
		int numTrainDocs = trainingDocs.size();
		int numTestDocs = testDocs.size();
		
		// extract features
		System.out.println("feature pre extraction");
		trainingDocs.addAll(testDocs);
		System.out.println("feature extraction");
		wib.prepareTrainingSet(trainingDocs, cfd);
		System.out.println("feature post extraction");
		Instances trainingSet = wib.getTrainingSet();
		Instances testSet = new Instances(
				trainingSet,
				numTrainDocs,
				numTestDocs);
		wib.setTestSet(testSet);
		int total = numTrainDocs + numTestDocs;
		for (int i = total - 1; i >= numTrainDocs; i--)
			trainingSet.delete(i);
		System.out.println("done!");
		
		Instances train = wib.getTrainingSet();
		Instances test = wib.getTestSet();
		WekaInstancesBuilder.writeSetToARFF("d:/tmp/drexel_1_tt_train.arff", train);
		WekaInstancesBuilder.writeSetToARFF("d:/tmp/drexel_1_tt_test.arff", test);
		System.exit(0);
		*/
		Instances train = new Instances(new FileReader(new File("d:/tmp/drexel_1_train.arff")));
		train.setClassIndex(train.numAttributes() - 1);
		//Instances test = new Instances(new FileReader(new File("d:/tmp/drexel_1_tt_test.arff")));
		//test.setClassIndex(test.numAttributes() - 1);
		
		// classify
		/*
		System.out.println("classification");
		Map<String,Map<String, Double>> res = wa.classify(train, test, ps.getTestDocs());
		System.out.println("done!");
		Map<String,Double> docMap;
		String selectedAuthor = null;
		double maxValue;
		double success = 0;
		for (String doc: res.keySet()) {
			maxValue = Double.NEGATIVE_INFINITY;
			docMap = res.get(doc);
			System.out.println(doc+":");
			for (String key: docMap.keySet())
				if (maxValue < docMap.get(key)) {
					selectedAuthor = key;
					maxValue = docMap.get(key);
				}
			System.out.println("- "+selectedAuthor+": "+maxValue);
			success += doc.replaceFirst(TEST_AUTHOR_NAME_PREFIX + "\\d+_", "").startsWith(selectedAuthor) ? 1 : 0;
		}
		success = 100 * success / res.size();
		System.out.printf("Total accuracy: %.2f\n",success);
		*/
		
		// cross-validation
		wa.runCrossValidation(train, 10, 0);
	}
}
