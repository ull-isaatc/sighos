/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A "pathway" to a manifestation. Pathways consists of a {@link Condition condition} that must be met by the patient and a way of computing the 
 * @link {@link TimeToEventCalculator time to the event}. 
 * @author Iván Castilla Rodríguez
 *
 */
public class SingleDiseaseProgressionPathway implements DiseaseProgressionPathway {
	/** Resulting manifestation */
	private final DiseaseProgression nextProgression;
	/** Condition that must be met progress to a manifestation */
	private final Condition<Patient> condition;
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
	public SingleDiseaseProgressionPathway(SecondOrderParamsRepository secParams, DiseaseProgression nextProgression, Condition<Patient> condition, TimeToEventCalculator timeToEvent) {
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
	public SingleDiseaseProgressionPathway(SecondOrderParamsRepository secParams, DiseaseProgression nextProgression, TimeToEventCalculator timeToEvent) {
		this(secParams, nextProgression, new TrueCondition<Patient>(), timeToEvent);
	}
	
	@Override
	public long getTimeToEvent(Patient pat, long limit) {
		if (condition.check(pat)) {
			final long time = timeToEvent.getTimeToEvent(pat);
			return (time >= limit) ? Long.MAX_VALUE : time;
		}
		return Long.MAX_VALUE;
	}

	@Override
	public Condition<Patient> getCondition() {
		return condition;
	}

	@Override
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
}
