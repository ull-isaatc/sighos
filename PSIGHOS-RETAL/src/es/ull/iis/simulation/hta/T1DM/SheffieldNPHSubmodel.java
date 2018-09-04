/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.Collection;
import java.util.TreeSet;

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
public class SheffieldNPHSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity ALB1 = new T1DMComorbidity("ALB1", "Microalbuminuria", MainComplications.NPH);
	public static T1DMComorbidity ALB2 = new T1DMComorbidity("ALB2", "Macroalbuminuria", MainComplications.NPH);
	public static T1DMComorbidity ESRD = new T1DMComorbidity("ESRD", "End-Stage Renal Disease", MainComplications.NPH);
	public static T1DMComorbidity[] NPHSubstates = new T1DMComorbidity[] {ALB1, ALB2, ESRD};

	private static final double BETA_ALB1 = 3.25;
	private static final double BETA_ALB2 = 7.95;
	private static final double P_DNC_ALB1 = 0.0436;
	private static final double P_DNC_ALB2 = 0.0037;
	private static final double P_NEU_ALB1 = 0.097;
	private static final double P_ALB1_ESRD = 0.0133;
	private static final double P_ALB2_ESRD = 0.1579;
	private static final double P_ALB1_ALB2 = 0.1565;
	private static final double P_DNC_ESRD = 0.0002;
	private static final double[] LIMITS_DNC_ESRD = {0.0, 0.0004}; // Assumption
	private static final double[] CI_DNC_NPH = {0.0136, 0.0736}; // Assumption
	private static final double[] CI_NEU_NPH = {0.055, 0.149};
	private static final double[] CI_NPH_ESRD = {0.01064, 0.01596};
	private static final double C_ALB2 = 5180.26;
	private static final double C_ESRD = 34259.48;
	private static final double TC_ALB2 = 33183.74;
	private static final double TC_ESRD = 3250.73;
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
		NEU_ALB1		
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [][] rnd;

	private final double[] costALB2;
	private final double[] costESRD;
	private final double duALB2;
	private final double duESRD;

	/**
	 * 
	 */
	public SheffieldNPHSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[NPHTransitions.values().length];
		invProb[NPHTransitions.HEALTHY_ALB1.ordinal()] = -1 / secParams.getProbability(ALB1);
		invProb[NPHTransitions.HEALTHY_ALB2.ordinal()] = -1 / secParams.getProbability(ALB2);
		invProb[NPHTransitions.HEALTHY_ESRD.ordinal()] = -1 / secParams.getProbability(ESRD);
		invProb[NPHTransitions.ALB1_ESRD.ordinal()] = -1 / secParams.getProbability(ALB1, ESRD);
		invProb[NPHTransitions.ALB2_ESRD.ordinal()] = -1 / secParams.getProbability(ALB2, ESRD);
		invProb[NPHTransitions.ALB1_ALB2.ordinal()] = -1 / secParams.getProbability(ALB1, ALB2);
		invProb[NPHTransitions.NEU_ALB1.ordinal()] = -1 / secParams.getProbability(MainComplications.NEU, ALB1);
		
		rr = new ComplicationRR[NPHTransitions.values().length];
		final ComplicationRR rrToALB1 = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1)); 
		final ComplicationRR rrToALB2 = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB2)); 
		rr[NPHTransitions.HEALTHY_ALB1.ordinal()] = rrToALB1;
		rr[NPHTransitions.HEALTHY_ALB2.ordinal()] = rrToALB2;
		rr[NPHTransitions.HEALTHY_ESRD.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[NPHTransitions.ALB1_ESRD.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[NPHTransitions.ALB2_ESRD.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[NPHTransitions.ALB1_ALB2.ordinal()] = rrToALB2;
		// Assume the same RR from healthy to ALB1 than from NEU to ALB1
		rr[NPHTransitions.NEU_ALB1.ordinal()] = rrToALB1;
		
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients][NPHSubstates.length];
		for (int i = 0; i < nPatients; i++) {
			for (int j = 0; j < NPHSubstates.length; j++) {
				rnd[i][j] = rng.draw();
			}
		}
		
		costALB2 = secParams.getCostsForHealthState(ALB1);
		costESRD = secParams.getCostsForHealthState(ESRD);
		
		duALB2 = secParams.getDisutilityForHealthState(ALB1);
		duESRD = secParams.getDisutilityForHealthState(ESRD);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_NPH = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_ALB1, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_NPH));
		final double[] paramsNEU_NPH = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_ALB1, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_NPH));
		final double[] paramsNPH_ESRD = SecondOrderParamsRepository.betaParametersFromNormal(P_ALB1_ESRD, SecondOrderParamsRepository.sdFrom95CI(CI_NPH_ESRD));

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ALB1), 
				"Probability of healthy to microalbuminutia, as processed in Sheffield Type 1 model", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_DNC_ALB1, "BetaVariate", paramsDNC_NPH[0], paramsDNC_NPH[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ALB2), 
				"Probability of healthy to macroalbuminutia, as processed in Sheffield Type 1 model", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_DNC_ALB2));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB1, ESRD), 
				"Probability of microalbuminuria to ESRD", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_ALB1_ESRD, "BetaVariate", paramsNPH_ESRD[0], paramsNPH_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB2, ESRD), 
				"Probability of macroalbuminuria to ESRD", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_ALB2_ESRD));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ALB1, ALB2), 
				"Probability of microalbuminuria to macroalbuminuria", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_ALB1_ALB2));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ESRD), 
				"Probability of healthy to ESRD, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
				P_DNC_ESRD, "UniformVariate", LIMITS_DNC_ESRD[0], LIMITS_DNC_ESRD[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(MainComplications.NEU, ALB1), 
				"", 
				"", 
				P_NEU_ALB1, "BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1]));		
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB1, 
				"%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				BETA_ALB1)); 
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ALB2, 
				"%risk reduction for combined groups for macroalbuminuria", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				BETA_ALB2)); 


		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ALB2.name(), 
				"Increased mortality risk due to severe proteinuria", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ESRD.name(), 
				"Increased mortality risk due to increased serum creatinine", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1)));
		
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ALB2, "Cost of ALB2", "", 2015, C_ALB2, SecondOrderParamsRepository.getRandomVariateForCost(C_ALB2)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ESRD, "Cost of ESRD", "", 2015, C_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(C_ESRD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ALB2, "Transition cost to ALB2", "", 2015, TC_ALB2, SecondOrderParamsRepository.getRandomVariateForCost(TC_ALB2)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ESRD, "Transition cost to ESRD", "", 2015, TC_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(TC_ESRD)));
		
		final double[] paramsDuNPH = SecondOrderParamsRepository.betaParametersFromNormal(DU_ALB2[0], DU_ALB2[1]);
		final double[] paramsDuESRD = SecondOrderParamsRepository.betaParametersFromNormal(DU_ESRD[0], DU_ESRD[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ALB2, "Disutility of ALB2", 
				"", DU_ALB2[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuNPH[0], paramsDuNPH[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ESRD, "Disutility of ESRD", 
				"", DU_ESRD[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuESRD[0], paramsDuESRD[1])));
		
		secParams.registerComplication(MainComplications.NPH);
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
					// RR from ALB1 to ESRD
					timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.ALB1_ESRD, limit);
					if (limit > timeToESRD)
						limit = timeToESRD;
					timeToALB2 = getAnnualBasedTimeToEvent(pat, NPHTransitions.ALB1_ALB2, limit);
				}
				else {
					// RR from healthy to ESRD
					timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_ESRD, limit);
					if (limit > timeToESRD)
						limit = timeToESRD;
					if (limit > previousTimeToALB2)
						limit = previousTimeToALB2;
					timeToALB2 = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_ALB2, limit);
					if (limit > timeToALB2)
						limit = timeToALB2;
					if (limit > previousTimeToALB1)
						limit = previousTimeToALB1;
					// RR from healthy to ALB1 (must be previous to ESRD and a (potential) formerly scheduled ALB1 event)
					timeToALB1 = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_ALB1, limit);
					if (pat.hasComplication(MainComplications.NEU)) {
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
		}
		return prog;
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, NPHTransitions transition, long limit) {
		final int ord = (NPHTransitions.HEALTHY_ALB1.equals(transition) || NPHTransitions.NEU_ALB1.equals(transition)) ? 0 : 1;
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
		if (state.contains(ALB2))
			return costALB2[0];		
		return 0.0;
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		if (ESRD.equals(newEvent))
			return costESRD[1];
		if (ALB2.equals(newEvent))
			return costALB2[1];
		return 0.0;
	}

	@Override
	public double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(ESRD))
			return duESRD;
		if (state.contains(ALB2))
			return duALB2;
		return 0.0;
	}
}
