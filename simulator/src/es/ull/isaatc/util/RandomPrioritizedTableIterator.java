/**
 * 
 */
package es.ull.isaatc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for pools that allows the programmer to randomly traverse the pool. A 
 * <code>RandomPrioritizedTableIterator</code> starts at level 0 (the higher priority) and returns
 * a new object of this level each time <code>next</code> is called in a random way. 
 * When the iterator reaches the end of the level, it starts the next level. 
 * @author Iván Castilla Rodríguez
 */
public class RandomPrioritizedTableIterator<T extends Prioritizable>  implements Iterator<T> {
    /** Index of the level which is being examinated */    
    protected int levelIndex = 0;
    /** Traversed pool */    
    protected PrioritizedTable<T> table;
    /** Visit order for this level. */
    int []order;
    /** Current object. */
    int current = 0;

    /**
     * Creates a random iterator for the pool.
     * @param table Traversed table.
     */
	public RandomPrioritizedTableIterator(PrioritizedTable<T> table) {
        this.table = table;
        if (table.size() > 0)
        	order = RandomPermutation.nextPermutation(table.size(0));
	}

    /**
     * Returns the next object of the pool. This method may be called repeatedly to iterate through the pool.
     * @return The next object of the pool. Null if there is no next object.
     */
    public T next() {
        // Se comprueba si no quedan más elementos que recorrer
        if (levelIndex >= table.levels.size())
        	throw new NoSuchElementException();
        // Se obtiene el siguiente elemento del nivel
        T obj = table.get(levelIndex, order[current++]);
        // Se comprueba si hemos dado la vuelta completa al nivel
        if (current == order.length) {
            levelIndex++;
            if (levelIndex < table.levels.size()) {
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
