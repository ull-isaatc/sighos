/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.condition.PreviousDiseaseProgressionCondition;

/**
 * A disease with two chronic (and independent) manifestations. The first manifestation increases the risk of the second. 
 * The intervention reduces the risk of the second manifestation
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease3 extends TemplateTestRareDisease {
	final private static double P_MANIF1 = 0.1;
	final private static double P_MANIF2 = 0.02;
	final private static double P_MANIF1_MANIF2 = 0.05;
	/** First manifestation (mild) */
	final private DiseaseProgression manif1;
	/** Second manifestation (severe) */
	final private DiseaseProgression manif2;
	
	/**
	 * @param model Repository with common information about the disease 
	 */
	public TestRareDisease3(HTAModel model) {
		super(model, "RD3", "Test rare disease 3");
		manif1 = new TestManifestationStage1(model, this);
		manif2 = new TestManifestationStage2(model, this);
		new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1);
		new DiseaseProgressionPathway(model, "PATHWAY2", "Pathway to chronic manifestation 2", manif2); 
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
		new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, cond); 
	}

	@Override
	public void createParameters() {
		StandardParameter.PROBABILITY.addParameter(model, manif1, "Test", P_MANIF1, StandardParameter.getRandomVariateForProbability(P_MANIF1));
		StandardParameter.PROBABILITY.addParameter(model, manif2, "Test", P_MANIF2, StandardParameter.getRandomVariateForProbability(P_MANIF2));
		StandardParameter.PROBABILITY.addParameter(model, manif1.name() + "_" + manif2.name(), "Probability from manifestation 1 to manifestation 2",
				"Test", P_MANIF1_MANIF2, StandardParameter.getRandomVariateForProbability(P_MANIF1_MANIF2));
		model.addParameter(new AnnualRiskBasedTimeToEventParameter(model, StandardParameter.TIME_TO_EVENT.createName(manif1), "Time to chronic  manifestation 1", "", HTAModel.getStudyYear(), manif1));
		model.addParameter(new AnnualRiskBasedTimeToEventParameter(model, StandardParameter.TIME_TO_EVENT.createName(manif2), "Time to chronic  manifestation 2", "", HTAModel.getStudyYear(), manif2));
		final Parameter tte = new AnnualRiskBasedTimeToEventParameter(model, manif1.name() + "_" + manif2.name(), "Time from chronic manifestation 1 to chronic manifestation 2", "", HTAModel.getStudyYear(), manif2);
		tte.setUsedParameterName(AnnualRiskBasedTimeToEventParameter.USED_PARAMETERS.PROB, StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()));
		model.addParameter(tte);
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(StandardParameter.PROBABILITY.createName(manif1));
		list.add(StandardParameter.PROBABILITY.createName(manif2));
		list.add(StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()));		
		return list;
	}
}
