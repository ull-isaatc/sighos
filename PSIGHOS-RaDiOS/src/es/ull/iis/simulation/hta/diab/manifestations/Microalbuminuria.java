/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class Microalbuminuria extends ChronicManifestation {
	public static final String NAME = "ALB1";

	/**
	 * @param secParams
	 * @param disease
	 */
	public Microalbuminuria(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Microalbuminuria", disease);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "Assumption", 2021, 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Assumption", 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
	}

}
