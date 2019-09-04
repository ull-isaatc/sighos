/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.TimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.UniqueEventParam;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * Based on the equations used in the PROSIT model ({@link https://www.prosit.de})
 * @author Iván Castilla Rodríguez
 *
 */
public class T2DMPrositRETSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage BGRET = new DiabetesComplicationStage("BGRET", "Background Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage PRET = new DiabetesComplicationStage("PRET", "Proliferative Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage ME = new DiabetesComplicationStage("ME", "Macular edema", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage BLI = new DiabetesComplicationStage("BLI", "Blindness", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage[] RETSubstates = new DiabetesComplicationStage[] {BGRET, PRET, ME, BLI};

	/** Unadjusted 5-year cumulative incidence of non-proliferative + preproliferative retinopathy from Jones et al. 10.2337/dc11-0943.	*/
	private static final double INC_DNC_BGRET = 0.359 + 0.04;  
	private static final double[] CI_INC_DNC_BGRET = {0.348 + 0.035, 0.37 + 0.044}; // From Jones et al. 10.2337/dc11-0943 (in suppl. data).  
	/** Annual risk of proliferative retinopathy related to duration of diabetes. Each t-upla is [duration, risk, cases, no cases]. 
	 * Cases and no cases are parameters for a beta distribution. 
	 * Adjusted from 14-years cum. incidence as in Klein et al. 10.1016/S0161-6420(98)91020-X */
	private static final double[][] P_PRET_DURATION = {
			{3, 0.0114583579396466, 0.838071440033366, 72.3025517144931},
			{5, 0.0138178645423381, 1.13084435305119, 80.7084550253924},
			{10, 0.0323542738061734, 7.13705744913035, 213.453813849256},
			{15, 0.0578795968469891, 7.57625727936768, 123.320599162054},
			{20, 0.0378073399495269, 2.83605031469243, 72.1771698292949},
			{25, 0.0306219917313025, 1.21611799312454, 38.4977583541608},
			{30, 0.0433123612117897, 1.36552525844188, 30.1618544603573},
			{Double.MAX_VALUE, 0.01959629623405, 0.537559864596714, 26.8941475446158}
	};
	/** Parameters of a log estimation of the effect of HbA1c in the risk of proliferative retinopathy from Klein et al. 10.1016/S0161-6420(98)91020-X.
	 * The final probability can be obtained by using the equation ln(F_P_PRET_HBA1C[0] + F_P_PRET_HBA1C[1] * ln(Patient.HbA1c)) */
	private static final double[] F_P_PRET_HBA1C = {0.865163983, 0.072083448};
	/** Base probability from healthy to proliferative retinopathy. 
	 * [base value, Alfa, Beta]. Alfa and Beta are parameters for a beta distribution according to the population size.
	 * Adjusted from 14-year incidence in Klein et al. 10.1016/S0161-6420(98)91020-X */
	private static final double[] P_DNC_PRET = {0.011874359, 3.087333468, 256.9126665};
	/** Relative risk of progressing to proliferative retinopathy fro nono-proliferative retinopathy, with respect to the base probability.
	 * The array contains the base value and parameters for a log normal distribution.
	 * Adapted from Klein et al. 10.1016/S0161-6420(98)91020-X */
	private static final double[] RR_BGRET_PRET = {4.016850595, 1.390498161, 0.602578596};
	
	/** Annual risk of macular edema related to duration of diabetes. Each t-upla is [duration, risk, cases, no cases]. 
	 * Cases and no cases are parameters for a beta distribution. 
	 * Adjusted from 14-years cum. incidence as in Klein et al. 10.1016/S0161-6420(98)91020-X */
	private static final double[][] P_ME_DURATION = {
			{3, 0.00981673405203298, 0.690049602438061, 69.6031455458156},
			{5, 0.015199841949835, 1.19727350516751, 77.5715392968959},
			{10, 0.024363387018722, 5.30007458872162, 212.242526719085},
			{15, 0.0283039507117478, 3.38204784793425, 116.108262263809},
			{20, 0.0195039664468808, 1.39563960229238, 70.1610668806384},
			{25, 0.0289290408280677, 1.2071256218647, 40.5199965817003},
			{30, 0.0222285611986246, 0.738477268099601, 32.4835230899485},
			{Double.MAX_VALUE, 0.0129667434006835, 0.422185893452303, 32.1369448309245}
	};
	/** Base probability from healthy to macular edema. 
	 * [base value, Alfa, Beta]. Alfa and Beta are parameters for a beta distribution according to the population size.
	 * Adjusted from 14-year incidence in Klein et al. 10.1016/S0161-6420(98)91020-X */
	private static final double[] P_DNC_ME = {0.013646875, 3.466306231, 250.5336938};
	/** Relative risk of progressing to macular edema from non-proliferative retinopathy, with respect to the base probability. 
	 * The array contains the base value and parameters for a log normal distribution.
	 * Adapted from Klein et al. 10.1016/S0161-6420(98)91020-X */
	private static final double[] RR_BGRET_ME = {1.860503855, 0.620847341, 0.616941709};
	/** Relative risk of progressing to macular edema from proliferative retinopathy, with respect to the base probability.
	 * The array contains the base value and parameters for a log normal distribution.
	 * Adapted from Klein et al. 10.1016/S0161-6420(98)91020-X */
	private static final double[] RR_PRET_ME = {2.625501971, 0.965272105, 0.995784813};
	/** Coefficients [A, B] for a linear interpolation y = A * X + B for different HbA1c levels */ 
	private static final double[][] F_P_ME_HBA1C = {
			{0.003391162, -0.015948774},
			{0.00846628, -0.066953706},
			{0.000690277, 0.021303929}
	};
	/** Reference HbA1c levels for linear interpolation in ME */
	private static final double[] F_LIMITS_ME_HBA1C = {7.55, 10.05, 11.35, 15.85};
	
	/** Base probability from healthy to blindness [base value, Alfa, Beta]. Alfa and Beta are parameters for a beta distribution according to the population size. 
	 * Adjusted from 14-year incidence in Moss et al. 10.1016/S0161-6420(98)96025-0 */
	private static final double[] P_DNC_BLI = {0.001151435, 0.340824848, 295.6591752};
	/** Relative risk of progressing to blindness from non-proliferative retinopathy, with respect to the base probability. The array contains the base value and
	 * parameters for a log normal distribution.
	 * Adapted from Moss et al. 10.1016/S0161-6420(98)96025-0 */
	private static final double[] RR_BGRET_BLI = {2.545462717, 0.934312448, 1.926641435};
	/** Relative risk of progressing to blindness from proliferative retinopathy, with respect to the base probability.The array contains the base value and
	 * parameters for a log normal distribution.
	 * Adapted from Moss et al. 10.1016/S0161-6420(98)96025-0 */
	private static final double[] RR_PRET_BLI = {14.27380266, 2.658425875, 1.851477385};
	/** Base probability from non macular edema to blindness (includes all healthy, non-proliferative and proliferative retinopathy).
	 * [base value, Alfa, Beta]. Alfa and Beta are parameters for a beta distribution according to the population size. 
	 * Adjusted from 14-year incidence in Moss et al. 10.1016/S0161-6420(98)96025-0 */
	private static final double[] P_NO_ME_BLI = {0.00261543, 1.867416888, 712.1325831};
	/** Relative risk of progressing to blindness with the presence of macular edema, with respect to its absence. The array contains the base value and
	 * parameters for a log normal distribution.
	 * Adapted from Moss et al. 10.1016/S0161-6420(98)96025-0 */
	private static final double[] RR_ME_BLI = {5.844950584, 1.76557814, 1.425261505};
	/** Parameters of a log estimation of the effect of HbA1c in the risk of blindness from Moss et al. 10.1016/S0161-6420(98)96025-0.
	 * The final probability can be obtained by using the equation ln(F_P_BLI_HBA1C[0] + F_P_BLI_HBA1C[1] * ln(Patient.HbA1c)) */
	private static final double[] F_P_BLI_HBA1C = {0.967923801, 0.017095587};
	
	private static final String STR_SOURCE_JONES = "Jones et al. 10.2337/dc11-0943"; 
	private static final String STR_SOURCE_KLEIN = "Klein et al. 10.1016/S0161-6420(98)91020-X"; 
	private static final String STR_SOURCE_MOSS = "Moss et al. 10.1016/S0161-6420(98)96025-0";
	
	// Assumption
	private static final double DU_BGRET = 0.0;
	// Utility (avg, SD) from either Fenwick et al.; or Sullivan
	private static final double[] DU_PRET = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.04, (0.066 - 0.014) / 3.92} : new double[] {0.0156, 0.0002};
	// Utility (avg, SD) from either Fenwick et al.; or Sullivan
	private static final double[] DU_ME = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.04, (0.066 - 0.014) / 3.92} : new double[] {0.0156, 0.0002};
	// Utility (avg, SD) from either Clarke et al.; or Sullivan
	private static final double[] DU_BLI = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.074, (0.124 - 0.025) / 3.92} : new double[] {0.0498, 0.0002};
	private static final double C_BGRET = 146.4525; // Detail: Parametros.xls
	private static final double C_PRET = 6394.62; // Detail: Parametros.xls
	private static final double C_ME = 6785.16; // Detail: Parametros.xls
	private static final double C_BLI = 2405.35;
	
	public enum RETTransitions {
		HEALTHY_BGRET(null, BGRET),
		HEALTHY_PRET(null, PRET),
		HEALTHY_ME(null, ME),
		BGRET_PRET(BGRET, PRET),
		BGRET_ME(BGRET, ME),
		BGRET_BLI(BGRET, BLI),
		PRET_BLI(PRET, BLI),
		PRET_ME(PRET, ME),
		ME_BLI(ME, BLI),
		HEALTHY_BLI(null, BLI);
		
		final private DiabetesComplicationStage from;
		final private DiabetesComplicationStage to;
		
		private RETTransitions(DiabetesComplicationStage from, DiabetesComplicationStage to) {
			this.from = from;
			this.to = to;
		}

		/**
		 * @return the from
		 */
		public DiabetesComplicationStage getFrom() {
			return from;
		}

		/**
		 * @return the to
		 */
		public DiabetesComplicationStage getTo() {
			return to;
		}
	}
	
	public T2DMPrositRETSubmodel() {
		super(DiabetesChronicComplications.RET, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public int getNStages() {
		return RETSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return RETSubstates;
	}

	@Override
	public int getNTransitions() {
		return RETTransitions.values().length;
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_BGRET = SecondOrderParamsRepository.betaParametersFromNormal(INC_DNC_BGRET, SecondOrderParamsRepository.sdFrom95CI(CI_INC_DNC_BGRET));
		
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, BGRET), 
				"5-year cummulative Incidence of healthy to background retinopathy", 
				STR_SOURCE_JONES, 
				INC_DNC_BGRET, "BetaVariate", paramsDNC_BGRET[0], paramsDNC_BGRET[1]));
		
		for (int i = 0; i < P_PRET_DURATION.length; i++) {
			secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, PRET) + "_DUR" + P_PRET_DURATION[i][0], 
					"Probability of proliferative retinopathy for duration of diabetes " + P_PRET_DURATION[i][0], 
					STR_SOURCE_KLEIN, P_PRET_DURATION[i][1], "BetaVariate", P_PRET_DURATION[i][2], P_PRET_DURATION[i][3]));			
		}
		
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, PRET), "Probability of healthy to proliferative retinopathy", 
				STR_SOURCE_KLEIN, P_DNC_PRET[0], "BetaVariate", P_DNC_PRET[1], P_DNC_PRET[2]));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + PRET, 
				"RR for proliferative retinopathy from background with respect to healthy", 
				STR_SOURCE_KLEIN, 
				RR_BGRET_PRET[0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_BGRET_PRET[1], RR_BGRET_PRET[2]))));

		for (int i = 0; i < P_ME_DURATION.length; i++) {
			secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ME) + "_DUR" + P_ME_DURATION[i][0], 
					"Probability of macular edema for duration of diabetes " + P_ME_DURATION[i][0], 
					STR_SOURCE_KLEIN, P_ME_DURATION[i][1], "BetaVariate", P_ME_DURATION[i][2], P_ME_DURATION[i][3]));			
		}

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ME), "Probability of healthy to macular edema", 
				STR_SOURCE_KLEIN, P_DNC_ME[0], "BetaVariate", P_DNC_ME[1], P_DNC_ME[2]));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ME + "_" + BGRET, 
				"RR for macular edema from background with respect to healthy", 
				STR_SOURCE_KLEIN, 
				RR_BGRET_ME[0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_BGRET_ME[1], RR_BGRET_ME[2]))));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ME + "_" + PRET, 
				"RR for macular edema from proliferative with respect to healthy", 
				STR_SOURCE_KLEIN, 
				RR_PRET_ME[0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_PRET_ME[1], RR_PRET_ME[2]))));

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, BLI), "Probability of healthy to blindness", 
				STR_SOURCE_MOSS, P_DNC_BLI[0], "BetaVariate", P_DNC_BLI[1], P_DNC_BLI[2]));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + BLI + "_" + BGRET, 
				"RR for blindness from background with respect to healthy", 
				STR_SOURCE_MOSS, 
				RR_BGRET_BLI[0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_BGRET_BLI[1], RR_BGRET_BLI[2]))));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + BLI + "_" + PRET, 
				"RR for blindness from proliferative with respect to healthy", 
				STR_SOURCE_MOSS, 
				RR_PRET_BLI[0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_PRET_BLI[1], RR_PRET_BLI[2]))));
		
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "NO_ME_" + BLI, "Probability of non macular edema to blindness", 
				STR_SOURCE_MOSS, P_NO_ME_BLI[0], "BetaVariate", P_NO_ME_BLI[1], P_NO_ME_BLI[2]));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + BLI + "_" + ME, 
				"RR for blindness with the presence of macular edema, with respect to its absence", 
				STR_SOURCE_MOSS, 
				RR_ME_BLI[0], RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_ME_BLI[1], RR_ME_BLI[2]))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BGRET, "Cost of BGRET", "Original analysis", 2018, C_BGRET, SecondOrderParamsRepository.getRandomVariateForCost(C_BGRET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + PRET, "Cost of PRET", "Original analysis", 2018, C_PRET, SecondOrderParamsRepository.getRandomVariateForCost(C_PRET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ME, "Cost of ME", "Original analysis", 2018, C_ME, SecondOrderParamsRepository.getRandomVariateForCost(C_ME)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BLI, "Cost of BLI", "Conget et al.", 2016, C_BLI, SecondOrderParamsRepository.getRandomVariateForCost(C_BLI)));
		
		final double[] paramsDuPRET = SecondOrderParamsRepository.betaParametersFromNormal(DU_PRET[0], DU_PRET[1]);
		final double[] paramsDuME = SecondOrderParamsRepository.betaParametersFromNormal(DU_ME[0], DU_ME[1]);
		final double[] paramsDuBLI = SecondOrderParamsRepository.betaParametersFromNormal(DU_BLI[0], DU_BLI[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + BGRET, "Disutility of BGRET", "", DU_BGRET));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + PRET, "Disutility of PRET", 
				"", DU_PRET[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuPRET[0], paramsDuPRET[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ME, "Disutility of RET", 
				"", DU_ME[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuME[0], paramsDuME[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + BLI, "Disutility of BLI", 
				"", DU_BLI[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuBLI[0], paramsDuBLI[1])));

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}
	private abstract class CompetingRisksTimeToEventParam extends UniqueEventParam<Long> implements TimeToEventParam {
		public CompetingRisksTimeToEventParam(RandomNumber rng, int nPatients) {
			super(rng, nPatients, true);
		}

		public long getProbRRValue(final DiabetesPatient pat, final double lifetime, final double p, final double rr) {
			if (p == 0.0)
				return Long.MAX_VALUE;
			final double newMinus = -1 / (1-Math.exp(Math.log(1 - p) * rr));
			final double time = newMinus * draw(pat);		
			return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
		}
		
		public long getValueDuration(final DiabetesPatient pat, final double lifetime, final double[][] incidenceDuration) {
			final double durationOfDiabetes = pat.getDurationOfDiabetes();
			double ref = 0.0;
			for (int i = 0; i < incidenceDuration.length; i++) {
				if (incidenceDuration[i][0] > durationOfDiabetes) {
					if (incidenceDuration[i][1] != 0.0) {
						final double newMinus = -1 / (1-Math.exp(Math.log(1-incidenceDuration[i][1])));
						final double time = newMinus * draw(pat) + ref;					
						if (time + durationOfDiabetes < incidenceDuration[i][0])
							return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
					}
					ref = incidenceDuration[i][0] - durationOfDiabetes;
				}
			}
			return Long.MAX_VALUE;
		}

		public long getValueHbA1cFromLnFunction(DiabetesPatient pat, final double lifetime, double[] fpHbA1c) {
			final double pHbA1c = Math.log(fpHbA1c[0] + fpHbA1c[1] * Math.log(pat.getHba1c())); 
			if (pHbA1c == 0.0)
				return Long.MAX_VALUE;
			final double newMinus = -1 / (1-Math.exp(Math.log(1-pHbA1c)));
			final double time = newMinus * draw(pat);		
			return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
		}
		
	}
	
	/**
	 * A class that calculates the time to the event of proliferative retinopathy according to the WESDR data. 
	 * In PROSIT, there are two apparent errors with regard to this computation
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class WESDR_TimeToPRETParam extends CompetingRisksTimeToEventParam {
		/** Annual risk of the event for duration of diabetes */
		private final double[][] incidenceDuration; 
		private final double[] fpHbA1c;
		private final double baseProbability;
		private final double rrFromBGRET;
		
		public WESDR_TimeToPRETParam(RandomNumber rng, int nPatients, double baseProbability, double rrFromBGRET, final double[][] incidenceDuration, double[] fpHbA1c) {
			super(rng, nPatients);
			this.fpHbA1c = fpHbA1c;
			this.baseProbability = baseProbability;
			this.rrFromBGRET = rrFromBGRET;
			this.incidenceDuration = incidenceDuration;
		}
		
		@Override
		public Long getValue(DiabetesPatient pat) {
			final double lifetime = pat.getAgeAtDeath() - pat.getAge();
			long timeToEvent = getProbRRValue(pat, lifetime, baseProbability, pat.getDetailedState().contains(BGRET) ? rrFromBGRET : 1.0);
			// Risk due to duration of diabetes
			long timeToEventDuration = getValueDuration(pat, lifetime, incidenceDuration);
			// Risk due to HbA1c
			long timeToEventHbA1c = getValueHbA1cFromLnFunction(pat, lifetime, fpHbA1c);

			return ComplicationSubmodel.min(timeToEvent, timeToEventDuration, timeToEventHbA1c);
		}
		
	}
	
	/**
	 * A class that calculates the time to the event of macular edema according to the WESDR data. 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class WESDR_TimeToMEParam extends CompetingRisksTimeToEventParam {
		/** Annual risk of the event for duration of diabetes */
		private final double[][] incidenceDuration; 
		private final double[][] fpHbA1c;
		private final double[] fpLimitsHbA1c;
		private final double baseProbability;
		private final double rrFromBGRET;
		private final double rrFromPRET;
		
		public WESDR_TimeToMEParam(RandomNumber rng, int nPatients, double baseProbability, double rrFromBGRET, double rrFromPRET, final double[][] incidenceDuration, double[][] fpHbA1c, double[] fpLimitsHbA1c) {
			super(rng, nPatients);
			this.fpHbA1c = fpHbA1c;
			this.fpLimitsHbA1c = fpLimitsHbA1c;
			this.baseProbability = baseProbability;
			this.rrFromBGRET = rrFromBGRET;
			this.rrFromPRET = rrFromPRET;
			this.incidenceDuration = incidenceDuration;
		}
		
		private long getValueHbA1c(DiabetesPatient pat, final double lifetime) {
			final double hba1c = pat.getHba1c();
			double pHbA1c = 0.0;
			if (hba1c < fpLimitsHbA1c[0])
				pHbA1c = fpHbA1c[0][0] * fpLimitsHbA1c[0] + fpHbA1c[0][1];
			else if (hba1c > fpLimitsHbA1c[fpLimitsHbA1c.length - 1]) 
				pHbA1c = fpHbA1c[fpHbA1c.length - 1][0] * fpLimitsHbA1c[fpLimitsHbA1c.length - 1] + fpHbA1c[fpHbA1c.length - 1][1];
			else {
				int i = 1;
				while (hba1c > fpLimitsHbA1c[i])
					i++;
				pHbA1c = fpHbA1c[i - 1][0] * hba1c + fpHbA1c[i - 1][1];				
			}
			if (pHbA1c == 0.0)
				return Long.MAX_VALUE;
			final double newMinus = -1 / (1-Math.exp(Math.log(1-pHbA1c)));
			final double time = newMinus * draw(pat);		
			return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
		}
		
		@Override
		public Long getValue(DiabetesPatient pat) {
			final double lifetime = pat.getAgeAtDeath() - pat.getAge();
			long timeToEvent = getProbRRValue(pat, lifetime, baseProbability, pat.getDetailedState().contains(PRET) ? rrFromPRET : (pat.getDetailedState().contains(BGRET) ? rrFromBGRET : 1.0));
			// Risk due to duration of diabetes
			long timeToEventDuration = getValueDuration(pat, lifetime, incidenceDuration);
			// Risk due to HbA1c
			long timeToEventHbA1c = getValueHbA1c(pat, lifetime);

			return ComplicationSubmodel.min(timeToEvent, timeToEventDuration, timeToEventHbA1c);
		}
		
	}

	/**
	 * A class that calculates the time to the event of proliferative retinopathy according to the WESDR data. 
	 * In PROSIT, there are two apparent errors with regard to this computation
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class WESDR_TimeToBLIParam extends CompetingRisksTimeToEventParam {
		/** Annual risk of the event for duration of diabetes */
		private final double[] fpHbA1c;
		private final double baseProbability;
		private final double rrFromBGRET;
		private final double rrFromPRET;
		private final double noMEProbability;
		private final double rrFromME; 
		
		public WESDR_TimeToBLIParam(RandomNumber rng, int nPatients, double baseProbability, double rrFromBGRET, double rrFromPRET, double noMEProbability, double rrFromME, double[] fpHbA1c) {
			super(rng, nPatients);
			this.fpHbA1c = fpHbA1c;
			this.baseProbability = baseProbability;
			this.rrFromBGRET = rrFromBGRET;
			this.rrFromPRET = rrFromPRET;
			this.noMEProbability = noMEProbability;
			this.rrFromME = rrFromME;
		}
		
		@Override
		public Long getValue(DiabetesPatient pat) {
			final double lifetime = pat.getAgeAtDeath() - pat.getAge();
			long timeToEvent = getProbRRValue(pat, lifetime, baseProbability, pat.getDetailedState().contains(PRET) ? rrFromPRET : (pat.getDetailedState().contains(BGRET) ? rrFromBGRET : 1.0));
			// Risk due to ME
			long timeToEventME = getProbRRValue(pat, lifetime, noMEProbability, pat.getDetailedState().contains(ME) ? rrFromME : 1.0);
			// Risk due to HbA1c
			long timeToEventHbA1c = getValueHbA1cFromLnFunction(pat, lifetime, fpHbA1c);

			return ComplicationSubmodel.min(timeToEvent, timeToEventME, timeToEventHbA1c);
		}
		
	}
	
	public class Instance extends ChronicComplicationSubmodel {
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(T2DMPrositRETSubmodel.this);
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();

			//The 5-year cumulative incidence is adjusted to annual probability
			addTime2Event(RETTransitions.HEALTHY_BGRET.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 1 - Math.pow(1 - secParams.getProbability(BGRET), 0.2), SecondOrderParamsRepository.NO_RR));
			final double[][]pPRETduration = new double[P_PRET_DURATION.length][2];
			for (int i = 0; i < P_PRET_DURATION.length; i++) {
				pPRETduration[i][0] = P_PRET_DURATION[i][0];
				pPRETduration[i][1] = secParams.getProbParam(SecondOrderParamsRepository.getProbString(null, PRET) + "_DUR" + P_PRET_DURATION[i][0]);
			}
			final WESDR_TimeToPRETParam timeToPRET = new WESDR_TimeToPRETParam(rng, nPatients, 
					secParams.getProbability(PRET), 
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + PRET), pPRETduration, F_P_PRET_HBA1C);
			addTime2Event(RETTransitions.HEALTHY_PRET.ordinal(), timeToPRET);
			addTime2Event(RETTransitions.BGRET_PRET.ordinal(), timeToPRET);

			final double[][]pMEduration = new double[P_ME_DURATION.length][2];
			for (int i = 0; i < P_ME_DURATION.length; i++) {
				pMEduration[i][0] = P_ME_DURATION[i][0];
				pMEduration[i][1] = secParams.getProbParam(SecondOrderParamsRepository.getProbString(null, ME) + "_DUR" + P_ME_DURATION[i][0]);
			}
			final WESDR_TimeToMEParam timeToME = new WESDR_TimeToMEParam(rng, nPatients, 
					secParams.getProbability(ME), 
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ME + "_" + BGRET),
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ME + "_" + PRET),
					pMEduration, F_P_ME_HBA1C, F_LIMITS_ME_HBA1C);
			addTime2Event(RETTransitions.HEALTHY_ME.ordinal(), timeToME);
			addTime2Event(RETTransitions.BGRET_ME.ordinal(), timeToME);
			addTime2Event(RETTransitions.PRET_ME.ordinal(), timeToME);
			
			final WESDR_TimeToBLIParam timeToBLI = new WESDR_TimeToBLIParam(rng, nPatients,
					secParams.getProbability(BLI),
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + BLI + "_" + BGRET),
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + BLI + "_" + PRET),
					secParams.getProbParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "NO_ME_" + BLI),
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + BLI + "_" + ME),
					F_P_BLI_HBA1C);
			
			addTime2Event(RETTransitions.HEALTHY_BLI.ordinal(), timeToBLI);
			addTime2Event(RETTransitions.BGRET_BLI.ordinal(), timeToBLI);
			addTime2Event(RETTransitions.PRET_BLI.ordinal(), timeToBLI);
			addTime2Event(RETTransitions.ME_BLI.ordinal(), timeToBLI);
			
			setStageInstance(BGRET, secParams);
			setStageInstance(PRET, secParams);
			setStageInstance(ME, secParams);
			setStageInstance(BLI, secParams);
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			// Only schedules new events if the the patient has not suffered the complication yet, and the time of the event is lower
			// than the expected time to death and the previously computed (if any) time to the event
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
				// Checks whether there is somewhere to transit to
				if (!state.contains(BLI)) {
					long timeToBLI = Long.MAX_VALUE;
					long timeToBGRET = Long.MAX_VALUE;
					long timeToPRET = Long.MAX_VALUE;
					long timeToME = Long.MAX_VALUE;
					final long previousTimeToBGRET = pat.getTimeToChronicComorbidity(BGRET);
					final long previousTimeToPRET = pat.getTimeToChronicComorbidity(PRET);
					final long previousTimeToME = pat.getTimeToChronicComorbidity(ME);
					final long previousTimeToBLI = pat.getTimeToChronicComorbidity(BLI);
					long limit = pat.getTimeToDeath();
					if (limit > previousTimeToBLI)
						limit = previousTimeToBLI;
					// Already at ME: can progress to PRET (if not yet) and BLI
					if (state.contains(ME)) {
						timeToBLI = getTimeToEvent(pat, RETTransitions.ME_BLI.ordinal(), limit);
						if (limit > timeToBLI)
							limit = timeToBLI;
						// Already at PRET and ME: calculate alternative time to BLI
						if (state.contains(PRET)) {
							final long altTimeToBLI = getTimeToEvent(pat, RETTransitions.PRET_BLI.ordinal(), limit);
							// If the time from PRET to BLI is lower than from ME to BLI, use the former
							if (timeToBLI > altTimeToBLI)
								timeToBLI = altTimeToBLI;
						}
						else {
							if (limit > previousTimeToPRET)
								limit = previousTimeToPRET;
							timeToPRET = getTimeToEvent(pat, RETTransitions.BGRET_PRET.ordinal(), limit);						
						}
					}
					// Already at PRET but not at ME: can progress to BLI and ME
					else if (state.contains(PRET)) {
						timeToBLI = getTimeToEvent(pat, RETTransitions.PRET_BLI.ordinal(), limit);
						limit = min(limit, timeToBLI, previousTimeToME);
						timeToME = getTimeToEvent(pat, RETTransitions.PRET_ME.ordinal(), limit);						
					}
					// Already at BGRET: can progress to BLI, ME and PRET
					else if (state.contains(BGRET)) {
						timeToBLI = getTimeToEvent(pat, RETTransitions.BGRET_BLI.ordinal(), limit);
						if (limit > timeToBLI)
							limit = timeToBLI;
						timeToPRET = getTimeToEvent(pat, RETTransitions.BGRET_PRET.ordinal(), (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
						timeToME = getTimeToEvent(pat, RETTransitions.BGRET_ME.ordinal(), (limit > previousTimeToME) ? previousTimeToME : limit);						
					}
					// Healthy: can progress to any state
					else {
						// RR from healthy to BLI
						timeToBLI = getTimeToEvent(pat, RETTransitions.HEALTHY_BLI.ordinal(), limit);
						if (limit > timeToBLI)
							limit = timeToBLI;
						timeToPRET = getTimeToEvent(pat, RETTransitions.HEALTHY_PRET.ordinal(), (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
						timeToME = getTimeToEvent(pat, RETTransitions.HEALTHY_ME.ordinal(), (limit > previousTimeToME) ? previousTimeToME : limit);						
						// Adjust limit for BGRET
						limit = min(limit, timeToPRET, timeToME, previousTimeToPRET, previousTimeToME, previousTimeToBGRET);
						timeToBGRET = getTimeToEvent(pat, RETTransitions.HEALTHY_BGRET.ordinal(), limit);
					}
					// Check previously scheduled events
					if (timeToBGRET != Long.MAX_VALUE) {
						if (previousTimeToBGRET < Long.MAX_VALUE) {
							prog.addCancelEvent(BGRET);
						}
						prog.addNewEvent(BGRET, timeToBGRET);
					}
					if (timeToPRET != Long.MAX_VALUE) {
						if (previousTimeToPRET < Long.MAX_VALUE) {
							prog.addCancelEvent(PRET);
						}
						prog.addNewEvent(PRET, timeToPRET);
						// If the new PRET event happens before a previously scheduled BGRET event, the latter must be cancelled 
						if (previousTimeToBGRET < Long.MAX_VALUE && timeToPRET < previousTimeToBGRET)
							prog.addCancelEvent(BGRET);
					}
					if (timeToME != Long.MAX_VALUE) {
						if (previousTimeToME < Long.MAX_VALUE) {
							prog.addCancelEvent(ME);
						}
						prog.addNewEvent(ME, timeToME);
						// If the new ME event happens before a previously scheduled BGRET event, the latter must be cancelled 
						if (previousTimeToBGRET < Long.MAX_VALUE && timeToME < previousTimeToBGRET)
							prog.addCancelEvent(BGRET);
					}
					if (timeToBLI != Long.MAX_VALUE) {
						if (previousTimeToBLI < Long.MAX_VALUE) {
							prog.addCancelEvent(BLI);
						}
						prog.addNewEvent(BLI, timeToBLI);
						// If the new BLI event happens before any previously scheduled RET event, the latter must be cancelled 
						if (previousTimeToBGRET < Long.MAX_VALUE && timeToBLI < previousTimeToBGRET)
							prog.addCancelEvent(BGRET);
						if (previousTimeToPRET < Long.MAX_VALUE && timeToBLI < previousTimeToPRET)
							prog.addCancelEvent(PRET);
						if (previousTimeToME < Long.MAX_VALUE && timeToBLI < previousTimeToME)
							prog.addCancelEvent(ME);
					}
				}
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			double cost = 0.0;
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();

			if (state.contains(LyRETSubmodel.BLI))
				cost += getCosts(BLI)[0];
			else if (state.contains(LyRETSubmodel.ME))
				cost += getCosts(ME)[0];
			else if (state.contains(LyRETSubmodel.PRET))
				cost += getCosts(PRET)[0];
			else if (state.contains(LyRETSubmodel.BGRET))
				cost += getCosts(BGRET)[0];				
			return cost;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			
			if (state.contains(BLI))
				return getDisutility(BLI);
			if (state.contains(ME)) {
				if (state.contains(PRET)) {
					return method.combine(getDisutility(ME), getDisutility(PRET));
				}
				else if (state.contains(BGRET)) {
					return method.combine(getDisutility(ME), getDisutility(BGRET));				
				}
			}
			if (state.contains(PRET)) {
				return getDisutility(PRET);
			}
			else if (state.contains(BGRET)) {
				return getDisutility(BGRET);				
			}
			return 0.0;
		}
	}
}
