/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

/**
 * A interface to represent a discount rate applied to cost and effects during the simulation.
 * Includes the methods required to use such discount
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public interface Discount {
	public final static Discount zeroDiscount = new Discount() {
		
		@Override
		public double getDiscountRate() {
			return 0.0;
		}

		@Override
		public double applyDiscount(double value, double initAge, double endAge) {
			return value * (endAge - initAge);
		}
		
		@Override
		public double applyPunctualDiscount(double value, double time) {
			return value;
		}
	};

	/**
	 * @return the discount rate: a value between 0 and 1
	 */
	public double getDiscountRate();

	/**
	 * Apply a discount rate to a constant value over a time period. 
	 * @param value A constant value that applied each year
	 * @param initAge The age that the patient had when starting the period
	 * @param endAge The age that the patient had when ending the period
	 * @return A discounted value
	 */
	public double applyDiscount(double value, double initAge, double endAge);
	
	/**
	 * Apply a discount rate to a value at a specific moment of the simulation.
	 * @param value A value
	 * @param time The specific age when the discount is applied 
	 * @return A discounted value
	 */
	public double applyPunctualDiscount(double value, double time);
	
}
