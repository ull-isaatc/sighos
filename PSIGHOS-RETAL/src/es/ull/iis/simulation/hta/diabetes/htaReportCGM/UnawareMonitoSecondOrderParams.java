/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.htaReportCGM;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AcuteComplicationCounterListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.HbA1cListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.Discount;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.StdDiscount;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
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
 * @author Iván Castilla Rodríguez
 *
 */
public class UnawareMonitoSecondOrderParams extends SecondOrderParamsRepository {
	/** Mean and SD rate (patients-year) of hypoglycemic events in HypoDE study (weighted average from baseline) */
	private static final double[] RATE_HYPO = {3.642340426, 14.56280812};
	private static final double[] MIN_MAX_RATE_HYPO = {0.5, 7.5};
	/** IRR of any severe hypoglycemia in CGM with respect to SMBG, according to HypoDE */
	private static final double IRR_HYPO = 0.36;
	private static final double []IRR_HYPO_IC95 = {0.15, 0.88};
	private static final double []LN_IRR_HYPO_BETA = {Math.log(IRR_HYPO_IC95[0]), Math.log(IRR_HYPO_IC95[1])};
	private double lastCStrips = 0.0;
	
	private final CGM_Intervention intCGM;
	private final SMBG_Intervention intSMBG;

	/**
	 * Represents different scenarios that are used for sensitivity analysis.
	 * @author Iván Castilla
	 *
	 */
	public enum Scenario {
		BASE_CASE,
		STRIP_USE_STD, // Recommended use of glucose strips (9/day) in the SMBG intervention 
		STRIP_USE_LOW, // Lowest use of glucose strips
		STRIP_USE_HIGH,// Highest use of glucose strips
		STRIP_C_LOW, // Lowest cost of glucose strips
		STRIP_C_HIGH,// Highest cost of glucose strips
		SENSOR_USE_EXTENDED, // Applies 10 days to the use of the G5 sensor, instead of 7
		BASAL_HYPO_RATE_LOW, // 0.5 events per patient-year
		BASAL_HYPO_RATE_HIGH, // 7.5 events per patient-year
		EFFECT_LOW,	// Lowest effectiveness: 0.15 
		EFFECT_HIGH,	// Highest effectiveness: 0.88
		NO_MORTALITY_HYPO, // Discharge mortality by severe hypoglycemia
		DU_HYPO_EVANS	// Use Evans et al. (2013) disutility value 
	}
	
	public static Scenario SCENARIO = Scenario.BASE_CASE;

