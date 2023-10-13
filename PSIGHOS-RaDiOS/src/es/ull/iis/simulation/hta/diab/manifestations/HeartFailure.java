/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HeartFailure extends Manifestation {
	private static final double ANNUAL_COST = 1054.42;
	private static final double TRANS_COST = 5557.66 - ANNUAL_COST;
	private static final int COSTYEAR = 2016;
	private static final double[] DU = new double[] {0.108, (0.169 - 0.048) / 3.92};
	public static final String NAME = "HF";
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public HeartFailure(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Heart Failure", disease, Type.CHRONIC);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "year 2+ of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "episode of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, TRANS_COST, SecondOrderParamsRepository.getRandomVariateForCost(TRANS_COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(secParams, this.name(), "macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1));
	}

}
