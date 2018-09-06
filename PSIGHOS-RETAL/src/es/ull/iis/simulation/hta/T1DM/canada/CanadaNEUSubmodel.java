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
public class CanadaNEUSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity NEU = new T1DMComorbidity("NEU", "Neuropathy", MainComplications.NEU);
	public static T1DMComorbidity LEA = new T1DMComorbidity("LEA", "Low extremity amputation", MainComplications.NEU);
	public static T1DMComorbidity[] NEUSubstates = new T1DMComorbidity[] {NEU, LEA};

//	addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
	private static final double REF_HBA1C = 9.1; 
	private static final double P_DNC_NEU = 0.0235;
	private static final double P_NEU_LEA = 0.12;
	private static final double RR_NEU = 0.624;
	private static final double C_NEU = 192;
	private static final double C_LEA = 6024;
	private static final double TC_NEU = 192 - C_NEU;
	private static final double TC_LEA = 43984 - C_LEA;

	private static final double DU_NEU = CanadaSecondOrderParams.U_DNC - 0.624;
	private static final double DU_LEA = CanadaSecondOrderParams.U_DNC - 0.534;

	public enum NEUTransitions {
		HEALTHY_NEU,
		NEU_LEA,
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [][] rnd;

	private final double[] costNEU;
	private final double[] costLEA;
	
	private final double duNEU;
	private final double duLEA;

	/**
	 * 
	 */
	public CanadaNEUSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[NEUTransitions.values().length];
		invProb[NEUTransitions.HEALTHY_NEU.ordinal()] = -1 / secParams.getProbability(NEU);
		invProb[NEUTransitions.NEU_LEA.ordinal()] = -1 / secParams.getProbability(NEU, LEA);

		rr = new ComplicationRR[NEUTransitions.values().length];
		rr[NEUTransitions.HEALTHY_NEU.ordinal()] = new HbA1c10ReductionComplicationRR(
				secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU.name()), REF_HBA1C);
		rr[NEUTransitions.NEU_LEA.ordinal()] = SecondOrderParamsRepository.NO_RR;

		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients][NEUSubstates.length];
		for (int i = 0; i < nPatients; i++) {
			for (int j = 0; j < NEUSubstates.length; j++) {
				rnd[i][j] = rng.draw();
			}
		}
		
		costNEU = secParams.getCostsForHealthState(NEU);
		costLEA = secParams.getCostsForHealthState(LEA);

		duNEU = secParams.getDisutilityForHealthState(NEU);
		duLEA = secParams.getDisutilityForHealthState(LEA);		
	}
	
	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, NEU), 
				"",	"", P_DNC_NEU));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(NEU, LEA), 
				"",	"",	P_NEU_LEA));

//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.NEU.name(), "Beta for confirmed clinical neuropathy", 
//		"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", 5.3));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU.name(), 
				"%risk reducion for combined groups for confirmed clinical neuropathy", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.3, RandomVariateFactory.getInstance("NormalVariate", 0.3, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.18, 0.40}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NEU, "Cost of NEU", "", 2018, C_NEU, SecondOrderParamsRepository.getRandomVariateForCost(C_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + LEA, "Cost of LEA", "", 2018, C_LEA, SecondOrderParamsRepository.getRandomVariateForCost(C_LEA)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NEU, "Transition cost to NEU", "", 2018, TC_NEU, SecondOrderParamsRepository.getRandomVariateForCost(TC_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + LEA, "Transition cost to LEA", "", 2018, TC_LEA, SecondOrderParamsRepository.getRandomVariateForCost(TC_LEA)));

		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NEU, "Disutility of NEU", "", DU_NEU));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + LEA, "Disutility of LEA", "", DU_LEA));
		
		secParams.registerComplication(MainComplications.NEU);
		secParams.registerHealthStates(NEUSubstates);
	}
	
	@Override
	public T1DMProgression getNextComplication(T1DMPatient pat) {
		final T1DMProgression prog = new T1DMProgression();
		if (enable) {
			final TreeSet<T1DMComorbidity> state = pat.getDetailedState();
			// Checks whether there is somewhere to transit to
			if (!state.contains(LEA)) {
				long timeToLEA = Long.MAX_VALUE;
				long timeToNEU = Long.MAX_VALUE;
				final long previousTimeToNEU = pat.getTimeToChronicComorbidity(NEU);
				final long previousTimeToLEA = pat.getTimeToChronicComorbidity(LEA);
				long limit = pat.getTimeToDeath();
				if (limit > previousTimeToLEA)
					limit = previousTimeToLEA;
				if (state.contains(NEU)) {
					// RR from NEU to LEA
					timeToLEA = getAnnualBasedTimeToEvent(pat, NEUTransitions.NEU_LEA, limit);
				}
				else {
					if (limit > previousTimeToNEU)
						limit = previousTimeToNEU;
					// RR from healthy to NEU (must be previous to LEA and a (potential) formerly scheduled NEU event)
					timeToNEU = getAnnualBasedTimeToEvent(pat, NEUTransitions.HEALTHY_NEU, limit);
				}
				// Check previously scheduled events
				if (timeToNEU != Long.MAX_VALUE) {
					if (previousTimeToNEU < Long.MAX_VALUE) {
						prog.addCancelEvent(NEU);
					}
					prog.addNewEvent(NEU, timeToNEU);
				}
				if (timeToLEA != Long.MAX_VALUE) {
					if (previousTimeToLEA < Long.MAX_VALUE) {
						prog.addCancelEvent(LEA);
					}
					prog.addNewEvent(LEA, timeToLEA);
					// If the new LEA event happens before a previously scheduled NEU event, the latter must be cancelled 
					if (previousTimeToNEU < Long.MAX_VALUE && timeToLEA < previousTimeToNEU)
						prog.addCancelEvent(NEU);
				}
			}
		}
		return prog;
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, NEUTransitions transition, long limit) {
		final int ord = NEUTransitions.HEALTHY_NEU.equals(transition) ? 0 : 1;
		return getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()][ord], rr[transition.ordinal()].getRR(pat), limit);
	}

	@Override
	public int getNSubstates() {
		return NEUSubstates.length;
	}

	@Override
	public T1DMComorbidity[] getSubstates() {
		return NEUSubstates;
	}

	@Override
	public TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat) {
		return new TreeSet<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(LEA))
			return costLEA[0];
		return costNEU[0];
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		if (LEA.equals(newEvent))
			return costLEA[1];
		return costNEU[1];
	}

	@Override
	public double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(LEA))
			return duLEA;
		return duNEU;
	}
}
