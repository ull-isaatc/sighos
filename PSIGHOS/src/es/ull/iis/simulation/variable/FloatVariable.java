package es.ull.iis.simulation.variable;


/**
 * Simulation's variable which house a float type.
 * @author ycallero
 *
 */
public class FloatVariable extends NumberVariable {
	
	/**
	 * Create a new FloatVariable.
	 * @param value Init value.
	 */
	public FloatVariable(float value) {
		this.value = new Float(value);
	}

	/**
	 * Create a new FloatVariable.
	 * @param value Init value.
	 */
	public FloatVariable(double value) {
		this.value = new Float(value);
	}
	
	/**
	 * Compare two Variables. 
	 * @param obj The Variable which you want compare.
	 * @return True if both are equal.
	 */	
	public boolean equals(Variable obj) {
		return value.equals(obj.getValue());
	}

	/**
	 * Set a new Variable's value from an integer.
	 * @param value New value.
	 */
	public void setValue(int value) {
		this.value = new Float((float) value);
	}

	/**
	 * Set a new Variable's value from a boolean.
	 * @param value New value.
	 */
	public void setValue(boolean value) {
		if (value)
			this.value = new Float(0);
		else
			this.value = new Float(1);
	}

	/**
	 * Set a new Variable's value from a char.
	 * @param value New value.
	 */
	public void setValue(char value) {
		this.value = new Float((float) value);
	}

	/**
	 * Set a new Variable's value from a byte.
	 * @param value New value.
	 */
	public void setValue(byte value) {
		this.value = new Float((float) value);
	}

	/**
	 * Set a new Variable's value from a double.
	 * @param value New value.
	 */
	public void setValue(double value) {
		this.value = new Float((float) value);	
	}

	/**
	 * Set a new Variable's value from a float.
	 * @param value New value.
	 */
	public void setValue(float value) {
		this.value = new Float((float) value);
	}

	/**
	 * Set a new Variable's value from a long.
	 * @param value New value.
	 */
	public void setValue(long value) {
		this.value = new Float((float) value);
	}

	/**
	 * Set a new Variable's value from a short.
	 * @param value New value.
	 */
	public void setValue(short value) {
		this.value = new Float((float) value);
	}

}
