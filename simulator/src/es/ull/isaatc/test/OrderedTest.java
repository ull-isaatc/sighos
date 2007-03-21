/**
 * 
 */
package es.ull.isaatc.test;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import es.ull.isaatc.util.Orderable;
import es.ull.isaatc.util.OrderedList;

class MyTree<K,T> extends TreeMap<K,T> {
	private static final long serialVersionUID = 1L;
	int a;

	/**
	 * 
	 */
	public MyTree() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param c
	 */
	public MyTree(Comparator<? super K> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 */
	public MyTree(Map<? extends K, ? extends T> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 */
	public MyTree(SortedMap<K, ? extends T> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}
	
}
class DummyObject implements Orderable {
	int key;
	
	public DummyObject(int key) {
		this.key = key;
	}
	
	public int compareTo(Orderable o) {
		return compareTo(o.getKey());
	}

	public int compareTo(Object o) {
		return getKey().compareTo(o);
	}        

	public Comparable getKey() {
		return key;
	}
	
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OrderedTest {
	final static int NELEM = 50;

	public static void originalMain() {
		long tIni, tCre, tIns, tRem;
		long mem1, mem0;
		int [] originalList = new int[NELEM];
		for (int i = 0; i < NELEM; i++)
			originalList[i] = (int)(Math.random() * NELEM);
		
		tIni = System.currentTimeMillis();
		mem0 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		OrderedList<DummyObject> list = new OrderedList<DummyObject>();
		tCre = System.currentTimeMillis();
		for (int val : originalList)
			list.add(new DummyObject(val));
		tIns = System.currentTimeMillis();
		mem1 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		for (int i = 0; i < NELEM; i++)
			list.remove(new Integer(originalList[i]));
		tRem = System.currentTimeMillis();
		System.out.println("Ord:\t" + (mem1 - mem0) + "\t" + (tRem - tIni) + "\t" + (tCre - tIni) + "\t" + (tIns - tCre) + "\t" + (tRem - tIns));
		
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		tIni = System.currentTimeMillis();
		mem0 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		MyTree<Integer, DummyObject> tree = new MyTree<Integer, DummyObject>();
		tCre = System.currentTimeMillis();
		for (int val : originalList)
			tree.put(val, new DummyObject(val));
		tIns = System.currentTimeMillis();
		mem1 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		for (int i = 0; i < NELEM; i++)
			tree.remove(originalList[i]);
		tRem = System.currentTimeMillis();
		System.out.println("Tree:\t" + (mem1 - mem0) + "\t" + (tRem - tIni) + "\t" + (tCre - tIni) + "\t" + (tIns - tCre) + "\t" + (tRem - tIns));		
	}

	public static void staticMain() {
		long tIni, tCre, tIns, tRem;
		long mem1, mem0;
		DummyObject [] originalList = new DummyObject[NELEM];
		for (int i = 0; i < NELEM; i++)
			originalList[i] = new DummyObject((int)(Math.random() * NELEM));
		
		tIni = System.currentTimeMillis();
		mem0 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		MyTree<Integer, DummyObject> tree = new MyTree<Integer, DummyObject>();
		tCre = System.currentTimeMillis();
		for (DummyObject val : originalList)
			tree.put(val.key, val);
		tIns = System.currentTimeMillis();
		mem1 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		for (int i = 0; i < NELEM; i++)
			tree.remove(originalList[i].key);
		tRem = System.currentTimeMillis();
		System.out.println("Tree:\t" + (mem1 - mem0) + "\t" + (tRem - tIni) + "\t" + (tCre - tIni) + "\t" + (tIns - tCre) + "\t" + (tRem - tIns));		
		
//		System.gc();
//		System.gc();
//		System.gc();
//		System.gc();

		tIni = System.currentTimeMillis();
		mem0 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		OrderedList<DummyObject> list = new OrderedList<DummyObject>();
		tCre = System.currentTimeMillis();
		for (DummyObject val : originalList)
			list.add(val);
		tIns = System.currentTimeMillis();
		mem1 = Runtime.getRuntime().totalMemory() -
	      Runtime.getRuntime().freeMemory();
		for (int i = 0; i < NELEM; i++)
			list.remove(new Integer(originalList[i].key));
		tRem = System.currentTimeMillis();
		System.out.println("Ord:\t" + (mem1 - mem0) + "\t" + (tRem - tIni) + "\t" + (tCre - tIni) + "\t" + (tIns - tCre) + "\t" + (tRem - tIns));
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		staticMain();
	}

}
