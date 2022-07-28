/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.PreviousManifestationCondition;

/**
 * A disease with two chronic manifestations in increasing order or severity. Each manifestation supposes a new stage in the development of the disease and replaces the previous one
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease1 extends TemplateTestRareDisease {
	final private static double P_MANIF1 = 0.1;
	final private static double P_MANIF1_MANIF2 = 0.3;
	/** First stage of the disease (mild) */
	final private Manifestation manif1;
	/** Second stage of the disease (severe) */
	final private Manifestation manif2;
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease1(SecondOrderParamsRepository secParams) {
		super(secParams, "RD1", "Test rare disease 1");
		manif1 = new TestManifestationStage1(secParams, this);
		manif2 = new TestManifestationStage2(secParams, this);
		TimeToEventCalculator tte = new AnnualRiskBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROBABILITY.getParameterName(manif1), secParams, manif1);
		new ManifestationPathway(secParams, manif1, tte);
		final Condition<Patient> cond = new PreviousManifestationCondition(manif1);
		tte = new AnnualRiskBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROBABILITY.getParameterName(manif1, manif2), secParams, manif2);
		new ManifestationPathway(secParams, manif2, cond, tte); 
		addExclusion(manif2, manif1);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		ProbabilityParamDescriptions.PROBABILITY.addParameter(secParams, manif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		ProbabilityParamDescriptions.PROBABILITY.addParameter(secParams, manif1, manif2,  
				"Test", P_MANIF1_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1_MANIF2));
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(ProbabilityParamDescriptions.PROBABILITY.getParameterName(manif1));
		list.add(ProbabilityParamDescriptions.PROBABILITY.getParameterName(manif1, manif2));		
		return list;
	}

}
