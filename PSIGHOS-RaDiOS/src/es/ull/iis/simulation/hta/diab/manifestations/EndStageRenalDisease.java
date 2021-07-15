/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla
 *
 */
public class EndStageRenalDisease extends Manifestation {
	/** Utility (avg, SD) from either Bagust and Beale; or Sullivan */
	private static final double[] DU = new double[] {0.204, (0.342 - 0.066) / 3.92};
	private static final double COST = 34259.48;
	private static final double TCOST = 3250.73;
	private static final int COSTYEAR = 2015;

	/**
	 * @param secParams
	 * @param disease
	 */
	public EndStageRenalDisease(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "ESRD", "End-stage renal disease", disease, Type.CHRONIC);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost for " + this, "Ray (2005)", COSTYEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));
		secParams.addTransitionCostParam(this, "Transition cost for " + this, "Ray (2005)", COSTYEAR, TCOST, SecondOrderParamsRepository.getRandomVariateForCost(TCOST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		secParams.addDisutilityParam(this, "Disutility for " + this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		secParams.addIMRParam(this, "Increased mortality risk due to end-stage renal disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1));
	}

}