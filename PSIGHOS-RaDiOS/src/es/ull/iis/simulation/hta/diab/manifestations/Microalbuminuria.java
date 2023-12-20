/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class Microalbuminuria extends DiseaseProgression {
	public static final String NAME = "ALB1";

	/**
	 * @param secParams
	 * @param disease
	 */
	public Microalbuminuria(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Microalbuminuria", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		CostParamDescriptions.ANNUAL_COST.addUsedParameter(secParams, this, "Assumption", 2021, 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Assumption", 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
	}

}
