/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class ProliferativeRetinopathy extends DiseaseProgression {
	private static final double COST = 6394.62;
	private static final int COSTYEAR = 2018;
	private static final double[] DU = new double[] {0.04, (0.066 - 0.014) / 3.92};
	public static final String NAME = "PRET";

	/**
	 * @param model
	 * @param disease
	 */
	public ProliferativeRetinopathy(HTAModel model, Disease disease) {
		super(model, NAME, "Proliferative Retinopathy", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.ANNUAL_COST, "", "Original analysis", COSTYEAR, COST, Parameter.getRandomVariateForCost(COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "Disutility of " + getDescription(), "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
	}

}
