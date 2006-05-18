/**
 * 
 */
package es.ull.cyc.util;

/**
 * An iterator for pools that allows the programmer to randomly traverse the pool. A 
 * <code>RandomPrioritizedTableIterator</code> starts at level 0 (the higher priority) and returns
 * a new object of this level each time <code>next</code> is called in a random way. 
 * When the iterator reaches the end of the level, it starts the next level. 
 * @author Iván Castilla Rodríguez
 */public class RandomPrioritizedTableIterator {
    /** Index of the level which is being examinated */    
    protected int levelIndex = 0;
    /** Traversed pool */    
    protected PrioritizedTable table;
    /** Level which is being examinated. */    
    protected PrioritizedLevel level;
    /** Visit order for this level. */
    int []order;
    /** Current object. */
    int current = 0;

    /**
     * Creates a random iterator for the pool.
     * @param table Traversed table.
     */
	public RandomPrioritizedTableIterator(PrioritizedTable table) {
        this.table = table;
        level = (PrioritizedLevel) table.levels.get(0);
        order = RandomPermutation.nextPermutation(level.size());
	}

    /**
     * Returns the next object of the pool. This method may be called repeatedly to iterate through the pool.
     * @return The next object of the pool. Null if there is no next object.
     */
    public Prioritizable next() {
        // Se comprueba si no quedan más elementos que recorrer
        if (levelIndex >= table.levels.size())
            return null;
        // Se obtiene el siguiente elemento del nivel
        Prioritizable obj = (Prioritizable)level.get(order[current++]);
        // Se comprueba si hemos dado la vuelta completa al nivel
        if (current == order.length) {
            levelIndex++;
            if (levelIndex < table.levels.size()) {
                level = (PrioritizedLevel) table.levels.get(levelIndex);
                order = RandomPermutation.nextPermutation(level.size());
                current = 0;
            }
        }
        return obj;
    }
}
