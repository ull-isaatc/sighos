/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation;

import java.util.TreeMap;
import java.util.concurrent.Callable;

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
import es.ull.isaatc.util.Output;

/**
 * Main simulation class. A simulation needs a model (introduced by means of the
 * <code>createModel()</code> method) to create several structures: activity
 * managers, logical processes...
 * <p>
 * A simulation use <b>InfoListeners</b> to show results. The "listeners" can
 * be added by invoking the <code>addListener()</code> method. When the
 * <code>endTs</code> is reached, it stores its state. This state can be
 * obtained by using the <code>getState</code> method.
 * 
 * @author Iván Castilla Rodríguez
 */
public abstract class Simulation implements VariableStore, Identifiable, Describable, Callable<Integer>, Runnable, Debuggable {
	/** Simulation's identifier */
	protected int id;
	
	/** A short text describing this simulation. */
	protected String description;

	/** Timestamp of simulation's start */
	protected double internalStartTs;

	/** Timestamp of Simulation's end */
	protected double internalEndTs;

	/** Timestamp of simulation's start */
	protected SimulationTime startTs;

	/** Timestamp of Simulation's end */
	protected SimulationTime endTs;
	
	/** Output for printing messages */
	protected Output out = null;
	
    /** Variable warehouse */
	protected final TreeMap<String, Variable> varCollection = new TreeMap<String, Variable>();
	
	protected SimulationTimeUnit unit = null;

	/**
	 * Empty constructor for compatibility purposes
	 */
	public Simulation() {
	}
	
	/**
	 * Creates a new instance of Simulation
	 * 
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 */
	public Simulation(int id, String description, SimulationTimeUnit unit) {
		super();
		this.id = id;
		this.description = description;
		this.unit = unit;
	}


	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id
	 *            This simulation's identifier
	 * @param description
	 *            A short text describing this simulation.
	 * @param startTs
	 *            Timestamp of simulation's start
	 * @param endTs
	 *            Timestamp of simulation's end
	 */
	public Simulation(int id, String description, SimulationTimeUnit unit, SimulationTime startTs, SimulationTime endTs) {
		this(id, description, unit);

		this.startTs = startTs;
		this.internalStartTs = simulationTime2Double(startTs);
		this.endTs = endTs;
		this.internalEndTs = simulationTime2Double(endTs);
	}
	
	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id
	 *            This simulation's identifier
	 * @param description
	 *            A short text describing this simulation.
	 * @param startTs
	 *            Simulation's start timestamp expresed in Simulation Time Units
	 * @param endTs
	 *            Simulation's end timestamp expresed in Simulation Time Units
	 */
	public Simulation(int id, String description, SimulationTimeUnit unit, double startTs, double endTs) {
		this(id, description, unit);

		this.startTs = new SimulationTime(unit, startTs);
		this.internalStartTs = simulationTime2Double(this.startTs);
		this.endTs = new SimulationTime(unit, endTs);
		this.internalEndTs = simulationTime2Double(this.endTs);
	}
	
	/**
	 * Contains the specifications of the model. All the components of the model
	 * must be declared here.
	 * <p>
	 * The components are added simply by invoking their constructors. For
	 * example: <code>
	 * Activity a1 = new Activity(0, this, "Act1");
	 * ResourceType rt1 = new ResourceType(0, this, "RT1");
	 * </code>
	 */
	protected abstract void createModel();

	/**
	 * Specifies the way the structure of the activity managers is built. 
	 * @see StandardAMSimulation
	 */
	protected abstract void createActivityManagers();
	
	/**
	 * Specifies the way the structure of the logical processes is built. 
	 * @see StandAloneLPSimulation
	 * @see SimpleLPSimulation
	 */
	protected abstract void createLogicalProcesses();

	public Integer call() {
		run();
		return 0;
}
	/**
	 * Starts the simulation execution in a threaded way. Initializes all the structures, and
	 * starts the logical processes. 
	 */
	public void start() {
		new Thread(this).start();		
	}

	public double simulationTime2Double(SimulationTime source) {
		return unit.convert(source) * Double.MIN_VALUE;
	}
	
	public SimulationTime double2SimulationTime(double sourceValue) {
		return new SimulationTime(unit, sourceValue / Double.MIN_VALUE);
	}
	
	/**
	 * @return the unit
	 */
	public SimulationTimeUnit getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(SimulationTimeUnit unit) {
		this.unit = unit;
	}

	/**
	 * Returns the simulation end timestamp.
	 * 
	 * @return Value of property endTs.
	 */
	public double getInternalEndTs() {
		return internalEndTs;
	}

	/**
	 * Returns the simulation end timestamp.
	 * 
	 * @return Value of property endTs.
	 */
	public SimulationTime getEndTs() {
		return endTs;
	}

	/**
	 * @param endTs the endTs to set
	 */
	public void setEndTs(SimulationTime endTs) {
		this.endTs = endTs;
		this.internalEndTs = simulationTime2Double(endTs);
	}

	/**
	 * Returns the simulation start timestamp.
	 * 
	 * @return Returns the startTs.
	 */
	public double getInternalStartTs() {
		return internalStartTs;
	}

	/**
	 * Returns the simulation start timestamp.
	 * 
	 * @return Returns the startTs.
	 */
	public SimulationTime getStartTs() {
		return startTs;
	}

	/**
	 * @param startTs the startTs to set
	 */
	public void setStartTs(SimulationTime startTs) {
		this.startTs = startTs;
		this.internalStartTs = simulationTime2Double(startTs);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Identifiable#getIdentifier()
	 */
	public int getIdentifier() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setIdentifier(int id) {
		this.id = id;
	}

	/**
	 * @param out the out to set
	 */
	public void setOutput(Output out) {
		this.out = out;
	}

	@Override
	public String toString() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Debuggable#debug(java.lang.String)
	 */
	public void debug(String description) {
		out.debug(description);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Debuggable#error(java.lang.String)
	 */
	public void error(String description) {
		out.error(description);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Debuggable#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return out.isDebugEnabled();
	}
	
	/**
	 * Event which is activated before the simulation start.
	 */
	public void init(){};
	
	/**
	 * Event which is activated after the simulation finish.
	 */
	public void end(){};
	
	/**
	 * Event which is activated before a clock change.
	 */
	public void beforeClockTick(){};
	
	/**
	 * Event which is activated after a clock change.
	 */
	public void afterClockTick(){}
	
	public Variable getVar(String varName) {
		return varCollection.get(varName);
	}
	
	public void putVar(String varName, Variable value) {
		varCollection.put(varName, value);
	}
	
	public void putVar(String varName, double value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new DoubleVariable(value));
	}
	
	public void putVar(String varName, int value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new IntVariable(value));
	}

	public void putVar(String varName, boolean value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new BooleanVariable(value));
	}

	public void putVar(String varName, char value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new CharacterVariable(value));
	}
	
	public void putVar(String varName, byte value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ByteVariable(value));
	}

	public void putVar(String varName, float value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new FloatVariable(value));
	}
	
	public void putVar(String varName, long value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new LongVariable(value));
	}
	
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
		Number value = getVar(varName).getValue(params);
		if (value != null)
			return value.doubleValue();
		else
			return -1;
	}
}
