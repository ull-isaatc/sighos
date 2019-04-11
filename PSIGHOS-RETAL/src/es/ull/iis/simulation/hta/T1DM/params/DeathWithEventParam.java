/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
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
	public Boolean getValue(T1DMPatient pat) {
		return draw(pat) < pDeath;
	}

}
