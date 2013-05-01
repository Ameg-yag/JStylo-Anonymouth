package edu.drexel.psal.anonymouth.gooie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.drexel.psal.JSANConstants;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;
import edu.drexel.psal.jstylo.eventDrivers.*;
import edu.drexel.psal.jstylo.generics.*;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;
import edu.drexel.psal.jstylo.GUI.DocsTabDriver.ExtFilter;
import edu.drexel.psal.jstylo.canonicizers.*;

import com.jgaap.canonicizers.*;
import com.jgaap.generics.*;

public class DriverMenu {
	
	private final static String NAME = "( DriverMenu ) - ";

	protected static ActionListener generalListener;
	protected static ActionListener saveProblemSetListener;
	protected static ActionListener loadProblemSetListener;
	protected static ActionListener saveTestDocListener;
	protected static ActionListener saveAsTestDocListener;
	protected static ActionListener aboutListener;
//	protected static ActionListener printMenuItemListener;
	
	protected static void initListeners(final GUIMain main)
	{
		generalListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				main.GSP.openWindow();
			}
        };
        main.settingsGeneralMenuItem.addActionListener(generalListener);
        
        saveProblemSetListener = new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		DriverPreProcessTabDocuments.saveProblemSetAL.actionPerformed(e);
        	}
        };
        main.fileSaveProblemSetMenuItem.addActionListener(saveProblemSetListener);
        
        loadProblemSetListener = new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		DriverPreProcessTabDocuments.loadProblemSetAL.actionPerformed(e);
        	}
        };
        main.fileLoadProblemSetMenuItem.addActionListener(loadProblemSetListener);
        
        saveTestDocListener = new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		DriverDocumentsTab.save(main);
        	}
        };
        main.fileSaveTestDocMenuItem.addActionListener(saveTestDocListener);

        saveAsTestDocListener = new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		DriverDocumentsTab.saveAsTestDoc.actionPerformed(e);
        	}
        };
        main.fileSaveAsTestDocMenuItem.addActionListener(saveAsTestDocListener);
        
        aboutListener = new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		JOptionPane.showMessageDialog(null, 
						"Anonymouth\nVersion 0.0.3\nAuthor: Andrew W.E. McDonald\nDrexel University, PSAL, Dr. Rachel Greenstadt - P.I.",
						"About Anonymouth",
						JOptionPane.INFORMATION_MESSAGE,
						ThePresident.LOGO);
        	}
        };
        main.helpAboutMenuItem.addActionListener(aboutListener);
        
        /*
        printMenuItemListener = new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		PrinterJob job = PrinterJob.getPrinterJob();
        		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        		PageFormat pf = job.pageDialog(aset);
        		job.setPrintable(new PrintDialogExample(), pf);
        		boolean ok = job.printDialog(aset);
        		
        	}
        };
        main.filePrintMenuItem.addActionListener(printMenuItemListener);
        */
	}
}