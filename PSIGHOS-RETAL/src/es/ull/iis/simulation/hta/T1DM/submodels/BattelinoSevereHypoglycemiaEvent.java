/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.AcuteEventParam;
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
public class BattelinoSevereHypoglycemiaEvent extends AcuteComplicationSubmodel {
	/** Uses the arm-specific probabilities of severe hypoglycemic event in the base case; otherwise, uses the aggregated value */
	public static final boolean ENABLE_HYPO_SCENARIO_1 = false;
	public static final String STR_P_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_P_DEATH_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "DEATH_" + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_RR_HYPO = SecondOrderParamsRepository.STR_RR_PREFIX + MainAcuteComplications.SEVERE_HYPO.name(); 
	public static final String STR_COST_HYPO_EPISODE = SecondOrderParamsRepository.STR_COST_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_DU_HYPO_EVENT = SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	
	private static final double P_HYPO = ENABLE_HYPO_SCENARIO_1 ? 0.027025776 : 0.0408163;
	private static final double RR_HYPO = ENABLE_HYPO_SCENARIO_1 ? 1.98630137 : 1.0;
	private static final double[] P_HYPO_BETA = {3.94576334, 142.05423666};
	private static final double[] RR_HYPO_BETA = {0.68627430, 0.60401244};
	
	private static final double DU_HYPO_EPISODE = BasicConfigParams.USE_REVIEW_UTILITIES ? 0.047 : 0.0206; // From Canada
	private static final double[] LIMITS_DU_HYPO_EPISODE = {BasicConfigParams.USE_REVIEW_UTILITIES ? 0.035 : 0.01, BasicConfigParams.USE_REVIEW_UTILITIES ? 0.059 : 0.122}; // From Canada
	private static final String DEF_SOURCE = "Battelino et al. 2012";

	private final double cost;
	private final double du;
	private final AcuteEventParam param;

	/**
	 * 
	 */
	public BattelinoSevereHypoglycemiaEvent(SecondOrderParamsRepository secParams) {
		super();
		
		cost = secParams.getCostForAcuteComplication(MainAcuteComplications.SEVERE_HYPO);
		du = secParams.getDisutilityForAcuteComplication(MainAcuteComplications.SEVERE_HYPO);
		final double[] rrValues = new double[secParams.getNInterventions()];
		rrValues[0] = 1.0;
		final double rr = secParams.getOtherParam(STR_RR_HYPO);
		for (int i = 1; i < secParams.getNInterventions(); i++) {
			rrValues[i] = rr;
		}
		this.param = new AcuteEventParam(secParams.getnPatients(), secParams.getProbParam(STR_P_HYPO), new InterventionSpecificComplicationRR(rrValues), secParams.getProbParam(STR_P_DEATH_HYPO));
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsDeathHypo = SecondOrderParamsRepository.betaParametersFromNormal(0.0063, SecondOrderParamsRepository.sdFrom95CI(new double[]{0.0058, 0.0068}));
		secParams.addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from n events of both arms; only events from injection therapy in probabilistic)", 
				DEF_SOURCE, P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])));
		secParams.addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", 
				"Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		secParams.addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic events (adjusted from rate/100 patient-month). Assumed 1 at base case; using difference at probabilistic", 
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

	@Override
	public AcuteEventParam getParam() {
		return param;
	}

}
