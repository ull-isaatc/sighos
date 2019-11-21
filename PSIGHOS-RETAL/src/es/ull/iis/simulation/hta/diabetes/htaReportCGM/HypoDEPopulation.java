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
 * Characteristics from the HypoDE study, which compared DEXCOM G5 on T1DM population with a history of impaired hypoglycaemia awareness or severe hypoglycaemia during the previous year.
 * Source: Heinemann L, Freckmann G, Ehrmann D, Faber-Heinemann G, Guerra S, Waldenmaier D, et al. Real-time continuous glucose monitoring in adults with type 1 diabetes and impaired hypoglycaemia awareness or severe hypoglycaemia treated with multiple daily insulin injections (HypoDE): a multicentre, randomised controlled trial. Lancet. 2018;391(10128):1367–77.
 * @author Iván Castilla
 *
 */
public class HypoDEPopulation extends DiabetesStdPopulation {
	/** Mean and SD of baseline HbA1c. */
	private static final double []BASELINE_HBA1C = {7.451006711, 1.0};
	/** Minimum and maximum baseline HbA1c. Minimum is an assumption; maximum is stated in the study */
	private static final double []MIN_MAX_BASELINE_HBA1C = {6, 9};
	/** Mean and SD of baseline age. SD is the highest from all the arms */
	private static final double []BASELINE_AGE = {46.24295302, 12};
	/** Mean and SD of baseline duration of diabetes. SD is the highest from all the arms */
	private static final double []BASELINE_DURATION = {21.24765101, 14};
	
	/**
	 *
	 */
	public HypoDEPopulation() {
		super(DiabetesType.T1);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getPMan()
	 */
	@Override
	protected double getPMan() {
		return 89.0 / 149.0;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineHBA1c()
	 */
	@Override
	protected RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C[0]);
		final double mode = Statistics.betaModeFromMeanSD(BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_HBA1C[0], mode, MIN_MAX_BASELINE_HBA1C[0], MIN_MAX_BASELINE_HBA1C[1]);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_BASELINE_HBA1C[1] - MIN_MAX_BASELINE_HBA1C[0], MIN_MAX_BASELINE_HBA1C[0]);			
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineAge()
	 */
	@Override
	protected RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_AGE[0], BASELINE_AGE[1]);			
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
