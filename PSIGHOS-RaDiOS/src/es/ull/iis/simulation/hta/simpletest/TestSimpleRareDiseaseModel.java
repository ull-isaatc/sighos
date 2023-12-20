/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.modifiers.DiffConstantParameterModifier;
import es.ull.iis.simulation.hta.params.modifiers.FactorConstantParameterModifier;
import es.ull.iis.simulation.hta.params.modifiers.SetConstantParameterModifier;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestSimpleRareDiseaseModel extends HTAModel {
	private final static int TEST_POPULATION = 1;
	private final static int TEST_INTERVENTIONS = 1;
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param population
	 */
	public TestSimpleRareDiseaseModel(HTAExperiment experiment, int disease) {
		super(experiment);

		TemplateTestRareDisease testDisease = null;
		try {
			switch (disease) {
			case 2:
				testDisease = new TestRareDisease2(this); break;
			case 3:
				testDisease = new TestRareDisease3(this); break;
			case 4:
				testDisease = new TestRareDisease4(this); break;
			case 1:
			default:
				testDisease = new TestRareDisease1(this); break;
			}
			switch(TEST_POPULATION) {
			case 1:
				new TestPopulation(this, testDisease); break;
			case 2:
				new TestNotDiagnosedPopulation(this, testDisease); break;
			default:
				new TestPopulation(this, testDisease); break;		
			}
			
			switch(TEST_INTERVENTIONS) {
			case 2:
				new DoNothingIntervention(this);
				new MortalityReductionIntervention(this, new DiffConstantParameterModifier(-5.0));
				break;
			case 3:
				new DoNothingIntervention(this);
				new MortalityReductionIntervention(this, new FactorConstantParameterModifier(1.2));
				break;
			case 4:
				new DoNothingIntervention(this);
				new MortalityReductionIntervention(this, new SetConstantParameterModifier(90.0));
				break;
			case 5:
				new DoNothingIntervention(this);
				new BasicNewbornScreening(this);
				break;
			case 1:
			default:
				new EffectiveIntervention(this, testDisease.getParamNames());
				new DoNothingIntervention(this);
				break;
			}
		} catch(MalformedSimulationModelException e) {
			e.printStackTrace();			
		}
	}
}
