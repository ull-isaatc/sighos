/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * @author Iván Castilla
 *
 */
public class CompoundRRCalculator implements RRCalculator {
	public enum DEF_COMBINATION implements CombinationMethod {
		ADD {
			@Override
			public double combine(RRCalculator[] calculators, DiabetesPatient pat) {
				double finalRR = 1.0;
				for (RRCalculator rr : calculators) {
					finalRR += rr.getRR(pat) - 1;
				}
				return finalRR;
			}			
		},
		MAX {
			@Override
			public double combine(RRCalculator[] calculators, DiabetesPatient pat) {
				double finalRR = calculators[0].getRR(pat);
				for (int i = 1; i < calculators.length; i++) {
					final double newRR = calculators[i].getRR(pat);
					finalRR = (newRR > finalRR) ? newRR : finalRR;
				}
				return finalRR;
			}			
		}
		
	}
	protected final RRCalculator[] calculators;
	private final CombinationMethod combMethod;

	/**
	 * 
	 */
	public CompoundRRCalculator(RRCalculator[] calculators, CombinationMethod combMethod) {
		this.calculators = calculators;
		this.combMethod = combMethod;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.params.RRCalculator#getRR(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getRR(DiabetesPatient pat) {
		return combMethod.combine(calculators, pat);
	}

	public static interface CombinationMethod {
		double combine(RRCalculator[] calculators, DiabetesPatient pat); 
	}
}
