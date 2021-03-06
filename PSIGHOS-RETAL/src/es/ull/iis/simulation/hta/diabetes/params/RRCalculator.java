/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * A class capable to compute relative risks for complications for a specific patient 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface RRCalculator {

	/**
	 * Returns the relative risk for the specified patient
	 * @param pat A patient
	 * @return the relative risk for the specified patient
	 */
	public abstract double getRR(DiabetesPatient pat);
}
