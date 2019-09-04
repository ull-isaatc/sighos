/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.diabetes.interventions.SMILECSIIIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.SMILESAPIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.populations.SMILEPopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.LyNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.LyRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SMILESevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleRETSubmodel;

/**
 * A repository with data used to parameterize a model where:
 * <ul>
 * <li>Interventions: SAP with predictive low-glucose management vs the standard insulin pump</li>
 * <li>Discount rate: 3%</li>
 * <li>Complications included in the model: Depending on the value of {@link BasicConfigParams#USE_SIMPLE_MODELS}, the model uses
 * the following submodels
 * <ul>
 * <li>Retinopathy: {@link SheffieldRETSubmodel}.</li>
 * <li>Nephropathy: {@link LyNPHSubmodel}.</li>
 * <li>Neuropathy: {@link SimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link SimpleCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link SMILESevereHypoglycemiaEvent}</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class SMILESecondOrderParams extends SecondOrderParamsRepository {
	private static final String STR_SMILE_PRIVATE = "SMILE study. Private communication";
	/** {Deterministic value, cases, no cases} for initial proportion of microalbuminuria */
	private static final double [] P_INIT_ALB1 = {0.0433078, 6.626097133, 146,3739029};
	/** {Deterministic value, cases, no cases} for initial proportion of macroalbuminuria */
	private static final double [] P_INIT_ALB2 = {0.0351235, 5.373902867, 147.6260971};
	/** {Deterministic value, cases, no cases} for initial proportion of background retinopathy */
	private static final double [] P_INIT_BGRET = {0.3192333, 48.84269663, 104.1573034};
	/** {Deterministic value, cases, no cases} for initial proportion of proliferative retinopathy */
	private static final double [] P_INIT_PRET = {0.0925314, 14.15730337, 138.8426966};
	/** {Deterministic value, cases, no cases} for initial proportion of macular edema */
	private static final double [] P_INIT_ME = {0.0573770, 8.778688525, 144.2213115};
	/** {Deterministic value, cases, no cases} for initial proportion of neuropathy */
	private static final double [] P_INIT_NEU = {0.2091503, 32, 121};
	/** {Deterministic value, cases, no cases} for initial proportion of myocardial infarction */
	private static final double [] P_INIT_MI = {0.0321488072353124, 4.9187675070028, 148.081232492997};
	/** {Deterministic value, cases, no cases} for initial proportion of angina */
	private static final double [] P_INIT_Angina = {0.0306841690924736, 4.69467787114846, 148.305322128852};
	/** {Deterministic value, cases, no cases} for initial proportion of stroke */
	private static final double [] P_INIT_Stroke = {0.0215301806997309, 3.29411764705882, 149.705882352941};
	/** {Deterministic value, cases, no cases} for initial proportion of heart failure */
	private static final double [] P_INIT_HF = {0.0202120063711759, 3.09243697478992, 149.90756302521};
	/**
	 * Initializes the parameters for the population defined in this class. With respect to the cost of the treatments,
	 * we apply full costs independently of the adherence, by assuming that the NHS would continue providing the treatment
	 * even if not used.
	 * @param nPatients Number of patients to create
	 */
	public SMILESecondOrderParams(int nPatients) {
		super(nPatients, new SMILEPopulation());
		BasicConfigParams.STUDY_YEAR = 2019;

		// Initial complications
		addProbParam(new SecondOrderParam(getInitProbString(LyNPHSubmodel.ALB1), "Initial probability of microalbuminuria", 
				STR_SMILE_PRIVATE, P_INIT_ALB1[0], "BetaVariate", P_INIT_ALB1[1], P_INIT_ALB1[2]));
		addProbParam(new SecondOrderParam(getInitProbString(LyNPHSubmodel.ALB2), "Initial probability of macroalbuminuria", 
				STR_SMILE_PRIVATE, P_INIT_ALB2[0], "BetaVariate", P_INIT_ALB2[1], P_INIT_ALB2[2]));
		addProbParam(new SecondOrderParam(getInitProbString(LyRETSubmodel.BGRET), "Initial probability of background retinopathy", 
				STR_SMILE_PRIVATE, P_INIT_BGRET[0], "BetaVariate", P_INIT_BGRET[1], P_INIT_BGRET[2]));
		addProbParam(new SecondOrderParam(getInitProbString(LyRETSubmodel.PRET), "Initial probability of proliferative retinopathy", 
				STR_SMILE_PRIVATE, P_INIT_PRET[0], "BetaVariate", P_INIT_PRET[1], P_INIT_PRET[2]));
		addProbParam(new SecondOrderParam(getInitProbString(LyRETSubmodel.ME), "Initial probability of macular edema", 
				STR_SMILE_PRIVATE, P_INIT_ME[0], "BetaVariate", P_INIT_ME[1], P_INIT_ME[2]));
		addProbParam(new SecondOrderParam(getInitProbString(SimpleNEUSubmodel.NEU), "Initial probability of neuropathy", 
				STR_SMILE_PRIVATE, P_INIT_NEU[0], "BetaVariate", P_INIT_NEU[1], P_INIT_NEU[2]));
		addProbParam(new SecondOrderParam(getInitProbString(SimpleCHDSubmodel.MI), "Initial probability of myocardial infarction", 
				STR_SMILE_PRIVATE, P_INIT_MI[0], "BetaVariate", P_INIT_MI[1], P_INIT_MI[2]));
		addProbParam(new SecondOrderParam(getInitProbString(SimpleCHDSubmodel.ANGINA), "Initial probability of angina", 
				STR_SMILE_PRIVATE, P_INIT_Angina[0], "BetaVariate", P_INIT_Angina[1], P_INIT_Angina[2]));
		addProbParam(new SecondOrderParam(getInitProbString(SimpleCHDSubmodel.STROKE), "Initial probability of stroke", 
				STR_SMILE_PRIVATE, P_INIT_Stroke[0], "BetaVariate", P_INIT_Stroke[1], P_INIT_Stroke[2]));
		addProbParam(new SecondOrderParam(getInitProbString(SimpleCHDSubmodel.HF), "Initial probability of heart failure", 
				STR_SMILE_PRIVATE, P_INIT_HF[0], "BetaVariate", P_INIT_HF[1], P_INIT_HF[2]));
		
		// Chronic complication submodels
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			registerComplication(new SimpleNPHSubmodel());
			registerComplication(new SimpleRETSubmodel());
		}
		else {
			registerComplication(new SheffieldNPHSubmodel());
			registerComplication(new SheffieldRETSubmodel());
		}
//		registerComplication(new LyNPHSubmodel());
//		registerComplication(new LyRETSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());

		// Acute complication submodels
		registerComplication(new SMILESevereHypoglycemiaEvent());

		registerIntervention(new SMILECSIIIntervention());
		registerIntervention(new SMILESAPIntervention());
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));
		
		final double[] paramsDuDNC = SecondOrderParamsRepository.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
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
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.MAX, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}

}
