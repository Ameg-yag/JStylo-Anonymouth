package edu.drexel.psal.anonymouth.gooie;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;

import net.miginfocom.swing.MigLayout;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.Cluster;
import edu.drexel.psal.anonymouth.engine.ClusterAnalyzer;
import edu.drexel.psal.anonymouth.engine.ClusterGroup;
import edu.drexel.psal.anonymouth.engine.DataAnalyzer;
import edu.drexel.psal.jstylo.generics.Logger;

public class DriverClustersTab {
	
	private final static String NAME = "( DriverClustersTab ) - ";


	private static GUIMain main;

	private static int lenJPanels;
	public static boolean clusterGroupReady = false;
	private static ClusterGroup[] clusterGroupRay;
	private static int lenCGR;
	private static int[][] intRepresentation;
	private static String[] stringRepresentation;
	protected static JPanel[] finalPanels;
	protected static JLabel[] nameLabels;
	protected static JPanel[] clusterPanels;
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

	public static void makePanels(Attribute[] theOnesYouWantToSee)
	{
		main = GUIMain.inst;

		//System.out.println("length of theOnesYouWantToSee: "+theOnesYouWantToSee.length);
		int numFeatures = theOnesYouWantToSee.length;
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
			everySingleCluster.add(i,theOnesYouWantToSee[i].getOrderedClusters());
			authorMin[i] = theOnesYouWantToSee[i].getAuthorAvg() - theOnesYouWantToSee[i].getAuthorConfidence();
			if(authorMin[i] <  0)
				authorMin[i] = 0;
			authorMax[i] = theOnesYouWantToSee[i].getAuthorAvg() + theOnesYouWantToSee[i].getAuthorConfidence();	
			presentValues[i] = theOnesYouWantToSee[i].getToModifyValue();
			tempMinMax = theOnesYouWantToSee[i].getTrainMax();
			tempAuthorMinMax = authorMax[i];

			if(tempAuthorMinMax < presentValues[i])
				tempAuthorMinMax = presentValues[i];

			if(tempAuthorMinMax > tempMinMax)
				maximums[i] = tempAuthorMinMax;
			else
				maximums[i] = tempMinMax; 
			tempMinMax = theOnesYouWantToSee[i].getTrainMin();
			tempAuthorMinMax = authorMin[i];

			if(tempAuthorMinMax > presentValues[i])
				tempAuthorMinMax = presentValues[i];

			if(tempAuthorMinMax < tempMinMax)
				minimums[i] = tempAuthorMinMax;
			else
				minimums[i] = tempMinMax;
			//System.out.println(presentValues[i]);
			tempString = theOnesYouWantToSee[i].getStringInBraces();
			if(tempString == "")
				dashes = "";
			else
				dashes = "--";
			names[i] = theOnesYouWantToSee[i].getGenericName()+dashes+tempString;
		}

		Iterator<Cluster[]> outerLevel = everySingleCluster.iterator();
		clusterPanels = new JPanel[numFeatures];// everySingleCluster.size()
		nameLabels = new JLabel[numFeatures];
		finalPanels = new JPanel[numFeatures];
		i=0;
		int[] initialLayoverVals = new int[numFeatures];
		String[] usedNames = new String[numFeatures];
		while(outerLevel.hasNext())
		{
			nameLabels[i] = new JLabel(names[i]); // for if you want to edit the label in any way
			usedNames[i] = names[i];

			JPanel clusterPanel = new ClusterPanel(outerLevel.next(),i,minimums[i],maximums[i], authorMin[i],authorMax[i],presentValues[i]);
			clusterPanels[i] = clusterPanel;

			MigLayout layout = new MigLayout(
					"fill, wrap, ins 0",
					"fill, grow",
					"[20]0[grow, fill]");
			finalPanels[i] = new JPanel(layout);
			finalPanels[i].add(nameLabels[i], "grow");
			finalPanels[i].add(clusterPanels[i], "grow");

			initialLayoverVals[i] = 1;
			i++;

		}
		GUIMain.inst.addClusterFeatures(usedNames); //--- fills the features and subfeatures list for searching
		/*
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClusterViewerFrame inst = new ClusterViewerFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
				}
			});
			*/
	}

	public static void initializeClusterViewer(GUIMain main, boolean showMessage)
	{
		Logger.logln("Initializing ClusterViewer");
		int numPanels = clusterPanels.length;
		for(int i = 0; i < numPanels; i++)
		{
			if (i == 0 || i % 2 == 0)
			{
				nameLabels[i].setBackground(Color.WHITE);
				clusterPanels[i].setBackground(Color.WHITE);
				finalPanels[i].setBorder(main.rlborder);
			}
			else
			{
				nameLabels[i].setBackground(main.tan);
				clusterPanels[i].setBackground(main.tan);
				finalPanels[i].setBorder(main.rlborder);
			}
			nameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
			nameLabels[i].setOpaque(true);
			clusterPanels[i].setPreferredSize(new Dimension(800,40));
			finalPanels[i].setPreferredSize(new Dimension(800,60));
			main.clusterHolderPanel.add(finalPanels[i]);
		}

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
		lenJPanels = clusterPanels.length;
		for(int i = 0; i < lenJPanels; i++)
		{
			clusterPanels[i].revalidate();
			clusterPanels[i].repaint();
		}

		/*if(showMessage == true)
			JOptionPane.showMessageDialog(main, "The red dot is where each of your features are now.\nThe center of the " +
					"green oval is where they will be after you are done editing.\nAccept these targets if they all look reasonably " +
					"far away from the purple ovals. If not, get new green ovals.","Target Selection",JOptionPane.INFORMATION_MESSAGE,GUIMain.icon);
		*/
	}

	public static void findCluster(GUIMain main, String name)
	{
//		for (int i = 0; i < main.clusterHolderPanel.getComponentCount(); i = i + 2)
//		{
//			JPanel panel = (JPanel)main.clusterHolderPanel.getComponent(i);
//			JLabel label = (JLabel)((JPanel)panel.getComponent(0)).getComponent(0);
//			String labelName = label.getText();
//			if (name.equals(labelName))
//			{
//				main.clusterScrollPane.getVerticalScrollBar().setValue((i/2)*74); // 70 + 4 extra pixels for the borders
//				break;
//			}
//		}
	}

	public static void initListeners(final GUIMain main) 
	{	
		main.featuresList.setCellRenderer(new alignListRenderer(SwingConstants.CENTER));
		main.subFeaturesList.setCellRenderer(new alignListRenderer(SwingConstants.CENTER));

		main.featuresList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				int index = main.featuresList.getSelectedIndices()[0];
				main.subFeaturesListModel = new DefaultListModel();
				for (int i = 0; i < main.subfeatures.get(index).size(); i++)
					main.subFeaturesListModel.addElement(main.subfeatures.get(index).get(i));
				main.subFeaturesList.setModel(main.subFeaturesListModel);
				main.subFeaturesListScrollPane.getVerticalScrollBar().setValue(0);
				if (main.subfeatures.get(index).isEmpty())
				{
					main.subFeaturesList.setEnabled(false);
					findCluster(main, (String)main.featuresList.getSelectedValue());
				}
				else
					main.subFeaturesList.setEnabled(true);
			}
		});

		main.subFeaturesList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				findCluster(main, (String)main.featuresList.getSelectedValue() + "--" + (String)main.subFeaturesList.getSelectedValue());
			}
		});
	}



	/*
	main.clusterConfigurationBox.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = main.clusterConfigurationBox.getSelectedIndex();
			Logger.logln("Cluster Group number '"+(selectedIndex-1)+"' selected for VIEWING");
			int i = 0;
			if(selectedIndex != 0){
				int[] theOne = intRepresentation[selectedIndex-1];
				ClusterViewer.selectedClustersByFeature = theOne;
				lenJPanels = ClusterViewer.allPanels.length;
			}
			for(i=0;i<lenJPanels;i++)
				ClusterViewer.allPanels[i].repaint();
			
		}
		
	});	
	*/
