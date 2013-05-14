package edu.drexel.psal.anonymouth.gooie;

import com.memetix.mst.language.Language;
import java.util.ArrayList;

import edu.drexel.psal.anonymouth.gooie.Translation;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;

public class Translator implements Runnable
{
	private ArrayList<TaggedSentence> sentences = new ArrayList<TaggedSentence>(); // essentially the priority queue
	private GUIMain main;
	public static boolean firstRun = true;
	public static boolean addSent = false;
	private TaggedSentence oldSentence;
	private TaggedSentence newSentence;
	private int currentSentNum = 1;
	private int currentLangNum = 1;
	public static ArrayList<String> translatedSentences = new ArrayList<String>(); 
	public static Boolean finished = false;
	public static Boolean noInternet = false;
	public static Boolean translations = true;

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

	public void replace(TaggedSentence newSentence, TaggedSentence oldSentence) {
		if (sentences.size() >= 1) {
			addSent = true;
			this.newSentence = newSentence;
			this.oldSentence = oldSentence;
		} else {
			ArrayList<TaggedSentence> loaded = new ArrayList<TaggedSentence>();
			loaded.add(newSentence);
			load(loaded);
		}
	}
	
	/**
	 * Loads sentences into the translation queue. Newly added sentences take priority. If translations arent running, it starts running.
	 * If translations are already running, adds new sentences into the front of the queue.
	 */
	public void load(ArrayList<TaggedSentence> loaded)  {
		// add the given sentences to the queue
		sentences.addAll(loaded);

		if (PropertiesUtil.getDoTranslations()) {
			translations = true;
			// start a new thread to begin translation
			Thread transThread = new Thread(this);
			transThread.start(); // calls run below
		} else {
			translations = false;
		}
	}

	@Override
	public void run() 
	{
		// disable everything to start so there are no interruptions
		main.processButton.setEnabled(false);

		// set up the progress bar
		main.translationsProgressBar.setIndeterminate(false);
		main.translationsProgressBar.setMaximum(sentences.size() * DriverDocumentsTab.translator.getUsedLangs().length);
		// finish set up for translation
		main.translationsProgressLabel.setText("Sentence: 1/" + sentences.size() + " Languages: 0/"  + DriverDocumentsTab.translator.getUsedLangs().length);

		// translate all languages for each sentence, sorting the list based on anon index after each translation
		while (!sentences.isEmpty() && currentSentNum <= sentences.size()) {
			
			translatedSentences.add(sentences.get(currentSentNum-1).getUntagged());
			// if the sentence that is about to be translated already has translations, get rid of them
			if (sentences.get(currentSentNum-1).hasTranslations()) {
				sentences.get(currentSentNum-1).setTranslations(new ArrayList<TaggedSentence>(Translation.getUsedLangs().length));
				sentences.get(currentSentNum-1).setTranslationNames(new ArrayList<String>(Translation.getUsedLangs().length));
			}

			// Translate the sentence for each language
			for (Language lang: Translation.getUsedLangs()) {
				// update the progress label

				String translation = Translation.getTranslation(sentences.get(currentSentNum-1).getUntagged().trim(), lang);
				
				if (translation == null) {
					finished = true;
					noInternet = true;
					DriverTranslationsTab.showTranslations(sentences.get(0));
					sentences.removeAll(sentences);
					currentSentNum = 1;
					main.translationsProgressBar.setIndeterminate(false);
					main.translationsProgressBar.setValue(0);
					main.translationsProgressLabel.setText("No Translations Pending.");
					main.processButton.setEnabled(true);
					return;
				}
				
				main.translationsProgressLabel.setText("Sentence: " + currentSentNum + "/" + sentences.size() + " Languages: " + currentLangNum + "/"  + Translation.getUsedLangs().length);
				currentLangNum++;
				TaggedSentence taggedTrans = new TaggedSentence(translation);
				taggedTrans.tagAndGetFeatures();
				sentences.get(currentSentNum-1).getTranslations().add(taggedTrans);
				sentences.get(currentSentNum-1).getTranslationNames().add(Translation.getName(lang));
				sentences.get(currentSentNum-1).sortTranslations();
				String one = DriverDocumentsTab.taggedDoc.getUntaggedSentences().get(DriverDocumentsTab.sentToTranslate).trim();
				String two = sentences.get(currentSentNum-1).getUntagged().trim();
				
				if (one.equals(two))
					DriverTranslationsTab.showTranslations(sentences.get(currentSentNum-1));
				
				if (main.translationsProgressBar.getValue() + 1 <= main.translationsProgressBar.getMaximum())
					main.translationsProgressBar.setValue(main.translationsProgressBar.getValue() + 1);
				
				if (addSent) {
					addSent = false;
					sentences.add(currentSentNum, newSentence);
					if (sentences.contains(oldSentence))
						sentences.remove(oldSentence);
					else
						main.translationsProgressBar.setMaximum(sentences.size() * DriverDocumentsTab.translator.getUsedLangs().length);
				
					if (currentSentNum - 1 >= 1)
						currentSentNum -= 1;
				}
			}
			currentLangNum = 1;
			currentSentNum++;
		}
		finished = true;
		sentences.removeAll(sentences);
		main.translationsProgressBar.setIndeterminate(false);
		main.translationsProgressBar.setValue(0);
		main.translationsProgressLabel.setText("No Translations Pending.");
		currentSentNum = 1;
		main.processButton.setEnabled(true);
	}
}