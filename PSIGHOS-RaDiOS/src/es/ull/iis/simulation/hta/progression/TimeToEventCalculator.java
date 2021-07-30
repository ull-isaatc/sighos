package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;

/**
 * Computes the time to event for each patient
 */
public interface TimeToEventCalculator {
	/**
	 * Returns the simulation timestamp when an event is intended to happen
	 * @param pat A patient
	 * @return the simulation timestamp when an event is intended to happen
	 */
	public long getTimeToEvent(Patient pat);
}