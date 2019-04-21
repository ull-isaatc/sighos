/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * <ul>
 * <li>Population: Spanish T1DM patients with inadequate glycemic control (based on Battelino et al. https://doi.org/10.1007/s00125-012-2708-9)
 * <ul>
 * <li>Age at start is defined constant (value = {@link #BASELINE_AGE_AVG}) if {@link BasicConfigParams#USE_FIXED_BASELINE_AGE} is set to <code>true</code>. 
 * Otherwise, is based on a beta distribution between {@link #BASELINE_AGE_MIN} and {@link #BASELINE_AGE_MAX}, and with average = {@link #BASELINE_AGE_AVG}
 * and SD = {@link #BASELINE_AGE_SD}</li>
 * <li>HbA1c level at start is defined constant (value = {@link #BASELINE_HBA1C_AVG}) if {@link BasicConfigParams#USE_FIXED_BASELINE_HBA1C} is set to <code>true</code>. Otherwise, it is set following
 *  a uniform distribution between {@link #BASELINE_HBA1C_MIN} and {@link #BASELINE_HBA1C_MAX}</li>
 * </ul>
 * </li>
 * </ul>
 * @author icasrod
 *
 */
public class UncontrolledT1DMPopulation extends DiabetesStdPopulation {
	
	/** Minimum HbA1c in the population at baseline. From Battelno 2012 */
	private static final double BASELINE_HBA1C_MIN = 7.5; 
	/** Maximum HbA1c in the population at baseline. From Battelno 2012 */
	private static final double BASELINE_HBA1C_MAX = 9.5; 
	/** Average HbA1c in the population at baseline. From Battelno 2012 */
	private static final double BASELINE_HBA1C_AVG = 8.5; 
//	private static final double BASELINE_HBA1C_SD = 0.6; // Battelino 2012: Not used because it leads to a BETA(1,1), which, in fact, is like using a uniform
	/** Minimum age in the population at baseline. From Battelno 2012 */
	private static final int BASELINE_AGE_MIN = 6; 
	/** Maximum HbA1c in the population at baseline. From Battelno 2012 */
	private static final int BASELINE_AGE_MAX = 70; 
	/** Average age in the population at baseline. From Battelno 2012 */
	private static final int BASELINE_AGE_AVG = 28; 
	/** SD for age in the population at baseline. From Battelno 2012 */
	private static final int BASELINE_AGE_SD = 17; 

	
	/**
	 * @param secParams
	 * @param type
	 */
	public UncontrolledT1DMPopulation(SecondOrderParamsRepository secParams) {
		super(secParams, Type.T1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMinAge() {
		return BASELINE_AGE_MIN;
	}
	
	@Override
	public double getPMan() {
		// From "https://doi.org/10.1016/j.endinu.2018.03.008"
		return 0.5;
	}
	
	@Override
	public RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C_AVG);
		return RandomVariateFactory.getInstance("UniformVariate", BASELINE_HBA1C_MIN, BASELINE_HBA1C_MAX);
	}

	@Override
	public RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE_AVG);
		final double[] initBetaParams = SecondOrderParamsRepository.betaParametersFromNormal(BASELINE_AGE_AVG, BASELINE_AGE_SD);
		// k is used to simplify the operations
		final double k = ((initBetaParams[0] + initBetaParams[1])*(initBetaParams[0] + initBetaParams[1]))/initBetaParams[1];
		final double variance = BASELINE_AGE_SD * BASELINE_AGE_SD;
		final double mode = variance * k * (initBetaParams[0] - 1) / (initBetaParams[0] - 3 * variance * k);
		final double[] betaParams = SecondOrderParamsRepository.betaParametersFromEmpiricData(BASELINE_AGE_AVG, mode, BASELINE_AGE_MIN, BASELINE_AGE_MAX);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, BASELINE_AGE_MAX - BASELINE_AGE_MIN, BASELINE_AGE_MIN);
	}

	@Override
	public RandomVariate getBaselineDurationOfDiabetes() {
		// FIXME: Currently not using this, but probably should
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}

}
