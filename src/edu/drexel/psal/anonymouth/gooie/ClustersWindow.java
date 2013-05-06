package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

/**
 * A modified version of the existing ClusterPanel such that it will be removed from the main window and relocated to it's own window
 * accessed by the pull-down menu.
 * @author Marc Barrowclift
 */
public class ClustersWindow extends JFrame {

	private final String NAME = "( "+this.getClass().getSimpleName()+" ) - ";
	private static final long serialVersionUID = 1L;
	protected ScrollablePanel clusterHolderPanel;
	private JPanel clustersPanel;
	private JScrollPane clusterScrollPane;
	private JLabel clustersLabel;
	private JLabel legendLabel;
	private JPanel legendPanel;
	private JPanel featuresPanel;
	private DefaultListModel featuresListModel;
	protected JList featuresList;
	private JScrollPane featuresListScrollPane;
	protected DefaultListModel subFeaturesListModel;
	protected JList subFeaturesList;
	protected JScrollPane subFeaturesListScrollPane;
	private GUIMain main;
	
	protected ArrayList<ArrayList<String>> subfeatures = new ArrayList<ArrayList<String>>();
	protected ArrayList<String> features = new ArrayList<String>();
	
	public ClustersWindow() {
		init();
		this.setVisible(false);
	}
	
	/**
	 * initializes all the data needed to display the clusters
	 */
	private void init() {
		PropertiesUtil.Location location = PropertiesUtil.getClustersTabLocation();
		clustersPanel = new JPanel();
		clustersPanel.setLayout(new MigLayout(
				"wrap, ins 0",
				"grow, fill",
				"0[]0[grow, fill][]0"));
		
		{ // --------------cluster panel components
			clustersLabel = new JLabel("Clusters:");
			clustersLabel.setHorizontalAlignment(SwingConstants.CENTER);
			clustersLabel.setFont(main.titleFont);
			clustersLabel.setOpaque(true);
			clustersLabel.setBackground(new Color(252,242,206));
			clustersLabel.setBorder(main.rlborder);
			
			clusterHolderPanel = new ScrollablePanel()
			{
				public boolean getScrollableTracksViewportWidth()
				{
					return true;
				}
			};
			clusterHolderPanel.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PIXELS, 74);
			clusterHolderPanel.setAutoscrolls(true);
			clusterHolderPanel.setOpaque(true);
			BoxLayout clusterHolderPanelLayout = new BoxLayout(clusterHolderPanel, javax.swing.BoxLayout.Y_AXIS);
			clusterHolderPanel.setLayout(clusterHolderPanelLayout);
			clusterScrollPane = new JScrollPane(clusterHolderPanel);
			clusterScrollPane.setOpaque(true);
			
//			legendLabel = new JLabel("Legend:");
//			legendLabel.setHorizontalAlignment(SwingConstants.CENTER);
//			legendLabel.setFont(main.titleFont);
//			legendLabel.setOpaque(true);
//			legendLabel.setBackground(new Color(252,242,206));
//			legendLabel.setBorder(main.rlborder);
//			
//			legendPanel = new JPanel();
//			legendPanel.setLayout(new MigLayout(
//					"wrap 2",
//					"20[][100]",
//					"grow, fill"));
//			
//			{ // --------------------legend panel components
//				JLabel presentValueLabel = new JLabel("Present Value:");
//				
//				JPanel presentValuePanel = new JPanel();
//				presentValuePanel.setBackground(Color.black);
//				
//				JLabel normalRangeLabel = new JLabel("Normal Range:");
//				
//				JPanel normalRangePanel = new JPanel();
//				normalRangePanel.setBackground(Color.red);
//				
//				JLabel safeZoneLabel = new JLabel("Safe Zone:");
//				
//				JPanel safeZonePanel = new JPanel();
//				safeZonePanel.setBackground(Color.green);
//				
//				legendPanel.add(presentValueLabel, "grow");
//				legendPanel.add(presentValuePanel, "grow");
//				legendPanel.add(normalRangeLabel, "grow");
//				legendPanel.add(normalRangePanel, "grow");
//				legendPanel.add(safeZoneLabel, "grow");
//				legendPanel.add(safeZonePanel, "grow");
//			}
			
//			featuresPanel = new JPanel();
//				featuresPanel.setLayout(new MigLayout(
//						"wrap, fill, ins 0, gap 0 0",
//						"grow, fill",
//						"[][grow, fill][][grow, fill]"));
//			{ // --------------------legend panel components
//				JLabel featuresLabel = new JLabel("Feature Search:");
//				featuresLabel.setHorizontalAlignment(SwingConstants.CENTER);
//				featuresLabel.setFont(main.titleFont);
//				featuresLabel.setOpaque(true);
//				featuresLabel.setBackground(new Color(252,242,206));
//				featuresLabel.setBorder(main.rlborder);
//				
//				featuresListModel = new DefaultListModel();
//				featuresList = new JList(featuresListModel);
//				featuresListScrollPane = new JScrollPane(featuresList);
//				
//				JLabel subFeaturesLabel = new JLabel("Sub-Feature Search:");
//				subFeaturesLabel.setHorizontalAlignment(SwingConstants.CENTER);
//				subFeaturesLabel.setFont(main.titleFont);
//				subFeaturesLabel.setOpaque(true);
//				subFeaturesLabel.setBackground(new Color(252,242,206));
//				subFeaturesLabel.setBorder(main.rlborder);
//				
//				subFeaturesListModel = new DefaultListModel();
//				subFeaturesList = new JList(subFeaturesListModel);
//				subFeaturesList.setEnabled(false);
//				subFeaturesListScrollPane = new JScrollPane(subFeaturesList);
//				
//				featuresPanel.add(featuresLabel, "grow, h " + main.titleHeight + "!");
//				featuresPanel.add(featuresListScrollPane, "grow");
//				featuresPanel.add(subFeaturesLabel, "grow, h " + main.titleHeight + "!");
//				featuresPanel.add(subFeaturesListScrollPane, "grow");
//			}

//			clustersPanel.add(clustersLabel);
//			clustersPanel.add(clusterScrollPane);
//			clustersPanel.add(featuresPanel, "h 250!");
			
			clustersPanel.add(clusterScrollPane, "growy");
		}
		
