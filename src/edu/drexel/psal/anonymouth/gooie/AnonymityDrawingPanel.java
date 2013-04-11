package edu.drexel.psal.anonymouth.gooie;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.drexel.psal.anonymouth.utils.ConsolidationStation;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * Paints the information gathered from "ConsolidationStation.toModifyTaggedDocs.get(0).getAvgPercentChangeNeeded(false)"
 * into a nice "intensity" bar for the user to see how anonymous their document is (and therefore how much of it needs to
 * be changed)
 * 
 * Will be added as part of the "Anonymity" tab on the left-hand side of the main Anonymouth window
 * @author Marc Barrowclift
 */
public class AnonymityDrawingPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final int MINY = 50;
//	private final int MAXY = 700;
	private final int MAXY = 470;
	private final String[] PERCENTTEXT = {"100%", "75%", "50%", "25%", "0%"};
	private JLabel anonymous;
	private JLabel notAnonymous;
	private Boolean showPointer;
	private static Pointer pointer;
	
	//Pointer to show the user how anonymous their document is on the scale.
	/**
	 * Manages all the data associated with the intensity bar pointer, which is simply a little arrow that will point
	 * to where the user's test document stands on the not anonymous <-> scale
	 */
	class Pointer {
		private final int X = 50;
		private int y;
		private int maxPercent;
		private int curPercent;
		private int[] triX;
		private int[] triY;
		private double ratio;
		
		public Pointer() {
			y = MAXY + MINY;
			triX = new int[3];
			triY = new int[3];
			
			setMaxPercentage(100); //default percentage is 100%
			setPercentage(50); //default percentage is 50%
		}
		
		/**
		 * Calculates what the arrow's new y position should be, should not be called by other methods.
		 * If the y value needs to be changed, call "setPercentage()" instead
		 */
		private void setValue() {
			this.y = (int)(MAXY * getRatio() + MINY * getRatio() + getMaxPercentage() * (.5 - getRatio()));
		}
		
		public int getY() {
			return y;
		}
		
		public int getX() {
			return X;
		}
		
		/**
		 * Sets the new anonymity percentage, the panel must be repainted for changes to be seen.
		 * @param perc must be integer representation of percentage (e.g., 50 for 50% instead of .5)
		 */
		public void setPercentage(int perc) {
			if (perc >= 0 && perc <= getMaxPercentage()) {
				curPercent = perc;
				setRatio(getPercentage(), getMaxPercentage());
				setValue();
				updateTriangle();
			}
		}
		
		public int getPercentage() {
			return curPercent;
		}
		
		public void setMaxPercentage(int perc) {
			if (perc >= 0) {
				maxPercent = perc;
			}
		}
		
		public int getMaxPercentage() {
			return maxPercent;
		}
		
		private void setRatio(int curPercent, int maxPercent) {
			ratio = (double)getPercentage() / (double)getMaxPercentage();
		}
		
		public double getRatio() {
			return ratio;
		}
		
		public int[] getTriX() {
			return triX;
		}
		
		public int[] getTriY() {
			return triY;
		}
		
		/**
		 * Updates and moves the triangle part of the arrow
		 */
		public void updateTriangle() {
			triX = new int[3];
			triY = new int[3];
			
			triX[0] = getX() + 30;
			triX[1] = getX() + 25;
			triX[2] = getX() + 25;
			
			triY[0] = getY();
			triY[1] = getY() - 5;
			triY[2] = getY() + 5;
		}
	}
	
	public AnonymityDrawingPanel() {
		this.setLayout(new MigLayout());
		
		anonymous = new JLabel("Anonymous");
		notAnonymous = new JLabel("Not Anonymous");
		
		showPointer = false;
		
		anonymous.setFont(new Font("Helvatica", Font.BOLD, 16));
		notAnonymous.setFont(new Font("Helvatica", Font.BOLD, 16));
		this.add(anonymous, "pos 68 15");
		this.add(notAnonymous, "pos 52 485");
		
		pointer = new Pointer();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(7f));
		
		//Paints the top and bottom lines
		g2d.setColor(Color.GREEN);
		g2d.drawLine((232 / 2) - 20 + 3, MINY, (232 / 2) + 20, MINY);
		g2d.setColor(Color.RED);
		g2d.drawLine((232 / 2) - 20 + 3, MAXY, (232 / 2) + 20, MAXY);
		
		Color startingColor = Color.GREEN;
		Color endingColor = Color.RED;
		
		//Drawing gradient "intensity" line 647
		for (int y = 0; y < MAXY - 53; y++) {
			float ratio = (float) y / (float) (MAXY - 53);
			int red = (int)(endingColor.getRed() * ratio + startingColor.getRed() * (1 - ratio));
			int green = (int)(endingColor.getGreen() * ratio + startingColor.getGreen() * (1 - ratio));
			int blue = (int)(endingColor.getBlue() * ratio + startingColor.getBlue() * (1 - ratio));
			
			Color stepColor = new Color(red, green, blue);
			g2d.setColor(stepColor);
			g2d.drawLine((232 / 2) + 2, y + 53, (232 / 2) + 2, y + 53);
		}
		
		g2d.setColor(Color.BLACK);
		
		//Drawing the pointer
		if (showPointer) {
			g2d.drawPolygon(pointer.getTriX(), pointer.getTriY(), 3);
			g2d.drawLine(pointer.getX(), pointer.getY(), pointer.getX() + 20, pointer.getY());
		}
		
		Font percFont = new Font("Helvatica", Font.PLAIN, 14);
		g2d.setFont(percFont);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		//Drawing Percentages
		for (int i = 0 ; i < PERCENTTEXT.length; i++) {
			g2d.drawString(PERCENTTEXT[i], 150, MINY + 8 + (((MAXY + MINY * 2) / PERCENTTEXT.length) * i - (i * 10)));
		}
	}
	
	public void showPointer(Boolean show) {
		showPointer = show;
		repaint();
	}
	
	/**
	 * This should be called whenever there have been changes to the test document (or the test document's being processed
	 * for the first time) so that the arrow may move accordingly
	 */
	public void updateAnonymityBar() {
		System.out.println((int)(ConsolidationStation.toModifyTaggedDocs.get(0).getAvgPercentChangeNeeded(false) + .5));
		pointer.setPercentage((int)(ConsolidationStation.toModifyTaggedDocs.get(0).getAvgPercentChangeNeeded(false) + .5));
		repaint();
	}
	
	/**
	 * Created for a quick and easy way to get the percent that the pointer uses for use in the text description below the bar
	 */
	public int getAvgPercentChangeNeeded() {
		return pointer.getPercentage();
	}
}
