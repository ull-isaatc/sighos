/**
 * 
 */
package es.ull.isaatc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class RemovablePrioritizedTable<T extends Prioritizable> extends PrioritizedTable<T> {
    public enum IteratorType {FIFO}

	/**
	 * 
	 */
	public RemovablePrioritizedTable() {
		super();
	}

	/**
	 * Removes the first object from the level specified by levelIndex. 
	 * @param levelIndex Indicates the level to remove an object
	 * @return The first object of the level specified by levelIndex
	 */
	public T remove(int levelIndex) {
		return remove(levelIndex, 0);
	}
	
	/**
	 * Removes the object specified by objIndex from the level specified by levelIndex.
	 * @param levelIndex Indicates the level to remove an object
	 * @param objIndex Indicates the object to be removed
	 * @return The object specified by objIndex of the level specified by levelIndex
	 */
	public T remove(int levelIndex, int objIndex) {
		return levels.get(levelIndex).remove(objIndex);
	}
	
	/**
	 * Removes the object specified.
	 * @param obj The object to be removed
	 * @return true if the object was correctly removed; false in other case.
	 */
	public boolean remove(T obj) {		
    	PrioritizedLevel pLevel = levels.get(new Integer(obj.getPriority()));
    	if (pLevel != null)
        	return pLevel.remove(obj);
        return false;
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
    		case FIFO:
    			iter = new FIFOIterator(); break;
    	}
    	return iter;
    }

    /**
     * An iterator that allows the programmer to traverse the prioritized table. A 
     * <code>FIFOIterator</code> starts at level 0 (the higher priority) and returns
     * a new object of this level each time <code>next</code> is called. When the 
     * iterator reaches the end of the level, it starts the next level.
     * @author Iván Castilla Rodríguez
     */
    private class FIFOIterator implements Iterator<T> {
        /** Index of the level which is being examinated. */   
        private int levelIndex = 0;
        /** Index of the current object of the level. */    
        private int objectIndex = 0;
        /** A counter to compute how many objects have been visited */ 
        private int objectCount = 0;
        /** Previously taken object */
        private int[]previous = {-1, -1};
        
        
        /**
         * Creates an iterator for the prioritized table.
         */
        public FIFOIterator() {
        }

        /**
         * Returns the next object of the prioritized table. This method may be called repeatedly to iterate 
         * through the prioritized table.
         * @return The next object of the prioritized table. 
         */
        public T next() {
            // Checks if there aren't more objects left
            if (objectCount >= nObj)
                throw new NoSuchElementException();
            // Checks if the current level has been completed or it is empty
            boolean levelOk = false;
            while ((levelIndex < levels.size()) && !levelOk) {
            	if (size(levelIndex) == objectIndex) {
            		levelIndex++;
            		objectIndex = 0;
            	}
            	else
            		levelOk = true;
            }
            // Next object
            previous[0] = levelIndex;
            previous[1] = objectIndex;
            T obj = get(levelIndex, objectIndex++);
            objectCount++;
            return obj;
        }

    	public boolean hasNext() {
            if (objectCount >= nObj)
                return false;
    		return true;
    	}

    	public void remove() {
    		if (previous[0] != -1)
    			RemovablePrioritizedTable.this.remove(previous[0], previous[1]);
    	}
    }

}
