/**
 * 
 */
package es.ull.iis.util;

import es.ull.iis.function.TimeFunction;

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
	final private Type type;
	/**
	 * The factor to which the results are fitted. The results are always 
	 * multiples of this value.
	 */
	private final double scale;
	/**
	 * The value to be added to the results.
	 */
	private final double shift;

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Type type, double scale) {
		this(startTs, period, endTs, type, scale, 0.0);
	}

	/**
	 * Creates a cycle which "rounds" the values that it returns and is 
	 * executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Type type, double scale) {
		this(startTs, period, iterations, type, scale, 0.0);
	}

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param subCycle Subcycle contained in this cycle.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Cycle subCycle, Type type, double scale) {
		this(startTs, period, endTs, subCycle, type, scale, 0.0);
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
	 * @param scale The factor to which the results are fitted. 
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Cycle subCycle, Type type, double scale) {
		this(startTs, period, iterations, subCycle, type, scale, 0.0);
	}

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Type type, double scale, double shift) {
		super(startTs, period, endTs);
		this.type = type;
		this.scale = scale;
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
	 * @param scale The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Type type, double scale, double shift) {
		super(startTs, period, iterations);
		this.type = type;
		this.scale = scale;
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
	 * @param scale The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, double endTs, Cycle subCycle, Type type, double scale, double shift) {
		super(startTs, period, endTs, subCycle);
		this.type = type;
		this.scale = scale;
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
	 * @param scale The factor to which the results are fitted. 
	 * @param shift The value added to the final result
	 */
	public RoundedPeriodicCycle(double startTs, TimeFunction period, int iterations, Cycle subCycle, Type type, double scale, double shift) {
		super(startTs, period, iterations, subCycle);
		this.type = type;
		this.scale = scale;
		this.shift = shift;
	}

	/**
	 * Returns the factor to which the results are fitted.
	 * @return The factor to which the results are fitted.
	 */
	public double getScale() {
		return scale;
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

	@Override
	protected IteratorLevel getIteratorLevel(double start, double end) {
		return new RoundedPeriodicIteratorLevel(start, end);
	}

	@Override
	protected DiscreteIteratorLevel getDiscreteIteratorLevel(long start, long end) {
		return new RoundedPeriodicDiscreteIteratorLevel(start, end);
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
			return type.getValue(super.getNextTs(), scale) + shift;
		}
		
		@Override
		public double getEndTs() {
			return type.getValue(endTs, scale) + shift;
		}
		
		@Override
		public double next() {
			return type.getValue(super.next(), scale) + shift;
		}
	}
	
	/**
	 * Represents a level in the cycle structure. Each level is a subcycle.
	 * @author Iván Castilla Rodríguez
	 */
	protected class RoundedPeriodicDiscreteIteratorLevel extends PeriodicCycle.PeriodicDiscreteIteratorLevel {
		/**
		 * @param start The start timestamp.
		 * @param end The end timestamp.
		 */
		public RoundedPeriodicDiscreteIteratorLevel(long start, long end) {
			super(start, end);
		}		

		@Override
		public long getNextTs() {
			return (long) (type.getValue(super.getNextTs(), scale) + shift);
		}
		
		@Override
		public long getEndTs() {
			return (long) (type.getValue(endTs, scale) + shift);
		}
		
		@Override
		public long next() {
			return (long) (type.getValue(super.next(), scale) + shift);
		}
	}
	
}
