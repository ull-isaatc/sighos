/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface TimeStampedObject {
	/**
	 * Returns the simulation timestamp of this object.
	 * @return Simulation timestamp of the object.
	 */
	long getTs();
}
