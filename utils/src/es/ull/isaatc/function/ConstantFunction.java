/**
 * 
 */
package es.ull.isaatc.function;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ConstantFunction extends TimeFunction {
	private double val;
	/**
	 * 
	 */
	public ConstantFunction(double val) {
		super();
		this.val = val;
	}

	public void setValue(double val) {
		this.val = val;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.function.TimeFunction#getValue(double)
	 */
	public double getValue(double ts) {
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
