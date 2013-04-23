package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.util.Date;
import com.memetix.mst.language.Language;
import java.util.ArrayList;
//import edu.drexel.psal.anonymouth.gooie.EditorTabDriver;

import edu.drexel.psal.anonymouth.gooie.Translation;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;

public class Translator implements Runnable
{
	private ArrayList<TaggedSentence> sentences = new ArrayList<TaggedSentence>(); // essentially the priority queue
	private ArrayList<Long> timeStamps = new ArrayList<Long>(); 
	private GUIMain main;
	private int currentSentNum = 1;
	private int currentLangNum = 1;

	/**
	 * Class that handles the 2-way translation of a given sentence. It starts a new thread so the main thread
	 * doesn't freeze up. This allows it to update the user about the progress of the translations.
	 * @param sentence String that holds the sentence to be translated
	 * @param main GUIMain object needed to access fields of the GUI
	 */
	public Translator (GUIMain main)
	{
		this.main = main;
	}

	/**
	 * Compact method that sets everything's enabled property while the thread is translating.
	 * The components listed are listed because they can possibly interrupt or overlap the translating.
	 * @param b boolean that controls the "enabled" value of the listed components
	 */
	public void setAllEnabled(boolean b)
	{
//		main.nextSentenceButton.setEnabled(b);
//		main.transButton.setEnabled(b);
//		main.prevSentenceButton.setEnabled(b);
//		main.translationsComboBox.setEnabled(b);
		main.processButton.setEnabled(b);
	}
	
	//UNCOMMENT
//	public void isSentenceChange(String newSentence) {
//		if (sentences.contains(newSentence))
//			return;
//		else
//			load(sentences);
//	}

	/**
	 * Loads sentences into the translation queue. Newly added sentences take priority. If translations arent running, it starts running.
	 * If translations are already running, adds new sentences into the front of the queue.
	 */
	public void load(ArrayList<TaggedSentence> loaded) 
	{
//		if (sentences.size() == 0) {
//			for (int i = 0; i < loaded.size(); i++) {
//				timeStamps.add(new Long(0));
//			}
//		}
		
		// if there are no sentences currently being translated
		if (sentences.size() == 0)
		{
			// add the given sentences to the queue
			for (int i = 0; i < loaded.size(); i++)
				sentences.add(loaded.get(i));
			
			// start a new thread to begin translation
			Thread transThread = new Thread(this);
			transThread.start(); // calls run below
		}
		// if there are sentences in the queue already
//		else
//		{
//			//
//			for (int i = 0; i < loaded.size(); i++)
//			{
//				if (currentSentNum + i == sentences.size()) // the + i makes the statement account for the loaded sentences that have been added.
//					sentences.add(loaded.get(i));
//				else
//					sentences.add(currentSentNum, loaded.get(i)); // adds it directly after the sentence currently being processed
//			}
//		}
	}

	@Override
	public void run() 
	{
		// disable everything to start so there are no interruptions
		setAllEnabled(false); 

		// set up the progress bar
		main.translationsProgressBar.setIndeterminate(false);
		main.translationsProgressBar.setMaximum(sentences.size() * DriverDocumentsTab.translator.getUsedLangs().length);

		// finish set up for translation
		main.translationsProgressLabel.setText("Sentence: 1/" + sentences.size() + " Languages: 1/"  + DriverDocumentsTab.translator.getUsedLangs().length);

		// translate all languages for each sentence, sorting the list based on anon index after each translation
		while (!sentences.isEmpty() && currentSentNum <= sentences.size())
		{
			// if the sentence that is about to be translated already has translations, get rid of them
			if (sentences.get(currentSentNum-1).hasTranslations())
			{
				sentences.get(currentSentNum-1).setTranslations(new ArrayList<TaggedSentence>(Translation.getUsedLangs().length));
				sentences.get(currentSentNum-1).setTranslationNames(new ArrayList<String>(Translation.getUsedLangs().length));
			}
			main.translationsProgressLabel.setText("Sentence: " + currentSentNum + "/" + sentences.size() + " Languages: " + currentLangNum + "/"  + Translation.getUsedLangs().length);

			// Translate the sentence for each language
			for (Language lang: Translation.getUsedLangs())
			{
				// update the progress label
				main.translationsProgressLabel.setText("Sentence: " + currentSentNum + "/" + sentences.size() + " Languages: " + currentLangNum + "/"  + Translation.getUsedLangs().length);

				String translation = Translation.getTranslation(sentences.get(currentSentNum-1).getUntagged().trim(), lang);
				TaggedSentence taggedTrans = new TaggedSentence(translation);
				taggedTrans.tagAndGetFeatures();
				sentences.get(currentSentNum-1).getTranslations().add(taggedTrans);
				sentences.get(currentSentNum-1).getTranslationNames().add(Translation.getName(lang));
				sentences.get(currentSentNum-1).sortTranslations();
				String one = DriverDocumentsTab.taggedDoc.getUntaggedSentences().get(DriverDocumentsTab.sentToTranslate).trim();
				String two = sentences.get(currentSentNum-1).getUntagged().trim();
				if (one.equals(two))
					DriverTranslationsTab.showTranslations(sentences.get(currentSentNum-1));
				currentLangNum++;
				
				if (main.translationsProgressBar.getValue() + 1 <= main.translationsProgressBar.getMaximum())
					main.translationsProgressBar.setValue(main.translationsProgressBar.getValue() + 1);
			}
			currentLangNum = 1;
			currentSentNum++;
		}
		sentences.removeAll(sentences);
		main.translationsProgressBar.setIndeterminate(false);
		main.translationsProgressBar.setValue(0);
		main.translationsProgressLabel.setText("No Translations Pending.");
		currentSentNum = 1;
		setAllEnabled(true);
	}
}
