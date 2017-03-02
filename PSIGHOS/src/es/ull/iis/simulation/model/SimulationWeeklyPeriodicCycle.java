/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.EnumSet;

import es.ull.iis.util.Cycle;
import es.ull.iis.util.WeeklyPeriodicCycle;
import es.ull.iis.util.WeeklyPeriodicCycle.WeekDays;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationWeeklyPeriodicCycle implements SimulationCycle {
	/**
	 * 
	 */
	private EnumSet<WeekDays> daySet;
	private final WeeklyPeriodicCycle cycle;
	
	/**
	 * Creates a new weekly periodic cycle.
	 * @param unit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param daySet
	 * @param startTs
	 * @param endTs
	 */
	public SimulationWeeklyPeriodicCycle(TimeUnit unit, EnumSet<WeekDays> daySet, TimeStamp startTs, TimeStamp endTs) {
		cycle = new WeeklyPeriodicCycle(daySet, unit.convert(TimeStamp.getDay()), unit.convert(startTs), unit.convert(endTs));
	}

	/**
	 * Creates a new weekly periodic cycle.
	 * @param unit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param daySet 
	 * @param startTs
	 * @param iterations
	 */
	public SimulationWeeklyPeriodicCycle(TimeUnit unit, EnumSet<WeekDays> daySet, TimeStamp startTs, int iterations) {
		cycle = new WeeklyPeriodicCycle(daySet, unit.convert(TimeStamp.getDay()), unit.convert(startTs), iterations);
	}

	/**
	 * Creates a new weekly periodic cycle.
	 * @param unit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param daySet 
	 * @param startTs
	 * @param endTs
	 */
	public SimulationWeeklyPeriodicCycle(TimeUnit unit, EnumSet<WeekDays> daySet, long startTs, long endTs) {
		this(unit, daySet, new TimeStamp(unit, startTs), new TimeStamp(unit, endTs));
	}

	/**
	 * Creates a new weekly periodic cycle.
	 * @param unit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param daySet
	 * @param startTs
	 * @param iterations
	 */
	public SimulationWeeklyPeriodicCycle(TimeUnit unit, EnumSet<WeekDays> daySet, long startTs, int iterations) {
		this(unit, daySet, new TimeStamp(unit, startTs), iterations);
	}

	/**
	 * @return the daySet
	 */
	public EnumSet<WeekDays> getDaySet() {
		return daySet;
	}

	@Override
	public Cycle getCycle() {
		return cycle;
	}
	
}
