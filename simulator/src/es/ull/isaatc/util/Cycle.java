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
    /** How many times is this cycle executed. A value of 0 indicates 
     * infinite iterations. */
    protected int iterations = 0;
	/** Subcycle contained in this cycle. */
	protected Cycle subCycle = null;

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public Cycle(double startTs, RandomNumber period, double endTs) {
        this.startTs = startTs;
        this.period = period;
        this.endTs = endTs;
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
    public Cycle(double startTs, RandomNumber period, int iterations) {
        this.startTs = startTs;
        this.period = period;
        this.iterations = iterations;
    }
    
    /**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public Cycle(double startTs, RandomNumber period, double endTs, Cycle subCycle) {
		this.startTs = startTs;
		this.period = period;
		this.endTs = endTs;
		this.subCycle = subCycle;
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public Cycle(double startTs, RandomNumber period, int iterations, Cycle subCycle) {
		this.startTs = startTs;
		this.period = period;
		this.iterations = iterations;
		this.subCycle = subCycle;
	}

	/**
     * Returns the start timestamp of the cycle.
     * @return Value of property startTs.
     */
    public double getStartTs() {
        return startTs;
    }
    
    /**
	 * @return Returns the endTs.
	 */
	public double getEndTs() {
		return endTs;
	}

	/**
	 * @return Returns the period.
	 */
	public RandomNumber getPeriod() {
		return period;
	}

	/**
	 * @return Returns the iterations.
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @return Returns the subCycle.
	 */
	public Cycle getSubCycle() {
		return subCycle;
	}

	public CycleIterator iterator(double absStart, double absEnd) {
		return new CycleIterator(this, absStart, absEnd);
	}

}
