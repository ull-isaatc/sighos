/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * Class that associates a relative risk to an intervention. If the patient loses the effect of the intervention, uses the
 * RR for intervention 0 (assumed to be base effect)
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class InterventionSpecificComplicationRR implements RRCalculator {
	/** The relative risk values associated to each intervention */
	final private double[] rr; 

	/**
	 * Creates a relative risk that associates a value to each intervention
	 * @param rr The relative risk values associated to each intervention (length of rr must be equal to the number of interventions)
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
