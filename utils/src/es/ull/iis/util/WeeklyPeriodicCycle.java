/**
 * 
 */
package es.ull.iis.util;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;

/**
 * A week-based periodic cycle which allows a user to define events happening 
 * some days a week.
 * @author Iván Castilla Rodríguez
 */
public class WeeklyPeriodicCycle extends PeriodicCycle {
	/**
	 * 
	 */
	public enum WeekDays {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	}
	public final static EnumSet<WeekDays> ALLWEEK = EnumSet.allOf(WeekDays.class);
	public final static EnumSet<WeekDays> WEEKEND = EnumSet.of(WeekDays.SATURDAY, WeekDays.SUNDAY);
	public final static EnumSet<WeekDays> WEEKDAYS = EnumSet.complementOf(WEEKEND);
	private final EnumSet<WeekDays> daySet;
	
	/**
	 * Creates a new weekly periodic cycle.
	 * @param daySet
	 * @param dayUnit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param startTs
	 * @param endTs
	 */
	public WeeklyPeriodicCycle(EnumSet<WeekDays> daySet, double dayUnit, double startTs, double endTs) {
		super(startTs, TimeFunctionFactory.getInstance("ConstantVariate", dayUnit * 7), endTs);
		this.daySet = daySet;
		double []stamps = new double[daySet.size()];
		int count = 0;
		for (WeekDays day : daySet) 
			stamps[count++] = day.ordinal() * dayUnit; 
		subCycle = new TableCycle(stamps); 
	}

	/**
	 * Creates a new weekly periodic cycle.
	 * @param daySet
	 * @param dayUnit Timestamp units in a day. For example: if time unit is hour, this should be 24.0
	 * @param startTs
	 * @param iterations
	 */
	public WeeklyPeriodicCycle(EnumSet<WeekDays> daySet, double dayUnit, double startTs, int iterations) {
		super(startTs, TimeFunctionFactory.getInstance("ConstantVariate", dayUnit * 7), iterations);
		this.daySet = daySet;
		double []stamps = new double[daySet.size()];
		int count = 0;
		for (WeekDays day : daySet) 
			stamps[count++] = day.ordinal() * dayUnit; 
		subCycle = new TableCycle(stamps); 
	}

	/**
	 * @return the daySet
	 */
	public EnumSet<WeekDays> getDaySet() {
		return daySet;
	}

}
