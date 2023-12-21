/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.PreviousDiseaseProgressionCondition;

/**
 * A disease with two chronic (and independent) manifestations, and one acute manifestation. The first chronic manifestation increases the risk of the second. 
 * The intervention reduces the risk of the acute and the second chronic manifestation
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease4 extends TemplateTestRareDisease {
	final private static double P_ACUTE_MANIF1 = 0.15;
	final private static double P_MANIF1 = 0.1;
	final private static double P_MANIF2 = 0.02;
	final private static double P_MANIF1_MANIF2 = 0.05;
	/** The acute manifestation */
	final private DiseaseProgression acuteManif1;
	/** First manifestation (mild) */
	final private DiseaseProgression manif1;
	/** Second manifestation (severe) */
	final private DiseaseProgression manif2;
	
	/**
	 * @param model Repository with common information about the disease 
	 */
	public TestRareDisease4(HTAModel model) {
		super(model, "RD4", "Test rare disease 4");
		manif1 = new TestManifestationStage1(model, this);
		manif2 = new TestManifestationStage2(model, this);
		acuteManif1 = new TestAcuteManifestation1(model, this);
		new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
			new AnnualRiskBasedTimeToEventCalculator(manif1, StandardParameter.PROBABILITY.createName(manif1)));
		new DiseaseProgressionPathway(model, "PATHWAY2", "Pathway to chronic manifestation 2", manif2,
			new AnnualRiskBasedTimeToEventCalculator(manif2, StandardParameter.PROBABILITY.createName(manif2))); 
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
		new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, 
			new AnnualRiskBasedTimeToEventCalculator(manif1, StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name())), cond);  
		new DiseaseProgressionPathway(model, "PATH_ACUTE1", "Pathway to acute manifestation 1", acuteManif1,
			new AnnualRiskBasedTimeToEventCalculator(acuteManif1, StandardParameter.PROBABILITY.createName(acuteManif1)));
	}

	@Override
	public void createParameters() {
		StandardParameter.PROBABILITY.addToModel(model, acuteManif1, "Test", P_ACUTE_MANIF1, StandardParameter.getRandomVariateForProbability(P_ACUTE_MANIF1));
		StandardParameter.PROBABILITY.addToModel(model, manif1, "Test", P_MANIF1, StandardParameter.getRandomVariateForProbability(P_MANIF1));
		StandardParameter.PROBABILITY.addToModel(model, manif2, "Test", P_MANIF2, StandardParameter.getRandomVariateForProbability(P_MANIF2));
		StandardParameter.PROBABILITY.addToModel(model, StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()), "Probability from manifestation 1 to manifestation 2",
				"Test", P_MANIF1_MANIF2, StandardParameter.getRandomVariateForProbability(P_MANIF1_MANIF2));
	}
	
	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(StandardParameter.PROBABILITY.createName(manif1));
		list.add(StandardParameter.PROBABILITY.createName(manif2));
		list.add(StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()));		
		list.add(StandardParameter.PROBABILITY.createName(acuteManif1));
		return list;
	}
}
