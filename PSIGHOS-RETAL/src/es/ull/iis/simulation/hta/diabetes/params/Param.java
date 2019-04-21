/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

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
	T getValue(DiabetesPatient pat);
	
}
