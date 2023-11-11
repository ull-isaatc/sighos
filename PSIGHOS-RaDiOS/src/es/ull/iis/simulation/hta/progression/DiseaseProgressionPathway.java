/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A "pathway" to a manifestation. Pathways consists of a {@link Condition condition} that must be met by the patient and a way of computing the 
 * @link {@link TimeToEventCalculator time to the event}. 
 * @author Iván Castilla Rodríguez
 */
public class DiseaseProgressionPathway implements CreatesSecondOrderParameters {
	/** Resulting manifestation */
	private final DiseaseProgression nextProgression;
	/** Condition that must be met progress to a manifestation */
	private final Condition<ConditionInformation> condition;
	/** Calculator of the time to event if the condition is met */
	private final TimeToEventCalculator timeToEvent;
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;

	/**
	 * Creates a new pathway to a manifestation
	 * @param secParams Repository for common parameters
	 * @param nextProgression Resulting progression of the disease
	 * @param condition A condition that the patient must met before he/she can progress to the manifestation
	 * @param timeToEvent A way of computing the time that will take the patient to show the manifestation in case the condition is met 
	 */
	public DiseaseProgressionPathway(SecondOrderParamsRepository secParams, DiseaseProgression nextProgression, Condition<ConditionInformation> condition, TimeToEventCalculator timeToEvent) {
		this.nextProgression = nextProgression;
		this.secParams = secParams;
		this.condition = condition;
		this.timeToEvent = timeToEvent;
		nextProgression.addPathway(this);
	}

	/**
	 * Creates a new pathway to a manifestation with no previous condition, i.e., this pathway is always suitable independently of the patient's state.
	 * @param secParams Repository for common parameters
	 * @param nextProgression Resulting progression of the disease
	 * @param timeToEvent A way of computing the time that will take the patient to show the manifestation in case the condition is met 
	 */
	public DiseaseProgressionPathway(SecondOrderParamsRepository secParams, DiseaseProgression nextProgression, TimeToEventCalculator timeToEvent) {
		this(secParams, nextProgression, new TrueCondition<ConditionInformation>(), timeToEvent);
	}
	
	
	/**
	 * Returns the time to event for a patient if he/she meets certain condition
	 * @param pat A patient
	 * @param limit The upper limit for the occurrence of the event. If the computed time to event is higher or equal 
	 * than the limit, returns Long.MAX_VALUE
	 * @return The time to event for the patient; Long.MAX_VALUE if the event will never happen.
	 */
	public long getTimeToEvent(Patient pat, long limit) {
		if (condition.check(new ConditionInformation(pat, nextProgression))) {
			final long time = timeToEvent.getTimeToEvent(pat);
			return (time >= limit) ? Long.MAX_VALUE : time;
		}
		return Long.MAX_VALUE;
	}

	/**
	 * Returns the condition that a patient must meet to be able to progress 
	 * @return The condition that a patient must meet to be able to progress
	 */
	public Condition<ConditionInformation> getCondition() {
		return condition;
	}


	/**
	 * @return the resulting disease progression
	 */
	public DiseaseProgression getNextProgression(Patient pat) {
		return nextProgression;
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
	}

	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
	
	public static class ConditionInformation {
		final private Patient patient;
		final private DiseaseProgression progression;
		/**
		 * @param patient
		 * @param progression
		 */
		public ConditionInformation(Patient patient, DiseaseProgression progression) {
			super();
			this.patient = patient;
			this.progression = progression;
		}
		/**
		 * @return the patient
		 */
		public Patient getPatient() {
			return patient;
		}
		/**
		 * @return the progression
		 */
		public DiseaseProgression getProgression() {
			return progression;
		}
	}
}
