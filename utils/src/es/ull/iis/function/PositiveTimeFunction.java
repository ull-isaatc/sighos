/**
 * 
 */
package es.ull.iis.function;

/**
 * @author Iván Castilla
 *
 */
public class PositiveTimeFunction extends TimeFunction {
	private TimeFunction innerTimeFunction;

	/**
	 * 
	 * @param innerTimeFunction
	 */
	public PositiveTimeFunction(TimeFunction innerTimeFunction) {
		this.innerTimeFunction = innerTimeFunction;
	}

	/**
	 * Creates a positive time function whose parameters must be set using <code>setParameters</code>
	 */	
	public PositiveTimeFunction() {
	}

	/**
	 * Simply calls <code>getValue</code> to return a value. However, if the value is negative, returns 0.0
	 * @param params The parameters which can be used to determine the value to be returned.
	 * @return A positive value corresponding to the definition of this function.
	 */
	@Override
	public double getValue(TimeFunctionParams params) {
    	double res = innerTimeFunction.getValue(params);
    	if (res < 0.0)
    		res = 0.0;
    	return res;
	}

	@Override
	public void setParameters(Object... params) {
		if (params.length != 1) {
            throw new IllegalArgumentException("Should be one parameters for PositiveTimeFunction: " +
            params.length + " passed.");
        }
        if  (!(params[0] instanceof TimeFunction)) {    
            throw new IllegalArgumentException("Parameters must be a TimeFunction");
        }
        else {
        	setInnerTimeFunction((TimeFunction) params[0]);
        }
	}


	/**
	 * @return the innerTimeFunction
	 */
	public TimeFunction getInnerTimeFunction() {
		return innerTimeFunction;
	}

	/**
	 * @param innerTimeFunction the innerTimeFunction to set
	 */
	public void setInnerTimeFunction(TimeFunction innerTimeFunction) {
		this.innerTimeFunction = innerTimeFunction;
	}
}