	/**
	 * @param nPatients Number of patients to create
	 */
	public UnawareMonitoSecondOrderParams(int nPatients) {
		super(nPatients, new GoldDiamondPopulation());
		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		// Select parameter for basal rate of hypoglycemia based on the scenario
		final SecondOrderParam pHypo;
		switch(SCENARIO) {
		case BASAL_HYPO_RATE_HIGH:
			pHypo = new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from 6-months rate)", 
					"HypoDE", MIN_MAX_RATE_HYPO[1]);
			break;
		case BASAL_HYPO_RATE_LOW:
			pHypo = new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from 6-months rate)", 
					"HypoDE", MIN_MAX_RATE_HYPO[0]);
			break;
		default:
			final double mode = Statistics.betaModeFromMeanSD(RATE_HYPO[0], RATE_HYPO[1]);
			final double[] betaParams = Statistics.betaParametersFromEmpiricData(RATE_HYPO[0], mode, MIN_MAX_RATE_HYPO[0], MIN_MAX_RATE_HYPO[1]);
			final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
			pHypo = new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from 6-months rate)", 
					"HypoDE", RATE_HYPO[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_RATE_HYPO[1] - MIN_MAX_RATE_HYPO[0], MIN_MAX_RATE_HYPO[0]));
		}
		
		// Select parameter for IRR of hypoglycemia based on the scenario
		final SecondOrderParam rrHypo;
		switch(SCENARIO) {
			case EFFECT_HIGH:
				rrHypo = new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "Relative risk of severe hypoglycemic event", "From meta", IRR_HYPO_IC95[0]);
				break;
			case EFFECT_LOW:
				rrHypo = new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "Relative risk of severe hypoglycemic event", "From meta", IRR_HYPO_IC95[1]);
				break;
			default:
				final RandomVariate rndIRR = RandomVariateFactory.getInstance("LimitedRandomVariate", RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", Math.log(IRR_HYPO), Statistics.sdFrom95CI(LN_IRR_HYPO_BETA))), 0.0, 0.9999);
				rrHypo = new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "Relative risk of severe hypoglycemic event", "From meta", IRR_HYPO, rndIRR);
		}
		
		// Select parameter for disutility of hypoglycemia based on the scenario
		final SecondOrderParam duHypo = Scenario.DU_HYPO_EVANS.equals(SCENARIO) ? 
			new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "Evans et al.", 0.047) :
			StandardSevereHypoglycemiaEvent.getDefaultDisutilityParameter();

		final SecondOrderParam mortality = Scenario.NO_MORTALITY_HYPO.equals(SCENARIO) ?
			new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_DEATH_HYPO, "Death by severe hypoglycemia", "Assumption", 0.0) :
			StandardSevereHypoglycemiaEvent.getDefaultMortalityParameter();
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(pHypo, rrHypo, duHypo, 
				StandardSevereHypoglycemiaEvent.getDefaultCostParameter(),
				mortality, EnumSet.of(DiabetesType.T1), true);
		registerComplication(hypoEvent);

		intSMBG = new SMBG_Intervention();
		intCGM = new CGM_Intervention();
		registerIntervention(intSMBG);
		registerIntervention(intCGM);
		
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		switch(SCENARIO) {
		case STRIP_C_HIGH:
			addCostParam(new SecondOrderCostParam(CommonParams.STR_C_STRIPS, "Annual cost of strips", CommonParams.STR_DESCRIPTION_C_STRIPS, CommonParams.YEAR_C_STRIPS,
					CommonParams.getMinMaxCostStrips()[1]));
			break;
		case STRIP_C_LOW:
			addCostParam(new SecondOrderCostParam(CommonParams.STR_C_STRIPS, "Annual cost of strips", CommonParams.STR_DESCRIPTION_C_STRIPS, CommonParams.YEAR_C_STRIPS,
					CommonParams.getMinMaxCostStrips()[0]));
			break;
		default:
			addCostParam(new SecondOrderCostParam(CommonParams.STR_C_STRIPS, "Annual cost of strips", CommonParams.STR_DESCRIPTION_C_STRIPS, CommonParams.YEAR_C_STRIPS,
				CommonParams.getCostStrips(), CommonParams.getRndVariateCostStrips()));
		}
	}

	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}

	@Override
	protected DiabetesIntervention[] getInterventions() {
		final DiabetesIntervention[] interventions = new DiabetesIntervention[registeredInterventions.size()];
		lastCStrips = getCostParam(CommonParams.STR_C_STRIPS);
		for (int i = 0; i < registeredInterventions.size(); i++) {
			interventions[i] = registeredInterventions.get(i).getInstance(i, this);
		}
		return interventions;
	}
	
	public static void main(String[] args) {		
		System.out.println("Simulating scenarios");
		// First base case
		final Discount discount = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
		// Print header
		final StringBuilder str = new StringBuilder();
		// Simulate all scenarios
		for (Scenario sce : Scenario.values()) {
			SCENARIO = sce;
			final SecondOrderParamsRepository secParams = new UnawareMonitoSecondOrderParams(BasicConfigParams.DEF_N_PATIENTS);

			final int timeHorizon = BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1;
			final int nInterventions = secParams.getRegisteredInterventions().size();
			final RepositoryInstance common = secParams.getInstance();
			final HbA1cListener[] hba1cListeners = new HbA1cListener[nInterventions];
			final CostListener[] costListeners = new CostListener[nInterventions];
			final LYListener[] lyListeners = new LYListener[nInterventions];
			final QALYListener[] qalyListeners = new QALYListener[nInterventions];
			final AcuteComplicationCounterListener[] acuteListeners = new AcuteComplicationCounterListener[nInterventions];

			for (int i = 0; i < nInterventions; i++) {
				hba1cListeners[i] = new HbA1cListener(BasicConfigParams.DEF_N_PATIENTS);
				costListeners[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discount, BasicConfigParams.DEF_N_PATIENTS);
				lyListeners[i] = new LYListener(discount, BasicConfigParams.DEF_N_PATIENTS);
				qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discount, BasicConfigParams.DEF_N_PATIENTS);
				acuteListeners[i] = new AcuteComplicationCounterListener(BasicConfigParams.DEF_N_PATIENTS);
			}
			final DiabetesIntervention[] intInstances = common.getInterventions();
			DiabetesSimulation simul = new DiabetesSimulation(0, intInstances[0], BasicConfigParams.DEF_N_PATIENTS, common, secParams.getPopulation(), timeHorizon);
			simul.addInfoReceiver(hba1cListeners[0]);
			simul.addInfoReceiver(costListeners[0]);
			simul.addInfoReceiver(lyListeners[0]);
			simul.addInfoReceiver(qalyListeners[0]);
			simul.addInfoReceiver(acuteListeners[0]);
			
			simul.run();
			for (int i = 1; i < nInterventions; i++) {
				simul = new DiabetesSimulation(simul, intInstances[i]);
				simul.addInfoReceiver(hba1cListeners[i]);
				simul.addInfoReceiver(costListeners[i]);
				simul.addInfoReceiver(lyListeners[i]);
				simul.addInfoReceiver(qalyListeners[i]);
				simul.addInfoReceiver(acuteListeners[i]);
				
				simul.run();
			}

			if (Scenario.BASE_CASE.equals(SCENARIO)) {
				str.append("SIM\t");
				for (SecondOrderDiabetesIntervention intervention : secParams.getRegisteredInterventions()) {
					final String shortName = intervention.getShortName();
					str.append(HbA1cListener.getStrHeader(shortName));
					str.append(CostListener.getStrHeader(shortName));
					str.append(LYListener.getStrHeader(shortName));
					str.append(QALYListener.getStrHeader(shortName));
					str.append(AcuteComplicationCounterListener.getStrHeader(shortName));
				}
				str.append(secParams.getStrHeader());
				str.append(System.lineSeparator());
			}
			
			str.append(SCENARIO.name() + "\t");
			for (int i = 0; i < nInterventions; i++) {
				str.append(hba1cListeners[i]);
				str.append(costListeners[i]);
				str.append(lyListeners[i]);
				str.append(qalyListeners[i]);
				str.append(acuteListeners[i]);
			}
			str.append(secParams).append(System.lineSeparator());
		}
		System.out.println(str.toString());				
	}
	
	private static class CGM_Intervention extends SecondOrderDiabetesIntervention {
		/** Cost of sensor for DEXCOM G5( */
		private static final double C_SENSOR_G5 = 50;
		/** Daily use of sensor: sensors are changed after 7 days */
		private static final double USE_SENSOR_G5 = 1.0/7.0;
		private static final String STR_USE_SENSOR_G5 = "DAILY_USE_SENSOR";
		private static final double []USE_STRIPS = {3.7, 1.9};
		private static final double []MIN_MAX_USE_STRIPS = {1.0, 8.0};

		public CGM_Intervention() {
			super("CGM", "CGM");
		}

		@Override
		public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
			switch(SCENARIO) {
				case STRIP_USE_STD:
					secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
							"Recommendations", 2));
					break;
				case STRIP_USE_LOW:
					secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
							"Assumption: lower limit", MIN_MAX_USE_STRIPS[0]));
					break;
				case STRIP_USE_HIGH:
					secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
							"Assumption: upper limit", MIN_MAX_USE_STRIPS[1]));
					break;
				default:
					final double mode = Statistics.betaModeFromMeanSD(USE_STRIPS[0], USE_STRIPS[1]);
					final double[] betaParams = Statistics.betaParametersFromEmpiricData(USE_STRIPS[0], mode, MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[1]);
					final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
					secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
							"HypoDE", USE_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_USE_STRIPS[1] - MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[0])));
			}
			if (Scenario.SENSOR_USE_EXTENDED.equals(SCENARIO)) {
				secParams.addOtherParam(new SecondOrderParam(STR_USE_SENSOR_G5, "Use of sensor", 
						"Assumption: use extended to 10 days", 0.1));				
			}
			else {
				// I assume a daily use with +-25% uncertainty
				secParams.addOtherParam(new SecondOrderParam(STR_USE_SENSOR_G5, "Use of sensor", 
						"Technical data sheets", USE_SENSOR_G5, RandomVariateFactory.getInstance("UniformVariate", 0.75 * USE_SENSOR_G5, 1.25 * USE_SENSOR_G5)));
			}
		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id, ((UnawareMonitoSecondOrderParams)secParams).lastCStrips, secParams.getOtherParam(CommonParams.STR_USE_STRIPS + "_" + getShortName()), secParams.getOtherParam(STR_USE_SENSOR_G5));
		}

		public class Instance extends DiabetesIntervention {
			private final double annualCost;

			public Instance(int id, double cStrips, double useStrips, double useSensor) {
				super(id, BasicConfigParams.DEF_MAX_AGE);
				annualCost = (cStrips * useStrips + C_SENSOR_G5 * useSensor) * 365 ; 
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
		private static final double []USE_STRIPS = {6, 1.3};
		private static final double []MIN_MAX_USE_STRIPS = {3.0, 10.0};

		public SMBG_Intervention() {
			super("SMBG", "SMBG");
		}

		@Override
		public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
			switch(SCENARIO) {
			case STRIP_USE_STD:
				secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
						"Recommendations", 9));
				break;
			case STRIP_USE_LOW:
				secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
						"Assumption: lower limit", MIN_MAX_USE_STRIPS[0]));
				break;
			case STRIP_USE_HIGH:
				secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
						"Assumption: upper limit", MIN_MAX_USE_STRIPS[1]));
				break;
			default:
				final double mode = Statistics.betaModeFromMeanSD(USE_STRIPS[0], USE_STRIPS[1]);
				final double[] betaParams = Statistics.betaParametersFromEmpiricData(USE_STRIPS[0], mode, MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[1]);
				final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
				secParams.addOtherParam(new SecondOrderParam(CommonParams.STR_USE_STRIPS + "_" + getShortName(), "Use of strips in " + getDescription(), 
						"HypoDE", USE_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_USE_STRIPS[1] - MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[0])));
			}
		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id, ((UnawareMonitoSecondOrderParams)secParams).lastCStrips, secParams.getOtherParam(CommonParams.STR_USE_STRIPS + "_" + getShortName()));
		}

		public class Instance extends DiabetesIntervention {
			private final double annualCost;

			public Instance(int id, double cStrips, double useStrips) {
				super(id, BasicConfigParams.DEF_MAX_AGE);
				annualCost = cStrips * useStrips * 365; 
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
