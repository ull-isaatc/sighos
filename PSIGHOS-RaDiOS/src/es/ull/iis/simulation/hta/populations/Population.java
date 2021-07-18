/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.PatientProfile;

/**
 * A class that can create patient profiles belonging to a population description
 * @author icasrod
 *
 */
public interface Population extends CreatesSecondOrderParameters {
	/**
	 * Returns a patient profile with characteristics generated according to the population
	 * @return a patient profile with characteristics generated according to the population
	 */
	public PatientProfile getPatientProfile(DiseaseProgressionSimulation simul);
	/**
	 * Returns the minimum age for the patients
	 * @return the minimum age for the patients
	 */
	public int getMinAge();
	/**
	 * Returns the maximum age for the patients
	 * @return the maximum age for the patients
	 */
	public int getMaxAge();

}
