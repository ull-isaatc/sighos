package es.ull.iis.simulation.sequential;

import java.util.TreeMap;

import es.ull.iis.simulation.info.VarViewValueRequestInfo;
import es.ull.iis.simulation.variable.BooleanVariable;
import es.ull.iis.simulation.variable.ByteVariable;
import es.ull.iis.simulation.variable.CharacterVariable;
import es.ull.iis.simulation.variable.DoubleVariable;
import es.ull.iis.simulation.variable.FloatVariable;
import es.ull.iis.simulation.variable.IntVariable;
import es.ull.iis.simulation.variable.LongVariable;
import es.ull.iis.simulation.variable.ShortVariable;
import es.ull.iis.simulation.variable.UserVariable;
import es.ull.iis.simulation.variable.Variable;


/**
 * An identifiable object belonging to a simulation which can be compared. The identifier is
 * unique per type of simulation object, thus different types of simulation objects can use 
 * the same identifiers.
 * @author Iván Castilla Rodríguez
 */
public abstract class VariableStoreSimulationObject extends SimulationObject implements es.ull.iis.simulation.core.VariableStoreSimulationObject {
    /** Variable warehouse */
	protected final TreeMap<String, Variable> varCollection = new TreeMap<String, Variable>();
    
	/**
     * Creates a new simulation object.
     * @param id Unique identifier of the object
     * @param simul Simulation this object belongs to
     */
	public VariableStoreSimulationObject(int id, SequentialSimulationEngine simul, String objTypeId) {
		super(id, simul, objTypeId);
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
	
	public double getVarViewValue(Object...params) {
		String varName = (String) params[0];
		params[0] = this;
		Number value = ((SequentialSimulationEngine)simul).getInfoHandler().notifyInfo(new VarViewValueRequestInfo((SequentialSimulationEngine)simul, varName, this, params, (Long)params[params.length-1]));
		if (value != null)
			return value.doubleValue();
		else
			return -1;
	}	
}
