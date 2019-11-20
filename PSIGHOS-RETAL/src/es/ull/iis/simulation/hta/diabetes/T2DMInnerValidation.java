/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.diabetes.inforeceiver.EpidemiologicView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.ExperimentListener;
import es.ull.iis.simulation.hta.diabetes.interventions.DCCTConventionalIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesPopulation;
import es.ull.iis.simulation.hta.diabetes.populations.UKPDSPopulation;
import es.ull.iis.simulation.hta.diabetes.populations.WESDRPopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.BattelinoSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMSimpleNEUSubmodel;
import es.ull.iis.util.Statistics;

/**
 * A repository with data used to parameterize a model where:
 * <ul>
 * <li>Interventions: SAP with predictive low-glucose management vs the standard insulin pump</li>
 * <li>Discount rate: 3%</li>
 * <li>Complications included in the model: Depending on the value of {@link BasicConfigParams#USE_SIMPLE_MODELS}, the model uses
 * the following submodels
 * <ul>
 * <li>Retinopathy: {@link SimpleRETSubmodel}, if {@link BasicConfigParams#USE_SIMPLE_MODELS USE_SIMPLE_MODELS} = true; {@link SheffieldRETSubmodel} otherwise.</li>
 * <li>Nephropathy: {@link SimpleNPHSubmodel}, if {@link BasicConfigParams#USE_SIMPLE_MODELS USE_SIMPLE_MODELS} = true; {@link SheffieldNPHSubmodel} otherwise.</li>
 * <li>Neuropathy: {@link T2DMSimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link T2DMPrositCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link BattelinoSevereHypoglycemiaEvent}</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class T2DMInnerValidation extends SecondOrderParamsRepository {
//	/** Initial proportion of [No retinopathy, no proliferative retinopathy, proliferative retinopathy] in the WESDR study. For a Dirichlet distribution */
//	private final double[] P_INIT_RET = {207, 271 + 69, 87};
//	private final RandomVariate pInitRet;
//	private static final String STR_SOURCE_KLEIN = "Klein et al. 10.1016/S0161-6420(98)91020-X"; 
	private static final String STR_SOURCE_ADLER = "Adler et al. 10.1046/j.1523-1755.2003.00712.x";
	private static final int N_PATIENTS = BasicConfigParams.DEF_N_PATIENTS;
	
	/**
	 * @param nPatients Number of patients to create
	 */
	public T2DMInnerValidation(int nPatients, DiabetesPopulation pop) {
		super(nPatients, pop);
		
//		pInitRet = RandomVariateFactory.getInstance("DirichletVariate", P_INIT_RET);

		registerComplication(new T2DMPrositRETSubmodel());
		registerComplication(new T2DMNPHSubmodel());
		registerComplication(new T2DMPrositCHDSubmodel());
		registerComplication(new T2DMSimpleNEUSubmodel());
		registerComplication(new T2DMPrositSevereHypoglycemiaEvent());

//		getRegisteredChronicComplications()[DiabetesChronicComplications.CHD.ordinal()].disable();
//		getRegisteredChronicComplications()[DiabetesChronicComplications.NEU.ordinal()].disable();
//		getRegisteredChronicComplications()[DiabetesChronicComplications.NPH.ordinal()].disable();
//		getRegisteredChronicComplications()[DiabetesChronicComplications.RET.ordinal()].disable();
//		for (SecondOrderAcuteComplicationSubmodel comp : getRegisteredAcuteComplications()) {
//			comp.disable();
//		}
		registerIntervention(new DCCTConventionalIntervention());
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		return new EmpiricalSpainDeathSubmodel(this);
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}

	public static void validateRET() {
		System.out.println("Validating retinopathy against WESDR");
		final SecondOrderParamsRepository secParams = new T2DMInnerValidation(N_PATIENTS, new WESDRPopulation());
//		secParams.addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositRETSubmodel.BGRET), "Initial probability of background retinopathy", 
//				STR_SOURCE_KLEIN, (271.0 + 69.0) / 634.0));
//		secParams.addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositRETSubmodel.PRET), "Initial probability of proliferative retinopathy", 
//				STR_SOURCE_KLEIN, 87.0 / 634.0));
//		secParams.addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositNPHSubmodel.ALB2), "Initial probability of proteinuria", 
//				STR_SOURCE_KLEIN, 0.155));
		final RepositoryInstance common = secParams.getInstance();
		final int timeHorizon = BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1;
		final DiabetesSimulation simul = new DiabetesSimulation(0, common.getInterventions()[0], N_PATIENTS, common, secParams.getPopulation(), timeHorizon);
		final ExperimentListener listener = new EpidemiologicView(1, secParams, 1, EpidemiologicView.Type.CUMUL_INCIDENCE, true, false);
		listener.addListener(simul);
		simul.run();
		System.out.println(listener);
	}
	
	public static void validateNPH() {
		System.out.println("Validating nephropathy against UKPDS");
		final SecondOrderParamsRepository secParams = new T2DMInnerValidation(N_PATIENTS, new UKPDSPopulation());
		secParams.addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositNPHSubmodel.ALB1), "Initial probability of microalbuminuria", 
				STR_SOURCE_ADLER, 333.0 / 5097.0, "BetaVariate", 333, 5097 - 333));
		secParams.addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositNPHSubmodel.ALB2), "Initial probability of macroalbuminuria", 
				STR_SOURCE_ADLER, 37.0 / 5097.0, "BetaVariate", 37, 5097 - 37));
		
		final RepositoryInstance common = secParams.getInstance();
		final int timeHorizon = BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1;
		final DiabetesSimulation simul = new DiabetesSimulation(0, common.getInterventions()[0], N_PATIENTS, common, secParams.getPopulation(), timeHorizon);
		final ExperimentListener listener = new EpidemiologicView(1, secParams, 1, EpidemiologicView.Type.PREVALENCE, true, false);
		listener.addListener(simul);
//		simul.addInfoReceiver(new DiabetesPatientInfoView(1));
		simul.run();
		System.out.println(listener);
	}
	
	public static void main(String[] args) {
//		validateRET();
		validateNPH();
	}
}
