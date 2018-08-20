/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * The base class for relative risks. Simply returns the same relative risk specified in the constructor , or 1.0 if the effect of the 
 * intervention is lost.
 * @author Iván Castilla Rodríguez
 *
 */
public class StdComplicationRR extends ComplicationRR {
	final private double rr; 
	/**
	 * 
	 */
	public StdComplicationRR(double rr) {
		this.rr = rr;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.params.ComplicationRR#getRR(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getRR(T1DMPatient pat) {
		return pat.isEffectActive() ? rr : 1.0;
	}

}
