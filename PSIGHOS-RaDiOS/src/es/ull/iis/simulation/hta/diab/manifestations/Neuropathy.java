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
 * @author Iván Castilla Rodríguez
 *
 */
public class Neuropathy extends ChronicManifestation {
	private static final double COST = 3108.86;
	private static final int COSTYEAR = 2015;
	private static final double[] DU = new double[] {0.084, (0.111 - 0.057) / 3.92};
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public Neuropathy(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams,"NEU", "Neuropathy", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost for " + this, "Ray (2015)", COSTYEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		secParams.addDisutilityParam(this, "Disutility for " + this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		secParams.addIMRParam(this, "Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.51, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.51, 1.00, 2.28, 1));
	}

}
