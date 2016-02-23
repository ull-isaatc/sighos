package es.ull.iis.simulation.core;

import es.ull.iis.simulation.variable.Variable;

public interface VariableStore {
	/**
	 * Returns a simulation's variable.
	 * @param varName Variable name.
	 * @return The Variable.
	 */
	public Variable getVar(String varName);
	
	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, Variable value);
	
	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, double value);
	
	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, int value);

	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, boolean value);
	
	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, char value);
	
	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name
	 * @param value The new value.
	 */
	public void putVar(String varName, byte value);

	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, float value);
	
	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, long value);
	
	/**
	 * Assigns a value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, short value);
}
