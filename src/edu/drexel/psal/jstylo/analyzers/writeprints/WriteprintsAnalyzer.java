package edu.drexel.psal.jstylo.analyzers.writeprints;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import Jama.Matrix;

import com.jgaap.JGAAPConstants;
import com.jgaap.generics.*;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
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
	 * Whether to reduce the feature space.
	 */
	private boolean reduceFeatureSpace = false;
	
	/**
	 * Whether to calculate word-based features synonym count for pattern disruption.
	 */
	private boolean calcSynCount = false;	
	
	/**
	 * Whether to count synonyms only in the first synset returned by Wordnet.
	 */
	private boolean useFirstSynsetOnly = true;

	/**
	 * Local logger
	 */
	protected static MultiplePrintStream log = new MultiplePrintStream();
	
	
	/* ============
	 * Constructors
	 * ============
	 */
	
	/**
	 * Default constructor for WriteprintsAnalyzer.
	 */
	public WriteprintsAnalyzer() {
		// default constructor
	}
	
	/**
	 * Constructor for WriteprintsAnalyzer.
	 * @param averageFeatureVectors
	 * 		Whether to average all feature vectors into one. Increases performance.
	 * @param reduceFeatureSpace
	 * 		Whether to apply feature space reduction for large feature classes (e.g.
	 * 		word bigrams) by information gain.
	 * @param calcSynCount
	 * 		Whether to apply word-based features synonym count calculation to be
	 * 		used for pattern disruption.
	 * @param useFirstSynsetOnly
	 * 		Whether to count synonyms only in the first synset returned by Wordnet.
	 */
	public WriteprintsAnalyzer(boolean averageFeatureVectors,
			boolean reduceFeatureSpace, boolean calcSynCount, boolean useFirstSynsetOnly) {
		this.averageFeatureVectors = averageFeatureVectors;
		this.reduceFeatureSpace = reduceFeatureSpace;
		this.calcSynCount = calcSynCount;
		this.useFirstSynsetOnly = useFirstSynsetOnly;
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
		
		// reduce feature space by info-gain
		if (reduceFeatureSpace) {
			Pair<Instances,Instances> newSets = reduceFeatures(trainingSet, testSet);
			trainingSet = newSets.getFirst();
			testSet = newSets.getSecond();
		}
		double[] IG = null;
		// calculate information-gain over only training authors
		// (after reduction)
		log.println("Calculating information gain over training authors data");
		int numFeatures = trainingSet.numAttributes() - 1;
		try {
			IG = calcInfoGain(trainingSet, numFeatures);
		} catch (Exception e) {
			System.err.println("Error evaluating information gain.");
			e.printStackTrace();
			return null;
		}

		trainAuthorData.clear();
		testAuthorData.clear();
		
		// initialize features, basis and writeprint matrices
		boolean authorHasInstances;
		Attribute classAttribute = trainingSet.classAttribute();
		int numAuthors = classAttribute.numValues();
		String authorName;
		AuthorWPData authorData;
		int authorsInRow = 5;
		// training set
		int numTrainInstances = trainingSet.numInstances();
		log.println("Initializing training authors data:");
		for (int i = 0; i < numAuthors; i++) {
			authorName = classAttribute.value(i);
			authorData = new AuthorWPData(authorName);
			log.print(authorName + "  ");
			// check whether this training author has any instances
			authorHasInstances = false;
			for (int j = 0; j < numTrainInstances; j++) {
				if (trainingSet.instance(j).stringValue(classAttribute).equals(authorName)) {
					authorHasInstances = true;
					break;
				}
			}
			if (!authorHasInstances) {
				log.print("(no data)  ");
			}
			else {
				authorData.initFeatureMatrix(trainingSet, averageFeatureVectors);
				trainAuthorData.add(authorData);
				authorData.initBasisAndWriteprintMatrices();
			}
			if ((i + 1) % authorsInRow == 0)
				log.println();
		}
		log.println();
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
				log.print(authorName + "  ");
				authorData.initFeatureMatrix(testSet, i, averageFeatureVectors);
				testAuthorData.add(authorData);				
				authorData.initBasisAndWriteprintMatrices();
				if ((i + 1) % authorsInRow == 0)
					log.println();
			}
		}
		// CV mode
		else {
			log.println("Initializing test authors data (CV mode):");
			for (int i = 0; i < numAuthors; i++) {
				authorName = classAttribute.value(i);
				authorData = new AuthorWPData(authorName);
				log.print(authorName + "  ");
				// check whether this test author has any instances
				authorHasInstances = false;
				for (int j = 0; j < numTestInstances; j++) {
					if (testSet.instance(j).stringValue(classAttribute).equals(authorName)) {
						authorHasInstances = true;
						break;
					}
				}
				if (!authorHasInstances) {
					log.print("(no data)  ");
				}
				else {
					authorData.initFeatureMatrix(testSet, averageFeatureVectors);
					testAuthorData.add(authorData);
					authorData.initBasisAndWriteprintMatrices();
				}
				if ((i + 1) % authorsInRow == 0)
					log.println();
			}
		}
		log.println();
		
		// initialize result set
		results = new HashMap<String,Map<String,Double>>(trainAuthorData.size());
		
		// initialize synonym count mapping
		Map<Integer,Integer> wordsSynCount = null;
		if (calcSynCount) {
			log.println("Initializing word synonym count");
			wordsSynCount = calcSynonymCount(trainingSet,numFeatures,useFirstSynsetOnly);
		}
		
		
		/* =======
		 * TESTING
		 * =======
		 */
		log.println("> Testing");
		
		Matrix testPattern, trainPattern;
		double dist1, dist2, totalDist;
		AuthorWPData testDataCopy, trainDataCopy;
		int count = 0;
		authorsInRow = 12;
		for (AuthorWPData testData: testAuthorData) {
			Map<String,Double> testRes = new HashMap<String,Double>();
			log.print(testData.authorName + "  ");
			count++;
			if (count % authorsInRow == 0)
				log.println();
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
		Instances train, test;
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
			train = new Instances(data,0);
			test = new Instances(data,0);
			
			log.print("> Training folds: ");
			// prepare training set
			for (int j = i; j < i + half; j++) {
				log.print(j % folds + " ");
				tmp = foldData[j % folds];
				tmpSize = tmp.numInstances();
				for (int k = 0; k < tmpSize; k++)
					train.add(tmp.instance(k));
			}
			log.println();
			//train = randData.trainCV(folds, i);
			log.print("> Test folds: ");
			// prepare test set
			for (int j = i + half; j < i + folds; j++) {
				log.print(j % folds + " ");
				tmp = foldData[j % folds];
				tmpSize = tmp.numInstances();
				for (int k = 0; k < tmpSize; k++)
					test.add(tmp.instance(k));
			}
			log.println();
			//test = randData.testCV(folds, i);
			
			// write to files
			//WekaInstancesBuilder.writeSetToARFF("./train_" + i + ".arff", train);
			//WekaInstancesBuilder.writeSetToARFF("./test_" + i + ".arff", test);
			
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
	
	
	/* ===================
	 * Getters and Setters
	 * ===================
	 */
	
	/**
	 * The <code>averageFeatureVectors</code> parameter sets whether to average
	 * all feature vectors for each author or to hold a vector per document.
	 * Increases performance when set to <code>true</code> (by default).
	 * @return <code>true</code> if the classifier is set to average all feature
	 * vectors for each author, across all author's documents.<br>
	 */
	public boolean isAverageFeatureVectors() {
		return averageFeatureVectors;
	}

	/**
	 * Setter for <code>averageFeatureVectors</code>. If set to <code>true</code>,
	 * averages all feature vectors per each author. Increases performance when
	 * set to <code>true</code> (by default).
	 * @param averageFeatureVectors
	 * 		The value to set.
	 */
	public void setAverageFeatureVectors(boolean averageFeatureVectors) {
		this.averageFeatureVectors = averageFeatureVectors;
	}

	/**
	 * @return <code>true</code> if the classifier is set to apply
	 * Information Gain over all varying-size feature classes (e.g. word bigrams),
	 * to be reduced to the value defined in <code>featureReductionThreshold</code>.
	 */
	public boolean isReduceFeatureSpace() {
		return reduceFeatureSpace;
	}

	/**
	 * Setter for <code>reduceFeatureSpace</code>. If set to <code>true</code>,
	 * reduces the feature space by applying Information Gain on all varying-size
	 * feature classes (e.g. word bigrams) and reducing them to the top
	 * <code>featureReductionThreshold</code> features.
	 * @param reduceFeatureSpace
	 * 		The value to be set.
	 */
	public void setReduceFeatureSpace(boolean reduceFeatureSpace) {
		this.reduceFeatureSpace = reduceFeatureSpace;
	}
	
	/**
	 * @return <code>true</code> if the classifier is set to calculate synonym
	 * count for all word-based features, to be integrated in the feature 
	 * pattern disruption calculation. Otherwise, all word-based features
	 * are addressed as all other features in the pattern disruption calculation. 
	 */
	public boolean isCalcSynCount() {
		return calcSynCount;
	}
	
	/**
	 * Setter for <code>calcSynCount</code>. If set to <code>true</code>,
	 * calculates synonym count for all word-based features to be integrated
	 * in the pattern disruption calculation for those features.
	 * @param calcSynCount
	 * 		The value to be set.
	 */
	public void setCalcSynCount(boolean calcSynCount) {
		this.calcSynCount = calcSynCount;
	}
	
	/**
	 * @return
	 * 		The value to reduce the feature-class with the given name prefix to
	 * 		if the <code>reduceFeatureSpace</code> is set to <code>true</code>,
	 * 		and -1 otherwise (or in the case the given feature prefix is not set
	 * 		to be reduced).
	 */
	public static int getFeatureReductionThreshold(String key) {
		Integer res = toReduceFeatures.get(key);
		if (res == null)
			return -1;
		return res;
	}
	
	/**
	 * Sets the value to reduce the given feature-classe name prefix to, if the
	 * <code>reduceFeatureSpace</code> is set to <code>true</code>, to the
	 * given one.
	 * @param featureNamePrefix
	 * 		The name prefix of the feature-class to reduce.
	 * @param featureReductionThreshold
	 * 		The value to set to.
	 */
	public static void setFeatureReductionThreshold(String featureNamePrefix,
			int featureReductionThreshold) {
		toReduceFeatures.put(featureNamePrefix, featureReductionThreshold);
	}
	
	
	/* ===============
	 * utility methods
	 * ===============
	 */
	
	/**
	 * Used to identify features that should be
	 * reduced by info-gain
	 */
	private static Map<String,Integer> toReduceFeatures = new HashMap<String,Integer>();
	static {
		toReduceFeatures.put("letter-bigrams",50);
		toReduceFeatures.put("letter-trigrams",50);
		toReduceFeatures.put("digit-bigrams",50);
		toReduceFeatures.put("digit-trigrams",50);
		toReduceFeatures.put("function-words",300);
		toReduceFeatures.put("POS-bigrams",50);
		toReduceFeatures.put("POS-trigrams",50);
		toReduceFeatures.put("bag-of-words",300);
		toReduceFeatures.put("word-bigrams",50);
		toReduceFeatures.put("word-trigrams",50);
		toReduceFeatures.put("misspellings",50);
	};
	
	/**
	 * Reduces the feature space by removing the lowest information-gain n-gram
	 * features (e.g. POS bigrams, word trigrams etc.) and misspelled words,
	 * as defined in <code>toReduceFeatures</code>.<br>
	 * The reduction is based on the information gain extracted from the given
	 * training set only. 
	 * @param train
	 * @param test
	 */
	private static Pair<Instances,Instances> reduceFeatures(Instances train, Instances test) {
		log.println("Reducing feature space");
		log.println("Number of attributes before: " + train.numAttributes());
		// calculate information-gain over only training authors
		log.println("Calculating information gain over training authors data");
		double[] IG = null;
		int numFeatures = train.numAttributes() - 1;
		try {
			IG = calcInfoGain(train, numFeatures);
		} catch (Exception e) {
			System.err.println("Error evaluating information gain.");
			e.printStackTrace();
			return null;
		}
		
		// find feature indices ranges
		Map<String,int[]> featureRanges =
				new HashMap<String,int[]>(toReduceFeatures.size());
		int[] range;
		boolean foundFirst, foundSecond;
		for (String feature: toReduceFeatures.keySet()) {
			int featureReductionThreshold = toReduceFeatures.get(feature);
			foundFirst = false;
			foundSecond = false;
			range = new int[2];
			for (int i = 0; i < numFeatures; i++) {
				if (train.attribute(i).name().startsWith(feature)) {
					if (!foundFirst) {
						range[0] = i;
						foundFirst = true;
					}
				}
				else {
					if (foundFirst) {
						range[1] = i - 1;
						foundSecond = true;
						break;
					}
				}
			}
			if (!foundSecond)
				range[1] = numFeatures - 1;
			log.println(feature + ": [" + range[0] + "," + range[1] + "]");
			if (range[1] - range[0] + 1 > featureReductionThreshold)
				featureRanges.put(feature, range);
		}		
		
		// mark indices of features to save
		Comparator<double[]> IGcomp = new Comparator<double[]>() {
			@Override
			public int compare(double[] arg0, double[] arg1) {
				double diff = arg0[1] - arg1[1];
				if (diff < 0) return -1;
				if (diff > 0) return 1;
				return 0;
			}
		};
		List<Integer> indicesToSave = new ArrayList<Integer>();
		double[][] featureIndicesIG;
		int featureIGLen;
		int count;
		log.println("calculating features to save");
		for (String feature: toReduceFeatures.keySet()) {
			int featureReductionThreshold = toReduceFeatures.get(feature);
			range = featureRanges.get(feature);
			if (range == null)
				continue;
			log.print(feature + ": ");
			featureIGLen = range[1] - range[0] + 1;
			featureIndicesIG = new double[featureIGLen][2];
			log.print("initializing... ");
			for (int i = 0; i < featureIGLen; i++) {
				featureIndicesIG[i][0] = i + range[0];
				featureIndicesIG[i][1] = IG[i + range[0]];
			}
			log.print("sorting... ");
			Arrays.sort(featureIndicesIG, IGcomp);
			log.print("adding feature indices to be saved... ");
			count = 0;
			for (int i = 0; i < featureReductionThreshold; i++) {
				indicesToSave.add((int) featureIndicesIG[i][0]);
				count++;
			}
			log.println("done! total features to save: " + count);
		}
		log.print("adding all features that weren't candidates to be removed... ");
		boolean inList;
		String attrName;
		for (int i = 0; i < numFeatures; i++) {
			inList = false;
			attrName = train.attribute(i).name();
			for (String prefix: toReduceFeatures.keySet())
				if (attrName.startsWith(prefix)) {
					inList = true;
					break;
				}
			if (!inList)
				indicesToSave.add(i);
		}
		log.println("done!");
		log.print("sorting all features to be saved by indices... ");
		Collections.sort(indicesToSave);
		int newNumFeatures = indicesToSave.size();		
		log.println("done!");
		
		// create new attribute list without the removed indices
		log.print("creating new attribute list... ");
		FastVector newAttrList = new FastVector(newNumFeatures);
		Iterator<Integer> indicesToSaveIter = indicesToSave.iterator();
		for (int i = 0; i < newNumFeatures - 1; i++)
			newAttrList.addElement(train.attribute(indicesToSaveIter.next().intValue()));
		// handle class attribute
		Attribute classAttr = train.classAttribute();
		FastVector newClassAttr = new FastVector(classAttr.numValues());
		for (int i = 0; i < classAttr.numValues(); i++)
			newClassAttr.addElement(classAttr.value(i));
		newAttrList.addElement(new Attribute(classAttr.name(), newClassAttr));
		log.println("done!");
		
		// create new training set
		log.print("creating new training set... ");
		int numTrainInst = train.numInstances();
		Instances newTrain = new Instances(train.relationName(), newAttrList, numTrainInst);
		newTrain.setClassIndex(newNumFeatures - 1);
		Instance inst, newInst;
		for (int i = 0; i < numTrainInst; i++) {
			inst = train.instance(i);
			newInst = new Instance(newNumFeatures);
			// copy all relevant features
			int j = 0;
			for (; j < newNumFeatures - 1; j++)
				newInst.setValue(j, inst.value(indicesToSave.get(j)));
			// copy class attribute
			newInst.setValue(newTrain.classAttribute(),
					newTrain.classAttribute().value((int) inst.classValue()));
			newTrain.add(newInst);
		}
		log.println("done!");
		
		// create new test set
		log.print("creating new test set... ");
		int numTestInst = test.numInstances();
		Instances newTest = new Instances(test.relationName(), newAttrList, numTestInst);
		newTest.setClassIndex(newNumFeatures - 1);
		for (int i = 0; i < numTestInst; i++) {
			inst = test.instance(i);
			newInst = new Instance(newNumFeatures);
			// copy all relevant features
			int j = 0;
			for (; j < newNumFeatures - 1; j++)
				newInst.setValue(j, inst.value(indicesToSave.get(j)));
			// copy class attribute
			newInst.setValue(newTest.classAttribute(),
					newTest.classAttribute().value((int) inst.classValue()));
			newTest.add(newInst);
		}
		log.println("done!");
				
		return new Pair<Instances, Instances>(newTrain, newTest);
	}
		
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
		"function-words",
		"bag-of-words",
		"word-bigrams",
		"word-trigrams",
		//"misspellings"
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
	 * @param firstSynsetOnly
	 * 		Whether to count only the number of synonyms in the first synset returned by Wordnet
	 * 		or count all synonyms in all synsets.
	 * @return
	 * 		A mapping from the word feature indices of the given training set to the synonym count.
	 */
	private static Map<Integer,Integer> calcSynonymCount(Instances trainingSet,
			int numFeatures, boolean firstSynsetOnly) {
		log.println("Calculating synonym count for word-based features:");
		
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
				if (featureName.toLowerCase().contains(wordFeature)) {
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
				for (Synset synset: synsets) {
					synonyms.addAll(Arrays.asList(synset.getWordForms()));
					if (firstSynsetOnly)
						break;
				}
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
	protected static double sumEuclideanDistance(Matrix a, Matrix b) {
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
		/*
		log.println("Total comparisons for distance: " +
				numACols + " x " + numBCols + " = " + total);
		*/
		return sum;
	}
	
	/**
	 * Setter for the local logger.
	 * @param log
	 * 		The logger to set to.
	 */
	public static void setLogger(MultiplePrintStream log) {
		WriteprintsAnalyzer.log = log;
	}
	
	/**
	 * Getter for the local logger.
	 * @return
	 * 		The local logger.
	 */
	public static MultiplePrintStream getLogger() {
		return log;
	}
	
	// ============================================================================================
	// ============================================================================================
	
	
	/**
	 * Main for testing.
	 * @param args
	 */
	/*
	public static void main(String[] args) throws Exception {
		// initialize log
		PrintStream logPS = new PrintStream(new File("./log/" + MultiplePrintStream.getLogFilename()));
		log = new MultiplePrintStream(System.out, logPS);
		WriteprintsAnalyzer wa = new WriteprintsAnalyzer();
		
		/*
		ProblemSet ps = new ProblemSet(JSANConstants.JSAN_PROBLEMSETS_PREFIX + "drexel_1.xml");
		CumulativeFeatureDriver cfd =
				new CumulativeFeatureDriver(JSANConstants.JSAN_FEATURESETS_PREFIX + "writeprints_feature_set.xml");
		WekaInstancesBuilder wib = new WekaInstancesBuilder(false);
		List<Document> trainingDocs = ps.getAllTrainDocs();
		int numTrainDocs = trainingDocs.size();
		
		// extract features
		System.out.println("feature extraction");
		wib.prepareTrainingSet(trainingDocs, cfd);
		System.out.println("feature post extraction");
		Instances trainingSet = wib.getTrainingSet();
		System.out.println("done!");
		
		Instances train = wib.getTrainingSet();
		WekaInstancesBuilder.writeSetToARFF("d:/tmp/drexel_1_all_train.arff", train);
		System.exit(0);
		*/
	/*
		Instances train = new Instances(new FileReader(
				new File("d:/dev/writeprints.cs613.project/arff/cv/drexel_1_limited_50_cv.arff")));
		train.setClassIndex(train.numAttributes() - 1);
		
		// cross-validation
		wa.runCrossValidation(train, 10, 0);
	}
	*/
}
