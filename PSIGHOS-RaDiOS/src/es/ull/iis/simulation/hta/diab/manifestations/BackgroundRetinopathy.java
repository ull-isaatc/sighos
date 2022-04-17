/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class BackgroundRetinopathy extends ChronicManifestation {
	private static final double COST = 146.4525;
	private static final int COSTYEAR = 2018;
	private static final double DU = 0.0;
	public static final String NAME = "BGRET";

	/**
	 * @param secParams
	 * @param disease
	 */
	public BackgroundRetinopathy(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Background Retinopathy", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost for " + this, "Original analysis", COSTYEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));
		secParams.addDisutilityParam(this, "Disutility for " + this, "Assumption", DU, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
	}

}
