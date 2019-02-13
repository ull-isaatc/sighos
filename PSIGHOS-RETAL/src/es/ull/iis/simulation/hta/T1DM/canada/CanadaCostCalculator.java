/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.Collection;

import es.ull.iis.simulation.hta.T1DM.T1DMChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.StdCostCalculator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaCostCalculator extends StdCostCalculator {
	/**
	 * @param costNoComplication
	 * @param costHypoglycemicEvent
	 */
	public CanadaCostCalculator(double costNoComplication, double costHypoglycemicEvent) {
		super(costNoComplication, new double[] {costHypoglycemicEvent});
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
			
			// NPH
			if (pat.hasComplication(T1DMChronicComplications.NPH)) {
				if (state.contains(CanadaNPHSubmodel.ESRD) && costs.containsKey(CanadaNPHSubmodel.ESRD))
					cost += costs.get(CanadaNPHSubmodel.ESRD)[0];
				else if (state.contains(CanadaNPHSubmodel.NPH) && costs.containsKey(CanadaNPHSubmodel.NPH))
					cost += costs.get(CanadaNPHSubmodel.NPH)[0];
			}		
			// NEU
			if (pat.hasComplication(T1DMChronicComplications.NEU)) {
				if (state.contains(CanadaNEUSubmodel.LEA) && costs.containsKey(CanadaNEUSubmodel.LEA))
					cost += costs.get(CanadaNEUSubmodel.LEA)[0];
				else if (state.contains(CanadaNEUSubmodel.NEU) && costs.containsKey(CanadaNEUSubmodel.NEU))
					cost += costs.get(CanadaNEUSubmodel.NEU)[0];
			}		
			// CHD
			if (pat.hasComplication(T1DMChronicComplications.CHD)) {
				if (state.contains(CanadaCHDSubmodel.CHD) && costs.containsKey(CanadaCHDSubmodel.CHD))
					cost += costs.get(CanadaCHDSubmodel.CHD)[0];
			}
			// RET
			if (pat.hasComplication(T1DMChronicComplications.RET)) {
				if (state.contains(CanadaRETSubmodel.BLI) && costs.containsKey(CanadaRETSubmodel.BLI))
					cost += costs.get(CanadaRETSubmodel.BLI)[0];
				else if (state.contains(CanadaRETSubmodel.RET) && costs.containsKey(CanadaRETSubmodel.RET))
					cost += costs.get(CanadaRETSubmodel.RET)[0];
			}			
		}
		return cost;
	}
}
