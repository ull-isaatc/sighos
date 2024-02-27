/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.DCCT;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Primary prevention cohort of DCCT
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTPrimaryConventionalPopulation extends DiabetesStdPopulation {
	private static final double BASELINE_HBA1C_MIN = 6.6; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final double BASELINE_HBA1C_AVG = 8.8; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_HBA1C_SD = 1.7; // SD from conventional therapy (the highest): https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final int BASELINE_AGE_MIN = 13; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final int BASELINE_AGE_MAX = 40; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final double BASELINE_AGE_AVG = 26; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_AGE_SD = 8; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_DURATION_AVG = 2.6; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_DURATION_SD = 1.4; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double P_MAN = 0.54; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401

	/**
	 * @param secParams
	 * @param type
	 */
	public DCCTPrimaryConventionalPopulation() {
		super(DiabetesType.T1);
	}

	@Override
	public int getMinAge() {
		return BASELINE_AGE_MIN;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getPMan()
	 */
	@Override
	protected double getPMan() {
		// From "https://doi.org/10.1056/NEJMoa052187"
		return P_MAN;
	}

	@Override
	protected RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C_AVG);
			
		final double alfa = ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD) * ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD);
		final double beta = (BASELINE_HBA1C_SD * BASELINE_HBA1C_SD) / (BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN);
		final RandomVariate rnd = RandomVariateFactory.getInstance("GammaVariate", alfa, beta);
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, 1.0, BASELINE_HBA1C_MIN);
	}
	
	@Override
	protected RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE_AVG);
		final double betaAvg = (BASELINE_AGE_AVG - BASELINE_AGE_MIN) / (BASELINE_AGE_MAX - BASELINE_AGE_MIN);
		final double betaSD = BASELINE_AGE_SD / (BASELINE_AGE_MAX - BASELINE_AGE_MIN);
		final double[] betaParams = Statistics.betaParametersFromNormal(betaAvg, betaSD);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, BASELINE_AGE_MAX - BASELINE_AGE_MIN, BASELINE_AGE_MIN);
	}

	@Override
	protected RandomVariate getBaselineDurationOfDiabetes() {
		if (BasicConfigParams.USE_FIXED_BASELINE_DURATION_OF_DIABETES)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_DURATION_AVG);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION_AVG, BASELINE_DURATION_SD);
	}
	
}
