/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.htaReportCGM;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
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
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
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
 * <li>Population: {@link HypoDEPopulation}
 * <li>Interventions: {@link CGM_Intervention CGM} vs {@link SMBG_Intervention SMBG}</li>
 * <li>Discount rate: 3%</li>
 * <li>Complications included in the model: Depending on the value of {@link BasicConfigParams#USE_SIMPLE_MODELS}, the model uses
 * the following submodels
 * <ul>
 * <li>Retinopathy: {@link SheffieldRETSubmodel}.</li>
 * <li>Nephropathy: {@link SheffieldNPHSubmodel}.</li>
 * <li>Neuropathy: {@link SimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link SimpleCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link StandardSevereHypoglycemiaEvent} adjusted according to HypoDE</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class UnawareMonitoSecondOrderParams extends SecondOrderParamsRepository {
	/** Mean and SD rate (patients-year) of hypoglycemic events in HypoDE study (weighted average from baseline) */
	private static final double[] RATE_HYPO = {3.642340426, 14.56280812};
	private static final double[] MIN_MAX_RATE_HYPO = {0.5, 9.0};
	/** IRR of any severe hypoglycemia in CGM with respect to SMBG, according to HypoDE */
	private static final double IRR_HYPO = 0.36;
	private static final double []LN_IRR_HYPO_BETA = {Math.log(0.15), Math.log(0.88)};
	
	private final CGM_Intervention intCGM;
	private final SMBG_Intervention intSMBG;


	/**
	 * @param nPatients Number of patients to create
	 */
	public UnawareMonitoSecondOrderParams(int nPatients) {
		super(nPatients, new GoldDiamondPopulation());
		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		final double mode = Statistics.betaModeFromMeanSD(RATE_HYPO[0], RATE_HYPO[1]);
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(RATE_HYPO[0], mode, MIN_MAX_RATE_HYPO[0], MIN_MAX_RATE_HYPO[1]);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		
		final RandomVariate rndIRR = RandomVariateFactory.getInstance("LimitedRandomVariate", RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", Math.log(IRR_HYPO), Statistics.sdFrom95CI(LN_IRR_HYPO_BETA))), 0.0, 1.0);
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from 6-months rate)", 
						"HypoDE", RATE_HYPO[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_RATE_HYPO[1] - MIN_MAX_RATE_HYPO[0], MIN_MAX_RATE_HYPO[0])),
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "Relative risk of severe hypoglycemic event", "From meta", IRR_HYPO, rndIRR),
				EnumSet.of(DiabetesType.T1), true
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
		private static final double COST = 3147.342857;

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
	
	private static class SMBG_Intervention extends SecondOrderDiabetesIntervention {
		private static final double COST = 730;

		public SMBG_Intervention() {
			super("SMBG", "SMBG");
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