		this.add(clustersPanel);
		this.setSize(400, 860);
		this.setLocationRelativeTo(null);
		this.setTitle("Clusters Viewer");
	}
	
	/**
	 * Displays the window
	 */
	public void openWindow() {
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
		this.setVisible(true);
	}
	
	public void addClusterFeatures (String[] names) {
//		Arrays.sort(names);
//		// add the holder at top
//		subfeatures.add(new ArrayList<String>());
//		for (int i = 0; i < names.length; i++)
//		{
//			String feature = null;
//			String subfeature = null;
//			
//			// get the feature and subfeature from the name
//			if (names[i].contains("--"))
//			{
//				feature = names[i].substring(0, names[i].indexOf("--"));
//				subfeature = names[i].substring(names[i].indexOf("--")+2, names[i].length());
//			}
//			else
//				feature = names[i];
//			
//			// if the feature doesnt exist yet, add it to the feature list
//			if (!features.contains(feature))
//			{
//				features.add(feature);
//				subfeatures.add(new ArrayList<String>());
//				if (subfeature != null)
//					subfeatures.get(features.indexOf(feature)).add(subfeature);
//					
//			}
//			else // if the feature does exist, add its subfeature to the subfeature list
//			{
//				if (subfeature != null)
//					subfeatures.get(features.indexOf(feature)).add(subfeature);
//			}
//		}
//		featuresListModel = new DefaultListModel();
//		for (int i = 0; i < features.size(); i++)
//			featuresListModel.addElement(features.get(i));
//		featuresList.setModel(featuresListModel);
	}
}
