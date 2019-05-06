/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * Computes the RR according to the Sheffiled's method
 * 
 * They assume a probability for HbA1c level = 10% (p_10), so that p_h = p_10 X (h/10)^beta, where "h" is the new HbA1c level.
 * As a consequence, RR = p_h/p_10 = (h/10)^beta
 *   
 * @author Iván Castilla Rodríguez
 *
 */
public class SheffieldComplicationRR implements RRCalculator {
	/** The beta of the complication */
	private final double beta;

	/**
	 * Creates a relative risk computed as described in the Sheffield's T1DM model
	 * @param beta The beta of the complication
	 */
	public SheffieldComplicationRR(double beta) {
		this.beta = beta;
	}

	@Override
	public double getRR(DiabetesPatient pat) {
		return Math.pow(pat.getHba1c()/10, beta);
	}
}
