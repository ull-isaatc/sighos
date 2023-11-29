/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import es.ull.iis.simulation.hta.Patient;

/**
 * A relative risk calculator that combines different relative risks
 * @author Iván Castilla
 *
 */
public class CompoundRRCalculator implements ParameterCalculator {
	/** The method used to combine the relative risks */
	public enum DEF_COMBINATION implements CombinationMethod {
		ADD {
			@Override
			public double combine(ParameterCalculator[] calculators, Patient pat) {
				double finalRR = 1.0;
				for (ParameterCalculator rr : calculators) {
					finalRR += rr.getValue(pat) - 1;
				}
				return finalRR;
			}			
		},
		MAX {
			@Override
			public double combine(ParameterCalculator[] calculators, Patient pat) {
				double finalRR = calculators[0].getValue(pat);
				for (int i = 1; i < calculators.length; i++) {
					final double newRR = calculators[i].getValue(pat);
					finalRR = (newRR > finalRR) ? newRR : finalRR;
				}
				return finalRR;
			}			
		}
		
	}
	/** The set of calculators to be combined */
	protected final ParameterCalculator[] calculators;
	/** Combination method used to combine the relative risks */
	private final CombinationMethod combMethod;

	/**
	 * Creates a relative risk calculator that combines several calculators
	 * @param calculators The set of calculators to be combined 
	 * @param combMethod Combination method used to combine the relative risks
	 */
	public CompoundRRCalculator(ParameterCalculator[] calculators, CombinationMethod combMethod) {
		this.calculators = calculators;
		this.combMethod = combMethod;
	}

	@Override
	public double getValue(Patient pat) {
		return combMethod.combine(calculators, pat);
	}

	/**
	 * A generic interface to implement other combination methods
	 * @author Iván Castilla Rodríguez
	 */
	public static interface CombinationMethod {
		double combine(ParameterCalculator[] calculators, Patient pat); 
	}
}
