/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelPeriodicCycle implements ModelCycle {
	private final PeriodicCycle cycle;
	
	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public ModelPeriodicCycle(TimeUnit unit, Time startTs, ModelTimeFunction period, Time endTs) {
		cycle = new PeriodicCycle(unit.time2Double(startTs), period.getFunction(), unit.time2Double(endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public ModelPeriodicCycle(TimeUnit unit, Time startTs, ModelTimeFunction period,
			int iterations) {
		cycle = new PeriodicCycle(unit.time2Double(startTs), period.getFunction(), iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, Time startTs, ModelTimeFunction period,
			Time endTs, ModelCycle subCycle) {
		cycle = new PeriodicCycle(unit.time2Double(startTs), period.getFunction(), unit.time2Double(endTs), subCycle.getCycle());
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, Time startTs, ModelTimeFunction period,
			int iterations, ModelCycle subCycle) {
		cycle = new PeriodicCycle(unit.time2Double(startTs), period.getFunction(), iterations, subCycle.getCycle());
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public ModelPeriodicCycle(TimeUnit unit, double startTs, ModelTimeFunction period, double endTs) {
		this(unit, new Time(unit, startTs), period, new Time(unit, endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public ModelPeriodicCycle(TimeUnit unit, double startTs, ModelTimeFunction period,
			int iterations) {
		this(unit, new Time(unit, startTs), period, iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, double startTs, ModelTimeFunction period,
			double endTs, ModelCycle subCycle) {
		this(unit, new Time(unit, startTs), period, new Time(unit, endTs), subCycle);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, double startTs, ModelTimeFunction period,
			int iterations, ModelCycle subCycle) {
		this(unit, new Time(unit, startTs), period, iterations, subCycle);
	}

	/**
	 * @return the cycle
	 */
	public Cycle getCycle() {
		return cycle;
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances hour to hour
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return An hourly cycle
	 */
	public static ModelPeriodicCycle newHourlyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.HOUR, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0.0 and advances day to day
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return A daily cycle
	 */
	public static ModelPeriodicCycle newDailyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.DAY, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances week to week
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return A weekly cycle
	 */
	public static ModelPeriodicCycle newWeeklyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.WEEK, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances month to month
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return A monthly cycle
	 */
	public static ModelPeriodicCycle newMonthlyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.MONTH, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances hour to hour
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using simulation time unit) when this cycle starts
	 * @return An hourly cycle
	 */
	public static ModelPeriodicCycle newHourlyCycle(TimeUnit unit, double startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.HOUR, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0.0 and advances day to day
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using simulation time unit) when this cycle starts
	 * @return A daily cycle
	 */
	public static ModelPeriodicCycle newDailyCycle(TimeUnit unit, double startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.DAY, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances week to week
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using simulation time unit) when this cycle starts
	 * @return A weekly cycle
	 */
	public static ModelPeriodicCycle newWeeklyCycle(TimeUnit unit, double startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.WEEK, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances month to month
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using simulation time unit) when this cycle starts
	 * @return A monthly cycle
	 */
	public static ModelPeriodicCycle newMonthlyCycle(TimeUnit unit, double startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.MONTH, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0.0 and advances hour to hour
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return An hourly cycle
	 */
	public static ModelPeriodicCycle newHourlyCycle(TimeUnit unit, Time startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.HOUR, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0.0 and advances day to day
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return A daily cycle
	 */
	public static ModelPeriodicCycle newDailyCycle(TimeUnit unit, Time startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.DAY, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances week to week
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return A weekly cycle
	 */
	public static ModelPeriodicCycle newWeeklyCycle(TimeUnit unit, Time startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.WEEK, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0.0 and advances month to month
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return A monthly cycle
	 */
	public static ModelPeriodicCycle newMonthlyCycle(TimeUnit unit, Time startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new Time(TimeUnit.MONTH, 1)), 0);
	}
	
	
}
