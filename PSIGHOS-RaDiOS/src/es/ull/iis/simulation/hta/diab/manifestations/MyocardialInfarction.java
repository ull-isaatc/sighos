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
public class MyocardialInfarction extends ChronicManifestation {
	private static final double ANNUAL_COST = 948;
	private static final double TRANS_COST = 23536 - ANNUAL_COST;
	private static final int COSTYEAR = 2016;
	private static final double[] DU = new double[] {0.055, (0.067 - 0.042) / 3.92};
	/** Probability of sudden death after MI (average men-women) */ 
	private static final double P_DEATH = (0.393 + 0.364) / 2;
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public MyocardialInfarction(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams,"MI", "Myocardial Infarction", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost of year 2+ of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		secParams.addTransitionCostParam(this, "Cost of episode of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, TRANS_COST, SecondOrderParamsRepository.getRandomVariateForCost(TRANS_COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		secParams.addDeathProbParam(this, "Core Model", P_DEATH, SecondOrderParamsRepository.getRandomVariateForProbability(P_DEATH));
		secParams.addDisutilityParam(this, "Disutility for " + this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		secParams.addIMRParam(this, "Increased mortality risk due to macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1));
	}

}