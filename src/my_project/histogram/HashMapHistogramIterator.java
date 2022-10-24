package my_project.histogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**************************************
 *  Add your code to this class !!!   *
 **************************************/

public class HashMapHistogramIterator<T extends Comparable<T>> 
							implements Iterator<Map.Entry<T, Integer>>{
	int cur;
	Map<T, Integer> map;
	List<Entry<T, Integer>> lst;
	public HashMapHistogramIterator(Map<T, Integer> map) {

		cur=-1;
		this.map=map;
		Set<Entry<T, Integer>> entries = map.entrySet();
		lst = new ArrayList<Entry<T, Integer>>(entries);
		key_Comp Comparator = new key_Comp();
		Collections.sort(lst,Comparator);
	}
	
	public class key_Comp implements Comparator<Map.Entry<T, Integer>>{
		public int compare(Entry<T, Integer> o1, Entry<T, Integer> o2) {
			return o1.getKey().compareTo(o2.getKey());	
		}
	}
	@Override
	public boolean hasNext() {
		if (cur+1==this.lst.size())
			return false;
		return true;
	}

	@Override
	public Map.Entry<T, Integer> next() {
		this.cur++;
		return this.lst.get(cur);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
		
	}
	
}
