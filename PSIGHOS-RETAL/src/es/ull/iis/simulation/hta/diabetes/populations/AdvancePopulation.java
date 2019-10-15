/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Population from the advance study. Combines both arms of the study.
 * From:
 * 1. Group TAC. Intensive Blood Glucose Control and Vascular Outcomes in Patients with Type 2 Diabetes. N Engl J Med. 2008 Jun 12;358(24):2560–72.
 * 2. Chalmers J. ADVANCE - Action in diabetes and vascular disease: Patient recruitment and characteristics of the study population at baseline. 
 * Diabet Med. 2005 Jul 1;22(7):882–8.
 *   
 * @author Iván Castilla Rodríguez
 *
 */
public class AdvancePopulation extends DiabetesStdPopulation {
	/** Average and SD HbA1c in the population at baseline. */
	private static final double[] BASELINE_HBA1C = {7.5, 1.5};
	/** Minimum and maximum expected values for baseline HbA1c */ 
	private static final double[] MIN_MAX_HBA1C = {6.5, 8.2};
	/** The average and SD age at baseline */
	private static final double[] BASELINE_AGE = {66, 6}; 
	/** The min and max age at baseline */
	private static final double[] MIN_MAX_AGE = {55, 100};
	private static final double P_MALE = 0.57;
	private static final double P_SMOKER = 0.14;
	/** Average and SD duration of diabetes at baseline */
	private static final double[] BASELINE_DURATION = {8, 6}; 
	/** The average and SD SBP at baseline */
	private static final double[] BASELINE_SBP = {145, 22};
	private static final double[] BASELINE_T_CHOL = {5.2, 1.2};
	private static final double[] BASELINE_HDL_CHOL = {1.3, 0.4};

	/**
	 */
	public AdvancePopulation() {
		super(DiabetesType.T2);
	}

	@Override
	protected double getPMan() {
		return P_MALE;
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
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION[0], BASELINE_DURATION[1]);
	}

	@Override
	protected RandomVariate getBaselineSBP() {
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_SBP[0], BASELINE_SBP[1]);
	}
	
	@Override
	protected RandomVariate getBaselineLipidRatio() {
		// FIXME: Add uncertainty
		return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_T_CHOL[0] / BASELINE_HDL_CHOL[0]);
	}
	
	@Override
	protected double getPSmoker() {
		return P_SMOKER; 
	}
}
