/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;

/**
 * Classes implementing this interface produce costs
 * @author Iván Castilla Rodríguez
 *
 */
public interface CostProducer extends OutcomeProducer {
	/**
	 * Returns the cost associated to the patient during the defined period
	 * @param pat A patient
	 * @param initYear Starting time of the period (in years)
	 * @param endYear Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public double getCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate);
	/**
	 * Returns the annualized cost associated to the patient during the defined period. 
	 * Applies the punctual discount corresponding to each year to all the costs within such year. 
	 * @param pat A patient
	 * @param initYear Starting time of the period (in years)
	 * @param endYear Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate);
	/**
	 * Returns the treatment and follow up costs associated to the patient during the defined period. These costs should only be applied to diagnosed patients.  
	 * @param pat A patient
	 * @param initYear Starting time of the period (in years)
	 * @param endYear Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return  the treatment and follow up costs for this class  during the defined period
	 */
	public double getTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate);
	/**
	 * Returns the annualized treatment and follow up costs associated to the patient during the defined period. These costs should only be applied to diagnosed patients.
	 * Applies the punctual discount corresponding to each year to all the costs within such year. 
	 * @param pat A patient
	 * @param initYear Starting time of the period (in years)
	 * @param endYear Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return The treatment and follow up costs for each natural year during the specified period
	 */
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate);

	/**
	 * Returns the initial cost associated to the patient,  i.e., a diagnostic cost in case it is a disease, an onset cost for manifestations, a punctual cost for an intervention... 
	 * @param pat A patient
	 * @param time Specific time when the cost is applied (in years)
	 * @param discountRate The discount rate to apply to the cost
	 * @return the initial cost of this class
	 */
	public double getStartingCost(Patient pat, double time, Discount discountRate);
}
