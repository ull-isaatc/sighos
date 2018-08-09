/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageParam implements Param<Double> {

	/**
	 * @param secondOrder
	 */
	public ResourceUsageParam() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double getResourceUsageCost(T1DMPatient pat, double initAge, double endAge) {
		return 0.0;
	}

	@Override
	public Double getValue(T1DMPatient pat) {
		// TODO Auto-generated method stub
		return null;
	}
}
