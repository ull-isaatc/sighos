/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public interface DiseaseProgressionPathway extends CreatesSecondOrderParameters {
	/**
	 * Returns the time to event for a patient if he/she meets certain condition
	 * @param pat A patient
	 * @param limit The upper limit for the occurrence of the event. If the computed time to event is higher or equal 
	 * than the limit, returns Long.MAX_VALUE
	 * @return The time to event for the patient; Long.MAX_VALUE if the event will never happen.
	 */
	public long getTimeToEvent(Patient pat, long limit);

	/**
	 * Returns the condition that a patient must meet to be able to progress 
	 * @return The condition that a patient must meet to be able to progress
	 */
	public Condition<Patient> getCondition();

	/**
	 * @return the resulting disease progression
	 */
	public DiseaseProgression getNextProgression(Patient pat);
	
}
