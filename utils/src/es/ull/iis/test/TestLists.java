/**
 * 
 */
package es.ull.iis.test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

class MyObject implements Comparable<MyObject> {
	Integer value;
	Double value2;
	
	/**
	 * @param value
	 * @param value2
	 */
	public MyObject(Integer value, Double value2) {
		this.value = value;
		this.value2 = value2;
	}

	@Override
	public int compareTo(MyObject arg0) {
		return value.compareTo(arg0.value);
	}
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestLists {
	static final int NELEM = 200000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long t1, t2;

//		ArrayList<MyObject> list = new ArrayList<MyObject>();
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list.add(new MyObject(i, (double)i));
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list.getClass().getSimpleName() + "(add):\t" + t2);
//
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list.remove(0);
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list.getClass().getSimpleName() + "(remove):\t" + t2);
//
//		ArrayDeque<MyObject> list2 = new ArrayDeque<MyObject>();
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list2.add(new MyObject(i, (double)i));
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list2.getClass().getSimpleName() + "(add):\t" + t2);
//
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list2.poll();
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list2.getClass().getSimpleName() + "(remove):\t" + t2);
//		
//		LinkedList<MyObject> list3 = new LinkedList<MyObject>();
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list3.add(new MyObject(i, (double)i));
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list3.getClass().getSimpleName() + "(add):\t" + t2);
//
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list3.poll();
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list3.getClass().getSimpleName() + "(remove):\t" + t2);
//
//		Stack<MyObject> list4 = new Stack<MyObject>();
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list4.add(new MyObject(i, (double)i));
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list4.getClass().getSimpleName() + "(add):\t" + t2);
//
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list4.pop();
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list4.getClass().getSimpleName() + "(remove):\t" + t2);
//		
//		Vector<MyObject> list5 = new Vector<MyObject>();
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list5.add(new MyObject(i, (double)i));
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list5.getClass().getSimpleName() + "(add):\t" + t2);
//
//		t1 = System.currentTimeMillis();
//		for (int i = 0; i < NELEM; i++) 
//			list5.remove(0);
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println(list5.getClass().getSimpleName() + "(remove):\t" + t2);
//		
		PriorityQueue<MyObject> list6 = new PriorityQueue<MyObject>();
		t1 = System.currentTimeMillis();
		for (int i = 0; i < NELEM; i++) 
			list6.add(new MyObject(i, (double)i));
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list6.getClass().getSimpleName() + "(add):\t" + t2);

		t1 = System.currentTimeMillis();
		for (int i = 0; i < NELEM; i++) 
			list6.poll();
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list6.getClass().getSimpleName() + "(remove):\t" + t2);

		PriorityQueue<MyObject> list7 = new PriorityQueue<MyObject>();
		t1 = System.currentTimeMillis();
		for (int i = NELEM - 1; i >= 0; i--)
			list7.add(new MyObject(i, (double)i));
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list7.getClass().getSimpleName() + "(add):\t" + t2);

		t1 = System.currentTimeMillis();
		for (int i = NELEM - 1; i >= 0; i--)
			list7.poll();
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list7.getClass().getSimpleName() + "(remove):\t" + t2);

		TreeSet<MyObject> list8 = new TreeSet<MyObject>();
		t1 = System.currentTimeMillis();
		for (int i = 0; i < NELEM; i++) 
			list8.add(new MyObject(i, (double)i));
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list8.getClass().getSimpleName() + "(add):\t" + t2);

		t1 = System.currentTimeMillis();
		for (int i = 0; i < NELEM; i++) 
			list8.pollFirst();
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list8.getClass().getSimpleName() + "(remove):\t" + t2);

		TreeSet<MyObject> list9 = new TreeSet<MyObject>();
		t1 = System.currentTimeMillis();
		for (int i = NELEM - 1; i >= 0; i--)
			list9.add(new MyObject(i, (double)i));
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list9.getClass().getSimpleName() + "(add):\t" + t2);

		t1 = System.currentTimeMillis();
		for (int i = NELEM - 1; i >= 0; i--)
			list9.pollLast();
		t2 = System.currentTimeMillis() - t1;
		System.out.println(list9.getClass().getSimpleName() + "(remove):\t" + t2);

	}

}
