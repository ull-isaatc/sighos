/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import simkit.random.RandomNumber;

/**
 * @author Iván Castilla
 *
 */
public class DeathWithEventParam extends MultipleEventParam<Boolean> {
	/** Probability of dying when an acute event appears */
	private final double pDeath;

	public DeathWithEventParam(RandomNumber rng, int nPatients, double pDeath) {
		super(rng, nPatients);
		this.pDeath = pDeath;		
	}

	@Override
	public Boolean getValue(DiabetesPatient pat) {
		return draw(pat) < pDeath;
	}

}
