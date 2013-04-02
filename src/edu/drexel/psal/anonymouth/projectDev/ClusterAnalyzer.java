package edu.drexel.psal.anonymouth.projectDev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.drexel.psal.anonymouth.gooie.ClusterViewerDriver;
import edu.drexel.psal.anonymouth.utils.Pair;
import edu.drexel.psal.anonymouth.utils.SmartIntegerArray;
import edu.drexel.psal.jstylo.generics.Logger;

/**
 * @author Andrew W.E. McDonald
 *
 */
public class ClusterAnalyzer {

	private HashMap<SmartIntegerArray,Integer> commonClusterSetMap;
	ArrayList<String> theFeatures;
	ArrayList<String> theDocs;
	int numDocs;
	int numFeatures;
	int numClusters;
	int[][] clustersByDoc; 
	private static ClusterGroup[] someClusters;
	
	/**
	 * Constructor for ClusterAnalyzer
	 */
	public ClusterAnalyzer(ArrayList<String> featuresToUse,int maxClusters){
		
		Logger.logln("Start construction of ClusterAnanlyzer");
		theDocs = DocumentMagician.getTrainTitlesList();
		theFeatures = featuresToUse;
		numDocs = theDocs.size();
		numFeatures = featuresToUse.size();
		clustersByDoc = new int[numDocs][numFeatures];
		int i,j;
		for(i=0; i< numFeatures;i++){
			for(j=0; j< numDocs; j++){
				clustersByDoc[j][i] = 0;
			}
		}
	}
	
	/**
	 * The clusters in the input Attribute are placed into their respective places in a 3d array based on feature, document, and Cluster number.
	 * @param attrib
	 * @return
	 * 	true if nothing went wrong
	 */
	public boolean addFeature(Attribute attrib){
		int row = theFeatures.indexOf(attrib.getConcatGenNameAndStrInBraces());
		Cluster[] orderedClusters = attrib.getOrderedClusters();
		int lenClusterRay = orderedClusters.length;
		int i =0;
		int j = 0;
		int lenPairRay;
		int clusterNum;
		int col;
		for(i=0; i<lenClusterRay;i++){
			Pair[] pairRay = orderedClusters[i].getElements();
			clusterNum = i;
			lenPairRay = pairRay.length;
			for(j=0;j<lenPairRay;j++){
				col = theDocs.indexOf(pairRay[j].doc);
				clustersByDoc[col][row] = clusterNum+1;
			}
		}
		return true;
	}
	
	public void analyzeNow(){ 
		Logger.logln("Begin analysis of clusters in analyzeNow in ClusterAnalyzer");
		//System.out.println(Arrays.deepToString(clustersByDoc).replace("], [","]\n[")); 
		int i,j,k;
		commonClusterSetMap = new HashMap<SmartIntegerArray,Integer>(theDocs.size()); // worst case, no two documents fall in same set of clusters
		i=0;
		j=0;
		k=0;
		
		for(i=0;i<numDocs;i++){
				SmartIntegerArray tempKey = new SmartIntegerArray(clustersByDoc[i]);
				if(commonClusterSetMap.containsKey(tempKey) == true)
					commonClusterSetMap.put(tempKey,commonClusterSetMap.get(tempKey)+1);
				else
					commonClusterSetMap.put(tempKey,1);
		}
		Set<SmartIntegerArray> clusterSetMapKeys = commonClusterSetMap.keySet();
		int numKeys = clusterSetMapKeys.size();
		SmartIntegerArray tempKey;
		Iterator<SmartIntegerArray> csmkIter = clusterSetMapKeys.iterator();
		int[] resultsByCluster = new int[numKeys];
		i =0;
		j = 0; 
		int lenKey;
		int[] clusterGroupFreq = new int[numKeys];
		ClusterGroup[] someClusters = new ClusterGroup[numKeys];
		double tempSum = 0;
		while(csmkIter.hasNext()){
			tempSum = 0;
			tempKey = csmkIter.next();
			//System.out.print("Key => "+tempKey+" .... Value => ");
			clusterGroupFreq[j] = commonClusterSetMap.get(tempKey);
			//System.out.print(clusterGroupFreq[j]);
			//System.out.println();
			lenKey = tempKey.length();
			// clusterGroupFreq[i]*(summation from i=0 to i=lenKey : ((cluster preference number for feature(i))*((number of features +1)-i))/num features)
			// this allows feature importance (as determined by information gain),  and cluster preference to influence the ordering of the cluster groups.
			int[] keyRay = tempKey.toIntArray();
			for(i=0; i< lenKey; i++){
				tempSum += (keyRay[i]*((lenKey+1) - i))/lenKey;
			}	
			//tempSum = tempSum * ((double)(.25)*clusterGroupFreq[j]/numDocs);
			someClusters[j] = new ClusterGroup(tempKey,tempSum);
			j++;
		}
		Arrays.sort(someClusters);
		ClusterViewerDriver.clusterGroupReady = true;
		this.someClusters = someClusters;
		//for(i=0;i<someClusters.length;i++){
		//	System.out.println(someClusters[i]);
		//}
		Logger.logln("ClusterAnalyzer analysis complete");
	}
	
	public static ClusterGroup[] getClusterGroupArray(){
		return someClusters;
	}

	
}


	
		
		
		
		
		
		
		
