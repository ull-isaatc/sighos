/**
 * 
 */
package es.ull.isaatc.simulation.common;

import java.util.EnumSet;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;
import es.ull.isaatc.util.TableCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationWeeklyPeriodicCycle implements SimulationCycle {
	/**
	 * 
	 */
	public enum WeekDays {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	}
	public static EnumSet<WeekDays> ALLWEEK = EnumSet.allOf(WeekDays.class);
	public static EnumSet<WeekDays> WEEKEND = EnumSet.of(WeekDays.SATURDAY, WeekDays.SUNDAY);
	public static EnumSet<WeekDays> WEEKDAYS = EnumSet.complementOf(WEEKEND);
	private EnumSet<WeekDays> daySet;
	private final PeriodicCycle cycle;
	
	/**
	 * Creates a new weekly periodic cycle.
	 * @param daySet
	 * @param dayUnit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param startTs
	 * @param endTs
	 */
	public SimulationWeeklyPeriodicCycle(Simulation simul, EnumSet<WeekDays> daySet, TimeStamp startTs, TimeStamp endTs) {
		this.daySet = daySet;
		double []stamps = new double[daySet.size()];
		int count = 0;
		for (WeekDays day : daySet) 
			stamps[count++] = simul.simulationTime2Long(new TimeStamp(TimeUnit.DAY, day.ordinal())); 
		TableCycle subCycle = new TableCycle(stamps); 
		cycle = new PeriodicCycle(simul.simulationTime2Long(startTs), 
				new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)).getFunction(), 
				simul.simulationTime2Long(endTs), subCycle);
	}

	/**
	 * Creates a new weekly periodic cycle.
	 * @param daySet
	 * @param dayUnit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param startTs
	 * @param iterations
	 */
	public SimulationWeeklyPeriodicCycle(Simulation simul, EnumSet<WeekDays> daySet, TimeStamp startTs, int iterations) {
		this.daySet = daySet;
		double []stamps = new double[daySet.size()];
		int count = 0;
		for (WeekDays day : daySet) 
			stamps[count++] = simul.simulationTime2Long(new TimeStamp(TimeUnit.DAY, day.ordinal())); 
		TableCycle subCycle = new TableCycle(stamps); 
		cycle = new PeriodicCycle(simul.simulationTime2Long(startTs), 
				new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)).getFunction(), 
				iterations, subCycle);
	}

	/**
	 * Creates a new weekly periodic cycle.
	 * @param daySet
	 * @param dayUnit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param startTs
	 * @param endTs
	 */
	public SimulationWeeklyPeriodicCycle(Simulation simul, EnumSet<WeekDays> daySet, long startTs, long endTs) {
		this(simul, daySet, new TimeStamp(simul.getTimeUnit(), startTs), new TimeStamp(simul.getTimeUnit(), endTs));
	}

	/**
	 * Creates a new weekly periodic cycle.
	 * @param daySet
	 * @param dayUnit Timestamp units in a day. For example: if time unit is minute, this should be 1440.0
	 * @param startTs
	 * @param iterations
	 */
	public SimulationWeeklyPeriodicCycle(Simulation simul, EnumSet<WeekDays> daySet, long startTs, int iterations) {
		this(simul, daySet, new TimeStamp(simul.getTimeUnit(), startTs), iterations);
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
