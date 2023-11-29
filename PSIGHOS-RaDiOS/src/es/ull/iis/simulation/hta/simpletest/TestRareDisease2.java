/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.calculators.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;
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
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease2(SecondOrderParamsRepository secParams) {
		super(secParams, "RD2", "Test rare disease 2");
		acuteManif1 = new TestAcuteManifestation1(secParams, this);
		ParameterCalculator tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(acuteManif1), secParams, acuteManif1);
		new DiseaseProgressionPathway(secParams, acuteManif1, tte);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, acuteManif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(acuteManif1));
		return list;
	}
}
