package es.ull.isaatc.simulation.model;

import java.util.TreeMap;

import es.ull.isaatc.simulation.VariableStore;
import es.ull.isaatc.simulation.variable.BooleanVariable;
import es.ull.isaatc.simulation.variable.ByteVariable;
import es.ull.isaatc.simulation.variable.CharacterVariable;
import es.ull.isaatc.simulation.variable.DoubleVariable;
import es.ull.isaatc.simulation.variable.FloatVariable;
import es.ull.isaatc.simulation.variable.IntVariable;
import es.ull.isaatc.simulation.variable.LongVariable;
import es.ull.isaatc.simulation.variable.ShortVariable;
import es.ull.isaatc.simulation.variable.UserVariable;
import es.ull.isaatc.simulation.variable.Variable;


/**
 * An identifiable object belonging to a simulation which can be compared. The identifier is
 * unique per type of simulation object, thus different types of simulation objects can use 
 * the same identifiers.
 * @author Iván Castilla Rodríguez
 */
public abstract class VariableStoreModelObject extends ModelObject implements VariableStore {
    /** Variable warehouse */
	protected final TreeMap<String, Variable> varCollection = new TreeMap<String, Variable>();
    
	/**
     * Creates a new simulation object.
     * @param id Unique identifier of the object
     * @param model Model this object belongs to
     */
	public VariableStoreModelObject(int id, Model model) {
		super(id, model);
	}

	/**
	 * Obtain a simulation's variable.
	 * @param varName Variable name.
	 * @return The Variable.
	 */
	public Variable getVar(String varName) {
		return varCollection.get(varName);
	}
	
	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, Variable value) {
		varCollection.put(varName, value);
	}
	
	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, double value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new DoubleVariable(value));
	}
	
	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, int value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new IntVariable(value));
	}

	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, boolean value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new BooleanVariable(value));
	}

	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, char value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new CharacterVariable(value));
	}
	
	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name
	 * @param value The new value.
	 */
	public void putVar(String varName, byte value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ByteVariable(value));
	}

	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, float value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new FloatVariable(value));
	}
	
	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, long value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new LongVariable(value));
	}
	
	/**
	 * Assign value to a simulation's variable.
	 * @param varName Variable name.
	 * @param value The new value.
	 */
	public void putVar(String varName, short value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ShortVariable(value));
	}
}
