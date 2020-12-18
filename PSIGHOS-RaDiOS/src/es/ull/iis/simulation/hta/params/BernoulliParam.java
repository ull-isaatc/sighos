/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomNumber;

/**
 * A parameter that uses a probability to predict whether or not something will happen to a patient, i.e. a Bernoulli distribution
 * @author Iván Castilla Rodríguez
 *
 */
public class BernoulliParam extends UniqueEventParam<Boolean> {
	private final double probability;
	
	public BernoulliParam(RandomNumber rng, int nPatients, double probability) {
		super(rng, nPatients);
		this.probability = probability;
	}

	@Override
	public Boolean getValue(Patient pat) {
		return draw(pat) < probability;
	}

}
