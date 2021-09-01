/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class HeartFailure extends ChronicManifestation {
	private static final double ANNUAL_COST = 1054.42;
	private static final double TRANS_COST = 5557.66 - ANNUAL_COST;
	private static final int COSTYEAR = 2016;
	private static final double[] DU = new double[] {0.108, (0.169 - 0.048) / 3.92};
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public HeartFailure(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "HF", "Heart Failure", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost of year 2+ of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		secParams.addTransitionCostParam(this, "Cost of episode of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, TRANS_COST, SecondOrderParamsRepository.getRandomVariateForCost(TRANS_COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		secParams.addDisutilityParam(this, "Disutility for " + this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		secParams.addIMRParam(this, "Increased mortality risk due to macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1));
	}

}