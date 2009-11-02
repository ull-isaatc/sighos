/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.common;

import java.util.concurrent.Callable;

import es.ull.isaatc.simulation.Debuggable;
import es.ull.isaatc.simulation.Describable;
import es.ull.isaatc.simulation.Identifiable;
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
public abstract class Simulation implements Identifiable, Describable, Callable<Integer>, Runnable, Debuggable {
	/** Simulation's identifier */
	protected int id;
	
	/** A short text describing this simulation. */
	protected String description;

	/** Simulation time unit */
	protected TimeUnit unit = null;

	/** Timestamp of simulation's start */
	protected Time startTs;

	/** Timestamp of Simulation's end */
	protected Time endTs;
	
	/** Timestamp of simulation's start */
	protected double internalStartTs;

	/** Timestamp of Simulation's end */
	protected double internalEndTs;
	
	/** Output for printing messages */
	protected Output out = null;
	
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
	public Simulation(int id, String description, TimeUnit unit) {
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
	public Simulation(int id, String description, TimeUnit unit, Time startTs, Time endTs) {
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
	public Simulation(int id, String description, TimeUnit unit, double startTs, double endTs) {
		this(id, description, unit);

		this.startTs = new Time(unit, startTs);
		this.internalStartTs = simulationTime2Double(this.startTs);
		this.endTs = new Time(unit, endTs);
		this.internalEndTs = simulationTime2Double(this.endTs);
	}
	
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

	public double simulationTime2Double(Time source) {
		return unit.convert(source) * Double.MIN_VALUE;
	}
	
	public Time double2SimulationTime(double sourceValue) {
		return new Time(unit, sourceValue / Double.MIN_VALUE);
	}
	
	/**
	 * @return the unit
	 */
	public TimeUnit getTimeUnit() {
		return unit;
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
	public Time getEndTs() {
		return endTs;
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
	public Time getStartTs() {
		return startTs;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Identifiable#getIdentifier()
	 */
	public int getIdentifier() {
		return id;
	}

	/**
	 * @param out the out to set
	 */
	public void setOutput(Output out) {
		this.out = out;
	}

	@Override
	public String toString() {
		return description + "(" + startTs + ", " + endTs + ")";
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
	public void init() {
	};
	
	/**
	 * Event which is activated after the simulation finish.
	 */
	public void end() {
	};
	
	/**
	 * Event which is activated before a clock change.
	 */
	public void beforeClockTick() {
	};
	
	/**
	 * Event which is activated after a clock change.
	 */
	public void afterClockTick() {
	}
}
