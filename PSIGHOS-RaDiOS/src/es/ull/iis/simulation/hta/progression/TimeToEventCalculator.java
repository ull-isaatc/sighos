package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;

/**
 * Computes the time to event for each patient
 */
public interface TimeToEventCalculator {
	public long getTimeToEvent(Patient pat);
}