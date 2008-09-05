/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iv�n Castilla Rodr�guez
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
	
	@Override
	public String toString() {
		return "" + value + " " + unit.getName();
	}
	
	public static SimulationTime getZero() {
		return new SimulationTime(SimulationTimeUnit.MINUTE, 0.0);
	}
}
