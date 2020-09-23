/**
 * 
 */
package simkit.random;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DirichletVariate extends RandomVariateBase {
	private double[] coefficients;
	private GammaVariate[] gammas;
	/**
	 * 
	 */
	public DirichletVariate() {
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	@Override
	public double generate() {
		return Double.NaN;
	}

	/**
	 * Generates probabilities according to the Dirichlet distribution. Note that the probabilities are not scaled unless explicitly indicated: 
	 * the {@link DiscreteIntegerVariate} can use these values to select among several discrete values 
	 * @param scale If true, scales the values generated to sum 1.0.
	 * @return
	 */
	public double[] generateValues(boolean scale) {
		double sum = 0.0;
		final double[] newCoeff = new double[coefficients.length];
		for (int i = 0; i < coefficients.length; i++) {
			newCoeff[i] = gammas[i].generate();
			sum += newCoeff[i];
		}
		if (scale) {
			for (int i = 0; i < coefficients.length; i++) {
				newCoeff[i] = newCoeff[i] / sum;
			}
		}			
		return newCoeff;
	}
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	@Override
	public Object[] getParameters() {
		return new Object[] {coefficients};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 1) {
			throw new IllegalArgumentException("Need array of coefficients (1 argument), received "
					+ params.length + " parameters");
		}
		if (!(params[0] instanceof double[])) {
			throw new IllegalArgumentException(
					"Parameter muct be double[]: "
	                + params[0].getClass().getName() + ", "
	                    + params[1].getClass().getName() + "}");
		}
		setCoefficients((double[])params[0]);
	}

	/**
	 * @return the coefficients
	 */
	public double[] getCoefficients() {
		return coefficients;
	}

	/**
	 * @param coefficients the coefficients to set
	 */
	public void setCoefficients(double[] coefficients) {
		this.coefficients = coefficients.clone();
		gammas = new GammaVariate[coefficients.length];
		for (int i = 0; i < coefficients.length; i++) {
			gammas[i] = (GammaVariate)RandomVariateFactory.getInstance("GammaVariate", 1.0, coefficients[i]);
		}
	}

}
