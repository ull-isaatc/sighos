/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

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
	// FIXME No calcula bien
	public double[] applyAnnualDiscount(double value, double initT, double endT) {
		int naturalYear = (int) initT;
		final int nIntervals = (int) endT - naturalYear + (int) Math.ceil(endT - (int) endT); 
		double annualDiscRate = 1 / Math.pow(1 + discountRate, naturalYear);
		// If both the initT and endT belong to the same natural year
		if (nIntervals == 1)
			return new double[] {value * (endT - initT) * annualDiscRate};
		final double[] result = new double[nIntervals];
		// Process the first interval
		result[0] = value * (naturalYear + 1 - initT) * annualDiscRate;
		// Process the intermediate intervals, corresponding to full years 
		for (int i = 1; i < nIntervals - 1; i++, annualDiscRate /= (1 + discountRate)) {
			result[i] = value * annualDiscRate;
		}
		// Process the last interval
		result[nIntervals - 1] = value * (endT - (int) endT + 1) * annualDiscRate / (1 + discountRate);
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
	
	/**
	 * Given an initial time (initT) and ending time (endT), returns the next interval 
	 * within a natural year for such period. For example, if initT = 0.5 and endT >= 1.0,
	 * the result will be {0.5, 1.0}; for initT = 2.1 and endT = 2.5, the result will be
	 * {2.1, 2.5}, and so on.    
	 * @param initT The starting time of the period (in years from the beginning of the simulation)
	 * @param endT The ending time of the period (in years from the beginning of the simulation)
	 * @return The next time interval within a natural year for the period {initT, endT}; null
	 * if initT >= endT
	 */
	public static double[] getNextAnnualInterval(double initT, double endT) {
		if (initT >= endT)
			return null;
		return new double[] {initT, Math.min(endT, (int)initT+1)};
	}
	
	public static void testPeriod(Discount disc, double value, double initT, double endT) {
		System.out.println("CONTINUOUS: " + initT + "-" + endT + ":\t\t" + disc.applyDiscount(value, initT, endT));
		double [] result = disc.applyAnnualDiscount(value, initT, endT);
		double total = 0.0;
		for (double val : result)
			total += val;
		System.out.println("ANNUAL (per year): " + initT + "-" + endT + ":\t" + Arrays.toString(result)); 
		System.out.println("ANNUAL (total): " + initT + "-" + endT + ":\t" + total);
		System.out.println();
	}
	
	public static void main(String[] args) {
		final double value = 100.0;
		final double discountRate = 0.03;
		final Discount disc = new Discount(discountRate);
		System.out.println("VALUE: " + value + " DISCOUNTED AT: " + discountRate);
		testPeriod(disc, value, 0.5, 0.8);
		testPeriod(disc, value, 0.0, 1.0);
		testPeriod(disc, value, 0.5, 3.8);
		testPeriod(disc, value, 0.0, 11.0);
		testPeriod(disc, value, 2.3, 9.8);
	}
}
