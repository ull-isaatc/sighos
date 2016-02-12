package es.ull.iis.simulation.variable;


/**
 * Class which define simulation's numeric variables. 
 * @author ycallero
 *
 */
public abstract class NumberVariable implements UserVariable {

	/** Variable's value */
	Number value;
	
	/**
	 * Obtain de current Variable's value.
	 * @return Variable's value.
	 */
	public Number getValue(Object... params) {
		return value;
	}

	/**
	 * Set a new Variable's value from an Number.
	 * @param value New value.
	 */
	public void setValue(Number value) {
		this.value = value;
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
		this.value = (Number) value;
	}

}
