package es.ull.iis.simulation.variable;


public interface UserVariable extends Variable {

	/**
	 * Set a new Variable's value from an Object.
	 * @param value New value.
	 */
	public void setValue(Object value);
	
	/**
	 * Set a new Variable's value from an integer.
	 * @param value New value.
	 */
	public void setValue(int value);
	
	/**
	 * Set a new Variable's value from a boolean.
	 * @param value New value.
	 */
	public void setValue(boolean value);
	
	/**
	 * Set a new Variable's value from a char.
	 * @param value New value.
	 */
	public void setValue(char value);
	
	/**
	 * Set a new Variable's value from a byte.
	 * @param value New value.
	 */
	public void setValue(byte value);
	
	/**
	 * Set a new Variable's value from a double.
	 * @param value New value.
	 */
	public void setValue(double value);
	
	/**
	 * Set a new Variable's value from a float.
	 * @param value New value.
	 */
	public void setValue(float value);
	
	/**
	 * Set a new Variable's value from a long.
	 * @param value New value.
	 */
	public void setValue(long value);
	
	/**
	 * Set a new Variable's value from a short.
	 * @param value New value.
	 */
	public void setValue(short value);
	
}
