package es.ull.iis.simulation.factory;

import java.util.Set;
import java.util.TreeMap;

/**
 * A structure to store user code to be used as the body of the user methods defined in the simulator.
 * @author Yeray Callero and Iván Castilla
 *
 */
public class SimulationUserCode {
	/** Import statement*/
	String imports = "";
	/** A map of <user methods, body> */
	TreeMap<UserMethod, String> container;
	
	/**
	 * Creates a new storage for defining user methods.
	 *
	 */
	public SimulationUserCode() {
		container = new TreeMap<UserMethod, String>();
	}
	
	/**
	 * Add a new event code to the list.
	 * @param method String that indexed the code.
	 * @param code Event code.
	 */
	public void add(UserMethod method, String code) {
		container.put(method, code);
	}
	
	public void addImports(String imports) {
		this.imports += imports;
	}
	
	public String getImports() {
		return imports;
	}
	
	/**
	 * Get an event code.
	 * @param event Index of the code.
	 * @return The event Java code.
	 */
	public String get(UserMethod event) {
		return container.get(event);
	}
	
	public Set<UserMethod> getDefinedMethods() {
		return container.keySet();
	}
	
	/**
	 * Empty the store.
	 *
	 */
	public void clear() {
		container.clear();
	}
	
	/**
	 * Obtain the store size.
	 * @return The size.
	 */
	public int size() {
		return container.size();
	}

	/**
	 * Remove one element of the store.
	 * @param key Index of the element.
	 */
	public void remove(String key) {
		container.remove(key);
	}
}
