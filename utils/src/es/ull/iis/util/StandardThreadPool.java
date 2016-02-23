package es.ull.iis.util;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A standard pool of threads, consisting on 1 to N threads to execute tasks. 
 * @author Iván Castilla Rodríguez
 */
public class StandardThreadPool<T extends Runnable> implements ThreadPool<T> {
    /** List of free threads */    
    protected ArrayDeque<PoolElement> freeThreads;
    /** Number of threads */    
    protected int nThreads;
    /** Finished flag */
    protected boolean finished = false;
    /** Events that cannot be executed because there are no free threads */
    protected ArrayDeque<T> pending;
    protected static StandardThreadPool<?> tp = null;
    protected static AtomicInteger assigned = new AtomicInteger(0);
    
    /** 
     * Creates a new pool of threads with <code>nThreads</code> threads.
     * @param nThreads Amount of internal threads in the pool
     */
    public StandardThreadPool(int nThreads) {
    	super();
        if (nThreads <= 0)
            throw new IllegalArgumentException("nThreads must be > 0");
        freeThreads = new ArrayDeque<PoolElement>();
        this.nThreads = nThreads;
        for (int i = 0; i < nThreads; i++) {
	        PoolElement p = new PoolElement(i);
	        p.start();
	        freeThreads.push(p);
        }
        pending = new ArrayDeque<T>();
    }

	@Override
    public synchronized void execute(T ev) {
        if (freeThreads.isEmpty())
	    	pending.push(ev);
        else {
        	PoolElement elem = freeThreads.pop();
        	elem.setEvent(ev);
        }
    }
    
	@Override
    public synchronized void shutdown() {
    	finished = true;
    	if (freeThreads.size() == nThreads) {
	        for (PoolElement elem : freeThreads) {
	            elem.finish();
	        }         
    	}
    }
    
    /**
     * Sets a thread as a free one.
     * @param elem The new free thread.
     */    
    protected synchronized void freeThread(PoolElement elem) {
        if (pending.isEmpty()) {
            freeThreads.push(elem);
        	if (finished && (freeThreads.size() == nThreads))
    	        for (PoolElement e : freeThreads)
    	            e.finish();
        }
        else
            elem.setEvent(pending.pop());        	
    }
    
	@Override
    public int getNThreads() {
        return nThreads;
    }

    @SuppressWarnings("unchecked")
	public static <T1 extends Runnable> StandardThreadPool<T1> getPool(int nThreads) {
    	if (tp == null) {
    		tp = new StandardThreadPool<T1>(nThreads) {
	    		@Override
	    		public synchronized void shutdown() {
	    	    	if (assigned.decrementAndGet() == 0) {
	    	    		super.shutdown();
	    	    		tp = null;
	    	    	}
	    		}
	    	};
    	}
    	assigned.incrementAndGet();
    	return (StandardThreadPool<T1>) tp;
    }
    
    /**
     * Each thread of the pool of threads. 
     * @author Iván Castilla Rodríguez
     */
    public class PoolElement extends Thread {
        T event = null;
        Semaphore pLock;
        
        public PoolElement(int index) {
        	super("" + index);
            pLock = new Semaphore(0);
        }

        public void run() {
            while (!finished) {
                try {
					pLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                if (event != null) {
	                event.run();
	                event = null;
	                StandardThreadPool.this.freeThread(this);
                }
            }
        }
        
        public void finish() {
        	pLock.release();
        }
        
        public void setEvent(T ev) {
            this.event = ev;
            pLock.release();
        }
        
    }
}
