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
public class MacularEdema extends ChronicManifestation {
	private static final double COST = 6785.16;
	private static final int COSTYEAR = 2018;
	private static final double[] DU = new double[] {0.04, (0.066 - 0.014) / 3.92};
	public static final String NAME = "ME";

	/**
	 * @param secParams
	 * @param disease
	 */
	public MacularEdema(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Macular Edema", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost for " + this, "Original analysis", COSTYEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		secParams.addUtilityParam(this, "Disutility for " + this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]), true);
	}

}
