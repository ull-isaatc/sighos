package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Modification;
import es.ull.iis.simulation.hta.progression.Transition;
import simkit.random.RandomVariateFactory;

/**
 * An intervention that delays all the transitions
 * @author Iván Castilla Rodríguez
 *
 */
public class EffectiveIntervention extends Intervention {
	private final static double ANNUAL_COST = 200.0; 
	private final static double RR = 0.5;

	public EffectiveIntervention(SecondOrderParamsRepository secParams) {
		super(secParams, "INTERV", "Effective intervention");
	}

	@Override
	public void registerSecondOrderParameters() {
		for (Transition trans : secParams.getRegisteredDiseases()[0].getTransitions()) {
			secParams.addModificationParam(this, Modification.Type.RR, trans.getSrcManifestation(), trans.getDestManifestation(),  
					"Test", RR, RandomVariateFactory.getInstance("UniformVariate", RR * 0.8, RR * 1.2));
		}
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return ANNUAL_COST;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return 0;
	}
	
}