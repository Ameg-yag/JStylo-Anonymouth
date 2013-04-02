package edu.drexel.psal.jstylo.analyzers.writeprints;

import java.util.*;

import weka.core.*;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Representation of an author (identity) data required for Writeprints.
 * Includes the feature matrix, basis matrix, writeprint matrix etc.
 * 
 * @author Ariel Stolerman
 *
 */
public class AuthorWPData {

	// fields
	
	/**
	 * The constant for pattern disruption calculation:<br>
	 * <code>d_p = IG(c,p) * K * (syn_total + 1) * (syn_used + 1)</code>
	 */
	protected static final int K = 2;
	
	protected String authorName;
	protected int numFeatures;
	protected Matrix featureMatrix;
	protected double[] featureAverages;
	protected List<Integer> zeroFeatures;
	protected Matrix basisMatrix;
	protected Matrix writeprint;
	
	// constructor
	
	/**
	 * Constructs a new author Writeprints data object with the given author name.
	 * @param authorName
	 * 		The name of the author.
	 */
	public AuthorWPData(String authorName) {
		this.authorName = authorName;
	}
	
	
	// methods
	
	/**
	 * Extracts the feature matrix from the given training data (set of all
	 * extracted features) for this author, identified by the
	 * <code>authorName</code> value.<br>
	 * If the <code>average</code> parameter is <code>true</code>, sets the
	 * matrix to be a single vector which is the average values across all
	 * the author's feature vectors.<br>
	 * In addition records the list of features that have 0 frequency.
	 * @param trainingData
	 * 		The ARFF training data representing feature vectors of various
	 * 		authors, including the current author, to extract the feature
	 * 		values from.
	 * @param average
	 * 		Whether to save only one feature vector, which will be the average
	 * 		of all extracted feature vectors.
	 */
	public void initFeatureMatrix(Instances trainingData, boolean average) {
		// isolate only relevant feature-vectors in a new Instances object
		int numInstances = trainingData.numInstances();
		int classIndex = trainingData.classIndex();
		Instances data = new Instances(trainingData, 0);
		for (int i = 0; i < numInstances; i++)
			if (trainingData.instance(i).stringValue(classIndex).equals(authorName))
				data.add(trainingData.instance(i));
		initFeatureMatrixHelper(data, average);
	}
	
	/**
	 * Extracts the feature matrix from the given training data (set of all
	 * extracted features), using only the given instance index.
	 * If the <code>average</code> parameter is <code>true</code>, sets the
	 * matrix to be a single vector which is the average values across all
	 * the author's feature vectors.<br>
	 * In addition records the list of features that have 0 frequency.
	 * @param trainingData
	 * 		The ARFF training data representing various feature vectors
	 * 		to extract the feature values from.
	 * @param instanceIndex
	 * 		The index of the instance to be used.
	 * @param average
	 * 		Whether to save only one feature vector, which will be the average
	 * 		of all extracted feature vectors.
	 */
	public void initFeatureMatrix(Instances trainingData, int instanceIndex,
			boolean average) {
		Instances data = new Instances(trainingData,1);
		data.add(trainingData.instance(instanceIndex));
		initFeatureMatrixHelper(data, average);
	}
	
