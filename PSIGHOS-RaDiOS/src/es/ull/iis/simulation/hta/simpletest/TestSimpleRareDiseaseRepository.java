/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.effectiveness.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator;
import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestSimpleRareDiseaseRepository extends SecondOrderParamsRepository {
	private final static int TEST_POPULATION = 1;
	private final static int TEST_INTERVENTIONS = 1;
	private final UtilityCalculator utilCalc;
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param population
	 */
	public TestSimpleRareDiseaseRepository(int nRuns, int nPatients, int disease) {
		super(nRuns, nPatients);

		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD);

		TemplateTestRareDisease testDisease = null;
		switch (disease) {
		case 2:
			testDisease = new TestRareDisease2(this);
			break;
		case 3:
			testDisease = new TestRareDisease3(this);
			break;
		case 4:
			testDisease = new TestRareDisease4(this);
			break;
		case 1:
		default:
			testDisease = new TestRareDisease1(this);
			break;
		}
		switch(TEST_POPULATION) {
		case 1:
			setPopulation(new TestPopulation(this, testDisease));
			break;
		case 2:
			setPopulation(new TestNotDiagnosedPopulation(this, testDisease));
			break;
		default:
			setPopulation(new TestPopulation(this, testDisease));
			break;		
		}
		switch(TEST_INTERVENTIONS) {
		case 2:
			new DoNothingIntervention(this);
			new MortalityReductionIntervention(this, Modification.Type.DIFF);
			break;
		case 3:
			new DoNothingIntervention(this);
			new MortalityReductionIntervention(this, Modification.Type.RR);
			break;
		case 4:
			new DoNothingIntervention(this);
			new MortalityReductionIntervention(this, Modification.Type.SET);
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
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}
}
