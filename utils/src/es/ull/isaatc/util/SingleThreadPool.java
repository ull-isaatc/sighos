package es.ull.isaatc.util;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

/**
 * Clase genérica que construye un pool de Threads. 
 * Se puede establecer un máximo al número de threads creado, pero por defecto 
 * no se pondrá.
 * La idea es tener un bucle infinito con el thread inicialmente bloqueado. 
 * Cada vez que se le da un valor a la acción (setAccion) se desbloquea el 
 * thread y la ejecuta. En cuanto termina la ejecución de la acción se pone a 
 * null y vuelve a bloquearse.
 * Para terminar la ejecución del thread se le asocia una AccionTermina.
 * @author Iván Castilla Rodríguez
 */
public class SingleThreadPool<T extends Runnable> extends Thread implements ThreadPool<T> {
    /** Finished flag */
    protected boolean finished = false;
    /** Events that cannot be executed because there are no free threads */
    protected ArrayDeque<T> pending;
    /** Bloqueo para que no esté en un bucle infinito, sino que pare cada vez 
     * que no tenga una acción asociada. */
    Semaphore pLock;
    
    /** 
     * Crea una nueva instancia de un ThreadPool limitando el máximo número de
     * threads que se puede crear.
     * @param maxThreads Número máximo de threads permitido por el usuario
     */
    public SingleThreadPool() {
    	super();
        pending = new ArrayDeque<T>();
        pLock = new Semaphore(0);
        start();
    }

    /**
     * Bucle principal de ejecución del thread.
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
     * Busca un thread disponible para realizar una acción. Establece la acción actual del thread. 
     * El control de que no tenga una acción asociada ya se hace desde fuera. Asociar una acción desbloquea al 
     * thread.
     * @param ev Acción que debe realizar el thread
     * @return El thread que realiza la acción
     * @throws ThreadPoolLimitException Si se llegó al máximo permitido de threads
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
