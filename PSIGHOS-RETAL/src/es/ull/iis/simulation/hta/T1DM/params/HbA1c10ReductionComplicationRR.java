/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * Computes the RR according to the DCCT, 1996 paper. There, they associated a risk reduction to a 10% HbA1c reduction.
 * They also consider a log-log linear relationship among these two parameters
 * @author Iván Castilla Rodríguez
 *
 */
public class HbA1c10ReductionComplicationRR implements RRCalculator {
	/** A constant for the log of the 10% HbA1c reduction */ 
	private static double LN09 = Math.log(0.9);

	/** The risk reduction of the complication */
	private final double rr10;
	/** The log of the reference HbA1c from which the risk reduction is applied */
	private final double lnReferenceHbA1c;

	/**
	 * Creates a relative risk that associates a risk reduction to a 10% HbA1c reduction
	 * @param rr10 Risk reduction associated to a 10% HbA1c reduction
	 * @param referenceHbA1c Reference HbA1c the reduction was estimated from
	 */
	public HbA1c10ReductionComplicationRR(double rr10, double referenceHbA1c) {
		this.rr10 = rr10;
		this.lnReferenceHbA1c = Math.log(referenceHbA1c);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.params.ComplicationRR#getRR(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getRR(T1DMPatient pat) {		
		// First compute the slope of the linear relationship
		final double beta = Math.log(-rr10+1)/LN09;
		return Math.exp(beta * (Math.log(pat.getHba1c())-lnReferenceHbA1c));
	}

}
