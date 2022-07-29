/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.Patient;

/**
 * Classes implementing this interface produce utility values
 * @author Iván Castilla Rodríguez
 *
 */
public interface UtilityProducer extends OutcomeProducer {

	/**
	 * Returns the annual disutility associated to the patient 
	 * @param pat A patient
	 * @return the annual disutility of this class
	 */
	public double getAnnualDisutility(Patient pat);

	/**
	 * Returns the initial disutility associated to the patient 
	 * @param pat A patient
	 * @return the initial disutility of this class
	 */
	public double getStartingDisutility(Patient pat);
}
