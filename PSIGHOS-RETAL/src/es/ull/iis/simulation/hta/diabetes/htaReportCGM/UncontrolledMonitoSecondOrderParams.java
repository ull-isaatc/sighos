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
 * <li>Population: {@link GoldDiamondPopulation}
 * <li>Interventions: {@link CGM_Intervention CGM} vs {@link SMBG_Intervention SMBG}</li>
 * <li>Discount rate: 3%</li>
 * <li>Complications included in the model: Depending on the value of {@link BasicConfigParams#USE_SIMPLE_MODELS}, the model uses
 * the following submodels
 * <ul>
 * <li>Retinopathy: {@link SheffieldRETSubmodel}.</li>
 * <li>Nephropathy: {@link SheffieldNPHSubmodel}.</li>
 * <li>Neuropathy: {@link SimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link SimpleCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link StandardSevereHypoglycemiaEvent} adjusted according to GOLD</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class UncontrolledMonitoSecondOrderParams extends SecondOrderParamsRepository {
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
		EFFECT_LOW,	// Lowest effectiveness 
		EFFECT_HIGH,	// Highest effectiveness
		NO_MORTALITY_HYPO, // Discharge mortality by severe hypoglycemia		
	}
	
	public static Scenario SCENARIO = Scenario.BASE_CASE;

	/**
	 * @param nPatients Number of patients to create
	 */
	public UncontrolledMonitoSecondOrderParams(int nPatients) {
		super(nPatients, new GoldDiamondPopulation());
		
		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());

		final SecondOrderParam mortality = Scenario.NO_MORTALITY_HYPO.equals(SCENARIO) ?
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_DEATH_HYPO, "Death by severe hypoglycemia", "Assumption", 0.0) :
				StandardSevereHypoglycemiaEvent.getDefaultMortalityParameter();
		
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
						"GOLD", P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])),
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "Relative risk of severe hypoglycemic event", "From meta", 1.0),
				StandardSevereHypoglycemiaEvent.getDefaultDisutilityParameter(), StandardSevereHypoglycemiaEvent.getDefaultCostParameter(), mortality,
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
		
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
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
			final SecondOrderParamsRepository secParams = new UncontrolledMonitoSecondOrderParams(BasicConfigParams.DEF_N_PATIENTS);

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
		private static final String STR_HBA1C_REDUCTION = "HBA1C_REDUCTION";
		private static final double []USE_STRIPS = {3.6, 1.6};
		private static final double []MIN_MAX_USE_STRIPS = {1.0, 8.0};
		private static final double HBA1C_REDUCTION = 0.49;
		private static final double []HBA1C_REDUCTION_IC95 = {0.375, 0.605};

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
							"DIAMOND 10.1001/jama.2016.19975", USE_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_USE_STRIPS[1] - MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[0])));
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
			switch (SCENARIO) {
			case EFFECT_HIGH:
				secParams.addOtherParam(new SecondOrderParam(STR_HBA1C_REDUCTION, "Reduction of the HbA1c level due to the intervention", 
						"GOLD+DIAMOND", HBA1C_REDUCTION_IC95[1]));
				break;
			case EFFECT_LOW:
				secParams.addOtherParam(new SecondOrderParam(STR_HBA1C_REDUCTION, "Reduction of the HbA1c level due to the intervention", 
						"GOLD+DIAMOND", HBA1C_REDUCTION_IC95[0]));
				break;
			default:
				final double sd = Statistics.sdFrom95CI(HBA1C_REDUCTION_IC95);
				secParams.addOtherParam(new SecondOrderParam(STR_HBA1C_REDUCTION, "Reduction of the HbA1c level due to the intervention", 
						"GOLD+DIAMOND", HBA1C_REDUCTION, RandomVariateFactory.getInstance("NormalVariate", HBA1C_REDUCTION, sd)));
				break;
			}

		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id, ((UncontrolledMonitoSecondOrderParams)secParams).lastCStrips, secParams.getOtherParam(CommonParams.STR_USE_STRIPS + "_" + getShortName()), 
					secParams.getOtherParam(STR_USE_SENSOR_G5), secParams.getOtherParam(STR_HBA1C_REDUCTION));
		}

		public class Instance extends DiabetesIntervention {
//			private final RandomVariate rnd; 
			private final double annualCost;
			private final double reduction;

			public Instance(int id, double cStrips, double useStrips, double useSensor, double reduction) {
				super(id, BasicConfigParams.DEF_MAX_AGE);
				annualCost = (cStrips * useStrips + C_SENSOR_G5 * useSensor) * 365;
				this.reduction = reduction; 
//				final double sd = Statistics.sdFrom95CI(new double[] {0.375, 0.605});
//				rnd = RandomVariateFactory.getInstance("NormalVariate", 0.49, sd);
			}

			@Override
			public double getHBA1cLevel(DiabetesPatient pat) {
				if (pat.isEffectActive()) {
					return pat.getBaselineHBA1c() - reduction;
					
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
		private static final double []USE_STRIPS = {4.6, 1.6};
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
							"DIAMOND 10.1001/jama.2016.19975", USE_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_USE_STRIPS[1] - MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[0])));
			}
		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id, ((UncontrolledMonitoSecondOrderParams)secParams).lastCStrips, secParams.getOtherParam(CommonParams.STR_USE_STRIPS + "_" + getShortName()));
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
