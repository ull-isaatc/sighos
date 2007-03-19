/**
 * 
 */
package es.ull.isaatc.function;

/**
 * @author Iv�n Castilla Rodr�guez
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
	
}
