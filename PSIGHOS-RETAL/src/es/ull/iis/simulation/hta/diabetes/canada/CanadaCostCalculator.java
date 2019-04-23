/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import java.util.Collection;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.outcomes.StdCostCalculator;

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
	public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
		double cost = pat.getIntervention().getAnnualCost(pat);
		final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
		// No complications
		if (state.isEmpty()) {
			cost += costNoComplication;
		}
		else {
			// Check each complication
			
			// NPH
			if (pat.hasComplication(DiabetesChronicComplications.NPH)) {
				if (state.contains(CanadaNPHSubmodel.ESRD) && costs.containsKey(CanadaNPHSubmodel.ESRD))
					cost += costs.get(CanadaNPHSubmodel.ESRD)[0];
				else if (state.contains(CanadaNPHSubmodel.NPH) && costs.containsKey(CanadaNPHSubmodel.NPH))
					cost += costs.get(CanadaNPHSubmodel.NPH)[0];
			}		
			// NEU
			if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
				if (state.contains(CanadaNEUSubmodel.LEA) && costs.containsKey(CanadaNEUSubmodel.LEA))
					cost += costs.get(CanadaNEUSubmodel.LEA)[0];
				else if (state.contains(CanadaNEUSubmodel.NEU) && costs.containsKey(CanadaNEUSubmodel.NEU))
					cost += costs.get(CanadaNEUSubmodel.NEU)[0];
			}		
			// CHD
			if (pat.hasComplication(DiabetesChronicComplications.CHD)) {
				if (state.contains(CanadaCHDSubmodel.CHD) && costs.containsKey(CanadaCHDSubmodel.CHD))
					cost += costs.get(CanadaCHDSubmodel.CHD)[0];
			}
			// RET
			if (pat.hasComplication(DiabetesChronicComplications.RET)) {
				if (state.contains(CanadaRETSubmodel.BLI) && costs.containsKey(CanadaRETSubmodel.BLI))
					cost += costs.get(CanadaRETSubmodel.BLI)[0];
				else if (state.contains(CanadaRETSubmodel.RET) && costs.containsKey(CanadaRETSubmodel.RET))
					cost += costs.get(CanadaRETSubmodel.RET)[0];
			}			
		}
		return cost;
	}
}
