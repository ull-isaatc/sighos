/**
 * 
 */
package es.ull.iis.function;

/**
 * A constant value wrapped by a time function.
 * @author Iván Castilla Rodríguez
 *
 */
public class ConstantFunction extends TimeFunction {
	/** The internal value */
	private double val;
	
	/**
	 * Creates a constant value which can be used as a time function 
	 */
	public ConstantFunction(double val) {
		super();
		this.val = val;
	}

	/**
	 * Sets the value
	 * @param val New value
	 */
	public void setValue(double val) {
		this.val = val;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#getValue(double)
	 */
	public double getValue(TimeFunctionParams params) {
		return val;
	}

	@Override
	public void setParameters(Object... params) {
		if (params.length < 1) 
			throw new IllegalArgumentException("Need (value), received " +
		            params.length + " parameters");
		else {
			setValue(((Number)params[0]).doubleValue());
		}
		
	}
	
}
