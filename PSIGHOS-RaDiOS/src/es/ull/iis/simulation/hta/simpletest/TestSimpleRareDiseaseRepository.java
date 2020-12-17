/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.params.SecondOrderInterventionSpecificRR;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestSimpleRareDiseaseRepository extends SecondOrderParamsRepository {
	private final static int TEST = 4;
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;
	private final SecondOrderInterventionSpecificRR interventionRR;
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param population
	 */
	public TestSimpleRareDiseaseRepository(int nRuns, int nPatients) {
		super(nRuns, nPatients);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);
		Disease testDisease = null;
		switch (TEST) {
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
		registerPopulation(new TestPopulation(testDisease));
		registerDisease(testDisease);
		registerIntervention(new NullIntervention(this));
		final Intervention int2 = new EffectiveIntervention(this);
		registerIntervention(int2);
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		interventionRR = new SecondOrderInterventionSpecificRR(this, new SecondOrderParam[] {otherParams.get(STR_RR_PREFIX + int2)});
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}
	
	public RRCalculator getInterventionRR() {
		return interventionRR;
	}

	private static class NullIntervention extends Intervention {

		public NullIntervention(SecondOrderParamsRepository secParams) {
			super(secParams, "NONE", "Null intervention");
		}

		@Override
		public void generate() {
		}

		@Override
		public void registerSecondOrderParameters() {
		}

		@Override
		public double getAnnualCost(Patient pat) {
			return 0;
		}
		
	}

	private static class EffectiveIntervention extends Intervention {
		private final static double ANNUAL_COST = 200.0; 

		public EffectiveIntervention(SecondOrderParamsRepository secParams) {
			super(secParams, "INTERV", "Effective intervention");
		}

		@Override
		public void generate() {
		}

		@Override
		public void registerSecondOrderParameters() {
			secParams.addOtherParam(new SecondOrderParam(secParams, STR_RR_PREFIX + this, 
					"Relative risk of developing manifestations", "Test", 0.5, RandomVariateFactory.getInstance("UniformVariate", 0.4, 0.6)));
		}

		@Override
		public double getAnnualCost(Patient pat) {
			return ANNUAL_COST;
		}
		
	}
}