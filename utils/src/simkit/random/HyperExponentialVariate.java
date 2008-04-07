/**
 * 
 */
package simkit.random;

import simkit.random.ExponentialVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateBase;

/**
 * Generates Hyper-exponential distribution using composition of exponentials.
 * By now, it can be only composition of two exponentials.
 * @author Iván Castilla Rodríguez
 */
public class HyperExponentialVariate extends RandomVariateBase implements RandomVariate {
	/** Lambda parameter */
	private double lambda;
	/** First exponential mean */
	private double mean1;
	/** First exponential */
	private ExponentialVariate exp1;
	/** Second exponential mean */
	private double mean2;
	/** Second exponential */
	private ExponentialVariate exp2;
	
	/** 
	 * Creates a new hyperExponential variate with a zero lambda and means.
	 */
	public HyperExponentialVariate() {
	}
	
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	public double generate() {
		return (rng.draw() < lambda) ? exp1.generate() : exp2.generate();
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	public Object[] getParameters() {
		return new Object[] { lambda, mean1, mean2};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	public void setParameters(Object... params) {
        if (params.length != 3) {
            throw new IllegalArgumentException("Should be three parameters for HyperExponential: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof Number) || !(params[1] instanceof Number) || !(params[2] instanceof Number)) {
            throw new IllegalArgumentException("Parameters must be a Number");
        }
        else {
            setMean1(((Number) params[0]).doubleValue());
            setMean2(((Number) params[1]).doubleValue());
            setLambda(((Number) params[2]).doubleValue());
        }
	}

	/**
	 * @return the lambda
	 */
	public double getLambda() {
		return lambda;
	}

	/**
	 * @param lambda the lambda to set
	 */
	public void setLambda(double lambda) {
        if (lambda > 0.0 && lambda < 1.0) {
    		this.lambda = lambda;
    	}
        else {
            throw new IllegalArgumentException("Lambda must be in (0, 1): " + lambda);
        }
	}

	/**
	 * @return the mean1
	 */
	public double getMean1() {
		return mean1;
	}

	/**
	 * @param mean1 the mean1 to set
	 */
	public void setMean1(double mean1) {
        if (mean1 > 0.0) {
            this.mean1 = mean1;
            exp1 = (ExponentialVariate)RandomVariateFactory.getInstance("ExponentialVariate", new Object[] {mean1});
        }
        else {
            throw new IllegalArgumentException("Exponential mean must be positive: " + mean1);
        }
    }

	/**
	 * @return the mean2
	 */
	public double getMean2() {
		return mean2;
	}

	/**
	 * @param mean2 the mean2 to set
	 */
	public void setMean2(double mean2) {
        if (mean2 > 0.0) {
            this.mean2 = mean2;
            exp2 = (ExponentialVariate)RandomVariateFactory.getInstance("ExponentialVariate", new Object[] {mean2});
        }
        else {
            throw new IllegalArgumentException("Exponential mean must be positive: " + mean2);
        }
	}

	public String toString() {
		return "HyperExponential (" + mean1 + ", " + mean2 + ", " + lambda + ")";
	}
}
