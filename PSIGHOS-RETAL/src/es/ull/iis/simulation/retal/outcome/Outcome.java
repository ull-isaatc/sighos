/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Outcome {
	/** The simulation this outcome is used in */
	protected final RETALSimulation simul;
	/** A textual description of the outcome */
	private final String description;
	/** The unit used to measure the outcome */
	protected final String unit;
	protected final double discountRate;
	protected final double[]aggregated = new double[RETALSimulation.NINTERVENTIONS];
	
	/**
	 * 
	 */
	public Outcome(RETALSimulation simul, String description, String unit, double discountRate) {
		this.simul = simul;
		this.description = description;
		this.unit = unit;
		this.discountRate = discountRate;
	}

	/**
	 * Updates the value of this outcome for a specified period
	 * @param pat A patient
	 * @param value A constant value during the period
	 * @param initAge Initial age when the value is applied
	 * @param endAge End age when the value is applied
	 */
	public abstract void update(Patient pat, double value, double initAge, double endAge);
	/**
	 * Updates the value of this outcome at a specified age
	 * @param pat A patient
	 * @param value The value to update
	 * @param age The age at which the value is applied
	 */
	public abstract void update(Patient pat, double value, double age);
	
	public abstract void print(boolean detailed);
	
	/**
	 * Apply a discount rate to a constant value over a time period. 
	 * @param value A constant value that applied each year
	 * @param initAge The age that the patient had when starting the period
	 * @param endAge The age that the patient had when ending the period
	 * @return A discounted value
	 */
	protected double applyDiscount(double value, double initAge, double endAge) {
		if (discountRate == 0.0)
			return value * (endAge - initAge);
		return value * (-1 / Math.log(1 + discountRate)) * (Math.pow(1 + discountRate, -endAge) - Math.pow(1 + discountRate, -initAge));
	}
	
	/**
	 * Apply a discount rate to a value at a specific moment of the simulation.
	 * @param value A value
	 * @param time The specific age when the discount is applied 
	 * @return A discounted value
	 */
	protected double applyPunctualDiscount(double value, double time) {
		if (discountRate == 0.0)
			return value;
		return value / Math.pow(1 + discountRate, time);
	}
	
	public double getValue(int intervention) {
		return aggregated[intervention];
	}
	
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return description;
	}
}
