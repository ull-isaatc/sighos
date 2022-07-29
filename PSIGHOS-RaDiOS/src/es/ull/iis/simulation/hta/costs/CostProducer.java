/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;

/**
 * Classes implementing this interface produce costs
 * @author Iván Castilla Rodríguez
 *
 */
public interface CostProducer {
	/**
	 * Returns the cost associated to the patient during the defined period
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);
	/**
	 * Returns the annualized cost associated to the patient during the defined period. 
	 * Applies the punctual discount corresponding to each year to all the costs within such year. 
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);
	/**
	 * Returns the treatment and follow up costs associated to the patient during the defined period. These costs should only be applied to diagnosed patients.  
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return  the treatment and follow up costs for this class  during the defined period
	 */
	public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate);
	/**
	 * Returns the annualized treatment and follow up costs associated to the patient during the defined period. These costs should only be applied to diagnosed patients.
	 * Applies the punctual discount corresponding to each year to all the costs within such year. 
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return The treatment and follow up costs for each natural year during the specified period
	 */
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate);

	/**
	 * Returns the initial cost associated to the patient,  i.e., a diagnostic cost in case it is a disease, an onset cost for manifestations, a punctual cost for an intervention... 
	 * @param pat A patient
	 * @param time Specific time when the cost is applied (in years)
	 * @param discountRate The discount rate to apply to the cost
	 * @return the initial cost of this class
	 */
	public double getStartingCost(Patient pat, double time, Discount discountRate);

	/**
	 * Returns an array with as many items as natural years are in the interval (initT, endT). The first and last item are initialized 
	 * with the proportional part of the natural year corresponding to initT and endT, respectively; while the rest of intervals are initialized to 1. 
	 * For example, if initT = 3.1 and endT = 5.2, this method should return [0.9, 1.0, 0.2].  
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @return an array with as many items as natural years are in the interval (initT, endT)
	 */
	public static double[] getIntervalsForPeriod(double initT, double endT) {
		int naturalYear = (int) initT;
		final int nIntervals = (int) endT - naturalYear + (int) Math.ceil(endT - (int) endT);
		if (nIntervals == 1)
			return new double[] {endT - initT};
		double[] result = new double[nIntervals];
		result[0] = naturalYear + 1 - initT;
		// Process the intermediate intervals, corresponding to full years 
		for (int i = 1; i < nIntervals - 1; i++) {
			result[i] = 1.0;
		}
		// Process the last interval
		result[nIntervals - 1] = endT - Math.ceil(endT) + 1;		
		return result;
	}
}
