package es.ull.iis.util;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionParams;

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
	protected final double startTs;
	/** Relative time when this cycle is expected to finish. */
    protected final double endTs;
    /** Time interval between two successive ocurrences of an event. */
    protected final TimeFunction period;
    /** How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations. */
    protected final int iterations;

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
        this.iterations = 0;
	}

	/**
	 * Creates a cycle which is executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 */
    public PeriodicCycle(double startTs, TimeFunction period, int iterations) {
    	super();
        this.startTs = startTs;
        this.period = period;
        this.iterations = iterations;
        this.endTs = Double.NaN;
    }
    
    /**
	 * Creates a cycle which ends at the specified timestamp and contains a subcycle.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param subCycle Subcycle contained in this cycle.
	 */
	public PeriodicCycle(double startTs, TimeFunction period, double endTs, Cycle subCycle) {
		super(subCycle);
		this.startTs = startTs;
		this.period = period;
		this.endTs = endTs;
        this.iterations = 0;
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
        this.endTs = Double.NaN;
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

	/* (non-Javadoc)
	 * @see es.ull.iis.util.Cycle#getIteratorLevel(es.ull.iis.util.Cycle.CycleIterator, double, double)
	 */
	@Override
	protected IteratorLevel getIteratorLevel(double start, double end) {
		return new PeriodicIteratorLevel(start, end);
	}

	@Override
	protected DiscreteIteratorLevel getDiscreteIteratorLevel(long start, long end) {
		return new PeriodicDiscreteIteratorLevel(start, end);
	}

	/**
	 * Represents a level in the cycle structure. Each level is a subcycle.
	 * @author Iván Castilla Rodríguez
	 */
	protected class PeriodicIteratorLevel extends Cycle.IteratorLevel {
		/** The next timestamp. */
		private double nextTs;
		/** The iterations left. */
		private int iter;
		final private TimeFunctionParams param;
		
		/**
		 * @param start The start timestamp.
		 * @param end The end timestamp.
		 */
		public PeriodicIteratorLevel(double start, double end) {
			super(start, end);
			param = new TimeFunctionParams() {
				@Override
				public double getTime() {
					return currentTs;
				}
			};
		}		

		@Override
		public double getNextTs() {
			return nextTs;
		}
		
		@Override
		public double reset(double start, double newEnd) {
			currentTs = start;
			this.iter = getIterations();
			// Bad defined subcycles are controled here
			if (!Double.isNaN(PeriodicCycle.this.getEndTs()))
				endTs = Math.min(start + PeriodicCycle.this.getEndTs(), newEnd);
			else
				endTs = newEnd;
			// If the "supercycle" starts after the simulation end.
			if (Double.isNaN(newEnd)) {
				nextTs = Double.NaN;
				currentTs = nextTs;
			}
			else {
				nextTs = start + getStartTs();
				// If the cycle starts after the simulation end
				if (hasNext())
					currentTs = next();
				else
					nextTs = Double.NaN;
			}
			return currentTs;
		}

		@Override
		public boolean hasNext() {
			if (Double.isNaN(nextTs))
				return false;
			// If the next timestamp to be generated is not valid
			if (getNextTs() >= endTs)
				return false;
			// If there are infinite iterations
			if (getIterations() == 0)
				return true;
			// If there are n iterations and we have reached the last iteration
			if (iter == 0)
				return false;
			// In other case
			return true;
		}

		@Override
		public double next() {
			currentTs = nextTs;
			if (iter > 0)
				iter--;
			// Computes the next valid timestamp...
			nextTs += getPeriod().getValue(param);
			return currentTs;
		}
	}

	/**
	 * Represents a level in the cycle structure. Each level is a subcycle.
	 * @author Iván Castilla Rodríguez
	 */
	protected class PeriodicDiscreteIteratorLevel extends Cycle.DiscreteIteratorLevel {
		/** The next timestamp. */
		private long nextTs;
		/** The iterations left. */
		private int iter;
		private long cycleEndTs;
		private long cycleStartTs;
		final private TimeFunctionParams param;
		
		/**
		 * @param start The start timestamp.
		 * @param end The end timestamp.
		 */
		public PeriodicDiscreteIteratorLevel(long start, long end) {
			currentTs = start;
			cycleEndTs = Math.round(PeriodicCycle.this.getEndTs());
			cycleStartTs = Math.round(PeriodicCycle.this.getStartTs()); 
			this.iter = getIterations();
			// Bad defined subcycles are controled here
			if (!Double.isNaN(PeriodicCycle.this.getEndTs()))
				endTs = Math.min(start + cycleEndTs, end);
			else
				endTs = end;
			// If the "supercycle" starts after the simulation end.
			if (end == -1) {
				nextTs = -1;
				currentTs = nextTs;
			}
			else {
				nextTs = start + cycleStartTs;
				// If the cycle starts after the simulation end
				if (hasNext())
					currentTs = next();
				else
					nextTs = -1;
			}
			param = new TimeFunctionParams() {
				@Override
				public double getTime() {
					return currentTs;
				}
			};
		}		

		@Override
		public long getNextTs() {
			return nextTs;
		}
		
		@Override
		public long reset(long start, long newEnd) {
			currentTs = start;
			this.iter = getIterations();
			// Bad defined subcycles are controled here
			if (!Double.isNaN(PeriodicCycle.this.getEndTs()))
				endTs = Math.min(start + cycleEndTs, newEnd);
			else
				endTs = newEnd;
			// If the "supercycle" starts after the simulation end.
			if (newEnd == -1) {
				nextTs = -1;
				currentTs = nextTs;
			}
			else {
				nextTs = start + cycleStartTs;
				// If the cycle starts after the simulation end
				if (hasNext())
					currentTs = next();
				else
					nextTs = -1;
			}
			return currentTs;
		}

		@Override
		public boolean hasNext() {
			if (Double.isNaN(nextTs))
				return false;
			// If the next timestamp to be generated is not valid
			if (getNextTs() >= endTs)
				return false;
			// If there are infinite iterations
			if (getIterations() == 0)
				return true;
			// If there are n iterations and we have reached the last iteration
			if (iter == 0)
				return false;
			// In other case
			return true;
		}

		@Override
		public long next() {
			currentTs = nextTs;
			if (iter > 0)
				iter--;
			// Computes the next valid timestamp...
			nextTs += getPeriod().getValue(param);
			return currentTs;
		}
	}
}
