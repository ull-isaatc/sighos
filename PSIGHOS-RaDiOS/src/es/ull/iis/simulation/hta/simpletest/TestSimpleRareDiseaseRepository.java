/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.DiffConstantParameterModifier;
import es.ull.iis.simulation.hta.params.FactorConstantParameterModifier;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.SetConstantParameterModifier;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestSimpleRareDiseaseRepository extends SecondOrderParamsRepository {
	private final static int TEST_POPULATION = 1;
	private final static int TEST_INTERVENTIONS = 1;
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param population
	 */
	public TestSimpleRareDiseaseRepository(int nRuns, int nPatients, int disease) {
		super(nRuns, nPatients);

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
				setPopulation(new TestPopulation(this, testDisease)); break;
			case 2:
				setPopulation(new TestNotDiagnosedPopulation(this, testDisease)); break;
			default:
				setPopulation(new TestPopulation(this, testDisease)); break;		
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
