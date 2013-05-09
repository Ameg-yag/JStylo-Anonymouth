package edu.drexel.psal.anonymouth.gooie;

import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ResultsWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private BufferedImage chartImage;
	private JFreeChart chart;
	private BufferedImage panelImage;
	private JFreeChart panelChart;
	protected JPanel drawingPanel;
	private ArrayList<String> authors;
	private ArrayList<Integer> percent;
	private DefaultCategoryDataset dataSet;
	private int width;
	
	public ResultsWindow(GUIMain main) {
		super(main, "Process Results", Dialog.ModalityType.APPLICATION_MODAL);
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
	
	public BufferedImage getPanelChart(int width, int height) {
		if (panelImage == null) {
			panelChart = ChartFactory.createBarChart(
					null, null, null,
	                dataSet, PlotOrientation.HORIZONTAL, false, true, false);
			
			panelImage = panelChart.createBufferedImage(width, height);
		}
		
		return panelImage; 
	}
	
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
	
	public int getAuthorSize() {
		return authors.size();
	}
}
