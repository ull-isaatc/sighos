/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.HTAModel;
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
	 * @param model Repository with common information about the disease 
	 */
	public TestRareDisease4(HTAModel model) {
		super(model, "RD4", "Test rare disease 4");
		manif1 = new TestManifestationStage1(model, this);
		manif2 = new TestManifestationStage2(model, this);
		acuteManif1 = new TestAcuteManifestation1(model, this);
		new DiseaseProgressionPathway(model, manif1, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1));
		new DiseaseProgressionPathway(model, manif2, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif2));
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
		new DiseaseProgressionPathway(model, manif2, cond, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1, manif2)); 
		new DiseaseProgressionPathway(model, acuteManif1, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(acuteManif1));
	}

	@Override
	public void createParameters() {
		RiskParamDescriptions.PROBABILITY.addParameter(model, acuteManif1, "Test", P_ACUTE_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_ACUTE_MANIF1));
		RiskParamDescriptions.PROBABILITY.addParameter(model, manif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		RiskParamDescriptions.PROBABILITY.addParameter(model, manif2, "Test", P_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF2));
		RiskParamDescriptions.PROBABILITY.addParameter(model, manif1, manif2, 
				"Test", P_MANIF1_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1_MANIF2));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(model, new AnnualRiskBasedTimeToEventParameter(model, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1), manif1, 
				RiskParamDescriptions.PROBABILITY.getParameterName(manif1)));		
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(model, new AnnualRiskBasedTimeToEventParameter(model, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif2), manif2, 
				RiskParamDescriptions.PROBABILITY.getParameterName(manif2)));		
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(model, new AnnualRiskBasedTimeToEventParameter(model, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif1, manif2), manif2, 
				RiskParamDescriptions.PROBABILITY.getParameterName(manif1, manif2)));		
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(model, new AnnualRiskBasedTimeToEventParameter(model, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(acuteManif1), acuteManif1, 
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