	/**
	 * Extracts the feature matrix from the given training data.
	 * If the <code>average</code> parameter is <code>true</code>, sets the
	 * matrix to be a single vector which is the average values across all
	 * the author's feature vectors.<br>
	 * In addition records the list of features that have 0 frequency.
	 * @param data
	 * 		The ARFF training data representing various feature vectors
	 * 		to extract the feature values from.
	 * @param average
	 * 		Whether to save only one feature vector, which will be the average
	 * 		of all extracted feature vectors.
	 */
	private void initFeatureMatrixHelper(Instances data, boolean average) {
		int numInstances = data.numInstances();
		numFeatures = data.numAttributes() - 1; // exclude class attribute

		/*
		 * initialize a matrix of features (each row represents an instance, each
		 * column represents a feature).
		 */
		double[][] matrix = new double[numInstances][numFeatures];
		Instance inst;
		for (int i = 0; i < numInstances; i++) {
			inst = data.instance(i);
			for (int j = 0; j < numFeatures; j++)
				matrix[i][j] = inst.value(j);
		}

		// calculate feature averages (for later use)
		featureAverages = new double[numFeatures];
		for (int j = 0; j < numFeatures; j++) {
			for (int i = 0; i < numInstances; i++)
				featureAverages[j] += matrix[i][j];
			featureAverages[j] /= numInstances;
		}

		// save feature matrix
		if (average)
			featureMatrix = new Matrix(new double[][] {featureAverages});
		else
			featureMatrix = new Matrix(matrix);

		// record zero-frequency features for author
		zeroFeatures = new ArrayList<Integer>();
		boolean isZero;
		for (int j = 0; j < numFeatures; j++) {
			isZero = true;
			for (int i = 0; i < numInstances; i++)
				if (matrix[i][j] != 0) {
					isZero = false;
					break;
				}
			if (isZero)
				zeroFeatures.add(j);
		}
	}
	
	/**
	 * Derives the basis matrix (set of eigenvectors) from feature usage
	 * covariance matrix using Karhunen-Loeve transform (PCA) as described in
	 * {@link http://isa.umh.es/asignaturas/cscs/PR/3%20-%20Feature%20extraction.pdf}.
	 * In addition computes the author's writeprint pattern.
	 */
	public void initBasisAndWriteprintMatrices() {
		int numInstances = featureMatrix.getRowDimension();
		int numFeatures = featureMatrix.getColumnDimension();
		
		// (1) calculate the covariance matrix
		// -----------------------------------		
		// calculate X, the (#features)x(#instances) matrix
		Matrix X = featureMatrix.transpose();
		// calculate the (#features)x(#features) unbiased estimator of the covariance matrix
		// http://en.wikipedia.org/wiki/Estimation_of_covariance_matrices
		double[] mu = featureAverages;
		Matrix COV = new Matrix(new double[numFeatures][numFeatures]);
		double tmp;
		for (int i = 0; i < numFeatures; i++) {
			for (int j = 0; j < numFeatures; j++) {
				tmp = 0;
				// sum over all instances
				for (int d = 0; d < numInstances; d++) {
					tmp += (X.get(i,d) - mu[i]) * (X.get(j,d) - mu[j]) /
							(numInstances > 1 ? (numInstances - 1) : 1);
				}
				COV.set(i,j,tmp);
			}
		}
		
		// (2)	calculate eigenvalues followed by eigenvectors - the basis matrix,
		// and calculate the principal component matrix - the author's writeprint
		// ----------------------------------------------------------------------
		EigenvalueDecomposition eig = COV.eig();
		basisMatrix = eig.getV();
		writeprint = basisMatrix.transpose().times(X); // was: times(X_minux_MU) instead of times(X)
		WriteprintsAnalyzer.log.print("(basis: " + basisMatrix.getRowDimension() +
				"x" + basisMatrix.getColumnDimension() +
				", writeprint: " + writeprint.getRowDimension() +
				"x" + writeprint.getColumnDimension() + ")  ");
	}
	
	/**
	 * Generates a pattern for the target author using the target author's feature values
	 * and the basis author's basis matrix (in contrast with the paper, there's no particular
	 * use for the basis author's feature set, as it is the same for all authors in this
	 * implementation).
	 * @param basisAuthor
	 * 		The basis author (the one supplying the basis matrix).
	 * @param targetAuthor
	 * 		The target author (the one to generate the pattern for).
	 * @return
	 * 		The pattern matrix for the target author.
	 */
	public static Matrix generatePattern(AuthorWPData basisAuthor, AuthorWPData targetAuthor) {
		Matrix targetValuesTransposed = targetAuthor.featureMatrix.transpose();
		//Matrix targetValues = targetAuthor.featureMatrix;
		Matrix basisTransposed = basisAuthor.basisMatrix.transpose();
		return basisTransposed.times(targetValuesTransposed);
	}
	
