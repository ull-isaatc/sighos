package es.ull.isaatc.util;


/**
 * Clase genérica que construye un pool de Threads. 
 * Se puede establecer un máximo al número de threads creado, pero por defecto 
 * no se pondrá.
 * @author Iván Castilla Rodríguez
 */
public interface ThreadPool<T extends Runnable> {
    public void execute(T ev);
    
    public void shutdown();
    
    /**
     * Return the amount of threads of the pool. 
     * @return Número de threads del pool.
     */
    public int getNThreads();
}
