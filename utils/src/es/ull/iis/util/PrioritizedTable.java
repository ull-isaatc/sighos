
package es.ull.iis.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A level of the prioritized map. All the objects belonging to a level have
 * the same priority. A level stores a value with the index of the next candidate 
 * object.
 * @author Iván Castilla Rodríguez
 */
class PrioritizedLevel<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	/** Next object that can be chosen. */
    protected int candidate;
    
    /**
     * Creates a new level
     */
    PrioritizedLevel() {
        super();
        candidate = 0;
    }

    /**
	 * Returns the next candidate object in the this level. This method also increases
	 * the value of the <code>candidate</code> object. 
	 * @return The next object chosen in the this level.
     */
    public E get() {
        return get(candidate);
    }

    /**
	 * Returns the object with the specified index in the this level. This method also increases
	 * the value of the <code>candidate</code> object.
	 * @param index Index of the objecdt to return   
	 * @return The object in position <code>index</code> in the this level.
     */
    public E get(int index) {
        E obj = super.get(index);
        candidate = (index + 1) % size();
        return obj;
    }

    public E remove(int index) {
        E obj = super.remove(index);
        candidate = (size() == 0)? 0: index % size();
    	return obj;
    }
    
    /**
	 * @return Returns the index of next object that can be chosen.
	 */
	public int getCandidate() {
		return candidate;
	}
}

/**
 * An structure which contains a priority-ordered list. Objects with the same priority are
 * located in the same level of the structure. <p>Several iterators can be used to traverse this
 * structure. Each iterator gives a different level of equality of opportunity when accessing
 * objects with the same priority. 
 * @author Iván Castilla Rodríguez
 */
public class PrioritizedTable<E extends Prioritizable> extends PrioritizedMap<PrioritizedLevel<E>, E> {
    
    /**
     * Initializes the levels of the structure.  
     */
    public PrioritizedTable() {
    	super();
    }
    
	@Override
	public PrioritizedLevel<E> createLevel(Integer priority) {
		return new PrioritizedLevel<E>();
	}
	
	/**
	 * Returns an iterator over this table which starts at a different component each time a level 
	 * is reached.  
	 * @return A balanced iterator over this table.
	 */
    public Iterator<E> balancedIterator() {
    	return new BalancedIterator();
    }

	/**
	 * Returns an iterator over this table which goes through each level randomly.  
	 * @return A random iterator over this table.
	 */
    public Iterator<E> randomIterator() {
    	return new RandomIterator();
    }

    /**
     * An iterator that allows the programmer to traverse a prioritized table. The balance is
     * provided by starting at a different component each time a level is reached. Thus, the first time
     * the level #1 is reached, the 0th component is treated as the first component of the level; the
     * second time, it is the 1st component, and so on.
     * @author Iván Castilla Rodríguez
     */
    private class BalancedIterator implements Iterator<E> {
        /** Main iterator level by level of the external estructure. */   
        private Iterator<PrioritizedLevel<E>> outIter;
        /** The current level being visited */
        private PrioritizedLevel<E> currentLevel = null;
        /** Number of objects of the level. This value is used for determining when the iterator has reached the end of the level. */    
        private int nObjects;
        
        /**
         * Creates an iterator for the prioritized table.
         */
        public BalancedIterator() {
        	outIter = levels.values().iterator();
        	if (outIter.hasNext()) {
        		currentLevel = outIter.next();
            	nObjects = currentLevel.size();
        	}
        }

        /**
         * Returns the next object of the prioritized map. This method makes use of the previously chosen
         * object of a level. Thus, every time this method starts a level, it's started at a different point.
         * @return The next object of the prioritized table. 
         */
        public E next() {
            // Next object of the current level        
            E obj = currentLevel.get();
            nObjects--;
            // The level has been finished
            if (nObjects == 0) {
            	// Next time this method will start this level at the next object 
            	currentLevel.get();
            	if (outIter.hasNext()) {
            		currentLevel = outIter.next();
                	nObjects = currentLevel.size();
            	}
            }
            return obj;
        }

    	public boolean hasNext() {
            if (currentLevel != null)
            	if (nObjects == 0)
            		return true;
    		return false;
    	}

    	public void remove() {
    		throw new UnsupportedOperationException("Not implemented");
    	}
    }
    
    /**
     * An iterator for pools that allows the programmer to randomly traverse the prioritized map.  
     * A <code>RandomIterator</code> starts at level 0 (the highest priority) and returns a new 
     * object of this level each time <code>next</code> is called in a random way. The order the 
     * objects of a level are returned is determined by a random permutation. 
     * When the iterator reaches the end of the level, it starts the next level. 
     * @author Iván Castilla Rodríguez
     */
    private class RandomIterator implements Iterator<E> {
        /** Main iterator level by level of the external estructure. */   
        private Iterator<PrioritizedLevel<E>> outIter;
        /** The current level being visited */
        private PrioritizedLevel<E> currentLevel = null;
        /** Visit order for this level. */
        private int []order;
        /** Current object. */
        private int current = 0;

        /**
         * Creates a random iterator for the prioritized table.
         */
    	public RandomIterator() {
        	outIter = levels.values().iterator();
        	if (outIter.hasNext()) {
        		currentLevel = outIter.next();
            	order = RandomPermutation.nextPermutation(currentLevel.size());
        	}
    	}

        /**
         * Returns the next object of the prioritized map. This method may be called repeatedly to iterate 
         * through the prioritized map.
         * @return The next object of the prioritized map. 
         */
        public E next() {
            // Next object of the current level
            E obj = currentLevel.get(order[current++]);
            // The level has been finished
            if (current == order.length) {
				if (outIter.hasNext()) {
					currentLevel = outIter.next();
	            	order = RandomPermutation.nextPermutation(currentLevel.size());
	            	current = 0;
				}
				else
					currentLevel = null;
            }
            return obj;
        }

    	public boolean hasNext() {
            if (currentLevel != null)
            	if (current < order.length)
            		return true;
    		return false;
    	}

    	public void remove() {
    		throw new UnsupportedOperationException("Not implemented");
    	}
    }
}
