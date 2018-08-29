/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaNPHSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity NPH = new T1DMComorbidity("NPH", "Neuropathy", MainComplications.NPH);
	public static T1DMComorbidity ESRD = new T1DMComorbidity("ESRD", "End-Stage Renal Disease", MainComplications.NPH);
	public static T1DMComorbidity[] NPHSubstates = new T1DMComorbidity[] {NPH, ESRD};

	private static final double P_DNC_NPH = 0.0094;
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_ESRD = 0.072;
	private static final double RR_NPH = 0.742;
	private static final double C_NPH = 13;
	private static final double C_ESRD = 12808;
	private static final double TC_NPH = 80 - C_NPH;
	private static final double TC_ESRD = 28221 - C_ESRD;
	private static final double DU_NPH = CanadaSecondOrderParams.U_GENERAL_POP - CanadaSecondOrderParams.DU_DNC - 0.575;
	private static final double DU_ESRD = CanadaSecondOrderParams.U_GENERAL_POP - CanadaSecondOrderParams.DU_DNC - 0.49;
	
//	addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
	private static final double REF_HBA1C = 9.1; 

	public enum NPHTransitions {
		HEALTHY_NPH,
		NPH_ESRD,
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
	public CanadaNPHSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[NPHTransitions.values().length];
		invProb[NPHTransitions.HEALTHY_NPH.ordinal()] = -1 / secParams.getProbability(NPH);
		invProb[NPHTransitions.NPH_ESRD.ordinal()] = -1 / secParams.getProbability(NPH, ESRD);
		invProb[NPHTransitions.NEU_NPH.ordinal()] = -1 / secParams.getProbability(MainComplications.NEU, NPH);
		
		rr = new ComplicationRR[NPHTransitions.values().length];
		final ComplicationRR rrToNPH = new HbA1c10ReductionComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NPH.name()), REF_HBA1C); 
		rr[NPHTransitions.HEALTHY_NPH.ordinal()] = rrToNPH;
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

		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, NPH), "", "", P_DNC_NPH));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(NPH, ESRD), "", "", P_NPH_ESRD));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(MainComplications.NEU, MainComplications.NPH),	"",	"",	P_NEU_NPH));		
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + MainComplications.NPH.name(), 
				"%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.25, RandomVariateFactory.getInstance("NormalVariate", 0.25, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.19, 0.32}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NPH, "Cost of NPH", "", 2018, C_NPH, SecondOrderParamsRepository.getRandomVariateForCost(C_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ESRD, "Cost of ESRD", "", 2018, C_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(C_ESRD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NPH, "Transition cost to NPH", "", 2018, TC_NPH, SecondOrderParamsRepository.getRandomVariateForCost(TC_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ESRD, "Transition cost to ESRD", "", 2018, TC_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(TC_ESRD)));
		
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
