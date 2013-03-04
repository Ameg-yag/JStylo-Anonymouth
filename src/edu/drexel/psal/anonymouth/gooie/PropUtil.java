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

public class PropUtil
{
	protected static GUIMain main = GUIMain.inst;
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
	 * Sets the previous problem set path, so the user doesn't need to go searching for it.
	 * @param path - path of the file in string form
	 */
	protected static void setProbSetPath(String path)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			main.prop.setProperty("recentProbSet", path);
			writer = new BufferedWriter(new FileWriter(main.propFileName));
			main.prop.store(writer, "User Preferences");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			main.prop.setProperty("clustersTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(main.propFileName));
			main.prop.store(writer, "User Preferences");
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
			location = main.prop.getProperty("clustersTabLocation");
		} catch (NullPointerException e) {
			location = "top"; // default
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
			main.prop.setProperty("preProcessTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(main.propFileName));
			main.prop.store(writer, "User Preferences");
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
			location = main.prop.getProperty("preProcessTabLocation");
		} catch (NullPointerException e) {
			location = "left"; // default
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
			main.prop.setProperty("suggestionsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(main.propFileName));
			main.prop.store(writer, "User Preferences");
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
			location = main.prop.getProperty("suggestionsTabLocation");
		} catch (NullPointerException e) {
			location = "left"; // default
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
			main.prop.setProperty("translationsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(main.propFileName));
			main.prop.store(writer, "User Preferences");
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
			location = main.prop.getProperty("translationsTabLocation");
		} catch (NullPointerException e) {
			location = "left"; // default
		}
		return stringToLocation(location);
	}
}