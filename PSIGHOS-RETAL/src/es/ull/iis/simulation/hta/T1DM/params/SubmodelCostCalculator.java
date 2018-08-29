/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Collection;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SubmodelCostCalculator implements CostCalculator {
	private final ComplicationSubmodel[] submodels;
	/** Cost of diabetes with no complications */ 
	protected final double costNoComplication;
	/** Cost of a severe hypoglycemic event */
	private final double costHypoglycemicEvent;
	
	/**
	 * @param costNoComplication
	 * @param costHypoglycemicEvent
	 */
	public SubmodelCostCalculator(double costNoComplication, double costHypoglycemicEvent, ComplicationSubmodel[] submodels) {
		this.costHypoglycemicEvent = costHypoglycemicEvent;
		this.costNoComplication = costNoComplication;
		this.submodels = submodels;
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = ((T1DMMonitoringIntervention)pat.getIntervention()).getAnnualCost(pat);
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		// No complications
		if (state.isEmpty()) {
			cost += costNoComplication;
		}
		else {
			// Check each complication
			for (MainComplications comp : MainComplications.values()) {
				if (pat.hasComplication(comp)) {
					cost += submodels[comp.ordinal()].getAnnualCostWithinPeriod(pat, initAge, endAge);
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
		return submodels[newEvent.getComplication().ordinal()].getCostOfComplication(pat, newEvent);
	}

}
