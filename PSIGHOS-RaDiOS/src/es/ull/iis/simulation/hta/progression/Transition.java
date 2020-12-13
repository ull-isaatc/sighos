/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.GenerateSecondOrderInstances;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.ReseteableParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.TimeToEventParam;

/**
 * Defines a transition from a manifestation to another manifestation
 * @author Iván Castilla
 *
 */
public abstract class Transition implements Comparable<Transition>, GenerateSecondOrderInstances {
	private static Manifestation NULL_MANIFESTATION = new Manifestation(null, "NONE", "No manifestations", null, null) {

		@Override
		public void registerSecondOrderParameters() {			
		}
		
	};
	private final Manifestation srcManifestation;
	private final Manifestation destManifestation;
	/** The time to event for each available transition and each simulation */
	private final ArrayList<TimeToEventParam> time2Event;
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;
	
	/**
	 * 
	 */
	public Transition(final SecondOrderParamsRepository secParams, Manifestation srcManifestation, Manifestation destManifestation) {
		this.secParams = secParams;
		this.srcManifestation = srcManifestation;
		this.destManifestation = destManifestation;
		this.time2Event = new ArrayList<>();
	}

	/**
	 * 
	 */
	public Transition(final SecondOrderParamsRepository secParams, Manifestation destManifestation) {
		this(secParams, NULL_MANIFESTATION, destManifestation);
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

	protected abstract TimeToEventParam getTimeToEventParam(int id);
	
	@Override
	public void generate() {
		final int n = secParams.getnRuns();
		time2Event.ensureCapacity(n);
		for (int i = 0; i < n; i++) {
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
	
	@Override
	public int compareTo(Transition o) {
		final int comp = srcManifestation.compareTo(o.srcManifestation);
		return (comp != 0) ? comp : destManifestation.compareTo(o.destManifestation);
	}
}
