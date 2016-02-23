package es.ull.iis.simulation.variable;


/**
 * Simulation's variable which house a boolean type.
 * @author ycallero
 *
 */
public class BooleanVariable implements UserVariable {
	
	/** Boolean value */
	Boolean value;

	/**
	 * Create a new BooleanVariable.
	 * @param value Init value.
	 */
	public BooleanVariable(Boolean value) {
		this.value = value;
	}
	
	/**
	 * Create a BooleanVariable.
	 * @param value Init value.
	 */
	public BooleanVariable(double value) {
		if (value == 0.0)
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);
	}
	
	/**
	 * Obtain de current Variable's value.
	 * @return Variable's value.
	 */
	public Number getValue(Object... params) {
		if (value.booleanValue())
			return (new Integer(1));
		return (new Integer(0));
	}

	/**
	 * Compare two Variables. 
	 * @param arg0 The Variable which you want compare.
	 * @return True if both are equal.
	 */
	public boolean equals(Variable arg0) {
		return (getValue().equals(arg0.getValue()));
	}
	
	/**
	 * Convert Variable's value to a string.
	 * @return String which represents Variable's value.
	 */
	public String toString() {
		return value.toString();
	}

	/**
	 * Set a new Variable's value from an Object.
	 * @param value New value.
	 */
	public void setValue(Object value) {
		this.value = (Boolean) value;
	}
	
	/**
	 * Set a new Variable's value from an integer.
	 * @param value New value.
	 */
	public void setValue(int value) {
		if (value == 0)
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);
	}

	/**
	 * Set a new Variable's value from a boolean.
	 * @param value New value.
	 */
	public void setValue(boolean value) {
		this.value = new Boolean(value);
	}

	/**
	 * Set a new Variable's value from a char.
	 * @param value New value.
	 */
	public void setValue(char value) {
		if (value == '0')
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);			
	}

	/**
	 * Set a new Variable's value from a byte.
	 * @param value New value.
	 */
	public void setValue(byte value) {
		if (value == 0)
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);
	}

	/**
	 * Set a new Variable's value from a double.
	 * @param value New value.
	 */
	public void setValue(double value) {
		if (value == 0)
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);
	}

	/**
	 * Set a new Variable's value from a float.
	 * @param value New value.
	 */
	public void setValue(float value) {
		if (value == 0)
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);
	}

	/**
	 * Set a new Variable's value from a long.
	 * @param value New value.
	 */
	public void setValue(long value) {
		if (value == 0)
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);
	}

	/**
	 * Set a new Variable's value from a short.
	 * @param value New value.
	 */
	public void setValue(short value) {
		if (value == 0)
			this.value = new Boolean(true);
		else
			this.value = new Boolean(false);
	}
}
