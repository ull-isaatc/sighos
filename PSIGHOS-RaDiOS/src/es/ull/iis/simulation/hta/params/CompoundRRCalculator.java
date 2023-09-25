/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * A relative risk calculator that combines different relative risks
 * @author Iván Castilla
 *
 */
public class CompoundRRCalculator implements RRCalculator {
	/** The method used to combine the relative risks */
	public enum DEF_COMBINATION implements CombinationMethod {
		ADD {
			@Override
			public double combine(RRCalculator[] calculators, Patient pat) {
				double finalRR = 1.0;
				for (RRCalculator rr : calculators) {
					finalRR += rr.getRR(pat) - 1;
				}
				return finalRR;
			}			
		},
		MAX {
			@Override
			public double combine(RRCalculator[] calculators, Patient pat) {
				double finalRR = calculators[0].getRR(pat);
				for (int i = 1; i < calculators.length; i++) {
					final double newRR = calculators[i].getRR(pat);
					finalRR = (newRR > finalRR) ? newRR : finalRR;
				}
				return finalRR;
			}			
		}
		
	}
	/** The set of calculators to be combined */
	protected final RRCalculator[] calculators;
	/** Combination method used to combine the relative risks */
	private final CombinationMethod combMethod;

	/**
	 * Creates a relative risk calculator that combines several calculators
	 * @param calculators The set of calculators to be combined 
	 * @param combMethod Combination method used to combine the relative risks
	 */
	public CompoundRRCalculator(RRCalculator[] calculators, CombinationMethod combMethod) {
		this.calculators = calculators;
		this.combMethod = combMethod;
	}

	@Override
	public double getRR(Patient pat) {
		return combMethod.combine(calculators, pat);
	}

	/**
	 * A generic interface to implement other combination methods
	 * @author Iván Castilla Rodríguez
	 */
	public static interface CombinationMethod {
		double combine(RRCalculator[] calculators, Patient pat); 
	}
}
