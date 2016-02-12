package es.ull.iis.simulation.variable;


/**
 * Simulation's variable which house a byte type.
 * @author ycallero
 *
 */

public class ByteVariable extends NumberVariable {

	/**
	 * Create a new ByteVariable.
	 * @param value Init value.
	 */
	public ByteVariable(byte value) {
		super();
		this.value = new Byte(value);
	}
	
	/**
	 * Create a new ByteVariable.
	 * @param value Init value.
	 */
	public ByteVariable(double value) {
		super();
		this.value = new Byte((byte)value);
	}
	
	/**
	 * Compare two Variables.
	 * @param obj Variable whitch you want compare.
	 * @return True if both are equals.
	 */
	public boolean equals(Variable obj) {
		return (value.equals(obj.getValue()));
	}

	/**
	 * Set a new Variable's value from an integer.
	 * @param value New value.
	 */
	public void setValue(int value) {
		this.value = new Byte((byte)value);
	}

	/**
	 * Set a new Variable's value from a boolean.
	 * @param value New value.
	 */
	public void setValue(boolean value) {
		if (value)
			this.value = new Byte((byte) 0);
		else
			this.value = new Byte((byte) 1);
	}

	/**
	 * Set a new Variable's value from a char.
	 * @param value New value.
	 */
	public void setValue(char value) {
		this.value = new Byte((byte)value);
	}

	/**
	 * Set a new Variable's value from a byte.
	 * @param value New value.
	 */
	public void setValue(byte value) {
		this.value = new Byte(value);
	}

	/**
	 * Set a new Variable's value from a double.
	 * @param value New value.
	 */
	public void setValue(double value) {
		this.value = new Byte((byte)value);
	}

	/**
	 * Set a new Variable's value from a float.
	 * @param value New value.
	 */
	public void setValue(float value) {
		this.value = new Byte((byte)value);
	}

	/**
	 * Set a new Variable's value from a long.
	 * @param value New value.
	 */	
	public void setValue(long value) {
		this.value = new Byte((byte)value);
	}

	/**
	 * Set a new Variable's value from a short.
	 * @param value New value.
	 */
	public void setValue(short value) {
		this.value = new Byte((byte)value);
	}

}
