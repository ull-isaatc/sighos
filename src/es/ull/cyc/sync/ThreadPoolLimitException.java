/*
 * ThreadPoolLimitException.java
 *
 * Created on 14 de junio de 2005, 11:59
 */

package es.ull.cyc.sync;

/**
 *
 * @author Iv�n Castilla Rodr�guez
 */
public class ThreadPoolLimitException extends Exception {
   
    /** Creates a new instance of ThreadPoolLimitException */
    public ThreadPoolLimitException() {
        super("No se pueden a�adir m�s threads al pool. L�mite alcanzado");
    }
    
}
