/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.htaReportSAP;

import es.ull.iis.simulation.hta.diabetes.interventions.CSIIIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.UncontrolledSAPIntervention;
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
import es.ull.iis.simulation.hta.diabetes.submodels.BattelinoSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSpainDeathSubmodel;
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
 * <li>Neuropathy: {@link SimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link SimpleCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link BattelinoSevereHypoglycemiaEvent}</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class UncontrolledSecondOrderParams extends SecondOrderParamsRepository {

	/**
	 * @param nPatients Number of patients to create
	 */
	public UncontrolledSecondOrderParams(int nPatients) {
		super(nPatients, new UncontrolledT1DMPopulation());
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			registerComplication(new SimpleRETSubmodel());
			registerComplication(new SimpleNPHSubmodel());
		}
		else {
			registerComplication(new SheffieldRETSubmodel());
			registerComplication(new SheffieldNPHSubmodel());
		}
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());

		registerComplication(new BattelinoSevereHypoglycemiaEvent());

		registerIntervention(new CSIIIntervention());
		registerIntervention(new UncontrolledSAPIntervention());
		
		registerDeathSubmodel(new StandardSpainDeathSubmodel(this));
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
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
