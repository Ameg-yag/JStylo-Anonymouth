package edu.drexel.psal.anonymouth.gooie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;


/**
 * @author sadiaafroz
 *
 */
public class Translation {
	
	private final static String NAME = "( Translation ) - ";
	
	/*
	 * ALTERNATE CLIENT ID/SECRET COMBOS (NEW / UNUSED) 
	 * 	 
	 * private final static String CLIENT_ID = "weegeemounty";
	 * private final static String CLIENT_SECRET = "UR0YCU0x20oOSzqt+xtHkT2lhk6RjcKvqEqd/3Hsdvs=";	
	 * 
	 * NO GOOD FOR 04/2013 (BUT GOOD AFTER):
	 * 
	 * private final static String CLIENT_ID = "drexel1";
	 * private final static String CLIENT_SECRET = "+L2MqaOGTDs4NpMTZyJ5IdBWD6CLFi9iV51NJTXLiYE=";
	 * 
	 * private final static String CLIENT_ID = "drexel4";
	 * private final static String CLIENT_SECRET = "F5Hw32MSQoTygwLu6YMpHYx9zV3TQVQxqsIIybVCI1Y=";
	 * 
	 * private final static String CLIENT_ID = "sheetal57";
	 * private final static String CLIENT_SECRET = "+L2MqaOGTDs4NpMTZyJ5IdBWD6CLFi9iV51NJTXLiYE=";
	 * 
	 * private final static String CLIENT_ID = "drexel2";
	 * private final static String CLIENT_SECRET = "KKQWCR7tBFZWA5P6VZzWRWg+5yJ+s1d5+RhcLW6+w3g=";
	 * 
	 * private final static String CLIENT_ID = "ozoxdxie";
	 * private final static String CLIENT_SECRET = "wU9ROglnO5qzntfRsxkq7WWGp7LAMrz0jdxPEd0t1u8=";	
	 * 
	 */
	
	private static Map<String, String> clientsAndSecrets;
	private static ArrayList<String> clients;
	private static int current = 0;
	private static Boolean translationFound = false;
	
	private static Language allLangs[] = {Language.ARABIC, Language.BULGARIAN, Language.CATALAN,
			Language.CHINESE_SIMPLIFIED, Language.CHINESE_TRADITIONAL,Language.CZECH,
			Language.DANISH,Language.DUTCH,Language.ESTONIAN,Language.FINNISH,
			Language.FRENCH,Language.GERMAN,Language.GREEK,Language.HAITIAN_CREOLE,
			Language.HEBREW,Language.HINDI,Language.HMONG_DAW,Language.HUNGARIAN,
			Language.INDONESIAN,Language.ITALIAN,Language.JAPANESE,
			Language.KOREAN,Language.LATVIAN,Language.LITHUANIAN,
			Language.NORWEGIAN,Language.POLISH,Language.PORTUGUESE,
			Language.ROMANIAN,Language.RUSSIAN,Language.SLOVAK,
			Language.SLOVENIAN,Language.SPANISH, Language.SWEDISH, 
			Language.THAI, Language.TURKISH, Language.UKRAINIAN, Language.VIETNAMESE};
	
	private static Language usedLangs[] = {Language.ARABIC, Language.CZECH, Language.DANISH,Language.DUTCH,
			Language.FRENCH,Language.GERMAN,Language.GREEK, Language.HUNGARIAN,
			Language.ITALIAN,Language.JAPANESE, Language.KOREAN, Language.POLISH, Language.RUSSIAN,
			Language.SPANISH, Language.VIETNAMESE};
	
	private static HashMap<Language, String> names = new HashMap<Language, String>();
	
