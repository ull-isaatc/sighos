/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientProfile;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;

/**
 * A class that can create patient profiles belonging to a population description
 * @author icasrod
 *
 */
public interface DiabetesPopulation {
	/**
	 * Returns a patient profile with characteristics generated according to the population
	 * @return a patient profile with characteristics generated according to the population
	 */
	public DiabetesPatientProfile getPatientProfile();
	/**
	 * Returns the diabetes type
	 * @return the diabetes type
	 */
	public DiabetesType getType();
	/**
	 * Returns the minimum age for the patients
	 * @return the minimum age for the patients
	 */
	public int getMinAge();
}
