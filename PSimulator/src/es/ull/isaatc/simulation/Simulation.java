/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation;

import java.util.concurrent.Callable;

import es.ull.isaatc.simulation.model.Model;
import es.ull.isaatc.simulation.model.Time;
import es.ull.isaatc.simulation.model.TimeUnit;
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
	
	/** The model to be simulated */
	protected Model model;
	
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
	 * @param model the model to be simulated
	 */
	public Simulation(int id, Model model) {
		super();
		this.id = id;
		this.model = model;
		this.internalStartTs = simulationTime2Double(model.startTs);
		this.internalEndTs = simulationTime2Double(model.endTs);
	}
	
	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
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
		return model.getUnit().convert(source) * Double.MIN_VALUE;
	}
	
	public Time double2SimulationTime(double sourceValue) {
		return new Time(model.getUnit(), sourceValue / Double.MIN_VALUE);
	}
	
	/**
	 * @return the unit
	 */
	public TimeUnit getUnit() {
		return model.unit;
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
		return model.endTs;
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
		return model.startTs;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return model.description;
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
		return model.description + "(" + model.startTs + ", " + model.endTs + ")";
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
	 * FIXME: Poner directamente la llamada donde corresponda
	 */
	public void init() {
		model.init();
	};
	
	/**
	 * Event which is activated after the simulation finish.
	 * FIXME: Poner directamente la llamada donde corresponda
	 */
	public void end() {
		model.end();
	};
	
	/**
	 * Event which is activated before a clock change.
	 * FIXME: Poner directamente la llamada donde corresponda
	 */
	public void beforeClockTick() {
		model.beforeClockTick();
	};
	
	/**
	 * Event which is activated after a clock change.
	 * FIXME: Poner directamente la llamada donde corresponda
	 */
	public void afterClockTick() {
		model.afterClockTick();
	}
}
