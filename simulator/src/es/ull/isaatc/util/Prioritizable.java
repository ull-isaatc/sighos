/*
 * Prioritizable.java
 *
 * Created on 18 de noviembre de 2005, 10:47
 */

package es.ull.isaatc.util;

/**
 * A prioritizable object can be used in a Prioritized Table.
 * @author Iván Castilla Rodríguez
 */
public interface Prioritizable {
	/** Returns the priority of the object */
    public int getPriority();
}
