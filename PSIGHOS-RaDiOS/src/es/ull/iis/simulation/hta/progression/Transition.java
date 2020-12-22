/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.ReseteableParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.TimeToEventParam;

/**
 * Defines a transition from a manifestation to another manifestation
 * @author Iván Castilla
 *
 */
public abstract class Transition {
	/** Manifestation that produces the transition */
	private final Manifestation srcManifestation;
	/** Manifestation that the transition leads to */
	private final Manifestation destManifestation;
	/** The time to event for each available transition and each simulation */
	private final TimeToEventParam[] time2Event;
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;
	/** Indicates whether the destination manifestation of this transition replaces the source destination in the state of the patient */
	private final boolean replacesPrevious;
	
	/**
	 * 
	 */
	public Transition(final SecondOrderParamsRepository secParams, Manifestation srcManifestation, Manifestation destManifestation, boolean replacesPrevious) {
		this.secParams = secParams;
		this.srcManifestation = srcManifestation;
		this.destManifestation = destManifestation;
		this.replacesPrevious = replacesPrevious;
		this.time2Event = new TimeToEventParam[secParams.getnRuns() + 1];
		Arrays.fill(time2Event, null);
	}

	/**
	 * Returns the manifestation that produces the transition
	 * @return the manifestation that produces the transition
	 */
	public Manifestation getSrcManifestation() {
		return srcManifestation;
	}

	/**
	 * Returns the manifestation that the transition leads to
	 * @return the manifestation that the transition leads to
	 */
	public Manifestation getDestManifestation() {
		return destManifestation;
	}

	/**
	 * @return the replacesPrevious
	 */
	public boolean replacesPrevious() {
		return replacesPrevious;
	}

	/**
	 * Creates a parameter to compute the time to event for each second order instance of the transition
	 * @param id Identifier of the simulation
	 * @return a parameter to compute the time to event for each second order instance of the transition
	 */
	protected abstract TimeToEventParam getTimeToEventParam(int id);
	
	public void reset(int id) {
		final TimeToEventParam t2Event = time2Event[id];
		if (t2Event instanceof ReseteableParam<?>)
			((ReseteableParam<?>)t2Event).reset();
	}
	
	/**
	 * Returns the time to event for a patient
	 * @param pat A patient
	 * @param limit The upper limit for the occurrence of the event. If the computed time to event is higher or equal 
	 * than the limit, returns Long.MAX_VALUE
	 * @return The time to event for the patient; Long.MAX_VALUE if the event will never happen.
	 */
	public long getTimeToEvent(Patient pat, long limit) {
		final int id = pat.getSimulation().getIdentifier();
		if (time2Event[id] == null)
			time2Event[id] = getTimeToEventParam(id);
		final long time = time2Event[id].getValue(pat);
		return (time >= limit) ? Long.MAX_VALUE : time;		
	}
}
