/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
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
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease4(SecondOrderParamsRepository secParams) {
		super(secParams, "RD4", "Test rare disease 4");
		manif1 = new TestManifestationStage1(secParams, this);
		manif2 = new TestManifestationStage2(secParams, this);
		acuteManif1 = new TestAcuteManifestation1(secParams, this);
		new DiseaseProgressionPathway(secParams, manif1, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1));
		new DiseaseProgressionPathway(secParams, manif2, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif2));
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
		new DiseaseProgressionPathway(secParams, manif2, cond, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1, manif2)); 
		new DiseaseProgressionPathway(secParams, acuteManif1, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(acuteManif1));
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, acuteManif1, "Test", P_ACUTE_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_ACUTE_MANIF1));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, manif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, manif2, "Test", P_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF2));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, manif1, manif2, 
				"Test", P_MANIF1_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1_MANIF2));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1), manif1, 
				RiskParamDescriptions.PROBABILITY.getParameterName(manif1)));		
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif2), manif2, 
				RiskParamDescriptions.PROBABILITY.getParameterName(manif2)));		
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1, manif2), manif2, 
				RiskParamDescriptions.PROBABILITY.getParameterName(manif1, manif2)));		
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new AnnualRiskBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(acuteManif1), acuteManif1, 
				RiskParamDescriptions.PROBABILITY.getParameterName(acuteManif1)));		
	}
	
	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(manif1));
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(manif2));
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(manif1, manif2));		
		list.add(RiskParamDescriptions.PROBABILITY.getParameterName(acuteManif1));
		return list;
	}
}
