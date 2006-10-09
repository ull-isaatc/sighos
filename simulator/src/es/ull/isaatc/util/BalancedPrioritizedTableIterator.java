/*
 * PrioritizedTableIterator.java
 *
 * Created on 18 de noviembre de 2005, 10:46
 */

package es.ull.isaatc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that allows the programmer to traverse a prioritized table. The balance is
 * provided by starting at a different component each time a level is reached. Thus, the first time
 * the level #1 is reached, the 0th component is treated as the first component of the level; the
 * second time, it is the 1st component, and so on.
 * @author Iván Castilla Rodríguez
 */
public class BalancedPrioritizedTableIterator<T extends Prioritizable> implements Iterator<T> {
    /** Index of the level which is being examinated. */   
    protected int levelIndex = 0;
    /** Index of the first object of the level. This value is used for determining when the iterator has reached the end of the level. */    
    protected int baseLevel;
    /** Traversed prioritized table. */    
    protected PrioritizedTable<T> table;
    
    /**
     * Creates an iterator for the prioritized table.
     * @param table Traversed prioritized table.
     */
    public BalancedPrioritizedTableIterator(PrioritizedTable<T> table) {
        this.table = table;
        if (table.size() > 0)
        	baseLevel = table.getChosen(0);
    }

    /**
     * Returns the next object of the prioitized table. This method makes use of the previously chosen
     * object of a level. Thus, every time this method starts a level, it's started at a different point.
     * @return The next object of the prioritized table. 
     */
    public T next() {
        // The end has been reached
        if (levelIndex >= table.levels.size())
            throw new NoSuchElementException();
        // Next object of the current level        
        T obj = table.get(levelIndex);
        // The level has been finished
        if (table.getChosen(levelIndex) == baseLevel) {
        	// The next time this method will start this level at the next object 
        	table.get(levelIndex);
            levelIndex++;
            if (levelIndex < table.levels.size())
            	baseLevel = table.getChosen(levelIndex);
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
