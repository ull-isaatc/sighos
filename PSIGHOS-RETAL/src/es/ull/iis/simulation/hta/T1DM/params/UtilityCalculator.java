/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface UtilityCalculator {
	public enum DisutilityCombinationMethod {
		ADD {
			@Override
			public double combine(double du1, double du2) {
				return du1 + du2;
			}
		},
		MAX {
			@Override
			public double combine(double du1, double du2) {
				if (du1 > du2)
					return du1;
				return du2;
			}
		};
		public abstract double combine(double du1, double du2);
		
	}

	public double getHypoEventDisutilityValue();
	public double getUtilityValue(T1DMPatient pat);

}
