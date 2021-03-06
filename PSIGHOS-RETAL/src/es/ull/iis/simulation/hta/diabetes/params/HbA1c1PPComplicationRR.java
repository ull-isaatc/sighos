/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * Computes the RR according to Selvin et al 2004. There, they associated a relative risk to a 1 percentage point increment of HbA1c.
 * Lets HbA1c_0 be the reference HbA1c level.
 * Lets p_0 be the probability of the complication for HbA1c_0.
 * Lets consider a new level of HbA1c, HbA1c_k = HbA1c_0 + k. p_k would be the probability of complication for that level.
 * Lets RR_0 be the relative risk associated to a 1 PP increment of HbA1c, i.e., p_1 = p_0 X RR_0
 * 
 * Then
 * RR_k = p_k / p_0 = RR_0^k
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class HbA1c1PPComplicationRR implements RRCalculator {
	/** The relative risk of the complication, associated to a 1 PP increment of HbA1c*/
	private final double referenceRR;
	/** The reference HbA1c from which the relative risk is applied */
	private final double referenceHbA1c;

	/**
	 * Creates a relative risk associated  to a 1 percentage point increment of HbA1c
	 * @param referenceRR The relative risk of the complication, associated to a 1 PP increment of HbA1c
	 * @param referenceHbA1c The reference HbA1c from which the relative risk is applied
	 */
	public HbA1c1PPComplicationRR(double referenceRR, double referenceHbA1c) {
		this.referenceRR = referenceRR;
		this.referenceHbA1c = referenceHbA1c;
	}

	@Override
	public double getRR(DiabetesPatient pat) {
		final double diff = pat.getHba1c() - referenceHbA1c;
		return Math.pow(referenceRR, diff);
	}
}
