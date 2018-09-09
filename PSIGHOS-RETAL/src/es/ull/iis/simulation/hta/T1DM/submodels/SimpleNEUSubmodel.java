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
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleNEUSubmodel extends ChronicComplicationSubmodel {
	public static T1DMComorbidity NEU = new T1DMComorbidity("NEU", "Neuropathy", MainChronicComplications.NEU);
	public static T1DMComorbidity LEA = new T1DMComorbidity("LEA", "Low extremity amputation", MainChronicComplications.NEU);
	public static T1DMComorbidity[] NEUSubstates = new T1DMComorbidity[] {NEU, LEA};
	
	private static final double P_DNC_NEU = 0.0354;
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double P_DNC_LEA = 0.0003;
	private static final double[] CI_DNC_NEU = {0.020, 0.055}; // McQueen
	private static final double[] LIMITS_DNC_LEA = {0.0, 0.0006}; // Assumption
	private static final double[] CI_NEU_LEA = {0.01232, 0.01848};
	private static final double BETA_NEU = 5.3;
	private static final double C_NEU = 3108.86;
	private static final double C_LEA = 918.01;
	private static final double TC_NEU = 0.0;
	/** [Avg, SD] cost of amputation, from Spanish national tariffs */
	private static final double[] TC_LEA = {11333.04, 1674.37};
	// Utility (avg, SD) from either Bagust and Beale; or Sullivan
	private static final double[] DU_NEU = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.084, (0.111 - 0.057) / 3.92} : new double[]{0.0244, 0.00012};
	// Utility (avg, SD) from either Clarke et al.; or Sullivan
	private static final double[] DU_LEA = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.28, (0.389 - 0.17) / 3.92} : new double[]{(0.0379 + 0.0244) / 2, 0.0002};

	public enum NEUTransitions {
		HEALTHY_NEU,
		NEU_LEA,
		HEALTHY_LEA;
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
	public SimpleNEUSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[NEUTransitions.values().length];
		invProb[NEUTransitions.HEALTHY_NEU.ordinal()] = -1 / secParams.getProbability(SimpleNEUSubmodel.NEU);
		invProb[NEUTransitions.HEALTHY_LEA.ordinal()] = -1 / secParams.getProbability(SimpleNEUSubmodel.LEA);
		invProb[NEUTransitions.NEU_LEA.ordinal()] = -1 / secParams.getProbability(SimpleNEUSubmodel.NEU, SimpleNEUSubmodel.LEA);

		rr = new ComplicationRR[NEUTransitions.values().length];
		rr[NEUTransitions.HEALTHY_NEU.ordinal()] = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU)); 
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
		
		costNEU = secParams.getCostsForHealthState(NEU);
		costLEA = secParams.getCostsForHealthState(LEA);

		duNEU = secParams.getDisutilityForHealthState(NEU);
		duLEA = secParams.getDisutilityForHealthState(LEA);
	}
	
	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		// From Sheffield
		final double[] paramsDNC_NEU = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_NEU, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_NEU));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, NEU), 
				"Probability of healthy to clinically confirmed neuropathy, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
				P_DNC_NEU, "BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, LEA), 
				"Probability of healthy to PAD with amputation, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
				P_DNC_LEA, "UniformVariate", LIMITS_DNC_LEA[0], LIMITS_DNC_LEA[1]));
		final double[] paramsNEU_LEA = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_LEA, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_LEA));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(NEU, LEA), 
				"Probability of clinically confirmed neuropathy to PAD with amputation", 
				"Klein et al. 2004 (also Sheffield)", 
				P_NEU_LEA, "BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1]));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU, "Beta for confirmed clinical neuropathy", 
		"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", BETA_NEU));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + NEU.name(), 
				"Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.51, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.51, 1.00, 2.28, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + LEA.name(), 
				"Increased mortality risk due to peripheral neuropathy (amputation)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				3.98, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.98, 1.84, 8.59, 1)));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NEU, "Cost of NEU", "", 2015, C_NEU, SecondOrderParamsRepository.getRandomVariateForCost(C_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + LEA, "Cost of LEA", "del Pino et al", 2017, C_LEA, SecondOrderParamsRepository.getRandomVariateForCost(C_LEA)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NEU, "Transition cost to NEU", "", 2015, TC_NEU, SecondOrderParamsRepository.getRandomVariateForCost(TC_NEU)));
		final double[] tcLEAParams = SecondOrderParamsRepository.gammaParametersFromNormal(TC_LEA[0], TC_LEA[1]); 
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + LEA, "Transition cost to LEA", 
				"Spanish tariffs: Cantabria; Cataluña; Madrid; Murcia; Navarra; País Vasco", 2017, 
				TC_LEA[0], RandomVariateFactory.getInstance("GammaVariate", tcLEAParams[0], tcLEAParams[1])));

		final double[] paramsDuNEU = SecondOrderParamsRepository.betaParametersFromNormal(DU_NEU[0], DU_NEU[1]);
		final double[] paramsDuLEA = SecondOrderParamsRepository.betaParametersFromNormal(DU_LEA[0], DU_LEA[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NEU, "Disutility of NEU", 
				"", DU_NEU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuNEU[0], paramsDuNEU[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + LEA, "Disutility of LEA", 
				"", DU_LEA[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuLEA[0], paramsDuLEA[1])));
		
		secParams.registerComplication(MainChronicComplications.NEU);
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
