/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaSevereHypoglycemiaEvent extends AcuteComplicationSubmodel {
	public static final String STR_P_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + T1DMAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_P_DEATH_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "DEATH_" + T1DMAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_RR_HYPO = SecondOrderParamsRepository.STR_RR_PREFIX + T1DMAcuteComplications.SEVERE_HYPO.name(); 
	public static final String STR_COST_HYPO_EPISODE = SecondOrderParamsRepository.STR_COST_PREFIX + T1DMAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_DU_HYPO_EVENT = SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + T1DMAcuteComplications.SEVERE_HYPO.name();
	
	private static final double C_HYPO_EPISODE = 3755;
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	private static final String DEF_SOURCE = "Canada";

	private final double cost;
	private final double du;

	/**
	 * 
	 */
	public CanadaSevereHypoglycemiaEvent(SecondOrderParamsRepository secParams) {
		super(secParams.getnPatients(), secParams.getProbParam(STR_P_HYPO), new InterventionSpecificComplicationRR(new double[]{1.0, secParams.getOtherParam(STR_RR_HYPO)}), secParams.getProbParam(STR_P_DEATH_HYPO));
		
		cost = secParams.getCostForAcuteComplication(T1DMAcuteComplications.SEVERE_HYPO);
		du = secParams.getDisutilityForAcuteComplication(T1DMAcuteComplications.SEVERE_HYPO);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsHypo = SecondOrderParamsRepository.betaParametersFromNormal(0.0982, SecondOrderParamsRepository.sdFrom95CI(new double[]{0.0526, 0.1513}));
		final double[] paramsDeathHypo = SecondOrderParamsRepository.betaParametersFromNormal(0.0063, SecondOrderParamsRepository.sdFrom95CI(new double[]{0.0058, 0.0068}));
		
		secParams.addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode", DEF_SOURCE, 0.0982, RandomVariateFactory.getInstance("BetaVariate", paramsHypo[0], paramsHypo[1])));
		secParams.addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", DEF_SOURCE, 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		secParams.addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch", DEF_SOURCE, 0.869, RandomVariateFactory.getInstance("RRFromLnCIVariate", 0.869, 0.476, 1.586, 1)));
		secParams.addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", DEF_SOURCE, 2018, C_HYPO_EPISODE));
		secParams.addUtilParam(new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", DEF_SOURCE, DU_HYPO_EPISODE));
		
		secParams.registerComplication(T1DMAcuteComplications.SEVERE_HYPO);
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
