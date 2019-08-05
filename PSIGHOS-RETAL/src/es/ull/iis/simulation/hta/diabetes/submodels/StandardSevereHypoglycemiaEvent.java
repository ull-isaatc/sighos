/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToMultipleEventParam;
import es.ull.iis.simulation.hta.diabetes.params.DeathWithEventParam;
import es.ull.iis.simulation.hta.diabetes.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StandardSevereHypoglycemiaEvent extends SecondOrderAcuteComplicationSubmodel {
	public static final String STR_P_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_P_DEATH_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_RR_HYPO = SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesAcuteComplications.SHE.name(); 
	public static final String STR_COST_HYPO_EPISODE = SecondOrderParamsRepository.STR_COST_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_DU_HYPO_EVENT = SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + DiabetesAcuteComplications.SHE.name();

	private static final double P_DEATH = 0.0063;
	private final SecondOrderParam p; 
	private final SecondOrderParam rr; 
	private final SecondOrderParam du; 
	private final SecondOrderCostParam cost;

	public StandardSevereHypoglycemiaEvent(SecondOrderParam p, SecondOrderParam rr, SecondOrderParam du, SecondOrderCostParam cost) {
		super(DiabetesAcuteComplications.SHE, EnumSet.of(DiabetesType.T1));
		this.p = p;
		this.rr = rr;
		this.du = du;
		this.cost = cost;
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDeathHypo = SecondOrderParamsRepository.betaParametersFromNormal(P_DEATH, SecondOrderParamsRepository.sdFrom95CI(new double[]{0.0058, 0.0068}));
		secParams.addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", 
				"Canada", P_DEATH, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		secParams.addProbParam(p);
		secParams.addOtherParam(rr);

		secParams.addCostParam(cost);

		secParams.addUtilParam(du);
	}

	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
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
		
		@Override
		public double getCostOfComplication(DiabetesPatient pat) {
			return cost;
		}

		@Override
		public double getDisutility(DiabetesPatient pat) {
			return du;
		}
	}

}
