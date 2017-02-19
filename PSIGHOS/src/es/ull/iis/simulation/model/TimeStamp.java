/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * A timed value expressed as a pair &lt{@link TimeUnit}, long}&gt.
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeStamp {
	/** The time unit of the value */ 
	private final TimeUnit unit;
	/** A time value expressed in the corresponding unit */ 
	private final long value;
	
	/**
	 * Creates a new timestamp.
	 * @param unit The time unit of the value
	 * @param value A time value expressed in the corresponding unit
	 */
	public TimeStamp(TimeUnit unit, long value) {
		this.unit = unit;
		this.value = value;
	}

	/**
	 * Returns the unit of this timestamp
	 * @return the unit of this timestamp
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * Returns the value of this timestamp
	 * @return the value of this timestamp
	 */
	public long getValue() {
		return value;
	}
	
	/**
	 * Converts the value of this timestamp to the specified unit.
	 * @param unit The unit of the new timestamp
	 * @return A new timestamp which represents the same value than this one but in a different unit
	 */
	public TimeStamp convert(TimeUnit unit) {
		return new TimeStamp(unit, unit.convert(this.value, this.unit));
	}

	/**
	 * Adds the specified timestamp to this timestamp
	 * @param op The adding timestamp
	 * @return A new timestamp whose value is the sum of this timestamp and the received one.
	 */
	public TimeStamp add(TimeStamp op) {
		return new TimeStamp(unit, value + op.convert(unit).value);
	}
	
	/**
	 * Multiplies this timestamp by the specified factor
	 * @param factor The multiplying timestamp
	 * @return A new timestamp whose value is the product of this timestamp and the received factor.
	 */
	public TimeStamp multiply(double factor) {
		return new TimeStamp(unit, Math.round(value * factor));		
	}
	
	@Override
	public String toString() {
		return "" + value + " " + unit.getName();
	}

	/**
	 * Returns a timestamp set to 0 (be default, the unit is MINUTE)
	 * @return a timestamp set to 0 MINUTE
	 */
	public static TimeStamp getZero() {
		return new TimeStamp(TimeUnit.MINUTE, 0);
	}
	
	/**
	 * Returns a "1 MINUTE" timestamp.
	 * @return A "1 MINUTE" timestamp
	 */
	public static TimeStamp getMinute() {
		return new TimeStamp(TimeUnit.MINUTE, 1);		
	}

	/**
	 * Returns a "1 HOUR" timestamp.
	 * @return A "1 HOUR" timestamp
	 */
	public static TimeStamp getHour() {
		return new TimeStamp(TimeUnit.HOUR, 1);		
	}

	/**
	 * Returns a "1 DAY" timestamp.
	 * @return A "1 DAY" timestamp
	 */
	public static TimeStamp getDay() {
		return new TimeStamp(TimeUnit.DAY, 1);		
	}

	/**
	 * Returns a "1 WEEK" timestamp.
	 * @return A "1 WEEK" timestamp
	 */
	public static TimeStamp getWeek() {
		return new TimeStamp(TimeUnit.WEEK, 1);		
	}

}