	public Translation()
	{
		names.put(allLangs[0], "Arabic");
		names.put(allLangs[1], "Bulgarian");
		names.put(allLangs[2], "Catalan");
		names.put(allLangs[3], "Chinese_Simplified");
		names.put(allLangs[4], "Chinese_Traditional");
		names.put(allLangs[5], "Czech");
		names.put(allLangs[6], "Danish");
		names.put(allLangs[7], "Dutch");
		names.put(allLangs[8], "Estonian");
		names.put(allLangs[9], "Finnish");
		names.put(allLangs[10], "French");
		names.put(allLangs[11], "German");
		names.put(allLangs[12], "Greek");
		names.put(allLangs[13], "Haitian_Creole");
		names.put(allLangs[14], "Hebrew");
		names.put(allLangs[15], "Hindi");
		names.put(allLangs[16], "Hmong_Daw");
		names.put(allLangs[17], "Hungarian");
		names.put(allLangs[18], "Indonesian");
		names.put(allLangs[19], "Italian");
		names.put(allLangs[20], "Japanese");
		names.put(allLangs[21], "Korean");
		names.put(allLangs[22], "Latvian");
		names.put(allLangs[23], "Lithuanian");
		names.put(allLangs[24], "Norwegian");
		names.put(allLangs[25], "Polish");
		names.put(allLangs[26], "Portugese");
		names.put(allLangs[27], "Romanian");
		names.put(allLangs[28], "Russian");
		names.put(allLangs[29], "Slovak");
		names.put(allLangs[30], "Slovenian");
		names.put(allLangs[31], "Spanish");
		names.put(allLangs[32], "Swedish");
		names.put(allLangs[33], "Thai");
		names.put(allLangs[34], "Turkish");
		names.put(allLangs[35], "Ukrainian");
		names.put(allLangs[36], "Vietnamese");
		
		readyAccountsAndSecrets();
	}

	private void readyAccountsAndSecrets() {
		clients = new ArrayList<String>();
		clients.add("fyberoptikz");
		clients.add("weegeemounty");
		clients.add("drexel1");
		clients.add("drexel4");
		clients.add("sheetal57");
		clients.add("drexel2");
		clients.add("ozoxdxie");
		
		clientsAndSecrets = new HashMap();
		clientsAndSecrets.put(clients.get(0), "fAjWBltN4QV+0BKqqqg9nmXVMlo5ffa90gxU6wOW55Q=");
		clientsAndSecrets.put(clients.get(1), "UR0YCU0x20oOSzqt+xtHkT2lhk6RjcKvqEqd/3Hsdvs=");
		clientsAndSecrets.put(clients.get(2), "+L2MqaOGTDs4NpMTZyJ5IdBWD6CLFi9iV51NJTXLiYE=");
		clientsAndSecrets.put(clients.get(3), "F5Hw32MSQoTygwLu6YMpHYx9zV3TQVQxqsIIybVCI1Y=");
		clientsAndSecrets.put(clients.get(4), "+L2MqaOGTDs4NpMTZyJ5IdBWD6CLFi9iV51NJTXLiYE=");
		clientsAndSecrets.put(clients.get(5), "KKQWCR7tBFZWA5P6VZzWRWg+5yJ+s1d5+RhcLW6+w3g=");
		clientsAndSecrets.put(clients.get(6), "wU9ROglnO5qzntfRsxkq7WWGp7LAMrz0jdxPEd0t1u8=");
	}
	
