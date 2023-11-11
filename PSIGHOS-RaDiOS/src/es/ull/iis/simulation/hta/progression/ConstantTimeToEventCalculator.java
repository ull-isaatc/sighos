/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;

/**
 * A dummy calculator that simply returns the same time every time is called 
 * @author Iván Castilla Rodríguez
 *
 */
public class ConstantTimeToEventCalculator implements TimeToEventCalculator {
	/** A convenient predefined calculator that defines an instantaneous event */
	public static final ConstantTimeToEventCalculator INMEDIATE_TIME = new ConstantTimeToEventCalculator(0);
	/** The constant time to event to be returned */
	private final long timeToEvent;
	
	/**
	 * Creates a time to event calculator that always returns the same value
	 * @param timeToEvent The constant time to event to be returned
	 */
	public ConstantTimeToEventCalculator(long timeToEvent) {
		this.timeToEvent = timeToEvent;
	}

	@Override
	public long getTimeToEvent(Patient pat) {
		return timeToEvent + pat.getTs();
	}

}
