/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class Time {
	private TimeUnit unit;
	private long value;
	
	/**
	 * @param unit
	 * @param value
	 */
	public Time(TimeUnit unit, long value) {
		this.unit = unit;
		this.value = value;
	}

	/**
	 * @return the unit
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}
	
	public Time convert(TimeUnit unit) {
		return new Time(unit, unit.convert(this.value, this.unit));
	}

	public Time add(Time op) {
		return new Time(unit, value + op.convert(unit).value);
	}
	
	public Time multiply(double factor) {
		return new Time(unit, Math.round(value * factor));		
	}
	
	@Override
	public String toString() {
		return "" + value + " " + unit.getName();
	}
	
	public static Time getZero() {
		return new Time(TimeUnit.MINUTE, 0);
	}
	
	public static Time getMinute() {
		return new Time(TimeUnit.MINUTE, 1);		
	}

	public static Time getHour() {
		return new Time(TimeUnit.HOUR, 1);		
	}

	public static Time getDay() {
		return new Time(TimeUnit.DAY, 1);		
	}

}