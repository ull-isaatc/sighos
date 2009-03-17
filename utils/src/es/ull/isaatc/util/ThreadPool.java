package es.ull.isaatc.util;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

/**
 * Clase gen�rica que construye un pool de Threads. 
 * Se puede establecer un m�ximo al n�mero de threads creado, pero por defecto 
 * no se pondr�.
 * @author Iv�n Castilla Rodr�guez
 */
public class ThreadPool<T extends Runnable> {
    /** List of free threads */    
    protected ArrayDeque<PoolElement> freeThreads;
    /** Number of threads */    
    protected int nThreads;
    /** Finished flag */
    protected boolean finished = false;
    /** Events that cannot be executed because there are no free threads */
    protected ArrayDeque<T> pending;
    
    /** 
     * Crea una nueva instancia de un ThreadPool limitando el m�ximo n�mero de
     * threads que se puede crear.
     * @param maxThreads N�mero m�ximo de threads permitido por el usuario
     */
    public ThreadPool(int maxThreads) {
    	super();
        if (maxThreads <= 0)
            throw new IllegalArgumentException("maxThreads debe ser > 0");
        freeThreads = new ArrayDeque<PoolElement>();
        this.nThreads = maxThreads;
        for (int i = 0; i < nThreads; i++) {
	        PoolElement p = new PoolElement();
	        p.start();
	        freeThreads.push(p);
        }
        pending = new ArrayDeque<T>();
    }

    /**
     * Busca un thread disponible para realizar una acci�n
     * @param ev Acci�n que debe realizar el thread
     * @return El thread que realiza la acci�n
     * @throws ThreadPoolLimitException Si se lleg� al m�ximo permitido de threads
     */    
    public synchronized void execute(T ev) {
        if (freeThreads.isEmpty())
	    	pending.push(ev);
        else {
        	PoolElement elem = freeThreads.pop();
        	elem.setEvent(ev);
        }
    }
    
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
    public synchronized void freeThread(PoolElement elem) {
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
    
    /**
     * Return the amount of threads of the pool. 
     * @return N�mero de threads del pool.
     */
    public int getNThreads() {
        return nThreads;
    }

    /**
     * Elemento del pool de threads. Este elemento funciona como un thread que 
     * ejecuta la acci�n que se le asocie en cada momento.
     * La idea es tener un bucle infinito con el thread inicialmente bloqueado. 
     * Cada vez que se le da un valor a la acci�n (setAccion) se desbloquea el 
     * thread y la ejecuta. En cuanto termina la ejecuci�n de la acci�n se pone a 
     * null y vuelve a bloquearse.
     * Para terminar la ejecuci�n del thread se le asocia una AccionTermina.
     * @author Iv�n Castilla Rodr�guez
     */
    public class PoolElement extends Thread {
        /** Acci�n que ejecuta el thread */
        T event = null;
        /** Bloqueo para que no est� en un bucle infinito, sino que pare cada vez 
         * que no tenga una acci�n asociada. */
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
        public PoolElement() {
            pLock = new Semaphore(0);
        }

        /**
         * Bucle principal de ejecuci�n del thread.
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
	                ThreadPool.this.freeThread(this);
	                tAcc += (System.currentTimeMillis() - tAux);
                }
            }
        }
        
        public void finish() {
        	termina = true;
        	pLock.release();
        }
        
        /**
         * Establece la acci�n actual del thread. El control de que no tenga una 
         * acci�n asociada ya se hace desde fuera. Asociar una acci�n desbloquea al 
         * thread.
         * @param acc Nueva acci�n del thread.
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
