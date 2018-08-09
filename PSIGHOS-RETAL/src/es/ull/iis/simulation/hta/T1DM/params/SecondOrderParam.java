/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface SecondOrderParam<T> extends Param<T> {

	/**
	 * Returns a probabilistic value for this parameter
	 * @param pat The patient that requires the parameter
	 * @return a probabilistic value for this parameter
	 */
	T getSecondOrderValue(T1DMPatient pat);
}
