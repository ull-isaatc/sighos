/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Param<T> {

	T getValue(T1DMPatient pat);
	
}
