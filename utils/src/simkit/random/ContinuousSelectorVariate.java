/**
 * 
 */
package simkit.random;

/**
 * A class to generate values from different ranges according to a set of predefined frequencies. Within each range, values are distributed
 * uniformly. Works as the CONT function from Rockwell Arena.
 * Adapted from "simkit.random.DiscreteIntegerVariate" (https://github.com/kastork/simkit-mirror/blob/master/src/simkit/random/DiscreteIntegerVariate.java)
 * @author Iván Castilla Rodríguez
 *
 */
public class ContinuousSelectorVariate extends RandomVariateBase {
	private double[] frequencies;
	private double[] values;
	private double[] cdf;
	/**
	 * 
	 */
	public ContinuousSelectorVariate() {
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	@Override
	public double generate() {
		int index;
		final double uniform = this.rng.draw();
		for (index = 0; (uniform > cdf[index]) && (index < cdf.length - 1); index++) ;
		return (values[index+1] - values[index]) * uniform + values[index];
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
		if (params.length != 2) {
			throw new IllegalArgumentException(
					"2 Argument needed: " + params.length);
		}
		if (!(params[0] instanceof double[] && params[1] instanceof double[])) {
			throw new IllegalArgumentException(
					"Parameters must be {double[], double[]}: {"
							+ params[0].getClass().getName() + ", " + params[0].getClass().getName() + "}");
		}
		final double[] freq = (double[]) params[0];
		final double[] val = (double[]) params[1];
		if (freq.length != val.length - 1) {
            throw new IllegalArgumentException("Values must have one element more than frequencies: " +
                    val.length + "!= (" + freq.length + "+1)");
		}
		this.setFrequencies(freq);
		this.setValues(val);
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

	/**
	 * @return the values
	 */
	public double[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(double[] values) {
		this.values = values;
	}
}
