/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
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
public class SimpleCHDSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity ANGINA = new T1DMComorbidity("ANGINA", "Angina", MainComplications.CHD);
	public static T1DMComorbidity STROKE = new T1DMComorbidity("STROKE", "Stroke", MainComplications.CHD);
	public static T1DMComorbidity MI = new T1DMComorbidity("MI", "Myocardial Infarction", MainComplications.CHD);
	public static T1DMComorbidity HF = new T1DMComorbidity("HF", "Heart Failure", MainComplications.CHD);
	public static T1DMComorbidity[] CHDSubstates = new T1DMComorbidity[] {ANGINA, STROKE, MI, HF}; 
	
	private static final double REF_HBA1C = 9.1; 
	private static final double P_DNC_CHD = 0.0045;
	private static final double P_NEU_CHD = 0.02;
	private static final double P_NPH_CHD = 0.0224;
	private static final double P_RET_CHD = 0.0155;
	private static final double[] CI_DNC_CHD = {0.001, 0.0084};
	private static final double[] CI_NEU_CHD = {0.016, 0.044};
	private static final double[] CI_NPH_CHD = {0.013, 0.034};
	private static final double[] CI_RET_CHD = {0.01, 0.043};
//	private static final double P_DNC_CHD = 0.031;
//	private static final double P_NEU_CHD = 0.029;
//	private static final double P_NPH_CHD = 0.022;
//	private static final double P_RET_CHD = 0.028;
//	private static final double[] CI_DNC_CHD = {0.018, 0.048};
//	private static final double[] CI_NEU_CHD = {0.016, 0.044};
//	private static final double[] CI_NPH_CHD = {0.013, 0.034};
//	private static final double[] CI_RET_CHD = {0.016, 0.043};
	private static final double U_GENERAL_POP = 0.911400915;
	
	public enum CHDTransitions {
		HEALTHY_CHD,
		NPH_CHD,
		RET_CHD,
		NEU_CHD		
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [] rnd;
	private final DiscreteSelectorVariate pCHDComplication;
	private final double[] costMI;
	private final double[] costHF;
	private final double[] costStroke;
	private final double[] costAngina;

	private final double duMI;
	private final double duHF;
	private final double duStroke;
	private final double duAngina;
	/**
	 * 
	 */
	public SimpleCHDSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[CHDTransitions.values().length];
		invProb[CHDTransitions.HEALTHY_CHD.ordinal()] = -1 / secParams.getProbability(MainComplications.CHD);
		invProb[CHDTransitions.NEU_CHD.ordinal()] = -1 / secParams.getProbability(MainComplications.NEU, MainComplications.CHD);
		invProb[CHDTransitions.NPH_CHD.ordinal()] = -1 / secParams.getProbability(MainComplications.NPH, MainComplications.CHD);
		invProb[CHDTransitions.RET_CHD.ordinal()] = -1 / secParams.getProbability(MainComplications.RET, MainComplications.CHD);
		this.rr = new ComplicationRR[CHDTransitions.values().length];
		final ComplicationRR rrToCHD = new HbA1c1PPComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + MainComplications.CHD.name()), REF_HBA1C);
		rr[CHDTransitions.HEALTHY_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.NEU_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.NPH_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.RET_CHD.ordinal()] = rrToCHD;
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients];
		for (int i = 0; i < nPatients; i++) {
			rnd[i] = rng.draw();
		}
		this.pCHDComplication = getRandomVariateForCHDComplications(secParams);
		
		costAngina = secParams.getCostsForHealthState(ANGINA);
		costStroke = secParams.getCostsForHealthState(STROKE);
		costMI = secParams.getCostsForHealthState(MI);
		costHF = secParams.getCostsForHealthState(HF);		

		duAngina = secParams.getDisutilityForHealthState(ANGINA);
		duStroke = secParams.getDisutilityForHealthState(STROKE);
		duMI = secParams.getDisutilityForHealthState(MI);
		duHF = secParams.getDisutilityForHealthState(HF);		
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_CHD));
		final double[] paramsNEU_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_CHD));
		final double[] paramsNPH_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_NPH_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_NPH_CHD));
		final double[] paramsRET_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_RET_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_RET_CHD));		

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, MainComplications.CHD), "Probability of healthy to CHD", 
				"", P_DNC_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(MainComplications.NEU, MainComplications.CHD), "", 
				"", P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(MainComplications.NPH, MainComplications.CHD), "", 
				"", P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(MainComplications.RET, MainComplications.CHD), "", 
				"", P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + MainComplications.CHD.name(), 
				SecondOrderParamsRepository.STR_RR_PREFIX + MainComplications.CHD.name(), 
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

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + MainComplications.CHD.name(), 
				"Increased mortality risk due to macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1)));
		
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + MI, "Cost of year 2+ Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 948, SecondOrderParamsRepository.getRandomVariateForCost(948)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ANGINA, "Cost of year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 532.01, SecondOrderParamsRepository.getRandomVariateForCost(532.01)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + STROKE, "Cost of year 2+ of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 2485.66, SecondOrderParamsRepository.getRandomVariateForCost(2485.66)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + HF, "Cost of year 2+ of Heart Failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 1054.42, SecondOrderParamsRepository.getRandomVariateForCost(1054.42)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + MI, "Cost of episode of Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 23536-948, SecondOrderParamsRepository.getRandomVariateForCost(23536-948)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ANGINA, "Cost of episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 2517.97-532.01, SecondOrderParamsRepository.getRandomVariateForCost(2517.97-532.01)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + STROKE, "Cost of episode of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 6120.32-2485.66, SecondOrderParamsRepository.getRandomVariateForCost(6120.32-2485.66)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + HF, "Cost of episode of Heart Failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 5557.66-1054.42, SecondOrderParamsRepository.getRandomVariateForCost(5557.66-1054.42)));

		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.ANGINA, 
				"Disutility of angina", "Sullivan et al 2006", BasicConfigParams.USE_REVIEW_UTILITIES ? 0.09 : 0.0412));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.MI, 
				"Disutility of MI", "Sullivan et al 2006", BasicConfigParams.USE_REVIEW_UTILITIES ? 0.055 : 0.0409));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.HF, 
				"Disutility of heart failure", "Sullivan et al 2006", BasicConfigParams.USE_REVIEW_UTILITIES ? 0.108 : 0.0409)); // Assumed equal to MI
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.STROKE, 
				"Disutility of stroke. Average of autonomous and dependant stroke disutilities", 
				"Mar et al. 2010", BasicConfigParams.USE_REVIEW_UTILITIES ? 0.164 : (U_GENERAL_POP - (0.4013+0.736)/2)));

		secParams.registerComplication(MainComplications.CHD);
		secParams.registerHealthStates(CHDSubstates);
	}

	public DiscreteSelectorVariate getRandomVariateForCHDComplications(SecondOrderParamsRepository secParams) {
		final double [] coef = new double[CHDSubstates.length];
		for (int i = 0; i < CHDSubstates.length; i++) {
			final T1DMComorbidity comp = CHDSubstates[i];
			coef[i] = secParams.getOtherParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + comp.name());
		}
		return (DiscreteSelectorVariate)RandomVariateFactory.getInstance("DiscreteSelectorVariate", coef);
	}

	
	@Override
	public T1DMProgression getNextComplication(T1DMPatient pat) {
		final T1DMProgression prog = new T1DMProgression();
		if (enable) {
			// If already has CHD, then nothing else to progress to
			if (!pat.hasComplication(MainComplications.CHD)) {
				long timeToCHD = pat.getTimeToDeath();
				if (pat.hasComplication(MainComplications.NEU)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NEU_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				if (pat.hasComplication(MainComplications.NPH)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NPH_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				if (pat.hasComplication(MainComplications.RET)) {
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
						final T1DMComorbidity stCHD = CHDSubstates[i];
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
					if (!foundPrevious)
						prog.addNewEvent(CHDSubstates[pCHDComplication.generateInt()], timeToCHD);
				}
			}
			
		}
		return prog;
	}

	@Override
	public int getNSubstates() {
		return CHDSubstates.length;
	}

	@Override
	public T1DMComorbidity[] getSubstates() {
		return CHDSubstates;
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, CHDTransitions transition) {
		return CommonParams.getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()], rr[transition.ordinal()].getRR(pat));
	}

	@Override
	public TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat) {
		return new TreeSet<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();

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
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
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
		final Collection<T1DMComorbidity> state = pat.getDetailedState();

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
