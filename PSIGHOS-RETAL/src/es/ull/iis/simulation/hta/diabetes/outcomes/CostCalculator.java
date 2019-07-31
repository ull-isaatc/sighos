/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.outcomes;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;

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
	public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge);
	
	/**
	 * Returns the cost of a complication upon incidence.
	 * @param pat A patient
	 * @param newEvent A new complication for the patient
	 * @return the cost of a complication upon incidence
	 */
	public double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newEvent);
	
	/**
	 * Returns the cost of an acute event
	 * @param pat A patient
	 * @param comp The acute event
	 * @return the cost of an acute event
	 */
	public double getCostForAcuteEvent(DiabetesPatient pat, DiabetesAcuteComplications comp);

	/**
	 * Returns the costs incurred by the intervention alone
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return the costs incurred by the intervention alone
	 */
	public double getAnnualInterventionCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge);
	
	/**
	 * Returns annual cost for the specified patient during a period of time for each complication 
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return An array, where each position represents the cost of certain chronic complication
	 */
	public double[] getAnnualChronicComplicationCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge);
	
	/**
	 * Returns the annual management cost applied independently of the complications
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return The annual management cost for the patient 
	 */
	public double getStdManagementCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge);
	
}
