/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;

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
		new DiseaseProgressionPathway(model, acuteManif1, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(acuteManif1));
	}

	@Override
	public void createParameters() {
		RiskParamDescriptions.PROBABILITY.addParameter(model, acuteManif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(model, new AnnualRiskBasedTimeToEventParameter(model, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(acuteManif1), acuteManif1, 
				RiskParamDescriptions.PROBABILITY.getParameterName(acuteManif1)));		
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(acuteManif1));
		return list;
	}
}
