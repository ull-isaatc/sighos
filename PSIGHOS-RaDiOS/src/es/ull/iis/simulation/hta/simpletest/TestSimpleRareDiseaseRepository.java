/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.progression.Modification;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestSimpleRareDiseaseRepository extends SecondOrderParamsRepository {
	private final static int TEST_POPULATION = 1;
	private final static int TEST_INTERVENTIONS = 1;
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param population
	 */
	public TestSimpleRareDiseaseRepository(int nRuns, int nPatients, int disease) {
		super(nRuns, nPatients);

		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);

		Disease testDisease = null;
		switch (disease) {
		case 1: 
			testDisease = new TestRareDisease1(this);
			break;
		case 2:
			testDisease = new TestRareDisease2(this);
			break;
		case 3:
			testDisease = new TestRareDisease3(this);
			break;
		case 4:
			testDisease = new TestRareDisease4(this);
			break;
		default:
			testDisease = new TestRareDisease1(this);
			break;
		}
		registerDisease(testDisease);
		switch(TEST_POPULATION) {
		case 1:
			registerPopulation(new TestPopulation(this, testDisease));
			break;
		case 2:
			registerPopulation(new TestNotDiagnosedPopulation(this, testDisease));
			break;
		default:
			registerPopulation(new TestPopulation(this, testDisease));
			break;		
		}
		switch(TEST_INTERVENTIONS) {
		case 2:
			registerIntervention(new NullIntervention(this));
			registerIntervention(new MortalityReductionIntervention(this, Modification.Type.DIFF));
			break;
		case 3:
			registerIntervention(new NullIntervention(this));
			registerIntervention(new MortalityReductionIntervention(this, Modification.Type.RR));
			break;
		case 4:
			registerIntervention(new NullIntervention(this));
			registerIntervention(new MortalityReductionIntervention(this, Modification.Type.SET));
			break;
		case 5:
			registerIntervention(new NullIntervention(this));
			registerIntervention(new BasicNewbornScreening(this));
			break;
		case 1:
		default:
			registerIntervention(new EffectiveIntervention(this));
			registerIntervention(new NullIntervention(this));
			break;
		}
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}
}
