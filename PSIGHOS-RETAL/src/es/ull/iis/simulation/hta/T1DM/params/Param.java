/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

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
	T getValue(T1DMPatient pat);
	
}
