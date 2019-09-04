/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.DurationOfDiabetesBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LyNPHSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage ALB1 = new DiabetesComplicationStage("ALB1", "Microalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ALB2 = new DiabetesComplicationStage("ALB2", "Macroalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ESRD = new DiabetesComplicationStage("ESRD", "End-Stage Renal Disease", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage[] STAGES = new DiabetesComplicationStage[] {ALB1, ALB2, ESRD};

	private static final String STR_COEF_ALB2 = "Coef_" + SecondOrderParamsRepository.STR_COST_PREFIX + ALB2;
	private static final double REF_DCCT_HBA1C = 9.1;
	/** Risk reduction for a relative 10% lower HbA1c versus comparator in EDIC */
	private static final double RR_ALB1 = 0.24;
	/** Probability of onset of microalbuminuria depending on duration of diabetes */
	private static final double[][] P_DNC_ALB1 = {{9, 0.022}, {Double.MAX_VALUE, 0.036}}; 
	/** Hazard ratio for an absolute 1% higher HbA1c versus comparator in EDIC */
	private static final double RR_ALB2 = 1/0.53;
	/** Probability of onset of macroalbuminuria depending on duration of diabetes */
	private static final double[][] P_ALB1_ALB2 = {{9, 0.0}, {Double.MAX_VALUE, 0.016966}};
	private static final double P_NEU_ALB1 = 0.097;
	private static final double P_ALB2_ESRD = 0.058;
	private static final double[] CI_NEU_NPH = {0.055, 0.149};
	private static final double C_ALB1 = 0.0;
	private static final double C_ESRD = 34259.48;
	private static final double TC_ESRD = 3250.73;
	private static final double[] LIMITS_C_ALB1 = {0.0, 500.0}; // Assumption
	private static final double[] COEF_C_ALB2 = {1.0, 2.0}; // Assumption
	// Utility (avg, SD) from either Bagust and Beale; or Sullivan
	private static final double[] DU_ALB2 = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.048, (0.091 - 0.005) / 3.92}: new double[] {0.0527, 0.0001};
	// Utility (avg, SD) from either Wasserfallen et al.; or Sullivan
	private static final double[] DU_ESRD = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.204, (0.342 - 0.066) / 3.92} : new double[] {0.0603, 0.0002};

	public enum NPHTransitions {
		HEALTHY_ALB1,
		ALB1_ALB2,
		ALB2_ESRD,
		NEU_ALB1;
	}

	/**
	 * 
	 */
	public LyNPHSubmodel() {
		super(DiabetesChronicComplications.NPH, EnumSet.of(DiabetesType.T1));
	}

	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsNEU_NPH = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_ALB1, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_NPH));

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB2, ESRD), 
				"Probability of macroalbuminuria to ESRD", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_ALB2_ESRD, SecondOrderParamsRepository.getRandomVariateForProbability(P_ALB2_ESRD)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.NEU, ALB1), 
				"", 
				"", 
				P_NEU_ALB1, "BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1]));		
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1, 
				"Risk reduction for a relative 10% lower HbA1c versus comparator in EDIC", 
				"EDIC adapted from 10.2337/dc13-2113", 
				RR_ALB1)); 
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB2, 
				"Hazard ratio for an absolute 1% higher HbA1c versus comparator in EDIC", 
				"EDIC adapted from 10.2337/dc13-2113", 
				RR_ALB2)); 


		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ALB2.name(), 
				"Increased mortality risk due to severe proteinuria", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ESRD.name(), 
				"Increased mortality risk due to increased serum creatinine", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1)));
		
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ALB1, "Cost of ALB1", "Assumption", 2018, 
				C_ALB1, RandomVariateFactory.getInstance("UniformVariate", LIMITS_C_ALB1[0], LIMITS_C_ALB1[1])));
		secParams.addOtherParam(new SecondOrderParam(STR_COEF_ALB2, "Coefficient cost of ALB2", "Assumption", 
				COEF_C_ALB2[0], RandomVariateFactory.getInstance("UniformVariate", COEF_C_ALB2[0], COEF_C_ALB2[1])));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ESRD, "Cost of ESRD", "", 2015, C_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(C_ESRD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ESRD, "Transition cost to ESRD", "", 2015, TC_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(TC_ESRD)));
		
		final double[] paramsDuNPH = SecondOrderParamsRepository.betaParametersFromNormal(DU_ALB2[0], DU_ALB2[1]);
		final double[] paramsDuESRD = SecondOrderParamsRepository.betaParametersFromNormal(DU_ESRD[0], DU_ESRD[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ALB2, "Disutility of ALB2", 
				"", DU_ALB2[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuNPH[0], paramsDuNPH[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ESRD, "Disutility of ESRD", 
				"", DU_ESRD[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuESRD[0], paramsDuESRD[1])));

		addSecondOrderInitProportion(secParams);
	}

	@Override
	public int getNStages() {
		return STAGES.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return STAGES;
	}

	@Override
	public int getNTransitions() {
		return NPHTransitions.values().length;
	}

	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}

	public class Instance extends ChronicComplicationSubmodel {

		/**
		 * 
		 */
		private Instance(SecondOrderParamsRepository secParams) {
			super(LyNPHSubmodel.this);
			final int nPatients = secParams.getnPatients();
			
			final RRCalculator rrToALB1 = new HbA1c10ReductionComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1), REF_DCCT_HBA1C); 
			final RRCalculator rrToALB2 = new HbA1c1PPComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB2), REF_DCCT_HBA1C); 

			addTime2Event(NPHTransitions.HEALTHY_ALB1.ordinal(), new DurationOfDiabetesBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, P_DNC_ALB1, rrToALB1));
			addTime2Event(NPHTransitions.ALB1_ALB2.ordinal(), new DurationOfDiabetesBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, P_ALB1_ALB2, rrToALB2));
			addTime2Event(NPHTransitions.ALB2_ESRD.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, secParams.getProbability(ALB2, ESRD), SecondOrderParamsRepository.NO_RR));
			// Assuming no extra RR from NEU to ALB1 with respect to healthy to ALB1  
			addTime2Event(NPHTransitions.NEU_ALB1.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, secParams.getProbability(DiabetesChronicComplications.NEU, ALB1), SecondOrderParamsRepository.NO_RR));
			
			setStageInstance(ALB1, secParams);
			setStageInstance(ESRD, secParams);
			final double coefALB2 = secParams.getOtherParam(STR_COEF_ALB2);
			final double[] costALB1 = getCosts(ALB1);
			final double[] costALB2 = new double[] {costALB1[0] * coefALB2, costALB1[1] * coefALB2};
			final double pInitALB2 = secParams.getInitProbParam(ALB2);
			setStageInstance(ALB2, secParams.getDisutilityForChronicComplication(ALB2), costALB2, pInitALB2, 
					secParams.getIMR(ALB2), secParams.getnPatients());
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
				// Checks whether there is somewhere to transit to
				if (!state.contains(ESRD)) {
					long timeToESRD = Long.MAX_VALUE;
					long timeToALB1 = Long.MAX_VALUE;
					long timeToALB2 = Long.MAX_VALUE;
					final long previousTimeToALB1 = pat.getTimeToChronicComorbidity(ALB1);
					final long previousTimeToALB2 = pat.getTimeToChronicComorbidity(ALB2);
					final long previousTimeToESRD = pat.getTimeToChronicComorbidity(ESRD);
					long limit = pat.getTimeToDeath();
					if (limit > previousTimeToESRD)
						limit = previousTimeToESRD;
					if (state.contains(ALB2)) {
						// RR from ALB2 to ESRD
						timeToESRD = getTimeToEvent(pat, NPHTransitions.ALB2_ESRD.ordinal(), limit);
					}
					else if (state.contains(ALB1)) {
						timeToALB2 = getTimeToEvent(pat, NPHTransitions.ALB1_ALB2.ordinal(), limit);
					}
					else {
						if (limit > previousTimeToALB1)
							limit = previousTimeToALB1;
						// RR from healthy to ALB1 (must be previous to ESRD and a (potential) formerly scheduled ALB1 event)
						timeToALB1 = getTimeToEvent(pat, NPHTransitions.HEALTHY_ALB1.ordinal(), limit);
						if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
							// RR from NEU to ALB1 (must be previous to the former transition)
							if (limit > timeToALB1)
								limit = timeToALB1;
							final long altTimeToNPH = getTimeToEvent(pat, NPHTransitions.NEU_ALB1.ordinal(), limit);
							if (altTimeToNPH < timeToALB1)
								timeToALB1 = altTimeToNPH;						
						}
					}
					// Check previously scheduled events
					if (timeToALB1 != Long.MAX_VALUE) {
						if (previousTimeToALB1 < Long.MAX_VALUE) {
							prog.addCancelEvent(ALB1);
						}
						prog.addNewEvent(ALB1, timeToALB1);
					}
					if (timeToALB2 != Long.MAX_VALUE) {
						if (previousTimeToALB2 < Long.MAX_VALUE) {
							prog.addCancelEvent(ALB2);
						}
						prog.addNewEvent(ALB2, timeToALB2);
						// If the new ALB2 event happens before a previously scheduled ALB1 event, the latter must be cancelled 
						if (previousTimeToALB1 < Long.MAX_VALUE && timeToALB2 < previousTimeToALB1)
							prog.addCancelEvent(ALB1);
					}
					if (timeToESRD != Long.MAX_VALUE) {
						if (previousTimeToESRD < Long.MAX_VALUE) {
							prog.addCancelEvent(ESRD);
						}
						prog.addNewEvent(ESRD, timeToESRD);
						// If the new ESRD event happens before a previously scheduled ALB1 or ALB2 event, the latter must be cancelled 
						if (previousTimeToALB2 < Long.MAX_VALUE && timeToESRD < previousTimeToALB2)
							prog.addCancelEvent(ALB2);
						if (previousTimeToALB1 < Long.MAX_VALUE && timeToESRD < previousTimeToALB1)
							prog.addCancelEvent(ALB1);
					}
				}
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return getCosts(ESRD)[0];
			if (state.contains(ALB2))
				return getCosts(ALB2)[0];		
			if (state.contains(ALB1))
				return getCosts(ALB1)[0];		
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return getDisutility(ESRD);
			if (state.contains(ALB2))
				return getDisutility(ALB2);
			return 0.0;
		}
		
	}
}
