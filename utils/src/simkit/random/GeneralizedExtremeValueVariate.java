/**
 * 
 */
package simkit.random;

/**
 * @author Iván
 *
 */
public class GeneralizedExtremeValueVariate extends RandomVariateBase {
	/**
	* The scale factor.
	*/
	private double sigma;
	
	/**
	 * The location parameter.
	 */
	private double mu;
	
	/**
	 * The shape parameter.
	 */
	private double k;
	
	public GeneralizedExtremeValueVariate() {
		
	}
	
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	public double generate() {
		double r = rng.draw();
		return sigma / k * Math.expm1(-k * Math.log(-Math.log(r))) + mu;
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	public Object[] getParameters() {
		return new Object[] {k, sigma, mu};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	public void setParameters(Object... params) {
        if (params.length != 3) {
            throw new IllegalArgumentException("GeneralizedExtremeValueVariate requires three parameters " +
                params.length + " given");
        }
        if (params[0] instanceof Number && params[1] instanceof Number && params[2] instanceof Number) {
        	setK(((Number)params[0]).doubleValue());
            setSigma(((Number) params[1]).doubleValue());
            setMu(((Number) params[2]).doubleValue());
        }
        else {
            throw new IllegalArgumentException(
                "Need three Number objects; (" + params[0].getClass().getName() +
                ", " + params[1].getClass().getName() + ", " + params[2].getClass().getName() + ") given");
        }

	}

	/**
	 * @return the sigma
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * @param sigma the sigma to set
	 */
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	/**
	 * @return the mu
	 */
	public double getMu() {
		return mu;
	}

	/**
	 * @param mu the mu to set
	 */
	public void setMu(double mu) {
		this.mu = mu;
	}

	/**
	 * @return the k
	 */
	public double getK() {
		return k;
	}

	/**
	 * @param k the k to set
	 */
	public void setK(double k) {
		this.k = k;
	}

}
