/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LySevereHypoglycemiaEvent extends AcuteComplicationSubmodel {
	public static final String STR_P_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_P_DEATH_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "DEATH_" + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_RR_HYPO = SecondOrderParamsRepository.STR_RR_PREFIX + MainAcuteComplications.SEVERE_HYPO.name(); 
	public static final String STR_COST_HYPO_EPISODE = SecondOrderParamsRepository.STR_COST_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_DU_HYPO_EVENT = SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();

	private static final double P_HYPO = 0.234286582;
	private static final double RR_HYPO = 0.020895447;
	private static final double[] P_HYPO_BETA = {23.19437163, 75.80562837};
	private static final double[] RR_HYPO_BETA = {-3.868224010, 1.421931924};
	
	private static final double DU_HYPO_EPISODE = BasicConfigParams.USE_REVIEW_UTILITIES ? 0.047 : 0.0206; // From Canada
	private static final double[] LIMITS_DU_HYPO_EPISODE = {BasicConfigParams.USE_REVIEW_UTILITIES ? 0.035 : 0.01, BasicConfigParams.USE_REVIEW_UTILITIES ? 0.059 : 0.122}; // From Canada
	private static final String DEF_SOURCE = "Ly et al. 2013";

	private final double cost;
	private final double du;

	/**
	 * 
	 */
	public LySevereHypoglycemiaEvent(SecondOrderParamsRepository secParams) {
		super(secParams.getnPatients(), secParams.getProbParam(STR_P_HYPO), new InterventionSpecificComplicationRR(new double[]{1.0, secParams.getOtherParam(STR_RR_HYPO)}), secParams.getProbParam(STR_P_DEATH_HYPO));
		
		cost = secParams.getCostForAcuteComplication(MainAcuteComplications.SEVERE_HYPO);
		du = secParams.getDisutilityForAcuteComplication(MainAcuteComplications.SEVERE_HYPO);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsDeathHypo = SecondOrderParamsRepository.betaParametersFromNormal(0.0063, SecondOrderParamsRepository.sdFrom95CI(new double[]{0.0058, 0.0068}));
		secParams.addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])));
		secParams.addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", 
				"Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		secParams.addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, RR_HYPO, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_HYPO_BETA[0], RR_HYPO_BETA[1]))));

		secParams.addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", 
				"https://doi.org/10.1007/s13300-017-0285-0", 2017, 716.82, SecondOrderParamsRepository.getRandomVariateForCost(716.82)));

		secParams.addUtilParam(new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", 
				DU_HYPO_EPISODE, "UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]));
		
		secParams.registerComplication(MainAcuteComplications.SEVERE_HYPO);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel#getCostOfComplication(es.ull.iis.simulation.hta.T1DM.T1DMPatient, es.ull.iis.simulation.hta.T1DM.T1DMComorbidity)
	 */
	@Override
	public double getCostOfComplication(T1DMPatient pat) {
		return cost;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel#getDisutility(es.ull.iis.simulation.hta.T1DM.T1DMPatient, es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod)
	 */
	@Override
	public double getDisutility(T1DMPatient pat) {
		return du;
	}

}
