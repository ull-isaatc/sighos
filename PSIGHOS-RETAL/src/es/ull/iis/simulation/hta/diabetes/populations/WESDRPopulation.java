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
public class WESDRPopulation extends DiabetesStdPopulation {
	/** Average and SD HbA1c in the population at baseline. */
	private static final double[] BASELINE_HBA1C = {10.6, 2.0}; 
	/** The average age at baseline */
	private static final double[] BASELINE_AGE = {26.8, 11.2}; 
	/** Duration of diabetes at baseline */
	private static final double[] BASELINE_DURATION = {12.6, 9.0}; 
	private static final double P_MAN = 0.492;  
	private static final double P_SMOKER = 0.249;
	/** SBP at baseline */
	private static final double[] BASELINE_SBP = {120, 16.0}; 

	/**
	 */
	public WESDRPopulation() {
		super(DiabetesType.T2);
	}

	@Override
	protected double getPMan() {
		return P_MAN;
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
		if (BasicConfigParams.USE_FIXED_BASELINE_DURATION_OF_DIABETES)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_DURATION[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION[0], BASELINE_DURATION[1]);
	}

	@Override
	protected double getPSmoker() {
		return P_SMOKER;
	}
	
	@Override
	protected RandomVariate getBaselineSBP() {
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_SBP[0], BASELINE_SBP[1]);
	}
	
}
