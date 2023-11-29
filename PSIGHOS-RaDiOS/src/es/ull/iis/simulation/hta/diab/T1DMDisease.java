/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import java.util.TreeMap;

import es.ull.iis.simulation.condition.AndCondition;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.manifestations.Angina;
import es.ull.iis.simulation.hta.diab.manifestations.BackgroundRetinopathy;
import es.ull.iis.simulation.hta.diab.manifestations.Blindness;
import es.ull.iis.simulation.hta.diab.manifestations.CoronaryHeartDisease;
import es.ull.iis.simulation.hta.diab.manifestations.EndStageRenalDisease;
import es.ull.iis.simulation.hta.diab.manifestations.HeartFailure;
import es.ull.iis.simulation.hta.diab.manifestations.LowExtremityAmputation;
import es.ull.iis.simulation.hta.diab.manifestations.Macroalbuminuria;
import es.ull.iis.simulation.hta.diab.manifestations.MacularEdema;
import es.ull.iis.simulation.hta.diab.manifestations.Microalbuminuria;
import es.ull.iis.simulation.hta.diab.manifestations.MyocardialInfarction;
import es.ull.iis.simulation.hta.diab.manifestations.Neuropathy;
import es.ull.iis.simulation.hta.diab.manifestations.ProliferativeRetinopathy;
import es.ull.iis.simulation.hta.diab.manifestations.SevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diab.manifestations.Stroke;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.params.calculators.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.params.calculators.ConstantParameterCalculator;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.condition.ExclusiveChoiceCondition;
import es.ull.iis.simulation.hta.progression.condition.PreviousDiseaseProgressionCondition;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class T1DMDisease extends Disease {
	public enum GroupOfManifestations implements Named {
		NEU,
		NPH,
		RET,
		CHD,
		HYPO
	};
	
	public static class DEF_C_DNC {
		/** Value computed by substracting the burden of complications from the global burden of DM1 in Spain; 
		 * finally divided by the prevalent DM1 population */
		public final static double VALUE = (5809000000d - 2143000000d) / 3282790d;
		/** The year of the default cost for diabetes with no complications: for updating with IPC */
		public final static int YEAR = 2012; 
		/** Description of the source */
		public final static String SOURCE = "Crespo et al. 2012: http://dx.doi.org/10.1016/j.avdiab.2013.07.007";
	}
	public final static double[] DEF_U_DNC = {0.785, ((0.889 - 0.681) / 3.92)};
	
	// Probability parameters for nephropathy
	private static final double P_DNC_ALB1 = 0.0436;
	private static final double[] CI_DNC_ALB1 = {0.0136, 0.0736}; // Assumption
	private static final double BETA_ALB1 = 3.25;
	private static final double P_DNC_ALB2 = 0.0037;
	private static final double BETA_ALB2 = 7.95;
	private static final double P_ALB1_ESRD = 0.0133;
	private static final double[] CI_ALB1_ESRD = {0.01064, 0.01596};
	private static final double P_ALB2_ESRD = 0.1579;
	private static final double P_ALB1_ALB2 = 0.1565;
	private static final double P_DNC_ESRD = 0.0002;

	// Probability parameters for neuropathy
	private static final double P_DNC_NEU = 0.0354;
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double P_DNC_LEA = 0.0003;
	private static final double[] CI_DNC_NEU = {0.020, 0.055}; // McQueen
	private static final double[] LIMITS_DNC_LEA = {0.0, 0.0006}; // Assumption
	private static final double[] CI_NEU_LEA = {0.01232, 0.01848};
	private static final double BETA_NEU = 5.3;

	private static final double P_NEU_ALB1 = 0.097;
	private static final double[] CI_NEU_ALB1 = {0.055, 0.149};

	// Probability parameters for retinopathy
	private static final double P_DNC_BGRET = 0.0454;
	private static final double P_DNC_PRET = 0.0013;
	private static final double P_DNC_ME = 0.0012;
	private static final double P_BGRET_PRET = 0.0595;
	private static final double P_BGRET_ME = 0.0512;
	private static final double P_BGRET_BLI = 0.0001;
	private static final double P_PRET_BLI = 0.0038;
	private static final double P_ME_BLI = 0.0016;
	private static final double P_DNC_BLI = 1.9e-6;
	private static final double BETA_BGRET = 10.10;
	private static final double BETA_PRET = 6.30;
	private static final double BETA_ME = 1.20;

	// Probability parameters for Coronary Heart Disease
	private static final double P_DNC_CHD = 0.0045;
	private static final double P_NEU_CHD = 0.02;
	private static final double P_NPH_CHD = 0.0224;
	private static final double P_RET_CHD = 0.0155;	
	private static final double[] CI_DNC_CHD = {0.001, 0.0084};
	private static final double[] CI_NEU_CHD = {0.016, 0.044};
	private static final double[] CI_NPH_CHD = {0.013, 0.034};
	private static final double[] CI_RET_CHD = {0.01, 0.043};
	private static final double RR_CHD = 1.15;
	private static final double[] CI_RR_CHD = {0.92, 1.43};
	/** Proportion of CHD manifestations that result a myocardial infarction */
	private static final double P_CHD_MI = 0.53;
	/** Proportion of CHD manifestations that result a stroke */
	private static final double P_CHD_STROKE = 0.07;
	/** Proportion of CHD manifestations that result a heart failure */
	private static final double P_CHD_HF = 0.12;
	/** Proportion of CHD manifestations that result an angina */
	private static final double P_CHD_ANGINA = 0.28;
	
	// Probability parameters for severe hypoglycemic episodes
	/** Mean probability of hypoglycemic events in GOLD study (adjusted from annual rate */
	private static final double P_HYPO = 0.0706690;
	private static final double []P_HYPO_BETA = {9.9643, 131.0357};

	final private DiseaseProgression she;
	final private DiseaseProgression alb1;
	final private DiseaseProgression alb2;
	final private DiseaseProgression esrd;
	final private DiseaseProgression neu;
	final private DiseaseProgression lea;
	final private DiseaseProgression bgret;
	final private DiseaseProgression pret;
	final private DiseaseProgression me;
	final private DiseaseProgression bli;
	final private DiseaseProgression stroke;
	final private DiseaseProgression angina;
	final private DiseaseProgression hf;
	final private DiseaseProgression mi;
	final private DiseaseProgression chd;

	// Flags for validation and comparison
	public static final boolean DISABLE_CHD = false;
	public static final boolean DISABLE_RET = true;
	public static final boolean DISABLE_NEU = true;
	public static final boolean DISABLE_NPH = true;
	public static final boolean DISABLE_SHE = true;
	/** Uses fix values for initial age, HbA1c level and duration of diabetes */
	public static final boolean FIXED_BASE_VALUES = true;
	
	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public T1DMDisease(SecondOrderParamsRepository secParams) {
		super(secParams, "T1DM", "Type I Diabetes Mellitus");
		if (DISABLE_SHE)
			she = null;
		else {
			she = new SevereHypoglycemiaEvent(secParams, this);
			assignLabel(GroupOfManifestations.HYPO, she);
			addProgression(she, false);
		}
		
		if (DISABLE_NPH) {
			alb1 = null;
			alb2 = null;
			esrd = null;
		}
		else {
			// Register and configure Nephropathy-related manifestations
			alb1 = new Microalbuminuria(secParams, this);
			alb2 = new Macroalbuminuria(secParams, this);
			esrd = new EndStageRenalDisease(secParams, this);
			assignLabel(GroupOfManifestations.NPH, alb1);
			assignLabel(GroupOfManifestations.NPH, alb2);
			assignLabel(GroupOfManifestations.NPH, esrd);
			addProgression(alb1, true);
			addProgression(alb2, true);
			addProgression(alb1, alb2, true);
			// Do not use any additional risk
			addProgression(esrd, false);
			addProgression(alb1, esrd, false);
			addProgression(alb2, esrd, false);
			// Define exclusive manifestations
			addExclusion(alb2, alb1);
			addExclusion(esrd, alb1);
			addExclusion(esrd, alb2);
		}

		if (DISABLE_NEU) {
			neu = null;
			lea = null;
		}
		else {
			// Register and configure Nephropathy-related manifestations
			neu = new Neuropathy(secParams, this);
			lea = new LowExtremityAmputation(secParams, this);
			assignLabel(GroupOfManifestations.NEU, neu);
			assignLabel(GroupOfManifestations.NEU, lea);
			addProgression(neu, true);
			addProgression(lea, false);
			addProgression(neu, lea, false);
			// Define exclusive manifestations
			addExclusion(lea, neu);
			if (!DISABLE_NPH) {
				// There is an additional risk to progress from neuropathy to microalbuminuria
				addProgression(neu, alb1, false);
				// Manually adds a second extra risk from LEA, which uses the same probability as the other progression 
				final ParameterCalculator tte =  
						new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(neu, alb1), secParams, alb1);
				final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(lea);
				new DiseaseProgressionPathway(secParams, alb1, cond, tte);
			}
		}
		
		if (DISABLE_RET) {
			bgret = null;
			pret = null;
			me = null;
			bli = null;
		}
		else {
			bgret = new BackgroundRetinopathy(secParams, this);
			pret = new ProliferativeRetinopathy(secParams, this);
			me = new MacularEdema(secParams, this);
			bli = new Blindness(secParams, this);
			assignLabel(GroupOfManifestations.RET, bgret);
			assignLabel(GroupOfManifestations.RET, pret);
			assignLabel(GroupOfManifestations.RET, me);
			assignLabel(GroupOfManifestations.RET, bli);
			addProgression(bgret, true);
			addProgression(pret, true);
			addProgression(bgret, pret, true);
			addProgression(me, true);
			addProgression(bgret, me, true);
			// Manually adds a second pathway to ME from PRET that uses the same risk than BGRET, in case BGRET is ommited 
			final ParameterCalculator tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(bgret, me), secParams, me, new SheffieldRRCalculator(secParams, me.name()));
			final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(pret);
			new DiseaseProgressionPathway(secParams, me, cond, tte);
			addProgression(bli, false);
			addProgression(bgret, bli, false);
			addProgression(pret, bli, false);
			addProgression(me, bli, false);
			// Define exclusive manifestations
			addExclusion(pret, bgret);
			addExclusion(bli, bgret);
			addExclusion(bli, pret);
			addExclusion(bli, me);
		}
		
		if (DISABLE_CHD) {
			angina = null;
			stroke = null;
			mi = null;
			hf = null;
			chd = null;
		}
		else {
			
			angina = new Angina(secParams, this);
			stroke = new Stroke(secParams, this);
			mi = new MyocardialInfarction(secParams, this);
			hf = new HeartFailure(secParams, this);
			chd = new CoronaryHeartDisease(secParams, this);
			assignLabel(GroupOfManifestations.CHD, stroke);
			assignLabel(GroupOfManifestations.CHD, angina);
			assignLabel(GroupOfManifestations.CHD, mi);
			assignLabel(GroupOfManifestations.CHD, hf);
			final ParameterCalculator rrCHD = new HbA1c1ppRRCalculator(secParams);
			// The destination is the stage: CHD 
			new DiseaseProgressionPathway(secParams, chd, new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName("CHD"), secParams, chd, rrCHD));

			final TreeMap<DiseaseProgression, String> mapping = new TreeMap<>();
			for (DiseaseProgression manifCHD : getLabeledManifestations(GroupOfManifestations.CHD)) 
				mapping.put(manifCHD, RiskParamDescriptions.PROBABILITY.getParameterName(manifCHD));
			final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new AndCondition<>(
					new ExclusiveChoiceCondition(secParams, mapping), 
					new PreviousDiseaseProgressionCondition(chd));
			for (DiseaseProgression manifCHD : getLabeledManifestations(GroupOfManifestations.CHD))
				new DiseaseProgressionPathway(secParams, manifCHD, cond, new ConstantParameterCalculator(0));
			
			
			// Defines a single pathway, but the calculator uses the different probabilities
			if (!DISABLE_NEU) {
				new DiseaseProgressionPathway(secParams, chd, new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName("NEU_CHD"), secParams, chd, rrCHD));
			}
			if (!DISABLE_NPH) {
				new DiseaseProgressionPathway(secParams, chd, new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName("NPH_CHD"), secParams, chd, rrCHD));
			}
			if (!DISABLE_RET) {
				new DiseaseProgressionPathway(secParams, chd, new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName("RET_CHD"), secParams, chd, rrCHD));
			}
		}
	}

	private void addProgression(DiseaseProgression fromManif, DiseaseProgression toManif, boolean useSheffieldRR) {
		final SecondOrderParamsRepository secParams = getRepository();
		
		ParameterCalculator tte;
		if (useSheffieldRR)
			tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(fromManif, toManif), secParams, toManif, new SheffieldRRCalculator(secParams, toManif.name()));
		else
			tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(fromManif, toManif), secParams, toManif);
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(fromManif);
		new DiseaseProgressionPathway(secParams, toManif, cond, tte);
	}

	private void addProgression(DiseaseProgression toManif, boolean useSheffieldRR) {
		final SecondOrderParamsRepository secParams = getRepository();
		
		ParameterCalculator tte;
		if (useSheffieldRR)
			tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(toManif), secParams, toManif, new SheffieldRRCalculator(secParams, toManif.name()));
		else
			tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(toManif), secParams, toManif);
		new DiseaseProgressionPathway(secParams, toManif, tte);
	}
	
	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		
		// Set asymptomatic follow-up cost and disutility. Treatment cost for asymptomatics is assumed to be 0 
		CostParamDescriptions.FOLLOW_UP_COST.addParameter(secParams, this, "Diabetes with no complications", 
				DEF_C_DNC.SOURCE, DEF_C_DNC.YEAR, DEF_C_DNC.VALUE, SecondOrderParamsRepository.getRandomVariateForCost(DEF_C_DNC.VALUE));

		final double[] paramsU_DNC = Statistics.betaParametersFromNormal(DEF_U_DNC[0], DEF_U_DNC[1]);
		UtilityParamDescriptions.UTILITY.addParameter(secParams, this, "", DEF_U_DNC[0], RandomVariateFactory.getInstance("BetaVariate", paramsU_DNC[0], paramsU_DNC[1]));
		
		if (!DISABLE_SHE) {
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, she, 
					"GOLD", P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1]));
		}
		
		registerNPHParameters(secParams);
		registerNEUParameters(secParams);
		registerRETParameters(secParams);
		registerCHDParameters(secParams);
	}

	private void registerNPHParameters(SecondOrderParamsRepository secParams) {
		if (!DISABLE_NPH) {
			// Adds parameters to compute HbA1c-dependent progressions for nephropathy-related complications 
			OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, alb1, "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", BETA_ALB1); 
			OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, alb2, "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", BETA_ALB2); 
	
			// Add transition probabilities for nephropathy-related complications
			final double[] paramsALB1_ESRD = Statistics.betaParametersFromNormal(P_ALB1_ESRD, Statistics.sdFrom95CI(CI_ALB1_ESRD));
			final double[] paramsDNC_ALB1 = Statistics.betaParametersFromNormal(P_DNC_ALB1, Statistics.sdFrom95CI(CI_DNC_ALB1));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb1, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_DNC_ALB1, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_ALB1[0], paramsDNC_ALB1[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb2, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_DNC_ALB2, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_ALB2));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb1, esrd, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB1_ESRD, RandomVariateFactory.getInstance("BetaVariate", paramsALB1_ESRD[0], paramsALB1_ESRD[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb2, esrd, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB2_ESRD, SecondOrderParamsRepository.getRandomVariateForProbability(P_ALB2_ESRD));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb1, alb2, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB1_ALB2, SecondOrderParamsRepository.getRandomVariateForProbability(P_ALB1_ALB2));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, esrd, 
					"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
					P_DNC_ESRD, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_ESRD));
		}
		
	}

	private void registerNEUParameters(SecondOrderParamsRepository secParams) {
		if (!DISABLE_NEU) {
			// Adds parameters to compute HbA1c-dependent progressions for neuropathy-related complications 
			OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, neu, 
					"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", BETA_NEU);
			
			// Add transition probabilities for neuropathy-related complications
			final double[] paramsDNC_NEU = Statistics.betaParametersFromNormal(P_DNC_NEU, Statistics.sdFrom95CI(CI_DNC_NEU));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, neu, 
					"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
					P_DNC_NEU, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, lea,
					"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
					P_DNC_LEA, RandomVariateFactory.getInstance("UniformVariate", LIMITS_DNC_LEA[0], LIMITS_DNC_LEA[1]));
			final double[] paramsNEU_LEA = Statistics.betaParametersFromNormal(P_NEU_LEA, Statistics.sdFrom95CI(CI_NEU_LEA));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, neu, lea, "Klein et al. 2004 (also Sheffield)", 
					P_NEU_LEA, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1]));
			
			if (!DISABLE_NPH) {
				final double[] paramsNEU_ALB1 = Statistics.betaParametersFromNormal(P_NEU_ALB1, Statistics.sdFrom95CI(CI_NEU_ALB1));
				RiskParamDescriptions.PROBABILITY.addParameter(secParams, neu, alb1, "", 
						P_NEU_ALB1, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_ALB1[0], paramsNEU_ALB1[1]));				
			}
		}
	}
	
	private void registerRETParameters(SecondOrderParamsRepository secParams) {
		if (!DISABLE_RET) {
			// Adds parameters to compute HbA1c-dependent progressions for retinopathy-related complications 
			OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, bgret,	"WESDR XXII, as adapted by Sheffield", BETA_BGRET);
			OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, pret,	"WESDR XXII, as adapted by Sheffield", BETA_PRET);
			OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, me,	"WESDR XXII, as adapted by Sheffield", BETA_ME);

			// Add transition probabilities for retinopathy-related complications
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret,
					"Sheffield (WESDR XXII)", P_DNC_BGRET, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_BGRET));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, pret, 
					"Sheffield (WESDR XXII)", P_DNC_PRET, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_PRET));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, me, 
					"Sheffield (WESDR XXII)", P_DNC_ME, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_ME));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret, pret, 
					"Sheffield (WESDR XXII)", P_BGRET_PRET, SecondOrderParamsRepository.getRandomVariateForProbability(P_BGRET_PRET));			
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret, me,
					"Sheffield (WESDR XXII)", P_BGRET_ME, SecondOrderParamsRepository.getRandomVariateForProbability(P_BGRET_ME));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret, bli,  
					"Sheffield (WESDR XXII)", P_BGRET_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_BGRET_BLI));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, pret, bli, 
					"Sheffield (WESDR XXII)", P_PRET_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_PRET_BLI));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, me, bli,  
					"Sheffield (WESDR XXII)", P_ME_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_ME_BLI));			
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bli, 
					"Sheffield (WESDR XXII)", P_DNC_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_BLI));			
		}
	}
	
	private void registerCHDParameters(SecondOrderParamsRepository secParams) {
		if (!DISABLE_CHD) {
			final double[] paramsDNC_CHD = Statistics.betaParametersFromNormal(P_DNC_CHD, Statistics.sdFrom95CI(CI_DNC_CHD));
			final double[] paramsNEU_CHD = Statistics.betaParametersFromNormal(P_NEU_CHD, Statistics.sdFrom95CI(CI_NEU_CHD));
			final double[] paramsNPH_CHD = Statistics.betaParametersFromNormal(P_NPH_CHD, Statistics.sdFrom95CI(CI_NPH_CHD));
			final double[] paramsRET_CHD = Statistics.betaParametersFromNormal(P_RET_CHD, Statistics.sdFrom95CI(CI_RET_CHD));		

			// All these parameters are generic for any CHD-related manifestation
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, "CHD", "no complication to any CHD manifestation", "Hoerger (2004)", 
					P_DNC_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, "NEU_CHD", "neuropathy to any CHD manifestation", "Klein (2004)", 
					P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, "NPH_CHD", "nephropathy to any CHD manifestation", "Klein (2004)", 
					P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, "RET_CHD", "retinopathy to any CHD manifestation", "Klein (2004)", 
					P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1]));
			
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, mi,  
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", P_CHD_MI, RandomVariateFactory.getInstance("GammaVariate", 1.0, P_CHD_MI));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, stroke, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", P_CHD_STROKE, RandomVariateFactory.getInstance("GammaVariate", 1.0, P_CHD_STROKE));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, hf, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", P_CHD_HF, RandomVariateFactory.getInstance("GammaVariate", 1.0, P_CHD_HF));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, angina, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", P_CHD_ANGINA, RandomVariateFactory.getInstance("GammaVariate", 1.0, P_CHD_ANGINA));
			
			OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, "CHD",	"CHD-related complication, associated to a 1 PP increment of HbA1c",
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 
					RR_CHD, RandomVariateFactory.getInstance("RRFromLnCIVariate", RR_CHD, CI_RR_CHD[0], CI_RR_CHD[1], 1));
		}
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO
		double [] results = new double[(int)endT - (int)initT + 1];
		return results;
	}

	/**
	 * Computes the RR according to the Sheffiled's method
	 * 
	 * They assume a probability for HbA1c level = 10% (p_10), so that p_h = p_10 X (h/10)^beta, where "h" is the new HbA1c level.
	 * As a consequence, RR = p_h/p_10 = (h/10)^beta
	 *   
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public static class SheffieldRRCalculator implements ParameterCalculator {
		private final String paramName;
		private final SecondOrderParamsRepository secParams;
		/**
		 * Creates a relative risk computed as described in the Sheffield's T1DM model
		 */
		public SheffieldRRCalculator(SecondOrderParamsRepository secParams, String paramName) {
			this.paramName = paramName;
			this.secParams = secParams;
		}

		@Override
		public double getValue(Patient pat) {
			final double beta = OtherParamDescriptions.RELATIVE_RISK.getValue(secParams, paramName, pat);
			return Math.pow(pat.getAttributeValue(T1DMRepository.STR_HBA1C).doubleValue()/10.0, beta);
		}
	}
	
	/**
	 * Computes the RR according to Selvin et al 2004. There, they associated a relative risk to a 1 percentage point increment of HbA1c.
	 * Lets HbA1c_0 be the reference HbA1c level.
	 * Lets p_0 be the probability of the complication for HbA1c_0.
	 * Lets consider a new level of HbA1c, HbA1c_k = HbA1c_0 + k. p_k would be the probability of complication for that level.
	 * Lets RR_0 be the relative risk associated to a 1 PP increment of HbA1c, i.e., p_1 = p_0 X RR_0
	 * 
	 * Then
	 * RR_k = p_k / p_0 = RR_0^k
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public static class HbA1c1ppRRCalculator implements ParameterCalculator {
		/** The reference HbA1c from which the relative risk is applied */
		private static final double REF_HBA1C = 9.1; 
		private final SecondOrderParamsRepository secParams;

		/**
		 * Creates a relative risk associated  to a 1 percentage point increment of HbA1c
		 */
		public HbA1c1ppRRCalculator(SecondOrderParamsRepository secParams) {
			this.secParams = secParams;
		}

		@Override
		public double getValue(Patient pat) {
			// Gets The relative risk of the complication, associated to a 1 PP increment of HbA1c
			final double referenceRR = OtherParamDescriptions.RELATIVE_RISK.getValue(secParams, "CHD", pat);
			final double diff = pat.getAttributeValue(T1DMRepository.STR_HBA1C).doubleValue() - REF_HBA1C;
			return Math.pow(referenceRR, diff);
		}
		
	}
}
