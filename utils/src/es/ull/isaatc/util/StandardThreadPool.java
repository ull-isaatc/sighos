package es.ull.isaatc.util;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase genérica que construye un pool de Threads. 
 * Se puede establecer un máximo al número de threads creado, pero por defecto 
 * no se pondrá.
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
     * Crea una nueva instancia de un ThreadPool limitando el máximo número de
     * threads que se puede crear.
     * @param maxThreads Número máximo de threads permitido por el usuario
     */
    public StandardThreadPool(int maxThreads) {
    	super();
        if (maxThreads <= 0)
            throw new IllegalArgumentException("maxThreads debe ser > 0");
        freeThreads = new ArrayDeque<PoolElement>();
        this.nThreads = maxThreads;
        for (int i = 0; i < nThreads; i++) {
	        PoolElement p = new PoolElement(i);
	        p.start();
	        freeThreads.push(p);
        }
        pending = new ArrayDeque<T>();
    }

    /**
     * Busca un thread disponible para realizar una acción
     * @param ev Acción que debe realizar el thread
     * @return El thread que realiza la acción
     * @throws ThreadPoolLimitException Si se llegó al máximo permitido de threads
     */    
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

	/**
     * Getter for property maxThreads.
     * @return Value of property maxThreads.
     */
    public int getMaxThreads() {
        return nThreads;
    }
    
	@Override
    public int getNThreads() {
        return nThreads;
    }

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
     * Elemento del pool de threads. Este elemento funciona como un thread que 
     * ejecuta la acción que se le asocie en cada momento.
     * La idea es tener un bucle infinito con el thread inicialmente bloqueado. 
     * Cada vez que se le da un valor a la acción (setAccion) se desbloquea el 
     * thread y la ejecuta. En cuanto termina la ejecución de la acción se pone a 
     * null y vuelve a bloquearse.
     * Para terminar la ejecución del thread se le asocia una AccionTermina.
     * @author Iván Castilla Rodríguez
     */
    public class PoolElement extends Thread {
        /** Acción que ejecuta el thread */
        T event = null;
        /** Bloqueo para que no esté en un bucle infinito, sino que pare cada vez 
         * que no tenga una acción asociada. */
        Semaphore pLock;
        // Control para debug
        /** Executed events */
        int nEvents = 0;
        /** Initial time */
        long tIni;
        /** Accum. time */
        long tAcc;
        boolean termina = false;
        
        /** 
         * Crea una nueva instanciaElementoPool.
         */
        public PoolElement(int index) {
        	super("" + index);
            pLock = new Semaphore(0);
        }

        /**
         * Bucle principal de ejecución del thread.
         */
        public void run() {
            tIni = System.currentTimeMillis();
            while (!termina) {
                try {
					pLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                if (event != null) {
                	long tAux = System.currentTimeMillis();
	                nEvents++;
	                event.run();
	                event = null;
	                StandardThreadPool.this.freeThread(this);
	                tAcc += (System.currentTimeMillis() - tAux);
                }
            }
        }
        
        public void finish() {
        	termina = true;
        	pLock.release();
        }
        
        /**
         * Establece la acción actual del thread. El control de que no tenga una 
         * acción asociada ya se hace desde fuera. Asociar una acción desbloquea al 
         * thread.
         * @param acc Nueva acción del thread.
         */
        public void setEvent(T acc) {
            this.event = acc;
            pLock.release();
        }
        
    	/**
    	 * DEBUG
    	 * @return Returns the nEvents.
    	 */
    	public int getNEvents() {
    		return nEvents;
    	}

    	/**
    	 * DEBUG
    	 * @return Returns the tAcc.
    	 */
    	public long getTAcc() {
    		return tAcc;
    	}

    	/**
    	 * DEBUG
    	 * @return Returns the tIni.
    	 */
    	public long getTIni() {
    		return tIni;
    	}
    }
}
