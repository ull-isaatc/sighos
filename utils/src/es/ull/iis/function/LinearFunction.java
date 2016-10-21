/**
 * 
 */
package es.ull.iis.function;


/**
 * Represents the linear function: A·x + B. Thus, two parameters are required: A (scale) and B (shift).
 * @author Iván Castilla Rodríguez
 */
public class LinearFunction extends TimeFunction {
	/** Scale */
	private TimeFunction scale;
	/** Shift */
	private TimeFunction shift;
	
	/**
	 * Creates a new linear function with two parameters A (scale) and B (factor), which can be also
	 * defined as other time functions.
	 * @param a Scale
	 * @param b Factor
	 */
	public LinearFunction(TimeFunction a, TimeFunction b) {
		this.scale = a;
		this.shift = b;
	}

	/**
	 * Creates a new linear function with two parameters A (scale) and B (factor), which are defined
	 * as constants.
	 * @param a Scale
	 * @param b Factor
	 */
	public LinearFunction(double a, double b) {
		this.scale = new ConstantFunction(a);
		this.shift = new ConstantFunction(b);
	}

	/**
	 * Creates a non-set linear function. This constructor must be used together with the 
	 * <code>setParameters</code> method.
	 */
	public LinearFunction() {	
	}
	
	/**
	 * Returns the scale.
	 * @return Returns the scale.
	 */
	public TimeFunction getScale() {
		return scale;
	}

	/**
	 * Returns the shift
	 * @return Returns the shift.
	 */
	public TimeFunction getShift() {
		return shift;
	}

	/**
	 * Sets a new value for the scale.
	 * @param a The scale to set.
	 */
	public void setScale(TimeFunction a) {
		this.scale = a;
	}

	/**
	 * Sets a new value for the shift
	 * @param b The shift to set.
	 */
	public void setShift(TimeFunction b) {
		this.shift = b;
	}

	public double getValue(TimeFunctionParams params) {
		return scale.getValue(params) * params.getTime() + shift.getValue(params);
	}

	/**
	 * Requires two parameters: scale and shift.
	 * @param params Parameters required by this method.
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length < 2) 
			throw new IllegalArgumentException("Need (Scale, Shift), received " +
		            params.length + " parameters");
		else {
			setScale((TimeFunction)params[0]);
			setShift((TimeFunction)params[1]);
		}
		
	}
}
