package es.ull.isaatc.util;

import es.ull.isaatc.random.RandomNumber;

/**
 * Defines a periodically repeated sequence of events. It can be defined in 
 * two different ways: onthe one hand, you can define a cycle that stops when 
 * a particular timestamp (endTs) is reached; on the other hand, you can 
 * define a cycle which stops after an specific number of iterations. You can 
 * even define an infinuite cycle (0 iterations).<p>
 * A cycle can be defined as containing a subcycle. The subcycle total duration 
 * should be restricted to the main cycle period. 
 * @author Iván Castilla Rodríguez
 */
public class Cycle {
	/** Relative time when this cycle is expected to start. */
	protected double startTs;
	/** Relative time when this cycle is expected to finish. */
    protected double endTs = Double.NaN;
    /** Time interval between two successive ocurrences of an event. */
    protected RandomNumber period;
    /** How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations. */
    protected int iterations = 0;
	/** Subcycle contained in this cycle. */
	protected Cycle subCycle = null;

	/**
	 * Creates a cycle which ends at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive ocurrences of an event.
	 * @param endTs Relative time when this cycle is expected to finish.
	 */
	public Cycle(double startTs, RandomNumber period, double endTs) {
        this.startTs = startTs;
        this.period = period;
        this.endTs = endTs;
	}

	/**
	 * Creates a cycle which is executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive ocurrences of an event.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 */
    public Cycle(double startTs, RandomNumber period, int iterations) {
        this.startTs = startTs;
        this.period = period;
        this.iterations = iterations;
    }
    
    /**
	 * Creates a cycle which ends at the specified timestamp and contains a subcycle.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive ocurrences of an event.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param subCycle Subcycle contained in this cycle.
	 */
	public Cycle(double startTs, RandomNumber period, double endTs, Cycle subCycle) {
		this.startTs = startTs;
		this.period = period;
		this.endTs = endTs;
		this.subCycle = subCycle;
	}

	/**
	 * Creates a cycle which is executed the specified iterations and contains a subcycle.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive ocurrences of an event.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param subCycle Subcycle contained in this cycle.
	 */
	public Cycle(double startTs, RandomNumber period, int iterations, Cycle subCycle) {
		this.startTs = startTs;
		this.period = period;
		this.iterations = iterations;
		this.subCycle = subCycle;
	}

	/**
     * Returns the start timestamp of the cycle.
     * @return The start timestamp of the cycle.
     */
    public double getStartTs() {
        return startTs;
    }
    
    /**
     * Returns the end timestamp of the cycle.
	 * @return The end timestamp of the cycle.
	 */
	public double getEndTs() {
		return endTs;
	}

	/**
	 * Returns the time interval between two successive ocurrences of an event.
	 * @return The time interval between two successive ocurrences of an event.
	 */
	public RandomNumber getPeriod() {
		return period;
	}

	/**
	 * Returns how many times this cycle is executed.
	 * @return How many times this cycle is executed.
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * Returns the subCycle contained in this cycle (if exists).
	 * @return The subCycle contained in this cycle (if exists).
	 */
	public Cycle getSubCycle() {
		return subCycle;
	}

	/**
	 * Returns an iterator over this cycle.
	 * @param absStart Absolute start timestamp 
	 * @param absEnd Absolute end timestamp
	 * @return An iterator over this cycle.
	 */
	public CycleIterator iterator(double absStart, double absEnd) {
		return new CycleIterator(this, absStart, absEnd);
	}
}
