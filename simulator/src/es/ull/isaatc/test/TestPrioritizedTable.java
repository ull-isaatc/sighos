package es.ull.isaatc.test;
import java.util.ArrayList;

import es.ull.isaatc.util.Orderable;
import es.ull.isaatc.util.OrderedList;

class PriLevel extends ArrayList<Integer> implements Orderable {
	private static final long serialVersionUID = 1L;	
	int priority;
	
	public PriLevel(int priority) {
		this.priority = priority;
	}
	public Comparable getKey() {
		return priority;
	}
	public int compareTo(Orderable o) {
		return compareTo(o.getKey());
	}
	public int compareTo(Object o) {
		return getKey().compareTo(o);
	}
}

public class TestPrioritizedTable {
	static OrderedList<PriLevel> table;
	/** Initial CPU time (miliseconds). */
	static long iniT;
	
	/**
     * Inserts a new object in the table. The priority of the object determines its order.
     * @param obj New object with a priority value.
     */
    public static void add(Integer obj) {
    	PriLevel pLevel = table.get(obj);
    	if (pLevel == null) {
            pLevel = new PriLevel(obj);
            table.add(pLevel);
    	}    	
        pLevel.add(obj);
	}

	/**
	 * Removes the object specified.
	 * @param obj The object to be removed
	 * @return true if the object was correctly removed; false in other case.
	 */
	public static boolean remove(Integer obj) {		
    	PriLevel pLevel = table.get(obj);
    	if (pLevel != null)
        	return pLevel.remove(obj);
        return false;
	}
	
	public static void print() {
		long t = System.currentTimeMillis();
//		for (int i = 0; i < table.size(); i++) {
//			for (Integer val : table.get(i))
//				System.out.print(val + " ");
//			System.out.println();
//		}
		System.out.println("Tiempo transcurrido: " + (t - iniT));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		table = new OrderedList<PriLevel>();
		iniT = System.currentTimeMillis();
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 100000; i++)
				add(i);
			System.out.println("Añadidos. T: " + (System.currentTimeMillis() - iniT));
			for (int i = 0; i < 100000; i++)
				remove(i);
			System.out.println("Borrados. T: " + (System.currentTimeMillis() - iniT));
		}
	}

}
