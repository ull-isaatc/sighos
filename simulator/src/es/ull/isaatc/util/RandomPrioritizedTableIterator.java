/**
 * 
 */
package es.ull.isaatc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for pools that allows the programmer to randomly traverse the prioritized table. A 
 * <code>RandomPrioritizedTableIterator</code> starts at level 0 (the higher priority) and returns
 * a new object of this level each time <code>next</code> is called in a random way. The order the 
 * objects of a level are returned is determined by a random permutation. 
 * When the iterator reaches the end of the level, it starts the next level. 
 * @author Iván Castilla Rodríguez
 */
public class RandomPrioritizedTableIterator<T extends Prioritizable>  implements Iterator<T> {
    /** Index of the level which is being examinated */    
    protected int levelIndex = 0;
    /** Traversed prioritized table */    
    protected PrioritizedTable<T> table;
    /** Visit order for this level. */
    int []order;
    /** Current object. */
    int current = 0;

    /**
     * Creates a random iterator for the prioritized table.
     * @param table Traversed prioritized table.
     */
	public RandomPrioritizedTableIterator(PrioritizedTable<T> table) {
        this.table = table;
        if (table.size() > 0)
        	order = RandomPermutation.nextPermutation(table.size(0));
	}

    /**
     * Returns the next object of the prioritized table. This method may be called repeatedly to iterate 
     * through the prioritized table.
     * @return The next object of the prioritized table. 
     */
    public T next() {
        // The end has been reached
        if (levelIndex >= table.levels.size())
        	throw new NoSuchElementException();
        // Next object of the current level
        T obj = table.get(levelIndex, order[current++]);
        // The level has been finished
        if (current == order.length) {
            levelIndex++;
            if (levelIndex < table.levels.size()) {
            	// Computes the next permutation
                order = RandomPermutation.nextPermutation(table.size(levelIndex));
                current = 0;
            }
        }
        return obj;
    }

	public boolean hasNext() {
        if (levelIndex >= table.levels.size())
            return false;
		return true;
	}

	public void remove() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
