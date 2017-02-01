/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.iis.simulation.core;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.inforeceiver.InfoHandler;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.SimulationInfoHandler;
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
import es.ull.iis.util.Output;

/**
 * Main simulation class, identified by means of an identifier and a description.
 * A simulation executes a model defined by means of different structures: 
 * <ul>
 * <li>{@link ResourceType}</li>
 * <li>{@link Resource}</li>
 * <li>{@link WorkGroup}</li>
 * <li>{@link Activity}</li>
 * <li>{@link ElementType}</li>
 * <li>{@link Flow}</li>
 * <li>{@link TimeDrivenGenerator}</li>
 * </ul>
 * A simulation has an associated clock which starts in <tt>startTs</tt> and advances according 
 * to the events produced by the {@link Element}s, {@link Resource}s and {@link TimeDrivenGenerator}s. 
 * A "next-event" technique is used to determine the next timestamp to advance. A minimum 
 * {@link TimeUnit} determines the accuracy of the simulation's clock. The simulation ends when the 
 * simulation clock reaches the <tt>endTs</tt> timestamp or no more events are available.<br>
 * Depending on the specific implementation, a simulation can use one or more "worker" threads to 
 * execute the event's actions.
 * <p>
 * A user can interact with this Simulation by filling in some user methods that are activated in different
 * instants:
 * <ul>
 * <li>Just before the simulation starts {@link #init()}</li>
 * <li>Just after the simulation ends {@link #end()}</li>
 * <li>Just before the simulation clock advances {@link #beforeClockTick()}</li> 
 * <li>Just After the simulation clock advances {@link #afterClockTick()}</li> 
 * </ul> 
 * <p>
 * A simulation uses {@link InfoReceiver}s to show results. Those "listeners" can
 * be added by invoking the {@link #addInfoReceiver(InfoReceiver)} method. 
 * <p>
 * For debugging purposes, an {@link Output} can be associated to this simulation, thus
 * defining the destination for error and debug messages.
 * @author Iván Castilla Rodríguez
 */
public abstract class Simulation implements Callable<Integer>, Runnable, Identifiable, Describable, Debuggable, VariableStore {
	/** Simulation's identifier */
	protected int id;
	
	/** A short text describing this simulation. */
	protected String description;

	/** Simulation time unit */
	protected TimeUnit unit = null;

	/** Timestamp of simulation's start */
	protected TimeStamp startTs;

	/** Timestamp of Simulation's end */
	protected TimeStamp endTs;
	
	/** A value representing the simulation's start timestamp without unit */
	protected long internalStartTs;

	/** A value representing the simulation's end timestamp without unit */
	protected long internalEndTs;
	
	/** Output for printing debug and error messages */
	protected Output out = null;
	
    /** Variable store */
	protected final Map<String, Variable> varCollection = new TreeMap<String, Variable>();
	
	/** A handler for the information produced by the execution of this simulation */
	protected final SimulationInfoHandler infoHandler = new SimulationInfoHandler();
	
	/** Number of worker threads which run the simulation events (if required by the implementation) */
	protected int nThreads = 1;
	
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
	 * @param unit This simulation's time unit
	 */
	public Simulation(int id, String description, TimeUnit unit) {
		super();
		this.id = id;
		this.description = description;
		this.unit = unit;
	}


	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 * @param unit This simulation's time unit
	 * @param startTs Timestamp of simulation's start
	 * @param endTs Timestamp of simulation's end
	 */
	public Simulation(int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		this(id, description, unit);

		this.startTs = startTs;
		this.internalStartTs = simulationTime2Long(startTs);
		this.endTs = endTs;
		this.internalEndTs = simulationTime2Long(endTs);
	}
	
	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 * @param unit This simulation's time unit
	 * @param startTs Timestamp of simulation's start expressed in Simulation Time Units
	 * @param endTs Timestamp of simulation's end expressed in Simulation Time Units
	 */
	public Simulation(int id, String description, TimeUnit unit, long startTs, long endTs) {
		this(id, description, unit);

		this.startTs = new TimeStamp(unit, startTs);
		this.internalStartTs = startTs;
		this.endTs = new TimeStamp(unit, endTs);
		this.internalEndTs = endTs;
	}

	/**
	 * A convenience method for converting a timestamp to a long value expressed in the
	 * simulation's time unit.
	 * @param source A timestamp
	 * @return A long value representing the received timestamp in the simulation's time unit 
	 */
	public long simulationTime2Long(TimeStamp source) {
		return unit.convert(source);
	}
	
	/**
	 * A convenience method for converting a long value expressed in the simulation's time unit
	 * to a timestamp.
	 * @param sourceValue A long value expressed in the simulation's time unit
	 * @return A timestamp representing the received long value in the simulation's time unit 
	 */
	public TimeStamp long2SimulationTime(long sourceValue) {
		return new TimeStamp(unit, sourceValue);
	}

	@Override
	public Integer call() {
		run();
		return 0;
}
	/**
	 * Starts the simulation execution in a threaded way. Initializes all the structures, and
	 * starts the workers. 
	 */
	public void start() {
		new Thread(this).start();		
	}
	
	/**
	 * Returns the number of workers to execute events defined in this simulation.
	 * @return the number of workers to execute events defined in this simulation
	 */
	public int getNThreads() {
		return nThreads;
	}

	/**
	 * Sets the number of workers to execute events defined in this simulation.
	 * @param threads the number of workers to execute events defined in this simulation
	 */
	public void setNThreads(int threads) {
		nThreads = threads;
	}

	/**
	 * Returns this simulation's time unit
	 * @return the unit
	 */
	public TimeUnit getTimeUnit() {
		return unit;
	}
	
