/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeStampedObject {
	/**
	 * Returns the simulation timestamp of this object.
	 * @return Simulation timestamp of the object.
	 */
	long getTs();
}
