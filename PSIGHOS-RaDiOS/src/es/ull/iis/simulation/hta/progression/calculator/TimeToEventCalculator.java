package es.ull.iis.simulation.hta.progression.calculator;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * Computes the time to event for each patient. The time to event is expressed in a specific {@link TimeUnit time unit}.
 */
public interface TimeToEventCalculator {
	/**
	 * Returns the time until an event is intended to happen (expressed in the time unit used by the calculator)
	 * @param pat A patient
	 * @return the time until an event is intended to happen (expressed in the time unit used by the calculator)
	 */
	public double getTimeToEvent(Patient pat);

	/**
	 * Returns the time unit used by the calculator
	 * @return the time unit used by the calculator
	 */
	TimeUnit getTimeUnit();
}