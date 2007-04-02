package es.ull.isaatc.util;

import es.ull.isaatc.function.TimeFunction;

/**
 * Defines a periodically repeated sequence of events. It can be defined in 
 * two different ways: onthe one hand, you can define a cycle that stops when 
 * a particular timestamp (endTs) is reached; on the other hand, you can 
 * define a cycle which stops after an specific number of iterations. You can 
 * even define an infinite cycle (0 iterations).<p>
 * A cycle can be defined as containing a subcycle. The subcycle total duration 
 * should be restricted to the main cycle period. 
 * @author Iván Castilla Rodríguez
 */
public class PeriodicCycle extends Cycle {
	/** Relative time when this cycle is expected to start. */
	protected double startTs;
	/** Relative time when this cycle is expected to finish. */
    protected double endTs = Double.NaN;
    /** Time interval between two successive ocurrences of an event. */
    protected TimeFunction period;
    /** How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations. */
    protected int iterations = 0;

	/**
	 * Creates a cycle which ends at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive ocurrences of an event.
	 * @param endTs Relative time when this cycle is expected to finish.
	 */
	public PeriodicCycle(double startTs, TimeFunction period, double endTs) {
		super();
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
    public PeriodicCycle(double startTs, TimeFunction period, int iterations) {
    	super();
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
	public PeriodicCycle(double startTs, TimeFunction period, double endTs, Cycle subCycle) {
		super(subCycle);
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
	public PeriodicCycle(double startTs, TimeFunction period, int iterations, Cycle subCycle) {
		super(subCycle);
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
	public TimeFunction getPeriod() {
		return period;
	}

	/**
	 * Returns how many times this cycle is executed.
	 * @return How many times this cycle is executed.
	 */
	public int getIterations() {
		return iterations;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("Periodic Cycle. Start: " + startTs);
		if (!Double.isNaN(endTs))
			str.append("\tEnd: " + endTs);
		else
			str.append("\tIterations: " + iterations);
		if (subCycle != null)
			str.append("\r\n" + subCycle.toString());
		return str.toString();
	}
}
