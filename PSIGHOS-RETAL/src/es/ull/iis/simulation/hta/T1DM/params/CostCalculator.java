/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface CostCalculator {
	/**
	 * Return the annual cost for the specified patient during a period of time. The initAge and endAge parameters
	 * can be used to select different frequency of treatments according to the age of the patient 
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return the annual cost for the specified patient during a period of time.
	 */
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge);
	
	/**
	 * Returns the cost of a complication upon incidence.
	 * @param pat A patient
	 * @param newEvent A new complication for the patient
	 * @return the cost of a complication upon incidence
	 */
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent);
	
	/**
	 * Returns the cost of a severe hypoglycemic episode
	 * @param pat A patient
	 * @return the cost of a severe hypoglycemic episode
	 */
	public double getCostForSevereHypoglycemicEpisode(T1DMPatient pat);
}