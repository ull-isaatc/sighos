/**
 * 
 */
package simkit.random;

/**
 * Implements a bivariate normal random number generator, as in {@link https://wernerantweiler.ca/blog.php?item=2019-03-03}.
 * @author Iván Castilla
 *
 */
public class BivariateNormalVariate extends RandomVariateBase {
	private final double[] mu = {0, 0};
	private final double[] sigma = {1, 1};
	private double rho = 0.0;
	private double lambda;
	private double nu;
	private final NormalVariate stdNormal;
	
	/**
	 * 
	 */
	public BivariateNormalVariate() {
		stdNormal = (NormalVariate)RandomVariateFactory.getInstance("NormalVariate", 0.0, 1.0);
	}

	@Override
	public double generate() {
		return Double.NaN;
	}

	public double[]generateValues() {
		final double[] result = new double[2];
		result[0] = mu[0] + sigma[0] * stdNormal.generate();
		result[1] = mu[1] + lambda * (result[0] - mu[0]) + nu * stdNormal.generate();
		return result;
		
	}
	
	@Override
	public Object[] getParameters() {
		return new Object[] {mu[0], sigma[0], mu[1], sigma[1], rho};
	}

	@Override
	public void setParameters(Object... params) {
		if (params.length != 5) {
            throw new IllegalArgumentException("Should be five parameters for Bivariate normal: " +
            params.length + " passed.");
        }
		for (int i = 0; i < 5; i++) {
			if (!(params[i] instanceof Number))
	            throw new IllegalArgumentException("Parameters must be a Number");
		}
        if ((((Number)params[1]).doubleValue() <= 0.0) || (((Number)params[3]).doubleValue() <= 0.0)) {
            throw new IllegalArgumentException("Parameters sigma must be higher than 0");
        }
        if (((Number)params[4]).doubleValue() < -1.0 || ((Number)params[4]).doubleValue() > 1.0) {
            throw new IllegalArgumentException("Parameter rho must be in [-1, +1]");
        }
        else {
            setMu(new double[] {((Number) params[0]).doubleValue(), ((Number) params[2]).doubleValue()});
            setSigma(new double[] {((Number) params[1]).doubleValue(), ((Number) params[3]).doubleValue()});
            setRho(((Number) params[4]).doubleValue());
        }
		
	}

	/**
	 * @return the mu values
	 */
	public double[] getMu() {
		return mu;
	}

	/**
	 * @param mu the mu to set
	 */
	public void setMu(double[] mu) {
		this.mu[0] = mu[0];
		this.mu[1] = mu[1];
	}

	/**
	 * @return the sigma values
	 */
	public double[] getSigma() {
		return sigma;
	}

	/**
	 * @param sigma the sigma to set
	 */
	public void setSigma(double[] sigma) {
		this.sigma[0] = sigma[0];
		this.sigma[1] = sigma[1];
		// Update lambda and nu
		lambda = (sigma[1] / sigma[0]) * rho;
		nu = Math.sqrt((1 - rho * rho) * sigma[1] * sigma[0]);
	}

	/**
	 * @return the rho
	 */
	public double getRho() {
		return rho;
	}

	/**
	 * @param rho the rho to set
	 */
	public void setRho(double rho) {
		this.rho = rho;
		// Update lambda and nu
		lambda = (sigma[1] / sigma[0]) * rho;
		nu = Math.sqrt((1 - rho * rho) * sigma[1] * sigma[0]);
	}
}
