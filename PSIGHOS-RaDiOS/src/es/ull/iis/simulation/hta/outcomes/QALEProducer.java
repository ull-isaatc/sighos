/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;

/**
 * Classes implementing this interface produce Quality Expected Life Expectancy (QALE)
 * @author Iván Castilla Rodríguez
 *
 */
public interface QALEProducer extends OutcomeProducer {
	/**
	 * Returns the QALE associated to the patient during the defined period
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the QALE
	 * @return the QALE associated to the current state of the patient and during the defined period
	 */
	public double getUtilityWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);
	/**
	 * Returns the annualized utility associated to the patient during the defined period. 
	 * Applies the punctual discount corresponding to each year to all the QALEs within such year. 
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the QALE
	 * @return the annual QALE associated to the current state of the patient and during the defined period
	 */
	public double[] getAnnualizedUtilityWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);

	/**
	 * Returns the initial QALE associated to the patient 
	 * @param pat A patient
	 * @param time Specific time when the QALE is applied (in years)
	 * @param discountRate The discount rate to apply to the QALE
	 * @return the initial QALE of this class
	 */
	public double getStartingUtility(Patient pat, double time, Discount discountRate);
}
