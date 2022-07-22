/**
 * 
 */
package es.ull.iis.simulation.hta.params;

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
	 * @param initT The starting time of the period (in years from the beginning of the simulation)
	 * @param endT The ending time of the period (in years from the beginning of the simulation)
	 * @return A discounted value
	 */
	public double applyDiscount(double value, double initT, double endT) {
		return value * invLogDiscountRatePlus1 * (Math.pow(1 + discountRate, -endT) - Math.pow(1 + discountRate, -initT));
	}
	
	/**
	 * Apply a discount rate to a value at a specific moment of the simulation.
	 * @param value A value
	 * @param time Time when the discount is applied (in years from the beginning of the simulation) 
	 * @return A discounted value
	 */
	public double applyPunctualDiscount(double value, double time) {
		return value / Math.pow(1 + discountRate, time);
	}
	
}
