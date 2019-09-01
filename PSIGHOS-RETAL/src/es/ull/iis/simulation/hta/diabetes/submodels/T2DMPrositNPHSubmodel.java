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
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.SheffieldComplicationRR;
import simkit.random.RandomVariateFactory;

/**
 * Based on the equations used in the PROSIT model ({@link https://www.prosit.de})
 * FIXME: Still not using RR for smoking or treatment with ACE in microalbuminuria
 * FIXME: Still not using remission to normoalbuminuria from microalbuminuria
 * FIXME: Still not using RR for ACE in macroalbuminuria nor ESRD
 * FIXME: Still not using remission to microalbuminuria from macroalbuminuria
 * @author Iván Castilla Rodríguez
 *
 */
public class T2DMPrositNPHSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage ALB1 = new DiabetesComplicationStage("ALB1", "Microalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ALB2 = new DiabetesComplicationStage("ALB2", "Macroalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ESRD = new DiabetesComplicationStage("ESRD", "End-Stage Renal Disease", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage[] STAGES = new DiabetesComplicationStage[] {ALB1, ALB2, ESRD};

	private static final String STR_COEF_ALB2 = "Coef_" + SecondOrderParamsRepository.STR_COST_PREFIX + ALB2;
	private static final String SOURCE1 = "Vupputuri et al 2011. 10.1016/j.diabres.2010.11.022";
	private static final double P_DNC_ALB1 = 1 - Math.exp(-0.0758); // From Vupputuri et al 2011 (Figure 1): 75.8 / 1000 person-years 
	private static final double[] CI_DNC_ALB1 = {1 - Math.exp(-0.0727), 1 - Math.exp(-0.0789)}; // From Vupputuri et al 2011 (Figure 1)
	private static final double HR_DURATION_ALB1 = 1.06; // From Vupputuri et al 2011 (Table 3)
	private static final double[] CI_HR_DURATION_ALB1 = {1.045, 1.075}; // From Vupputuri et al 2011 (Table 3), adjusted to be symmetric
	private static final double[] BASE_DURATION = {4.7, 4.1}; // From Vupputuri et al 2011 (Table 1): [Mean, SD]
	private static final String STR_DURATION = "Duration";
	private static final double HR_HBA1C_ALB1 = 1.12; // From Vupputuri et al 2011 (Table 3)
	private static final double[] CI_HR_HBA1C_ALB1 = {1.06, 1.18}; // From Vupputuri et al 2011 (Table 3)
	private static final double[] BASE_HBA1C = {7.6, 1.4}; // From Vupputuri et al 2011 (Table 1): [Mean, SD]
	private static final String STR_HBA1C = "HbA1c";

	private static final double P_DNC_ALB2 = 0.001; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x 
	private static final double[] CI_DNC_ALB2 = {0.0005, 0.0015}; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x. Adjusted to be symmetric 
	private static final double P_ALB1_ALB2 = 0.028; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x
	private static final double[] CI_ALB1_ALB2 = {0.0245, 0.0315}; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x. Adjusted to be symmetric 
	
	private static final double P_ALB1_ESRD = 0.003; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x
	private static final double[] CI_ALB1_ESRD = {0.0015, 0.0045}; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x. Adjusted to be symmetric 

	private static final double P_ALB2_ESRD = 0.023; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x
	private static final double[] CI_ALB2_ESRD = {0.0155, 0.0305}; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x. Adjusted to be symmetric 

	private static final double P_DNC_ESRD = 0.001; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x
	private static final double[] CI_DNC_ESRD = {0.0005, 0.0015}; // From Adler et al. 10.1046/j.1523-1755.2003.00712.x. Adjusted to be symmetric 
	
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
		HEALTHY_ALB2,
		ALB1_ESRD,
		ALB2_ESRD,
		ALB1_ALB2,
		HEALTHY_ESRD,
	}
	public T2DMPrositNPHSubmodel() {
		super(DiabetesChronicComplications.NPH, EnumSet.of(DiabetesType.T1));
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

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_ALB1 = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_ALB1, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_ALB1));
		final double[] paramsALB1_ALB2 = SecondOrderParamsRepository.betaParametersFromNormal(P_ALB1_ALB2, SecondOrderParamsRepository.sdFrom95CI(CI_ALB1_ALB2));
		final double[] paramsDNC_ALB2 = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_ALB2, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_ALB2));
		final double[] paramsALB1_ESRD = SecondOrderParamsRepository.betaParametersFromNormal(P_ALB1_ESRD, SecondOrderParamsRepository.sdFrom95CI(CI_ALB1_ESRD));
		final double[] paramsALB2_ESRD = SecondOrderParamsRepository.betaParametersFromNormal(P_ALB2_ESRD, SecondOrderParamsRepository.sdFrom95CI(CI_ALB2_ESRD));
		final double[] paramsDNC_ESRD = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_ESRD, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_ESRD));
		
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ALB1), 
				"Probability of healthy to microalbuminutia, as processed in PROSIT model", 
				SOURCE1, 
				P_DNC_ALB1, "BetaVariate", paramsDNC_ALB1[0], paramsDNC_ALB1[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ALB2), 
				"Probability of healthy to macroalbuminutia", 
				"Adler et al. 10.1046/j.1523-1755.2003.00712.x", 
				P_DNC_ALB2, "BetaVariate", paramsDNC_ALB2[0], paramsDNC_ALB2[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB1, ALB2), 
				"Probability of microalbuminuria to macroalbuminuria", 
				"Adler et al. 10.1046/j.1523-1755.2003.00712.x", 
				P_ALB1_ALB2, "BetaVariate", paramsALB1_ALB2[0], paramsALB1_ALB2[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB1, ESRD), 
				"Probability of microalbuminuria to ESRD", 
				"Adler et al. 10.1046/j.1523-1755.2003.00712.x", 
				P_ALB1_ESRD, "BetaVariate", paramsALB1_ESRD[0], paramsALB1_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB2, ESRD), 
				"Probability of macroalbuminuria to ESRD", 
				"Adler et al. 10.1046/j.1523-1755.2003.00712.x", 
				P_ALB2_ESRD, "BetaVariate", paramsALB2_ESRD[0], paramsALB2_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ESRD), 
				"Probability of healthy to ESRD", 
				"Adler et al. 10.1046/j.1523-1755.2003.00712.x", 
				P_DNC_ESRD, "BetaVariate", paramsDNC_ESRD[0], paramsDNC_ESRD[1]));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1 + "_" + STR_DURATION, 
				"hazard ratio per year of duration of diabetes in the progression to microalbuminuria", 
				SOURCE1, 
				HR_DURATION_ALB1, "NormalVariate", CI_HR_DURATION_ALB1[0], CI_HR_DURATION_ALB1[1]));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1 + "_" + STR_HBA1C, 
				"hazard ratio per 1 pp increment of HbA1c in the progression to microalbuminuria", 
				SOURCE1, 
				HR_HBA1C_ALB1, "NormalVariate", CI_HR_HBA1C_ALB1[0], CI_HR_HBA1C_ALB1[1]));
		secParams.addOtherParam(new SecondOrderParam("BASE_" + STR_DURATION, 
				"Base diabetes duration", 
				SOURCE1, 
				BASE_DURATION[0], "NormalVariate", BASE_DURATION[0], BASE_DURATION[1]));
		secParams.addOtherParam(new SecondOrderParam("BASE_" + STR_HBA1C, 
				"Base HbA1c", 
				SOURCE1, 
				BASE_HBA1C[0], "NormalVariate", BASE_HBA1C[0], BASE_HBA1C[1]));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ALB1.name(), 
				"Increased mortality risk due to microalbuminuria", 
				"https://doi.org/10.3310/hta9300 (Chapter 3. Figure 9)", 
				1.8, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.8, 1.6, 1.9, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ALB2.name(), 
				"Increased mortality risk due to severe proteinuria", 
				"https://doi.org/10.1001/archinte.160.8.1093", 
				2.47, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.47, 1.97, 3.10, 1)));
		// Different from PROSIT
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ESRD.name(), 
				"Increased mortality risk due to increased serum creatinine", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				3.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.96, 3.17, 4.94, 1)));
		
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
	
	private class VupputuriRR implements RRCalculator {
		private final double hrDurationMinus1;
		private final double hrHbA1cMinus1;
		private final double baseDuration;
		private final double baseHbA1c;

		public VupputuriRR(final double hrDuration, final double baseDuration, final double hrHbA1c, final double baseHbA1c) {
			hrDurationMinus1 = hrDuration - 1;
			hrHbA1cMinus1 = hrHbA1c - 1;
			this.baseDuration = baseDuration;
			this.baseHbA1c = baseHbA1c;
		}
		
		@Override
		public double getRR(DiabetesPatient pat) {
			return (1 + hrDurationMinus1 * (pat.getDurationOfDiabetes() - baseDuration)) * (1 + hrHbA1cMinus1 * (pat.getHba1c() - baseHbA1c));
		}
		
	}
	
	public class Instance extends ChronicComplicationSubmodel {

		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(T2DMPrositNPHSubmodel.this);
			final int nPatients = secParams.getnPatients();
			
			final RRCalculator rrToALB1 = new VupputuriRR(
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1 + "_" + STR_DURATION), 
					secParams.getOtherParam("BASE_" + STR_DURATION), 
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1 + "_" + STR_HBA1C), 
					secParams.getOtherParam("BASE_" + STR_HBA1C)); 
					new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1)); 

			addTime2Event(NPHTransitions.HEALTHY_ALB1.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB1), rrToALB1));
			addTime2Event(NPHTransitions.HEALTHY_ALB2.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB2), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.ALB1_ALB2.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB1, ALB2), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.HEALTHY_ALB2.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB2), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.HEALTHY_ESRD.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ESRD), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.ALB1_ESRD.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB1, ESRD), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.ALB2_ESRD.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB2, ESRD), SecondOrderParamsRepository.NO_RR));
					
			addData(secParams, ALB1);
			addData(secParams, ESRD);
			final double coefALB2 = secParams.getOtherParam(STR_COEF_ALB2);
			final double[] costALB1 = getData(ALB1).getCosts();
			final double[] costALB2 = new double[] {costALB1[0] * coefALB2, costALB1[1] * coefALB2};
			final double pInitALB2 = secParams.getInitProbParam(ALB2);
			addData(secParams.getDisutilityForChronicComplication(ALB2), costALB2, pInitALB2, secParams, ALB2);
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
						// RR from ALB1 to ESRD
						timeToESRD = getTimeToEvent(pat, NPHTransitions.ALB1_ESRD.ordinal(), limit);
						if (limit > timeToESRD)
							limit = timeToESRD;
						timeToALB2 = getTimeToEvent(pat, NPHTransitions.ALB1_ALB2.ordinal(), limit);
					}
					else {
						// RR from healthy to ESRD
						timeToESRD = getTimeToEvent(pat, NPHTransitions.HEALTHY_ESRD.ordinal(), limit);
						if (limit > timeToESRD)
							limit = timeToESRD;
						if (limit > previousTimeToALB2)
							limit = previousTimeToALB2;
						timeToALB2 = getTimeToEvent(pat, NPHTransitions.HEALTHY_ALB2.ordinal(), limit);
						if (limit > timeToALB2)
							limit = timeToALB2;
						if (limit > previousTimeToALB1)
							limit = previousTimeToALB1;
						// RR from healthy to ALB1 (must be previous to ESRD and a (potential) formerly scheduled ALB1 event)
						timeToALB1 = getTimeToEvent(pat, NPHTransitions.HEALTHY_ALB1.ordinal(), limit);
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
				return getData(ESRD).getCosts()[0];
			if (state.contains(ALB2))
				return getData(ALB2).getCosts()[0];		
			if (state.contains(ALB1))
				return getData(ALB1).getCosts()[0];		
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return getData(ESRD).getDisutility();
			if (state.contains(ALB2))
				return getData(ALB2).getDisutility();
			return 0.0;
		}
	}
}
