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
	final private DiseaseProgressionPathway pathway1;
	final private DiseaseProgressionPathway pathway2;
	
	/**
	 * @param model Repository with common information about the disease 
	 */
	public TestRareDisease1(HTAModel model) {
		super(model, "RD1", "Test rare disease 1");
		manif1 = new TestManifestationStage1(model, this);
		manif2 = new TestManifestationStage2(model, this);
		pathway1 = new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1);
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
		pathway2 = new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, cond); 
		addExclusion(manif2, manif1);
	}

	@Override
	public void createParameters() {
		// TODO: This does not work: there is a mess with the names of the parameters. Return to TimeToEventCalculator?
		pathway1.addParameter(StandardParameter.TIME_TO_EVENT, new AnnualRiskBasedTimeToEventParameter(model, StandardParameter.TIME_TO_EVENT.createName(pathway1), "Time to chronic  manifestation 1", "", HTAModel.getStudyYear(), manif1));
		StandardParameter.PROBABILITY.addParameter(model, manif1, "Test", P_MANIF1, StandardParameter.getRandomVariateForProbability(P_MANIF1));
		StandardParameter.PROBABILITY.addParameter(model, manif1.name() + "_" + manif2.name(), "Probability from manifestation 1 to manifestation 2",
				"Test", P_MANIF1_MANIF2, StandardParameter.getRandomVariateForProbability(P_MANIF1_MANIF2));
		final Parameter tte = new AnnualRiskBasedTimeToEventParameter(model, manif1.name() + "_" + manif2.name(), "Time from chronic manifestation 1 to chronic manifestation 2", "", HTAModel.getStudyYear(), manif2);
		tte.setUsedParameterName(AnnualRiskBasedTimeToEventParameter.USED_PARAMETERS.PROB, StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()));
		model.addParameter(tte);
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(StandardParameter.PROBABILITY.createName(manif1));
		list.add(StandardParameter.PROBABILITY.createName(manif1.name() + "_" + manif2.name()));		
		return list;
	}

}
