/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Collection;

import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class AdjustedCostCalculator extends StdCostCalculator {
	private final boolean useSimpleModels;
	/**
	 * @param costNoComplication
	 * @param costHypoglycemicEvent
	 */
	public AdjustedCostCalculator(double costNoComplication, double costHypoglycemicEvent, boolean useSimpleModels) {
		super(costNoComplication, costHypoglycemicEvent);
		this.useSimpleModels = useSimpleModels;
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
			
			// NPH
			if (pat.hasComplication(MainComplications.NPH)) {
				if (state.contains(SimpleNPHSubmodel.ESRD) && costs.containsKey(SimpleNPHSubmodel.ESRD))
					cost += costs.get(SimpleNPHSubmodel.ESRD)[0];
				else if (state.contains(SimpleNPHSubmodel.NPH) && costs.containsKey(SimpleNPHSubmodel.NPH))
					cost += costs.get(SimpleNPHSubmodel.NPH)[0];
			}		
			// NEU
			if (pat.hasComplication(MainComplications.NEU)) {
				if (state.contains(SimpleNEUSubmodel.LEA) && costs.containsKey(SimpleNEUSubmodel.LEA))
					cost += costs.get(SimpleNEUSubmodel.LEA)[0];
				else if (state.contains(SimpleNEUSubmodel.NEU) && costs.containsKey(SimpleNEUSubmodel.NEU))
					cost += costs.get(SimpleNEUSubmodel.NEU)[0];
			}		
			// CHD
			if (pat.hasComplication(MainComplications.CHD)) {
				if (state.contains(SimpleCHDSubmodel.ANGINA) && costs.containsKey(SimpleCHDSubmodel.ANGINA))
					cost += costs.get(SimpleCHDSubmodel.ANGINA)[0];
				else if (state.contains(SimpleCHDSubmodel.STROKE) && costs.containsKey(SimpleCHDSubmodel.STROKE))
					cost += costs.get(SimpleCHDSubmodel.STROKE)[0];
				if (state.contains(SimpleCHDSubmodel.HF) && costs.containsKey(SimpleCHDSubmodel.HF))
					cost += costs.get(SimpleCHDSubmodel.HF)[0];
				else if (state.contains(SimpleCHDSubmodel.MI) && costs.containsKey(SimpleCHDSubmodel.MI))
					cost += costs.get(SimpleCHDSubmodel.MI)[0];				
			}
			// RET
			if (pat.hasComplication(MainComplications.RET)) {
				if (useSimpleModels) {
					if (state.contains(SimpleRETSubmodel.BLI) && costs.containsKey(SimpleRETSubmodel.BLI))
						cost += costs.get(SimpleRETSubmodel.BLI)[0];
					else if (state.contains(SimpleRETSubmodel.RET) && costs.containsKey(SimpleRETSubmodel.RET))
						cost += costs.get(SimpleRETSubmodel.RET)[0];
				}
				else {
					if (state.contains(SheffieldRETSubmodel.BLI) && costs.containsKey(SheffieldRETSubmodel.BLI))
						cost += costs.get(SheffieldRETSubmodel.BLI)[0];
					else {
						if (state.contains(SheffieldRETSubmodel.PRET) && costs.containsKey(SheffieldRETSubmodel.PRET))
							cost += costs.get(SheffieldRETSubmodel.PRET)[0];
						if (state.contains(SheffieldRETSubmodel.ME) && costs.containsKey(SheffieldRETSubmodel.ME))
							cost += costs.get(SheffieldRETSubmodel.ME)[0];
						if (!state.contains(SheffieldRETSubmodel.PRET) && !state.contains(SheffieldRETSubmodel.ME) 
								&& state.contains(SheffieldRETSubmodel.BGRET) && costs.containsKey(SheffieldRETSubmodel.BGRET))
							cost += costs.get(SheffieldRETSubmodel.BGRET)[0];				
					}
				}
			}			
		}
		return cost;
	}
}
