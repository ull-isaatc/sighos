package es.ull.isaatc.simulation.variable;


/**
 * Simulation's variable which house a double type.
 * @author ycallero
 *
 */
public class DoubleVariable extends NumberVariable{

	/**
	 * Create a new DoubleVariable.
	 * @param value Init value.
	 */
	public DoubleVariable(double value) {
		this.value = new Double(value);
	}
	
	/**
	 * Compare two Variables. 
	 * @param arg0 The Variable which you want compare.
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
		this.value = new Double((double) value);
	}

	/**
	 * Set a new Variable's value from a boolean.
	 * @param value New value.
	 */
	public void setValue(boolean value) {
		if (value)
			this.value = new Double(0);
		else
			this.value = new Double(1);
		
	}

	/**
	 * Set a new Variable's value from a char.
	 * @param value New value.
	 */
	public void setValue(char value) {
		this.value = new Double((double) value);
	}

	/**
	 * Set a new Variable's value from a byte.
	 * @param value New value.
	 */
	public void setValue(byte value) {
		this.value = new Double((double) value);
	}

	/**
	 * Set a new Variable's value from a double.
	 * @param value New value.
	 */
	public void setValue(double value) {
		this.value = new Double((double) value);
	}

	/**
	 * Set a new Variable's value from a float.
	 * @param value New value.
	 */
	public void setValue(float value) {
		this.value = new Double((double) value);
	}

	/**
	 * Set a new Variable's value from a long.
	 * @param value New value.
	 */
	public void setValue(long value) {
		this.value = new Double((double) value);
	}

	/**
	 * Set a new Variable's value from a short.
	 * @param value New value.
	 */
	public void setValue(short value) {
		this.value = new Double((double) value);
	}

}
