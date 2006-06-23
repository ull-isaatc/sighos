/*
 * PrioritizedTableIterator.java
 *
 * Created on 18 de noviembre de 2005, 10:46
 */

package es.ull.isaatc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for pools that allows the programmer to traverse the pool. A 
 * <code>PrioritizedTableIterator</code> starts at level 0 (the higher priority) and returns
 * a new object of this level each time <code>next</code> is called. When the 
 * iterator reaches the end of the level, it starts the next level.
 * @author Iván Castilla Rodríguez
 */
public class PrioritizedTableIterator<T extends Prioritizable> implements Iterator<T> {
    /** Index of the level which is being examinated. */   
    protected int levelIndex = 0;
    /** Index of the first object of the level. This value is used for determining when the iterator has reached the end of the level. */    
    protected int baseLevel;
    /** Traversed pool. */    
    protected PrioritizedTable<T> table;
    
    /**
     * Creates an iterator for the pool.
     * @param table Traversed pool.
     */
    public PrioritizedTableIterator(PrioritizedTable<T> table) {
        this.table = table;
        if (table.size() > 0)
        	baseLevel = table.getChosen(0);
    }

    /**
     * Returns the next object of the pool. This method may be called repeatedly to iterate through the pool.
     * @return The next object of the pool. 
     */
    public T next() {
        // Se comprueba si no quedan más elementos que recorrer
        if (levelIndex >= table.levels.size())
            throw new NoSuchElementException();
        // Se obtiene el siguiente elemento del nivel        
        T obj = table.get(levelIndex);
        // Se comprueba si hemos dado la vuelta completa al nivel
        if (table.getChosen(levelIndex) == baseLevel) {
        	// MOD 23/03/06 Añadido para dar mayor balanceo de carga
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
