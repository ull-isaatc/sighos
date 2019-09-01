/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;

import javax.lang.model.type.UnionType;

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
import es.ull.iis.simulation.hta.diabetes.params.TimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.UniqueEventParam;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomIntegerSelector;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T2DMPrositCHDSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage POST_STROKE = new DiabetesComplicationStage("POST_STROKE", "Post stroke", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage POST_STROKE2 = new DiabetesComplicationStage("POST_REC_STROKE", "Post recurrent stroke", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage POST_ANGINA = new DiabetesComplicationStage("POST_ANGINA", "Post angina", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage POST_MI = new DiabetesComplicationStage("POST_MI", "Post myocardial Infarction", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage POST_MI2 = new DiabetesComplicationStage("POST_REC_MI", "Post recurrent myocardial Infarction", DiabetesChronicComplications.CHD);

	public static DiabetesComplicationStage[] CHDSubstates = new DiabetesComplicationStage[] {POST_STROKE, POST_STROKE2}; 
	// Constants for stroke
	private final static double K = 1.145;
	private final static double ONE_MINUS_K = 1 - K;
	private final static double LN_K = Math.log(K);

	// Constants for angina
	private final static double D = 1.078;
	private final static double ONE_MINUS_D = 1 - D;
	private final static double LN_D = Math.log(D);
	
	/** Base probability of recurrent stroke for male 16-69 years-old. From Policardo et al. 2015 10.1016/j.jdiacomp.2014.12.008 */
	private static final double P_STROKE_STROKE2 = 0.061877043;
	/** Parameters for beta distribution to represent base probability of recurrent stroke */
	private static final double[] BETA_STROKE_STROKE2 = {97.51822034, 1478.48178};
	
	/** RR (base value, and parameters for lognormal) of recurrent stroke for 1) female 16-69 years-old; 2) male >=70 years-old; 3) female >= 70 years-old.
	 * From Policardo et al. 2015 10.1016/j.jdiacomp.2014.12.008 */
	private static final double[][] RR_STROKE_STROKE2 = {
			{1.094362361, 0.090171875, 0.168134267},	
			{0.905767388, -0.09897275, 0.123724412},
			{0.787932958, -0.238342272, 0.129044101}
			}; 
	
	/** Probability of developing angina. Based on the combining rates for intensive and conservative arms of UKPDS, 
	 * as described in UKPDS 33. 10.1016/S0140-6736(98)07019-6 */ 
	private static final double P_ANGINA = 0.0067706;
	/** Parameters for beta distribution to represent probability of angina */
	private static final double [] BETA_ANGINA = {26.1750, 3839.8250};

	/** Probability of developing MI from angina. Based on the crude event rates (Table 4) for diabetic people with no previous CHD (but angina) 
	 * as described in Oasis 10.1161/01.CIR.102.9.1014 */ 
	private static final double P_ANGINA2MI = 0.0550132;
	/** Parameters for beta distribution to represent probability of MI from angina */
	private static final double [] BETA_ANGINA2MI = {31.2475, 536.7525};
	
	/** Probability of 30-day death after stroke */ 
	private static final double P_DEATH_STROKE = 0.124;
	
	/** Probability of sudden death after POST_MI for {men, women} */ 
	private static final double[] P_DEATH_MI = {0.393, 0.364};

	// Utility (avg, SD) from either Bagust and Beale; or Sullivan
	private static final double[] DU_ANGINA = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.09, (0.126 - 0.054) / 3.92} : new double[]{0.0412, 0.0002};
	// Utility (avg, SD) from either Clarke et al.; or Mar et al. In the latter, the SD has been manually adjusted to generate utilities between 0.4 and 0.7
	private static final double[] DU_STROKE = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.164, (0.222 - 0.105) / 3.92} : new double[]{(BasicConfigParams.DEF_U_GENERAL_POP - (0.4013+0.736)/2), 0.045};
	// Utility (avg, SD) from either Clarke et al; or Sullivan
	private static final double[] DU_MI = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.055, (0.067 - 0.042) / 3.92} : new double[]{0.0409, 0.0002};
	
	// Based on Prosit model
	public enum CHDTransitions {
		HEALTHY_STROKE, // First stroke
		STROKE_STROKE2,	// Recurrent stroke
		HEALTHY_ANGINA,	// Angina
		ANGINA_MI,		// Angina to myocardial infarction
		HEALTHY_MI,		// First myocardial infarction
		MI_MI2,			// Recurrent myocardial infarction
	}
	
	private static final String STR_DEATH_STROKE = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + POST_STROKE.name();
	private static final String STR_SOURCE_POLI = "Policardo et al. 2015 10.1016/j.jdiacomp.2014.12.008";
	private static final String STR_SOURCE_UKPDS33 = "UKPDS 33. 10.1016/S0140-6736(98)07019-6";
	private static final String STR_SOURCE_OASIS = "Oasis 10.1161/01.CIR.102.9.1014";
	
	private static final String STR_DEATH_MI_MAN = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + "Men_" + POST_MI.name();
	private static final String STR_DEATH_MI_WOMAN = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + "Women_" + POST_MI.name();

	/**
	 * 
	 */
	public T2DMPrositCHDSubmodel() {
		super(DiabetesChronicComplications.CHD, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(POST_STROKE, POST_STROKE2), "Probability of recurrent stroke", 
				STR_SOURCE_POLI, P_STROKE_STROKE2, "BetaVariate", BETA_STROKE_STROKE2[0], BETA_STROKE_STROKE2[1]));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + POST_STROKE + "_" + POST_STROKE2 + "_1", 
				"RR of recurrent stroke for female 16-69 years-old", 
				STR_SOURCE_POLI, 
				RR_STROKE_STROKE2[0][0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_STROKE_STROKE2[0][1], RR_STROKE_STROKE2[0][2]))));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + POST_STROKE + "_" + POST_STROKE2 + "_2", 
				"RR of recurrent stroke for male >=70 years-old", 
				STR_SOURCE_POLI, 
				RR_STROKE_STROKE2[1][0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_STROKE_STROKE2[1][1], RR_STROKE_STROKE2[1][2]))));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + POST_STROKE + "_" + POST_STROKE2 + "_3", 
				"RR of recurrent stroke for female >=70 years-old", 
				STR_SOURCE_POLI, 
				RR_STROKE_STROKE2[2][0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_STROKE_STROKE2[2][1], RR_STROKE_STROKE2[2][2]))));

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, POST_ANGINA), "Probability of healthy to angina", 
				STR_SOURCE_UKPDS33, P_ANGINA, RandomVariateFactory.getInstance("BetaVariate", BETA_ANGINA[0], BETA_ANGINA[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(POST_ANGINA, POST_MI), "Probability of angina to MI", 
				STR_SOURCE_OASIS, P_ANGINA2MI, RandomVariateFactory.getInstance("BetaVariate", BETA_ANGINA2MI[0], BETA_ANGINA2MI[1])));

		
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + POST_STROKE, 
				"Cost of year 2+ of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 2485.66, SecondOrderParamsRepository.getRandomVariateForCost(2485.66)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + POST_STROKE, 
				"Cost of episode of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 6120.32-2485.66, SecondOrderParamsRepository.getRandomVariateForCost(6120.32-2485.66)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + POST_ANGINA, 
				"Cost of year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 532.01, SecondOrderParamsRepository.getRandomVariateForCost(532.01)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + POST_ANGINA, 
				"Cost of episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 2517.97-532.01, SecondOrderParamsRepository.getRandomVariateForCost(2517.97-532.01)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + POST_MI, 
				"Cost of year 2+ Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 948, SecondOrderParamsRepository.getRandomVariateForCost(948)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + POST_MI, 
				"Cost of episode of Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 
				2016, 23536-948, SecondOrderParamsRepository.getRandomVariateForCost(23536-948)));
		
		final double[] paramsDuSTROKE = SecondOrderParamsRepository.betaParametersFromNormal(DU_STROKE[0], DU_STROKE[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + T2DMPrositCHDSubmodel.POST_STROKE, 
				"Disutility of stroke. Average of autonomous and dependant stroke disutilities", 
				"", DU_STROKE[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuSTROKE[0], paramsDuSTROKE[1])));
		final double[] paramsDuANGINA = SecondOrderParamsRepository.betaParametersFromNormal(DU_ANGINA[0], DU_ANGINA[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + T2DMPrositCHDSubmodel.POST_ANGINA, 
				"Disutility of angina", 
				"", DU_ANGINA[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuANGINA[0], paramsDuANGINA[1])));
		final double[] paramsDuMI = SecondOrderParamsRepository.betaParametersFromNormal(DU_MI[0], DU_MI[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + T2DMPrositCHDSubmodel.POST_MI, 
				"Disutility of POST_MI", "", DU_MI[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuMI[0], paramsDuMI[1])));
		
		secParams.addOtherParam(new SecondOrderParam(STR_DEATH_STROKE, 
				"Probability of death after stroke", "Core Model", 
				P_DEATH_STROKE));
		secParams.addOtherParam(new SecondOrderParam(STR_DEATH_MI_MAN, 
				"Probability of sudden death after POST_MI for men",	"Core Model", 
				P_DEATH_MI[BasicConfigParams.MAN]));
		secParams.addOtherParam(new SecondOrderParam(STR_DEATH_MI_WOMAN, 
				"Probability of sudden death after POST_MI for women", "Core Model", 
				P_DEATH_MI[BasicConfigParams.WOMAN]));

		// TODO: They are HR, not RR, so revision is required
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + DiabetesChronicComplications.CHD.name(), 
				"Increased mortality risk due to macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				2.00, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.00, 1.69, 2.38, 1)));
		
		addSecondOrderInitProportion(secParams);
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

	private class TimeToFirstStrokeParam extends UniqueEventParam<Long> implements TimeToEventParam { 

		public TimeToFirstStrokeParam(RandomNumber rng, int nPatients) {
			super(rng, nPatients, false);
		}

		@Override
		public Long getValue(DiabetesPatient pat) {
			final double age = pat.getAge();
			final double duration = pat.getDurationOfDiabetes();
			final int smoker = pat.isSmoker() ? 1 : 0;
			final int af = pat.hasAtrialFibrillation() ? 1 : 0;
			final double q = 0.00186 * Math.pow(1.092, age - duration - 55) * Math.pow(0.7, pat.getSex()) * Math.pow(1.547, smoker) * Math.pow(8.554, af) 
					* Math.pow(1.122,  (pat.getSbp() - 135.5) / 10) * Math.pow(1.138,  pat.getLipidRatio() - 5.11);
			final double aux = q * Math.pow(K, duration) / ONE_MINUS_K;
			
			final double lifetime = pat.getAgeAtDeath() - pat.getAge();
			final double time = Math.log(Math.log((1 - draw(pat)) / Math.exp(-aux)) / aux) / LN_K;
			return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));			
		}
		
	}
	
	/**
	 * Ommited risk ratio for Afro-Caribbean ethnicity (0.39)
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class TimeToFirstMIParam extends UniqueEventParam<Long> implements TimeToEventParam {

		public TimeToFirstMIParam(RandomNumber rng, int nPatients) {
			super(rng, nPatients, false);
		}

		@Override
		public Long getValue(DiabetesPatient pat) {
			final double age = pat.getAge();
			final double duration = pat.getDurationOfDiabetes();
			final int smoker = pat.isSmoker() ? 1 : 0;
			final int af = pat.hasAtrialFibrillation() ? 1 : 0;
			final double q = 0.0112 * Math.pow(1.059, age - duration - 55) * Math.pow(0.525, pat.getSex()) * Math.pow(1.350, smoker) 
					* Math.pow(1.183,  pat.getHba1c() - 6.72) * Math.pow(1.088,  (pat.getSbp() - 135.7) / 10) * Math.pow(3.845,  Math.log(pat.getLipidRatio()) - 1.59);
			
			final double lifetime = pat.getAgeAtDeath() - pat.getAge();
			final double time = Math.log(1 + Math.log(1 - draw(pat)) * ONE_MINUS_D / q) / LN_D;
			return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));			
		}
		
	}
	
	private class RRRecurrentStroke implements RRCalculator {
		private final double rrFemale16;
		private final double rrMale70;
		private final double rrFemale70;

		public RRRecurrentStroke(double rrFemale16, double rrMale70, double rrFemale70) {
			this.rrFemale16 = rrFemale16;
			this.rrMale70 = rrMale70;
			this.rrFemale70 = rrFemale70;
		}
		
		@Override
		public double getRR(DiabetesPatient pat) {
			if (BasicConfigParams.WOMAN == pat.getSex()) {
				return (pat.getAge() < 70) ? rrFemale16 : rrFemale70;
			}
			return (pat.getAge() < 70) ? 1.0 : rrMale70;
		}
	}
	
	private static void testStroke() {
		final Random rnd = new Random();
		final double age = 67;
		final double duration = 12;
		final int smoker = 0;
		final int af = 0;
		final int sex = 0;
		final double sbp = 147;
		final double lipidRatio = 5.65 / 1.11;
		final double q = 0.00186 * Math.pow(1.092, age - duration - 55) * Math.pow(0.7, sex) * Math.pow(1.547, smoker) * Math.pow(8.554, af) 
				* Math.pow(1.122,  (sbp - 135.5) / 10) * Math.pow(1.138,  lipidRatio - 5.11);
		final double aux = q * Math.pow(K, duration) / ONE_MINUS_K;
		
		System.out.println("rnd\tTime");
		for (int i = 0; i < 100; i++) {
			final double r = rnd.nextDouble();
			final double time = Math.log(Math.log((1 - r) / Math.exp(-aux)) / aux) / LN_K;
			System.out.println(r + "\t" + time);
		}
	}
	
	public class Instance extends ChronicComplicationSubmodel {
		/** Random value for predicting stroke-related death */ 
		private final double [] rndDeathStroke;
		/** Random value for predicting MI-related death */ 
		private final double [] rndDeathMI;

		private final double[]pDeathMI;
		private final double pDeathStroke;
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(T2DMPrositCHDSubmodel.this);

			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
			final RRCalculator rrToStroke2 = new RRRecurrentStroke(
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + POST_STROKE + "_" + POST_STROKE2 + "_1"), 
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + POST_STROKE + "_" + POST_STROKE2 + "_2"),
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + POST_STROKE + "_" + POST_STROKE2 + "_3"));
			
			addTime2Event(CHDTransitions.HEALTHY_STROKE.ordinal(), new TimeToFirstStrokeParam(rng, nPatients));
			addTime2Event(CHDTransitions.STROKE_STROKE2.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(POST_STROKE, POST_STROKE2), rrToStroke2));
			addTime2Event(CHDTransitions.HEALTHY_ANGINA.ordinal(), new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(POST_ANGINA), 
					SecondOrderParamsRepository.NO_RR));
			addTime2Event(CHDTransitions.HEALTHY_MI.ordinal(), new TimeToFirstMIParam(rng, nPatients));
			addTime2Event(CHDTransitions.ANGINA_MI.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients,
					secParams.getProbability(POST_ANGINA, POST_MI), SecondOrderParamsRepository.NO_RR));

			addData(secParams, POST_STROKE);
			addData(secParams, POST_STROKE2);
			addData(secParams, POST_ANGINA);
			addData(secParams, POST_MI);
			
			rndDeathStroke = new double[nPatients];
			rndDeathMI = new double[nPatients];
			
			for (int i = 0; i < nPatients; i++) {
				rndDeathStroke[i] = rng.draw();
				rndDeathMI[i] = rng.draw();
			}

			pDeathStroke = secParams.getOtherParam(STR_DEATH_STROKE);
			pDeathMI = new double[] {secParams.getOtherParam(STR_DEATH_MI_MAN), secParams.getOtherParam(STR_DEATH_MI_WOMAN)};
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
							if (BasicConfigParams.USE_CHD_DEATH_MODEL) {
								if (POST_MI.equals(stCHD)) {
									prog.addNewEvent(stCHD, timeToCHD, (rndDeath[id] <= pDeathMI[pat.getSex()]));
								}
								else if (POST_STROKE.equals(stCHD)) {
									prog.addNewEvent(stCHD, timeToCHD, (rndDeathStroke[id] <= pDeathStroke));							
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
			double cost = 0.0;
			if (state.contains(POST_ANGINA))
				cost += getData(POST_ANGINA).getCosts()[0];
			if (state.contains(POST_STROKE))
				cost += getData(POST_STROKE).getCosts()[0];
			if (state.contains(POST_MI))
				cost += getData(POST_MI).getCosts()[0];				
			return cost;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			double du = 0.0;
			if (state.contains(POST_ANGINA))
				du = getData(POST_ANGINA).getDisutility();
			if (state.contains(POST_STROKE) || state.contains(POST_STROKE2))
				du = method.combine(du, getData(POST_STROKE).getDisutility());
			if (state.contains(POST_MI))
				du = method.combine(du, getData(POST_MI).getDisutility());				
			return du;
		}
	}

}
