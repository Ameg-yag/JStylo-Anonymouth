package edu.drexel.psal.anonymouth.gooie;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Displays an enlarged, more detailed version of the results graph shown in the main window.
 * @author Marc Barrowclift
 *
 */
public class ResultsWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private GUIMain main;
	private BufferedImage chartImage;
	private JFreeChart chart;
	private BufferedImage panelImage;
	private JFreeChart panelChart;
	protected JPanel drawingPanel;
	private ArrayList<String> authors;
	private ArrayList<Integer> percent;
	private DefaultCategoryDataset dataSet;
	private int width;
	
	/**
	 * Constructor
	 * @param main - An instantace of GUIMain
	 */
	public ResultsWindow(GUIMain main) {
		super(main, "Process Results", Dialog.ModalityType.APPLICATION_MODAL);
		this.main = main;
		init();
		this.setVisible(false);
	}
	
	/**
	 * Displays the window
	 */
	public void openWindow() {
		this.setLocationRelativeTo(null); // makes it form in the center of the screen
		this.setVisible(true);
		this.repaint();
	}

	/**
	 * Should be called only by the resultsMainPanel to get the appropriately sized image for it to display.
	 * @param width - the width of the resultsMainPanel panel
	 * @param height - the height the developer is willing the scroll panel to have
	 * @return panelImage - A BufferedImage of the chart
	 */
	public BufferedImage getPanelChart(int width, int height) {
		if (panelImage == null) {
			panelChart = ChartFactory.createBarChart(
					null, null, null,
	                dataSet, PlotOrientation.HORIZONTAL, false, true, false);
			
			panelImage = panelChart.createBufferedImage(width, height);
		}
		
		return panelImage; 
	}
	
	/**
	 * Makes the data set and main chart from the data given. Must be called first before painting any windows or panels.
	 */
	public void makeChart() {
		dataSet = new DefaultCategoryDataset();
		
		for (int i = 0; i < authors.size(); i++)
			dataSet.setValue(percent.get(i).intValue(), "", authors.get(i));
		
		chart = ChartFactory.createBarChart(
				"Chance of Documents Ownership", "Authors", "Percent Chance",
                dataSet, PlotOrientation.VERTICAL, false, true, false);
		
		if (authors.size() < 10)
			width = 100 * authors.size();
		else
			width = 1000;
		
		this.setSize(width, 500);
		chartImage = chart.createBufferedImage(width, 478);
	}
	
	/**
	 * Initializes the data we need.
	 */
	@SuppressWarnings("serial")
	private void init() {
		authors = new ArrayList<String>();
		percent = new ArrayList<Integer>();
		
		drawingPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				g2d.drawImage(chartImage, 0, 0, null);
			}
		};
		
		this.setSize(500, 500);
		this.add(drawingPanel);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setTitle("Process Results");
	}
	
	/**
	 * Add an attribute to the data set
	 * @param author - The author name
	 * @param percentage - the author's percent chance of owning the test document
	 */
	public void addAttrib(String author, int percentage) {
		if (!authors.contains(author)) {
			authors.add(author);
			percent.add((Integer)percentage);
		}
	}
	
	/**
	 * Checks to see if the author and percentage data has been acquired and returns the appropriate boolean.
	 * @return
	 */
	public Boolean isReady() {
		if (authors.size() == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * Returns the author ArrayList size
	 * @return
	 */
	public int getAuthorSize() {
		return authors.size();
	}
	
	/**
	 * Resets all values and clears all graphs, to be used for re-processing
	 */
	public void reset() {
		authors.clear();
		percent.clear();
		main.resultsMainPanel.setPreferredSize(new Dimension(175, 110));
	}
}
