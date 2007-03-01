/*
 * ElementoPool.java
 *
 * Created on 10 de junio de 2005, 11:36
 */

package es.ull.isaatc.sync;

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
    Event event = null;
    /** Pool de threads al que está asociado */
    ThreadPool tp;
    /** Bloqueo para que no esté en un bucle infinito, sino que pare cada vez 
     * que no tenga una acción asociada. */
    Lock pLock;
    // Control para debug
    /** Executed events */
    int nEvents = 0;
    /** Initial time */
    long tIni;
    /** Accum. time */
    long tAcc;
    
    /** 
     * Crea una nueva instanciaElementoPool.
     * @param tp Pool de threads al que está asociada.
     */
    public PoolElement(ThreadPool tp) {
    	super("Pool Element " + tp.getNThreads());
        pLock = new Lock();
        this.tp = tp;
    }

    /**
     * Bucle principal de ejecución del thread.
     */
    public void run() {
        boolean termina = false;
        // DEBUG
        tIni = System.currentTimeMillis();
        try {
            while (!termina) {
                pLock.lock();
                long tAux = System.currentTimeMillis();
                if (event instanceof FinishEvent)
                    termina = true;
                else {
                    nEvents++;
                    event.run();
                    event = null;
                    tp.freeThread(this);
                }
                tAcc += (System.currentTimeMillis() - tAux);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Establece la acción actual del thread. El control de que no tenga una 
     * acción asociada ya se hace desde fuera. Asociar una acción desbloquea al 
     * thread.
     * @param acc Nueva acción del thread.
     */
    public synchronized void setEvent(Event acc) {
        this.event = acc;
        pLock.unlock();
    }
    
    /**
     * Devuelve la acción que está ejecutando actualmente el thread.
     * @return Acción en ejecución por el thread.
     */
    public synchronized Event getEvent() {
        return event;
    }
    
    /**
     * Establece una acción de terminar para finalizar la ejecución del thread.
     */
    public void finish() {
        setEvent(new FinishEvent());
    }

    /**
     * Esta acción es la acción de finalización del thread.
     */
    class FinishEvent extends Event {
        /** La acción está vacía */
        public void event() {}        
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
