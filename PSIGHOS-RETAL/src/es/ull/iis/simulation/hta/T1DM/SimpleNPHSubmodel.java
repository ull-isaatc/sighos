/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleNPHSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity NPH = new T1DMComorbidity("NPH", "Neuropathy", MainComplications.NPH);
	public static T1DMComorbidity ESRD = new T1DMComorbidity("ESRD", "End-Stage Renal Disease", MainComplications.NPH);
	public static T1DMComorbidity[] NPHSubstates = new T1DMComorbidity[] {NPH, ESRD};

	private static final double REF_HBA1C = 9.1; 
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_ESRD = 0.0133;
	private static final double[] CI_NEU_NPH = {0.055, 0.149};
	private static final double[] CI_NPH_ESRD = {0.01064, 0.01596};
	private static final double C_NPH = 5180.26;
	private static final double C_ESRD = 34259.48;
	private static final double TC_NPH = 33183.74;
	private static final double TC_ESRD = 3250.73;
	private static final double DU_NPH = 0.0527;
	private static final double DU_ESRD = 0.0603;

	public enum NPHTransitions {
		HEALTHY_NPH,
		NPH_ESRD,
		HEALTHY_ESRD,
		NEU_NPH		
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [][] rnd;

	/**
	 * 
	 */
	public SimpleNPHSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[NPHTransitions.values().length];
		invProb[NPHTransitions.HEALTHY_NPH.ordinal()] = -1 / secParams.getProbability(NPH);
		invProb[NPHTransitions.HEALTHY_ESRD.ordinal()] = -1 / secParams.getProbability(ESRD);
		invProb[NPHTransitions.NPH_ESRD.ordinal()] = -1 / secParams.getProbability(NPH, ESRD);
		invProb[NPHTransitions.NEU_NPH.ordinal()] = -1 / secParams.getProbability(MainComplications.NEU, NPH);
		rr = new ComplicationRR[NPHTransitions.values().length];
		final ComplicationRR rrToNPH = new HbA1c10ReductionComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NPH.name()), REF_HBA1C); 
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
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double coefHbA1c = REF_HBA1C/10;
		// From Sheffield
		final double pDNC_NPH = 0.0436 * Math.pow(coefHbA1c, 3.25);
		
		final double[] paramsNEU_NPH = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_NPH, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_NPH));
		final double[] paramsNPH_ESRD = SecondOrderParamsRepository.betaParametersFromNormal(P_NPH_ESRD, SecondOrderParamsRepository.sdFrom95CI(CI_NPH_ESRD));

		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, NPH), 
				"Probability of healthy to microalbuminutia, as processed in Sheffield Type 1 model", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				pDNC_NPH));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(NPH, ESRD), 
				"Probability of microalbuminuria to ESRD", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_NPH_ESRD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_ESRD[0], paramsNPH_ESRD[1])));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, ESRD), 
				"Probability of healthy to ESRD, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
				0.0002));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(MainComplications.NEU, MainComplications.NPH), 
				"", 
				"", 
				P_NEU_NPH, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1])));		
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + MainComplications.NPH.name(), 
				"%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.25, RandomVariateFactory.getInstance("NormalVariate", 0.25, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.19, 0.32}))));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + NPH.name(), 
				"Increased mortality risk due to severe proteinuria", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + ESRD.name(), 
				"Increased mortality risk due to increased serum creatinine", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1)));
		
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NPH, "Cost of NPH", "", 2015, C_NPH, SecondOrderParamsRepository.getRandomVariateForCost(C_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ESRD, "Cost of ESRD", "", 2015, C_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(C_ESRD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NPH, "Transition cost to NPH", "", 2015, TC_NPH, SecondOrderParamsRepository.getRandomVariateForCost(TC_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ESRD, "Transition cost to ESRD", "", 2015, TC_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(TC_ESRD)));
		
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NPH, "Disutility of NPH", "", DU_NPH));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ESRD, "Disutility of ESRD", "", DU_ESRD));
		
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
					if (pat.hasComplication(MainComplications.NEU)) {
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
}
