/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.DCCT;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToMultipleEventParam;
import es.ull.iis.simulation.hta.diabetes.params.DeathWithEventParam;
import es.ull.iis.simulation.hta.diabetes.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderAcuteComplicationSubmodel;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSevereHypoglycemiaEvent extends SecondOrderAcuteComplicationSubmodel {
	public static final String STR_P_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_P_DEATH_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_RR_HYPO = SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesAcuteComplications.SHE.name(); 
	public static final String STR_COST_HYPO_EPISODE = SecondOrderParamsRepository.STR_COST_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_DU_HYPO_EVENT = SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + DiabetesAcuteComplications.SHE.name();
	
	private static final String DEF_SOURCE = "DCCT: https://www.ncbi.nlm.nih.gov/pubmed/90007053";

	public DCCTSevereHypoglycemiaEvent() {
		super(DiabetesAcuteComplications.SHE, EnumSet.of(DiabetesType.T1));
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDeathHypo = Statistics.betaParametersFromNormal(0.0063, Statistics.sdFrom95CI(new double[]{0.0058, 0.0068}));
		secParams.addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, 
				0.187, RandomVariateFactory.getInstance("BetaVariate", 18.513, 80.487)));
		secParams.addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", 
				"Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		secParams.addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, 
				3.27272727, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", 1.1856237, 0.22319455))));
	}
	
	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return isEnabled() ? new Instance(secParams) : new DisabledAcuteComplicationInstance();
	}
	
	public class Instance extends AcuteComplicationSubmodel {
		private final double cost;
		private final double du;

		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(new AnnualRiskBasedTimeToMultipleEventParam(
					SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					secParams.getnPatients(), 
					secParams.getProbParam(STR_P_HYPO), 
					new InterventionSpecificComplicationRR(new double[]{1.0, secParams.getOtherParam(STR_RR_HYPO)})), 
				new DeathWithEventParam(
						SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
						secParams.getnPatients(), 
						secParams.getProbParam(STR_P_DEATH_HYPO)));
			
			cost = secParams.getCostForAcuteComplication(DiabetesAcuteComplications.SHE);
			du = secParams.getDisutilityForAcuteComplication(DiabetesAcuteComplications.SHE);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel#getCostOfComplication(es.ull.iis.simulation.hta.T1DM.T1DMPatient, es.ull.iis.simulation.hta.T1DM.T1DMComorbidity)
		 */
		@Override
		public double getCostOfComplication(DiabetesPatient pat) {
			return cost;
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel#getDisutility(es.ull.iis.simulation.hta.T1DM.T1DMPatient, es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod)
		 */
		@Override
		public double getDisutility(DiabetesPatient pat) {
			return du;
		}

	}
	
}
