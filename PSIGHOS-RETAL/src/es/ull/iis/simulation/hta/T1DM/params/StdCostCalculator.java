/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * Model parameters related to costs. 
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StdCostCalculator implements CostCalculator {

	/** Annual and incident cost of complications */
	protected final TreeMap<T1DMComorbidity, double[]> costs;
	/** Cost of diabetes with no complications */ 
	protected final double costNoComplication;
	/** Cost of a severe hypoglycemic event */
	private final double costHypoglycemicEvent;
	
	/**
	 * Creates an instance of the model parameters related to costs.
	 * @param secParams Second order parameters to be used
	 */
	public StdCostCalculator(double costNoComplication, double costHypoglycemicEvent) {
		this.costHypoglycemicEvent = costHypoglycemicEvent;
		this.costNoComplication = costNoComplication;
		costs = new TreeMap<>();
	}

	/**
	 * Return the annual cost for the specified patient during a period of time. The initAge and endAge parameters
	 * can be used to select different frequency of treatments according to the age of the patient 
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return the annual cost for the specified patient during a period of time.
	 */
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = ((T1DMMonitoringIntervention)pat.getIntervention()).getAnnualCost(pat);
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		// No complications
		if (state.isEmpty()) {
			cost += costNoComplication;
		}
		else {
			for (T1DMComorbidity st : state) {
				if (costs.containsKey(st)) {
					cost += costs.get(st)[0];
				}
			}
		}
		return cost;
	}
	
	/**
	 * Returns the cost of a severe hypoglycemic episode
	 * @param pat A patient
	 * @return the cost of a severe hypoglycemic episode
	 */
	public double getCostForSevereHypoglycemicEpisode(T1DMPatient pat) {
		return costHypoglycemicEvent;
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		if (costs.containsKey(newEvent))
			return costs.get(newEvent)[1];
		return 0;
	}

	/**
	 * Adds annual and transition costs for a health state
	 * @param state A health state
	 * @param costs The annual cost (costs[0]) of being in this health state, and the cost of starting this health state (costs[1])
	 */
	public void addCostForHealthState(T1DMComorbidity state, double[] costs) {
		this.costs.put(state, costs);
	}

}
