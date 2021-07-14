/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class Microalbuminuria extends Manifestation {

	/**
	 * @param secParams
	 * @param disease
	 */
	public Microalbuminuria(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "ALB1", "Microalbuminuria", disease, Type.CHRONIC);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost for " + this, "Assumption", 2021, 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
		secParams.addDisutilityParam(this, "Disutility for " + this, "Assumption", 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
	}

}
