package es.ull.isaatc.util;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

/**
 * Clase gen�rica que construye un pool de Threads. 
 * Se puede establecer un m�ximo al n�mero de threads creado, pero por defecto 
 * no se pondr�.
 * La idea es tener un bucle infinito con el thread inicialmente bloqueado. 
 * Cada vez que se le da un valor a la acci�n (setAccion) se desbloquea el 
 * thread y la ejecuta. En cuanto termina la ejecuci�n de la acci�n se pone a 
 * null y vuelve a bloquearse.
 * Para terminar la ejecuci�n del thread se le asocia una AccionTermina.
 * @author Iv�n Castilla Rodr�guez
 */
public class SingleThreadPool<T extends Runnable> extends Thread implements ThreadPool<T> {
    /** Finished flag */
    protected boolean finished = false;
    /** Events that cannot be executed because there are no free threads */
    protected ArrayDeque<T> pending;
    /** Bloqueo para que no est� en un bucle infinito, sino que pare cada vez 
     * que no tenga una acci�n asociada. */
    Semaphore pLock;
    
    /** 
     * Crea una nueva instancia de un ThreadPool limitando el m�ximo n�mero de
     * threads que se puede crear.
     * @param maxThreads N�mero m�ximo de threads permitido por el usuario
     */
    public SingleThreadPool() {
    	super();
        pending = new ArrayDeque<T>();
        pLock = new Semaphore(0);
        start();
    }

    /**
     * Bucle principal de ejecuci�n del thread.
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
     * Busca un thread disponible para realizar una acci�n. Establece la acci�n actual del thread. 
     * El control de que no tenga una acci�n asociada ya se hace desde fuera. Asociar una acci�n desbloquea al 
     * thread.
     * @param ev Acci�n que debe realizar el thread
     * @return El thread que realiza la acci�n
     * @throws ThreadPoolLimitException Si se lleg� al m�ximo permitido de threads
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
