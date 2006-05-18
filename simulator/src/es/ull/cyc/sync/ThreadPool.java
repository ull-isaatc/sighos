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
 * Clase gen�rica que construye un pool de Threads. 
 * Se puede establecer un m�ximo al n�mero de threads creado, pero por defecto 
 * no se pondr�.
 * @author Iv�n Castilla Rodr�guez
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
     * Crea una nueva instancia de un ThreadPool limitando el m�ximo n�mero de
     * threads que se puede crear.
     * @param maxThreads N�mero m�ximo de threads permitido por el usuario
     */
    public ThreadPool(int maxThreads) {
        this(0, maxThreads);
    }

    /**
     *
     * Crea una nueva instancia de un ThreadPool limitando el m�ximo n�mero de
     * threads que se puede crear y creando varios threads por defecto.
     * @param initThreads N�mero inicial de threads en el pool
     * @param maxThreads N�mero m�ximo de threads permitido por el usuario
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
     * A�ade un nuevo thread al pool
     * @return El nuevo thread a�adido
     * @throws ThreadPoolLimitException Si no puede agregar m�s threads por haber llegado al m�ximo permitido
     */
    private PoolElement addThread() throws ThreadPoolLimitException {
        if (pool.size() == maxThreads)
            throw new ThreadPoolLimitException();
        //System.out.println("Nuevo thread a�adido " + pool.size());
        return addThreadSafe();
    }
    
    /**
     * Versi�n segura de addThread. S�lo debe usarse cuando no haya riesgo de superar
     * el m�ximo de threads permitido.
     * @return El nuevo thread a�adido
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
     * @param ind �ndice del thread a devolver
     * @return El thread indicado con el �ndice
     */    
    public PoolElement getPoolElement(int ind) {
        return pool.get(ind);
    }
    
    /**
     * Busca un thread disponible para realizar una acci�n
     * @param ev Acci�n que debe realizar el thread
     * @return El thread que realiza la acci�n
     * @throws ThreadPoolLimitException Si se lleg� al m�ximo permitido de threads
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
    // 22/01/06 He sincronizado tambi�n este m�todo
    public synchronized void finish() {
    	finished = true;
    	if (freeThreads.size() == pool.size())
    		finishAll();
    }

    /**
     * Sends a finish event to all the threads of the pool.
     */
    // FIXME S�lo est� como protected para el ejemplo. Deber�a ser private 
    protected void finishAll() {
        for(int i = 0; i < pool.size(); i++) {
            PoolElement elem = pool.get(i);
            elem.finish();
        }         
    }

    /**
     * Return the amount of threads of the pool. 
     * @return N�mero de threads del pool.
     */
    public int getNThreads() {
        return pool.size();
    }
}
