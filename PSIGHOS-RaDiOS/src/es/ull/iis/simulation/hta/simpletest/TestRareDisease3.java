/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.params.ParameterCalculator;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
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
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease3(SecondOrderParamsRepository secParams) {
		super(secParams, "RD3", "Test rare disease 3");
		manif1 = new TestManifestationStage1(secParams, this);
		manif2 = new TestManifestationStage2(secParams, this);
		ParameterCalculator tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(manif1), secParams, manif1);
		new DiseaseProgressionPathway(secParams, manif1, tte);
		tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(manif2), secParams, manif2);
		new DiseaseProgressionPathway(secParams, manif2, tte);
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
		tte = new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(manif1, manif2), secParams, manif2);
		new DiseaseProgressionPathway(secParams, manif2, cond, tte); 
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, manif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, manif2, "Test", P_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF2));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, manif1, manif2, 
				"Test", P_MANIF1_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1_MANIF2));
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(manif1));
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(manif2));
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(manif1, manif2));		
		return list;
	}
}
