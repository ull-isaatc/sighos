/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.PreviousDiseaseProgressionCondition;

/**
 * A disease with two chronic manifestations in increasing order or severity. Each manifestation supposes a new stage in the development of the disease and replaces the previous one
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease1 extends TemplateTestRareDisease {
	final private static double P_MANIF1 = 0.1;
	final private static double P_MANIF1_MANIF2 = 0.3;
	/** First stage of the disease (mild) */
	final private DiseaseProgression manif1;
	/** Second stage of the disease (severe) */
	final private DiseaseProgression manif2;
	
	/**
	 * @param model Repository with common information about the disease 
	 */
	public TestRareDisease1(HTAModel model) {
		super(model, "RD1", "Test rare disease 1");
		manif1 = new TestManifestationStage1(model, this);
		manif2 = new TestManifestationStage2(model, this);
		new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
			new AnnualRiskBasedTimeToEventCalculator(manif1, StandardParameter.PROBABILITY.createName(manif1)));
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
		new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, 
			new AnnualRiskBasedTimeToEventCalculator(manif1, StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name())), cond); 
		addExclusion(manif2, manif1);
	}

	@Override
	public void createParameters() {
		StandardParameter.PROBABILITY.addToModel(model, manif1, "Test", P_MANIF1, Parameter.getRandomVariateForProbability(P_MANIF1));
		StandardParameter.PROBABILITY.addToModel(model, StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()), "Probability from manifestation 1 to manifestation 2",
				"Test", P_MANIF1_MANIF2, Parameter.getRandomVariateForProbability(P_MANIF1_MANIF2));
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(StandardParameter.PROBABILITY.createName(manif1));
		list.add(StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()));		
		return list;
	}

}
