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

	public abstract void update(Patient pat);
	
	/**
	 * Apply a discount rate to a value. 
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
