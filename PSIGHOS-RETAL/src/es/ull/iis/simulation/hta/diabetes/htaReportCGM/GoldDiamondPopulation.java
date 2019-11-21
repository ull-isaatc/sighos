/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.htaReportCGM;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Combined characteristics from the GOLD and DIAMOND studies. Both compared DEXCOM G4 on T1DM population with uncontrolled HbA1c.
 * Sources:
 * <ul>
 * <li>Lind M, Polonsky W, Hirsch IB, Heise T, Bolinder J, Dahlqvist S, et al. Continuous glucose monitoring vs conventional therapy for glycemic control in adults with type 1 diabetes treated with multiple daily insulin injections the gold randomized clinical trial. JAMA - J Am Med Assoc. 2017;317(4):379–87.</li>
 * <li>Beck RW, Riddlesworth T, Ruedy K, Ahmann A, Bergenstal R, Haller S, et al. Effect of continuous glucose monitoring on glycemic control in adults with type 1 diabetes using insulin injections the diamond randomized clinical trial. JAMA - J Am Med Assoc. 2017;317(4):371–8.</li>
 * </ul>
 * @author Iván Castilla
 *
 */
public class GoldDiamondPopulation extends DiabetesStdPopulation {
	/** Mean and SD of baseline HbA1c. SD is the highest from all the cohorts */
	private static final double []BASELINE_HBA1C = {8.5382, 0.9};
	/** Mean and SD of baseline age. SD is the highest from all the cohorts */
	private static final double []BASELINE_AGE = {46.217, 14};
	/** Minimum and maximum of baseline age. From DIAMOND */
	private static final double []MIN_MAX_BASELINE_AGE = {26, 73};
	/** Mean and SD of baseline duration of diabetes. SD from GOLD */
	private static final double []BASELINE_DURATION = {22.16619718, 11.8};
	
	/**
	 *
	 */
	public GoldDiamondPopulation() {
		super(DiabetesType.T1);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getPMan()
	 */
	@Override
	protected double getPMan() {
		return 168.0 / 300.0;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineHBA1c()
	 */
	@Override
	protected RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineAge()
	 */
	@Override
	protected RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE[0]);

		final double mode = Statistics.betaModeFromMeanSD(BASELINE_AGE[0], BASELINE_AGE[1]);
		
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_AGE[0], mode, MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[1]);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_BASELINE_AGE[1] - MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[0]);			
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineDurationOfDiabetes()
	 */
	@Override
	protected RandomVariate getBaselineDurationOfDiabetes() {
		if (BasicConfigParams.USE_FIXED_BASELINE_DURATION_OF_DIABETES)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_DURATION[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION[0], BASELINE_DURATION[1]);
	}

	@Override
	public int getMinAge() {
		return (int)MIN_MAX_BASELINE_AGE[0];
	}
	
	@Override
	public int getMaxAge() {
		return (int)MIN_MAX_BASELINE_AGE[1];
	}
	
	@Override
	protected double getPSmoker() {
		// Current + previous smokers
		return (17.0+32.0) / 300.0;
	}
//	public static void main(String[] args) {
//		final double[] initBetaParams = Statistics.betaParametersFromNormal(BASELINE_AGE[0], BASELINE_AGE[1]);
//		final double k = ((initBetaParams[0] + initBetaParams[1])*(initBetaParams[0] + initBetaParams[1]))/initBetaParams[1];
//		final double variance = BASELINE_AGE[1] * BASELINE_AGE[1];
//		final double mode = variance * k * (initBetaParams[0] - 1) / (initBetaParams[0] - 3 * variance * k);
//		
//		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_AGE[0], mode, MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[1]);
//		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
//		final RandomVariate rnd1 = RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_BASELINE_AGE[1] - MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[0]);
//		for (int i = 0; i < 10000; i++)
//			System.out.println(rnd1.generate());
//		
//	}
}
