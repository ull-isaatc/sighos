/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRateBasedTimeToMultipleEventParam;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToMultipleEventParam;
import es.ull.iis.simulation.hta.diabetes.params.DeathWithEventParam;
import es.ull.iis.simulation.hta.diabetes.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.util.Statistics;
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

	private static final double DU_HYPO_EPISODE = 0.0631; // Walters et al. (2011) 
	private static final double[] LIMITS_DU_HYPO_EPISODE = {0.01, 2 * DU_HYPO_EPISODE - 0.01}; // Assumption from observed values in Canada and Beaudet
	/** Cost from 2017, from https://doi.org/10.1007/s13300-017-0285-0 */
	private static final double COST_HYPO_EPISODE = 716.82;
	
	private static final double P_DEATH = 0.0063;
	private final SecondOrderParam mort;
	private final SecondOrderParam p; 
	private final SecondOrderParam rr; 
	private final SecondOrderParam du; 
	private final SecondOrderCostParam cost;
	/** If true, interprets p as an annual rate (patient-year), and rr as an IRR */
	private final boolean rate;

	public StandardSevereHypoglycemiaEvent(SecondOrderParam p, SecondOrderParam rr, SecondOrderParam du, SecondOrderCostParam cost, SecondOrderParam mortality, EnumSet<DiabetesType> diabetesTypes) {
		this(p, rr, du, cost, mortality, diabetesTypes, false);
	}

	/**
	 * Creates a standard severe hypoglycemia event, which may use probabilities or rates to compute time to event
	 * @param p If (rate = true), the annual patients-year that suffer an event; otherwise, the probability of suffering an event
	 * @param rr If (rate = true), the incidence rate ratio of suffering an event in the intervention arm; otherwise, the relative risk
	 * @param du Disutility associated to the event
	 * @param cost Cost associated to the event
	 * @param diabetesTypes Types of diabetes that this event is valid for 
	 * @param rate If true, interprets p as an annual rate (patient-year), and rr as an IRR
	 */
	public StandardSevereHypoglycemiaEvent(SecondOrderParam p, SecondOrderParam rr, SecondOrderParam du, SecondOrderCostParam cost, SecondOrderParam mortality, EnumSet<DiabetesType> diabetesTypes, boolean rate) {
		super(DiabetesAcuteComplications.SHE, diabetesTypes);
		this.p = p;
		this.rr = rr;
		this.du = du;
		this.cost = cost;
		this.mort = mortality;
		this.rate = rate;
	}

	public StandardSevereHypoglycemiaEvent(SecondOrderParam p, SecondOrderParam rr, EnumSet<DiabetesType> diabetesTypes) {
		this(p, rr, diabetesTypes, false);
	}
	
	public StandardSevereHypoglycemiaEvent(SecondOrderParam p, SecondOrderParam rr, EnumSet<DiabetesType> diabetesTypes, boolean rate) {
		this(p, rr, getDefaultDisutilityParameter(), getDefaultCostParameter(),	getDefaultMortalityParameter(), diabetesTypes, rate);
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(mort);
		secParams.addProbParam(p);
		secParams.addOtherParam(rr);

		secParams.addCostParam(cost);

		secParams.addUtilParam(du);
	}

	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return isEnabled() ? new Instance(secParams) : new DisabledAcuteComplicationInstance();
	}
	
	public static SecondOrderParam getDefaultDisutilityParameter() {
		return new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "Walters et al. 10.1016/s1098-3015(10)63316-5", 
			DU_HYPO_EPISODE, "UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]);
	}
	
	public static SecondOrderCostParam getDefaultCostParameter() {
		return new SecondOrderCostParam(StandardSevereHypoglycemiaEvent.STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", 
			"https://doi.org/10.1007/s13300-017-0285-0", 2017, COST_HYPO_EPISODE, SecondOrderParamsRepository.getRandomVariateForCost(COST_HYPO_EPISODE));
	}
	
	public static SecondOrderParam getDefaultMortalityParameter() {
		final double[] paramsDeathHypo = Statistics.betaParametersFromNormal(P_DEATH, Statistics.sdFrom95CI(new double[]{0.0058, 0.0068}));
		return new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", 
				"Canada", P_DEATH, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1]));
	}
	
	public class Instance extends AcuteComplicationSubmodel {
		private final double cost;
		private final double du;
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(DiabetesAcuteComplications.SHE, (rate) ? new AnnualRateBasedTimeToMultipleEventParam(
					SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					secParams.getnPatients(), 
					secParams.getProbParam(STR_P_HYPO), 
					new InterventionSpecificComplicationRR(new double[]{1.0, secParams.getOtherParam(STR_RR_HYPO)}))
					: new AnnualRiskBasedTimeToMultipleEventParam(
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
