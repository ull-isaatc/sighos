/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.util.Cycle;
import es.ull.iis.util.PeriodicCycle;

/**
 * A wrapper class for {@link es.ull.iis.util.PeriodicCycle PeriodicCycle} to be used inside a simulation. 
 * Thus {@link TimeStamp} can be used to define the cycle parameters.
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelPeriodicCycle implements ModelCycle {
	/** Inner {@link es.ull.iis.util.PeriodicCycle PeriodicCycle} */
	private final PeriodicCycle cycle;
	
	/**
	 * Creates a new cycle which runs until the specified timestamp is reached.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start
	 * @param period Time interval between two successive ocurrences of an event
	 * @param endTs Relative time when this cycle is expected to finish
	 */
	public ModelPeriodicCycle(TimeUnit unit, TimeStamp startTs, TimeFunction period, TimeStamp endTs) {
		cycle = new PeriodicCycle(unit.convert(startTs), period, unit.convert(endTs));
	}

	/**
	 * Creates a new cycle which runs N times.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start
	 * @param period Time interval between two successive ocurrences of an event
	 * @param iterations How many times this cycle is executed. A value of 0 indicates infinite iterations
	 */
	public ModelPeriodicCycle(TimeUnit unit, TimeStamp startTs, TimeFunction period, int iterations) {
		cycle = new PeriodicCycle(unit.convert(startTs), period, iterations);
	}

	/**
	 * Creates a new cycle containing a subcycle which runs until the specified timestamp is reached.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start
	 * @param period Time interval between two successive ocurrences of an event
	 * @param endTs Relative time when this cycle is expected to finish
	 * @param subCycle Subcycle contained in this cycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, TimeStamp startTs, TimeFunction period,
			TimeStamp endTs, ModelCycle subCycle) {
		cycle = new PeriodicCycle(unit.convert(startTs), period, unit.convert(endTs), subCycle.getCycle());
	}

	/**
	 * Creates a new cycle containing a subcycle which runs N times.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start
	 * @param period Time interval between two successive ocurrences of an event
	 * @param iterations How many times this cycle is executed. A value of 0 indicates infinite iterations
	 * @param subCycle Subcycle contained in this cycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, TimeStamp startTs, TimeFunction period,
			int iterations, ModelCycle subCycle) {
		cycle = new PeriodicCycle(unit.convert(startTs), period, iterations, subCycle.getCycle());
	}

	/**
	 * Creates a new cycle which runs until the specified timestamp is reached.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start expressed in <tt>unit</tt>
	 * @param period Time interval between two successive ocurrences of an event
	 * @param endTs Relative time when this cycle is expected to finish expressed in <tt>unit</tt>
	 */
	public ModelPeriodicCycle(TimeUnit unit, long startTs, ModelTimeFunction period, long endTs) {
		this(unit, new TimeStamp(unit, startTs), period, new TimeStamp(unit, endTs));
	}

	/**
	 * Creates a new cycle which runs N times.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start expressed in <tt>unit</tt>
	 * @param period Time interval between two successive ocurrences of an event
	 * @param iterations How many times this cycle is executed. A value of 0 indicates infinite iterations
	 */
	public ModelPeriodicCycle(TimeUnit unit, long startTs, ModelTimeFunction period,
			int iterations) {
		this(unit, new TimeStamp(unit, startTs), period, iterations);
	}

	/**
	 * Creates a new cycle which runs until the specified timestamp is reached.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start expressed in <tt>unit</tt>
	 * @param period Time interval between two successive ocurrences of an event
	 * @param endTs Relative time when this cycle is expected to finish expressed in <tt>unit</tt>
	 * @param subCycle Subcycle contained in this cycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, long startTs, ModelTimeFunction period,
			long endTs, ModelCycle subCycle) {
		this(unit, new TimeStamp(unit, startTs), period, new TimeStamp(unit, endTs), subCycle);
	}

	/**
	 * Creates a new cycle which runs N times.
	 * @param unit Time unit used in the simulation
	 * @param startTs Relative time when this cycle is expected to start expressed in <tt>unit</tt>
	 * @param period Time interval between two successive ocurrences of an event
	 * @param iterations How many times this cycle is executed. A value of 0 indicates infinite iterations
	 * @param subCycle Subcycle contained in this cycle
	 */
	public ModelPeriodicCycle(TimeUnit unit, long startTs, ModelTimeFunction period,
			int iterations, ModelCycle subCycle) {
		this(unit, new TimeStamp(unit, startTs), period, iterations, subCycle);
	}

	@Override
	public Cycle getCycle() {
		return cycle;
	}

	/**
	 * Creates a cycle that starts at 0 and advances hour to hour
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return An hourly cycle
	 */
	public static ModelPeriodicCycle newHourlyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0 and advances day to day
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return A daily cycle
	 */
	public static ModelPeriodicCycle newDailyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0 and advances week to week
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return A weekly cycle
	 */
	public static ModelPeriodicCycle newWeeklyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0 and advances month to month
	 * @param unit TimeUnit this cycle is attached to. 
	 * @return A monthly cycle
	 */
	public static ModelPeriodicCycle newMonthlyCycle(TimeUnit unit) {
		return new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MONTH, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0 and advances hour to hour
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using specified time unit) when this cycle starts
	 * @return An hourly cycle
	 */
	public static ModelPeriodicCycle newHourlyCycle(TimeUnit unit, long startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0 and advances day to day
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using specified time unit) when this cycle starts
	 * @return A daily cycle
	 */
	public static ModelPeriodicCycle newDailyCycle(TimeUnit unit, long startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0 and advances week to week
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using specified time unit) when this cycle starts
	 * @return A weekly cycle
	 */
	public static ModelPeriodicCycle newWeeklyCycle(TimeUnit unit, long startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0 and advances month to month
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp (using specified time unit) when this cycle starts
	 * @return A monthly cycle
	 */
	public static ModelPeriodicCycle newMonthlyCycle(TimeUnit unit, long startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MONTH, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0 and advances hour to hour
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return An hourly cycle
	 */
	public static ModelPeriodicCycle newHourlyCycle(TimeUnit unit, TimeStamp startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)), 0);
	}
	
	/**
	 * Creates a cycle that starts at 0 and advances day to day
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return A daily cycle
	 */
	public static ModelPeriodicCycle newDailyCycle(TimeUnit unit, TimeStamp startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0 and advances week to week
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return A weekly cycle
	 */
	public static ModelPeriodicCycle newWeeklyCycle(TimeUnit unit, TimeStamp startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)), 0);
	}

	/**
	 * Creates a cycle that starts at 0 and advances month to month
	 * @param unit TimeUnit this cycle is attached to. 
	 * @param startTs Timestamp when this cycle starts
	 * @return A monthly cycle
	 */
	public static ModelPeriodicCycle newMonthlyCycle(TimeUnit unit, TimeStamp startTs) {
		return new ModelPeriodicCycle(unit, startTs, new ModelTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MONTH, 1)), 0);
	}	
	
}
