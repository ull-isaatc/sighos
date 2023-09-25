/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class SevereHypoglycemiaEvent extends AcuteManifestation {
	private static final double DU_HYPO_EPISODE = 0.0631; // Walters et al. (2011) 
	private static final double[] LIMITS_DU_HYPO_EPISODE = {0.01, 2 * DU_HYPO_EPISODE - 0.01}; // Assumption from observed values in Canada and Beaudet
	/** Cost from 2017, from https://doi.org/10.1007/s13300-017-0285-0 */
	private static final double COST_HYPO_EPISODE = 716.82;
	private static final int COSTYEAR = 2017;
	
	private static final double P_DEATH = 0.0063;
	public static final String NAME = "SHE";
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public SevereHypoglycemiaEvent(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Severe hypoglycemic event", disease);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "severe hypoglycemic episode", 
			"https://doi.org/10.1007/s13300-017-0285-0", COSTYEAR, COST_HYPO_EPISODE, SecondOrderParamsRepository.getRandomVariateForCost(COST_HYPO_EPISODE));
		UtilityParamDescriptions.ONE_TIME_DISUTILITY.addParameter(secParams, this, "Walters et al. 10.1016/s1098-3015(10)63316-5", 
			DU_HYPO_EPISODE, RandomVariateFactory.getInstance("UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]));
		
		final double[] paramsDeathHypo = Statistics.betaParametersFromNormal(P_DEATH, Statistics.sdFrom95CI(new double[]{0.0058, 0.0068}));		
		ProbabilityParamDescriptions.PROBABILITY_DEATH.addParameter(secParams, this, "Canada", P_DEATH, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1]));
	}

}
