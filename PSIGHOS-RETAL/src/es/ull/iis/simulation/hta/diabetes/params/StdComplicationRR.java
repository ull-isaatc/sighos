/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * The base class for relative risks. Simply returns the same relative risk specified in the constructor , or 1.0 if the effect of the 
 * intervention is lost and the applyEffect option is true.
 * @author Iván Castilla Rodríguez
 *
 */
public class StdComplicationRR implements RRCalculator {
	/** A fixed value for the relative risk */
	final private double rr; 
	/** If true, checks if the effect of the intervention is active before returning the RR */
	final private boolean applyEffect;
	
	/**
	 * Creates a simple relative risk, which always applies the same value, independently of the state of the intervention
	 * @param rr A fixed value for the relative risk 
	 */
	public StdComplicationRR(double rr) {
		this(rr, false);
	}

	/**
	 * Creates a simple relative risk, which always applies the same value
	 * @param rr A fixed value for the relative risk 
	 * @param applyEffect If true, checks if the effect of the intervention is active before returning the RR
	 */
	public StdComplicationRR(double rr, boolean applyEffect) {
		this.rr = rr;
		this.applyEffect = applyEffect;
	}

	@Override
	public double getRR(DiabetesPatient pat) {
		if (applyEffect)
			return pat.isEffectActive() ? rr : 1.0;
		else
			return rr;
	}

}
