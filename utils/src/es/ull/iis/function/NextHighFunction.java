/**
 * 
 */
package es.ull.iis.function;


/**
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class NextHighFunction extends TimeFunction {
	private TimeFunction func;
	private double scale = 1.0;
	private double shift = 0.0;	

	/**
	 * 
	 */
	public NextHighFunction() {
	}

	/**
	 * @param func
	 * @param scale
	 * @param shift
	 */
	public NextHighFunction(TimeFunction func, double scale, double shift) {
		this.func = func;
		this.scale = scale;
		this.shift = shift;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#getValue(double)
	 */
	@Override
	public double getValue(TimeFunctionParams params) {
		final double ts = params.getTime();
		final double auxVal = Math.ceil((ts + func.getValue(params) - shift) / scale) * scale + shift;
		return auxVal - ts;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 3) {
            throw new IllegalArgumentException("Should be four parameters for Round: " +
            params.length + " passed.");
        }
        if  (!(params[0] instanceof TimeFunction))    
            throw new IllegalArgumentException("Parameters must be a TimeFunction");
        else if  (!(params[1] instanceof Number))
            throw new IllegalArgumentException("Parameters must be a Number");
        else if  (!(params[2] instanceof Number))
            throw new IllegalArgumentException("Parameters must be a Number");
        else {
            setFunc((TimeFunction) params[0]);
            setScale(((Number) params[1]).doubleValue());
            setShift(((Number) params[2]).doubleValue());
        }
	}

	/**
	 * @return the func
	 */
	public TimeFunction getFunc() {
		return func;
	}

	/**
	 * @param func the func to set
	 */
	public void setFunc(TimeFunction func) {
		this.func = func;
	}

	/**
	 * @return the factor
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * @param scale the factor to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * @return the shift
	 */
	public double getShift() {
		return shift;
	}

	/**
	 * @param shift the shift to set
	 */
	public void setShift(double shift) {
		this.shift = shift;
	}
	
}
