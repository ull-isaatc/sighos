package es.ull.isaatc.util;


/**
 * Clase gen�rica que construye un pool de Threads. 
 * Se puede establecer un m�ximo al n�mero de threads creado, pero por defecto 
 * no se pondr�.
 * @author Iv�n Castilla Rodr�guez
 */
public interface ThreadPool<T extends Runnable> {
    public void execute(T ev);
    
    public void shutdown();
    
    /**
     * Return the amount of threads of the pool. 
     * @return N�mero de threads del pool.
     */
    public int getNThreads();
}
