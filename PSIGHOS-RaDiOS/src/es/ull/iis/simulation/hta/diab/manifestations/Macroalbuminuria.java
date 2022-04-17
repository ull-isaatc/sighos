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
public class Macroalbuminuria extends ChronicManifestation {
	/** Utility (avg, SD) from either Bagust and Beale; or Sullivan */
	private static final double[] DU = new double[] {0.048, (0.091 - 0.005) / 3.92};
	public static final String NAME = "ALB2";

	/**
	 * @param secParams
	 * @param disease
	 */
	public Macroalbuminuria(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Macroalbuminuria", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost for " + this, "Assumption", 2021, 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		secParams.addDisutilityParam(this, "Disutility for " + this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		secParams.addIMRParam(this, "Increased mortality risk due to severe proteinuria", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1));
	}

}
