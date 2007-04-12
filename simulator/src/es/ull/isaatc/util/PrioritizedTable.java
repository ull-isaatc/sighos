
package es.ull.isaatc.util;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * An structure which contains a priority-ordered list. Objects with the same priority are
 * located in the same level of the structure. <p>Several iterators can be used to traverse this
 * structure. Each iterator gives a different level of equality of opportunity when accessing
 * objects with the same priority. 
 * @author Iván Castilla Rodríguez
 */
public abstract class PrioritizedTable<T extends Prioritizable> {
    /** Array of priority levels. */
    protected TreeMap<Integer, PrioritizedLevel> levels;
    /** Number of objects which this table contains. This value is updated when an object
     * is added or removed. */
    protected int nObj;
    
    /**
     * Initializes the levels of the structure.  
     */
    public PrioritizedTable() {
        levels = new TreeMap<Integer, PrioritizedLevel>();
    }
    
	/**
     * Inserts a new object in the table. The priority of the object determines its order.
     * @param obj New object with a priority value.
     */
    public void add(T obj) {
    	PrioritizedLevel pLevel = levels.get(obj.getPriority());
    	if (pLevel == null) {
            pLevel = new PrioritizedLevel(obj.getPriority());
            levels.put(obj.getPriority(), pLevel);
    	}    	
        pLevel.add(obj);
	}

	/**
     * Total amount of objects that this table contains.
     * @return Total amount of objects contained by this table.
     */
	public int size() {
		return nObj;
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
	public int size(int levelIndex) {
		return levels.get(levelIndex).size();
	}
	
	public String toString() {
		return levels.toString();
	}
	
    /**
     * A level of the prioritized table. All the objects belonging to a level have
     * the same priority. A level stores a value with the index of the next candidate 
     * object.
     * @author Iván Castilla Rodríguez
     */
    class PrioritizedLevel extends ArrayList<T> implements Comparable<PrioritizedLevel> {
    	private static final long serialVersionUID = 1L;
    	/** Next object that can be chosen. */
        protected int candidate;
        /** Priority of this level. */
        protected int priority;
        
        /**
         * Creates a new level with priority <code>pri</code>
         * @param pri Priority of this level.
         */
        PrioritizedLevel(int pri) {
            super();
            candidate = 0;
            priority = pri;
        }

        public boolean add(T obj) {
        	super.add(obj);
        	nObj++;
        	return true;
        }

        /**
		 * Returns the next candidate object in the this level. This method also increases
		 * the value of the <code>candidate</code> object. 
		 * @return The next object chosen in the this level.
         */
        public T get() {
            return get(candidate);
        }

        /**
		 * Returns the object with the specified index in the this level. This method also increases
		 * the value of the <code>candidate</code> object.
		 * @param index Index of the objecdt to return   
		 * @return The object in position <code>index</code> in the this level.
         */
        public T get(int index) {
            T obj = super.get(index);
            candidate = (index + 1) % size();
            return obj;
        }

        public T remove(int index) {
            T obj = super.remove(index);
            candidate = (size() == 0)? 0: index % size();
            nObj--;
        	return obj;
        }
        
        /**
         * Removes the object obj from the level
         * @param obj Object to be removed
         * @return True if the object exists in the level; false in other case.
         */
        public boolean remove(T obj) {
        	int ind = super.indexOf(obj);
        	if (ind == -1)
        		return false;
        	remove(ind);
        	return true;
        }
        
        /**
		 * @return Returns the index of next object that can be chosen.
		 */
		public int getCandidate() {
			return candidate;
		}

		public int compareTo(PrioritizedLevel o) {
			if (priority < o.priority)
				return -1;
			if (priority > o.priority)
				return 1;
			return 0;
		}        
    }

}
