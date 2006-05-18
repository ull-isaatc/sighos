/*
 * PrioritizedTableIterator.java
 *
 * Created on 18 de noviembre de 2005, 10:46
 */

package es.ull.cyc.util;

/**
 * An iterator for pools that allows the programmer to traverse the pool. A 
 * <code>PrioritizedTableIterator</code> starts at level 0 (the higher priority) and returns
 * a new object of this level each time <code>next</code> is called. When the 
 * iterator reaches the end of the level, it starts the next level.
 * @author Iván Castilla Rodríguez
 */
public class PrioritizedTableIterator {
    /** Index of the level which is being examinated. */   
    protected int levelIndex = 0;
    /** Index of the first object of the level. This value is used for determining when the iterator has reached the end of the level. */    
    protected int baseLevel;
    /** Traversed pool. */    
    protected PrioritizedTable table;
    /** Level which is being examinated. */    
    protected PrioritizedLevel level;
    
    /**
     * Creates an iterator for the pool.
     * @param table Traversed pool.
     */
    public PrioritizedTableIterator(PrioritizedTable table) {
        this.table = table;
        level = (PrioritizedLevel) table.levels.get(0);
        baseLevel = level.chosen;
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
        Prioritizable obj = level.get();
        // Se comprueba si hemos dado la vuelta completa al nivel
        if (level.chosen == baseLevel) {
        	// MOD 23/03/06 Añadido para dar mayor balanceo de carga
        	level.get();
            levelIndex++;
            if (levelIndex < table.levels.size()) {
                level = (PrioritizedLevel) table.levels.get(levelIndex);
                baseLevel = level.chosen;
            }
        }
        return obj;
    }
}
