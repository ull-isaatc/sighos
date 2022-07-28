/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class EndStageRenalDisease extends ChronicManifestation {
	/** Utility (avg, SD) from either Bagust and Beale; or Sullivan */
	private static final double[] DU = new double[] {0.204, (0.342 - 0.066) / 3.92};
	private static final double COST = 34259.48;
	private static final double TCOST = 3250.73;
	private static final int COSTYEAR = 2015;
	public static final String NAME = "ESRD";

	/**
	 * @param secParams
	 * @param disease
	 */
	public EndStageRenalDisease(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "End-stage renal disease", disease);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "Ray (2005)", COSTYEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "Ray (2005)", COSTYEAR, TCOST, SecondOrderParamsRepository.getRandomVariateForCost(TCOST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(secParams, this,  
				"https://doi.org/10.2337/diacare.28.3.617", 
				4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1));
	}

}
