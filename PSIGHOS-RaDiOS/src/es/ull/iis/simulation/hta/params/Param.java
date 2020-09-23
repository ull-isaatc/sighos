/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * A basic interface to represent simulation parameters
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Param<T> {

	/**
	 * Returns a specific value for the parameter
	 * @param pat A patient
	 * @return a specific value for the parameter
	 */
	T getValue(Patient pat);
	
}
