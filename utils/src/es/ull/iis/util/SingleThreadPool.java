package es.ull.iis.util;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

/**
 * A single threaded pool of threads. This structure is intended to execute a set of tasks in a 
 * sequential order. 
 * This pool has a queue of waiting tasks and a semaphore to control the idle time.
 * @author Iván Castilla Rodríguez
 */
public class SingleThreadPool<T extends Runnable> extends Thread implements ThreadPool<T> {
    /** Finished flag */
    protected boolean finished = false;
    /** Events that cannot be executed because there are no free threads */
    protected ArrayDeque<T> pending;
    /** Lock to set the pool into the idle state */
    protected Semaphore pLock;
    /** An internal counter to set a different value to each pool */
    private static int count = 0;
    
    /** 
     * Creates a new single threaded pool of threads.
     */
    public SingleThreadPool() {
    	super("STP" + count++);
        pending = new ArrayDeque<T>();
        pLock = new Semaphore(0);
        start();
    }

    /**
     * Execution loop. The thread is initially put into the idle state. Once a task arrives, the thread is
     * awaken and executes it. As the task is finished, the thread becomes idle again. The loop repeats until
     * the <code>finished</code> flag is set to true.
     */
    public void run() {
    	T event = null;
        while (!finished) {
            try {
				pLock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (!pending.isEmpty()) {
				synchronized (pending) {
					event = pending.pop();					
				}
                event.run();
			}
        }
    }

    /**
     * Puts a task in the pending task queue. This action releases the semaphore in the main loop.
     * @param ev Task to be executed
     */    
	@Override
    public void execute(T ev) {
    	synchronized (pending) {
	    	pending.push(ev);			
		}
        pLock.release();
    }
    
	@Override
    public void shutdown() {
    	finished = true;
    	pLock.release();
    }

	@Override
	public int getNThreads() {
		return 1;
	}
    
}
