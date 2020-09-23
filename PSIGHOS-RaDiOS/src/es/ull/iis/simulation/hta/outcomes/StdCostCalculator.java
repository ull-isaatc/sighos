/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;

/**
 * A standard cost calculator that assigns a constant cost to each complication, and then computes the final cost by 
 * aggregating all the costs for the different complications suffered by the patient.
 * Acute events and no complication costs are defined in the constructor. The cost for every stage of each chronic complication
 * is defined by using {@link #addCostForComplicationStage(DiabetesComplicationStage, double[])}
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StdCostCalculator implements CostCalculator {

	/** Annual [0] and incident cost [1] associated to each stage of a chronic complication */
	protected final TreeMap<DiabetesComplicationStage, double[]> costs;
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
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		double cost = pat.getIntervention().getAnnualCost(pat);
		final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
		cost += costNoComplication;
		for (DiabetesComplicationStage st : state) {
			if (costs.containsKey(st)) {
				cost += costs.get(st)[0];
			}
		}
		return cost;
	}
	
	@Override
	public double getCostForAcuteEvent(Patient pat, DiabetesAcuteComplications comp) {
		return costAcuteEvent[comp.ordinal()];
	}

	@Override
	public double getCostOfComplication(Patient pat, DiabetesComplicationStage newEvent) {
		if (costs.containsKey(newEvent))
			return costs.get(newEvent)[1];
		return 0;
	}

	/**
	 * Adds annual and transition costs for a complication stage
	 * @param stage A complication stage
	 * @param costs The annual cost (costs[0]) of being in this complication stage, and the cost of starting this complication stage (costs[1])
	 */
	public void addCostForComplicationStage(DiabetesComplicationStage stage, double[] costs) {
		this.costs.put(stage, costs);
	}

	@Override
	public double getAnnualInterventionCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return pat.getIntervention().getAnnualCost(pat);
	}

	@Override
	public double[] getAnnualChronicComplicationCostWithinPeriod(Patient pat, double initAge, double endAge) {
		final double[] result = new double[DiabetesChronicComplications.values().length];
		final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
		for (DiabetesComplicationStage st : state) {
			if (costs.containsKey(st)) {
				result[st.ordinal()] = costs.get(st)[0];
			}
		}
		return result;
	}

	@Override
	public double getStdManagementCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return costNoComplication;
	}

}
