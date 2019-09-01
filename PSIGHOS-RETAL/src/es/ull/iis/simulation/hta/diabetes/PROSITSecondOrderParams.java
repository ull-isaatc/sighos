/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

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
import es.ull.iis.simulation.hta.diabetes.populations.UKPDSPopulation;
import es.ull.iis.simulation.hta.diabetes.populations.WESDRPopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.BattelinoSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.LyNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.LyRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMSimpleNEUSubmodel;

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
public class PROSITSecondOrderParams extends SecondOrderParamsRepository {
//	/** Initial proportion of [No retinopathy, no proliferative retinopathy, proliferative retinopathy] in the WESDR study. For a Dirichlet distribution */
//	private final double[] P_INIT_RET = {207, 271 + 69, 87};
//	private final RandomVariate pInitRet;

	/**
	 * @param nPatients Number of patients to create
	 */
	public PROSITSecondOrderParams(int nPatients) {
//		super(nPatients, new WESDRPopulation());
		super(nPatients, new UKPDSPopulation());
		
//		pInitRet = RandomVariateFactory.getInstance("DirichletVariate", P_INIT_RET);

//		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositRETSubmodel.BGRET), "Initial probability of background retinopathy", 
//				"Klein et al. 10.1016/S0161-6420(98)91020-X", 0.427444795 + 0.108832808));
//		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositRETSubmodel.PRET), "Initial probability of proliferative retinopathy", 
//				"Klein et al. 10.1016/S0161-6420(98)91020-X", 0.137223975));
		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositNPHSubmodel.ALB1), "Initial probability of microalbuminuria", 
				"Adler et al. 10.1046/j.1523-1755.2003.00712.x", 333.0 / 5097.0, "BetaVariate", 333, 5097 - 333));
		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositNPHSubmodel.ALB2), "Initial probability of macroalbuminuria", 
				"Adler et al. 10.1046/j.1523-1755.2003.00712.x", 37.0 / 5097.0, "BetaVariate", 37, 5097 - 37));
		
		registerComplication(new T2DMPrositRETSubmodel());
		registerComplication(new T2DMPrositNPHSubmodel());
		SecondOrderChronicComplicationSubmodel subCHD = new SimpleCHDSubmodel();
		registerComplication(subCHD);
		registerComplication(new T2DMSimpleNEUSubmodel());
		subCHD.disable();
		registerComplication(new T2DMPrositSevereHypoglycemiaEvent());

		registerIntervention(new DCCTConventionalIntervention());
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = SecondOrderParamsRepository.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		final EmpiricalSpainDeathSubmodel dModel = new EmpiricalSpainDeathSubmodel(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients);

		dModel.addIMR(T2DMPrositNPHSubmodel.ALB2, getIMR(T2DMPrositNPHSubmodel.ALB2));
		dModel.addIMR(T2DMPrositNPHSubmodel.ESRD, getIMR(T2DMPrositNPHSubmodel.ESRD));			
		dModel.addIMR(SimpleNEUSubmodel.NEU, getIMR(SimpleNEUSubmodel.NEU));
		dModel.addIMR(SimpleNEUSubmodel.LEA, getIMR(SimpleNEUSubmodel.LEA));
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(DiabetesChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(DiabetesChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(DiabetesChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(DiabetesChronicComplications.CHD));
		return dModel;
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}
	
}
