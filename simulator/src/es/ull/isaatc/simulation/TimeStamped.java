/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * Indicates that an object has an associated timestamp.
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeStamped {
	/**
	 * Returns the timestamp of this object.
	 * @return timestamp of the object.
	 */
	double getTs();
	
}
