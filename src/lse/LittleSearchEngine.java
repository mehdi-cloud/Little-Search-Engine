package lse;

import java.io.*;
import java.util.*;

/*
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	
	 // The hash set of all noise words.
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		
		HashMap<String,Occurrence> demap = new HashMap<String,Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		
		while(sc.hasNext()){
			String word = getKeyword(sc.next());
			//checks to see if the given word is not a noiseword /punct
			if (word != null) {
				
				if(demap.containsKey(word)) { 
					Occurrence current = demap.get(word);
					current.frequency++;
				}else {
					Occurrence current = new Occurrence(docFile, 1);
					demap.put(word, current);
				}}}
		sc.close();
		return demap;
		}//closes the code
		
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		
		//goes through the global hashmap
		for(Map.Entry<String, Occurrence> entry: kws.entrySet()){
			
			//global table sjit
			if(keywordsIndex.containsKey(entry.getKey())) {
				//retrieves the list
				ArrayList<Occurrence> list = keywordsIndex.get(entry.getKey());
				Occurrence occ1 = entry.getValue(); 
				
				
				list.add(occ1);
				this.insertLastOccurrence(list);
				
			}else {
				
				Occurrence occ2 = entry.getValue();
				ArrayList<Occurrence> list = new ArrayList<Occurrence>();
				
				
				list.add(occ2);
				keywordsIndex.put(entry.getKey(), list);
	} }//ends for loop 

	}//ends method
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		
		if(word.isEmpty())  return null;
		
		//punctuation check
		for(int i = 0; i<word.length(); i++) {
			if(word.charAt(i)=='.'||word.charAt(i)==','||word.charAt(i)=='?'||word.charAt(i)==':'||word.charAt(i)==';'||word.charAt(i)=='!') {
				//checks to see if i is at the last letter
				if(i!=(word.length()-1)) {
					//check if next char is a letter
					if(Character.isLetter(word.charAt(i+1))) {
						return null; } }
			//insert delete punctuation	
				word =word.substring(0,i);
				break;}
			//checks to see if not punct or letter (-)
			else if (!Character.isLetter(word.charAt(i))) {
				return null; 
				}}
		
		//convert to lowercase 
		word= word.toLowerCase();
		
		
		//noiseword check
		if(noiseWords.contains(word)) {
			return null;}
		else {
			return word;}
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occ1 List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occ1) {
		/** COMPLETE THIS METHOD **/
		if (occ1.size() < 2) {
			return null;}
		
		int l = 0;
		int h = occ1.size() - 1; //target index
		int r = occ1.size() - 2; //array list minus last occurrence
		int mid = 0;
		
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		
		//closes once left is greater than r
		while (l <= r) {
			//sets and adds every single midpoint
			mid = (l + r) / 2;
			midpoints.add(mid);
			if (occ1.get(mid).frequency == occ1.get(h).frequency) {
				break;
			} 
			else if (occ1.get(mid).frequency < occ1.get(h).frequency) {
				l = mid + 1;
			}
			else {
				r = mid - 1; }}
		
		Occurrence pt = occ1.remove(h);
		occ1.add(midpoints.get(midpoints.size()-1), pt);
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return midpoints;
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
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		
		//traverses all the keywords
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Occurrence> firstlist = keywordsIndex.get(kw1);
		ArrayList<Occurrence> seclist = keywordsIndex.get(kw2);
		ArrayList<String> top5 = new ArrayList<String>();
		
		int i = 0; // array list 1
		int j= 0; //array list 2
		int count = 0; 
		
		//loops thru arraylist to find the top 2 docs
		while(i<firstlist.size() && j< seclist.size()) {
			//1st
			Occurrence occuno = firstlist.get(i);
			String docc = occuno.document; 
			int freq1 = occuno.frequency;
			
			//2nd
			Occurrence occsec = seclist.get(j);
			String doc2 = occsec.document; 
			int freq2 = occsec.frequency;
		 
			//freq1 big
			if(freq1>=freq2) {
				//only enter if top5 doesnt have doct 1, if it does continue;
				if(!top5.contains(docc)) {
					//adds docc to top5 bc it fits the descrip
					top5.add(docc);
					count++;
					if(count>5) {
						return top5;
				}   }
				i++; 
				continue;
			//freq2 big
			}else if(freq1<freq2) {
				if(!top5.contains(doc2)) {
					top5.add(doc2);
					count++;
					if(count>5) {
						return top5;}
				}
				j++;
				continue;}}

		
		
		if (i < firstlist.size()) {
			i = 0;
			while (i < firstlist.size()) {
				Occurrence occ1 = firstlist.get(i);
				String doc1 = occ1.document;
				
				if (!top5.contains(doc1)) {
					top5.add(doc1);
					count++;
					if (count > 5) {
					return top5;
					}
				}
				i++;
			}
		}
		if (j < seclist.size()) {
			j = 0;
			while (j < seclist.size()) {
				Occurrence occ2 = seclist.get(j);
				String doc2 = occ2.document;
				
				if (!top5.contains(doc2)) {
					top5.add(doc2);
					count++;
					if (count > 5) {
						return top5;
					}
				}
				j++;
			}
		}
		
		return top5;
	}//ends method
	
}//ends code

