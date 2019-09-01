/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * Computes the RR according to a linear regression A * HbA1c + B
 * @author Iván Castilla Rodríguez
 *
 */
public class HbA1c1PPLinearRegressionRR implements RRCalculator {
	/** A and B coefficients for the linear regression */
	private final double[] coefficients;

	/**
	 * Creates a relative risk associated  to a 1 percentage point increment of HbA1c
	 * @param coefficients A and B coefficients for the linear regression
	 */
	public HbA1c1PPLinearRegressionRR(double [] coefficients) {
		this.coefficients = coefficients;
	}

	@Override
	public double getRR(DiabetesPatient pat) {
		return coefficients[0] * pat.getHba1c() + coefficients[1];
	}
}
