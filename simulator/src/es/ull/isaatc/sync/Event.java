/*
 * Accion.java
 *
 * Created on 10 de junio de 2005, 11:38
 */

package es.ull.isaatc.sync;

/**
 *
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class Event {
    public abstract void event();
    protected void run() {
        event();
    }
}
