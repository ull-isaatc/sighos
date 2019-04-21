/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientProfile;

/**
 * @author icasrod
 *
 */
public interface DiabetesPopulation {
	public enum Type {
		T1,
		T2
	}
	public DiabetesPatientProfile getPatientProfile();
	public Type getType();
	/**
	 * Returns the minimum age for the patients
	 * @return the minimum age for the patients
	 */
	public int getMinAge();
}
