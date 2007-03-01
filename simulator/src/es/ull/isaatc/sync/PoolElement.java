/*
 * ElementoPool.java
 *
 * Created on 10 de junio de 2005, 11:36
 */

package es.ull.isaatc.sync;

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
    Event event = null;
    /** Pool de threads al que est� asociado */
    ThreadPool tp;
    /** Bloqueo para que no est� en un bucle infinito, sino que pare cada vez 
     * que no tenga una acci�n asociada. */
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
     * @param tp Pool de threads al que est� asociada.
     */
    public PoolElement(ThreadPool tp) {
    	super("Pool Element " + tp.getNThreads());
        pLock = new Lock();
        this.tp = tp;
    }

    /**
     * Bucle principal de ejecuci�n del thread.
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
     * Establece la acci�n actual del thread. El control de que no tenga una 
     * acci�n asociada ya se hace desde fuera. Asociar una acci�n desbloquea al 
     * thread.
     * @param acc Nueva acci�n del thread.
     */
    public synchronized void setEvent(Event acc) {
        this.event = acc;
        pLock.unlock();
    }
    
    /**
     * Devuelve la acci�n que est� ejecutando actualmente el thread.
     * @return Acci�n en ejecuci�n por el thread.
     */
    public synchronized Event getEvent() {
        return event;
    }
    
    /**
     * Establece una acci�n de terminar para finalizar la ejecuci�n del thread.
     */
    public void finish() {
        setEvent(new FinishEvent());
    }

    /**
     * Esta acci�n es la acci�n de finalizaci�n del thread.
     */
    class FinishEvent extends Event {
        /** La acci�n est� vac�a */
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
