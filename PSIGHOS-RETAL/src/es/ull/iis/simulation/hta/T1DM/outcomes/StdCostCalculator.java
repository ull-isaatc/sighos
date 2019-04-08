/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.outcomes;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * A standard cost calculator that assigns a constant cost to each complication, and then computes the final cost by 
 * aggregating all the costs for the different complications suffered by the patient.
 * Acute events and no complication costs are defined in the constructor. The cost for every stage of each chronic complication
 * is defined by using {@link #addCostForComplicationStage(T1DMComplicationStage, double[])}
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StdCostCalculator implements CostCalculator {

	/** Annual [0] and incident cost [1] associated to each stage of a chronic complication */
	protected final TreeMap<T1DMComplicationStage, double[]> costs;
	/** Cost of diabetes with no complications */ 
	protected final double costNoComplication;
	/** Cost of acute events */
	private final double[] costAcuteEvent;
	
	/**
	 * Creates an instance of the model parameters related to costs.
	 * @param costNoComplication Cost of diabetes with no complications 
	 * @param costAcuteEvent Cost of acute events
	 */
	public StdCostCalculator(double costNoComplication, double[] costAcuteEvent) {
		this.costAcuteEvent = costAcuteEvent;
		this.costNoComplication = costNoComplication;
		costs = new TreeMap<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = ((T1DMMonitoringIntervention)pat.getIntervention()).getAnnualCost(pat);
		final Collection<T1DMComplicationStage> state = pat.getDetailedState();
		// No complications
		if (state.isEmpty()) {
			cost += costNoComplication;
		}
		else {
			for (T1DMComplicationStage st : state) {
				if (costs.containsKey(st)) {
					cost += costs.get(st)[0];
				}
			}
		}
		return cost;
	}
	
	@Override
	public double getCostForAcuteEvent(T1DMPatient pat, T1DMAcuteComplications comp) {
		return costAcuteEvent[comp.ordinal()];
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComplicationStage newEvent) {
		if (costs.containsKey(newEvent))
			return costs.get(newEvent)[1];
		return 0;
	}

	/**
	 * Adds annual and transition costs for a complication stage
	 * @param stage A complication stage
	 * @param costs The annual cost (costs[0]) of being in this complication stage, and the cost of starting this complication stage (costs[1])
	 */
	public void addCostForComplicationStage(T1DMComplicationStage stage, double[] costs) {
		this.costs.put(stage, costs);
	}

}
