/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToMultipleEventParam;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.DeathWithEventParam;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.util.Statistics;
import simkit.random.DirichletBetaVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * An hypoglycemic event, similarly to how the PROSIT model deals with it. 
 * @author Iván Castilla Rodríguez
 *
 */
public class T2DMPrositSevereHypoglycemiaEvent extends SecondOrderAcuteComplicationSubmodel {
	public static final String STR_P_DEATH_HYPO = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + SecondOrderParamsRepository.STR_DEATH_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_RR_HYPO = SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesAcuteComplications.SHE.name(); 
	public static final String STR_COST_HYPO_EPISODE = SecondOrderParamsRepository.STR_COST_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_DU_HYPO_EVENT = SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + DiabetesAcuteComplications.SHE.name();
	public static final String STR_BASE_RATE_SHE = "RATE_" + DiabetesAcuteComplications.SHE.name();
	public static final String[] STR_AWARENESS = {"ALWAYS", "IMPAIRED", "UNAWARE"};
	public static final String STR_REF_HBA1C = "REF_HBA1C";

	/** Relative risk for 1 % increment in basal HbA1c level. From DCCT: 10.1016/0002-9343(91)80085-Z */ 
	private static final double RR_HYPO = 1.2;
	private static final double[] CI_RR_HYPO = {1.04, 1.39};
	
	private static final double[] REF_HBA1C = {9.4, 2.7};
	private static final double[] MIN_MAX_REF_HBA1C = {4, 14}; // Assumption
	/** Rates of severe hypoglycemic events per awareness (always, impaired, unaware), according to Orozco-Beltrán et al. 10.1007/s13300-014-0057-z.
	 * Each pair is {average, SD} */
	private static final double[][] RATE_SHE_PER_AWARENESS = {{0.23, 1.31}, {0.61, 1.25}, {0.79, 2.53}};
	private static final double[] N_SHE_PER_AWARENESS = {150, 62, 34};
	
	/** Average cost of a severe hypoglycemia event in € 2017 according to 10.1007/s13300-017-0285-0 */
	private static final double COST_HYPO_EPISODE = 716.82;
	private static final double DU_HYPO_EPISODE = BasicConfigParams.USE_REVIEW_UTILITIES ? 0.047 : 0.0206; // From Canada
	private static final double[] LIMITS_DU_HYPO_EPISODE = {BasicConfigParams.USE_REVIEW_UTILITIES ? 0.035 : 0.01, BasicConfigParams.USE_REVIEW_UTILITIES ? 0.059 : 0.122}; // From Canada
	private static final String STR_SOURCE_DCCT = "DCCT: 10.1016/0002-9343(91)80085-Z";
	private static final String STR_SOURCE_OROZCO = "Orozco-Beltrán et al. 10.1007/s13300-014-0057-z";

	private static final double P_DEATH = 0.0063;
	
	private final DirichletBetaVariate sheAwarenessProportion;
	
	public T2DMPrositSevereHypoglycemiaEvent() {
		super(DiabetesAcuteComplications.SHE, EnumSet.of(DiabetesType.T2));
		sheAwarenessProportion = (DirichletBetaVariate)RandomVariateFactory.getInstance("DirichletBetaVariate", N_SHE_PER_AWARENESS);
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDeathHypo = Statistics.betaParametersFromNormal(P_DEATH, Statistics.sdFrom95CI(new double[]{0.0058, 0.0068}));
		final double[] initBetaParams = Statistics.betaParametersFromNormal(REF_HBA1C[0], REF_HBA1C[1]);
		// k is used to simplify the operations
		final double k = ((initBetaParams[0] + initBetaParams[1])*(initBetaParams[0] + initBetaParams[1]))/initBetaParams[1];
		final double variance = REF_HBA1C[1] * REF_HBA1C[1];
		final double mode = variance * k * (initBetaParams[0] - 1) / (initBetaParams[0] - 3 * variance * k);
		final double[] paramsRefHbA1c = Statistics.betaParametersFromEmpiricData(REF_HBA1C[0], mode, MIN_MAX_REF_HBA1C[0], MIN_MAX_REF_HBA1C[1]);
		
		for (int i = 0; i < RATE_SHE_PER_AWARENESS.length; i++) {
			final double[] paramRateSHE = Statistics.gammaParametersFromNormal(RATE_SHE_PER_AWARENESS[i][0], RATE_SHE_PER_AWARENESS[i][1]);
			secParams.addProbParam(new SecondOrderParam(STR_BASE_RATE_SHE + STR_AWARENESS[i], "Annual rate of severe hypoglycemic episode", 
					STR_SOURCE_OROZCO, RATE_SHE_PER_AWARENESS[i][0], RandomVariateFactory.getInstance("GammaVariate", paramRateSHE[0], paramRateSHE[1])));
		}
				
		secParams.addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", 
				"Canada", P_DEATH, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		secParams.addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
				STR_SOURCE_DCCT, RR_HYPO, RandomVariateFactory.getInstance("RRFromLnCIVariate", RR_HYPO, CI_RR_HYPO[0], CI_RR_HYPO[1], 1)));
		RandomVariate betaHbA1c = RandomVariateFactory.getInstance("BetaVariate", paramsRefHbA1c[0], paramsRefHbA1c[1]);
		secParams.addOtherParam(new SecondOrderParam(STR_REF_HBA1C, "Reference HbA1c level for the basal risk of severe hypoglycemic event", 
				STR_SOURCE_OROZCO, REF_HBA1C[0], RandomVariateFactory.getInstance("ScaledVariate", betaHbA1c, MIN_MAX_REF_HBA1C[1] - MIN_MAX_REF_HBA1C[0], MIN_MAX_REF_HBA1C[0])));

		secParams.addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", 
				"https://doi.org/10.1007/s13300-017-0285-0", 2017, COST_HYPO_EPISODE, SecondOrderParamsRepository.getRandomVariateForCost(COST_HYPO_EPISODE)));

		secParams.addUtilParam(new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", 
				DU_HYPO_EPISODE, "UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]));
	}

	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		if (!isEnabled())
			return new DisabledAcuteComplicationInstance();
		
		final double[] proportion = sheAwarenessProportion.generateValues(true);
		final double[] rates = new double[RATE_SHE_PER_AWARENESS.length]; 
		double finalRate = 0.0; 
		for (int i = 0; i < RATE_SHE_PER_AWARENESS.length; i++) {
			rates[i] = secParams.getProbParam(STR_BASE_RATE_SHE + STR_AWARENESS[i]);
			finalRate += rates[i] * proportion[i];
		}
		double prob = 1 - Math.exp(finalRate);
		return new Instance(secParams, prob);
	}

	// For testing only
