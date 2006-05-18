/*
 * PoolThreads.java
 *
 * Created on 2 de junio de 2005, 12:09
 */

package es.ull.cyc.sync;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Clase genérica que construye un pool de Threads. 
 * Se puede establecer un máximo al número de threads creado, pero por defecto 
 * no se pondrá.
 * @author Iván Castilla Rodríguez
 */
public class ThreadPool {
    /** List of threads in the pool */    
    protected ArrayList<PoolElement> pool;
    /** List of free threads */    
    protected LinkedList<PoolElement> freeThreads;
    /** Min amount of threads. This threads are created at the class constructor */
    protected int initThreads; 
    /** Max amount of threads */    
    protected int maxThreads = Integer.MAX_VALUE;
    /** Finished flag */
    protected boolean finished = false;
    /** Events that cannot be executed because there are no free threads */
    protected Vector<Event> pending;
    
    /** Crea una nueva instancia de un ThreadPool */
    public ThreadPool() {
    	super();
        pool = new ArrayList<PoolElement>();
        freeThreads = new LinkedList<PoolElement>();
        pending = new Vector<Event>();
    }

    /** 
     * Crea una nueva instancia de un ThreadPool limitando el máximo número de
     * threads que se puede crear.
     * @param maxThreads Número máximo de threads permitido por el usuario
     */
    public ThreadPool(int maxThreads) {
        this(0, maxThreads);
    }

    /**
     *
     * Crea una nueva instancia de un ThreadPool limitando el máximo número de
     * threads que se puede crear y creando varios threads por defecto.
     * @param initThreads Número inicial de threads en el pool
     * @param maxThreads Número máximo de threads permitido por el usuario
     */
    public ThreadPool(int initThreads, int maxThreads) {
    	super();
        if (maxThreads <= 0)
            throw new IllegalArgumentException("maxThreads debe ser > 0");
        if (initThreads > maxThreads)
            throw new IllegalArgumentException("initThreads debe ser <= maxThreads");
        pool = new ArrayList<PoolElement>();
        freeThreads = new LinkedList<PoolElement>();
        this.maxThreads = maxThreads;
        this.initThreads = initThreads;
        for (int i = 0; i < initThreads; i++)
            addThreadSafe();
        pending = new Vector<Event>();
    }

    /**
     * Añade un nuevo thread al pool
     * @return El nuevo thread añadido
     * @throws ThreadPoolLimitException Si no puede agregar más threads por haber llegado al máximo permitido
     */
    private PoolElement addThread() throws ThreadPoolLimitException {
        if (pool.size() == maxThreads)
            throw new ThreadPoolLimitException();
        //System.out.println("Nuevo thread añadido " + pool.size());
        return addThreadSafe();
    }
    
    /**
     * Versión segura de addThread. Sólo debe usarse cuando no haya riesgo de superar
     * el máximo de threads permitido.
     * @return El nuevo thread añadido
     */    
    private PoolElement addThreadSafe() {
        PoolElement elem = new PoolElement(this);
        pool.add(elem);
        elem.start();
        freeThreads.add(elem);
        return elem;        
    }

    /**
     * Devuelve un thread concreto del pool
     * @param ind Índice del thread a devolver
     * @return El thread indicado con el índice
     */    
    public PoolElement getPoolElement(int ind) {
        return pool.get(ind);
    }
    
    /**
     * Busca un thread disponible para realizar una acción
     * @param ev Acción que debe realizar el thread
     * @return El thread que realiza la acción
     * @throws ThreadPoolLimitException Si se llegó al máximo permitido de threads
     */    
    public synchronized void getThread(Event ev) {
        try {
            PoolElement elem = null;
            if (freeThreads.isEmpty())
                elem = addThread();
            elem = freeThreads.removeFirst();
            elem.setEvent(ev);
		} catch (ThreadPoolLimitException e) {
	    	pending.add(ev);
		}
    }
    
    /**
     * Sets a thread as a free one.
     * @param elem The new free thread.
     */    
    public synchronized void freeThread(PoolElement elem) {
        if (pending.size() == 0) {
            freeThreads.add(elem);
        	if (finished && (freeThreads.size() == pool.size()))
        		finishAll();
        }
        else {
        	Event ev = pending.remove(0);
            elem.setEvent(ev);        	
        }
    }

    /**
	 * @return Returns the initThreads.
	 */
	public int getInitThreads() {
		return initThreads;
	}

	/**
     * Getter for property maxThreads.
     * @return Value of property maxThreads.
     */
    public int getMaxThreads() {
        return maxThreads;
    }
    
    /**
     * Setter for property maxThreads.
     * @param maxThreads New value of property maxThreads.
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }
    
    /**
     * Starts the finish sequence.
     */
    // 22/01/06 He sincronizado también este método
    public synchronized void finish() {
    	finished = true;
    	if (freeThreads.size() == pool.size())
    		finishAll();
    }

    /**
     * Sends a finish event to all the threads of the pool.
     */
    // FIXME Sólo está como protected para el ejemplo. Debería ser private 
    protected void finishAll() {
        for(int i = 0; i < pool.size(); i++) {
            PoolElement elem = pool.get(i);
            elem.finish();
        }         
    }

    /**
     * Return the amount of threads of the pool. 
     * @return Número de threads del pool.
     */
    public int getNThreads() {
        return pool.size();
    }
}
