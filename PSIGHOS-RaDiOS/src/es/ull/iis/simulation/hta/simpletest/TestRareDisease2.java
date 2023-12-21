/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.AnnualRiskBasedTimeToEventCalculator;

/**
 * A disease with a single acute manifestation with recurrent episodes
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease2 extends TemplateTestRareDisease {
	final private static double P_MANIF1 = 0.1;
	/** The acute manifestation */
	final private DiseaseProgression acuteManif1;
	
	/**
	 * @param model Repository with common information about the disease 
	 */
	public TestRareDisease2(HTAModel model) {
		super(model, "RD2", "Test rare disease 2");
		acuteManif1 = new TestAcuteManifestation1(model, this);
		new DiseaseProgressionPathway(model, "PATH_ACUTE1", "Pathway to acute manifestation 1", acuteManif1,
			new AnnualRiskBasedTimeToEventCalculator(acuteManif1, StandardParameter.PROBABILITY.createName(acuteManif1)));
	}

	@Override
	public void createParameters() {
		StandardParameter.PROBABILITY.addToModel(model, acuteManif1, "Test", P_MANIF1, StandardParameter.getRandomVariateForProbability(P_MANIF1));
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(StandardParameter.PROBABILITY.createName(acuteManif1));
		return list;
	}
}
