package es.ull.iis.simulation.variable;

import java.util.TreeMap;

/**
 * Define the Enumerate type which used with the simulation's variables.
 * @author ycallero
 *
 */
public class EnumType {

	/** Descriptions of all values */
	TreeMap<Integer, String> valuesDescrip;
	/** Number of values */
	int maxValue = 0;
	
	/**
	 * Create a new EnumType.
	 * @param params List of fields of the type.
	 */
	public EnumType(String ... params) {
		valuesDescrip = new TreeMap<Integer, String>();
		for (int i = 0; i < params.length; i++) {
			valuesDescrip.put(new Integer(maxValue + i), params[i]);
		}
		maxValue += params.length;
	}
	
	/**
	 * Add a field to the type.
	 * @param field New field.
	 */
	public void addField(String field) {
		valuesDescrip.put(new Integer(maxValue), field);
		maxValue++;
	}

	/**
	 * Get the field's number of the type.
	 * @return Defined field's number.
	 */
	public int getMaxValue() {
		return maxValue;
	}
	
	/**
	 * Obtain all the fields asociated with their numeric values. 
	 * @return List of fields with asociated values. 
	 */
	public TreeMap<Integer, String> getValuesDescrip() {
		return valuesDescrip;
	}

	/**
	 * Change the field list of the type. The list have to include
	 * the numeric asociated values.
	 * @param valuesDescrip New field's list.
	 */
	public void setValuesDescrip(TreeMap<Integer, String> valuesDescrip) {
		this.valuesDescrip = valuesDescrip;
	}
}
