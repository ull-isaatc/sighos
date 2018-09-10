/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.MainChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.SheffieldComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SimpleNPHSubmodel extends ChronicComplicationSubmodel {
	public static T1DMComorbidity NPH = new T1DMComorbidity("NPH", "Neuropathy", MainChronicComplications.NPH);
	public static T1DMComorbidity ESRD = new T1DMComorbidity("ESRD", "End-Stage Renal Disease", MainChronicComplications.NPH);
	public static T1DMComorbidity[] NPHSubstates = new T1DMComorbidity[] {NPH, ESRD};

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
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [][] rnd;

	private final double[] costNPH;
	private final double[] costESRD;
	private final double duNPH;
	private final double duESRD;

	/**
	 * 
	 */
	public SimpleNPHSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[NPHTransitions.values().length];
		invProb[NPHTransitions.HEALTHY_NPH.ordinal()] = -1 / secParams.getProbability(NPH);
		invProb[NPHTransitions.HEALTHY_ESRD.ordinal()] = -1 / secParams.getProbability(ESRD);
		invProb[NPHTransitions.NPH_ESRD.ordinal()] = -1 / secParams.getProbability(NPH, ESRD);
		invProb[NPHTransitions.NEU_NPH.ordinal()] = -1 / secParams.getProbability(MainChronicComplications.NEU, NPH);
		
		rr = new ComplicationRR[NPHTransitions.values().length];
		final ComplicationRR rrToNPH = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NPH)); 
		rr[NPHTransitions.HEALTHY_NPH.ordinal()] = rrToNPH;
		rr[NPHTransitions.HEALTHY_ESRD.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[NPHTransitions.NPH_ESRD.ordinal()] = SecondOrderParamsRepository.NO_RR;
		// Assume the same RR from healthy to NPH than from NEU to NPH
		rr[NPHTransitions.NEU_NPH.ordinal()] = rrToNPH;
		
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients][NPHSubstates.length];
		for (int i = 0; i < nPatients; i++) {
			for (int j = 0; j < NPHSubstates.length; j++) {
				rnd[i][j] = rng.draw();
			}
		}
		
		costNPH = secParams.getCostsForHealthState(NPH);
		costESRD = secParams.getCostsForHealthState(ESRD);
		
		duNPH = secParams.getDisutilityForHealthState(NPH);
		duESRD = secParams.getDisutilityForHealthState(ESRD);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
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
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(MainChronicComplications.NEU, MainChronicComplications.NPH), 
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
		
		secParams.registerComplication(MainChronicComplications.NPH);
		secParams.registerHealthStates(NPHSubstates);
		
	}

	@Override
	public T1DMProgression getNextComplication(T1DMPatient pat) {
		final T1DMProgression prog = new T1DMProgression();
		if (enable) {
			final TreeSet<T1DMComorbidity> state = pat.getDetailedState();
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
					timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.NPH_ESRD, limit);
				}
				else {
					// RR from healthy to ESRD
					timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_ESRD, limit);
					if (limit > timeToESRD)
						limit = timeToESRD;
					if (limit > previousTimeToNPH)
						limit = previousTimeToNPH;
					// RR from healthy to NPH (must be previous to ESRD and a (potential) formerly scheduled NPH event)
					timeToNPH = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_NPH, limit);
					if (pat.hasComplication(MainChronicComplications.NEU)) {
						// RR from NEU to NPH (must be previous to the former transition)
						if (limit > timeToNPH)
							limit = timeToNPH;
						final long altTimeToNPH = getAnnualBasedTimeToEvent(pat, NPHTransitions.NEU_NPH, limit);
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

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, NPHTransitions transition, long limit) {
		final int ord = (NPHTransitions.HEALTHY_NPH.equals(transition) || NPHTransitions.NEU_NPH.equals(transition)) ? 0 : 1;
		return getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()][ord], rr[transition.ordinal()].getRR(pat), limit);
	}

	@Override
	public int getNSubstates() {
		return NPHSubstates.length;
	}

	@Override
	public T1DMComorbidity[] getSubstates() {
		return NPHSubstates;
	}

	@Override
	public TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat) {
		return new TreeSet<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(ESRD))
			return costESRD[0];
		return costNPH[0];
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		if (ESRD.equals(newEvent))
			return costESRD[1];
		return costNPH[1];
	}

	@Override
	public double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(ESRD))
			return duESRD;
		return duNPH;
	}
}
