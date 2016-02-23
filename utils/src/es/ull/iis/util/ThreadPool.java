package es.ull.iis.util;


/**
 * A generic interface to create a pool of threads. A pool of threads consists on 1 to N threads which
 * can execute tasks. The idea is to reuse the same threads once and again instead of being creating a
 * new thread per task.
 * @author Iván Castilla Rodríguez
 */
public interface ThreadPool<T extends Runnable> {
	/** 
	 * Adds a new task to be executed in the pool.
	 * @param ev Task to be executed
	 */
    public void execute(T ev);
    
    /**
     * Stops the pool.
     */
    public void shutdown();
    
    /**
     * Return the amount of threads of the pool. 
     * @return The amount of threads of the pool.
     */
    public int getNThreads();
}