//	public static void main(String[] args) {
//		final int N = 1000;
//		final DirichletBetaVariate sheAwarenessProportion = (DirichletBetaVariate)RandomVariateFactory.getInstance("DirichletBetaVariate", N_SHE_PER_AWARENESS);
//		final RandomVariate[] rateFunctions = new RandomVariate[RATE_SHE_PER_AWARENESS.length];  
//		for (int i = 0; i < RATE_SHE_PER_AWARENESS.length; i++) {
//			final double[] paramRateSHE = SecondOrderParamsRepository.gammaParametersFromNormal(RATE_SHE_PER_AWARENESS[i][0], RATE_SHE_PER_AWARENESS[i][1]);
//			rateFunctions[i] = RandomVariateFactory.getInstance("GammaVariate", paramRateSHE[0], paramRateSHE[1]);
//		}
//		System.out.print("ID\tP\tRATE");
//		for (int i = 0; i < RATE_SHE_PER_AWARENESS.length; i++) {
//			System.out.print("\tPROP" + i + "\tRATE" + i);
//		}
//		System.out.println();
//		for (int exp = 0; exp < N; exp++) {
//			final double[] proportion = sheAwarenessProportion.generateValues(true);
//			final double[] rates = new double[RATE_SHE_PER_AWARENESS.length]; 
//			double finalRate = 0.0; 
//			for (int i = 0; i < RATE_SHE_PER_AWARENESS.length; i++) {
//				rates[i] = rateFunctions[i].generate();
//				finalRate += rates[i] * proportion[i];
//			}
//			double prob = 1 - Math.exp(-finalRate);
//			System.out.print(exp + "\t" + prob + "\t" + finalRate);
//			for (int i = 0; i < RATE_SHE_PER_AWARENESS.length; i++) {
//				System.out.print("\t" + proportion[i] + "\t" + rates[i]);
//			}
//			System.out.println();
//		}
//	}
	
	public class Instance extends AcuteComplicationSubmodel {
		private final double cost;
		private final double du;
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams, double prob) {
			super(DiabetesAcuteComplications.SHE, new AnnualRiskBasedTimeToMultipleEventParam(
					SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					secParams.getnPatients(), 
					prob,
					new HbA1c1PPComplicationRR(secParams.getOtherParam(STR_RR_HYPO), secParams.getOtherParam(STR_REF_HBA1C))), 
				new DeathWithEventParam(
					SecondOrderParamsRepository.getRNG_FIRST_ORDER(), 
					secParams.getnPatients(), 
					secParams.getProbParam(STR_P_DEATH_HYPO)));
			
			cost = secParams.getCostForAcuteComplication(DiabetesAcuteComplications.SHE);
			du = secParams.getDisutilityForAcuteComplication(DiabetesAcuteComplications.SHE);
		}
		
		@Override
		public double getCostOfComplication(DiabetesPatient pat) {
			return cost;
		}

		@Override
		public double getDisutility(DiabetesPatient pat) {
			return du;
		}
	}

	
}
