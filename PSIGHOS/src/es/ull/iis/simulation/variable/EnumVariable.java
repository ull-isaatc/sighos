package es.ull.iis.simulation.variable;

import java.util.Iterator;
import java.util.TreeMap;


/**
 * Simulation's variable which house a enumerate type.
 * @author ycallero
 *
 */
public class EnumVariable implements UserVariable{
	
	/** Actual variable's value */
	Integer value;
	/** Variable's type */
	EnumType type;

	/**
	 * Create a new EnumVariable. 
	 * @param type Variable's type.
	 * @param defaultValue Init variable's value.
	 */
	public EnumVariable(EnumType type, Integer defaultValue) {
		value = defaultValue;
		this.type = type;
	}
	
	/**
	 * Create a new EnumVariable. 
	 * @param type Variable's type.
	 * @param defaultValue Init variable's value.
	 */
	public EnumVariable(EnumType type, Double defaultValue) {
		value = new Integer (defaultValue.intValue());
		this.type = type;
	}
	
	/**
	 * Obtain de current Variable's value.
	 * @return Variable's value.
	 */
	public Number getValue(Object... params) {
		if (value > type.getMaxValue())
			return -1;
		return value;
	}

	/**
	 * Set a new Variable's value from an Object.
	 * @param value New value.
	 */
	public void setValue(Object value) {
		if (value instanceof String) {
			TreeMap<Integer, String> values = type.getValuesDescrip();
			/** If value exist in the type */
			if (values.containsValue(value)) {
				Iterator<Integer> itr = values.keySet().iterator();
				while (itr.hasNext()) {
					Integer key = (Integer) itr.next();
					if (values.get(key).equals(value)) {
						this.value = key;
						break;
					}
				}
			} else
				/** If the value doesn't exist in the type */ 
				this.value = new Integer(-1);
		} else
			this.value = (Integer) value;
	}

	/**
	 * Set a new Variable's value from an integer.
	 * @param value New value.
	 */
	public void setValue(int value) {
		this.value = new Integer(value);	
	}

	/**
	 * Set a new Variable's value from a boolean.
	 * @param value New value.
	 */
	public void setValue(boolean value) {
		if (value)
			this.value = new Integer(0);
		else
			this.value = new Integer(1);
	}

	/**
	 * Set a new Variable's value from a char.
	 * @param value New value.
	 */
	public void setValue(char value) {
		this.value = new Integer(value);
	}

	/**
	 * Set a new Variable's value from a byte.
	 * @param value New value.
	 */
	public void setValue(byte value) {
		this.value = new Integer(value);
	}

	/**
	 * Set a new Variable's value from a double.
	 * @param value New value.
	 */
	public void setValue(double value) {
		this.value = new Integer((int) value);
	}

	/**
	 * Set a new Variable's value from a float.
	 * @param value New value.
	 */
	public void setValue(float value) {
		this.value = new Integer((int) value);
	}

	/**
	 * Set a new Variable's value from a long.
	 * @param value New value.
	 */
	public void setValue(long value) {
		this.value = new Integer((int) value);
	}

	/**
	 * Set a new Variable's value from a short.
	 * @param value New value.
	 */
	public void setValue(short value) {
		this.value = new Integer(value);
	}

	/**
	 * Compare two Variables. 
	 * @param arg0 The Variable which you want compare.
	 * @return True if both are equal.
	 */
	public boolean equals(Variable arg0) {
		return value.equals(arg0.getValue().intValue());
	}

	/**
	 * Convert Variable's value to a string.
	 * @return String which represents Variable's value.
	 */
	public String toString() {
		return type.getValuesDescrip().get(value);
	}
}
