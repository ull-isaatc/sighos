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
 * @author Iván Castilla
 *
 */
public class Blindness extends ChronicManifestation {
	private static final double COST = 2405.35;
	private static final int COSTYEAR = 2016;
	private static final double[] DU = new double[] {0.074, (0.124 - 0.025) / 3.92};

	/**
	 * @param secParams
	 * @param disease
	 */
	public Blindness(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "BLI", "Blindness", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost for " + this, "Conget et al", COSTYEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		secParams.addDisutilityParam(this, "Disutility for " + this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
	}

}
