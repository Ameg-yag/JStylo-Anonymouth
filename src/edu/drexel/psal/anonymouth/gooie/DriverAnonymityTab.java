package edu.drexel.psal.anonymouth.gooie;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.Cluster;
import edu.drexel.psal.anonymouth.engine.ClusterAnalyzer;
import edu.drexel.psal.anonymouth.engine.ClusterGroup;
import edu.drexel.psal.jstylo.generics.Logger;

public class DriverAnonymityTab {

	private final static String NAME = "( DriverAnonymityTab ) - ";

	private static GUIMain main;

	public static boolean clusterGroupReady = false;
	private static ClusterGroup[] clusterGroupRay;
	private static int lenCGR;
	private static int[][] intRepresentation;
	private static String[] stringRepresentation;
	protected static JLabel[] nameLabels;
	protected static int numFeatures;
	protected static int[] selectedClustersByFeature;

	public static class alignListRenderer implements ListCellRenderer {
		
		int alignValue;
		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

		public alignListRenderer(int value)
		{
			super();
			alignValue = value;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
	      boolean isSelected, boolean cellHasFocus) 
		{

		    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
		        isSelected, cellHasFocus);

		    renderer.setHorizontalAlignment(alignValue);

		    return renderer;
	    }
	}
	
	public static int[][] getIntRep()
	{
		return intRepresentation;
	}

	public static String[] getStringRep()
	{
		return stringRepresentation;
	}
	
	public static boolean setClusterGroup()
	{
		Logger.logln("Cluster group array retrieved from ClusterAnalyzer and brought to ClusterViewerDriver");
		if(clusterGroupReady)
		{
			clusterGroupRay = ClusterAnalyzer.getClusterGroupArray();
			lenCGR = clusterGroupRay.length;
			return true;
		}
		else
			return false;
	}
	
	//previously called makePanels()
	public static void setAttributes(Attribute[] attribs) {		
		main = GUIMain.inst;

		//System.out.println("length of attribs: "+attribs.length);
		int numFeatures = attribs.length;
		double[] minimums = new double[numFeatures]; 
		double[] maximums = new double[numFeatures];
		double[] authorMin = new double[numFeatures];
		double[] authorMax = new double[numFeatures];
		double[] presentValues = new double[numFeatures];
		String[] names = new String[numFeatures];

		int i = 0;
		ArrayList<Cluster[]> everySingleCluster = new ArrayList<Cluster[]>(numFeatures);
		double tempMinMax;
		String tempString;
		String dashes;
		selectedClustersByFeature = new int[numFeatures];
		double tempAuthorMinMax;
		for(i=0; i< numFeatures;i++){
			selectedClustersByFeature[i] = -1; // initialize with no clusters selected;
			everySingleCluster.add(i,attribs[i].getOrderedClusters());
			authorMin[i] = attribs[i].getAuthorAvg() - attribs[i].getAuthorConfidence();
			if(authorMin[i] <  0)
				authorMin[i] = 0;
			authorMax[i] = attribs[i].getAuthorAvg() + attribs[i].getAuthorConfidence();	
			presentValues[i] = attribs[i].getToModifyValue();
			tempMinMax = attribs[i].getTrainMax();
			tempAuthorMinMax = authorMax[i];

			if(tempAuthorMinMax < presentValues[i])
				tempAuthorMinMax = presentValues[i];

			if(tempAuthorMinMax > tempMinMax)
				maximums[i] = tempAuthorMinMax;
			else
				maximums[i] = tempMinMax; 
			tempMinMax = attribs[i].getTrainMin();
			tempAuthorMinMax = authorMin[i];

			if(tempAuthorMinMax > presentValues[i])
				tempAuthorMinMax = presentValues[i];

			if(tempAuthorMinMax < tempMinMax)
				minimums[i] = tempAuthorMinMax;
			else
				minimums[i] = tempMinMax;
			//System.out.println(presentValues[i]);
			tempString = attribs[i].getStringInBraces();
			if(tempString == "")
				dashes = "";
			else
				dashes = "--";
			names[i] = attribs[i].getGenericName()+dashes+tempString;
		}
		
		Iterator<Cluster[]> outerLevel = everySingleCluster.iterator();
		nameLabels = new JLabel[numFeatures];
		i=0;
		int[] initialLayoverVals = new int[numFeatures];
		String[] usedNames = new String[numFeatures];
		while(outerLevel.hasNext())
		{
			nameLabels[i] = new JLabel(names[i]); // for if you want to edit the label in any way
			usedNames[i] = names[i];

			JPanel clusterPanel = new ClusterPanel(outerLevel.next(),i,minimums[i],maximums[i], authorMin[i],authorMax[i],presentValues[i]);

			initialLayoverVals[i] = 1;
			i++;

		}
//		int numFeatures = attribs.length;
//		ArrayList<Cluster[]> everySingleCluster = new ArrayList<Cluster[]>(numFeatures);
//		double[] minimums = new double[numFeatures]; 
//		double[] maximums = new double[numFeatures];
//		double[] authorMin = new double[numFeatures];
//		double[] authorMax = new double[numFeatures];
//		double[] presentValues = new double[numFeatures];
//		
//		double tempMinMax;
//		double tempAuthorMinMax;
//		
//		for (int i = 0; i < numFeatures; i++) {
//			everySingleCluster.add(i, attribs[i].getOrderedClusters());
//			authorMin[i] = attribs[i].getAuthorAvg() - attribs[i].getAuthorConfidence();
//			
//			if (authorMin[i] < 0)
//				authorMin[i] = 0;
//			
//			authorMax[i] = attribs[i].getAuthorAvg() + attribs[i].getAuthorConfidence();	
//			presentValues[i] = attribs[i].getToModifyValue();
//			tempMinMax = attribs[i].getTrainMax();
//			tempAuthorMinMax = authorMax[i];
//			
//			if(tempAuthorMinMax < presentValues[i])
//				tempAuthorMinMax = presentValues[i];
//
//			if(tempAuthorMinMax > tempMinMax)
//				maximums[i] = tempAuthorMinMax;
//			else
//				maximums[i] = tempMinMax; 
//			tempMinMax = attribs[i].getTrainMin();
//			tempAuthorMinMax = authorMin[i];
//
//			if(tempAuthorMinMax > presentValues[i])
//				tempAuthorMinMax = presentValues[i];
//
//			if(tempAuthorMinMax < tempMinMax)
//				minimums[i] = tempAuthorMinMax;
//			else
//				minimums[i] = tempMinMax;
//			
//			System.out.printf("name = '%s', minimums[%s] = %s, maximums[%s] = %s\n", attribs[i].getStringInBraces(), i, minimums[i], i, maximums[i]);
//			System.out.printf("authorMin[%s] = %s, authorMax[%s] = %s, presentValues[%s] = %s\n", i, authorMin[i], i, authorMax[i], i, presentValues[i]);
//		}
//		System.exit(0);
	}
	
	public static void initializeAnonymityBar(GUIMain main)
	{
		Logger.logln("Initializing ClusterViewer");

		boolean cgIsSet = setClusterGroup();

		intRepresentation = new int[lenCGR][clusterGroupRay[0].getGroupKey().length()];
		stringRepresentation = new String[1+lenCGR];
		stringRepresentation[0] = "Select Targets";
		for(int i = 0; i < lenCGR; i++)
		{
			intRepresentation[i] = clusterGroupRay[i].getGroupKey().toIntArray();
			stringRepresentation[i+1] = clusterGroupRay[i].getGroupKey().toString();
		}

		//ComboBoxModel clusterGroupChoices = new DefaultComboBoxModel(stringRepresentation);
		//main.clusterConfigurationBox.setModel(clusterGroupChoices);
		//main.mainJTabbedPane.setSelectedIndex(4);
		int[] theOne = intRepresentation[0];
		selectedClustersByFeature = theOne;
//		Logger.logln("Initializing Anonymity Bar");
//
//		intRepresentation = new int[lenCGR][clusterGroupRay[0].getGroupKey().length()];
//		stringRepresentation = new String[1+lenCGR];
//		stringRepresentation[0] = "Select Targets";
//		for(int i = 0; i < lenCGR; i++)
//		{
//			intRepresentation[i] = clusterGroupRay[i].getGroupKey().toIntArray();
//			stringRepresentation[i+1] = clusterGroupRay[i].getGroupKey().toString();
//		}
//
//		boolean cgIsSet = setClusterGroup();
//		//ComboBoxModel clusterGroupChoices = new DefaultComboBoxModel(stringRepresentation);
//		//main.clusterConfigurationBox.setModel(clusterGroupChoices);
//		//main.mainJTabbedPane.setSelectedIndex(4);
//		int[] theOne = intRepresentation[0];
//		selectedClustersByFeature = theOne;
//
//		/*if(showMessage == true)
//			JOptionPane.showMessageDialog(main, "The red dot is where each of your features are now.\nThe center of the " +
//					"green oval is where they will be after you are done editing.\nAccept these targets if they all look reasonably " +
//					"far away from the purple ovals. If not, get new green ovals.","Target Selection",JOptionPane.INFORMATION_MESSAGE,GUIMain.icon);
//		*/
	}
