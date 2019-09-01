/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * From Klein et al. 10.1016/S0161-6420(98)91020-X
 * @author Iván Castilla Rodríguez
 *
 */
public class UKPDSPopulation extends DiabetesStdPopulation {
	/** Average and SD HbA1c in the population at baseline. */
	private static final double[] BASELINE_HBA1C = {7.2, 1.8}; 
	/** The average age at baseline */
	private static final double[] BASELINE_AGE = {52.1, 8.8}; 
	/** Duration of diabetes at baseline */
	private static final double BASELINE_DURATION = 0.0; 

	/**
	 */
	public UKPDSPopulation() {
		super(DiabetesType.T2);
	}

	@Override
	protected double getPMan() {
		return 0.59;
	}

	@Override
	protected RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
	}

	@Override
	protected RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_AGE[0], BASELINE_AGE[1]);
	}

	@Override
	protected RandomVariate getBaselineDurationOfDiabetes() {
		return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_DURATION);
	}

}
