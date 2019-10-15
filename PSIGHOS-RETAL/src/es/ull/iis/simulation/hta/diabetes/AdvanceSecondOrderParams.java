/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.diabetes.interventions.AdvanceConventionalIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.AdvanceIntensiveIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.populations.AdvancePopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.AdvanceSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMPrositRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.T2DMSimpleNEUSubmodel;
import es.ull.iis.util.Statistics;

/**
 * A repository with data used to parameterize a model where:
 * <ul>
 * <li>Interventions: Advance conservative versus intensive</li>
 * <li>Discount rate: 3%</li>
 * <li>Complications included in the model: 
 * <ul>
 * <li>Retinopathy: {@link T2DMPrositRETSubmodel}</li>
 * <li>Nephropathy: {@link T2DMNPHSubmodel}</li>
 * <li>Neuropathy: {@link T2DMSimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link T2DMPrositCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link AdvanceSevereHypoglycemiaEvent}</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class AdvanceSecondOrderParams extends SecondOrderParamsRepository {
	private static final String SOURCE_ADVANCE = "10.1056/NEJMoa0802987";
	/** Starting number of patients in the conservative and intensive branches of the study */
	private static final double INIT_N = 5569 + 5571;
	private static final double INIT_N_ALB1 = 1423 + 1432;
	private static final double INIT_N_ALB2 = 189 + 215;
	/** Initial number of patients with microvascular eye disease: assuming all of them have proliferative retinopathy */ 
	private static final double INIT_N_PRET = 403 + 392;
	private static final double INIT_N_MI = 666 + 668;
	private static final double INIT_N_STROKE = 508 + 515;
	/** Assumed to be anginas */
	private static final double INIT_N_OTHER_MACROVASCULAR = 683 + 378;
	
	/**
	 * @param nPatients Number of patients to create
	 */
	public AdvanceSecondOrderParams(int nPatients) {
		super(nPatients, new AdvancePopulation());
		
		addProbParam(new SecondOrderParam(getInitProbString(T2DMNPHSubmodel.ALB1), "Initial probability of microalbuminuria", 
				SOURCE_ADVANCE, INIT_N_ALB1 / INIT_N, "BetaVariate", INIT_N_ALB1, INIT_N - INIT_N_ALB1));
		addProbParam(new SecondOrderParam(getInitProbString(T2DMNPHSubmodel.ALB2), "Initial probability of macroalbuminuria", 
				SOURCE_ADVANCE, INIT_N_ALB2 / INIT_N, "BetaVariate", INIT_N_ALB2, INIT_N - INIT_N_ALB2));
		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositRETSubmodel.PRET), "Initial probability of proliferative retinopathy", 
				SOURCE_ADVANCE, INIT_N_PRET / INIT_N, "BetaVariate", INIT_N_PRET, INIT_N - INIT_N_PRET));
		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositCHDSubmodel.POST_MI), "Initial probability of myocardial infarction", 
				SOURCE_ADVANCE, INIT_N_MI / INIT_N, "BetaVariate", INIT_N_MI, INIT_N - INIT_N_MI));
		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositCHDSubmodel.POST_STROKE), "Initial probability of stroke", 
				SOURCE_ADVANCE, INIT_N_STROKE / INIT_N, "BetaVariate", INIT_N_STROKE, INIT_N - INIT_N_STROKE));
		addProbParam(new SecondOrderParam(getInitProbString(T2DMPrositCHDSubmodel.POST_ANGINA), "Initial probability of angina", 
				SOURCE_ADVANCE, INIT_N_OTHER_MACROVASCULAR / INIT_N, "BetaVariate", INIT_N_OTHER_MACROVASCULAR, INIT_N - INIT_N_OTHER_MACROVASCULAR));
		
		registerComplication(new T2DMPrositRETSubmodel());
		registerComplication(new T2DMNPHSubmodel());
		registerComplication(new T2DMPrositCHDSubmodel());
		registerComplication(new T2DMSimpleNEUSubmodel());
		registerComplication(new AdvanceSevereHypoglycemiaEvent());

		registerIntervention(new AdvanceConventionalIntervention());
		registerIntervention(new AdvanceIntensiveIntervention());
		
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
	
}
