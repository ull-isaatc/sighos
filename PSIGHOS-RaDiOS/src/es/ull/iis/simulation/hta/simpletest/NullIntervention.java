package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

public class NullIntervention extends Intervention {

	public NullIntervention(SecondOrderParamsRepository secParams, String name) {
		super(secParams, name, "Null intervention");
	}

	public NullIntervention(SecondOrderParamsRepository secParams) {
		super(secParams, "NONE", "Null intervention");
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return 0;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return 0;
	}
	
}