	/**
	 * Adds pattern disruption values with respect to the given author data.
	 * That is, for any zero-frequency feature of this author, that is not a
	 * zero-frequency feature for the other author, adds pattern disruption values
	 * calculated as follows:<br>
	 * <code>d_p = IG(c,p) * K * (syn_total + 1) * (syn_used + 1)</code>
	 * @param other
	 * 		The other author data to add the pattern disruption with respect to.
	 * @param IG
	 * 		Information gain for all features.
	 * @param wordsSynCount
	 * 		Mapping from all word-based feature indices to their synonym-count.
	 * @param otherPattern
	 * 		The pattern of the other author generated with this author's basis matrix.
	 */
	public void addPatternDisruption(AuthorWPData other, double[] IG,
			Map<Integer,Integer> wordsSynCount, Matrix otherPattern) {
		
		// set pattern disruption values
		int synUsed, synTotal;
		double patternDisruption;
		double thisWPAvg, otherPatternAvg;
		//int basisNumRows = basisMatrix.getRowDimension();
		int basisNumCols = basisMatrix.getColumnDimension();
		for (int j: zeroFeatures) {
			if (!other.zeroFeatures.contains(j)) {
				if (wordsSynCount != null && wordsSynCount.keySet().contains(j)) {
					synUsed = 1; // simplifying synonym usage count
					synTotal = wordsSynCount.get(j);
				}
				else {
					synUsed = 0;
					synTotal = 0;
				}
				patternDisruption = IG[j] * K * (synTotal + 1) * (synUsed + 1);
				
				for (int k = 0; k < basisNumCols; k++) {
					thisWPAvg = avgForRow(writeprint,k);
					otherPatternAvg = avgForRow(otherPattern, k);
					if (thisWPAvg > otherPatternAvg)
						basisMatrix.set(j, k, -1 * patternDisruption);
					else
						basisMatrix.set(j, k, patternDisruption);
				}
				
				/*
				// update pattern disruption sign
				thisWPAvg = avgForRow(writeprint,j);
				otherPatternAvg = avgForRow(otherPattern, j);
				if (thisWPAvg > otherPatternAvg)
					patternDisruption *= -1;
				
				// update basis matrix with pattern disruption value
				for (int i = 0; i < basisNumRows; i++)
					basisMatrix.set(j, i, patternDisruption);
				*/
			}
		}
		
		// update writeprint
		writeprint = generatePattern(this, this);
	}
	
	/**
	 * Calculates and returns the average over all columns of the given matrix
	 * for the given row index.
	 * @param m
	 * 		The matrix.
	 * @param row
	 * 		The row index with respect to which calculate the average.
	 * @return
	 * 		The average over all columns of the given matrix for the given column index.
	 */
	private static double avgForRow(Matrix m, int row) {
		int numCols = m.getColumnDimension();
		double sum = 0;
		for (int i = 0; i < numCols; i ++)
			sum += m.get(row,i);
		return sum / numCols;
	}
	
	@Override
	protected AuthorWPData clone() {
		AuthorWPData cloned = new AuthorWPData(authorName);
		cloned.basisMatrix = new Matrix(basisMatrix.getArrayCopy());
		cloned.featureAverages = Arrays.copyOf(featureAverages, featureAverages.length);
		cloned.featureMatrix = new Matrix(featureMatrix.getArrayCopy());
		cloned.numFeatures = numFeatures;
		cloned.writeprint = new Matrix(writeprint.getArrayCopy());
		cloned.zeroFeatures = new ArrayList<Integer>(zeroFeatures);
		return cloned;
	}
	
