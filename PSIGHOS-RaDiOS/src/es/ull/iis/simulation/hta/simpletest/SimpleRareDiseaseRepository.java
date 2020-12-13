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
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.Population;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleRareDiseaseRepository extends SecondOrderParamsRepository {
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param population
	 */
	public SimpleRareDiseaseRepository(int nRuns, int nPatients, Population population) {
		super(nRuns, nPatients, population);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);
		registerDisease(new TestRareDisease1(this));
		registerIntervention(new NullIntervention(this));
		registerIntervention(new EffectiveIntervention(this));
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}

	private static class NullIntervention extends Intervention {

		public NullIntervention(SecondOrderParamsRepository secParams) {
			super(secParams, "NONE", "Null intervention");
			// TODO Auto-generated constructor stub
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
			// TODO Auto-generated constructor stub
		}

		@Override
		public void generate() {
		}

		@Override
		public void registerSecondOrderParameters() {
			secParams.addOtherParam(new SecondOrderParam(secParams, SecondOrderParamsRepository.STR_RR_PREFIX + this, 
					"Relative risk of developing manifestations", "Test", 0.5));
		}

		@Override
		public double getAnnualCost(Patient pat) {
			return ANNUAL_COST;
		}
		
	}
}
