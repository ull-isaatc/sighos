/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Collection;
import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleCHDSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage ANGINA = new DiabetesComplicationStage("ANGINA", "Angina", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage STROKE = new DiabetesComplicationStage("STROKE", "Stroke", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage MI = new DiabetesComplicationStage("MI", "Myocardial Infarction", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage HF = new DiabetesComplicationStage("HF", "Heart Failure", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage[] CHDSubstates = new DiabetesComplicationStage[] {ANGINA, STROKE, MI, HF}; 

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

	/**
	 * 
	 */
	public SimpleCHDSubmodel() {
		super(DiabetesChronicComplications.CHD, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_CHD));
		final double[] paramsNEU_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_NEU_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_NEU_CHD));
		final double[] paramsNPH_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_NPH_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_NPH_CHD));
		final double[] paramsRET_CHD = SecondOrderParamsRepository.betaParametersFromNormal(P_RET_CHD, SecondOrderParamsRepository.sdFrom95CI(CI_RET_CHD));		

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, DiabetesChronicComplications.CHD), "Probability of healthy to CHD", 
				"", P_DNC_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.NEU, DiabetesChronicComplications.CHD), "", 
				"", P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.NPH, DiabetesChronicComplications.CHD), "", 
				"", P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.RET, DiabetesChronicComplications.CHD), "", 
				"", P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.CHD.name(), 
				SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.CHD.name(), 
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

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + DiabetesChronicComplications.CHD.name(), 
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
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.ANGINA, 
				"Disutility of angina", 
				"", DU_ANGINA[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuANGINA[0], paramsDuANGINA[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.MI, 
				"Disutility of MI", 
				"", DU_MI[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuMI[0], paramsDuMI[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.HF, 
				"Disutility of heart failure", 
				"", DU_HF[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuHF[0], paramsDuHF[1])));				
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + SimpleCHDSubmodel.STROKE, 
				"Disutility of stroke. Average of autonomous and dependant stroke disutilities", 
				"", DU_STROKE[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuSTROKE[0], paramsDuSTROKE[1])));

		addSecondOrderInitProportion(secParams);
	}

	public CHDComplicationSelector getRandomVariateForCHDComplications(SecondOrderParamsRepository secParams) {
		final double [] coef = new double[CHDSubstates.length];
		for (int i = 0; i < CHDSubstates.length; i++) {
			final DiabetesComplicationStage comp = CHDSubstates[i];
			coef[i] = secParams.getOtherParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + comp.name());
		}
		return new CHDComplicationSelector(coef);
	}

	@Override
	public int getNStages() {
		return CHDSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return CHDSubstates;
	}

	@Override
	public int getNTransitions() {
		return CHDTransitions.values().length;
	}

	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}

	public class Instance extends ChronicComplicationSubmodel {
		/** Random value for predicting time to event [0] and type of event [1]*/
		private final double [][] rnd;
		/** Random value for predicting CHD-related death */ 
		private final double [] rndDeath;
		private final CHDComplicationSelector pCHDComplication;

		private final double[]pDeathMI;
		private final double pDeathStroke;
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(SimpleCHDSubmodel.this);
			
			final RRCalculator rrToCHD = new HbA1c1PPComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.CHD.name()), REF_HBA1C);
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
			
			
			rnd = new double[nPatients][2];
			rndDeath = new double[nPatients];
			
			for (int i = 0; i < nPatients; i++) {
				rnd[i][0] = rng.draw();
				rnd[i][1] = rng.draw();
				rndDeath[i] = rng.draw();
			}
			this.pCHDComplication = getRandomVariateForCHDComplications(secParams);

			addTime2Event(CHDTransitions.HEALTHY_CHD.ordinal(), 
				new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
				secParams.getProbability(DiabetesChronicComplications.CHD), rrToCHD));
			addTime2Event(CHDTransitions.NEU_CHD.ordinal(), 
				new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
				secParams.getProbability(DiabetesChronicComplications.NEU, DiabetesChronicComplications.CHD), rrToCHD));
			addTime2Event(CHDTransitions.NPH_CHD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(DiabetesChronicComplications.NPH, DiabetesChronicComplications.CHD), rrToCHD));
			addTime2Event(CHDTransitions.RET_CHD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(DiabetesChronicComplications.RET, DiabetesChronicComplications.CHD), rrToCHD));

			addData(secParams, ANGINA);
			addData(secParams, STROKE);
			addData(secParams, MI);
			addData(secParams, HF);
			
			pDeathMI = new double[] {secParams.getOtherParam(STR_DEATH_MI_MAN), secParams.getOtherParam(STR_DEATH_MI_WOMAN)};
			pDeathStroke = secParams.getOtherParam(STR_DEATH_STROKE);
		}

		
		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				// If already has CHD, then nothing else to progress to
				if (!pat.hasComplication(DiabetesChronicComplications.CHD)) {
					long timeToCHD = pat.getTimeToDeath();
					if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
						final long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.NEU_CHD.ordinal(), timeToCHD);
						if (newTimeToCHD < timeToCHD)
							timeToCHD = newTimeToCHD;
					}
					if (pat.hasComplication(DiabetesChronicComplications.NPH)) {
						final long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.NPH_CHD.ordinal(), timeToCHD);
						if (newTimeToCHD < timeToCHD)
							timeToCHD = newTimeToCHD;
					}
					if (pat.hasComplication(DiabetesChronicComplications.RET)) {
						final long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.RET_CHD.ordinal(), timeToCHD);
						if (newTimeToCHD < timeToCHD)
							timeToCHD = newTimeToCHD;
					}
					long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.HEALTHY_CHD.ordinal(), timeToCHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
					if (timeToCHD < pat.getTimeToDeath()) {
						// First try with previously scheduled events
						boolean foundPrevious = false;
						for (int i = 0; i < CHDSubstates.length && !foundPrevious; i++) {
							final DiabetesComplicationStage stCHD = CHDSubstates[i];
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
							final int id = pat.getIdentifier();
							// Choose CHD substate
							final DiabetesComplicationStage stCHD = CHDSubstates[pCHDComplication.generate(rnd[id][1])];
							if (BasicConfigParams.USE_CHD_DEATH_MODEL) {
								if (MI.equals(stCHD)) {
									prog.addNewEvent(stCHD, timeToCHD, (rndDeath[id] <= pDeathMI[pat.getSex()]));
								}
								else if (STROKE.equals(stCHD)) {
									prog.addNewEvent(stCHD, timeToCHD, (rndDeath[id] <= pDeathStroke));							
								}
								else {
									prog.addNewEvent(stCHD, timeToCHD);														
								}
							}
							else {
								// No deaths
								prog.addNewEvent(stCHD, timeToCHD);														
							}
						}
					}
				}
				
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();

			if (state.contains(ANGINA))
				return getData(ANGINA).getCosts()[0];
			else if (state.contains(STROKE))
				return getData(STROKE).getCosts()[0];
			else if (state.contains(HF))
				return getData(HF).getCosts()[0];
			else if (state.contains(MI))
				return getData(MI).getCosts()[0];				
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();

			if (state.contains(ANGINA))
				return getData(ANGINA).getDisutility();
			else if (state.contains(STROKE))
				return getData(STROKE).getDisutility();
			else if (state.contains(HF))
				return getData(HF).getDisutility();
			else if (state.contains(MI))
				return getData(MI).getDisutility();				
			return 0.0;
		}
	}

	/**
	 * A class to select among different options. Returns the index of the option selected according to a set of initial frequencies.
	 * Adapted from "simkit.random.DiscreteIntegerVariate" (https://github.com/kastork/simkit-mirror/blob/master/src/simkit/random/DiscreteIntegerVariate.java)
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private final class CHDComplicationSelector {
		private final double[] frequencies;
		private final double[] cdf;
		/**
		 * 
		 */
		public CHDComplicationSelector(double[] frequencies) {
	        this.frequencies = frequencies;
	        this.normalize();
	        cdf = new double[frequencies.length];
	        cdf[0] = frequencies[0];
	        for (int i = 1; i < frequencies.length; i++) {
	                cdf[i] += cdf[i - 1] + frequencies[i];
	        }
		}

		public int generate(double uniform) {
			int index;
			for (index = 0; (uniform > cdf[index]) && (index < cdf.length - 1); index++) ;
			return index;
		}

	    private void normalize() {
	        double sum = 0.0;
	        for (int i = 0; i < frequencies.length; ++i) {
	            if (frequencies[i] < 0.0) {
	                throw new IllegalArgumentException(
	                        String.format("Bad frequency value at index %d (value = %.3f)", i, frequencies[i]));
	            }
	            sum += frequencies[i];
	        }
	        if (sum > 0.0) {
	            for (int i = 0; i < frequencies.length; ++i) {
	                frequencies[i] /= sum;
	            }
	        } else {
	            throw new IllegalArgumentException(
	                    String.format("Frequency sum not positive: %.3f", sum));
	        }
	    }
	}

}
