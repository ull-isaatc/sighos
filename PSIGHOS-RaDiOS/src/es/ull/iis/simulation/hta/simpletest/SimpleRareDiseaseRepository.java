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
import es.ull.iis.simulation.hta.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SimpleRareDiseaseRepository extends SecondOrderParamsRepository {
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;
	private final InterventionSpecificComplicationRR[] interventionRR;
	
	/**
	 * @param nRuns
	 * @param nPatients
	 * @param population
	 */
	public SimpleRareDiseaseRepository(int nRuns, int nPatients) {
		super(nRuns, nPatients);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);
		final Disease testDisease = new TestRareDisease1(this);
		registerPopulation(new TestPopulation(testDisease));
		registerDisease(testDisease);
		registerIntervention(new NullIntervention(this));
		registerIntervention(new EffectiveIntervention(this));
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		interventionRR = new InterventionSpecificComplicationRR[nRuns + 1];
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}
	
	@Override
	public void generate() {
		super.generate();
		for (int i = 0; i < getnRuns(); i++)
			interventionRR[i] = new InterventionSpecificComplicationRR(new double[] {0.0, getOtherParam(STR_RR_PREFIX + getRegisteredInterventions()[1], i)});
	}
	
	public RRCalculator getInterventionRR(int id) {
		return interventionRR[id];
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
			secParams.addOtherParam(new SecondOrderParam(secParams, STR_RR_PREFIX + this, 
					"Relative risk of developing manifestations", "Test", 0.5));
		}

		@Override
		public double getAnnualCost(Patient pat) {
			return ANNUAL_COST;
		}
		
	}
}
