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
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * Based on data from the UKPDS study
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class T2DMNPHSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage ALB1 = new DiabetesComplicationStage("ALB1", "Microalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ALB2 = new DiabetesComplicationStage("ALB2", "Macroalbuminuria", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ESRD = new DiabetesComplicationStage("ESRD", "End-Stage Renal Disease", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage[] STAGES = new DiabetesComplicationStage[] {ALB1, ALB2, ESRD};

	private static final double[] ADJUST = {0.65, 0.55, 0.5};
//	private static final double[] ADJUST = {1.0, 1.0, 1.0};
	private static final String STR_SOURCE_ADLER = "Adler et al. 10.1046/j.1523-1755.2003.00712.x";
	private static final double P_DNC_ALB1 = 0.02; // From Adler et al. 
	private static final double[] CI_DNC_ALB1 = {0.0185, 0.0215}; // From Adler et al.
	
	/** Increment of risk of micro or macroalbuminuria associated to 1% increment of HbA1c */
	private static final double RR_HBA1C = 1 / (1 - 0.37); 
	private static final String STR_COEF_ALB2 = "Coef_" + SecondOrderParamsRepository.STR_COST_PREFIX + ALB2;
	private static final double[] BASE_HBA1C = {7.2, 1.8}; // From Adler et al. 
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
	public T2DMNPHSubmodel() {
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
		return isEnabled() ? new Instance(secParams) : new DisabledChronicComplicationInstance(this);
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_ALB1 = Statistics.betaParametersFromNormal(P_DNC_ALB1, Statistics.sdFrom95CI(CI_DNC_ALB1));
		final double[] paramsALB1_ALB2 = Statistics.betaParametersFromNormal(P_ALB1_ALB2, Statistics.sdFrom95CI(CI_ALB1_ALB2));
		final double[] paramsDNC_ALB2 = Statistics.betaParametersFromNormal(P_DNC_ALB2, Statistics.sdFrom95CI(CI_DNC_ALB2));
		final double[] paramsALB1_ESRD = Statistics.betaParametersFromNormal(P_ALB1_ESRD, Statistics.sdFrom95CI(CI_ALB1_ESRD));
		final double[] paramsALB2_ESRD = Statistics.betaParametersFromNormal(P_ALB2_ESRD, Statistics.sdFrom95CI(CI_ALB2_ESRD));
		final double[] paramsDNC_ESRD = Statistics.betaParametersFromNormal(P_DNC_ESRD, Statistics.sdFrom95CI(CI_DNC_ESRD));
		
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ALB1), 
				"Probability of healthy to microalbuminutia", 
				STR_SOURCE_ADLER, 
				P_DNC_ALB1, "BetaVariate", paramsDNC_ALB1[0], paramsDNC_ALB1[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ALB2), 
				"Probability of healthy to macroalbuminutia", 
				STR_SOURCE_ADLER, 
				P_DNC_ALB2, "BetaVariate", paramsDNC_ALB2[0], paramsDNC_ALB2[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB1, ALB2), 
				"Probability of microalbuminuria to macroalbuminuria", 
				STR_SOURCE_ADLER, 
				P_ALB1_ALB2, "BetaVariate", paramsALB1_ALB2[0], paramsALB1_ALB2[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB1, ESRD), 
				"Probability of microalbuminuria to ESRD", 
				STR_SOURCE_ADLER, 
				P_ALB1_ESRD, "BetaVariate", paramsALB1_ESRD[0], paramsALB1_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB2, ESRD), 
				"Probability of macroalbuminuria to ESRD", 
				STR_SOURCE_ADLER, 
				P_ALB2_ESRD, "BetaVariate", paramsALB2_ESRD[0], paramsALB2_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ESRD), 
				"Probability of healthy to ESRD", 
				STR_SOURCE_ADLER, 
				P_DNC_ESRD, "BetaVariate", paramsDNC_ESRD[0], paramsDNC_ESRD[1]));

		secParams.addOtherParam(new SecondOrderParam("BASE_" + STR_HBA1C, 
				"Base HbA1c", 
				STR_SOURCE_ADLER, 
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
		
		final double[] paramsDuNPH = Statistics.betaParametersFromNormal(DU_ALB2[0], DU_ALB2[1]);
		final double[] paramsDuESRD = Statistics.betaParametersFromNormal(DU_ESRD[0], DU_ESRD[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ALB2, "Disutility of ALB2", 
				"", DU_ALB2[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuNPH[0], paramsDuNPH[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ESRD, "Disutility of ESRD", 
				"", DU_ESRD[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuESRD[0], paramsDuESRD[1])));

		addSecondOrderInitProportion(secParams);
	}
	
	public class Instance extends ChronicComplicationSubmodel {

		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(T2DMNPHSubmodel.this);
			final int nPatients = secParams.getnPatients();
			
			final RRCalculator rrToALB1 = new HbA1c1PPComplicationRR(RR_HBA1C, secParams.getOtherParam("BASE_" + STR_HBA1C)); 

			addTime2Event(NPHTransitions.HEALTHY_ALB1.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB1) * ADJUST[0], rrToALB1));
			addTime2Event(NPHTransitions.ALB1_ALB2.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB1, ALB2) * ADJUST[1], rrToALB1));
			addTime2Event(NPHTransitions.HEALTHY_ALB2.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB2) * ADJUST[1], rrToALB1));
			addTime2Event(NPHTransitions.HEALTHY_ESRD.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ESRD) * ADJUST[2], SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.ALB1_ESRD.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB1, ESRD) * ADJUST[2], SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.ALB2_ESRD.ordinal(), new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					nPatients, secParams.getProbability(ALB2, ESRD) * ADJUST[2], SecondOrderParamsRepository.NO_RR));
					
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
				adjustProgression(prog, ALB1, timeToALB1, previousTimeToALB1);
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
