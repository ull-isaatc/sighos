/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;

/**
 * A disease with a single acute manifestation with recurrent episodes
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease2 extends TemplateTestRareDisease {
	final private static double P_MANIF1 = 0.1;
	/** The acute manifestation */
	final private Manifestation acuteManif1;
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease2(SecondOrderParamsRepository secParams) {
		super(secParams, "RD2", "Test rare disease 2");
		acuteManif1 = new TestAcuteManifestation1(secParams, this);
		TimeToEventCalculator tte = new AnnualRiskBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROBABILITY.getParameterName(acuteManif1), secParams, acuteManif1);
		new ManifestationPathway(secParams, acuteManif1, tte);
	}

	@Override
	public void registerSecondOrderParameters() {
		ProbabilityParamDescriptions.PROBABILITY.addParameter(secParams, acuteManif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(ProbabilityParamDescriptions.PROBABILITY.getParameterName(acuteManif1));
		return list;
	}
}
