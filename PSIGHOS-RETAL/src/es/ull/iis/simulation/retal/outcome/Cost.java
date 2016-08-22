/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.OphthalmologicResourceUsage;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.ResourceUsageItem;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Cost extends Outcome {
	private final double[][]costs = new double[RETALSimulation.NPATIENTS][RETALSimulation.NINTERVENTIONS];
	private final double[]aggregated = new double[RETALSimulation.NINTERVENTIONS];
	
	public Cost(RETALSimulation simul, double discountRate) {
		super(simul, "Cost", "€", discountRate);
	}

	@Override
	public double[] getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Computes the cost associated to the current state of the patient
	 * @param pat The patient who updates the cost 
	 */
	@Override
	public void update(Patient pat) {
		OphthalmologicPatient p = (OphthalmologicPatient)pat;
		final double initAge = TimeUnit.YEAR.convert(pat.getLastTs(), simul.getTimeUnit()); 
		final double endAge = TimeUnit.YEAR.convert(pat.getTs(), simul.getTimeUnit()); 
		double cost = 0.0;
		for(EyeState stage : p.getEyeState(0)) {
			// FIXME: Check if res == null
			final ResourceUsageItem[] res = OphthalmologicResourceUsage.getResourceUsageItems(stage);
			if (res != null) {
				for (ResourceUsageItem usage : res) {
					cost += usage.computeCost(initAge, endAge);
				}
			}
		}
		for(EyeState stage : p.getEyeState(1)) {
			final ResourceUsageItem[] res = OphthalmologicResourceUsage.getResourceUsageItems(stage);
			if (res != null) {
				for (ResourceUsageItem usage : res) {
					cost += usage.computeCost(initAge, endAge);
				}
			}
		}

		final int patientId = pat.getIdentifier() / RETALSimulation.NINTERVENTIONS;
		final int interventionId = pat.getIdentifier() % RETALSimulation.NINTERVENTIONS;
		cost = applyDiscount(cost, initAge, endAge);
		costs[patientId][interventionId] += cost;
		aggregated[interventionId] += cost;
	}

	@Override
	public void print(boolean detailed) {
		// TODO Auto-generated method stub
		
	}

}
