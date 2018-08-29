/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
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
public class SimpleNEUSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity NEU = new T1DMComorbidity("NEU", "Neuropathy", MainComplications.NEU);
	public static T1DMComorbidity LEA = new T1DMComorbidity("LEA", "Low extremity amputation", MainComplications.NEU);
	public static T1DMComorbidity[] NEUSubstates = new T1DMComorbidity[] {NEU, LEA};
	
	private static final double REF_HBA1C = 9.1; 
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double[] CI_NEU_LEA = {0.01232, 0.01848};
	private static final double C_NEU = 3108.86;
	private static final double C_LEA = 9305.74;
	private static final double TC_NEU = 0.0;
	private static final double TC_LEA = 11966.18;
	private static final double DU_NEU = BasicConfigParams.USE_REVIEW_UTILITIES ? 0.084 : 0.0244;
	private static final double DU_LEA = BasicConfigParams.USE_REVIEW_UTILITIES ? 0.28 : (0.0379 + 0.0244) / 2;

	public enum NEUTransitions {
		HEALTHY_NEU,
		NEU_LEA,
		HEALTHY_LEA;
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [][] rnd;

	/**
	 * 
	 */
	public SimpleNEUSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[NEUTransitions.values().length];
		invProb[NEUTransitions.HEALTHY_NEU.ordinal()] = -1 / secParams.getProbability(SimpleNEUSubmodel.NEU);
		invProb[NEUTransitions.HEALTHY_LEA.ordinal()] = -1 / secParams.getProbability(SimpleNEUSubmodel.LEA);
		invProb[NEUTransitions.NEU_LEA.ordinal()] = -1 / secParams.getProbability(SimpleNEUSubmodel.NEU, SimpleNEUSubmodel.LEA);

		rr = new ComplicationRR[NEUTransitions.values().length];
		rr[NEUTransitions.HEALTHY_NEU.ordinal()] = new HbA1c10ReductionComplicationRR(
				secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + SimpleNEUSubmodel.NEU.name()), REF_HBA1C);
		rr[NEUTransitions.HEALTHY_LEA.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[NEUTransitions.NEU_LEA.ordinal()] = SecondOrderParamsRepository.NO_RR;
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients][NEUSubstates.length];
		for (int i = 0; i < nPatients; i++) {
			for (int j = 0; j < NEUSubstates.length; j++) {
				rnd[i][j] = rng.draw();
			}
		}
	}
	
	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double coefHbA1c = REF_HBA1C/10;
		// From Sheffield
		final double pDNC_NEU = 0.0354 * Math.pow(coefHbA1c, 5.3);
		final double[] paramsNEU_LEA = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_LEA, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_LEA));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, NEU), 
				"Probability of healthy to clinically confirmed neuropathy, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", pDNC_NEU));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, LEA), 
				"Probability of healthy to PAD with amputation, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 0.0003));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(NEU, LEA), 
				"Probability of clinically confirmed neuropathy to PAD with amputation", 
				"Klein et al. 2004 (also Sheffield)", 
				P_NEU_LEA, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1])));

//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.NEU.name(), "Beta for confirmed clinical neuropathy", 
//		"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", 5.3));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU.name(), 
				"%risk reducion for combined groups for confirmed clinical neuropathy", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.3, RandomVariateFactory.getInstance("NormalVariate", 0.3, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.18, 0.40}))));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + NEU.name(), 
				"Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.51, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.51, 1.00, 2.28, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + LEA.name(), 
				"Increased mortality risk due to peripheral neuropathy (amputation)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				3.98, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.98, 1.84, 8.59, 1)));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NEU, "Cost of NEU", "", 2015, C_NEU, SecondOrderParamsRepository.getRandomVariateForCost(C_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + LEA, "Cost of LEA", "", 2015, C_LEA, SecondOrderParamsRepository.getRandomVariateForCost(C_LEA)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NEU, "Transition cost to NEU", "", 2015, TC_NEU, SecondOrderParamsRepository.getRandomVariateForCost(TC_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + LEA, "Transition cost to LEA", "", 2015, TC_LEA, SecondOrderParamsRepository.getRandomVariateForCost(TC_LEA)));

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
					// RR from healthy to LEA
					timeToLEA = getAnnualBasedTimeToEvent(pat, NEUTransitions.HEALTHY_LEA, limit);
					if (limit > timeToLEA)
						limit = timeToLEA;
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
}
