/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.DCCT;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSevereHypoglycemiaEvent extends AcuteComplicationSubmodel {
	public static final String STR_P_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_P_DEATH_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "DEATH_" + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_RR_HYPO = SecondOrderParamsRepository.STR_RR_PREFIX + MainAcuteComplications.SEVERE_HYPO.name(); 
	public static final String STR_COST_HYPO_EPISODE = SecondOrderParamsRepository.STR_COST_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	public static final String STR_DU_HYPO_EVENT = SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + MainAcuteComplications.SEVERE_HYPO.name();
	
	private static final String DEF_SOURCE = "DCCT: https://www.ncbi.nlm.nih.gov/pubmed/90007053";

	private final double cost;
	private final double du;

	/**
	 * 
	 */
	public DCCTSevereHypoglycemiaEvent(SecondOrderParamsRepository secParams) {
		super(secParams.getnPatients(), secParams.getProbParam(STR_P_HYPO), new InterventionSpecificComplicationRR(new double[]{1.0, secParams.getOtherParam(STR_RR_HYPO)}), secParams.getProbParam(STR_P_DEATH_HYPO));
		
		cost = secParams.getCostForAcuteComplication(MainAcuteComplications.SEVERE_HYPO);
		du = secParams.getDisutilityForAcuteComplication(MainAcuteComplications.SEVERE_HYPO);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsDeathHypo = SecondOrderParamsRepository.betaParametersFromNormal(0.0063, SecondOrderParamsRepository.sdFrom95CI(new double[]{0.0058, 0.0068}));
		secParams.addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, 
				0.187, RandomVariateFactory.getInstance("BetaVariate", 18.513, 80.487)));
		secParams.addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", 
				"Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		secParams.addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, 
				3.27272727, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", 1.1856237, 0.22319455))));

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
