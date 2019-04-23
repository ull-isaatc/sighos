/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.outcomes;

import java.util.Collection;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;

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
	public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
		double cost = pat.getIntervention().getAnnualCost(pat);
		final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
		// No complications
		if (state.isEmpty()) {
			cost += costNoComplication;
		}
		else {
			// Check each complication
			for (DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
				if (pat.hasComplication(comp)) {
					cost += chronicSubmodels[comp.ordinal()].getAnnualCostWithinPeriod(pat, initAge, endAge);
				}		
			}
		}
		return cost;
	}
	
	@Override
	public double getCostForAcuteEvent(DiabetesPatient pat, DiabetesAcuteComplications comp) {
		return acuteSubmodels[comp.ordinal()].getCostOfComplication(pat);
	}

	@Override
	public double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newEvent) {
		return chronicSubmodels[newEvent.getComplication().ordinal()].getCostOfComplication(pat, newEvent);
	}

}
