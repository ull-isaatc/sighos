/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientProfile;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;

/**
 * @author icasrod
 *
 */
public interface DiabetesPopulation {
	public DiabetesPatientProfile getPatientProfile();
	public DiabetesType getType();
	/**
	 * Returns the minimum age for the patients
	 * @return the minimum age for the patients
	 */
	public int getMinAge();
}
