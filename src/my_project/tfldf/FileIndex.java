package my_project.tfIdf;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import my_project.histogram.*;


/**************************************
 *  Add your code to this class !!!   *
 **************************************/

public class FileIndex {
	private Map<String,List<Map.Entry<String,Double>>> map_tfidt=new HashMap<String,List<Map.Entry<String, Double>>>();
	private boolean isInitialized = false;
	private Map<String, IHistogram> index=new HashMap();
	private int len;
	
	/*
	 * @pre: the directory is no empty, and contains only readable text files
	 * @pre: isInitialized() == false;
	 */
  	public void indexDirectory(String folderPath) { //Q1
		//This code iterates over all the files in the folder. add your code wherever is needed
		File folder = new File(folderPath);
		File[] listFiles = folder.listFiles();
		len=listFiles.length;
		for (File file : listFiles) {
			 if (file.isFile()) {
				try {
					List<String> f =FileUtils.readAllTokens(file);
					IHistogram x=new HashMapHistogram();
					x.addAll(f);
					index.put(file.getName(), x);
				}
				catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
		}
		for(Map.Entry<String, IHistogram> entry:index.entrySet()) {
			try {
			String filename=entry.getKey();
			map_tfidt.put(filename, sort_1(entry.getValue(),filename));}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		isInitialized = true;
	}

	public class Entrycomperator implements Comparator <Entry<String, Double>>{
		@Override
		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			if (o2.getValue().equals(o1.getValue())) {
				return o1.getKey().compareTo(o2.getKey());}
			return o2.getValue().compareTo(o1.getValue());
		}
	}
	
	
	
	// Q2
	/* @pre: isInitialized() */
	public int getCountInFile(String word, String fileName) throws FileIndexException{
		try{
			word=word.toLowerCase();
			return (index.get(fileName)).getCountForItem(word);}
		catch(NullPointerException e) {
			throw new FileIndexException(fileName);}
		}
	
	
	/* @pre: isInitialized() */
	public int getNumOfUniqueWordsInFile(String fileName) throws FileIndexException{ 
		try	{
			Set<String> set=index.get(fileName).getItemsSet(); 
			return set.size(); }
		catch(NullPointerException e) {
			throw new FileIndexException(fileName);
		}
	}
	
	/* @pre: isInitialized() */
	public int getNumOfFilesInIndex(){
		return this.len; 
	}

	
	/* @pre: isInitialized() */
	public double getTF(String word, String fileName) throws FileIndexException{ // Q3
		try{
			word=word.toLowerCase();
			IHistogram<String> his=this.index.get(fileName);
			return calcTF(his.getCountForItem(word), his.getCountsSum());}
		catch(NullPointerException e) {
			throw new FileIndexException(fileName);
		}
		 
	}
	
	/* @pre: isInitialized() 
	 * @pre: exist fileName such that getCountInFile(word) > 0*/
	public double getIDF(String word){ //Q4
		word=word.toLowerCase();
		int cnt=0;
		for(Map.Entry<String, IHistogram> entry:index.entrySet()) {
			if (entry.getValue().getCountForItem(word)>0)
				cnt++;
		}

		return calcIDF(this.len, cnt);
	}
	
	
	
	/*
	 * @pre: isInitialized()
	 * @pre: 0 < k <= getNumOfUniqueWordsInFile(fileName)
	 * @post: $ret.size() = k
	 * @post for i in (0,k-2):
	 * 		$ret[i].value >= $ret[i+1].value
	 */
	public List<Map.Entry<String, Double>> getTopKMostSignificantWords(String fileName, int k) 
													throws FileIndexException{ //Q5
		try{
			return (this.map_tfidt.get(fileName).subList(0, k));}
		catch(NullPointerException e) {
			throw new FileIndexException(fileName);
		}
	}
	
	/* @pre: isInitialized() */
	public double getCosineSimilarity(String fileName1, String fileName2) throws FileIndexException{ //Q6
		double numerator=0;
		double Denominator_A=denominator(this.map_tfidt.get(fileName1));
		double Denominator_B=denominator(this.map_tfidt.get(fileName2));
		Map <String,Double> map_tfidf_2=convert_tomap(this.map_tfidt.get(fileName2));		
		for(Entry<String, Double> entry:this.map_tfidt.get(fileName1)) {
			Double s=map_tfidf_2.get(entry.getKey());
			if (!(s==null||s==0)) {
				numerator+=(s*entry.getValue());
				}
		}
		return( numerator/Math.pow(Denominator_B*Denominator_A, 0.5)); 
	}
	
	/*
	 * @pre: isInitialized()
	 * @pre: 0 < k <= getNumOfFilesInIndex()-1
	 * @post: $ret.size() = k
	 * @post for i in (0,k-2):
	 * 		$ret[i].value >= $ret[i+1].value
	 */
	public List<Map.Entry<String, Double>> getTopKClosestDocuments(String fileName, int k) 
			throws FileIndexException{ //Q6
		try{
			List<Map.Entry<String, Double>> ret=new ArrayList<Map.Entry<String, Double>>();
			for(Map.Entry<String, IHistogram> entry:index.entrySet()) {
				if (!entry.getKey().equals(fileName)) {
					Double sim=getCosineSimilarity(fileName,entry.getKey());
					Entry<String, Double> ent= new AbstractMap.SimpleEntry(entry.getKey(), sim);
					ret.add(ent);
				}
			}
			Entrycomperator comperator =new Entrycomperator();
			Collections.sort(ret,comperator);
			return ret;}
		catch(Exception e) {
			throw new FileIndexException(fileName);
		}
		}

	
	private List<Map.Entry<String, Double>> sort_1(IHistogram x,String file_name) throws FileIndexException{
		List<Map.Entry<String, Double>> sorted=new ArrayList<Map.Entry<String, Double>>();
		Set<String> set= x.getItemsSet();
		for (String word : set) {
			double tfidf=this.getTFIDF(word, file_name);
			Entry<String, Double> entry= new AbstractMap.SimpleEntry(word, tfidf);
			sorted.add(entry);
		}
		Entrycomperator comperator =new Entrycomperator();
		Collections.sort(sorted,comperator);
		return sorted;
		
	}
	private Map<String, Double> convert_tomap(List<Map.Entry<String, Double>> lst ){
		Map <String,Double>mm=new HashMap();
		for (Entry<String, Double> entry : lst) {
			mm.put(entry.getKey(),entry.getValue());
		}
		return mm;
	}
	private static Double denominator(List<Map.Entry<String, Double>> lst ){
		Double res=0.;
		for (Entry<String, Double> entry : lst) {
			res+=Math.pow(entry.getValue(),2);
		}
		return res;
		
	}
	
	/*************************************************************/
	/********************* Don't change this ********************/
	/*************************************************************/
	
	public boolean isInitialized(){
		return this.isInitialized;
	}
	
	/* @pre: exist fileName such that getCountInFile(word) > 0*/
	public double getTFIDF(String word, String fileName) throws FileIndexException{
		return this.getTF(word, fileName)*this.getIDF(word);
	}
	
	private static double calcTF(int repetitionsForWord, int numOfWordsInDoc){
		return (double)repetitionsForWord/numOfWordsInDoc;
	}
	
	private static double calcIDF(int numOfDocs, int numOfDocsContainingWord){
		return Math.log((double)numOfDocs/numOfDocsContainingWord);
	}
	
}
