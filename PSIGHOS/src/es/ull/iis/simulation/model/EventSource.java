/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.TreeMap;

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
 * @author Ivan Castilla Rodriguez
 *
 */
public abstract class EventSource extends ModelObject implements VariableStore {
    /** Variable warehouse */
	protected final TreeMap<String, Variable> varCollection = new TreeMap<String, Variable>();
	
	public EventSource(Model model, int id, String objectTypeId) {
		super(model, id, objectTypeId);
	}
	
	public abstract DiscreteEvent onCreate(long ts);
	public abstract DiscreteEvent onDestroy();

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    public void notifyEnd() {
        model.getSimulationEngine().addEvent(onDestroy());
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
	
    /**
     * The last event this element executes. It decrements the total amount of elements of the
     * simulation.
     * @author Iván Castilla Rodríguez
     */
    public final class DefaultFinalizeEvent extends DiscreteEvent {
        
        public DefaultFinalizeEvent() {
            super(model.getSimulationEngine().getTs());
        }
        
        public void event() {
        	Model.debug(EventSource.this + "\tEnds execution");
        }
    }

    /**
     * The first event this element executes.
     * @author Iván Castilla Rodríguez
     *
     */
    public final class DefaultStartEvent extends DiscreteEvent {
        public DefaultStartEvent(long ts) {
            super(ts);
        }

        public void event() {
        	Model.debug(EventSource.this + "\tStarts Execution");
        }
    }
}
