/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Collection;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.MainChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SubmodelCostCalculator implements CostCalculator {
	private final ChronicComplicationSubmodel[] submodels;
	private final AcuteComplicationSubmodel[] acuteSubmodels;
	/** Cost of diabetes with no complications */ 
	protected final double costNoComplication;
	
	/**
	 * @param costNoComplication
	 * @param costHypoglycemicEvent
	 */
	public SubmodelCostCalculator(double costNoComplication, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		this.costNoComplication = costNoComplication;
		this.submodels = submodels;
		this.acuteSubmodels = acuteSubmodels;
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
			for (MainChronicComplications comp : MainChronicComplications.values()) {
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
	public double getCostForAcuteEvent(T1DMPatient pat, MainAcuteComplications comp) {
		return acuteSubmodels[comp.ordinal()].getCostOfComplication(pat);
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		return submodels[newEvent.getComplication().ordinal()].getCostOfComplication(pat, newEvent);
	}

}
