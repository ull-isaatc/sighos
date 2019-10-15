/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgressionPair;
import es.ull.iis.simulation.hta.diabetes.params.DeathWithEventParam;
import es.ull.iis.simulation.hta.diabetes.params.MultipleEventParam;

/**
 * An abstract class to represent acute complications. Acute complications are characterized by an annual
 * probability of developing the complication, a relative risk of development depending on characteristics
 * of the patient or the intervention, and a probability of dying from the complication.
 * A patient can suffer several acute events during his/her life. Hence, this class stores all the predictions. 
 * An event can be cancelled for any reason, so the random number used to generate the event is reused.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class AcuteComplicationSubmodel extends ComplicationSubmodel {
	/** The function to calculate the time to next event of the acute complication */
	private final MultipleEventParam<Long> time2Event;
	/** Death associated to the acute events */
	private final DeathWithEventParam associatedDeath;

	private final DiabetesAcuteComplications comp;
	/**
	 * Creates a submodel for an acute complication. 
	 * @param nPatients Number of patient that will be created
	 * @param annualProb Annual probability of developing the acute complication
	 * @param rr Relative risk of developing the acute complication, depending on characteristics of the patient or the intervention
	 * @param pDeath Probability of dying when an acute event appears
	 */
	public AcuteComplicationSubmodel(DiabetesAcuteComplications comp, MultipleEventParam<Long> time2Event, DeathWithEventParam associatedDeath) {
		this.time2Event = time2Event;
		this.associatedDeath = associatedDeath;
		this.comp = comp;
	}

	/**
	 * Returns the time to a new acute event and whether it causes causes the death of the patient.
	 * @param pat A patient 
	 * @return the time to a new acute event and whether it causes causes the death of the patient.
	 */
	public DiabetesProgressionPair getProgression(DiabetesPatient pat) {
		// New event for the patient
		final long timeToDeath = pat.getTimeToDeath();
		final long timeToHypo = time2Event.getValue(pat);
		if (timeToHypo >= timeToDeath)
			return new DiabetesProgressionPair(comp, Long.MAX_VALUE, false);
		return new DiabetesProgressionPair(comp, timeToHypo, associatedDeath.getValue(pat));
	}

	/**
	 * Tells the parameter to reuse the last values generated 
	 * @param pat A patient
	 */
	public void cancelLast(DiabetesPatient pat) {
		time2Event.cancelLast(pat);
		associatedDeath.cancelLast(pat);
	}
	
	public void reset() {
		time2Event.reset();
		associatedDeath.reset();
	}
	
	/**
	 * Computes the cost of the acute event for a patient
	 * @param pat A patient
	 * @return The cost of the acute event for a patient
	 */
	public abstract double getCostOfComplication(DiabetesPatient pat);
	
	/**
	 * Computes the disutility associated to this acute event
	 * @param pat A patient
	 * @return The disutility associated to this acute event
	 */
	public abstract double getDisutility(DiabetesPatient pat);
}
