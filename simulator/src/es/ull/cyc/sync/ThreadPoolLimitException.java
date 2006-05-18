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
   
    /** Creates a new instance of ThreadPoolLimitException */
    public ThreadPoolLimitException() {
        super("No se pueden añadir más threads al pool. Límite alcanzado");
    }
    
}
