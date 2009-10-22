/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationTime {
	private SimulationTimeUnit unit;
	private double value;
	
	/**
	 * @param unit
	 * @param value
	 */
	public SimulationTime(SimulationTimeUnit unit, double value) {
		this.unit = unit;
		this.value = value;
	}

	/**
	 * @return the unit
	 */
	public SimulationTimeUnit getUnit() {
		return unit;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	
	public SimulationTime convert(SimulationTimeUnit unit) {
		return new SimulationTime(unit, unit.convert(this.value, this.unit));
	}
	
	public SimulationTime add(SimulationTime op) {
		return new SimulationTime(unit, value + op.convert(unit).value);
	}
	
	public SimulationTime multiply(double factor) {
		return new SimulationTime(unit, value * factor);		
	}
	
	@Override
	public String toString() {
		return "" + value + " " + unit.getName();
	}
	
	public static SimulationTime getZero() {
		return new SimulationTime(SimulationTimeUnit.MINUTE, 0.0);
	}
	
	public static SimulationTime getMinute() {
		return new SimulationTime(SimulationTimeUnit.MINUTE, 1.0);		
	}

	public static SimulationTime getHour() {
		return new SimulationTime(SimulationTimeUnit.HOUR, 1.0);		
	}

	public static SimulationTime getDay() {
		return new SimulationTime(SimulationTimeUnit.DAY, 1.0);		
	}

}
