/**
 * 
 */
package es.ull.isaatc.simulation.common;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Time {
	private TimeUnit unit;
	private double value;
	
	/**
	 * @param unit
	 * @param value
	 */
	public Time(TimeUnit unit, double value) {
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
	public double getValue() {
		return value;
	}
	
	public Time convert(TimeUnit unit) {
		return new Time(unit, unit.convert(this.value, this.unit));
	}

	public Time add(Time op) {
		return new Time(unit, value + op.convert(unit).value);
	}
	
	public Time multiply(double factor) {
		return new Time(unit, value * factor);		
	}
	
	@Override
	public String toString() {
		return "" + value + " " + unit.getName();
	}
	
	public static Time getZero() {
		return new Time(TimeUnit.MINUTE, 0.0);
	}
	
	public static Time getMinute() {
		return new Time(TimeUnit.MINUTE, 1.0);		
	}

	public static Time getHour() {
		return new Time(TimeUnit.HOUR, 1.0);		
	}

	public static Time getDay() {
		return new Time(TimeUnit.DAY, 1.0);		
	}

}
