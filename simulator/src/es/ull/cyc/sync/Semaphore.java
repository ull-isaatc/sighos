package es.ull.cyc.sync;

/**
 * Clase que sirve como mecanismo de sincronizaci�n.
 * Implementa la interfaz serializable para permitir su uso con javaparty
 * @author Carlos Martin Galan
 */
public class Semaphore {
    /** Contador para controlar los bloqueos */
	protected int count;

    /**
     * Constructor del sem�foro
     * @param initCount Valor inicial del contador
     */
	public Semaphore(int initCount)  {
		if (initCount < 0)
			System.err.println("Error inicializando semaforo en valor negativo");
		count = initCount;
	}

    /**
     * Constructor del sem�foro
     */
	public Semaphore() { count = 0;  }

    /**
     * Indica al sem�foro que espere
     */
	public synchronized void waitSemaphore() {
		while ( count == 0 )
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		count--;
	}

    /**
     * Indica al sem�foro que contin�e
     */
	public synchronized void signalSemaphore() {
		count++;
		notify();
	}
}
