/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageParams extends ModelParams {

	private final static double[] COST_PER_COMPLICATION = {1112.24, 3108.86, 5180.26, 3651.31, 9305.74, 34259.48, 469.22};
	private final static double[] TRANS_COST_TO_COMPLICATION = {12082.36, 0.0, 33183.74, 0.0, 11966.18, 3250.73, 0.0};
	private final static double COST_NO_COMPLICATION = 2174.11;
	/**
	 * @param secondOrder
	 */
	public ResourceUsageParams() {
		super();
	}

	public double getResourceAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = 0.0;
		final EnumSet<Complication> state = pat.getState();
		// No complications
		if (state.isEmpty()) {
			cost = COST_NO_COMPLICATION;
		}
		else {
			for (Complication comp : state) {
				cost += COST_PER_COMPLICATION[comp.ordinal()];
			}
		}
		return cost;
	}
}
