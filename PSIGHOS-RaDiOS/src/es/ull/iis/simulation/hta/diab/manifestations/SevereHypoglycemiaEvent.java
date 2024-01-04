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
public class SevereHypoglycemiaEvent extends DiseaseProgression {
	private static final double DU_HYPO_EPISODE = 0.0631; // Walters et al. (2011) 
	private static final double[] LIMITS_DU_HYPO_EPISODE = {0.01, 2 * DU_HYPO_EPISODE - 0.01}; // Assumption from observed values in Canada and Beaudet
	/** Cost from 2017, from https://doi.org/10.1007/s13300-017-0285-0 */
	private static final double COST_HYPO_EPISODE = 716.82;
	private static final int COSTYEAR = 2017;
	
	private static final double P_DEATH = 0.0063;
	public static final String NAME = "SHE";
	
	/**
	 * @param model
	 * @param disease
	 */
	public SevereHypoglycemiaEvent(HTAModel model, Disease disease) {
		super(model, NAME, "Severe hypoglycemic event", disease, Type.ACUTE_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.ONE_TIME_COST, "severe hypoglycemic episode", 
			"https://doi.org/10.1007/s13300-017-0285-0", COSTYEAR, COST_HYPO_EPISODE, Parameter.getRandomVariateForCost(COST_HYPO_EPISODE));
			addUsedParameter(StandardParameter.ONSET_DISUTILITY, "Disutility of " + getDescription(), "Walters et al. 10.1016/s1098-3015(10)63316-5", 
			DU_HYPO_EPISODE, RandomVariateFactory.getInstance("UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]));
		
		final double[] paramsDeathHypo = Statistics.betaParametersFromNormal(P_DEATH, Statistics.sdFrom95CI(new double[]{0.0058, 0.0068}));		
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH, "", "Canada", P_DEATH, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1]));
	}

}
