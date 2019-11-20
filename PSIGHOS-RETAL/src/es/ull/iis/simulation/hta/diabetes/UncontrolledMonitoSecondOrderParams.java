/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.populations.GoldDiamondPopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.BattelinoSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSpainDeathSubmodel;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A repository with data used to parameterize a model where:
 * <ul>
 * <li>Interventions: CGM vs SMBG</li>
 * <li>Discount rate: 3%</li>
 * <li>Complications included in the model: Depending on the value of {@link BasicConfigParams#USE_SIMPLE_MODELS}, the model uses
 * the following submodels
 * <ul>
 * <li>Retinopathy: {@link SheffieldRETSubmodel}.</li>
 * <li>Nephropathy: {@link SheffieldNPHSubmodel}.</li>
 * <li>Neuropathy: {@link SimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link SimpleCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link BattelinoSevereHypoglycemiaEvent}</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class UncontrolledMonitoSecondOrderParams extends SecondOrderParamsRepository {
	private static final double DU_HYPO_EPISODE = BasicConfigParams.USE_REVIEW_UTILITIES ? 0.047 : 0.0206; // From Canada
	private static final double[] LIMITS_DU_HYPO_EPISODE = {BasicConfigParams.USE_REVIEW_UTILITIES ? 0.035 : 0.01, BasicConfigParams.USE_REVIEW_UTILITIES ? 0.059 : 0.122}; // From Canada
	/** Mean probability of hypoglycemic events in GOLD study (adjusted from annual rate */
	private static final double P_HYPO = 0.0706690;
	private static final double []P_HYPO_BETA = {9.9643, 131.0357};
	/** Beta parameters (cases, no cases) for the initial proportion of proliferative retinopathy, according to the GOLD study */
	private static final double []P_INI_PRET_BETA = {28, 300-28}; 
	/** Beta parameters (cases, no cases) for the initial proportion of myocardial infarction, according to the GOLD study */
	private static final double []P_INI_MI_BETA = {3, 300-3}; 
	/** Beta parameters (cases, no cases) for the initial proportion of stroke, according to the GOLD study */
	private static final double []P_INI_STROKE_BETA = {2, 300-2}; 
	/** Beta parameters (cases, no cases) for the initial proportion of heart failure, according to the GOLD study */
	private static final double []P_INI_HF_BETA = {1, 300-1}; 
	/** Beta parameters (cases, no cases) for the initial proportion of lower amputation, according to the GOLD study */
	private static final double []P_INI_LEA_BETA = {1, 300-1}; 
	
	private final CGM_Intervention intCGM;
	private final SMBG_Intervention intSMBG;


	/**
	 * @param nPatients Number of patients to create
	 */
	public UncontrolledMonitoSecondOrderParams(int nPatients) {
		super(nPatients, new GoldDiamondPopulation());
		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
						"GOLD", P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])),
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "Relative risk of severe hypoglycemic event", "From meta", 1.0),
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", 
						DU_HYPO_EPISODE, "UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]),
					new SecondOrderCostParam(StandardSevereHypoglycemiaEvent.STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", 
						"https://doi.org/10.1007/s13300-017-0285-0", 2017, 716.82, SecondOrderParamsRepository.getRandomVariateForCost(716.82)),
					EnumSet.of(DiabetesType.T1)
				);
		registerComplication(hypoEvent);

		intSMBG = new SMBG_Intervention();
		intCGM = new CGM_Intervention();
		registerIntervention(intSMBG);
		registerIntervention(intCGM);
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		addProbParam(new SecondOrderParam(getInitProbString(SheffieldRETSubmodel.PRET), "Initial probability of proliferative retinopathy", "GOLD",
				P_INI_PRET_BETA[0] / (P_INI_PRET_BETA[0] + P_INI_PRET_BETA[1]), "BetaVariate", P_INI_PRET_BETA[0], P_INI_PRET_BETA[1])); 
		addProbParam(new SecondOrderParam(getInitProbString(SimpleCHDSubmodel.MI), "Initial probability of myocardial infarction", "GOLD",
				P_INI_MI_BETA[0] / (P_INI_MI_BETA[0] + P_INI_MI_BETA[1]), "BetaVariate", P_INI_MI_BETA[0], P_INI_MI_BETA[1])); 
		addProbParam(new SecondOrderParam(getInitProbString(SimpleCHDSubmodel.STROKE), "Initial probability of stroke", "GOLD",
				P_INI_STROKE_BETA[0] / (P_INI_STROKE_BETA[0] + P_INI_STROKE_BETA[1]), "BetaVariate", P_INI_STROKE_BETA[0], P_INI_STROKE_BETA[1])); 
		addProbParam(new SecondOrderParam(getInitProbString(SimpleCHDSubmodel.HF), "Initial probability of Heart failure", "GOLD",
				P_INI_HF_BETA[0] / (P_INI_HF_BETA[0] + P_INI_HF_BETA[1]), "BetaVariate", P_INI_HF_BETA[0], P_INI_HF_BETA[1])); 
		addProbParam(new SecondOrderParam(getInitProbString(SimpleNEUSubmodel.LEA), "Initial probability of lower amputation", "GOLD",
				P_INI_LEA_BETA[0] / (P_INI_LEA_BETA[0] + P_INI_LEA_BETA[1]), "BetaVariate", P_INI_LEA_BETA[0], P_INI_LEA_BETA[1])); 

	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		return new StandardSpainDeathSubmodel(this);
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}

	private static class CGM_Intervention extends SecondOrderDiabetesIntervention {
		private static final double COST = 3099.162857;

		public CGM_Intervention() {
			super("CGM", "CGM");
		}

		@Override
		public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
			secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + getShortName(), "Annual cost of " + getDescription(),  
					"Own calculations from data provided by DEXCOM", 2019, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST)));			
		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id, secParams);
		}

		public class Instance extends DiabetesIntervention {
			private final RandomVariate rnd; 
			private final double annualCost;

			public Instance(int id, SecondOrderParamsRepository secParams) {
				super(id, BasicConfigParams.DEF_MAX_AGE);
				final double sd = Statistics.sdFrom95CI(new double[] {0.375, 0.605});
				rnd = RandomVariateFactory.getInstance("NormalVariate", 0.49, sd);
				annualCost = secParams.getCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + getShortName()); 
			}

			@Override
			public double getHBA1cLevel(DiabetesPatient pat) {
				if (pat.isEffectActive()) {
					return pat.getBaselineHBA1c() - rnd.generate();
					
				}
				return pat.getBaselineHBA1c();
			}

			@Override
			public double getAnnualCost(DiabetesPatient pat) {
				return annualCost;
			}
			
		}
	}
	
	private static class SMBG_Intervention extends SecondOrderDiabetesIntervention {
		private static final double COST = 602.98;

		public SMBG_Intervention() {
			super("SMBG", "SMBG");
		}

		@Override
		public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
			secParams.addOtherParam(new SecondOrderParam("USE_STRIPS_SMBG", "Use of glucose strips", "DIAMOND", 4.055396825, "UniformVariate", 3, 9));
			secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + getShortName(), "Annual cost of " + getDescription(),  
					"Own calculations from data provided by DEXCOM", 2019, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST)));			
		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id, secParams);
		}

		public class Instance extends DiabetesIntervention {
			private final double annualCost;

			public Instance(int id, SecondOrderParamsRepository secParams) {
				super(id, BasicConfigParams.DEF_MAX_AGE);
				annualCost = secParams.getCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + getShortName()); 
			}

			@Override
			public double getHBA1cLevel(DiabetesPatient pat) {
				return pat.getBaselineHBA1c();
			}

			@Override
			public double getAnnualCost(DiabetesPatient pat) {
				return annualCost;
			}
			
		}
	}
	
}
