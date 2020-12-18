/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomNumber;

/**
 * A parameter that uses a probability to predict whether or not something will happen to a patient, i.e. a Bernoulli distribution. The same patient may use this parameter
 * multiple times during his/her lifetime
 * @author Iván Castilla
 *
 */
public class MultipleBernoulliParam extends MultipleEventParam<Boolean> {
	/** Probability of the event to happen */
	private final double probability;

	/**
	 * Creates a parameter that defines whether or not something will happen to a patient
	 * @param rng Random number generator
	 * @param nPatients Number of patients
	 * @param probability Probability of the the event occurs
	 */
	public MultipleBernoulliParam(RandomNumber rng, int nPatients, double probability) {
		super(rng, nPatients);
		this.probability = probability;		
	}

	@Override
	public Boolean getValue(Patient pat) {
		return draw(pat) < probability;
	}

}
