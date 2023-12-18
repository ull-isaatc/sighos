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
import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.ParameterDescription;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
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
			addProgression(she);
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
			addProgression(alb1);
			addProgression(alb2);
			addProgression(alb1, alb2);
			addProgression(esrd);
			addProgression(alb1, esrd);
			addProgression(alb2, esrd);
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
			addProgression(neu);
			addProgression(lea);
			addProgression(neu, lea);
			// Define exclusive manifestations
			addExclusion(lea, neu);
			if (!DISABLE_NPH) {
				// There is an additional risk to progress from neuropathy to microalbuminuria
				addProgression(neu, alb1);
				addProgression(lea, alb1);
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
			addProgression(bgret);
			addProgression(pret);
			addProgression(bgret, pret);
			addProgression(me);
			addProgression(bgret, me);
			addProgression(pret, me);
			addProgression(bli);
			addProgression(bgret, bli);
			addProgression(pret, bli);
			addProgression(me, bli);
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
			// The destination is the stage: CHD 
			addProgression(chd);

			final TreeMap<DiseaseProgression, String> mapping = new TreeMap<>();
			for (DiseaseProgression manifCHD : getLabeledManifestations(GroupOfManifestations.CHD)) 
				mapping.put(manifCHD, RiskParamDescriptions.PROBABILITY.getParameterName(manifCHD));
			final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new AndCondition<>(
					new ExclusiveChoiceCondition(secParams, mapping), 
					new PreviousDiseaseProgressionCondition(chd));
			// And now adds an instantaneous transition to the specific manifestation
			final String paramName = RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, "0_TIME", "Instantaneous transition", "", 0.0);
			for (DiseaseProgression manifCHD : getLabeledManifestations(GroupOfManifestations.CHD))
				new DiseaseProgressionPathway(secParams, manifCHD, cond, paramName);
			
			
			if (!DISABLE_NEU) {
				addProgression(neu, chd);
				addProgression(lea, chd);
			}
			if (!DISABLE_NPH) {
				addProgression(alb1, chd);
				addProgression(alb2, chd);
				addProgression(esrd, chd);
			}
			if (!DISABLE_RET) {
				addProgression(bgret, chd);
				addProgression(pret, chd);
				addProgression(me, chd);
				addProgression(bli, chd);
			}
		}
	}

	private void addProgression(DiseaseProgression fromManif, DiseaseProgression toManif) {
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(fromManif);
		new DiseaseProgressionPathway(getRepository(), toManif, cond, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(fromManif, toManif));
	}

	private void addProgression(DiseaseProgression toManif) {
		new DiseaseProgressionPathway(getRepository(), toManif, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(toManif));
	}

	private void registerStandardTimeToEventParameter(SecondOrderParamsRepository secParams, DiseaseProgression toManif) {
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(toManif), toManif, 
				RiskParamDescriptions.PROBABILITY.getParameterName(toManif)));		
	}
	
	private void registerStandardTimeToEventParameter(SecondOrderParamsRepository secParams, DiseaseProgression fromManif, DiseaseProgression toManif) {
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(fromManif, toManif), toManif, 
				RiskParamDescriptions.PROBABILITY.getParameterName(fromManif, toManif)));
	}
	
	private String registerSheffieldTimeToEventParameter(SecondOrderParamsRepository secParams, DiseaseProgression toManif, String betaParamName) {
		final String rrParamName = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, new SheffieldRRParameter(secParams, betaParamName));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(toManif), toManif, 
				RiskParamDescriptions.PROBABILITY.getParameterName(toManif), rrParamName));
		return rrParamName;
	}
	private String registerSheffieldTimeToEventParameter(SecondOrderParamsRepository secParams, DiseaseProgression fromManif, DiseaseProgression toManif, String betaParamName) {
		final String rrParamName = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, new SheffieldRRParameter(secParams, betaParamName));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(fromManif, toManif), toManif, 
				RiskParamDescriptions.PROBABILITY.getParameterName(fromManif, toManif), rrParamName));
		return rrParamName;
	}
	
	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		
		// Set asymptomatic follow-up cost and disutility. Treatment cost for asymptomatics is assumed to be 0 
		CostParamDescriptions.FOLLOW_UP_COST.addParameter(secParams, this, "Diabetes with no complications", 
				DEF_C_DNC.SOURCE, DEF_C_DNC.YEAR, DEF_C_DNC.VALUE, StandardParameter.getRandomVariateForCost(DEF_C_DNC.VALUE));

		final double[] paramsU_DNC = Statistics.betaParametersFromNormal(DEF_U_DNC[0], DEF_U_DNC[1]);
		UtilityParamDescriptions.UTILITY.addParameter(secParams, this, "", DEF_U_DNC[0], RandomVariateFactory.getInstance("BetaVariate", paramsU_DNC[0], paramsU_DNC[1]));
		
		if (!DISABLE_SHE) {
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, she, 
					"GOLD", P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1]));
			registerStandardTimeToEventParameter(secParams, she);
		}
		
		registerNPHParameters(secParams);
		registerNEUParameters(secParams);
		registerRETParameters(secParams);
		registerCHDParameters(secParams);
	}

	private void registerNPHParameters(SecondOrderParamsRepository secParams) {
		if (!DISABLE_NPH) {
			// Adds parameters to compute HbA1c-dependent progressions for nephropathy-related complications 
			final String paramBetaALB1 = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, alb1, "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", BETA_ALB1); 
			final String paramBetaALB2 = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, alb2, "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", BETA_ALB2);
	
			// Add transition probabilities for nephropathy-related complications
			final double[] paramsALB1_ESRD = Statistics.betaParametersFromNormal(P_ALB1_ESRD, Statistics.sdFrom95CI(CI_ALB1_ESRD));
			final double[] paramsDNC_ALB1 = Statistics.betaParametersFromNormal(P_DNC_ALB1, Statistics.sdFrom95CI(CI_DNC_ALB1));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb1, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_DNC_ALB1, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_ALB1[0], paramsDNC_ALB1[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb2, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_DNC_ALB2, StandardParameter.getRandomVariateForProbability(P_DNC_ALB2));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb1, esrd, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB1_ESRD, RandomVariateFactory.getInstance("BetaVariate", paramsALB1_ESRD[0], paramsALB1_ESRD[1]));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb2, esrd, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB2_ESRD, StandardParameter.getRandomVariateForProbability(P_ALB2_ESRD));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb1, alb2, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB1_ALB2, StandardParameter.getRandomVariateForProbability(P_ALB1_ALB2));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, esrd, 
					"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
					P_DNC_ESRD, StandardParameter.getRandomVariateForProbability(P_DNC_ESRD));
			
			registerSheffieldTimeToEventParameter(secParams, alb1, paramBetaALB1);
			registerSheffieldTimeToEventParameter(secParams, alb2, paramBetaALB2);
			registerSheffieldTimeToEventParameter(secParams, alb1, alb2, paramBetaALB2);
			registerStandardTimeToEventParameter(secParams, esrd);
			registerStandardTimeToEventParameter(secParams, alb1, esrd);
			registerStandardTimeToEventParameter(secParams, alb2, esrd);
		}
		
	}

	private void registerNEUParameters(SecondOrderParamsRepository secParams) {
		if (!DISABLE_NEU) {
			// Adds parameters to compute HbA1c-dependent progressions for neuropathy-related complications 
			final String paramBetaNEU = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, neu, 
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

			registerSheffieldTimeToEventParameter(secParams, neu, paramBetaNEU);
			registerStandardTimeToEventParameter(secParams, lea);
			registerStandardTimeToEventParameter(secParams, neu, lea);
			
			if (!DISABLE_NPH) {
				final double[] paramsNEU_ALB1 = Statistics.betaParametersFromNormal(P_NEU_ALB1, Statistics.sdFrom95CI(CI_NEU_ALB1));
				RiskParamDescriptions.PROBABILITY.addParameter(secParams, neu, alb1, "", 
						P_NEU_ALB1, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_ALB1[0], paramsNEU_ALB1[1]));				
				registerStandardTimeToEventParameter(secParams, neu, alb1);
				// Manually adds a second extra risk from LEA, which uses the same probability as the other progression
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(lea, alb1), alb1, 
						RiskParamDescriptions.PROBABILITY.getParameterName(neu, alb1)));
			}
		}
	}
	
	private void registerRETParameters(SecondOrderParamsRepository secParams) {
		if (!DISABLE_RET) {
			// Adds parameters to compute HbA1c-dependent progressions for retinopathy-related complications 
			final String paramBetaBGRET = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, bgret,	"WESDR XXII, as adapted by Sheffield", BETA_BGRET);
			final String paramBetaPRET = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, pret,	"WESDR XXII, as adapted by Sheffield", BETA_PRET);
			final String paramBetaME = OtherParamDescriptions.RELATIVE_RISK.addParameter(secParams, me,	"WESDR XXII, as adapted by Sheffield", BETA_ME);

			// Add transition probabilities for retinopathy-related complications
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret,
					"Sheffield (WESDR XXII)", P_DNC_BGRET, StandardParameter.getRandomVariateForProbability(P_DNC_BGRET));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, pret, 
					"Sheffield (WESDR XXII)", P_DNC_PRET, StandardParameter.getRandomVariateForProbability(P_DNC_PRET));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, me, 
					"Sheffield (WESDR XXII)", P_DNC_ME, StandardParameter.getRandomVariateForProbability(P_DNC_ME));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret, pret, 
					"Sheffield (WESDR XXII)", P_BGRET_PRET, StandardParameter.getRandomVariateForProbability(P_BGRET_PRET));			
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret, me,
					"Sheffield (WESDR XXII)", P_BGRET_ME, StandardParameter.getRandomVariateForProbability(P_BGRET_ME));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret, bli,  
					"Sheffield (WESDR XXII)", P_BGRET_BLI, StandardParameter.getRandomVariateForProbability(P_BGRET_BLI));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, pret, bli, 
					"Sheffield (WESDR XXII)", P_PRET_BLI, StandardParameter.getRandomVariateForProbability(P_PRET_BLI));
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, me, bli,  
					"Sheffield (WESDR XXII)", P_ME_BLI, StandardParameter.getRandomVariateForProbability(P_ME_BLI));			
			RiskParamDescriptions.PROBABILITY.addParameter(secParams, bli, 
					"Sheffield (WESDR XXII)", P_DNC_BLI, StandardParameter.getRandomVariateForProbability(P_DNC_BLI));			

			registerSheffieldTimeToEventParameter(secParams, bgret, paramBetaBGRET);
			registerSheffieldTimeToEventParameter(secParams, pret, paramBetaPRET);
			registerSheffieldTimeToEventParameter(secParams, bgret, pret, paramBetaPRET);
			registerSheffieldTimeToEventParameter(secParams, me, paramBetaME);
			final String rrParamName = registerSheffieldTimeToEventParameter(secParams, bgret, me, paramBetaME);
			// The time to event to ME from PRET uses the same risk than BGRET, in case BGRET is ommited 
			RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(pret, me), me, 
					RiskParamDescriptions.PROBABILITY.getParameterName(bgret, me), rrParamName));
			registerStandardTimeToEventParameter(secParams, bli);
			registerStandardTimeToEventParameter(secParams, bgret, bli);
			registerStandardTimeToEventParameter(secParams, pret, bli);
			registerStandardTimeToEventParameter(secParams, me, bli);
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
			
			final Parameter rrCHD = new HbA1c1ppRRParameter(secParams);
			RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(chd), chd, 
					RiskParamDescriptions.PROBABILITY.getParameterName(chd), rrCHD.name()));
			if (!DISABLE_NEU) {
				RiskParamDescriptions.PROBABILITY.addParameter(secParams, neu, chd, "Klein (2004)",  
						P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1]));
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(neu, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(neu, chd), rrCHD.name()));
				// Defines a different time to event parameter that uses the same probability
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(lea, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(neu, chd), rrCHD.name()));
			}
			if (!DISABLE_NPH) {
				RiskParamDescriptions.PROBABILITY.addParameter(secParams, alb1, chd, "Klein (2004)", 
						P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1]));
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(alb1, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(alb1, chd), rrCHD.name()));
				// Defines a different time to event parameter that uses the same probability
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(alb2, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(alb1, chd), rrCHD.name()));
				// Defines a different time to event parameter that uses the same probability
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(esrd, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(alb1, chd), rrCHD.name()));
			}
			if (!DISABLE_RET) {
				RiskParamDescriptions.PROBABILITY.addParameter(secParams, bgret, chd, "Klein (2004)", 
						P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1]));
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(bgret, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(bgret, chd), rrCHD.name()));
				// Defines a different time to event parameter that uses the same probability
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(pret, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(bgret, chd), rrCHD.name()));
				// Defines a different time to event parameter that uses the same probability
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(me, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(bgret, chd), rrCHD.name()));
				// Defines a different time to event parameter that uses the same probability
				RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(bli, chd), chd, 
						RiskParamDescriptions.PROBABILITY.getParameterName(bgret, chd), rrCHD.name()));
			}
		}
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO
		double [] results = new double[(int)endT - (int)initT + 1];
		return results;
	}

	/**
	 * A parameter that computes the RR according to the Sheffiled's method
	 * 
	 * They assume a probability for HbA1c level = 10% (p_10), so that p_h = p_10 X (h/10)^beta, where "h" is the new HbA1c level.
	 * As a consequence, RR = p_h/p_10 = (h/10)^beta
	 *   
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public static class SheffieldRRParameter extends Parameter {
		private final String betaParamName;
		public SheffieldRRParameter(SecondOrderParamsRepository secParams, String betaParamName) {
			super(secParams, "SH_" + betaParamName, new ParameterDescription("Sheffield-based method to compute RR from a beta and HbA1c level", "Sheffield report"));
			this.betaParamName = betaParamName;
		}
		@Override
		public double getValue(Patient pat) {
			final double beta = getRepository().getParameterValue(betaParamName, pat);
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
	public static class HbA1c1ppRRParameter extends Parameter {
		/** The reference HbA1c from which the relative risk is applied */
		private static final double REF_HBA1C = 9.1; 

		/**
		 * Creates a relative risk associated  to a 1 percentage point increment of HbA1c
		 */
		public HbA1c1ppRRParameter(SecondOrderParamsRepository secParams) {
			super(secParams, "RR_CHD", new ParameterDescription("Relative risk associated to a 1 percentage point increment of HbA1c", "Selvin et al 2004"));
		}

		@Override
		public double getValue(Patient pat) {
			// Gets The relative risk of the complication, associated to a 1 PP increment of HbA1c
			final double referenceRR = OtherParamDescriptions.RELATIVE_RISK.getValue(getRepository(), "CHD", pat);
			final double diff = pat.getAttributeValue(T1DMRepository.STR_HBA1C).doubleValue() - REF_HBA1C;
			return Math.pow(referenceRR, diff);
		}
		
	}
}
