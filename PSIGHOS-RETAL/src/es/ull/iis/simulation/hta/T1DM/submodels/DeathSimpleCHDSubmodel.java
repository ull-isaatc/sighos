/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.T1DMChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.RRCalculator;
import es.ull.iis.simulation.hta.T1DM.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import simkit.random.DiscreteSelectorVariate;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DeathSimpleCHDSubmodel extends ChronicComplicationSubmodel {
	public static T1DMComplicationStage ANGINA = new T1DMComplicationStage("ANGINA", "Angina", T1DMChronicComplications.CHD);
	public static T1DMComplicationStage STROKE = new T1DMComplicationStage("STROKE", "Stroke", T1DMChronicComplications.CHD);
	public static T1DMComplicationStage MI = new T1DMComplicationStage("MI", "Myocardial Infarction", T1DMChronicComplications.CHD);
	public static T1DMComplicationStage HF = new T1DMComplicationStage("HF", "Heart Failure", T1DMChronicComplications.CHD);
	public static T1DMComplicationStage[] CHDSubstates = new T1DMComplicationStage[] {ANGINA, STROKE, MI, HF}; 

	private static final String STR_DEATH_MI_MAN = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + "Men_" + MI.name();
	private static final String STR_DEATH_MI_WOMAN = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + "Women_" + MI.name();
	private static final String STR_DEATH_STROKE = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + STROKE.name();
	
	private static final double REF_HBA1C = 9.1; 
	private static final double P_DNC_CHD = 0.0045;
	private static final double P_NEU_CHD = 0.02;
	private static final double P_NPH_CHD = 0.0224;
	private static final double P_RET_CHD = 0.0155;
	private static final double[] CI_DNC_CHD = {0.001, 0.0084};
	private static final double[] CI_NEU_CHD = {0.016, 0.044};
	private static final double[] CI_NPH_CHD = {0.013, 0.034};
	private static final double[] CI_RET_CHD = {0.01, 0.043};
	// Utility (avg, SD) from either Bagust and Beale; or Sullivan
	private static final double[] DU_ANGINA = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.09, (0.126 - 0.054) / 3.92} : new double[]{0.0412, 0.0002};
	// Utility (avg, SD) from either Clarke et al.; or Mar et al. In the latter, the SD has been manually adjusted to generate utilities between 0.4 and 0.7
	private static final double[] DU_STROKE = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.164, (0.222 - 0.105) / 3.92} : new double[]{(BasicConfigParams.DEF_U_GENERAL_POP - (0.4013+0.736)/2), 0.045};
	// Utility (avg, SD) from either Clarke et al; or Sullivan
	private static final double[] DU_MI = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.055, (0.067 - 0.042) / 3.92} : new double[]{0.0409, 0.0002};
	// Utility (avg, SD) from either Clarke et al.; or Sullivan (assumed equal to MI)
	private static final double[] DU_HF = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.108, (0.169 - 0.048) / 3.92} : new double[]{0.0409, 0.0002};
	
	/** Probability of sudden death after MI for {men, women} */ 
	private static final double[] P_DEATH_MI = {0.393, 0.364};
	/** Probability of 30-day death after stroke */ 
	private static final double P_DEATH_STROKE = 0.124;
	
	public enum CHDTransitions {
		HEALTHY_CHD,
		NPH_CHD,
		RET_CHD,
		NEU_CHD		
	}
	private final double[] invProb;
	private final RRCalculator[] rr;
	/** Random value for predicting time to event */
	private final double [] rnd;
	/** Random value for predicting CHD-related death */ 
	private final double [] rndDeath;
	private final DiscreteSelectorVariate pCHDComplication;
	private final double[] costMI;
	private final double[] costHF;
	private final double[] costStroke;
	private final double[] costAngina;

	private final double[]pDeathMI;
	private final double pDeathStroke;
	private final double duMI;
	private final double duHF;
	private final double duStroke;
	private final double duAngina;
	/**
	 * 
	 */
	public DeathSimpleCHDSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[CHDTransitions.values().length];
		invProb[CHDTransitions.HEALTHY_CHD.ordinal()] = -1 / secParams.getProbability(T1DMChronicComplications.CHD);
		invProb[CHDTransitions.NEU_CHD.ordinal()] = -1 / secParams.getProbability(T1DMChronicComplications.NEU, T1DMChronicComplications.CHD);
		invProb[CHDTransitions.NPH_CHD.ordinal()] = -1 / secParams.getProbability(T1DMChronicComplications.NPH, T1DMChronicComplications.CHD);
		invProb[CHDTransitions.RET_CHD.ordinal()] = -1 / secParams.getProbability(T1DMChronicComplications.RET, T1DMChronicComplications.CHD);
		this.rr = new RRCalculator[CHDTransitions.values().length];
		final RRCalculator rrToCHD = new HbA1c1PPComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + T1DMChronicComplications.CHD.name()), REF_HBA1C);
		rr[CHDTransitions.HEALTHY_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.NEU_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.NPH_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.RET_CHD.ordinal()] = rrToCHD;
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients];
		rndDeath = new double[nPatients];
		
		for (int i = 0; i < nPatients; i++) {
			rnd[i] = rng.draw();
			rndDeath[i] = rng.draw();
		}
		this.pCHDComplication = getRandomVariateForCHDComplications(secParams);
		
		costAngina = secParams.getCostsForChronicComplication(ANGINA);
		costStroke = secParams.getCostsForChronicComplication(STROKE);
		costMI = secParams.getCostsForChronicComplication(MI);
		costHF = secParams.getCostsForChronicComplication(HF);		

		duAngina = secParams.getDisutilityForChronicComplication(ANGINA);
		duStroke = secParams.getDisutilityForChronicComplication(STROKE);
		duMI = secParams.getDisutilityForChronicComplication(MI);
		duHF = secParams.getDisutilityForChronicComplication(HF);		
		
		pDeathMI = new double[] {secParams.getOtherParam(STR_DEATH_MI_MAN), secParams.getOtherParam(STR_DEATH_MI_WOMAN)};
		pDeathStroke = secParams.getOtherParam(STR_DEATH_STROKE);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_CHD));
		final double[] paramsNEU_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_CHD));
		final double[] paramsNPH_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_NPH_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_NPH_CHD));
		final double[] paramsRET_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_RET_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_RET_CHD));		

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, T1DMChronicComplications.CHD), "Probability of healthy to CHD", 
				"", P_DNC_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(T1DMChronicComplications.NEU, T1DMChronicComplications.CHD), "", 
				"", P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(T1DMChronicComplications.NPH, T1DMChronicComplications.CHD), "", 
				"", P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(T1DMChronicComplications.RET, T1DMChronicComplications.CHD), "", 
				"", P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + T1DMChronicComplications.CHD.name(), 
				SecondOrderParamsRepository.STR_RR_PREFIX + T1DMChronicComplications.CHD.name(), 
				"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 
				1.15, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.15, 0.92, 1.43, 1)));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + MI.name(), 
				"Probability of a CHD complication being Myocardial Infarction", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				0.53, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.53)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + STROKE.name(), 
				"Probability of a CHD complication being Stroke", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				0.07, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.07)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + ANGINA.name(), 
				"Probability of a CHD complication being Angina", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				0.28, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.28)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + HF.name(), 
				"Probability of a CHD complication being Heart Failure", 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				0.12, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.12)));

		secParams.addOtherParam(new SecondOrderParam(STR_DEATH_MI_MAN, 
				"Probability of sudden death after MI for men",	"Core Model", 
				P_DEATH_MI[BasicConfigParams.MAN]));
		secParams.addOtherParam(new SecondOrderParam(STR_DEATH_MI_WOMAN, 
				"Probability of sudden death after MI for women", "Core Model", 
				P_DEATH_MI[BasicConfigParams.WOMAN]));
		secParams.addOtherParam(new SecondOrderParam(STR_DEATH_STROKE, 
				"Probability of death after stroke", "Core Model", 
				P_DEATH_STROKE));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + T1DMChronicComplications.CHD.name(), 
				"Increased mortality risk due to macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1)));
		
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + MI, 
				"Cost of year 2+ Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 948, SecondOrderParamsRepository.getRandomVariateForCost(948)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ANGINA, 
				"Cost of year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 532.01, SecondOrderParamsRepository.getRandomVariateForCost(532.01)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + STROKE, 
				"Cost of year 2+ of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 2485.66, SecondOrderParamsRepository.getRandomVariateForCost(2485.66)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + HF, 
				"Cost of year 2+ of Heart Failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 1054.42, SecondOrderParamsRepository.getRandomVariateForCost(1054.42)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + MI, 
				"Cost of episode of Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 23536-948, SecondOrderParamsRepository.getRandomVariateForCost(23536-948)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ANGINA, 
				"Cost of episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 2517.97-532.01, SecondOrderParamsRepository.getRandomVariateForCost(2517.97-532.01)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + STROKE, 
				"Cost of episode of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 6120.32-2485.66, SecondOrderParamsRepository.getRandomVariateForCost(6120.32-2485.66)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + HF, 
				"Cost of episode of Heart Failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 5557.66-1054.42, SecondOrderParamsRepository.getRandomVariateForCost(5557.66-1054.42)));

		final double[] paramsDuANGINA = SecondOrderParamsRepository.betaParametersFromNormal(DU_ANGINA[0], DU_ANGINA[1]);
		final double[] paramsDuMI = SecondOrderParamsRepository.betaParametersFromNormal(DU_MI[0], DU_MI[1]);
		final double[] paramsDuSTROKE = SecondOrderParamsRepository.betaParametersFromNormal(DU_STROKE[0], DU_STROKE[1]);
		final double[] paramsDuHF = SecondOrderParamsRepository.betaParametersFromNormal(DU_HF[0], DU_HF[1]);		
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + DeathSimpleCHDSubmodel.ANGINA, 
				"Disutility of angina", 
				"", DU_ANGINA[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuANGINA[0], paramsDuANGINA[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + DeathSimpleCHDSubmodel.MI, 
				"Disutility of MI", 
				"", DU_MI[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuMI[0], paramsDuMI[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + DeathSimpleCHDSubmodel.HF, 
				"Disutility of heart failure", 
				"", DU_HF[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuHF[0], paramsDuHF[1])));				
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + DeathSimpleCHDSubmodel.STROKE, 
				"Disutility of stroke. Average of autonomous and dependant stroke disutilities", 
				"", DU_STROKE[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuSTROKE[0], paramsDuSTROKE[1])));

		secParams.registerComplication(T1DMChronicComplications.CHD);
		secParams.registerComplicationStages(CHDSubstates);
	}

	public DiscreteSelectorVariate getRandomVariateForCHDComplications(SecondOrderParamsRepository secParams) {
		final double [] coef = new double[CHDSubstates.length];
		for (int i = 0; i < CHDSubstates.length; i++) {
			final T1DMComplicationStage comp = CHDSubstates[i];
			coef[i] = secParams.getOtherParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + comp.name());
		}
		return (DiscreteSelectorVariate)RandomVariateFactory.getInstance("DiscreteSelectorVariate", coef);
	}

	
	@Override
	public T1DMProgression getProgression(T1DMPatient pat) {
		final T1DMProgression prog = new T1DMProgression();
		if (enable) {
			// If already has CHD, then nothing else to progress to
			if (!pat.hasComplication(T1DMChronicComplications.CHD)) {
				long timeToCHD = pat.getTimeToDeath();
				if (pat.hasComplication(T1DMChronicComplications.NEU)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NEU_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				if (pat.hasComplication(T1DMChronicComplications.NPH)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NPH_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				if (pat.hasComplication(T1DMChronicComplications.RET)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.RET_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.HEALTHY_CHD);
				if (newTimeToCHD < timeToCHD)
					timeToCHD = newTimeToCHD;
				if (timeToCHD < pat.getTimeToDeath()) {
					// First try with previously scheduled events
					boolean foundPrevious = false;
					for (int i = 0; i < CHDSubstates.length && !foundPrevious; i++) {
						final T1DMComplicationStage stCHD = CHDSubstates[i];
						final long previousTime = pat.getTimeToChronicComorbidity(stCHD);
						// Found a previous event with lower or equal timestamp --> Do nothing
						if (previousTime <= timeToCHD) {
							foundPrevious = true;
						}
						// Found a previous VALID timestamp > new time --> modify the event 
						else if (previousTime < Long.MAX_VALUE) {
							prog.addCancelEvent(stCHD);
						}
					}
					if (!foundPrevious) {
						// Choose CHD substate
						final T1DMComplicationStage stCHD = CHDSubstates[pCHDComplication.generateInt()];
						// If the complication is MI 
						if (MI.equals(stCHD)) {
							prog.addNewEvent(stCHD, timeToCHD, (rndDeath[pat.getIdentifier()] <= pDeathMI[pat.getSex()]));
						}
						else if (STROKE.equals(stCHD)) {
							prog.addNewEvent(stCHD, timeToCHD, (rndDeath[pat.getIdentifier()] <= pDeathStroke));							
						}
						else {
							prog.addNewEvent(stCHD, timeToCHD);														
						}
					}
				}
			}
			
		}
		return prog;
	}

	@Override
	public int getNStages() {
		return CHDSubstates.length;
	}

	@Override
	public T1DMComplicationStage[] getStages() {
		return CHDSubstates;
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, CHDTransitions transition) {
		return CommonParams.getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()], rr[transition.ordinal()].getRR(pat));
	}

	@Override
	public TreeSet<T1DMComplicationStage> getInitialStage(T1DMPatient pat) {
		return new TreeSet<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		final Collection<T1DMComplicationStage> state = pat.getDetailedState();

		if (state.contains(ANGINA))
			return costAngina[0];
		else if (state.contains(STROKE))
			return costStroke[0];
		else if (state.contains(HF))
			return costHF[0];
		else if (state.contains(MI))
			return costMI[0];				
		return 0.0;
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComplicationStage newEvent) {
		if (HF.equals(newEvent))
			return costHF[1];
		if (MI.equals(newEvent))
			return costMI[1];
		if (STROKE.equals(newEvent))
			return costStroke[1];
		if (ANGINA.equals(newEvent))
			return costAngina[1];
		return 0.0;
	}

	@Override
	public double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method) {
		final Collection<T1DMComplicationStage> state = pat.getDetailedState();

		if (state.contains(ANGINA))
			return duAngina;
		else if (state.contains(STROKE))
			return duStroke;
		else if (state.contains(HF))
			return duHF;
		else if (state.contains(MI))
			return duMI;				
		return 0.0;
	}

}
