/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class BackgroundRetinopathy extends DiseaseProgression {
	private static final double COST = 146.4525;
	private static final int COSTYEAR = 2018;
	private static final double DU = 0.0;
	public static final String NAME = "BGRET";

	/**
	 * @param model
	 * @param disease
	 */
	public BackgroundRetinopathy(HTAModel model, Disease disease) {
		super(model, NAME, "Background Retinopathy", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		CostParamDescriptions.ANNUAL_COST.addUsedParameter(model, this, "Original analysis", COSTYEAR, COST, StandardParameter.getRandomVariateForCost(COST));
		UtilityParamDescriptions.DISUTILITY.addParameter(model, this, "Assumption", DU, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
	}

}
