/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

/**
 * A collection of parameters for a model.  
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelParameterMap {
	/** A single parameter for a model */
	public interface ModelParameter {
		/**
		 * Returns the type of the parameter.
		 * @return the type of the parameter
		 */
		Class<?> getType();
		/**
		 * Returns the index used to allocate the parameter in the collection of parameters.
		 * @return the index used to allocate the parameter in the collection of parameters
		 */
		int ordinal();
	}
	
	/** The inner collection of parameters */
	protected Object[] values;

	/** Creates a collection of parameters of fixed size.
	 * @param size Size of the collection of parameters
	 */
	public ModelParameterMap(int size) {
		values = new Object[size];
	}
	
	/**
	 * Adds the value of a parameter. Throws a {@link RuntimeException} if the parameter has been already defined.
	 * Throws a {@link ClassCastException} if the value has an incompatible type.
	 * @param key The identifier of the parameter
	 * @param value The value of the parameter
	 */
	public void put(ModelParameter key, Object value) {
		if (values[key.ordinal()] != null)
			throw new RuntimeException("Parameter <<" + key + ">> already initialized");
		else if (key.getType().isInstance(value))
			values[key.ordinal()] = value;
		else
			throw new ClassCastException("Invalid class. Received: " + value.getClass() + " expected: " + key.getType());
	}
	
	/**
	 * Returns the value for the specified parameter.
	 * @param key The identifies of the parameter
	 * @return The value for the specified parameter
	 */
	public Object get(ModelParameter key) {
		return values[key.ordinal()];
	}
}
