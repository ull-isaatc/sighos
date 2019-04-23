/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * <ul>
 * <li>Population: Spanish T1DM patients at risk of episodes of severe hypoglycemia (based on Conget et al. https://doi.org/10.1016/j.endinu.2018.03.008)
 * <ul>
 * <li>Age at start is defined constant (value = {@link #BASELINE_AGE_AVG}) if {@link BasicConfigParams#USE_FIXED_BASELINE_AGE} is set to <code>true</code>. 
 * Otherwise, is obtained according to the proportions defined in {@link #BASELINE_AGE_PROPORTIONS} for the age ranges defined in {@link #BASELINE_AGE_RANGES}</li>
 * <li>HbA1c level at start is defined constant (value = {@link #BASELINE_HBA1C_AVG}) if {@link BasicConfigParams#USE_FIXED_BASELINE_HBA1C} is set to <code>true</code>. Otherwise, it is set following
 *  a uniform distribution between {@link #BASELINE_HBA1C_MIN} and {@link #BASELINE_HBA1C_MAX}</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author icasrod
 *
 */
public class UnconsciousT1DMPopulation extends DiabetesStdPopulation {
	/** Minimum HbA1c in the population at baseline. Assumption */
	private static final double BASELINE_HBA1C_MIN = 7; 
	/** Maximum HbA1c in the population at baseline. Assumption */
	private static final double BASELINE_HBA1C_MAX = 8; 
	/** Average HbA1c in the population at baseline. https://doi.org/10.1016/j.endinu.2018.03.008 */
	private static final double BASELINE_HBA1C_AVG = 7.5; 
	/** A vector that defines groups of age [4 - 7, 7 - 12, 12 - 18, 18 - 50, > 50], according to https://doi.org/10.1016/j.endinu.2018.03.008 */
	private static final double[] BASELINE_AGE_RANGES = {4.0, 7.0, 12.0, 18.0, 50.0};
	/** The proportion of patients within each age group at baseline, as defined in {@link #BASELINE_AGE_RANGES}, according to https://doi.org/10.1016/j.endinu.2018.03.008 */ 
	private static final double[] BASELINE_AGE_PROPORTIONS = {4.0/95.0, 27.0/95.0, 34.0/95.0, 30.0/95.0};
	/** The average age at baseline, according to https://doi.org/10.1016/j.endinu.2018.03.008 */
	private static final double BASELINE_AGE_AVG = 18.6; 
	/** Duration of diabetes at baseline, according to https://doi.org/10.1016/j.endinu.2018.03.008 */
	private static final double[] BASELINE_DURATION = {12.0, 8.74}; 
	

	/**
	 * @param secParams
	 */
	public UnconsciousT1DMPopulation(SecondOrderParamsRepository secParams) {
		super(secParams, DiabetesType.T1);
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
		return RandomVariateFactory.getInstance("ContinuousSelectorVariate", BASELINE_AGE_PROPORTIONS, BASELINE_AGE_RANGES);
	}

	@Override
	public RandomVariate getBaselineDurationOfDiabetes() {
		if (BasicConfigParams.USE_FIXED_BASELINE_DURATION_OF_DIABETES)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_DURATION[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION[0], BASELINE_DURATION[1]);
	}
	
}
