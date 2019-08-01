/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

/**
 * A class to represent a discount rate >= 0 and <= 1, applied to cost and effects during the simulation.
 * Includes the methods required to use such discount
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StdDiscount implements Discount {
	/** The discount rate: a value between 0 and 1 */
	private final double discountRate;
	/** A precalculated value to improve efficiency of calculations */
	private final double invLogDiscountRatePlus1;

	/**
	 * Creates a discount rate
	 * @param discountRate The discount rate: a value between 0 and 1. If discountRate >= 1.0 or discountRate <= 0.0, it's set to 0.0. 
	 */
	public StdDiscount(final double discountRate) throws IllegalArgumentException {
		if (discountRate <= 0.0 || discountRate >= 1.0) {
			throw new IllegalArgumentException("Discount rate must be >= 0.0 and <= 1.0");
		}
		this.discountRate = discountRate;
		this.invLogDiscountRatePlus1 = -1 / Math.log(1 + discountRate);
	}

	/**
	 * @return the discount rate: a value between 0 and 1
	 */
	public double getDiscountRate() {
		return discountRate;
	}

	/**
	 * Apply a discount rate to a constant value over a time period. 
	 * @param value A constant value that applied each year
	 * @param initAge The age that the patient had when starting the period
	 * @param endAge The age that the patient had when ending the period
	 * @return A discounted value
	 */
	public double applyDiscount(double value, double initAge, double endAge) {
		return value * invLogDiscountRatePlus1 * (Math.pow(1 + discountRate, -endAge) - Math.pow(1 + discountRate, -initAge));
	}
	
	/**
	 * Apply a discount rate to a value at a specific moment of the simulation.
	 * @param value A value
	 * @param time The specific age when the discount is applied 
	 * @return A discounted value
	 */
	public double applyPunctualDiscount(double value, double time) {
		return value / Math.pow(1 + discountRate, time);
	}
	
}
