/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelParameterMap {
	public interface ModelParameter {
		Class<?> getType();
		int ordinal();
	}
	
	protected Object[] values;

	public ModelParameterMap(int size) {
		values = new Object[size];
	}
	
	public void put(ModelParameter key, Object value) {
		if (values[key.ordinal()] != null)
			throw new RuntimeException("Parameter <<" + key + ">> already initialized");
		else if (key.getType().isInstance(value))
			values[key.ordinal()] = value;
		else
			throw new ClassCastException("Invalid class. Received: " + value.getClass() + " expected: " + key.getType());
	}
	
	public Object get(ModelParameter key) {
		return values[key.ordinal()];
	}
}
