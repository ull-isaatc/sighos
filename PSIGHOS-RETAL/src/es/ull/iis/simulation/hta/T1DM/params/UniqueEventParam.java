/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * A class to generate unique events for patients, i.e., an event that happens only once during the lifetime of the patient.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class UniqueEventParam<T> implements Param<T> {
	/** Random number generator */
	private static final RandomNumber rng = RandomNumberFactory.getInstance();
	/** The value generated for this parameter for each patient */
	private final double[] generated;

	/**
	 * Creates a parameter that represents a one-time event for a patient
	 * @param nPatients Number of patients simulated
	 */
	public UniqueEventParam(int nPatients) {
		this.generated = new double[nPatients];
		Arrays.fill(generated, Double.NaN);
	}

	/**
	 * Returns a value in [0, 1] for the patient. Always returns the same value for the same patient.
	 * @param pat A patient
	 * @return a value in [0, 1] for the patient
	 */
	protected double draw(T1DMPatient pat) {
		if (Double.isNaN(generated[pat.getIdentifier()])) {
			generated[pat.getIdentifier()] = rng.draw();
		}
		return generated[pat.getIdentifier()];
	}
}
