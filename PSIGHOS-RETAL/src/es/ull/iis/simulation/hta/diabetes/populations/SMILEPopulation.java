/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * <ul>
 * <li>Population: Spanish T1DM patients based on SMILE study
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
public class SMILEPopulation extends DiabetesStdPopulation {
	/** Average and SD HbA1c in the population at baseline, according to SMILE */
	private static final double[] BASELINE_HBA1C = {7.649673203, 0.898436081}; 
	/** The average age at baseline, according to SMILE */
	private static final double[] BASELINE_AGE = {48.19477124, 12.33734058}; 
	/** Duration of diabetes at baseline, according to SMILE */
	private static final double[] BASELINE_DURATION = {29.10392157, 12.23117986}; 

	/**
	 * @param secParams
	 * @param type
	 */
	public SMILEPopulation() {
		super(DiabetesType.T1);
	}

	@Override
	protected double getPMan() {
		// From SMILE
		return 0.503267974;
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

}
