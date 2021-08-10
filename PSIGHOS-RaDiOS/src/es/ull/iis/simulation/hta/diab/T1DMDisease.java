/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.manifestations.EndStageRenalDisease;
import es.ull.iis.simulation.hta.diab.manifestations.LowExtremityAmputation;
import es.ull.iis.simulation.hta.diab.manifestations.Macroalbuminuria;
import es.ull.iis.simulation.hta.diab.manifestations.Microalbuminuria;
import es.ull.iis.simulation.hta.diab.manifestations.Neuropathy;
import es.ull.iis.simulation.hta.diab.manifestations.SevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.StagedDisease;
import es.ull.iis.simulation.hta.progression.Transition;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class T1DMDisease extends StagedDisease {
	public static class DEF_C_DNC {
		/** Value computed by substracting the burden of complications from the global burden of DM1 in Spain; 
		 * finally divided by the prevalent DM1 population */
		public final static double VALUE = (5809000000d - 2143000000d) / 3282790d;
		/** The year of the default cost for diabetes with no complications: for updating with IPC */
		public final static int YEAR = 2012; 
		/** Description of the source */
		public final static String SOURCE = "Crespo et al. 2012: http://dx.doi.org/10.1016/j.avdiab.2013.07.007";
	}
	public final static double[] DEF_DU_DNC = {T1DMRepository.DEF_U_GENERAL_POP - 0.785, ((0.889 - 0.681) / 3.92)};
	
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

	private static final double P_DNC_NEU = 0.0354;
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double P_DNC_LEA = 0.0003;
	private static final double[] CI_DNC_NEU = {0.020, 0.055}; // McQueen
	private static final double[] LIMITS_DNC_LEA = {0.0, 0.0006}; // Assumption
	private static final double[] CI_NEU_LEA = {0.01232, 0.01848};
	private static final double BETA_NEU = 5.3;
	/** Beta parameters (cases, no cases) for the initial proportion of lower amputation, according to the GOLD study */
	private static final double []P_INI_LEA_BETA = {1, 300-1}; 

	private static final double P_NEU_ALB1 = 0.097;
	private static final double[] CI_NEU_ALB1 = {0.055, 0.149};

	/** Mean probability of hypoglycemic events in GOLD study (adjusted from annual rate */
	private static final double P_HYPO = 0.0706690;
	private static final double []P_HYPO_BETA = {9.9643, 131.0357};

	final private Manifestation she;
	final private Manifestation alb1;
	final private Manifestation alb2;
	final private Manifestation esrd;
	final private Manifestation neu;
	final private Manifestation lea;

	private static final boolean DISABLE_NEU = true;
	private static final boolean DISABLE_NPH = true;
	private static final boolean DISABLE_SHE = false;
	
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
			she = addManifestation(new SevereHypoglycemiaEvent(secParams, this));
			addTransition(new Transition(secParams, getAsymptomaticManifestation(), she));
			// FIXME: Terminar y comprobar qué ocurre cuando ya se tiene una manifestación crónica. ¿Seguiría lanzándose este evento agudo?
		}
		
		if (DISABLE_NPH) {
			alb1 = null;
			alb2 = null;
			esrd = null;
		}
		else {
			// Register and configure Nephropathy-related manifestations
			alb1 = addManifestation(new Microalbuminuria(secParams, this));
			alb2 = addManifestation(new Macroalbuminuria(secParams, this));
			esrd = addManifestation(new EndStageRenalDisease(secParams, this));
			final Transition toAlb1 = addTransition(new Transition(secParams, getAsymptomaticManifestation(), alb1));
			toAlb1.setCalculator(toAlb1.new AnnualRiskBasedTimeToEventCalculator(new SheffieldComplicationRR(getAsymptomaticManifestation(), alb1)));
			final Transition toAlb2 = addTransition(new Transition(secParams, getAsymptomaticManifestation(), alb2));
			toAlb2.setCalculator(toAlb2.new AnnualRiskBasedTimeToEventCalculator(new SheffieldComplicationRR(getAsymptomaticManifestation(), alb2)));
			final Transition alb1ToAlb2 = addTransition(new Transition(secParams, alb1, alb2));
			alb1ToAlb2.setCalculator(alb1ToAlb2.new AnnualRiskBasedTimeToEventCalculator(new SheffieldComplicationRR(getAsymptomaticManifestation(), alb2)));
			// Do not use any additional risk
			addTransition(new Transition(secParams, getAsymptomaticManifestation(), esrd));
			addTransition(new Transition(secParams, alb1, esrd));
			addTransition(new Transition(secParams, alb2, esrd));
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
			neu = addManifestation(new Neuropathy(secParams, this));
			lea = addManifestation(new LowExtremityAmputation(secParams, this));
			final Transition toNeu = addTransition(new Transition(secParams, getAsymptomaticManifestation(), neu));
			toNeu.setCalculator(toNeu.new AnnualRiskBasedTimeToEventCalculator(new SheffieldComplicationRR(getAsymptomaticManifestation(), neu)));
			addTransition(new Transition(secParams, getAsymptomaticManifestation(), lea));
			addTransition(new Transition(secParams, neu, lea));
			addExclusion(lea, neu);
			if (!DISABLE_NPH)
				// There is an additional risk to progress from neuropathy to microalbuminuria
				addTransition(new Transition(secParams, neu, alb1));
		}
	}

	@Override
	public void registerSecondOrderParameters() {
		// Set asymptomatic cost and disutility 
		secParams.addCostParam(new SecondOrderCostParam(secParams, SecondOrderParamsRepository.STR_COST_PREFIX + "DNC", "Cost of Diabetes with no complications", 
				DEF_C_DNC.SOURCE, DEF_C_DNC.YEAR, 
				DEF_C_DNC.VALUE, SecondOrderParamsRepository.getRandomVariateForCost(DEF_C_DNC.VALUE)));

		final double[] paramsduDNC = Statistics.betaParametersFromNormal(DEF_DU_DNC[0], DEF_DU_DNC[1]);
		secParams.addDisutilityParam(this, "Disutility of DNC", "", DEF_DU_DNC[0], RandomVariateFactory.getInstance("BetaVariate", paramsduDNC[0], paramsduDNC[1]));
		
		if (!DISABLE_SHE) {
			secParams.addProbParam(getAsymptomaticManifestation(), she, 
					"GOLD", P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1]));
		}
		
		if (!DISABLE_NPH) {
			// Adds parameters to compute HbA1c-dependent progressions for nephropathy-related complications 
			secParams.addRRParam(getAsymptomaticManifestation(), alb1, 
					"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", BETA_ALB1); 
			secParams.addRRParam(getAsymptomaticManifestation(), alb2, 
					"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", BETA_ALB2); 
	
			// Add transition probabilities for nephropathy-related complications
			final double[] paramsALB1_ESRD = Statistics.betaParametersFromNormal(P_ALB1_ESRD, Statistics.sdFrom95CI(CI_ALB1_ESRD));
			final double[] paramsDNC_ALB1 = Statistics.betaParametersFromNormal(P_DNC_ALB1, Statistics.sdFrom95CI(CI_DNC_ALB1));
			secParams.addProbParam(getAsymptomaticManifestation(), alb1, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_DNC_ALB1, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_ALB1[0], paramsDNC_ALB1[1]));
			secParams.addProbParam(getAsymptomaticManifestation(), alb2, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_DNC_ALB2, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_ALB2));
			secParams.addProbParam(alb1, esrd, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB1_ESRD, RandomVariateFactory.getInstance("BetaVariate", paramsALB1_ESRD[0], paramsALB1_ESRD[1]));
			secParams.addProbParam(alb2, esrd, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB2_ESRD, SecondOrderParamsRepository.getRandomVariateForProbability(P_ALB2_ESRD));
			secParams.addProbParam(alb1, alb2, 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
					P_ALB1_ALB2, SecondOrderParamsRepository.getRandomVariateForProbability(P_ALB1_ALB2));
			secParams.addProbParam(getAsymptomaticManifestation(), esrd, 
					"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
					P_DNC_ESRD, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_ESRD));
		}

		if (!DISABLE_NEU) {
			// Adds parameters to compute HbA1c-dependent progressions for neuropathy-related complications 
			secParams.addRRParam(getAsymptomaticManifestation(), neu, 
					"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", BETA_NEU);
			
			// Add transition probabilities for neuropathy-related complications
			final double[] paramsDNC_NEU = Statistics.betaParametersFromNormal(P_DNC_NEU, Statistics.sdFrom95CI(CI_DNC_NEU));
			secParams.addProbParam(getAsymptomaticManifestation(), neu, 
					"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
					P_DNC_NEU, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1]));
			secParams.addProbParam(getAsymptomaticManifestation(), lea,
					"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
					P_DNC_LEA, RandomVariateFactory.getInstance("UniformVariate", LIMITS_DNC_LEA[0], LIMITS_DNC_LEA[1]));
			final double[] paramsNEU_LEA = Statistics.betaParametersFromNormal(P_NEU_LEA, Statistics.sdFrom95CI(CI_NEU_LEA));
			secParams.addProbParam(neu, lea, 
					"Klein et al. 2004 (also Sheffield)", 
					P_NEU_LEA, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1]));
			
			secParams.addInitProbParam(lea, "GOLD", P_INI_LEA_BETA[0] / (P_INI_LEA_BETA[0] + P_INI_LEA_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_LEA_BETA[0], P_INI_LEA_BETA[1]));
			
			if (!DISABLE_NPH) {
				final double[] paramsNEU_ALB1 = Statistics.betaParametersFromNormal(P_NEU_ALB1, Statistics.sdFrom95CI(CI_NEU_ALB1));
				secParams.addProbParam(neu, alb1, 
						"", 
						P_NEU_ALB1, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_ALB1[0], paramsNEU_ALB1[1]));				
			}
		}
		
	}

	@Override
	public double getDiagnosisCost(Patient pat) {
		return 0;
	}

	@Override
	public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
		return secParams.getCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + "DNC", pat.getSimulation());
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
	public class SheffieldComplicationRR implements es.ull.iis.simulation.hta.params.RRCalculator {
		final private Manifestation srcManifestation;
		final private Manifestation destManifestation;
		/**
		 * Creates a relative risk computed as described in the Sheffield's T1DM model
		 */
		public SheffieldComplicationRR(Manifestation srcManifestation, Manifestation destManifestation) {
			this.srcManifestation = srcManifestation;
			this.destManifestation = destManifestation;
		}

		@Override
		public double getRR(Patient pat) {
			final double beta = secParams.getRR(srcManifestation, destManifestation, pat.getSimulation());
			return Math.pow(pat.getProfile().getPropertyValue(T1DMRepository.STR_HBA1C, pat).doubleValue()/10.0, beta);
		}
	}
	
}
