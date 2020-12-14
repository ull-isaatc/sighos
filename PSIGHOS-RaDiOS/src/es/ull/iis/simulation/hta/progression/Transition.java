/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.GeneratesSecondOrderInstances;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.ReseteableParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.TimeToEventParam;

/**
 * Defines a transition from a manifestation to another manifestation
 * @author Iván Castilla
 *
 */
public abstract class Transition implements GeneratesSecondOrderInstances {
	private final Manifestation srcManifestation;
	private final Manifestation destManifestation;
	/** The time to event for each available transition and each simulation */
	private final ArrayList<TimeToEventParam> time2Event;
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
		this.time2Event = new ArrayList<>();
	}

	/**
	 * @return the srcManifestation
	 */
	public Manifestation getSrcManifestation() {
		return srcManifestation;
	}

	/**
	 * @return the destManifestation
	 */
	public Manifestation getDestManifestation() {
		return destManifestation;
	}

	/**
	 * @return the replacesPrevious
	 */
	public boolean isReplacesPrevious() {
		return replacesPrevious;
	}

	/**
	 * Creates a parameter to compute the time to event for each second order instance of the transition
	 * @param id Identifier of the simulation
	 * @return a parameter to compute the time to event for each second order instance of the transition
	 */
	protected abstract TimeToEventParam getTimeToEventParam(int id);
	
	@Override
	public void generate() {
		final int n = secParams.getnRuns();
		time2Event.ensureCapacity(n + 1);
		for (int i = 0; i < n + 1; i++) {
			time2Event.add(getTimeToEventParam(i));
		}		
	}
	
	public void reset(int id) {
		final TimeToEventParam t2Event = time2Event.get(id);
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
		final TimeToEventParam param = time2Event.get(pat.getSimulation().getIdentifier());
		final long time = param.getValue(pat);
		return (time >= limit) ? Long.MAX_VALUE : time;		
	}
}
