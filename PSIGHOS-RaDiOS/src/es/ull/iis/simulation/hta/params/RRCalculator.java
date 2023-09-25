/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * A class capable to compute relative risks for complications for a specific patient 
 * @author Iván Castilla Rodríguez
 *
 */
public interface RRCalculator {

	/**
	 * Returns the relative risk for the specified patient
	 * @param pat A patient
	 * @return the relative risk for the specified patient
	 */
	public abstract double getRR(Patient pat);
}
