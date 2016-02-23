/**
 * 
 */
package es.ull.iis.simulation.test;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

class ResourceList1 {
	private TreeMap<Integer, Integer> tree = new TreeMap<Integer, Integer>();
	private ArrayList<Integer> list = new ArrayList<Integer>();
    static AtomicInteger nADDS = new AtomicInteger(0);
    static AtomicInteger nREMOVES = new AtomicInteger(0);
    static AtomicInteger nGETS = new AtomicInteger(0);
    static AtomicInteger nSIZES = new AtomicInteger(0);

    static public void printStats() {
    	System.out.print("ADDS: " + nADDS);
    	System.out.print("\tREMOVES: " + nREMOVES);
    	System.out.print("\tGETS: " + nGETS);
    	System.out.println("\tSIZES: " + nSIZES);
    }

    /**
     * Adds a resource. If the resource isn't present in the list, it's included with a "1" count.
     * If the resource exists already, the count is increased.
     * @param res The resource added
     */
    public synchronized void add(Integer res) {
    	nADDS.incrementAndGet();
    	Integer val = tree.get(res);
    	if (val == null) {
    		val = 1;
    		list.add(res);
    	}
    	else
    		val++;
    	tree.put(res, val);
    }
    
    /**
     * Removes a resource. The resource can have more than one appearance in the list. In 
     * this case, it's no t really removed.
     * @param res The resource removed.
     * @return True if the resource is completely removed from the list. False in other case.
     */
    public synchronized boolean remove(Integer res) {
    	nREMOVES.incrementAndGet();
    	Integer val = tree.get(res);
    	// FIXME Debería crearme un tipo personalizado de excepción
    	if (val == null)
    		throw new RuntimeException("Unexpected error: Integer not found in resource type");
    	if (val > 1) {
    		tree.put(res, val - 1);
    		return false;
    	}
    	tree.remove(res);
    	list.remove(res);
    	return true;
    }
    
    /**
     * Returns the resource at the specified position 
     * @param index The position of the resource
     * @return The resource at the specified position.
     */
    public Integer get(int index) {
    	nGETS.incrementAndGet();
    	return list.get(index);
    }

    public ArrayList<Integer> getIntegerList() {
    	return list;
    }
    
    public int size() {
    	nSIZES.incrementAndGet();
    	return list.size();
    }
}

class ResourceList2 {
	/** List of resources */
	private final ArrayList<Integer> resources = new ArrayList<Integer>();
	/** A count of how many times each resource has been put as available */
	private final ArrayList<Integer> counter = new ArrayList<Integer>();
    static AtomicInteger nADDS = new AtomicInteger(0);
    static AtomicInteger nREMOVES = new AtomicInteger(0);
    static AtomicInteger nGETS = new AtomicInteger(0);
    static AtomicInteger nSIZES = new AtomicInteger(0);

    static public void printStats() {
    	System.out.print("ADDS: " + nADDS);
    	System.out.print("\tREMOVES: " + nREMOVES);
    	System.out.print("\tGETS: " + nGETS);
    	System.out.println("\tSIZES: " + nSIZES);
    }
    
    /**
     * Adds a resource. If the resource isn't present in the list, it's included with a "1" count.
     * If the resource exists already, the count is increased.
     * @param res The resource added
     */
    public synchronized void add(Integer res) {
    	nADDS.incrementAndGet();
    	int pos = resources.indexOf(res);
    	if (pos == -1) {
    		resources.add(res);
    		counter.add(1);
    	}
    	else
    		counter.set(pos, counter.get(pos).intValue() + 1);
    }
    
    /**
     * Removes a resource. The resource can have more than one appearance in the list. In 
     * this case, it's no t really removed.
     * @param res The resource removed.
     * @return True if the resource is completely removed from the list. False in other case.
     */
    public synchronized boolean remove(Integer res) {
    	nREMOVES.incrementAndGet();
    	int pos = resources.indexOf(res);
    	// FIXME Debería crearme un tipo personalizado de excepción
    	if (pos == -1)
    		throw new RuntimeException("Unexpected error: Integer not found in resource type");
    	if (counter.get(pos).intValue() > 1) {
    		counter.set(pos, new Integer(counter.get(pos).intValue() - 1));
    		return false;
    	}
		resources.remove(pos);
		counter.remove(pos);
    	return true;
    }
    
    /**
     * Returns the resource at the specified position 
     * @param index The position of the resource
     * @return The resource at the specified position.
     */
    public Integer get(int index) {
    	nGETS.incrementAndGet();
    	return resources.get(index);
    }
    
    /**
     * Returns the number of resources in this list. 
     * @return The number of resources in this list.
     */
    public int size() {
    	nSIZES.incrementAndGet();
    	return resources.size();
    }

	public ArrayList<Integer> getIntegers() {
		return resources;
	}
}

/**
 * Checks two different structures to control resource lists which requires sequential and random access.
 * ResourceList1 seems to be far much better  
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRTStructure {
	static int NTEST = 100000;
	static int NDIF = NTEST / 2;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long t1, t2, t3, t4;
		Integer[] list = new Integer[NTEST];
		for (int i = 0; i < NTEST; i++)
			list[i] = new Integer(i % NDIF);
		
		t1 = System.nanoTime();
		ResourceList1 rList1 = new ResourceList1();
		for (Integer i : list)
			rList1.add(i);
		t2 = System.nanoTime();
		for (int i = 0; i < NDIF; i++)
			rList1.get(i);
		t3 = System.nanoTime();
		for (Integer i : list)
			rList1.remove(i);
		t4 = System.nanoTime();
		System.out.println("Time List 1: " + (t4 - t1) + "\tADD: " + (t2 - t1) + "\tGET: " + (t3 - t2) + "\tREMOVE: " + (t4 - t3));

		t1 = System.nanoTime();
		ResourceList2 rList2 = new ResourceList2();
		for (Integer i : list)
			rList2.add(i);
		t2 = System.nanoTime();
		for (int i = 0; i < NDIF; i++)
			rList2.get(i);
		t3 = System.nanoTime();
		for (Integer i : list)
			rList2.remove(i);
		t4 = System.nanoTime();
		System.out.println("Time List 2: " + (t4 - t1) + "\tADD: " + (t2 - t1) + "\tGET: " + (t3 - t2) + "\tREMOVE: " + (t4 - t3));
	}

}
