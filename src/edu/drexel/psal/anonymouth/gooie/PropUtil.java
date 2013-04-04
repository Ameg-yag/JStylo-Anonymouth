package edu.drexel.psal.anonymouth.gooie;

import java.awt.Point;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jgaap.generics.Document;

import edu.drexel.psal.jstylo.generics.Logger.LogOut;
import edu.drexel.psal.jstylo.generics.*;

public class PropUtil {
	
	private final String NAME = "( "+this.getClass().getName()+" ) - ";

	protected static String propFileName = "jsan_resources/anonymouth_prop.prop";
	protected static File propFile = new File(propFileName);
	protected static Properties prop = new Properties();
	protected static JFileChooser load = new JFileChooser();
	protected static JFileChooser save = new JFileChooser();
	private static String[] DEFAULT_LOCATIONS = new String[]{"top","left","right","bottom"};
	
	public static enum Location // just so you cant mess up the input to methods by spelling stuff wrong
	{
		LEFT("left"), TOP("top"), RIGHT("right"), BOTTOM("bottom"), NONE("none");
		
		public String strRep;
		Location(String rep)
		{
			strRep = rep;
		}
	}
	
	protected static Location stringToLocation(String loc)
	{
		switch (loc)
		{
		case "left":
			return Location.LEFT;
		case "top":
			return Location.TOP;
		case "right":
			return Location.RIGHT;
		case "bottom":
			return Location.BOTTOM;
		case "none":
			return Location.NONE;
		}
		return null;
	}
	
	protected static String locationToString(Location loc)
	{
		switch (loc)
		{
		case LEFT:
			return "left";
		case TOP:
			return "top";
		case RIGHT:
			return "right";
		case BOTTOM:
			return "bottom";
		case NONE:
			return "none";
		}
		return null;
	}
	
	/**
	 * Sets the previous problem set filename, so the user doesn't need to go searching for it. Must be in jsan_resources/problem_sets.
	 * @param filename - name of the file in string form (e.g. ps.xml)
	 */
	protected static void setRecentProbSet(String filename)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("recentProbSet", filename);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the location of the documents tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setDocumentsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("documentsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getDocumentsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("documentsTabLocation");
			if (location == null)
			{
				prop.setProperty("documentsTabLocation", DEFAULT_LOCATIONS[0]); // top
				location = prop.getProperty("documentsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("documentsTabLocation", DEFAULT_LOCATIONS[0]);
			location = prop.getProperty("documentsTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the clusters tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setResultsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("resultsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getResultsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("resultsTabLocation");
			if (location == null)
			{
				prop.setProperty("resultsTabLocation", DEFAULT_LOCATIONS[1]);
				location = prop.getProperty("resultsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("resultsTabLocation", DEFAULT_LOCATIONS[1]);
			location = prop.getProperty("resultsTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the clusters tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setClustersTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("clustersTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getClustersTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("clustersTabLocation");
			if (location == null)
			{
				prop.setProperty("clustersTabLocation", DEFAULT_LOCATIONS[1]); // left
				location = prop.getProperty("clustersTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("clustersTabLocation", DEFAULT_LOCATIONS[1]);
			location = prop.getProperty("clustersTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the preprocess tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setPreProcessTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("preProcessTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getPreProcessTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("preProcessTabLocation");
			if (location == null)
			{
				prop.setProperty("preProcessTabLocation", DEFAULT_LOCATIONS[2]); // right
				location = prop.getProperty("preProcessTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("preProcessTabLocation", DEFAULT_LOCATIONS[2]);
			location = prop.getProperty("preProcessTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the suggestions tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setSuggestionsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("suggestionsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getSuggestionsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("suggestionsTabLocation");
			if (location == null)
			{
				prop.setProperty("suggestionsTabLocation", DEFAULT_LOCATIONS[2]); // right
				location = prop.getProperty("suggestionsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("suggestionsTabLocation", DEFAULT_LOCATIONS[2]);
			location = prop.getProperty("preProcessTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the translations tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setTranslationsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("translationsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getTranslationsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("translationsTabLocation");
			if (location == null)
			{
				prop.setProperty("translationsTabLocation", DEFAULT_LOCATIONS[2]); // right
				location = prop.getProperty("translationsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("translationsTabLocation", DEFAULT_LOCATIONS[2]);
			location = prop.getProperty("translationsTabLocation");
		}
		return stringToLocation(location);
	}
}