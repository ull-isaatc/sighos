/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.condition.PathwayCondition;

/**
 * A "pathway" to a manifestation. Pathways consists of a {@link PathwayCondition condition} that must be met by the patient and a way of computing the 
 * @link {@link TimeToEventCalculator time to the event}. 
 * @author Iván Castilla Rodríguez
 *
 */
public class ManifestationPathway implements CreatesSecondOrderParameters {
	/** Resulting manifestation */
	private final Manifestation destManifestation;
	/** Condition that must be met progress to a manifestation */
	private final PathwayCondition condition;
	/** Calculator of the time to event if the condition is met */
	private final TimeToEventCalculator timeToEvent;
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;

	/**
	 * Creates a new pathway to a manifestation
	 * @param secParams Repository for common parameters
	 * @param destManifestation Resulting manifestation
	 * @param condition A condition that the patient must met before he/she can progress to the manifestation
	 * @param timeToEvent A way of computing the time that will take the patient to show the manifestation in case the condition is met 
	 */
	public ManifestationPathway(SecondOrderParamsRepository secParams, Manifestation destManifestation, PathwayCondition condition, TimeToEventCalculator timeToEvent) {
		this.destManifestation = destManifestation;
		this.secParams = secParams;
		this.condition = condition;
		this.timeToEvent = timeToEvent;
		destManifestation.addPathway(this);
	}

	/**
	 * Creates a new pathway to a manifestation with no previous condition, i.e., this pathway is always suitable independently of the patient's state.
	 * @param secParams Repository for common parameters
	 * @param destManifestation Resulting manifestion
	 * @param timeToEvent A way of computing the time that will take the patient to show the manifestation in case the condition is met 
	 */
	public ManifestationPathway(SecondOrderParamsRepository secParams, Manifestation destManifestation, TimeToEventCalculator timeToEvent) {
		this(secParams, destManifestation, PathwayCondition.TRUE_CONDITION, timeToEvent);
	}
	
	/**
	 * Returns the time to event for a patient if he/she meets the condition
	 * @param pat A patient
	 * @param limit The upper limit for the occurrence of the event. If the computed time to event is higher or equal 
	 * than the limit, returns Long.MAX_VALUE
	 * @return The time to event for the patient; Long.MAX_VALUE if the event will never happen.
	 */
	public long getTimeToEvent(Patient pat, long limit) {
		if (condition.check(pat)) {
			final long time = timeToEvent.getTimeToEvent(pat);
			return (time >= limit) ? Long.MAX_VALUE : time;
		}
		return Long.MAX_VALUE;
	}

	public PathwayCondition getCondition() {
		return condition;
	}

	/**
	 * @return the resulting manifestation
	 */
	public Manifestation getDestManifestation() {
		return destManifestation;
	}

	@Override
	public void registerSecondOrderParameters() {
	}
}
