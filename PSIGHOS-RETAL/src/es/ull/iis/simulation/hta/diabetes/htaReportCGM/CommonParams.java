/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.htaReportCGM;

import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class CommonParams {
	/** Average and SD cost for reactive glucose strips */
	private static final double []C_STRIPS = {0.281378955, 0.098554232};
	private static final double []MIN_MAX_C_STRIPS = {0.1, 0.5};
	public static final String STR_C_STRIPS = SecondOrderParamsRepository.STR_COST_PREFIX + "STRIPS";
	public static final String STR_USE_STRIPS = "DAILY_USE_STRIPS";
	public static final String STR_DESCRIPTION_C_STRIPS = "Average from Spanish regions";
	public static final int YEAR_C_STRIPS = 2019;
	
	/**
	 * 
	 */
	private CommonParams() {
	}

	public static double getCostStrips() {
		return C_STRIPS[0];
	}

	public static double[] getMinMaxCostStrips() {
		return MIN_MAX_C_STRIPS;
	}
	
	public static RandomVariate getRndVariateCostStrips() {
		final double mode = Statistics.betaModeFromMeanSD(C_STRIPS[0], C_STRIPS[1]);
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(C_STRIPS[0], mode, MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[1]);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_C_STRIPS[1] - MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[0]);
	}
}
