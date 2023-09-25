/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * A parameter capable of cancelling the last generated value
 * @author Iván Castilla
 *
 */
public interface CancellableParam<T> extends Param<T> {
	/**
	 * Tells the parameter to reuse the last values generated 
	 * @param pat A patient
	 */
	public void cancelLast(Patient pat);
}
