/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageParams extends ModelParams {

	private final static double[] COST_PER_COMPLICATION = {1112.24, 3108.86, 5180.26, 3651.31, 9305.74, 34259.48, 469.22};
	/**
	 * @param secondOrder
	 */
	public ResourceUsageParams() {
		super();
	}

	public double getResourceAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = 0.0;
		return 0.0;
	}
}
