/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.util.EnumSet;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
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
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderChronicComplicationSubmodel;
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
public class DiabPlusSecondOrderRepository extends SecondOrderParamsRepository {

	protected DiabPlusSecondOrderRepository(int nPatients, DiabPlusStdPopulation population, double hypoRate, double baseHbA1cLevel, double objHbA1cLevel, double annualCost) {
		super(nPatients, population);

		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual rate of severe hypoglycemia events", "Assumption", hypoRate), 
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "No RR", "Assumption", 1.0), 
				EnumSet.of(DiabetesType.T1), true);
		registerComplication(hypoEvent);
		
		registerIntervention(new DiabPlusStdIntervention(baseHbA1cLevel, 0.0, true));
		registerIntervention(new DiabPlusStdIntervention(objHbA1cLevel, annualCost, false));

		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
//        System.out.println(getComplicationsJSON());
	}


	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}

	public JSONObject getComplicationsJSON() {
		final JSONObject json = new JSONObject();
		final JSONArray jcomps = new JSONArray();
		
		for (SecondOrderChronicComplicationSubmodel comp : getRegisteredChronicComplications()) {
			final JSONObject jcomp = new JSONObject();
			final JSONArray jstages = new JSONArray();
			for (DiabetesComplicationStage stage : comp.getStages()) {
				final JSONObject jstage = new JSONObject();
				jstage.put("name", stage.name());
				jstage.put("description", stage.getDescription());
				jstages.put(jstage);
			}
			jcomp.put("manifestations", jstages);
			jcomp.put("name", comp.getComplicationType().getDescription());
			jcomps.put(jcomp);
		}
		json.put("complications", jcomps);
		return json;
	}
}
