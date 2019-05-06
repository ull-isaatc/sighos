/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import simkit.random.RandomNumber;

/**
 * A parameter to define an event that may involve the death of the patient
 * @author Iván Castilla
 *
 */
public class DeathWithEventParam extends MultipleEventParam<Boolean> {
	/** Probability of dying when an acute event appears */
	private final double pDeath;

	/**
	 * Creates a parameter that defines an event that may involve the death of the patient
	 * @param rng Random number generator
	 * @param nPatients Number of patients
	 * @param pDeath Probablity of dying every time the event occurs
	 */
	public DeathWithEventParam(RandomNumber rng, int nPatients, double pDeath) {
		super(rng, nPatients);
		this.pDeath = pDeath;		
	}

	@Override
	public Boolean getValue(DiabetesPatient pat) {
		return draw(pat) < pDeath;
	}

}
