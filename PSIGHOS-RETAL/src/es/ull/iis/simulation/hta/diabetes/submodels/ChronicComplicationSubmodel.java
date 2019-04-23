/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;

/**
 * An abstract class representing the evolution of a chronic complication. Chronic complications can be represented 
 * as a progression of stages of any complexity, and incur in annual costs and disutilities. Additionaly, starting
 * each stage of the complication can incur in one-time costs.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ChronicComplicationSubmodel extends ComplicationSubmodel {
	/**
	 * Creates a submodel for a chronic complication.
	 */
	public ChronicComplicationSubmodel() {
		super();
	}

	/**
	 * Returns how this patient will progress from its current state with regards to this
	 * chronic complication. The progress can include removal of events already scheduled, modification of 
	 * previously scheduled events and new events.
	 * @param pat A patient
	 * @return how this patient will progress from its current state with regards to this
	 * chronic complication
	 */
	public abstract DiabetesProgression getProgression(DiabetesPatient pat);
	/**
	 * Returns the initial set of stages (one or more) that the patient will start with when this complication appears. 
	 * @param pat A patient
	 * @return the initial set of stages that the patient will start with when this complication appears
	 */
	public abstract TreeSet<DiabetesComplicationStage> getInitialStage(DiabetesPatient pat);
	/**
	 * Returns the annual cost associated to the current state of the patient and during the defined period
	 * @param pat A patient
	 * @param initAge Starting time of the period (in years)
	 * @param endAge Ending time of the period
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public abstract double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge);
	/**
	 * Returns the cost associated to the start of a new stage of this chronic complication
	 * @param pat A patient
	 * @param newComplication New stage of this chronic complication
	 * @return the cost associated to the start of a new stage of this chronic complication
	 */
	public abstract double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newComplication);
	/**
	 * Returns the disutility value associated to the current stage of this chronic complication
	 * @param pat A patient
	 * @param method Method used to compute the disutility of this chronic complication in case the 
	 * complication allows for several stages to be concurrently active
	 * @return The disutility value associated to the current stage of this chronic complication
	 */
	public abstract double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method);
	
}
