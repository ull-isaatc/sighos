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
	private static final RandomNumber rng = RandomNumberFactory.getInstance();
	private final double[] generated;

	/**
	 * 
	 */
	public UniqueEventParam(int nPatients) {
		this.generated = new double[nPatients];
		Arrays.fill(generated, Double.NaN);
	}

	protected double draw(T1DMPatient pat) {
		if (Double.isNaN(generated[pat.getIdentifier()])) {
			generated[pat.getIdentifier()] = rng.draw();
		}
		return generated[pat.getIdentifier()];
	}
}
