package es.ull.iis.simulation.hta.outcomes;

/**
 * Defines different methods to combine disutilities in a patient
 * @author Iván Castilla Rodríguez
 *
 */
public enum DisutilityCombinationMethod {
	/** Additive method */
	ADD {
		@Override
		public double combine(double du1, double du2) {
			return du1 + du2;
		}
	},
	/** Takes the maximum among the disutilities */
	MAX {
		@Override
		public double combine(double du1, double du2) {
			if (du1 > du2)
				return du1;
			return du2;
		}
	};
	/**
	 * Combines two disutilities into a single value
	 * @param du1 First disutility
	 * @param du2 Second disutility
	 * @return The result of combining the two disutilities
	 */
	public abstract double combine(double du1, double du2);
	
}