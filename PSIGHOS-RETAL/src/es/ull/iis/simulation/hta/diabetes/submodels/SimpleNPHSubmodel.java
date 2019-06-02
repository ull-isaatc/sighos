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
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleNPHSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage NPH = new DiabetesComplicationStage("NPH", "Neuropathy", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ESRD = new DiabetesComplicationStage("ESRD", "End-Stage Renal Disease", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage[] NPHSubstates = new DiabetesComplicationStage[] {NPH, ESRD};

	private static final double BETA_NPH = 3.25;
	private static final double P_DNC_NPH = 0.0436;
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_ESRD = 0.0133;
	private static final double P_DNC_ESRD = 0.0002;
	private static final double[] LIMITS_DNC_ESRD = {0.0, 0.0004}; // Assumption
	private static final double[] CI_DNC_NPH = {0.0136, 0.0736}; // Assumption
	private static final double[] CI_NEU_NPH = {0.055, 0.149};
	private static final double[] CI_NPH_ESRD = {0.01064, 0.01596};
	private static final double C_NPH = 0.0;
	private static final double[] LIMITS_C_NPH = {0.0, 500.0}; // Assumption
	private static final double C_ESRD = 34259.48;
	private static final double TC_ESRD = 3250.73;
	// Utility (avg, SD) from either Bagust and Beale; or Sullivan
	private static final double[] DU_NPH = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.048, (0.091 - 0.005) / 3.92}: new double[] {0.0527, 0.0001};
	// Utility (avg, SD) from either Wasserfallen et al.; or Sullivan
	private static final double[] DU_ESRD = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.204, (0.342 - 0.066) / 3.92} : new double[] {0.0603, 0.0002};

	public enum NPHTransitions {
		HEALTHY_NPH,
		NPH_ESRD,
		HEALTHY_ESRD,
		NEU_NPH		
	}
	
	public SimpleNPHSubmodel() {
		super(DiabetesChronicComplications.NPH, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_NPH = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_NPH, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_NPH));
		final double[] paramsNEU_NPH = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_NPH, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_NPH));
		final double[] paramsNPH_ESRD = SecondOrderParamsRepository.betaParametersFromNormal(P_NPH_ESRD, SecondOrderParamsRepository.sdFrom95CI(CI_NPH_ESRD));

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, NPH), 
				"Probability of healthy to microalbuminutia, as processed in Sheffield Type 1 model", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_DNC_NPH, "BetaVariate", paramsDNC_NPH[0], paramsDNC_NPH[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(NPH, ESRD), 
				"Probability of microalbuminuria to ESRD", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_NPH_ESRD, "BetaVariate", paramsNPH_ESRD[0], paramsNPH_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ESRD), 
				"Probability of healthy to ESRD, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
				P_DNC_ESRD, "UniformVariate", LIMITS_DNC_ESRD[0], LIMITS_DNC_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.NEU, DiabetesChronicComplications.NPH), 
				"", 
				"", 
				P_NEU_NPH, "BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1]));		
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + NPH, 
				"%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				BETA_NPH)); 

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + NPH.name(), 
				"Increased mortality risk due to severe proteinuria", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ESRD.name(), 
				"Increased mortality risk due to increased serum creatinine", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1)));
		
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NPH, "Cost of NPH", "", 2015, C_NPH, RandomVariateFactory.getInstance("UniformVariate", LIMITS_C_NPH[0], LIMITS_C_NPH[1])));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ESRD, "Cost of ESRD", "", 2015, C_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(C_ESRD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ESRD, "Transition cost to ESRD", "", 2015, TC_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(TC_ESRD)));
		
		final double[] paramsDuNPH = SecondOrderParamsRepository.betaParametersFromNormal(DU_NPH[0], DU_NPH[1]);
		final double[] paramsDuESRD = SecondOrderParamsRepository.betaParametersFromNormal(DU_ESRD[0], DU_ESRD[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NPH, "Disutility of NPH", 
				"", DU_NPH[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuNPH[0], paramsDuNPH[1])));
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
		return new Instance(secParams);
	}
	
	public class Instance extends ChronicComplicationSubmodel {

		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(SimpleNPHSubmodel.this);
			
			final RRCalculator rrToNPH = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NPH)); 
			
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();

			addTime2Event(NPHTransitions.HEALTHY_NPH.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NPH), rrToNPH));
			addTime2Event(NPHTransitions.HEALTHY_ESRD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(ESRD), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NPHTransitions.NPH_ESRD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NPH, ESRD), SecondOrderParamsRepository.NO_RR));
			// Assume the same RR from healthy to NPH than from NEU to NPH
			addTime2Event(NPHTransitions.NEU_NPH.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(DiabetesChronicComplications.NEU, NPH), rrToNPH));

			addData(secParams, NPH);
			addData(secParams, ESRD);
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
				// Checks whether there is somewhere to transit to
				if (!state.contains(ESRD)) {
					long timeToESRD = Long.MAX_VALUE;
					long timeToNPH = Long.MAX_VALUE;
					final long previousTimeToNPH = pat.getTimeToChronicComorbidity(NPH);
					final long previousTimeToESRD = pat.getTimeToChronicComorbidity(ESRD);
					long limit = pat.getTimeToDeath();
					if (limit > previousTimeToESRD)
						limit = previousTimeToESRD;
					if (state.contains(NPH)) {
						// RR from NPH to ESRD
						timeToESRD = getTimeToEvent(pat, NPHTransitions.NPH_ESRD.ordinal(), limit);
					}
					else {
						// RR from healthy to ESRD
						timeToESRD = getTimeToEvent(pat, NPHTransitions.HEALTHY_ESRD.ordinal(), limit);
						if (limit > timeToESRD)
							limit = timeToESRD;
						if (limit > previousTimeToNPH)
							limit = previousTimeToNPH;
						// RR from healthy to NPH (must be previous to ESRD and a (potential) formerly scheduled NPH event)
						timeToNPH = getTimeToEvent(pat, NPHTransitions.HEALTHY_NPH.ordinal(), limit);
						if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
							// RR from NEU to NPH (must be previous to the former transition)
							if (limit > timeToNPH)
								limit = timeToNPH;
							final long altTimeToNPH = getTimeToEvent(pat, NPHTransitions.NEU_NPH.ordinal(), limit);
							if (altTimeToNPH < timeToNPH)
								timeToNPH = altTimeToNPH;						
						}
					}
					// Check previously scheduled events
					if (timeToNPH != Long.MAX_VALUE) {
						if (previousTimeToNPH < Long.MAX_VALUE) {
							prog.addCancelEvent(NPH);
						}
						prog.addNewEvent(NPH, timeToNPH);
					}
					if (timeToESRD != Long.MAX_VALUE) {
						if (previousTimeToESRD < Long.MAX_VALUE) {
							prog.addCancelEvent(ESRD);
						}
						prog.addNewEvent(ESRD, timeToESRD);
						// If the new ESRD event happens before a previously scheduled NPH event, the latter must be cancelled 
						if (previousTimeToNPH < Long.MAX_VALUE && timeToESRD < previousTimeToNPH)
							prog.addCancelEvent(NPH);
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
			else if (state.contains(NPH))
				return getData(NPH).getCosts()[0];
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return getData(ESRD).getDisutility();
			else if (state.contains(NPH))
				return getData(NPH).getDisutility();
			return 0.0;
		}
		
	}
}
