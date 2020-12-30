/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * A class implementing this interface can calculate the disease-related costs 
 * @author Iván Castilla Rodríguez
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
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge);
	
	/**
	 * Returns the cost of a complication upon incidence.
	 * @param pat A patient
	 * @param newEvent A new complication for the patient
	 * @return the cost of a complication upon incidence
	 */
	public double getCostOfManifestation(Patient pat, Manifestation newEvent);
	
	/**
	 * Returns the cost of an acute event
	 * @param pat A patient
	 * @param comp The acute event
	 * @return the cost of an acute event
	 */
	public double getCostForAcuteManifestation(Patient pat, Manifestation comp);

	/**
	 * Returns the costs incurred by the intervention alone
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return the costs incurred by the intervention alone
	 */
	public double getAnnualInterventionCostWithinPeriod(Patient pat, double initAge, double endAge);
	
	/**
	 * Returns the punctual cost upon starting a new intervention 
	 * @param pat A patient
	 * @return the punctual cost upon starting a new intervention
	 */
	public double getCostForIntervention(Patient pat);
	
	/**
	 * Returns annual cost for the specified patient during a period of time for each manifestation 
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return A map of pairs Manifestation - cost of the manifestation during the period
	 */
	public TreeMap<Manifestation, Double> getAnnualManifestationCostWithinPeriod(Patient pat, double initAge, double endAge);
	
	/**
	 * Returns annual cost for the specified patient during a period of time  
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return The annual cost for the specified patient during a period of time
	 */
	public double getAnnualDiseaseCostWithinPeriod(Patient pat, double initAge, double endAge);

	/**
	 * Returns the annual management cost applied independently of the complications
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return The annual management cost for the patient 
	 */
	public double getStdManagementCostWithinPeriod(Patient pat, double initAge, double endAge);
}
