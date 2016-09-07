/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

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
	public void update(Patient pat, double value, double initAge, double endAge) {
		final int interventionId = pat.getnIntervention();
		value = applyDiscount(value, initAge, endAge);
		costs[pat.getIdentifier()][interventionId] += value;
		aggregated[interventionId] += value;
	}

	@Override
	public void update(Patient pat, double value, double age) {
		final int interventionId = pat.getnIntervention();
		value = applyPunctualDiscount(value, age);
		costs[pat.getIdentifier()][interventionId] += value;
		aggregated[interventionId] += value;
	}
	
	@Override
	public void print(boolean detailed) {
		if (detailed) {
			for (int i = 0; i < RETALSimulation.NPATIENTS; i++) {
				System.out.print("[" + i + "]\t");
				for (int j = 0; j < RETALSimulation.NINTERVENTIONS; j++) {
					System.out.print(costs[i][j] + " " + unit + "\t");
				}
				System.out.println();
			}
		}
		System.out.println(this + " summary:");
		for (int j = 0; j < RETALSimulation.NINTERVENTIONS; j++) {
			System.out.print(aggregated[j] / RETALSimulation.NPATIENTS + " " + unit + "\t");
		}
		System.out.println();
	}

}