	/**
	 * Clones only parts of this author data and shallow copies the rest.
	 * Only the basis and writeprint matrices are deep-copied.
	 * @return
	 * 		The half-cloned author data.
	 */
	protected AuthorWPData halfClone() {
		AuthorWPData halfCloned = new AuthorWPData(authorName);
		halfCloned.basisMatrix = new Matrix(basisMatrix.getArrayCopy());
		halfCloned.featureAverages = featureAverages;
		halfCloned.featureMatrix = featureMatrix;
		halfCloned.numFeatures = numFeatures;
		halfCloned.writeprint = new Matrix(writeprint.getArrayCopy());
		halfCloned.zeroFeatures = zeroFeatures;
		return halfCloned;
	}
	
	// ============================================================================================
	// ============================================================================================
	
	/**
	 * Main for testing
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("author a");
		System.out.println("========");
		AuthorWPData a = new AuthorWPData("a");
		a.featureMatrix = new Matrix(new double[][]{
				{4,2,0,3,1,5,3,2,3,5,4,3},
				{1,2,0,4,5,6,2,4,3,2,1,4}	
		});
		a.zeroFeatures = new ArrayList<Integer>();
		a.zeroFeatures.add(2);
		a.featureAverages = new double[]{2.5,2,0,3.5,3,5.5,2.5,3,3,3.5,2.5,3.5};
		a.numFeatures = a.featureAverages.length;
		a.initBasisAndWriteprintMatrices();
		System.out.println("features:");
		a.featureMatrix.print(4, 4);
		System.out.println();
		System.out.println("writeprint:");
		a.writeprint.print(4,4);
		System.out.println();
		System.out.println("basis:");
		a.basisMatrix.print(4, 4);
		System.out.println();
		System.out.println();
		
		System.out.println("author a");
		System.out.println("========");
		AuthorWPData b = new AuthorWPData("a");
		b.featureMatrix = new Matrix(new double[][]{
				{3,6,5,4,2,7,0,6,4,3,5,7},
				{6,5,3,7,6,1,0,7,4,1,2,3}	
		});
		b.zeroFeatures = new ArrayList<Integer>();
		b.zeroFeatures.add(6);
		b.featureAverages = new double[]{4.5,5.5,4,5.5,4,4,0,6.5,4,2,3.5,5};
		b.numFeatures = b.featureAverages.length;
		b.initBasisAndWriteprintMatrices();
		System.out.println("features:");
		b.featureMatrix.print(4, 4);
		System.out.println();
		System.out.println("writeprint:");
		b.writeprint.print(4,4);
		System.out.println();
		System.out.println("basis:");
		b.basisMatrix.print(4, 4);
		System.out.println();
		System.out.println();
		
		// compare
		// -------
		
		double[] IG = new double[12];
		for (int i = 0; i < 12; i++) IG[i] = 1;
		Map<Integer,Integer> syn = new HashMap<Integer, Integer>();
		syn.put(2, 3);
		syn.put(6, 3);
		a.addPatternDisruption(b, IG, syn, AuthorWPData.generatePattern(a, b));
		b.addPatternDisruption(a, IG, syn, AuthorWPData.generatePattern(b, a));
		System.out.println("a after pattern disruption:");
		a.writeprint.print(4, 4);
		a.basisMatrix.print(4, 4);
		System.out.println("b after pattern disruption:");
		b.writeprint.print(4, 4);
		b.basisMatrix.print(4, 4);
		
		double dist1 = WriteprintsAnalyzer.sumEuclideanDistance(AuthorWPData.generatePattern(b, a), b.writeprint);
		System.out.println("dist1: " + dist1);
		double dist2 = WriteprintsAnalyzer.sumEuclideanDistance(AuthorWPData.generatePattern(a, b), a.writeprint);
		System.out.println("dist2: " + dist2);
		System.out.println("avg: " + (dist1 + dist2)/2);
	}
}








