/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import java.util.ArrayList;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.params.ResourceUsageParam;
import es.ull.iis.simulation.retal.params.ResourceUsageItem;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Cost extends Outcome {
	private final double[][]costs = new double[RETALSimulation.NPATIENTS][RETALSimulation.NINTERVENTIONS];
	private final double[]aggregated = new double[RETALSimulation.NINTERVENTIONS];
	private final ResourceUsageParam resourceUsage;
	
	public Cost(RETALSimulation simul, double discountRate, ResourceUsageParam resourceUsage) {
		super(simul, "Cost", "€", discountRate);
		this.resourceUsage = resourceUsage;
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
		final double initAge = TimeUnit.YEAR.convert(pat.getLastTs(), simul.getTimeUnit()); 
		final double endAge = TimeUnit.YEAR.convert(pat.getTs(), simul.getTimeUnit()); 
		double cost = 0.0;
		final ArrayList<ResourceUsageItem> res = resourceUsage.getResourceUsageItems(pat);
		if (res != null) {
			for (ResourceUsageItem usage : res) {
				cost += usage.computeCost(initAge, endAge);
			}
		}
		final int interventionId = pat.getnIntervention();
		cost = applyDiscount(cost, initAge, endAge);
		costs[pat.getIdentifier()][interventionId] += cost;
		aggregated[interventionId] += cost;
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
