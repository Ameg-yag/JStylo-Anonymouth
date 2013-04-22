package edu.drexel.psal.anonymouth.gooie;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.jstylo.generics.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.Application;

/**
 * ThePresident sets up the Application and System fields/preferences prior to calling 'GUIMain'
 * @author Andrew W.E. McDonald
 *
 */
public class ThePresident {

	//protected static ImageIcon buffImg;
	private final String NAME = "( "+this.getClass().getSimpleName()+" ) - ";
	public static ImageIcon LOGO;
	public static String sessionName;
	public static final String DOC_MAGICIAN_WRITE_DIR = "./.edited_documents/";
	public static final String LOG_DIR = "log";
	public static String TEMP_DIR =  "temp/"; // TODO: put in "options"
	public static String SER_DIR = "./.serialized_objects/";
	public static String GRAMMAR_DIR = "grammar_data/";//TODO: put in "options"
	//public static boolean SHOULD_KEEP_TEMP_CLEAN_DOCS = false; // TODO : put in "options" XXX not used!!
	public static boolean SHOULD_KEEP_AUTO_SAVED_ANONYMIZED_DOCS = true; // TODO: put in "options"
	public static boolean SAVE_TAGGED_DOCUMENTS = true; // TODO: put in "options
	public static int MAX_FEATURES_TO_CONSIDER = 1000; // todo: put in 'options', and figure out an optimal number (maybe based upon info gain, total #, etc.)... basically, when the processing time outweighs the benefit, that should be our cutoff.

	/*
	public void getDockImage(String name){
		try{
			buffImg = new ImageIcon(getClass().getResource(name));
		} catch (Exception e){
			e.printStackTrace();
			//System.exit(5);
		}
	}
	*/

	public void getLogo(String name){
		try{
			LOGO = new ImageIcon(getClass().getResource(name), "Anonymouth's Logo");
		} catch (Exception e){
			e.printStackTrace();
			//System.exit(6);
		}
	}


	public static void main(String[] args){
		String OS = System.getProperty("os.name").toLowerCase();
		ThePresident leader = new ThePresident();
		if(OS.contains("mac")){
			Logger.logln(leader.NAME+"We're on a Mac!");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name","Anonymouth");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			Application app = Application.getApplication();
			String logoName = JSANConstants.JSAN_GRAPHICS_PREFIX+"Anonymouth_LOGO.png";
			try{
				leader.getLogo(logoName);
				app.setDockIconImage(LOGO.getImage());
			}catch(Exception e){
				Logger.logln("Error loading logos");
			}
			/*
			JMenuBar menuBar = new JMenuBar();
			int numMenus = 3;
			JMenu[] menu = new JMenu[numMenus];
			menu[0] = new JMenu();
			menu[0].setText("File");
			menu[1] = new JMenu();
			menu[1].setText("Edit");
			menu[2] = new JMenu();
			menu[2].setText("Help");
			int i;
			for(i=0;i<numMenus;i++)
				menuBar.add(menu[i]);
			app.setDefaultMenuBar(menuBar);
			 */
			app.setAboutHandler(new AboutHandler(){
				public void handleAbout(AboutEvent e){
					JOptionPane.showMessageDialog(null, 
							"Anonymouth\nVersion 0.0.3\nAuthor: Andrew W.E. McDonald\nDrexel University, PSAL, Dr. Rachel Greenstadt - P.I.",
							"About Anonymouth",
							JOptionPane.INFORMATION_MESSAGE,
							LOGO);

				}
			});

			app.requestForeground(true);
		}
		sessionName = "anonymous"; 
//		String tempName = null;
//		tempName = JOptionPane.showInputDialog("Please name your session:", sessionName);
//		if(tempName == null)
//			System.exit(665);
//			
//		tempName = tempName.replaceAll("['.?!()<>#\\\\/|\\[\\]{}*\":;`~&^%$@+=,]", "");
//		tempName = tempName.replaceAll(" ", "_");
//		if(tempName != null)
//			sessionName = tempName;
		//System.out.println(tempName+" "+sessionName);
		File log_dir = new File(LOG_DIR); // create log directory if it doesn't exist.
		if (!log_dir.exists()){
			System.out.println(leader.NAME+"Creating directory for DocumentMagician to write to...");
			log_dir.mkdir();
		}
		Logger.setFilePrefix("Anonymouth_"+sessionName);
		Logger.logFile = true;	
		Logger.initLogFile();
		File dm_write_dir = new File(DOC_MAGICIAN_WRITE_DIR);
		if (!dm_write_dir.exists()){
			Logger.logln(leader.NAME+"Creating directory for DocumentMagician to write to...");
			dm_write_dir.mkdir();
		}
		File ser_dir = new File(SER_DIR);
		if (!ser_dir.exists()){
			Logger.logln(leader.NAME+"Creating directory to save serialized objects to...");
			ser_dir.mkdir();
		}
		
		Logger.logln("Gooie starting...");
		GUIMain.startGooie();
	}
}
