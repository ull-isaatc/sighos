/**
 * 
 */
package es.ull.iis.simulation.hta.params;

/**
 * A interface to represent a discount rate applied to cost and effects during the simulation.
 * Includes the methods required to use such discount
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public interface Discount {

	/**
	 * @return the discount rate: a value between 0 and 1
	 */
	public double getDiscountRate();

	/**
	 * Apply a discount rate to a constant value over a time period. 
	 * @param value A constant value that applied each year
	 * @param initT The starting time of the period (in years from the beginning of the simulation)
	 * @param endT The ending time of the period (in years from the beginning of the simulation)
	 * @return A discounted value
	 */
	public double applyDiscount(double value, double initT, double endT);
	
	/**
	 * Apply a discount rate to a value at a specific moment of the simulation.
	 * @param value A value
	 * @param time Time when the discount is applied (in years from the beginning of the simulation) 
	 * @return A discounted value
	 */
	public double applyPunctualDiscount(double value, double time);

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
	
}
