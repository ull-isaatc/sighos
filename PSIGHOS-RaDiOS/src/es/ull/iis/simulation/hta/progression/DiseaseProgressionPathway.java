/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.calculator.TimeToEventCalculator;

/**
 * A "pathway" to a manifestation. Pathways consists of a {@link Condition condition} that must be met by the patient and a way of computing the 
 * @link {@link TimeToEventParameter time to the event}. 
 * @author Iván Castilla Rodríguez
 */
public class DiseaseProgressionPathway extends HTAModelComponent {
	/** Resulting manifestation */
	private final DiseaseProgression nextProgression;
	/** Condition that must be met progress to a manifestation */
	private final Condition<ConditionInformation> condition;
	/** Calculator of the time to event if the condition is met */
	private final TimeToEventCalculator tteCalculator;

	/**
	 * Creates a new pathway to a manifestation
	 * @param secParams Repository for common parameters
	 * @param nextProgression Resulting progression of the disease
	 * @param tteCalculator A way of computing the time that will take the patient to progress in case the condition is met 
	 * @param condition A condition that the patient must met before he/she can progress to the manifestation
	 */
	public DiseaseProgressionPathway(HTAModel model, String name, String description, DiseaseProgression nextProgression, TimeToEventCalculator tteCalculator, Condition<ConditionInformation> condition) {
		super(model, name, description);
		this.nextProgression = nextProgression;
		this.condition = condition;
		this.tteCalculator = tteCalculator;
		nextProgression.addPathway(this);
		registerUsedParameter(StandardParameter.TIME_TO_EVENT);
	}

	/**
	 * Creates a new pathway to a manifestation with no previous condition, i.e., this pathway is always suitable independently of the patient's state.
	 * @param secParams Repository for common parameters
	 * @param nextProgression Resulting progression of the disease
	 * @param tteCalculator A way of computing the time that will take the patient to progress in case the condition is met 
	 */
	public DiseaseProgressionPathway(HTAModel model, String name, String description, DiseaseProgression nextProgression, TimeToEventCalculator tteCalculator) {
		this(model, name, description, nextProgression, tteCalculator, new TrueCondition<ConditionInformation>());
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
			final double time = tteCalculator.getTimeToEvent(pat);
			if(Double.isNaN(time))
				return Long.MAX_VALUE;
			final long ts = pat.getSimulation().getTimeUnit().convert(time, tteCalculator.getTimeUnit());
			return (ts >= limit) ? Long.MAX_VALUE : pat.getTs() + ts;
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
	public DiseaseProgression getNextProgression() {
		return nextProgression;
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
