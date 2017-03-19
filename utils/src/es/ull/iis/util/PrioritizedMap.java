/**
 * 
 */
package es.ull.iis.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * An structure which contains a priority-ordered map. Objects with the same priority are
 * located in the same level of the structure.
 * @author Iván Castilla Rodríguez
 */
public abstract class PrioritizedMap<T extends Collection<E>, E extends Prioritizable> implements Iterable<E> {
    /** Array of priority levels. */
	protected final Map<Integer, T> levels;
    /** Number of objects which this table contains. This value is updated when an object
     * is added or removed. */
    private int nObj;
	
	public PrioritizedMap() {
		levels = new TreeMap<Integer, T>();
	}
	
	/**
     * Inserts a new object in the table. The priority of the object determines its order.
     * @param obj New object with a priority value.
     */
	public void add(E obj) {
		T level = levels.get(obj.getPriority());
		if (level == null) {
			level = createLevel(obj.getPriority());
			levels.put(obj.getPriority(), level);
		}
		level.add(obj);
		nObj++;
	}
	
	public void clear() {
		levels.clear();
		nObj = 0;
	}
	
	public void remove(E obj) {
		final T level = levels.get(obj.getPriority());
		if (level == null)
			System.err.println(obj);
		level.remove(obj);
		if (level.isEmpty())
			levels.remove(obj.getPriority());
		nObj--;
	}
	
	public abstract T createLevel(Integer priority);

	/**
     * Total amount of objects that this table contains.
     * @return Total amount of objects contained by this table.
     */
	public int size() {
		return nObj;
	}
	
	public String toString() {
		return levels.toString();
	}

	/**
	 * Returns an iterator over the table 
	 * @return an iterator over the table
	 */
	public Iterator<E> iterator() {
		return new FIFOIterator();
	}
	
    /**
     * An iterator that allows the programmer to traverse the prioritized map. A 
     * <code>FIFOIterator</code> starts at level 0 (the highest priority) and returns
     * a new object of this level each time <code>next</code> is called. When the 
     * iterator reaches the end of the level, it starts the next level.
     * @author Iván Castilla Rodríguez
     */
	protected class FIFOIterator implements Iterator<E> {
        /** Main iterator level by level of the external estructure. */   
        final private Iterator<T> outIter;
        /** Minor iterator object by object of each level. */
        private Iterator<E> inIter = null;
        
        /**
         * Creates an iterator for the table.
         */
        public FIFOIterator() {
        	outIter = levels.values().iterator();
			if (outIter.hasNext()) {
				inIter = outIter.next().iterator();
			}
        }

        public E next() {
        	E obj = inIter.next();
			if (!inIter.hasNext()) {
				if (outIter.hasNext())
					inIter = outIter.next().iterator();
				else
					inIter = null;
			}
			return obj;
        }

    	public boolean hasNext() {
			if (inIter != null)
				if (inIter.hasNext())
					return true;
			return false;
    	}

    	public void remove() {
    		throw new UnsupportedOperationException("Not implemented");
    	}

	}
}
