/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * Indicates that the state of an object can be stored and restored.
 * @author Iván Castilla Rodríguez
 *
 */
public interface RecoverableState<T extends State> {
	/**
	 * Returns the information required to save the state of this object. 
	 * @return The state of the object.
	 */
	T getState();
	
	/**
	 * Restablish the state of this object.
	 * @param state Stored state.
	 */
	void setState(T state);
}
