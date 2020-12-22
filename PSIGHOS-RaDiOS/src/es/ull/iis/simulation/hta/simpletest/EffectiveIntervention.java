package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import simkit.random.RandomVariateFactory;

public class EffectiveIntervention extends Intervention {
	private final static double ANNUAL_COST = 200.0; 

	public EffectiveIntervention(SecondOrderParamsRepository secParams) {
		super(secParams, "INTERV", "Effective intervention");
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addOtherParam(new SecondOrderParam(secParams, TestSimpleRareDiseaseRepository.STR_RR_PREFIX + this, 
				"Relative risk of developing manifestations", "Test", 0.5, RandomVariateFactory.getInstance("UniformVariate", 0.4, 0.6)));
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return ANNUAL_COST;
	}
	
}