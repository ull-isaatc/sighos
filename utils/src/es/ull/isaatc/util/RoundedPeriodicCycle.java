/**
 * 
 */
package es.ull.isaatc.util;

import es.ull.isaatc.function.TimeFunction;

/**
 * An special periodic cycle which rounds (or trunks) the incidence of events.
 * It uses three attributes<ul>
 * <li><code>type</code> can be ROUND, CEIL or FLOOR, depending on if you need to 
 * round, get the ceil or the floor of the value;</li>
 * <li><code>factor</code> indicates the value to adjust the result: what you're 
 * going to get are multiples of the factor;</li>
 * <li><code>shift</code> sets a displacement to be added to the result.</li></ul>  
 * The key point of this cycle is that it preserves the behavior of the 
 * underlying period, but it rounds the events. For example, suppose that you 
 * have a periodic cycle which generates events in 1, 4, 10, 11, 14... The result
 * of a rounded periodic cycle with factor 5.0, shift 1.0 and type ROUND would be
 * 1, 6, 11, 16, 16. 
 * @author Iván Castilla Rodríguez
 */
public class RoundedPeriodicCycle extends PeriodicCycle {
	/**
	 * Different ways the incidence of events can be treated:
	 * <ul>
	 * <li>ROUND: Events are going to be rounded. 1.1 is 1 and 1.6 is 2</li>
	 * <li>CEIL: Events are going to be ceiled. 1.1 is 2 and 1.6 is 2</li>
	 * <li>FLOOR: Events are going to be floored. 1.1 is 1 and 1.6 is 1</li>
	 * @author Iván Castilla Rodríguez
	 */
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
	/**
	 * The way the incidence of events is going to be treated.
	 */
	private Type type = Type.ROUND;
	/**
	 * The factor to which the results are fitted. The results are always 
	 * multiples of this value.
	 */
	private double factor = 1.0;
	/**
	 * The value to be added to the results.
	 */
	private double shift = 0.0;

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Type type, double factor) {
		super(startTs, period, endTs);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * Creates a cycle which "rounds" the values that it returns and is 
	 * executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Type type, double factor) {
		super(startTs, period, iterations);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param subCycle Subcycle contained in this cycle.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Cycle subCycle, Type type, double factor) {
		super(startTs, period, endTs, subCycle);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * Creates a cycle which "rounds" the values that it returns and is 
	 * executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param subCycle Subcycle contained in this cycle.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Cycle subCycle, Type type, double factor) {
		super(startTs, period, iterations, subCycle);
		this.type = type;
		this.factor = factor;
	}

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Type type, double factor, double shift) {
		super(startTs, period, endTs);
		this.type = type;
		this.factor = factor;
		this.shift = shift;
	}

	/**
	 * Creates a cycle which "rounds" the values that it returns and is 
	 * executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Type type, double factor, double shift) {
		super(startTs, period, iterations);
		this.type = type;
		this.factor = factor;
		this.shift = shift;
	}

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param subCycle Subcycle contained in this cycle.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Cycle subCycle, Type type, double factor, double shift) {
		super(startTs, period, endTs, subCycle);
		this.type = type;
		this.factor = factor;
		this.shift = shift;
	}

	/**
	 * Creates a cycle which "rounds" the values that it returns and is 
	 * executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param subCycle Subcycle contained in this cycle.
	 * @param type The way the events are going to be treated.
	 * @param factor The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Cycle subCycle, Type type, double factor, double shift) {
		super(startTs, period, iterations, subCycle);
		this.type = type;
		this.factor = factor;
		this.shift = shift;
	}

	/**
	 * Returns the factor to which the results are fitted.
	 * @return The factor to which the results are fitted.
	 */
	public double getFactor() {
		return factor;
	}

	/**
	 * Returns the shift added to each result
	 * @return the shift added to each result
	 */
	public double getShift() {
		return shift;
	}

	/**
	 * Returns the way the cycle treats the incidence of events.
	 * @return The way the cycle treats the incidence of events.
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
			return type.getValue(nextTs, factor) + shift;
		}
		
		@Override
		public double getEndTs() {
			return type.getValue(endTs, factor) + shift;
		}
		
		@Override
		public double next() {
			return type.getValue(super.next(), factor) + shift;
		}
	}
	
}
