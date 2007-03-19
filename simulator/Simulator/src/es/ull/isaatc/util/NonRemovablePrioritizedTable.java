/**
 * 
 */
package es.ull.isaatc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A prioritized table intended for be used as a static container, that is, the contents of the
 * table are never removed.
 * @author Iván Castilla Rodríguez
 *
 */
public class NonRemovablePrioritizedTable<T extends Prioritizable> extends PrioritizedTable<T> {
    /** Ways of iterating through the structure. <p><strong>FIFO</strong> indicates a sequential order.
     * <p><strong>BALANCED</strong> indicates every time the level is started at a different object.
     * <p><strong>RANDOM</strong> indicates a random permutation determines the order.
     **/
    public enum IteratorType {FIFO, BALANCED, RANDOM}

	/**
	 * 
	 */
	public NonRemovablePrioritizedTable() {
		super();
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
    		case BALANCED:
    			iter = new BalancedIterator(); break;
    		case RANDOM:
    			iter = new RandomIterator(); break;
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
        protected int levelIndex = 0;
        /** Index of the current object of the level. */    
        protected int objectIndex = 0;
        
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
            if (levelIndex >= levels.size())
                throw new NoSuchElementException();
            // Next object        
            T obj = get(levelIndex, objectIndex++);
            // Checks if the level has been completed
            if (objectIndex == levels.get(levelIndex).size()) {
                levelIndex++;
                objectIndex = 0;
            }
            return obj;
        }

    	public boolean hasNext() {
            if (levelIndex >= levels.size())
                return false;
    		return true;
    	}

    	public void remove() {
    		throw new UnsupportedOperationException("Not implemented");
    	}
    }

    /**
     * An iterator that allows the programmer to traverse a prioritized table. The balance is
     * provided by starting at a different component each time a level is reached. Thus, the first time
     * the level #1 is reached, the 0th component is treated as the first component of the level; the
     * second time, it is the 1st component, and so on.
     * @author Iván Castilla Rodríguez
     */
    private class BalancedIterator implements Iterator<T> {
        /** Index of the level which is being examinated. */   
        protected int levelIndex = 0;
        /** Index of the first object of the level. This value is used for determining when the iterator has reached the end of the level. */    
        protected int baseLevel;
        
        /**
         * Creates an iterator for the prioritized table.
         */
        public BalancedIterator() {
            if (size() > 0)
            	baseLevel = getCandidate(0);
        }

        /**
         * Returns the next object of the prioitized table. This method makes use of the previously chosen
         * object of a level. Thus, every time this method starts a level, it's started at a different point.
         * @return The next object of the prioritized table. 
         */
        public T next() {
            // The end has been reached
            if (levelIndex >= levels.size())
                throw new NoSuchElementException();
            // Next object of the current level        
            T obj = get(levelIndex);
            // The level has been finished
            if (getCandidate(levelIndex) == baseLevel) {
            	// Next time this method will start this level at the next object 
            	get(levelIndex);
                levelIndex++;
                if (levelIndex < levels.size())
                	baseLevel = getCandidate(levelIndex);
            }
            return obj;
        }

    	public boolean hasNext() {
            if (levelIndex >= levels.size())
                return false;
    		return true;
    	}

    	public void remove() {
    		throw new UnsupportedOperationException("Not implemented");
    	}
    }
    
    /**
     * An iterator for pools that allows the programmer to randomly traverse the prioritized table. A 
     * <code>RandomPrioritizedTableIterator</code> starts at level 0 (the higher priority) and returns
     * a new object of this level each time <code>next</code> is called in a random way. The order the 
     * objects of a level are returned is determined by a random permutation. 
     * When the iterator reaches the end of the level, it starts the next level. 
     * @author Iván Castilla Rodríguez
     */
    private class RandomIterator implements Iterator<T> {
        /** Index of the level which is being examinated */    
        protected int levelIndex = 0;
        /** Visit order for this level. */
        int []order;
        /** Current object. */
        int current = 0;

        /**
         * Creates a random iterator for the prioritized table.
         */
    	public RandomIterator() {
            if (size() > 0)
            	order = RandomPermutation.nextPermutation(size(0));
    	}

        /**
         * Returns the next object of the prioritized table. This method may be called repeatedly to iterate 
         * through the prioritized table.
         * @return The next object of the prioritized table. 
         */
        public T next() {
            // The end has been reached
            if (levelIndex >= levels.size())
            	throw new NoSuchElementException();
            // Next object of the current level
            T obj = get(levelIndex, order[current++]);
            // The level has been finished
            if (current == order.length) {
                levelIndex++;
                if (levelIndex < levels.size()) {
                	// Computes the next permutation
                    order = RandomPermutation.nextPermutation(size(levelIndex));
                    current = 0;
                }
            }
            return obj;
        }

    	public boolean hasNext() {
            if (levelIndex >= levels.size())
                return false;
    		return true;
    	}

    	public void remove() {
    		throw new UnsupportedOperationException("Not implemented");
    	}
    }

}
