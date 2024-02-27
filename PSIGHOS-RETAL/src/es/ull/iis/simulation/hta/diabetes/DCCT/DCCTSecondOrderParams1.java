/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.DCCT;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.interventions.DCCTConventionalIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.DCCTIntensiveIntervention1;
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
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSpainDeathSubmodel;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class DCCTSecondOrderParams1 extends SecondOrderParamsRepository {
	private static final double []P_INI_NEU_BETA = {8 + 17, 378 + 348 - 8 - 17}; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final String DEF_SOURCE = "DCCT: https://www.ncbi.nlm.nih.gov/pubmed/90007053";
	private static final double C_HYPO_EPISODE = 3755;		// Assumption
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	
	/**
	 * @param nPatients Number of patients to create
	 */
	public DCCTSecondOrderParams1(int nPatients) {
		super(nPatients, new DCCTPrimaryIntensivePopulation());

		addProbParam(new SecondOrderParam(getInitProbString(SimpleNEUSubmodel.NEU), "Initial probability of neuropathy", 
				"DDCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401", P_INI_NEU_BETA[0] / (P_INI_NEU_BETA[0] + P_INI_NEU_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_NEU_BETA[0], P_INI_NEU_BETA[1])));
		
		
		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		registerComplication(new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
						DEF_SOURCE, 0.187, RandomVariateFactory.getInstance("BetaVariate", 18.513, 80.487)),
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
					DEF_SOURCE,	3.27272727, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", 1.1856237, 0.22319455))),
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "Assumption", DU_HYPO_EPISODE),
				new SecondOrderCostParam(StandardSevereHypoglycemiaEvent.STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", "Assumption", 2018, C_HYPO_EPISODE),
				StandardSevereHypoglycemiaEvent.getDefaultMortalityParameter(),
				EnumSet.of(DiabetesType.T1)
				));

		registerDeathSubmodel(new StandardSpainDeathSubmodel(this));
		
		registerIntervention(new DCCTConventionalIntervention());
		registerIntervention(new DCCTIntensiveIntervention1());
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
