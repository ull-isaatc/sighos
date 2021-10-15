/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomNumber;

/**
 * A parameter to select between N different options (labeled 0, 1, ..., N - 1). In a single simulation replication, the selection will be always the same for each patient
 * Adapted from "simkit.random.DiscreteIntegerVariate" (https://github.com/kastork/simkit-mirror/blob/master/src/simkit/random/DiscreteIntegerVariate.java)
 * @author Iván Castilla
 *
 */
public class SingleSelectorParam extends UniqueEventParam<Integer> {
	private final double[] frequencies;
	private final double[] cdf;

	/**
	 * @param rng Random number generator
	 * @param nPatients Number of patients simulated
	 */
	public SingleSelectorParam(RandomNumber rng, int nPatients, double[] frequencies) {
		super(rng, nPatients);
        this.frequencies = frequencies;
        this.normalize();
        cdf = new double[frequencies.length];
        cdf[0] = frequencies[0];
        for (int i = 1; i < frequencies.length; i++) {
                cdf[i] += cdf[i - 1] + frequencies[i];
        }
	}

	@Override
	public Integer getValue(Patient pat) {
		int index;
		final double rnd = draw(pat);
		for (index = 0; (rnd > cdf[index]) && (index < cdf.length - 1); index++) ;
		return index;
	}

	/**
	 * Rescales the frequencies so that they sum up 1.
	 */
    private void normalize() {
        double sum = 0.0;
        for (int i = 0; i < frequencies.length; ++i) {
            if (frequencies[i] < 0.0) {
                throw new IllegalArgumentException(
                        String.format("Bad frequency value at index %d (value = %.3f)", i, frequencies[i]));
            }
            sum += frequencies[i];
        }
        if (sum > 0.0) {
            for (int i = 0; i < frequencies.length; ++i) {
                frequencies[i] /= sum;
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Frequency sum not positive: %.3f", sum));
        }
    }
}
