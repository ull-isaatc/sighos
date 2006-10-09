
package es.ull.isaatc.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An structure which contains a priority-ordered list. Objects with the same priority are
 * located in the same level of the structure. <p>Several iterators can be used to traverse this
 * structure. Each iterator gives a different level of equality of opportunity when accessing
 * objects with the same priority. 
 * @author Iván Castilla Rodríguez
 */
public class PrioritizedTable<T extends Prioritizable> {
    /** Array of priority levels. */
    protected OrderedList<PrioritizedLevel> levels;
    /** Ways of iterate through the structure. <p><strong>NORMAL</strong> indicates a sequential order.
     * <p><strong>BALANCED</strong> indicates every time the level is started at a different object.
     * <p><strong>RANDOM</strong> indicates a random permutation determines the order.
     **/
    public enum IteratorType {NORMAL, BALANCED, RANDOM}
    
    /**
     * Initializes the levels of the structure.  
     */
    public PrioritizedTable() {
        levels = new OrderedList<PrioritizedLevel>();
    }
    
	/**
     * Inserts a new object in the table. The priority of the object determines its order.
     * @param obj New object with a priority value.
     */
    public void add(T obj) {
    	PrioritizedLevel pLevel = levels.get(new Integer(obj.getPriority()));
    	if (pLevel == null) {
            pLevel = new PrioritizedLevel(obj.getPriority());
            levels.add(pLevel);
    	}    	
        pLevel.add(obj);
	}

	/**
     * Total amount of objects that this table contains.
     * @return Total amount of objects contained by this table.
     */
	public int size() {
        int suma = 0;
        for (PrioritizedLevel pLevel : levels)
            suma += pLevel.size();
		return suma;
	}
	
	/**
	 * Returns the index of the last object chosen in the specified level.
	 * @param levelIndex The index of the level
	 * @return The index of the last object chosen in the specified level.
	 */
	protected int getChosen(int levelIndex) {
		return levels.get(levelIndex).chosen;
	}
	
	/**
	 * Returns the next object chosen in the specified level. 
	 * @param levelIndex The index of the level where the object is located.
	 * @return The next object chosen in the specified level.
	 */
	protected T get(int levelIndex) {
		return levels.get(levelIndex).get();		
	}
    
	/**
	 * Returns the specified object in the specified level. 
	 * @param levelIndex The index of the level where the object is located.
	 * @param objIndex The index of the object
	 * @return The specified object in the specified level.
	 */
	protected T get(int levelIndex, int objIndex) {
		return levels.get(levelIndex).get(objIndex);		
	}
    
	/**
	 * Returns the amount of objects contained in a specified level.
	 * @param levelIndex The index of the level 
	 * @return The amount of objects contained in a specified level.
	 */
	protected int size(int levelIndex) {
		return levels.get(levelIndex).size();
	}

	/**
	 * Returns an iterator over this table. The iterator's behaviour is determined by the
	 * <code>type</code> parameter. 
	 * @param type Determines the way this table is traversed.
	 * @return An iterator over this table.
	 */
    public Iterator<T> iterator(IteratorType type) {
    	Iterator<T> iter = null;
    	switch(type) {
    		case NORMAL:
    			iter = new PrioritizedTableIterator<T>(this); break;
    		case BALANCED:
    			iter = new BalancedPrioritizedTableIterator<T>(this); break;
    		case RANDOM:
    			iter = new RandomPrioritizedTableIterator<T>(this); break;
    	}
    	return iter;
    }

    /**
     * A level of the prioritized table. All the objects belonging to a level have
     * the same priority. A level stores a value with the last chosen object.
     * @author Iván Castilla Rodríguez
     */
    class PrioritizedLevel extends ArrayList<T> implements Orderable {
    	private static final long serialVersionUID = 1L;
    	/** Next object that can be chosen. */
        protected int chosen;
        /** Priority of this level. */
        protected int priority;
        
        /**
         * Creates a new level with priority <code>pri</code>
         * @param pri Priority of this level.
         */
        PrioritizedLevel(int pri) {
            super();
            chosen = 0;
            priority = pri;
        }

        /**
		 * Returns the next object chosen in the this level. This method also increases
		 * the value of the <code>chosen</code> object. 
		 * @return The next object chosen in the this level.
         */
        public T get() {
            T obj = get(chosen);
            chosen = (chosen + 1) % size();
            return obj;
        }

        /**
         * Returns the priority of the level.
         * @return The priority of the level.
         */
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

}
