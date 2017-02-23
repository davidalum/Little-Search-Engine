package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String dFile, String nWords) 
	throws FileNotFoundException {
		// table has words loaded
		Scanner sc = new Scanner(new File(nWords));
		while (sc.hasNext()) {
			String w = sc.next();
			noiseWords.put(w,w);
		}
		
		sc = new Scanner(new File(dFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String dFile) 
		
		
			 throws FileNotFoundException
		        {
		                HashMap<String, Occurrence> mh = new HashMap<String, Occurrence>();
		                Scanner sc = null;
		                try{
		                sc = new Scanner(new File(dFile));
		                }
		                catch(FileNotFoundException f){
		                	throw new FileNotFoundException();
		                }
		                
		                
		                while (sc.hasNext())
		                {
		                        String kw = getKeyWord(sc.next());
		                        if (kw == null)
		                        {continue;
		                        }
		                        if (mh.containsKey(kw))
		                        {
		                                mh.get(kw).frequency++;}
		                        else
		                        {
		                             Occurrence occ = new Occurrence(dFile, 1);
		                                mh.put(kw, occ);
		                        }
		                }
		                sc.close();
		                
		                System.out.println(mh);
		                	return mh;
		        }
		
	
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
ArrayList<Occurrence> list = new ArrayList<Occurrence>();
		
		for(String key: kws.keySet())
		{	
			Occurrence occ = kws.get(key);
			
			if(!keywordsIndex.containsKey(key))
			{
				ArrayList<Occurrence> occurList = new ArrayList<Occurrence>();				
				occurList.add(occ);
				keywordsIndex.put(key, occurList);
			}
			else
			{
				list = keywordsIndex.get(key);
				list.add(occ);
				insertLastOccurrence(list);
				keywordsIndex.put(key, list);
			}	
		}	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		word = word.trim();
		int in = 0;
		
	while(in == '.' || in == ',' || in == '?' || in == ':' || in == ';' || in == '!')
		{
						word = word.substring(0, word.length()-1);
			
				if(word.length() > 1)
			{
				in = word.charAt(word.length()-1);
			}
		else
			{
				break;
			}
		}
		
	
						word = word.toLowerCase();

	for(String noiseWord: noiseWords.keySet())
		{
		      	if(word.equalsIgnoreCase(noiseWord))
			{
		return null;
			}
		}
		
		for(int j= 0; j < word.length(); j++)
		{
			if(!Character.isLetter(word.charAt(j)))
			{
				return null;
			}
		}
		System.out.println(word);
return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size() == 1)
		{
			return null;
		}
		
		int lF = occs.get(occs.size()-1).frequency;
Occurrence temp = occs.get(occs.size() -1);
				int ol = 0;
		  int ih = occs.size()-1;
		
		  			int dim;
		ArrayList<Integer> midIndex = new ArrayList<Integer>();
		
        while( ol <= ih )
        {
        	dim = ( ol + ih ) / 2;
            midIndex.add(dim);

            if( lF > occs.get(dim).frequency )
            {            	
            	ih = dim-1;
            }
            else if(lF < occs.get(dim).frequency)
            {
            	ol = dim+1;
            }
            else
            {
            	break;
            }
        }
        
        if(midIndex.get(midIndex.size()-1) == 0)
        {
        	if(temp.frequency < occs.get(0).frequency)
        	{
        		occs.add(1, temp);
        		occs.remove(occs.size()-1);
        		
        		return midIndex;
        	}
        }
        
        occs.add(midIndex.get(midIndex.size()-1), temp);
        occs.remove(occs.size()-1);
        
     /*   for(int i = 0; i < midIndex.size(); i++)
        {
        	System.out.print(midIndex.get(i) + " ");
        }
        
        System.out.println(); */
        
		return midIndex;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
        if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2))
        {
                return null;
        }
        String doc1;
        String doc2;
        int cat;
        int dog;
        ArrayList<String> ans = new ArrayList<String>();
        ArrayList<Occurrence> occ1 = keywordsIndex.get(kw1), occ2 = keywordsIndex.get(kw2);
        int da = 0, aj = 0;
        while (ans.size() <= 4 && ((occ1 != null && da < occ1.size()) || (occ2 != null && aj < occ2.size())))
        {
                if (occ1 != null && occ2 != null)
                {
                        	//String doc1 = (da < occ1.size()) ? occ1.get(da).document : null, 
                //	doc2 = (aj < occ2.size()) ? occ2.get(aj).document : null;
                        if(da < occ1.size()) {
                        	 doc1 = occ1.get(da).document;
                        	                        }
                        else{
                        	 doc1 = null;}
                        
                        if(aj < occ2.size()){
                        	 doc2 = occ2.get(aj).document   ;                     	
                        }
                        else{
                        	doc2 = null;
                        }
                        
                        
                        
                        //int freq1 = (da < occ1.size()) ? occ1.get(da).frequency : 0, 
                        //freq2 = (aj < occ2.size()) ? occ2.get(aj).frequency : 0;
                        if(da < occ1.size()){
                        	cat = occ1.get(da).frequency; 
                        }
                        else{
                        	cat = 0;
                        }
                        
                        if(aj < occ2.size()){
                        	
                        	dog = occ2.get(aj).frequency;
                        }
                        else{
                        	dog = 0;
                        }
                        
                        if (cat >= dog)
                        {
                        			if (!ans.contains(doc1))
                                {
                                        ans.add(doc1);
                                }
                        			da++;
                        }
                 else
                        {
                                if (!ans.contains(doc2))
                                {
                                        ans.add(doc2);}
                                aj++;
                        }
                        continue;
                }
                if (occ2 != null)
                {
                         doc2 = occ2.get(aj).document;
                        
                        if (!ans.contains(doc2))
                        {
                                ans.add(doc2);
                        }
                        aj++;
                }
                else
                {
                         doc1 = occ1.get(da).document;
                        
                        	
                        		if (!ans.contains(doc1))
                        {
                        ans.add(doc1);
                        }
                        da++;
                }
        }
    
        
        return ans;
}
}