//
//		int alignValue;
//		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
//
//		public alignListRenderer(int value) {
//			super();
//			alignValue = value;
//		}
//
//		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//			JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//			renderer.setHorizontalAlignment(alignValue);
//			
//			return renderer;
//		}
//	}
//
//	public static int[][] getIntRep() {
//		return intRepresentation;
//	}
//
//	public static String[] getStringRep() {
//		return stringRepresentation;
//	}
//
//	public static void makePanels(Attribute[] attribs) {
//		main = GUIMain.inst;
//
//		int numFeatures = attribs.length;
//		double[] minimums = new double[numFeatures]; 
//		double[] maximums = new double[numFeatures];
//		double[] authorMin = new double[numFeatures];
//		double[] authorMax = new double[numFeatures];
//		double[] presentValues = new double[numFeatures];
//		String[] names = new String[numFeatures];
//
//		int i = 0;
//		ArrayList<Cluster[]> everySingleCluster = new ArrayList<Cluster[]>(numFeatures);
//		double tempMinMax;
//		String tempString;
//		String dashes;
//		selectedClustersByFeature = new int[numFeatures];
//		double tempAuthorMinMax;
//		for(i=0; i< numFeatures;i++){
//			selectedClustersByFeature[i] = -1; // initialize with no clusters selected;
//			everySingleCluster.add(i,attribs[i].getOrderedClusters());
//			authorMin[i] = attribs[i].getAuthorAvg() - attribs[i].getAuthorConfidence();
//			if(authorMin[i] <  0)
//				authorMin[i] = 0;
//			authorMax[i] = attribs[i].getAuthorAvg() + attribs[i].getAuthorConfidence();	
//			presentValues[i] = attribs[i].getToModifyValue();
//			tempMinMax = attribs[i].getTrainMax();
//			tempAuthorMinMax = authorMax[i];
//
//			if(tempAuthorMinMax < presentValues[i])
//				tempAuthorMinMax = presentValues[i];
//
//			if(tempAuthorMinMax > tempMinMax)
//				maximums[i] = tempAuthorMinMax;
//			else
//				maximums[i] = tempMinMax; 
//			tempMinMax = attribs[i].getTrainMin();
//			tempAuthorMinMax = authorMin[i];
//
//			if(tempAuthorMinMax > presentValues[i])
//				tempAuthorMinMax = presentValues[i];
//
//			if(tempAuthorMinMax < tempMinMax)
//				minimums[i] = tempAuthorMinMax;
//			else
//				minimums[i] = tempMinMax;
//			//System.out.println(presentValues[i]);
//			tempString = attribs[i].getStringInBraces();
//			if(tempString == "")
//				dashes = "";
//			else
//				dashes = "--";
//			names[i] = attribs[i].getGenericName()+dashes+tempString;
//		}
//
//		nameLabels = new JLabel[numFeatures];
//		i=0;
//	}
//
//	public static void initializeClusterViewer(GUIMain main, boolean showMessage)
//	{
//		Logger.logln("Initializing ClusterViewer");
//		int numPanels = clusterPanels.length;
//		for(int i = 0; i < numPanels; i++)
//		{
//			if (i == 0 || i % 2 == 0)
//			{
//				nameLabels[i].setBackground(Color.WHITE);
//				clusterPanels[i].setBackground(Color.WHITE);
//				finalPanels[i].setBorder(main.rlborder);
//			}
//			else
//			{
//				nameLabels[i].setBackground(main.tan);
//				clusterPanels[i].setBackground(main.tan);
//				finalPanels[i].setBorder(main.rlborder);
//			}
//			nameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
//			nameLabels[i].setOpaque(true);
//			clusterPanels[i].setPreferredSize(new Dimension(800,40));
//			finalPanels[i].setPreferredSize(new Dimension(800,60));
//			main.anonymityPanel.add(finalPanels[i]);
//		}
//
//		stringRepresentation[0] = "Select Targets";
//		
//		//ComboBoxModel clusterGroupChoices = new DefaultComboBoxModel(stringRepresentation);
//		//main.clusterConfigurationBox.setModel(clusterGroupChoices);
//		//main.mainJTabbedPane.setSelectedIndex(4);
//		int[] theOne = intRepresentation[0];
//		selectedClustersByFeature = theOne;
//
//		/*if(showMessage == true)
//			JOptionPane.showMessageDialog(main, "The red dot is where each of your features are now.\nThe center of the " +
//					"green oval is where they will be after you are done editing.\nAccept these targets if they all look reasonably " +
//					"far away from the purple ovals. If not, get new green ovals.","Target Selection",JOptionPane.INFORMATION_MESSAGE,GUIMain.icon);
//		*/
	}