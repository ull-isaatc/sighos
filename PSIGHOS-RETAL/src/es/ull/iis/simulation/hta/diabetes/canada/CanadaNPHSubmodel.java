/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaNPHSubmodel extends ChronicComplicationSubmodel {
	public static DiabetesComplicationStage NPH = new DiabetesComplicationStage("NPH", "Neuropathy", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ESRD = new DiabetesComplicationStage("ESRD", "End-Stage Renal Disease", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage[] NPHSubstates = new DiabetesComplicationStage[] {NPH, ESRD};

	private static final double P_DNC_NPH = 0.0094;
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_ESRD = 0.072;
	private static final double RR_NPH = 0.742;
	private static final double C_NPH = 13;
	private static final double C_ESRD = 12808;
	private static final double TC_NPH = 80 - C_NPH;
	private static final double TC_ESRD = 28221 - C_ESRD;
	private static final double DU_NPH = CanadaSecondOrderParams.U_DNC - 0.575;
	private static final double DU_ESRD = CanadaSecondOrderParams.U_DNC - 0.49;
	
//	addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
	private static final double REF_HBA1C = 9.1; 

	public enum NPHTransitions {
		HEALTHY_NPH,
		NPH_ESRD,
		NEU_NPH		
	}
	private final double[] invProb;
	private final RRCalculator[] rr;
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
		invProb[NPHTransitions.NEU_NPH.ordinal()] = -1 / secParams.getProbability(DiabetesChronicComplications.NEU, NPH);
		
		rr = new RRCalculator[NPHTransitions.values().length];
		final RRCalculator rrToNPH = new HbA1c10ReductionComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NPH.name()), REF_HBA1C); 
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
		
		costNPH = secParams.getCostsForChronicComplication(NPH);
		costESRD = secParams.getCostsForChronicComplication(ESRD);
		
		duNPH = secParams.getDisutilityForChronicComplication(NPH);
		duESRD = secParams.getDisutilityForChronicComplication(ESRD);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {

		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, NPH), "", "", P_DNC_NPH));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(NPH, ESRD), "", "", P_NPH_ESRD));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(DiabetesChronicComplications.NEU, DiabetesChronicComplications.NPH),	"",	"",	P_NEU_NPH));		
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.NPH.name(), 
				"%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.25, RandomVariateFactory.getInstance("NormalVariate", 0.25, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.19, 0.32}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NPH, "Cost of NPH", "", 2018, C_NPH, SecondOrderParamsRepository.getRandomVariateForCost(C_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ESRD, "Cost of ESRD", "", 2018, C_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(C_ESRD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NPH, "Transition cost to NPH", "", 2018, TC_NPH, SecondOrderParamsRepository.getRandomVariateForCost(TC_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ESRD, "Transition cost to ESRD", "", 2018, TC_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(TC_ESRD)));
		
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NPH, "Disutility of NPH", "", DU_NPH));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ESRD, "Disutility of ESRD", "", DU_ESRD));
		
		secParams.registerComplication(DiabetesChronicComplications.NPH);
		secParams.registerComplicationStages(NPHSubstates);
		
	}

	@Override
	public DiabetesProgression getProgression(DiabetesPatient pat) {
		final DiabetesProgression prog = new DiabetesProgression();
		if (enable) {
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
					timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.NPH_ESRD, limit);
				}
				else {
					if (limit > previousTimeToNPH)
						limit = previousTimeToNPH;
					// RR from healthy to NPH (must be previous to ESRD and a (potential) formerly scheduled NPH event)
					timeToNPH = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_NPH, limit);
					if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
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

	private long getAnnualBasedTimeToEvent(DiabetesPatient pat, NPHTransitions transition, long limit) {
		final int ord = (NPHTransitions.HEALTHY_NPH.equals(transition) || NPHTransitions.NEU_NPH.equals(transition)) ? 0 : 1;
		return getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()][ord], rr[transition.ordinal()].getRR(pat), limit);
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
	public TreeSet<DiabetesComplicationStage> getInitialStage(DiabetesPatient pat) {
		return new TreeSet<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
		final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
		if (state.contains(ESRD))
			return costESRD[0];
		return costNPH[0];
	}

	@Override
	public double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newEvent) {
		if (ESRD.equals(newEvent))
			return costESRD[1];
		return costNPH[1];
	}

	@Override
	public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
		final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
		if (state.contains(ESRD))
			return duESRD;
		return duNPH;
	}
}
