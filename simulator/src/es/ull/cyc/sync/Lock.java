package es.ull.cyc.sync;

/**
 * Clase que sirve como mecanismo de sincronización.
 * Implementa la interfaz serializable para permitir su uso con javaparty
 * @author Carlos Martin Galan
 */
public class Lock {
	/** Contador para controlar los bloqueos */
	protected int counter;

    /**
     * Constructor del bloqueo
     */
	public Lock() {
		counter = 0;
	}

    /**
     * Establece un bloqueo
     */
	public synchronized void lock() throws InterruptedException {
		counter--;
		while (counter < 0) wait();
	}

    /**
     * Quita el bloquea
     */
	public synchronized void unlock() {
		counter++;
		if (counter == 0) notify();
	}

    /**
     * Permite saber si está bloqueado
     * @return el estado bloqueado (true) o no (false)
     */
    public synchronized boolean locked() {
        return (counter < 0);
    }
}