//	main.selectClusterConfiguration.addActionListener(new ActionListener(){
//		
//		public void actionPerformed(ActionEvent e){
//			int selectedIndex = 1; //main.clusterConfigurationBox.getSelectedIndex();
//			if(selectedIndex == 0){
//				JOptionPane.showMessageDialog(main,"You must select a cluster group configuration before continuing","Select Targets!", JOptionPane.OK_OPTION);
//			}
//			else{
//				int answer = JOptionPane.showConfirmDialog(main, "Are you sure you would like to generate suggestions to move your\n" +
//						"document's features in the direction of the selectd clusters?","Confirm Choice",JOptionPane.YES_NO_OPTION);
//				if(answer ==0){
//					int trueIndex = selectedIndex -1;
//					Logger.logln("Cluster Group number '"+trueIndex+"' selected: "+stringRepresentation[selectedIndex]);
//					Logger.logln("Cluster Group chosen by Anonymouth: "+stringRepresentation[1]);
//					DataAnalyzer.selectedTargets = intRepresentation[trueIndex];
//					Logger.logln("INTREP: "+intRepresentation[trueIndex]);//added this.
//					EditorTabDriver.wizard.setSelectedTargets();
//					EditorTabDriver.signalTargetsSelected(main, true);
//					//main.mainJTabbedPane.getComponentAt(3).setEnabled(true);
//					//main.mainJTabbedPane.setSelectedIndex(3);
//				}
//			}
//			
//		}
//		
//	});

//	main.refreshButton.addActionListener(new ActionListener(){
//		public void actionPerformed(ActionEvent e){
//			for(int i=0;i<lenJPanels;i++)
//				ClusterViewer.allPanels[i].repaint();
//			
//		}
//		
//	});

//	main.reClusterAllButton.addActionListener(new ActionListener(){
//		
//		public void actionPerformed(ActionEvent e){
//			int sureness = JOptionPane.showConfirmDialog(main, "Are you sure you want to re-cluster all features?");
//			if (sureness == 0){
//				Logger.logln("Re-cluster requested...");
//				int i =0;
//				JPanel[] firstThreePanels = new JPanel[3];
//				for(i=0;i<3;i++)
//					firstThreePanels[i] = (JPanel) main.clusterHolderPanel.getComponent(i);
//				main.clusterHolderPanel.removeAll();
//				for(i=0;i<3;i++)
//					main.clusterHolderPanel.add(firstThreePanels[i]);
//				int maxClusters =EditorTabDriver.wizard.runAllTopFeatures();
//				EditorTabDriver.wizard.runClusterAnalysis(maxClusters);
//				initializeClusterViewer(main,false);
//				int[] theOne = intRepresentation[0];
//				ClusterViewer.selectedClustersByFeature = theOne;
//				lenJPanels = ClusterViewer.allPanels.length;
//				for(i=0;i<lenJPanels;i++)
//					ClusterViewer.allPanels[i].repaint();
//				Logger.logln("Re-cluster complete.");
//			}
//		}
//		
//	});
}