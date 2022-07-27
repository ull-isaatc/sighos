package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import simkit.random.RandomVariateFactory;

/**
 * An intervention that delays all the transitions
 * @author Iván Castilla Rodríguez
 *
 */
public class EffectiveIntervention extends Intervention {
	private final static double ANNUAL_COST = 200.0; 
	private final static double RR = 0.5;
	final ArrayList<String> modifiedParams;

	public EffectiveIntervention(SecondOrderParamsRepository secParams, ArrayList<String> modifiedParams) {
		super(secParams, "InterventionEffective", "Effective intervention");
		this.modifiedParams = modifiedParams;
	}

	@Override
	public void registerSecondOrderParameters() {
		for (String paramName : modifiedParams) {
			secParams.addModificationParam(this, Modification.Type.RR, paramName,  
					"Test", RR, RandomVariateFactory.getInstance("UniformVariate", RR * 0.8, RR * 1.2));
		}
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return discountRate.applyDiscount(ANNUAL_COST, initT, endT);
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return 0;
	}
	
}