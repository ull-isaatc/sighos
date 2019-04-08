/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.outcomes;

import java.util.Collection;

import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;

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
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = ((T1DMMonitoringIntervention)pat.getIntervention()).getAnnualCost(pat);
		final Collection<T1DMComplicationStage> state = pat.getDetailedState();
		// No complications
		if (state.isEmpty()) {
			cost += costNoComplication;
		}
		else {
			// Check each complication
			for (T1DMChronicComplications comp : T1DMChronicComplications.values()) {
				if (pat.hasComplication(comp)) {
					cost += chronicSubmodels[comp.ordinal()].getAnnualCostWithinPeriod(pat, initAge, endAge);
				}		
			}
		}
		return cost;
	}
	
	@Override
	public double getCostForAcuteEvent(T1DMPatient pat, T1DMAcuteComplications comp) {
		return acuteSubmodels[comp.ordinal()].getCostOfComplication(pat);
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComplicationStage newEvent) {
		return chronicSubmodels[newEvent.getComplication().ordinal()].getCostOfComplication(pat, newEvent);
	}

}
