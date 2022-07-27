/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * A class implementing this interface can calculate the disease-related costs 
 * @author Iván Castilla Rodríguez
 *
 */
public interface CostCalculator {
	/**
	 * Return the cost for the specified patient during a period of time. The initT and endT parameters
	 * can be used to select different frequency of treatments according to the age of the patient 
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the cost for the specified patient during a period of time.
	 */
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);
	
	/**
	 * Returns the cost of a manifestation upon incidence.
	 * @param pat A patient
	 * @param newEvent A new complication for the patient
	 * @param time Time (in years) when the manifestation appears
	 * @param discountRate The discount rate to apply to the cost
	 * @return the cost of a manifestation upon incidence
	 */
	public double getCostUponIncidence(Patient pat, Manifestation newEvent, double time, Discount discountRate);

	/**
	 * Returns the costs incurred by the intervention alone
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the costs incurred by the intervention alone
	 */
	public double getInterventionCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);
	
	/**
	 * Returns the punctual cost upon starting a new intervention 
	 * @param pat A patient
	 * @param time Specific time when the cost is applied (in years)
	 * @param discountRate The discount rate to apply to the cost
	 * @return the punctual cost upon starting a new intervention
	 */
	public double getCostForIntervention(Patient pat, double time, Discount discountRate);
	
	/**
	 * Returns the cost for the specified patient during a period of time for each manifestation 
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return A map of pairs Manifestation - cost of the manifestation during the period
	 */
	public TreeMap<Manifestation, Double> getManifestationCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);
	
	/**
	 * Returns the cost for the specified patient during a period of time  
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return The annual cost for the specified patient during a period of time
	 */
	public double getDiseaseCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);

	/**
	 * Returns the management cost applied independently of the complications
	 * @param pat A patient
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return The annual management cost for the patient 
	 */
	public double getStdManagementCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate);
}
