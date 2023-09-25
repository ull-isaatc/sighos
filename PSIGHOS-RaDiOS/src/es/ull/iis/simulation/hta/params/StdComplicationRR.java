/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * The base class for relative risks. Simply returns the same relative risk specified in the constructor , or 1.0 if the effect of the 
 * intervention is lost and the applyEffect option is true.
 * @author Iván Castilla Rodríguez
 *
 */
public class StdComplicationRR implements RRCalculator {
	/** A fixed value for the relative risk */
	final private double rr; 
	
	/**
	 * Creates a simple relative risk, which always applies the same value, independently of the state of the intervention
	 * @param rr A fixed value for the relative risk 
	 */
	public StdComplicationRR(double rr) {
		this.rr = rr;
	}

	@Override
	public double getRR(Patient pat) {
		return rr;
	}

}
