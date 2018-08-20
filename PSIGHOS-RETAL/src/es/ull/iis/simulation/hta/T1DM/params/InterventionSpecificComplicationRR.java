/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * Class that associates a relative risk to an intervention. IF the patient loses the effect of the intervention, uses the
 * RR for intervention 0 (assumed to be base effect)
 * @author Iván Castilla Rodríguez
 *
 */
public class InterventionSpecificComplicationRR extends ComplicationRR {
	final private double[] rr; 
	/**
	 * 
	 */
	public InterventionSpecificComplicationRR(double[] rr) {
		this.rr = rr;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.params.ComplicationRR#getRR(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getRR(T1DMPatient pat) {
		return pat.isEffectActive() ? rr[pat.getnIntervention()] : rr[0];
	}

}
