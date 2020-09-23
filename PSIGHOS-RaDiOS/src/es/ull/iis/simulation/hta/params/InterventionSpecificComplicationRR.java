/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * Class that associates a relative risk to an intervention. If the patient loses the effect of the intervention, uses the
 * RR for intervention 0 (assumed to be base effect)
 * @author Iván Castilla Rodríguez
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

	@Override
	public double getRR(Patient pat) {
		return pat.isEffectActive() ? rr[pat.getnIntervention()] : rr[0];
	}

}
