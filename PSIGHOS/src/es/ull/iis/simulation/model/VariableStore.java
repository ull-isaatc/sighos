package es.ull.iis.simulation.model;

import es.ull.iis.simulation.variable.Variable;

/**
 * An object capable to store {@link Variable variables}
 * @author Iván Castilla Rodríguez
 *
 */
public interface VariableStore 
{
	/**
	 * Returns a simulation's variable.
	 * @param varName Variable name.
	 * @return The Variable.
	 */
	public Variable getVar(final String varName);
	
	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final Variable value);
	
	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final double value);
	
	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final int value);

	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final boolean value);
	
	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final char value);
	
	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name
	 * @param value The new value.
	 */
	public void putVar(final String varName, final byte value);

	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final float value);
	
	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final long value);
	
	/**
	 * Assigns a value to a variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(final String varName, final short value);
}
