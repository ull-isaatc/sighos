/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * A class implementing this interface can calculate the disease-related utilities 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface UtilityCalculator {
	/**
	 * Defines different methods to combine disutilities in a patient
	 * @author Iv�n Castilla Rodr�guez
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

	/**
	 * Returns the disutility value for the specified acute event
	 * @param pat A patient
	 * @param manif The acute complication
	 * @return the disutility value for the specified acute event
	 */
	public double getPunctualDisutilityValue(Patient pat, Manifestation manif);
	
	/**
	 * Returns the utility value for a patient, taking into account the chronic manifestations he/she is suffering
	 * @param pat A patient
	 * @return the utility value for a patient, taking into account the chronic manifestations he/she is suffering
	 */
	public double getUtilityValue(Patient pat);

}
