/*
 * ThreadPoolLimitException.java
 *
 * Created on 14 de junio de 2005, 11:59
 */

package es.ull.cyc.sync;

/**
 *
 * @author Iván Castilla Rodríguez
 */
public class ThreadPoolLimitException extends Exception {
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 6376140003034929924L;

	/** Creates a new instance of ThreadPoolLimitException */
    public ThreadPoolLimitException() {
        super("No se pueden añadir más threads al pool. Límite alcanzado");
    }
    
}
