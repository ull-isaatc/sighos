/**
 * 
 */
package es.ull.isaatc.util;

import es.ull.isaatc.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class RoundedPeriodicCycle extends PeriodicCycle {
	public enum Type {
		ROUND,
		CEIL,
		FLOOR;
		
		double getValue(double value, double factor) {
			switch(this) {
				case ROUND: return ExtendedMath.round(value, factor);
				case CEIL:  return ExtendedMath.ceil(value, factor);
				case FLOOR:  return ExtendedMath.floor(value, factor);
			}
			throw new AssertionError("Unknown op: " + this);			
		}
	}
	private Type type = Type.ROUND;
	private double factor = 1.0;

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Type type, double factor) {
		super(startTs, period, endTs);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Type type, double factor) {
		super(startTs, period, iterations);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Cycle subCycle, Type type, double factor) {
		super(startTs, period, endTs, subCycle);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Cycle subCycle, Type type, double factor) {
		super(startTs, period, iterations, subCycle);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * @return the factor
	 */
	public double getFactor() {
		return factor;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.util.Cycle#getIteratorLevel(es.ull.isaatc.util.Cycle.CycleIterator, double, double)
	 */
	@Override
	protected IteratorLevel getIteratorLevel(double start, double end) {
		return new RoundedPeriodicIteratorLevel(start, end);
	}

	/**
	 * Represents a level in the cycle structure. Each level is a subcycle.
	 * @author Iván Castilla Rodríguez
	 */
	protected class RoundedPeriodicIteratorLevel extends PeriodicCycle.PeriodicIteratorLevel {
		/**
		 * @param start The start timestamp.
		 * @param end The end timestamp.
		 */
		public RoundedPeriodicIteratorLevel(double start, double end) {
			super(start, end);
		}		

		@Override
		public double getNextTs() {
			return type.getValue(nextTs, factor);
		}
		
		@Override
		public double getEndTs() {
			return type.getValue(endTs, factor);
		}
		
		@Override
		public double next() {
			return type.getValue(super.next(), factor);
		}
	}
	
}
