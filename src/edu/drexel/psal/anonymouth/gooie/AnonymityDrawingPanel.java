package edu.drexel.psal.anonymouth.gooie;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.drexel.psal.JSANConstants;
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
	private final int MAXY = 470; //700 without results part
	private final String[] PERCENTTEXT = {"100%", "75%", "50%", "25%", "0%"};
	private JLabel anonymous;
	private JLabel notAnonymous;
	private Boolean showPointer;
	private static Pointer pointer;
	protected Image bar;
	protected Image barFull;
	protected Image barEmpty;
	
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
			
			setPercentages(50, 100); //default percentage is 50% out of 100%
		}
		
		/**
		 * Calculates what the arrow's new y position should be, should not be called by other methods.
		 * If the y value needs to be changed, call "setPercentage()" instead
		 */
		private void setValue() {
//			System.out.println("   " + getRatio());
			this.y = (int)(MAXY * getRatio() + MINY * getRatio() + getMaxPercentage() * (.5 - getRatio()));
//			this.y = (int)(MAXY * getRatio() + MINY * getRatio());
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
		public void setPercentages(int percentage, int maxPercentage) {
			System.out.println("DEBUG: Before, percentage = " + percentage + " and maxPercentage = " + maxPercentage);
			if (maxPercentage >= 0 && percentage >= 0 && percentage <= maxPercentage) {
				System.out.println("DEBUG: HELLO!!! " + percentage + " / " + maxPercentage);
				setRatio(percentage, maxPercentage);
				System.out.println("DEBUG: HELLO!!! " + getRatio());
				curPercent = (int)(getRatio() * 100);
				maxPercent = 100;
				
				setValue();
				updateTriangle();
			}
			
			System.out.println("DEBUG: max percentage = " + getMaxPercentage());
			System.out.println("DEBUG: percentage = " + getPercentage());
			System.out.println("DEBUG: y = " + getY());
			System.out.println("DEBUG: ratio = " + getRatio());
		}
		
		public int getPercentage() {
			return curPercent;
		}
		
		public int getMaxPercentage() {
			return maxPercent;
		}
		
		private void setRatio(int percentage, int maxPercentage) {
			ratio = (double)percentage / (double)maxPercentage;
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
		
		try {
			bar = ImageIO.read(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"bar.png"));
			barFull = ImageIO.read(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"barFull.png"));
			barEmpty = ImageIO.read(getClass().getResource(JSANConstants.JSAN_GRAPHICS_PREFIX+"barEmpty.png"));
		} catch (Exception e) {
			System.err.println("Error loading anonymity bar pictures (See AnonymityDrawingPanel.java)");
			e.printStackTrace();
		}
		
		pointer = new Pointer();
		showPointer = true;
		pointer.setPercentages(75, 100);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(1f));
		
		if (!showPointer)
			g2d.drawImage(barEmpty, (232 / 2) - 50 + 3, MINY-5, null);
		else if (pointer.getPercentage() >= 99)
			g2d.drawImage(barFull, (232 / 2) - 50 + 3, MINY-5, null);
		else
			g2d.drawImage(bar, (232 / 2) - 50 + 3, MINY-5, null);
		
		Color startingColor = Color.GREEN;
		Color endingColor = Color.RED;
		
		//Drawing gradient "intensity" line 647
		if (showPointer) {
			for (int y = MAXY; y > 58; y--) {
				float ratio = (float) (y - 58) / (float) (MAXY - 58);
				int red = (int)(endingColor.getRed() * ratio + startingColor.getRed() * (1 - ratio));
				int green = (int)(endingColor.getGreen() * ratio + startingColor.getGreen() * (1 - ratio));
				int blue = (int)(endingColor.getBlue() * ratio + startingColor.getBlue() * (1 - ratio));
				
				Color stepColor = new Color(red, green, blue);
				g2d.setColor(stepColor);

				if (y <= MAXY - pointer.getY() + 50)
					break;
				else
					g2d.drawLine((232 / 2) - 32, y, (232 / 2) - 20, y);
			}
		}
		
		g2d.setColor(Color.BLACK);
		
		Font percFont = new Font("Helvatica", Font.PLAIN, 14);
		g2d.setFont(percFont);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		//Drawing Percentages
		for (int i = 0 ; i < PERCENTTEXT.length; i++) {
			g2d.drawString(PERCENTTEXT[i], 130, MINY + 8 + (((MAXY + MINY * 2) / PERCENTTEXT.length) * i - (i * 9)));
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
		pointer.setPercentages((int)(DriverDocumentsTab.taggedDoc.getAnonymityIndex() + .5), (int)(DriverDocumentsTab.taggedDoc.getTargetAnonymityIndex() + .5));
		System.out.println("TargetAnonymityIndex: " + (int)(DriverDocumentsTab.taggedDoc.getTargetAnonymityIndex() + .5));
		System.out.println("AnonymityIndex: " + (int)(DriverDocumentsTab.taggedDoc.getAnonymityIndex() + .5));
		repaint();
	}
	
	/**
	 * Created for a quick and easy way to get the percent that the pointer uses for use in the text description below the bar
	 */
	public int getAvgPercentChangeNeeded() {
		return 100 - pointer.getPercentage();
	}
}
