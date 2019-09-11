/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * NPH submodel based on Ly, 
 * FIXME: Requires review
 * @author Iván Castilla Rodríguez
 *
 */
public class LyNPHSubmodel90 extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage ALB1 = new DiabetesComplicationStage("ALB1", "Microalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ALB2 = new DiabetesComplicationStage("ALB2", "Macroalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ESRD = new DiabetesComplicationStage("ESRD", "End-Stage Renal Disease", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage[] NPHSubstates = new DiabetesComplicationStage[] {ALB1, ALB2, ESRD};

	private static final String STR_COEF_ALB2 = "Coef_" + SecondOrderParamsRepository.STR_COST_PREFIX + ALB2;
	private static final double REF_DCCT_HBA1C = 9.1;
	/** Risk reduction for a relative 10% lower HbA1c versus comparator in EDIC */
	private static final double RR_ALB1 = 0.502;
	/** Probability of onset of microalbuminuria depending on duration of diabetes */
	private static final double[] P_DNC_ALB1 = {0.00629, 0.01032};
	private static final int DURATION_ALB1 = 6;

	/** Hazard ratio for an absolute 1% higher HbA1c versus comparator in EDIC */
	private static final double RR_ALB2 = 1.25;
	/** Probability of onset of macroalbuminuria depending on time from microalbuminuria */
	private static final double[] P_ALB1_ALB2 = {0.04195, 0.02367, 0.02679};
	private static final int[] DURATION_ALB2 = {6, 11};
	private static final double P_NEU_ALB1 = 0.097;
	private static final double P_ALB2_ESRD = 0.0255;
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
		HEALTHY_ALB1_d0,
		HEALTHY_ALB1_d6,
		ALB1_ALB2_d0,
		ALB1_ALB2_d6,
		ALB1_ALB2_d11,
		ALB2_ESRD,
		NEU_ALB1		
	}
	
	public LyNPHSubmodel90() {
		super(DiabetesChronicComplications.NPH, EnumSet.of(DiabetesType.T1));
	}

	@Override
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
		return NPHSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return NPHSubstates;
	}

	@Override
	public int getNTransitions() {
		return NPHTransitions.values().length;
	}

	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return isEnabled() ? new Instance(secParams) : new DisabledChronicComplicationInstance(this);
	}
	
	public class Instance extends ChronicComplicationSubmodel {
		private final double[] invProb;
		private final RRCalculator[] rr;
		private final double [][] rnd;
		private final double[] rndALB1AtStart;

		private final double[] costALB1;
		private final double[] costALB2;
		private final double[] costESRD;
		private final double duALB2;
		private final double duESRD;

		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(LyNPHSubmodel90.this);
			
			invProb = new double[NPHTransitions.values().length];
			invProb[NPHTransitions.HEALTHY_ALB1_d0.ordinal()] = -1 / P_DNC_ALB1[0];
			invProb[NPHTransitions.HEALTHY_ALB1_d6.ordinal()] = -1 / P_DNC_ALB1[1];
			invProb[NPHTransitions.ALB1_ALB2_d0.ordinal()] = -1 / P_ALB1_ALB2[0];
			invProb[NPHTransitions.ALB1_ALB2_d6.ordinal()] = -1 / P_ALB1_ALB2[1];
			invProb[NPHTransitions.ALB1_ALB2_d11.ordinal()] = -1 / P_ALB1_ALB2[2];
			invProb[NPHTransitions.ALB2_ESRD.ordinal()] = -1 / secParams.getProbability(ALB2, ESRD);
			invProb[NPHTransitions.NEU_ALB1.ordinal()] = -1 / secParams.getProbability(DiabetesChronicComplications.NEU, ALB1);
			
			rr = new RRCalculator[NPHTransitions.values().length];
			final RRCalculator rrToALB1 = new HbA1c10ReductionComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1), REF_DCCT_HBA1C); 
			final RRCalculator rrToALB2 = new HbA1c1PPComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB2), REF_DCCT_HBA1C); 
			rr[NPHTransitions.HEALTHY_ALB1_d0.ordinal()] = rrToALB1;
			rr[NPHTransitions.HEALTHY_ALB1_d6.ordinal()] = rrToALB1;
			rr[NPHTransitions.ALB1_ALB2_d0.ordinal()] = rrToALB2;
			rr[NPHTransitions.ALB1_ALB2_d6.ordinal()] = rrToALB2;
			rr[NPHTransitions.ALB1_ALB2_d11.ordinal()] = rrToALB2;
			rr[NPHTransitions.ALB2_ESRD.ordinal()] = SecondOrderParamsRepository.NO_RR;
			// Assume the same RR from healthy to ALB1 than from NEU to ALB1
			rr[NPHTransitions.NEU_ALB1.ordinal()] = SecondOrderParamsRepository.NO_RR;
			
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
			rnd = new double[nPatients][NPHSubstates.length];
			rndALB1AtStart = new double[nPatients];
			for (int i = 0; i < nPatients; i++) {
				rndALB1AtStart[i] = rng.draw();
				for (int j = 0; j < NPHSubstates.length; j++) {
					rnd[i][j] = rng.draw();
				}
			}
			
			costALB1 = secParams.getCostsForChronicComplication(ALB1);
			final double coefALB2 = secParams.getOtherParam(STR_COEF_ALB2);
			costALB2 = new double[] {costALB1[0] * coefALB2, costALB1[1] * coefALB2}; 
			costESRD = secParams.getCostsForChronicComplication(ESRD);
			
			duALB2 = secParams.getDisutilityForChronicComplication(ALB2);
			duESRD = secParams.getDisutilityForChronicComplication(ESRD);
			
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
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
					timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.ALB2_ESRD, limit);
				}
				else if (state.contains(ALB1)) {
					final double yearsFromALB1 = (pat.getTs() - previousTimeToALB1) / BasicConfigParams.YEAR_CONVERSION;
					if (yearsFromALB1 < DURATION_ALB2[0])
						timeToALB2 = getAnnualBasedTimeToEvent(pat, NPHTransitions.ALB1_ALB2_d0, limit);
					else if (yearsFromALB1 < DURATION_ALB2[1])
						timeToALB2 = getAnnualBasedTimeToEvent(pat, NPHTransitions.ALB1_ALB2_d6, limit);					
					else
						timeToALB2 = getAnnualBasedTimeToEvent(pat, NPHTransitions.ALB1_ALB2_d11, limit);
				}
				else {
					if (limit > previousTimeToALB1)
						limit = previousTimeToALB1;
					// RR from healthy to ALB1 (must be previous to ESRD and a (potential) formerly scheduled ALB1 event)
					timeToALB1 = getAnnualBasedTimeToEvent(pat, 
							(pat.getDurationOfDiabetes() >= DURATION_ALB1) ? NPHTransitions.HEALTHY_ALB1_d6 : NPHTransitions.HEALTHY_ALB1_d0, limit);
					if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
						// RR from NEU to ALB1 (must be previous to the former transition)
						if (limit > timeToALB1)
							limit = timeToALB1;
						final long altTimeToNPH = getAnnualBasedTimeToEvent(pat, NPHTransitions.NEU_ALB1, limit);
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
			return prog;
		}

		private long getAnnualBasedTimeToEvent(DiabetesPatient pat, NPHTransitions transition, long limit) {
			final int ord = (NPHTransitions.HEALTHY_ALB1_d0.equals(transition) || NPHTransitions.HEALTHY_ALB1_d6.equals(transition) || NPHTransitions.NEU_ALB1.equals(transition)) ? 0 : 1;
			return getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()][ord], rr[transition.ordinal()].getRR(pat), limit);
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return costESRD[0];
			if (state.contains(ALB2))
				return costALB2[0];		
			if (state.contains(ALB1))
				return costALB1[0];		
			return 0.0;
		}

		@Override
		public double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newEvent) {
			if (ESRD.equals(newEvent))
				return costESRD[1];
			if (ALB2.equals(newEvent))
				return costALB2[1];
			if (ALB1.equals(newEvent))
				return costALB1[1];
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return duESRD;
			if (state.contains(ALB2))
				return duALB2;
			return 0.0;
		}
	}
}
