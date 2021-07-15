/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
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
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMGoldDiamondPopulation extends StdPopulation {
	/** Mean and SD of baseline HbA1c. SD is the highest from all the cohorts */
	private static final double []BASELINE_HBA1C = {8.5382, 0.9};
	/** Minimum and maximum baseline HbA1c, as stated in the DIAMOND study */
	private static final double []MIN_MAX_BASELINE_HBA1C = {7.5, 9.9};
	/** Mean and SD of baseline age. SD is the highest from all the cohorts */
	private static final double []BASELINE_AGE = {46.217, 14};
	/** Minimum and maximum of baseline age. From DIAMOND */
	private static final double []MIN_MAX_BASELINE_AGE = {26, 73};
	/** Mean and SD of baseline duration of diabetes. SD from GOLD */
	private static final double []BASELINE_DURATION = {22.16619718, 11.8};

	/**
	 * @param secParams
	 * @param disease
	 */
	public T1DMGoldDiamondPopulation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, disease);
	}

	@Override
	public double getPDisease(DiseaseProgressionSimulation simul) {
		return 1.0;
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	protected double getPMan(DiseaseProgressionSimulation simul) {
		return 168.0 / 300.0;
	}

	@Override
	protected double getPDiagnosed(DiseaseProgressionSimulation simul) {
		return 1.0;
	}

	@Override
	protected RandomVariate getBaselineAge(DiseaseProgressionSimulation simul) {
		final double mode = Statistics.betaModeFromMeanSD(BASELINE_AGE[0], BASELINE_AGE[1]);
		
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_AGE[0], mode, MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[1]);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_BASELINE_AGE[1] - MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[0]);			
	}

	@Override
	public int getMinAge() {
		return (int)MIN_MAX_BASELINE_AGE[0];
	}
	
	@Override
	public int getMaxAge() {
		return (int)MIN_MAX_BASELINE_AGE[1];
	}
	
}