	public static String getTranslation(String original, Language other)
	{   
	    Translate.setClientId(clients.get(current));
		Translate.setClientSecret(clientsAndSecrets.get(clients.get(current)));
	    
	    try {
	    	String backToenglish;
	    	
	    	do {
	    		String translatedText = Translate.execute(original, Language.ENGLISH,other);
				backToenglish = Translate.execute(translatedText,other,Language.ENGLISH);
				
				if (backToenglish.contains("TranslateApiException: The Azure Market Place Translator Subscription associated with the request credentials has zero balance.")) {
					if (current+1 >= clients.size())
						current = 0;
					else
						current++;
					Translate.setClientId(clients.get(current));
					Translate.setClientSecret(clientsAndSecrets.get(clients.get(current)));
					translationFound = false;
				} else
					translationFound = true;
	    	} while (!translationFound);
			
			return backToenglish;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return null;
	}
	
	/**
	 * 2-way translates the given sentence and returns an ArrayList of them
	 * @param original String you want translated
	 * @return 2-way translated sentences for every language available
	 * @author julman
	 */
	public ArrayList<String> getAllTranslations(String original)
	{
		Translate.setClientId(clients.get(current));
		Translate.setClientSecret(clientsAndSecrets.get(clients.get(current)));
	    
	    ArrayList<String> translations = new ArrayList<String>();
    	try {
    		String backToEnglish;
    		
    		for (Language other:allLangs)
    	    {
    			do {
    	    		String translatedText = Translate.execute(original, Language.ENGLISH,other);
    				backToEnglish = Translate.execute(translatedText,other,Language.ENGLISH);
    				
    				if (backToEnglish.contains("TranslateApiException: The Azure Market Place Translator Subscription associated with the request has credentials zero balance.")) {
    					if (current+1 >= clients.size())
    						current = 0;
    					else
    						current++;
    					
    					Translate.setClientId(clients.get(current));
    					Translate.setClientSecret(clientsAndSecrets.get(clients.get(current)));
    					translationFound = false;
    				} else
    					translationFound = true;
    	    	} while (!translationFound);

				translations.add(backToEnglish);
    	    }
    		
    		return translations;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
	
	public static String getName(Language lang)
	{
		return names.get(lang);
	}
	
	public static Language[] getAllLangs()
	{
		return allLangs;
	}
	
	public static Language[] getUsedLangs()
	{
		return usedLangs;
	}
	
	/*
    public static void main(String[] args) throws Exception {
	    // Set your Windows Azure Marketplace client info - See http://msdn.microsoft.com/en-us/library/hh454950.aspx
	    Translation t = new Translation();
	    Language allLangs[] = {Language.ARABIC, Language.BULGARIAN, Language.CATALAN,
	    		Language.CHINESE_SIMPLIFIED, Language.CHINESE_TRADITIONAL,Language.CZECH,
	    		Language.DANISH,Language.DUTCH,Language.ESTONIAN,Language.FINNISH,
	    		Language.FRENCH,Language.GERMAN,Language.GREEK,Language.HAITIAN_CREOLE,
	    		Language.HEBREW,Language.HINDI,Language.HMONG_DAW,Language.HUNGARIAN,
	    		Language.INDONESIAN,Language.ITALIAN,Language.JAPANESE,
	    		Language.KOREAN,Language.LATVIAN,Language.LITHUANIAN,
	    		Language.NORWEGIAN,Language.POLISH,Language.PORTUGUESE,
	    		Language.ROMANIAN,Language.RUSSIAN,Language.SLOVAK,
	    		Language.SLOVENIAN,Language.SPANISH, Language.SWEDISH, 
	    		Language.THAI, Language.TURKISH, Language.UKRAINIAN, Language.VIETNAMESE};

	    //read file
	    File original = new File("original_data/a_01.txt");
	    String output = "translated/"+original.getName();
	    
	    List<String> allLines = Util.readFile(original, true);
	    
	    String twoWayTranslation = "";
	    int lineNumber = 0;
	    
	    for(String aLine:allLines){
	    	    if(aLine.length()==0) continue;
	    	    twoWayTranslation = "Original: "+aLine+"\n";
	    	    for(Language other:allLangs)
			    {
		 	    	twoWayTranslation+=other.name()+":  "+t.getTranslation(aLine, other)+"\n";
			    }
	    	    Util.writeFile(twoWayTranslation, output+lineNumber, false);
	    	    
	    	    lineNumber++;
		 	    
	    }
	   // System.out.println(translatedText);
	  }*/

}

/*
    emails (@gmail.com)
    OzoXDxiE -> done
    w3gMTdJp -> done
	FIKCG9KW -> gmail yes, microsoft no
	5YBFviQ8 -> done
	ewvqAbvm -> not done
	Zj5RR5co -> not done
	
	
	pass
	
	jWURke4=s8q@%^n2
	
	
	favorite historical person:
	
	wellicantbesureatm

*/