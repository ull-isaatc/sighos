/**
 * 
 */
package simkit.random;

/**
 * A random generator that generates always the same value. Basically, it does the same as {@link ConstantVariate} but 
 * only generates integer values to be compatible with Discrete random variates.
 * @author Iván Castilla
 *
 */
public class DiscreteConstantVariate extends RandomVariateBase implements DiscreteRandomVariate {
	/** The constant value to always be generated. **/
	private int value;
	
	/**
	 * Creates a new DiscreteConstantVariate with a value of 0. 
	 */
	public DiscreteConstantVariate() {
		this.setParameters(0);
	}

	@Override
	public double generate() {
		return value;
	}

	@Override
	public void setParameters(Object... params) {
        if (params.length != 1)
            throw new IllegalArgumentException("Need parameters length 1: " + params.length);
        if (params[0] instanceof Number)
            setValue(((Number)params[0]).intValue());
        else 
            throw new IllegalArgumentException("Must be Number: " + params[0]);

	}

	@Override
	public Object[] getParameters() {
		return new Object[] { value };
	}

	/**
	 * Returns the constant value.
	 * @return the constant value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the constant value to be generated.
	 * @param value The value to be generated
	 **/
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int generateInt() {
		return value;
	}

}
