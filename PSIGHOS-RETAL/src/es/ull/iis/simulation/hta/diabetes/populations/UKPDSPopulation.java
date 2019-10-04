/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * From Adler et al. 10.1046/j.1523-1755.2003.00712.x
 * @author Iván Castilla Rodríguez
 *
 */
public class UKPDSPopulation extends DiabetesStdPopulation {
	/** 0: Normal; 1: Gamma; 2: Gamma starting in min */
	private static final int HBA1C_FUNCTION = 2; 
	/** Average and SD HbA1c in the population at baseline. */
	private static final double[] BASELINE_HBA1C = {7.2, 1.8};
	/** Minimum and maximum expected values for baseline HbA1c, according to https://www.ncbi.nlm.nih.gov/pmc/articles/PMC27454/ */ 
	private static final double[] MIN_MAX_HBA1C = {4.6, 11.2};
	/** The average and SD age at baseline */
	private static final double[] BASELINE_AGE = {52.1, 8.8}; 
	/** Duration of diabetes at baseline */
	private static final double BASELINE_DURATION = 0.0; 
	/** The average and SD SBP at baseline */
	private static final double[] BASELINE_SBP = {135, 19.5}; 

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
		switch (HBA1C_FUNCTION) {
		case 0:
			return RandomVariateFactory.getInstance("NormalVariate", BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
		case 1:
			final double[]gammaParams = Statistics.gammaParametersFromNormal(BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
			return RandomVariateFactory.getInstance("GammaVariate", gammaParams[0], gammaParams[1]);
		case 2:
			final double[]gammaParams2 = Statistics.gammaParametersFromNormal(BASELINE_HBA1C[0] - MIN_MAX_HBA1C[0], BASELINE_HBA1C[1]);
			final RandomVariate gamma = RandomVariateFactory.getInstance("GammaVariate", gammaParams2[0], gammaParams2[1]);
			return RandomVariateFactory.getInstance("ScaledVariate", gamma, 1.0, MIN_MAX_HBA1C[0]);
		default:
			return RandomVariateFactory.getInstance("NormalVariate", BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
		}
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

	@Override
	protected RandomVariate getBaselineSBP() {
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_SBP[0], BASELINE_SBP[1]);
	}
	
	@Override
	protected double getPSmoker() {
		return 0.31; // Current smokers
	}
}
