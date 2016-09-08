package es.ull.iis.simulation.parallel;

import java.util.TreeMap;

import es.ull.iis.simulation.core.VariableStore;
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
 * An identifiable object belonging to a simulation which can be compared and can use 
 * {@link Variable simulation variables}. The identifier is unique per type of simulation object, 
 * thus different types of simulation objects can use the same identifiers.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class VariableStoreSimulationObject extends SimulationObject implements VariableStore {
    /** Variable store */
	protected final TreeMap<String, Variable> varCollection = new TreeMap<String, Variable>();
    
	/**
     * Creates a new simulation object which can store variables.
     * @param id Unique identifier of the object
     * @param simul Simulation this object belongs to
     */
	public VariableStoreSimulationObject(int id, Simulation simul) {
		super(id, simul);
	}

	@Override
	public Variable getVar(String varName) {
		return varCollection.get(varName);
	}
	
	@Override
	public void putVar(String varName, Variable value) {
		varCollection.put(varName, value);
	}
	
	@Override
	public void putVar(String varName, double value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new DoubleVariable(value));
	}
	
	@Override
	public void putVar(String varName, int value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new IntVariable(value));
	}

	@Override
	public void putVar(String varName, boolean value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new BooleanVariable(value));
	}

	@Override
	public void putVar(String varName, char value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new CharacterVariable(value));
	}
	
	@Override
	public void putVar(String varName, byte value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ByteVariable(value));
	}

	@Override
	public void putVar(String varName, float value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new FloatVariable(value));
	}
	
	@Override
	public void putVar(String varName, long value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new LongVariable(value));
	}
	
	@Override
	public void putVar(String varName, short value) {
		final UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ShortVariable(value));
	}

	/**
	 * Notifies the change of a simulation's variable view
	 * @param params The required parameters
	 * @return The value of the variable
	 */
	public double getVarViewValue(Object...params) {
		final String varName = (String) params[0];
		params[0] = this;
		final Number value = simul.getInfoHandler().notifyInfo(new VarViewValueRequestInfo(simul, varName, this, params, (Long)params[params.length-1]));
		if (value != null)
			return value.doubleValue();
		else
			return -1;
	}	
}