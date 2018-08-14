/**
 * 
 */
package simkit.random;

/**
 * A class to select among different options. Returns the index of the option selected according to a set of initial frequencies.
 * Adapted from "simkit.random.DiscreteIntegerVariate" (https://github.com/kastork/simkit-mirror/blob/master/src/simkit/random/DiscreteIntegerVariate.java)
 * @author Iván Castilla Rodríguez
 *
 */
public class DiscreteSelectorVariate extends RandomVariateBase implements DiscreteRandomVariate {
	private double[] frequencies;
	private double[] cdf;
	/**
	 * 
	 */
	public DiscreteSelectorVariate() {
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	@Override
	public double generate() {
		return (double) generateInt();
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	@Override
	public Object[] getParameters() {
		return new Object[]{getFrequencies()};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 1) {
			throw new IllegalArgumentException(
					"1 Argument needed: " + params.length);
		}
		if (!(params[0] instanceof double[])) {
			throw new IllegalArgumentException(
					"Parameters muct be {double[]}: {"
							+ params[0].getClass().getName() + "}");
		}
		double[] freq = (double[]) params[0];
		this.setFrequencies(freq);
	}

	/* (non-Javadoc)
	 * @see simkit.random.DiscreteRandomVariate#generateInt()
	 */
	@Override
	public int generateInt() {
		int index;
		double uniform = this.rng.draw();
		for (index = 0; (uniform > cdf[index]) && (index < cdf.length - 1); index++) ;
		return index;
	}

    /**
     * Convert the given array of frequencies/frequencies to a cdf.
     *
     * @throws IllegalArgumentException If any of the frequencies/frequencies
     * are negative or they sum to zero.
     */
    private void toCDF() {
        this.normalize();
        cdf = new double[frequencies.length];
        cdf[0] = frequencies[0];
        for (int i = 1; i < frequencies.length; i++) {
                cdf[i] += cdf[i - 1] + frequencies[i];
        }
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
    
    public double[] getFrequencies() {
        return frequencies;
    }
    
    public void setFrequencies(double[] frequencies) {
        this.frequencies = frequencies;
        this.normalize();
        this.toCDF();
    }
}
