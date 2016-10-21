/**
 * 
 */
package es.ull.iis.function;

import simkit.random.RandomVariateFactory;


/**
 * Represents a polynomic function: a1·x^n-1 + a2·x^n-2 + ... + an
 * @author Roberto Muñoz
 *
 */
public class PolynomialFunction extends TimeFunction {
	/** Function coefficients */
	private TimeFunction coefficients[];
	/** Highest coefficient index */
	private int length;
	
	/**
	 * Creates a polynomial function.
	 * @param coefficients Coefficients of this function, expressed as other time functions.
	 */
	public PolynomialFunction(TimeFunction coefficients[]) {
	    this.length = coefficients.length;
	    this.coefficients = coefficients;
	}

	/**
	 * Creates a polynomial function.
	 * @param coefficients Coefficients of this function, expressed as constants.
	 */
	public PolynomialFunction(double coefficients[]) {
	    this.length = coefficients.length;
	    int i = 0;
	    this.coefficients = new TimeFunction[length];
	    for (double j : coefficients)
	    	this.coefficients[i++] = new RandomFunction(RandomVariateFactory.getInstance("ConstantVariate", j));
	    
	}

	/**
	 * Creates a polynomial function whose parameters must be set using <code>setParameters</code>
	 */	
	public PolynomialFunction() {}


	@Override
	public double getValue(TimeFunctionParams params) {
	    double value = 0;
	    for (int i = 0; i < length ; i++) {
		value += Math.pow(params.getTime(), length - i - 1) * coefficients[i].getValue(params);
	    }
	    return value;
	}

	/**
	 * Returns this function coefficients.
	 * @return the coefficients
	 */
	public TimeFunction[] getCoefficients() {
	    return coefficients;
	}

	/**
	 * Sets this function coefficients.
	 * @param coefficients the coefficients to set
	 */
	public void setCoefficients(TimeFunction[] coefficients) {
	    this.coefficients = coefficients;
	}

	@Override
	public void setParameters(Object... params) {
		this.coefficients = (TimeFunction[]) params;
	}

}
