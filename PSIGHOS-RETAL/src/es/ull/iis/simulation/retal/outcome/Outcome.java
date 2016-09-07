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
	
	/**
	 * 
	 */
	public Outcome(RETALSimulation simul, String description, String unit, double discountRate) {
		this.simul = simul;
		this.description = description;
		this.unit = unit;
		this.discountRate = discountRate;
	}

	public abstract double[] getValue();

	public abstract void update(Patient pat, double value, double initAge, double endAge);
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
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @return the simul
	 */
	public RETALSimulation getSimul() {
		return simul;
	}

	@Override
	public String toString() {
		return description;
	}
}
