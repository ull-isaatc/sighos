/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.iis.simulation.model;

import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.util.Output;

/**
 * Main simulation class, identified by means of an identifier and a description.
 * A simulation executes a model defined by means of different structures: 
 * <ul>
 * <li>{@link ResourceTypeEngine}</li>
 * <li>{@link ResourceEngine}</li>
 * <li>{@link WorkGroup}</li>
 * <li>{@link ActivityFlow}</li>
 * <li>{@link ElementType}</li>
 * <li>{@link Flow}</li>
 * <li>{@link TimeDrivenGenerator}</li>
 * </ul>
 * A simulation has an associated clock which starts in <tt>startTs</tt> and advances according 
 * to the events produced by the {@link Element}s, {@link ResourceEngine}s and {@link TimeDrivenGenerator}s. 
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
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class SimulationEngine implements Identifiable, Debuggable {
	/** Simulation's identifier */
	protected final int id;
	
	/** A value representing the simulation's start timestamp without unit */
	protected final long internalStartTs;

	/** A value representing the simulation's end timestamp without unit */
	protected final long internalEndTs;
	
	/** Number of worker threads which run the simulation events (if required by the implementation) */
	protected int nThreads = 1;
	
	protected final Model model;
	
	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 * @param unit This simulation's time unit
	 * @param startTs Timestamp of simulation's start expressed in Simulation Time Units
	 * @param endTs Timestamp of simulation's end expressed in Simulation Time Units
	 */
	public SimulationEngine(int id, Model model, long startTs, long endTs) {
		this.id = id;
		this.model = model;
		this.internalStartTs = startTs;
		this.internalEndTs = endTs;
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * Returns a long value representing the simulation's end timestamp without unit.
	 * @return A long value representing the simulation's end timestamp without unit
	 */
	public long getInternalEndTs() {
		return internalEndTs;
	}

	/**
	 * Returns a long value representing the simulation's start timestamp without unit.
	 * @return A long value representing the simulation's start timestamp without unit
	 */
	public long getInternalStartTs() {
		return internalStartTs;
	}

	@Override
	public int getIdentifier() {
		return id;
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

	@Override
	public void debug(String description) {
		Model.debug(description);
	}

	@Override
	public void error(String description) {
		Model.error(description);
	}

	@Override
	public boolean isDebugEnabled() {
		return Model.isDebugEnabled();
	}
	
	protected abstract void initializeEngine();
	protected abstract void launchInitialEvents();
	protected abstract void simulationLoop();
	public abstract ActivityWorkGroupEngine getActivityWorkGroupEngineInstance(ActivityWorkGroup modelWG);
	public abstract ResourceTypeEngine getResourceTypeEngineInstance(ResourceType modelRT);
	public abstract ResourceEngine getResourceEngineInstance(Resource modelRes);
	public abstract ElementEngine getElementEngineInstance(Element modelElem);
	public abstract ResourceList getResourceListInstance(ResourceType modelRT);
	public abstract ActivityManagerEngine getActivityManagerEngineInstance(ActivityManager modelAM);
	public abstract void addEvent(DiscreteEvent ev); 
	/**
	 * Prints the current state of the simulation for debug purposes. Prints the current local 
	 * time, the contents of the future event list and the execution queue. 
	 */
	protected abstract void printState();
    /**
     * Returns the current simulation time
     * @return The current simulation time
     */
	public abstract long getTs();
}
