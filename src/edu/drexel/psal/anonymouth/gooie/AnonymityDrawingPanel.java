package edu.drexel.psal.anonymouth.gooie;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.Cluster;

import net.miginfocom.swing.MigLayout;

public class AnonymityDrawingPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final int MINY = 50;
	private final int MAXY = 700;
	private final String[] PERCENTTEXT = {"100%", "75%", "50%", "25%", "0%"};
	private JLabel anonymous;
	private JLabel notAnonymous;
	private Boolean showPointer;
	private Pointer pointer;
	
	//Pointer to show the user how anonymous their document is on the scale.
	class Pointer {
		private final int X = 50;
		private int y;
		private int maxPercent;
		private int curPercent;
		private int[] triX;
		private int[] triY;
		private double ratio;
		
		//constructor
		public Pointer() {
			y = MAXY + MINY;
			triX = new int[3];
			triY = new int[3];
			
			setMaxPercentage(100); //default percentage is 100%
			setPercentage(50); //default percentage is 50%
		}
		
		public void setValue() {
			this.y = (int)(getRatio() * (MAXY + MINY));
		}
		
		public int getY() {
			return y;
		}
		
		public int getX() {
			return X;
		}
		
		public void setPercentage(int perc) {
			if (perc >= 0 && perc < getMaxPercentage()) {
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
		
		public void setRatio(int curPercent, int maxPercent) {
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
		
		showPointer = true;
		
		anonymous.setFont(new Font("Helvatica", Font.BOLD, 16));
		notAnonymous.setFont(new Font("Helvatica", Font.BOLD, 16));
		this.add(anonymous, "pos 68 15");
		this.add(notAnonymous, "pos 52 715");
		
		pointer = new Pointer();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(7f));
		
		//Paints the top and bottom lines
		g2d.setColor(Color.GREEN);
		g2d.drawLine((232 / 2) - 20 + 3, 50, (232 / 2) + 20, 50);
		g2d.setColor(Color.RED);
		g2d.drawLine((232 / 2) - 20 + 3, 700, (232 / 2) + 20, 700);
		
		Color startingColor = Color.GREEN;
		Color endingColor = Color.RED;
		
		//Drawing gradient "intensity" line
		for (int y = 0; y < 647; y++) {
			float ratio = (float) y / (float) 647;
			int red = (int)(endingColor.getRed() * ratio + startingColor.getRed() * (1 - ratio));
			int green = (int)(endingColor.getGreen() * ratio + startingColor.getGreen() * (1 - ratio));
			int blue = (int)(endingColor.getBlue() * ratio + startingColor.getBlue() * (1 - ratio));
			
			Color stepColor = new Color(red, green, blue);
			g2d.setColor(stepColor);
			g2d.drawLine((232 / 2) + 2, y + 53, (232 / 2) + 2, y + 53);
		}
		
		//Drawing the pointer
		if (showPointer) {
			g2d.setColor(Color.BLACK);
//			for (int i = 0; i < 3; i++) {
//				System.out.printf("[%s, %s]\n", pointer.getTriX()[i], pointer.getTriY()[i]);
//			}
			g2d.drawPolygon(pointer.getTriX(), pointer.getTriY(), 3);
			g2d.drawLine(pointer.getX(), pointer.getY(), pointer.getX() + 20, pointer.getY());
//			g2d.setColor(Color.WHITE);
//			g2d.drawLine(pointer.getX() - 10, pointer.getY(), pointer.getX(), pointer.getY());
		}
		
		//Drawing Percentages
		for (int i = 0 ; i < PERCENTTEXT.length; i++) {
			g2d.drawString(PERCENTTEXT[i], 150, MINY + 10 + (((MAXY + MINY * 2) / PERCENTTEXT.length) * i));
		}
	}
	
	public void showPointer(Boolean show) {
		showPointer = show;
	}
}
