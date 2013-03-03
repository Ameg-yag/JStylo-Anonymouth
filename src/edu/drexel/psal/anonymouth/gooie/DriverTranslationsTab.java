package edu.drexel.psal.anonymouth.gooie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

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

public class DriverTranslationsTab
{
	private static ListSelectionListener selListener;
	
	protected static void initListeners(final GUIMain main)
	{
		 selListener = new ListSelectionListener()
         {
         	public void valueChanged(ListSelectionEvent e) 
         	{
         		int row = main.translationsTable.getSelectedRow();
 				String sentence = ConsolidationStation.toModifyTaggedDocs.get(0).getCurrentSentence().getTranslations().get(row).getUntagged();
 				main.translationEditPane.setText(sentence);
             }
         };
         main.translationsTable.getSelectionModel().addListSelectionListener(selListener);
	}
}