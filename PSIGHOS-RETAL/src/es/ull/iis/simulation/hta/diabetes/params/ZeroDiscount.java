/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

/**
 * A class to represent a null discount rate, i.e. discount = 0.0, applied to cost and effects during the simulation.
 * Includes the methods required to use such discount
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class ZeroDiscount implements Discount {

	/**
	 * Creates a null discount rate
	 */
	public ZeroDiscount() {
	}

	/**
	 * @return the discount rate: a value between 0 and 1
	 */
	public double getDiscountRate() {
		return 0.0;
	}

	public double applyDiscount(double value, double initAge, double endAge) {
		return value * (endAge - initAge);
	}
	
	public double applyPunctualDiscount(double value, double time) {
		return value;
	}
	
}
