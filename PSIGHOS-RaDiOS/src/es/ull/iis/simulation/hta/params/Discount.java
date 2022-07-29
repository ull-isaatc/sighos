/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.costs.CostProducer;

/**
 * A class to represent a discount rate >= 0 and <= 1, applied to cost and effects during the simulation.
 * Includes the methods required to use such discount
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class Discount {
	/** A "null" discount calculator (discount rate = 0) */
	public static final Discount ZERO_DISCOUNT = new Discount(0.0) {
		public double applyDiscount(double value, double initAge, double endAge) {
			return value * (endAge - initAge);
		}
		
		public double applyPunctualDiscount(double value, double time) {
			return value;
		}		
	};
	/** The discount rate: a value between 0 and 1 */
	private final double discountRate;
	/** A precalculated value to improve efficiency of calculations */
	private final double invLogDiscountRatePlus1;

	/**
	 * Creates a discount calculator
	 * @param discountRate The discount rate: a value between 0 and 1
	 * @throws IllegalArgumentException If discountRate >= 1.0 or discountRate < 0.0 
	 */
	public Discount(final double discountRate) throws IllegalArgumentException {
		if (discountRate < 0.0 || discountRate >= 1.0) {
			throw new IllegalArgumentException("Discount rate must be >= 0.0 and < 1.0");
		}
		this.discountRate = discountRate;
		this.invLogDiscountRatePlus1 = (discountRate == 0.0) ? 0.0 : (-1 / Math.log(1 + discountRate));
	}

	/**
	 * Returns the discount rate: a value between 0 and 1
	 * @return the discount rate: a value between 0 and 1
	 */
	public double getDiscountRate() {
		return discountRate;
	}

	/**
	 * Apply a discount rate to a constant value over a time period by using a continuous approach. 
	 * @param value A constant value that applied each year
	 * @param initT The starting time of the period (in years from the beginning of the simulation)
	 * @param endT The ending time of the period (in years from the beginning of the simulation)
	 * @return A discounted value
	 */
	public double applyDiscount(double value, double initT, double endT) {
		return value * invLogDiscountRatePlus1 * (Math.pow(1 + discountRate, -endT) - Math.pow(1 + discountRate, -initT));
	}

	/**
	 * Apply a discount rate to a constant value over a time period by using an annual approach, i.e., divides the period
	 * of time in natural years and returns an array where each position represents the discounted amount for each year. If a period 
	 * results inferior than a year, computes the proportional discounted value. The discount applied for each period is that
	 * corresponding to the beginning of the year.
	 * @param value A constant value that applied each year
	 * @param initT The starting time of the period (in years from the beginning of the simulation)
	 * @param endT The ending time of the period (in years from the beginning of the simulation)
	 * @return An array where each position represents the discounted value for each natural year in the specified period
	 */
	public double[] applyAnnualDiscount(double value, double initT, double endT) {
		final double[] result = CostProducer.getIntervalsForPeriod(initT, endT);
		double annualDiscRate = 1 / Math.pow(1 + discountRate, (int) initT);
		for (int i = 0; i < result.length; i++) {
			result[i] *= value * annualDiscRate;
			annualDiscRate = annualDiscRate / (1 + discountRate);
		}
		return result;
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
