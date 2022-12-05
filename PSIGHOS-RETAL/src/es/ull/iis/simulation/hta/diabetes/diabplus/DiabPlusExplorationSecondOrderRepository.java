<<<<<<< Upstream, based on origin/master
/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
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
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
import es.ull.iis.util.Statistics;

/**
 * @author icasrod
 *
 */
public class DiabPlusExplorationSecondOrderRepository extends SecondOrderParamsRepository {
	/** Lower and upper limits for severe hypoglycemia event rates. Source: Frier BM. The incidence and impact of hypoglycemia in type 1 and type 2 diabetes. International Diabetes Monitor 2009;21:210–218 */
	private static final double[] SHE_LIMITS = new double[] {1.0, 1.7};  

	protected DiabPlusExplorationSecondOrderRepository(int nPatients, DiabPlusStdPopulation population, int[] hba1cLimits) {
		super(nPatients, population);

		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual rate of severe hypoglycemia events", 
						"Frier BM. The incidence and impact of hypoglycemia in type 1 and type 2 diabetes. International Diabetes Monitor 2009;21:210–218", 
						(SHE_LIMITS[0] + SHE_LIMITS[1]) / 2, "UniformVariate", SHE_LIMITS[0], SHE_LIMITS[1]), 
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "No RR", "Assumption", 1.0), 
				EnumSet.of(DiabetesType.T1), true);
		registerComplication(hypoEvent);
		
		for (int i = hba1cLimits[0]; i <= hba1cLimits[1]; i++)
			registerIntervention(new DiabPlusExplorationStdIntervention(0.0, i, true));

		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
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
=======
/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.util.ArrayList;
import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
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
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
import es.ull.iis.util.Statistics;

/**
 * @author icasrod
 *
 */
public class DiabPlusExplorationSecondOrderRepository extends SecondOrderParamsRepository {
	/** Lower and upper limits for severe hypoglycemia event rates. Source: Frier BM. The incidence and impact of hypoglycemia in type 1 and type 2 diabetes. International Diabetes Monitor 2009;21:210–218 */
	private static final double[] SHE_LIMITS = new double[] {1.0, 1.7};  

	protected DiabPlusExplorationSecondOrderRepository(int nPatients, DiabPlusStdPopulation population, ArrayList<Double> hba1cLevels) {
		super(nPatients, population);

		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual rate of severe hypoglycemia events", 
						"Frier BM. The incidence and impact of hypoglycemia in type 1 and type 2 diabetes. International Diabetes Monitor 2009;21:210–218", 
						(SHE_LIMITS[0] + SHE_LIMITS[1]) / 2, "UniformVariate", SHE_LIMITS[0], SHE_LIMITS[1]), 
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "No RR", "Assumption", 1.0), 
				EnumSet.of(DiabetesType.T1), true);
		registerComplication(hypoEvent);
		
		for (double hba1cLevel : hba1cLevels)
			registerIntervention(new DiabPlusExplorationStdIntervention(0.0, hba1cLevel));

		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
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
>>>>>>> c063eda Added exploratory classes to diabplus to generate data for ML