	/**
	 * Sets this simulation's time unit
	 * @param unit New time unit of this simulation
	 */
	public void setTimeUnit(TimeUnit unit) {
		this.unit = unit;
	}

	/**
	 * Returns a long value representing the simulation's end timestamp without unit.
	 * @return A long value representing the simulation's end timestamp without unit
	 */
	public long getInternalEndTs() {
		return internalEndTs;
	}

	/**
	 * Returns the simulation's end timestamp.
	 * @return Simulation's end timestamp
	 */
	public TimeStamp getEndTs() {
		return endTs;
	}

	/**
	 * Sets the simulation's end timestamp
	 * @param endTs New simulation end
	 */
	public void setEndTs(TimeStamp endTs) {
		this.endTs = endTs;
		this.internalEndTs = simulationTime2Long(endTs);
	}

	/**
	 * Returns a long value representing the simulation's start timestamp without unit.
	 * @return A long value representing the simulation's start timestamp without unit
	 */
	public long getInternalStartTs() {
		return internalStartTs;
	}

	/**
	 * Returns the simulation's start timestamp.
	 * @return Simulation's start timestamp
	 */
	public TimeStamp getStartTs() {
		return startTs;
	}

	/**
	 * Sets the simulation's start timestamp
	 * @param startTs New simulation start
	 */
	public void setStartTs(TimeStamp startTs) {
		this.startTs = startTs;
		this.internalStartTs = simulationTime2Long(startTs);
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a new description for this simulation.
	 * @param description The new description of this simulation 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int getIdentifier() {
		return id;
	}

	/**
	 * Sets a new identifier for this simulation.
	 * @param id The new identifier for this simulation
	 */
	public void setIdentifier(int id) {
		this.id = id;
	}
	
	/**
	 * Sets an output for debugging and error messages.
	 * @param out The new output for debugging and error messages
	 */
	public void setOutput(Output out) {
		this.out = out;
	}

	/**
	 * Returns the activity with the corresponding identifier.
	 * @param id Activity identifier.
	 * @return An activity with the indicated identifier.
	 */
	public abstract BasicStep getActivity(int id);

	/** 	 
	 * Returns a list of the activities of the model. 	 
	 * 
	 *  @return Activities of the model. 	 
	 */ 	
	public abstract Map<Integer, ? extends BasicStep> getActivityList();
	
	/**
	 * Returns the resource type with the corresponding identifier.
	 * @param id Resource type identifier.
	 * @return A resource type with the indicated identifier.
	 */
	public abstract ResourceType getResourceType(int id);

	/**
	 * Returns a list of the resource types of the model.
	 * 
	 * @return Resource types of the model.
	 */
	public abstract Map<Integer, ? extends ResourceType> getResourceTypeList();
	
	/**
	 * Returns the resource with the corresponding identifier.
	 * @param id Resource identifier.
	 * @return A resource with the indicated identifier.
	 */
	public abstract Resource getResource(int id);

	/**
	 * Returns a list of the resources of the model.
	 * 
	 * @return Resources of the model.
	 */
	public abstract Map<Integer, ? extends Resource> getResourceList();

	/**
	 * Returns the element type with the corresponding identifier.
	 * @param id Element type identifier.
	 * @return An element type with the indicated identifier.
	 */
	public abstract ElementType getElementType(int id);

	/**
	 * Returns a list of the element types of the model.
	 * 
	 * @return element types of the model.
	 */
	public abstract Map<Integer, ? extends ElementType> getElementTypeList();
	
	/**
	 * Returns the flow with the corresponding identifier.
	 * @param id Flow identifier.
	 * @return A flow with the indicated identifier.
	 */
	public abstract Flow getFlow(int id);

	/**
	 * Returns a list of the flows of the model.
	 * 
	 * @return flows of the model.
	 */
	public abstract Map<Integer, ? extends Flow> getFlowList();
	
	@Override
	public String toString() {
		return description + "(" + startTs + ", " + endTs + ")";
	}

	@Override
	public void debug(String description) {
		out.debug(description);
	}

	@Override
	public void error(String description) {
		out.error(description);
	}

	@Override
	public boolean isDebugEnabled() {
		return out.isDebugEnabled();
	}
	
	// User methods
	
	/**
	 * Allows a user for adding customized code before the simulation starts.
	 */
	public void init() {
	};
	
	/**
	 * Allows a user for adding customized code after the simulation finishes.
	 */
	public void end() {
	};
	
	/**
	 * Allows a user for adding customized code before the simulation clock advances.
	 */
	public void beforeClockTick() {
	};
	
	/**
	 * Allows a user for adding customized code just after the simulation clock advances.
	 */
	public void afterClockTick() {
	}

	// End of user methods
	
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
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new DoubleVariable(value));
	}
	
	@Override
	public void putVar(String varName, int value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new IntVariable(value));
	}

	@Override
	public void putVar(String varName, boolean value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new BooleanVariable(value));
	}

	@Override
	public void putVar(String varName, char value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new CharacterVariable(value));
	}
	
	@Override
	public void putVar(String varName, byte value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ByteVariable(value));
	}

	@Override
	public void putVar(String varName, float value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new FloatVariable(value));
	}
	
	@Override
	public void putVar(String varName, long value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new LongVariable(value));
	}
	
	@Override
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
	
	/**
	 * Adds an information receiver which processes information produced by this simulation.
	 * @param receiver A processor for the information produced by this simulation
	 */
	public void addInfoReceiver(InfoReceiver receiver) {
		infoHandler.registerReceivers(receiver);
	}

	/**
	 * Returns the handler for the information produced by the execution of this simulation.
	 * @return The handler for the information produced by the execution of this simulation
	 */
	public InfoHandler getInfoHandler() {
		return infoHandler;
	}
}
