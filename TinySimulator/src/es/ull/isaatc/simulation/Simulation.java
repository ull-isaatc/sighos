package es.ull.isaatc.simulation;


import java.util.ArrayList;
import java.util.concurrent.Callable;

import es.ull.isaatc.simulation.inforeceiver.InfoHandler;
import es.ull.isaatc.simulation.inforeceiver.InfoReceiver;
import es.ull.isaatc.simulation.inforeceiver.SimulationInfoHandler;
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
	public enum LPType {
		BARRIER,
		STD,
		SEQUENTIAL,
		QUICK,
		BUFFERED,
		BUNCH
	};

	protected LPType type = LPType.STD;
	/** Simulation's identifier */
	protected int id;
	
	/** A short text describing this simulation. */
	protected String description;

	/** List of element generators of the simulation. */
	protected final ArrayList<Generator> generatorList = new ArrayList<Generator>();

	/** Logical Process list */
	protected LogicalProcess lp = null;

	/** Timestamp of simulation's start */
	protected long internalStartTs;

	/** Timestamp of Simulation's end */
	protected long internalEndTs;

	/** Timestamp of simulation's start */
	protected Time startTs;

	/** Timestamp of Simulation's end */
	protected Time endTs;
	
	/** Output for printing messages */
	protected Output out = null;

	private final SimulationInfoHandler infoHandler = new SimulationInfoHandler();
	
	protected TimeUnit unit = null;

	/** Number of threads which handle events in the logical processes */
	protected int nThreads = 1;

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
		this.id = id;
		this.description = description;
		this.unit = unit;
		this.startTs = startTs;
		this.internalStartTs = simulationTime2Long(startTs);
		this.endTs = endTs;
		this.internalEndTs = simulationTime2Long(endTs);
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
	public Simulation(int id, String description, TimeUnit unit, long startTs, long endTs) {
		this(id, description, unit, new Time(unit, startTs), new Time(unit, endTs));
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
	public Simulation(int id, String description, TimeUnit unit, Time startTs, Time endTs, LPType type) {
		this(id, description, unit, startTs, endTs);
		this.type = type;
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
	public Simulation(int id, String description, TimeUnit unit, long startTs, long endTs, LPType type) {
		this(id, description, unit, new Time(unit, startTs), new Time(unit, endTs));
		this.type = type;
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
	 * Starts the simulation execution. It creates and starts all the necessary 
	 * structures. This method blocks until all the logical processes have finished 
	 * their execution.<p>
	 * If a state is indicated, sets the state of this simulation.<p>
	 * Checks if a valid output for debug messages has been declared. Note that no 
	 * debug messages can be printed before this method is declared unless <code>setOutput</code>
	 * had been invoked. 
	 */
	public void run() {
		if (out == null)
			out = new Output();
		
		createModel();
		debug("SIMULATION MODEL CREATED");
		switch (type) {
		case STD: lp = new StdLogicalProcess (this, internalStartTs, internalEndTs, nThreads); break;
		case SEQUENTIAL: lp = new SequentialLogicalProcess(this, internalStartTs, internalEndTs); break;
		case BARRIER: lp = new BarrierLogicalProcess(this, internalStartTs, internalEndTs, nThreads); break;
		case QUICK: lp = new QuickLogicalProcess(this, internalStartTs, internalEndTs, nThreads); break;
		case BUFFERED: lp = new BufferedLogicalProcess(this, internalStartTs, internalEndTs, nThreads); break;
		case BUNCH: lp = new BunchLogicalProcess(this, internalStartTs, internalEndTs, nThreads); break;
		}
		init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.info.SimulationStartInfo(this, System.currentTimeMillis(), this.internalStartTs));
		
		lp.run();
		
		end();
		infoHandler.notifyInfo(new es.ull.isaatc.simulation.info.SimulationEndInfo(this, System.currentTimeMillis(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

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

	public long simulationTime2Long(Time source) {
		return unit.convert(source);
	}
	
	public Time long2SimulationTime(long sourceValue) {
		return new Time(unit, sourceValue);
	}

	/**
	 * @return the unit
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}

	/**
	 * Adds a generator to the simulation. The generators are automatically
	 * added from their constructor.
	 * 
	 * @param gen
	 *            Generator.
	 */
	protected void add(Generator gen) {
		generatorList.add(gen);
	}

	public ArrayList<Generator> getGeneratorList() {
		return generatorList;
	}

	/**
	 * @return the nThreads
	 */
	public int getNThreads() {
		return nThreads;
	}

	/**
	 * @param threads the nThreads to set
	 */
	public void setNThreads(int threads) {
		nThreads = threads;
	}
	
	/**
	 * Returns the logical process that can be used as a default LP.
	 * <p>
	 * The default LP is useful for any simulation object which don't have a
	 * direct relation to an activity or resource type.
	 * 
	 * @return This simulation's default logical process.
	 */
	public LogicalProcess getDefaultLogicalProcess() {
		return lp;
	}

	/**
	 * Returns the simulation end timestamp.
	 * 
	 * @return Value of property endTs.
	 */
	public long getInternalEndTs() {
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
	 * @param endTs the endTs to set
	 */
	public void setEndTs(Time endTs) {
		this.endTs = endTs;
		this.internalEndTs = simulationTime2Long(endTs);
	}

	/**
	 * Returns the simulation start timestamp.
	 * 
	 * @return Returns the startTs.
	 */
	public long getInternalStartTs() {
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
	 * @param startTs the startTs to set
	 */
	public void setStartTs(Time startTs) {
		this.startTs = startTs;
		this.internalStartTs = simulationTime2Long(startTs);
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
	public void init(){}
	
	/**
	 * Event which is activated after the simulation finish.
	 */
	public void end(){}
	
	/**
	 * Event which is activated before a clock change.
	 */
	public void beforeClockTick(){}
	
	/**
	 * Event which is activated after a clock change.
	 */
	public void afterClockTick(){}
	
	public void addInfoReciever(InfoReceiver reciever) {
		infoHandler.registerRecievers(reciever);
	}

	public InfoHandler getInfoHandler() {
		return infoHandler;
	}

}
