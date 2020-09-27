/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.AcuteComplication;
import es.ull.iis.simulation.hta.ChronicComplication;
import es.ull.iis.simulation.hta.ComplicationStage;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.submodels.ChronicComplicationSubmodel;

/**
 * A calculator of costs that relies on the submodels to compute each complication cost, and then aggregates all the
 * costs into a single value.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class SubmodelCostCalculator implements CostCalculator {
	/** Submodels for each chronic complication */
	private final ChronicComplicationSubmodel[] chronicSubmodels;
	/** Submodels for each acute complication */
	private final AcuteComplicationSubmodel[] acuteSubmodels;
	/** Cost of diabetes with no complications */ 
	protected final double costNoComplication;

	/**
	 * Creates a cost calculator that relies on the complication submodels to calculate the final cost
	 * @param costNoComplication Cost of diabetes with no complications
	 * @param chronicSubmodels Submodels for each chronic complication
	 * @param acuteSubmodels Submodels for each acute complication
	 */
	public SubmodelCostCalculator(double costNoComplication, ChronicComplicationSubmodel[] chronicSubmodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		this.costNoComplication = costNoComplication;
		this.chronicSubmodels = chronicSubmodels;
		this.acuteSubmodels = acuteSubmodels;
	}

	@Override
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		double cost = pat.getIntervention().getAnnualCost(pat);
		// The management cost is added anyway
		cost += costNoComplication;
		// Check each complication
		for (ChronicComplication comp : ChronicComplication.values()) {
			if (pat.hasComplication(comp)) {
				cost += chronicSubmodels[comp.getInternalId()].getAnnualCostWithinPeriod(pat, initAge, endAge);
			}		
		}
		return cost;
	}

	@Override
	public double getAnnualInterventionCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return pat.getIntervention().getAnnualCost(pat);
	}
	
	@Override
	public double[] getAnnualChronicComplicationCostWithinPeriod(Patient pat, double initAge, double endAge) {
		final double[] costs = new double[ChronicComplication.values().length];
		// Check each complication
		for (ChronicComplication comp : ChronicComplication.values()) {
			if (pat.hasComplication(comp)) {
				costs[comp.getInternalId()] = chronicSubmodels[comp.getInternalId()].getAnnualCostWithinPeriod(pat, initAge, endAge);
			}		
		}
		return costs;
	}
	
	@Override
	public double getCostForAcuteEvent(Patient pat, AcuteComplication comp) {
		return acuteSubmodels[comp.getInternalId()].getCostOfComplication(pat);
	}

	@Override
	public double getCostOfComplication(Patient pat, ComplicationStage newEvent) {
		return chronicSubmodels[newEvent.getComplication().getInternalId()].getCostOfComplication(pat, newEvent);
	}

	@Override
	public double getStdManagementCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return costNoComplication;
	}

}
