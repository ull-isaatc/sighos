/*
 * PrioritizedTableIterator.java
 *
 * Created on 18 de noviembre de 2005, 10:46
 */

package es.ull.isaatc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that allows the programmer to traverse the prioritized table. A 
 * <code>PrioritizedTableIterator</code> starts at level 0 (the higher priority) and returns
 * a new object of this level each time <code>next</code> is called. When the 
 * iterator reaches the end of the level, it starts the next level.
 * @author Iván Castilla Rodríguez
 */
public class PrioritizedTableIterator<T extends Prioritizable> implements Iterator<T> {
    /** Index of the level which is being examinated. */   
    protected int levelIndex = 0;
    /** Index of the current object of the level. */    
    protected int objectIndex = 0;
    /** Traversed prioritized table. */    
    protected PrioritizedTable<T> table;
    
    /**
     * Creates an iterator for the prioritized table.
     * @param table Traversed prioritized table.
     */
    public PrioritizedTableIterator(PrioritizedTable<T> table) {
        this.table = table;
    }

    /**
     * Returns the next object of the prioritized table. This method may be called repeatedly to iterate 
     * through the prioritized table.
     * @return The next object of the prioritized table. 
     */
    public T next() {
        // Checks if there aren't more objects left
        if (levelIndex >= table.levels.size())
            throw new NoSuchElementException();
        // Next object        
        T obj = table.get(levelIndex, objectIndex++);
        // Checks if the level has been completed
        if (objectIndex == table.levels.get(levelIndex).size())
            levelIndex++;
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
