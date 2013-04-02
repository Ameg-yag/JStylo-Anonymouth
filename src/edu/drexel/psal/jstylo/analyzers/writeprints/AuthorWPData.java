package edu.drexel.psal.jstylo.analyzers.writeprints;

import java.util.*;

import edu.drexel.psal.jstylo.generics.MultiplePrintStream;

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
	 * Local logger
	 */
	public static MultiplePrintStream log = new MultiplePrintStream();
	
	/**
	 * The constant for pattern disruption calculation:<br>
	 * <code>d_p = IG(c,p) * K * (syn_total + 1) * (syn_used + 1)</code>
	 */
	protected static final int K = 2;
	
	protected String authorName;
	protected int numInstances;
	protected int numAuthorInstances;
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
		numInstances = trainingData.numInstances();
		int classIndex = trainingData.classIndex();
		Instances data = new Instances(trainingData, 0);
		for (int i = 0; i < numInstances; i++)
			if (trainingData.instance(i).stringValue(classIndex).equals(authorName))
			{
				data.add(trainingData.instance(i));
				numAuthorInstances++;
			}
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
		//int numFeatures = featureMatrix.getColumnDimension();
		
		/* (1) calculate the covariance matrix */
		
		// calculate X, the (#features)x(#instances) matrix
		Matrix X = featureMatrix.transpose();
		// calculate MU, the (#features)x(#instances) matrix of feature means
		// where each cell i,j equals mean(feature_i)
		double[][] MU_matrix_values = new double[numFeatures][numInstances];
		for (int i = 0; i < numFeatures; i++)
			for (int j = 0; j < numInstances; j++)
				MU_matrix_values[i][j] = featureAverages[i];
		Matrix MU = new Matrix(MU_matrix_values);
		// calculate X - MU
		Matrix X_minus_MU = X.minus(MU);
		// finally, calculate the covariance matrix
		Matrix COV = X_minus_MU.times(X_minus_MU.transpose()).times(1 / ((double) numFeatures));
		
		/* (2)	calculate eigenvalues followed by eigenvectors - the basis matrix,
		 * 		and calculate the principal component matrix - the author's writeprint*/
		EigenvalueDecomposition eigenvalues = COV.eig();
		basisMatrix = eigenvalues.getV();
		writeprint = basisMatrix.transpose().times(X_minus_MU);
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
		int basisNumRows = basisMatrix.getRowDimension();
		for (int j: zeroFeatures) {
			if (!other.zeroFeatures.contains(j)) {
				if (wordsSynCount.keySet().contains(j)) {
					synUsed = 1; // simplifying synonym usage count
					synTotal = wordsSynCount.get(j);
				}
				else {
					synUsed = 0;
					synTotal = 0;
				}
				patternDisruption = IG[j] * K * (synTotal + 1) * (synUsed + 1);
				
				// update pattern disruption sign
				thisWPAvg = avgForRow(writeprint,j);
				otherPatternAvg = avgForRow(otherPattern, j);
				if (thisWPAvg > otherPatternAvg)
					patternDisruption *= -1;
				
				// update basis matrix with pattern disruption value
				for (int i = 0; i < basisNumRows; i++)
					basisMatrix.set(j, i, patternDisruption);
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
		AuthorWPData a = new AuthorWPData("a");
		double[][] d = new double[][]{
				{1,2,3},
				{4,5,6}
		};
		a.featureAverages = new double[] {2.5,3.5,4.5};
		a.featureMatrix = new Matrix(d);
		a.initBasisAndWriteprintMatrices();
		a.writeprint.print(4,4);
	}
}
