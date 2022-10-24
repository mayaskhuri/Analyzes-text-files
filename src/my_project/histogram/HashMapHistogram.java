package my_project.histogram;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**************************************
 *  Add your code to this class !!!   *
 **************************************/
public class HashMapHistogram<T extends Comparable<T>> implements IHistogram<T>{
	private Map<T, Integer> map;
	
	//add constructor here, if needed
	
	public HashMapHistogram(){
		map = new HashMap();
	}
	@Override
	public void addItem(T item) {
			int cnt=this.getCountForItem(item);
			this.map.put(item,cnt+1);	
	}
	
	@Override
	public boolean removeItem(T item)  {
		int cnt=this.getCountForItem(item);
		if (cnt ==0)
			return false;
		if (cnt==1) 
			this.map.remove(item);
		else {
			map.put(item,cnt-1);}
		return true;
	}
	
	@Override
	public void addAll(Collection<T> items) {
		for (T t : items) {
			addItem(t);
		}
	}

	@Override
	public int getCountForItem(T item) {
		if (this.map.get(item)==null) {
			return 0; }
		return this.map.get(item);
	}

	@Override
	public void clear() {
		 Map<T, Integer> clear_map=new HashMap();
		 this.map=clear_map;
	}

	@Override
	public Set<T> getItemsSet() {
		Set <T> set =new HashSet<T>();
		for (Map.Entry<T,Integer>  entry: this.map.entrySet()) {
			set.add(entry.getKey());
		}
		return set;
	}
	
	@Override
	public int getCountsSum() {
		int x=0;
		for (Map.Entry<T,Integer>  entry: this.map.entrySet()) {
			x+=entry.getValue();
		}
		return x;
	}

	@Override
	public Iterator<Map.Entry<T, Integer>> iterator() {
		Iterator<Map.Entry<T, Integer>> it= new HashMapHistogramIterator<>(this.map);
		return it;
	}


}
