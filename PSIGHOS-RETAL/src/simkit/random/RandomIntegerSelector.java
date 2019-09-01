package simkit.random;

/**
 * A class to select among different options. Returns the index of the option selected according to a set of initial frequencies.
 * Adapted from "simkit.random.DiscreteIntegerVariate" (https://github.com/kastork/simkit-mirror/blob/master/src/simkit/random/DiscreteIntegerVariate.java)
 * @author Iván Castilla Rodríguez
 *
 */
final public class RandomIntegerSelector {
	private final double[] frequencies;
	private final double[] cdf;
	/**
	 * 
	 */
	public RandomIntegerSelector(double[] frequencies) {
        this.frequencies = frequencies;
        this.normalize();
        cdf = new double[frequencies.length];
        cdf[0] = frequencies[0];
        for (int i = 1; i < frequencies.length; i++) {
                cdf[i] += cdf[i - 1] + frequencies[i];
        }
	}

	public int generate(double uniform) {
		int index;
		for (index = 0; (uniform > cdf[index]) && (index < cdf.length - 1); index++) ;
		return index;
	}

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