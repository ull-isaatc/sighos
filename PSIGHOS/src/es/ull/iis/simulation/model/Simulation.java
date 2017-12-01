/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.SimulationInfoHandler;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.BasicFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.sequential.SequentialSimulationEngine;
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
 * The main simulation class. Defines all the components of the model and the logical structures required to simulate them.
 * 
 * @author Ivan Castilla Rodriguez
 *
 */
public class Simulation implements Identifiable, Runnable, Describable, VariableStore {
	/** The default time unit used by the simulation */
	private final static TimeUnit DEF_TIME_UNIT = TimeUnit.MINUTE; 
	/** If true, notifies Activity Managers that an element is available randomly; otherwise, the notification is purely sequential */
	private final static boolean RANDOM_NOTIFY_AMS = false;
	
	/** A short text describing this simulation. */
	protected final String description;
	/** Time unit of the simulation */
	protected final TimeUnit unit;
	/** A unique simulation identifier */
	protected final int id;
	/** The identifier to be assigned to the next element */ 
	private int elemCounter = 0;
	
	/** List of element types present in the simulation. */
	private final ArrayList<ElementType> elementTypeList = new ArrayList<ElementType>();
	/** List of resources present in the simulation. */
	private final ArrayList<Resource> resourceList = new ArrayList<Resource>();
	/** List of resource types present in the simulation. */
	private final ArrayList<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
	/** List of workgroups present in the simulation */
	private final ArrayList<WorkGroup> workGroupList = new ArrayList<WorkGroup>();
	/** List of flows present in the simulation */
	private final ArrayList<BasicFlow> flowList = new ArrayList<BasicFlow>();
	/** List of activities present in the simulation. */
	private final ArrayList<RequestResourcesFlow> actList = new ArrayList<RequestResourcesFlow>();
	/** List of time-driven element generators of the simulation. */
	private final ArrayList<TimeDrivenGenerator<?>> tGenList = new ArrayList<TimeDrivenGenerator<?>>();
	/** List of condition-driven element generators of the simulation. */
	private final ArrayList<ConditionDrivenGenerator<?>> cGenList = new ArrayList<ConditionDrivenGenerator<?>>();
	/** List of activity managers that partition the simulation. */
	private final ArrayList<ActivityManager> amList = new ArrayList<ActivityManager>();

	/** Output for printing debug and error messages */
	protected static Output out = new Output();
	
    /** Variable store */
	protected final Map<String, Variable> varCollection = new TreeMap<String, Variable>();
	
	/** A handler for the information produced by the execution of this simulation */
	protected final SimulationInfoHandler infoHandler = new SimulationInfoHandler();
	
	/** The simulation engine that executes this model */
	protected SimulationEngine simulationEngine = null;
	
	/** The way the activity managers are created */
	protected ActivityManagerCreator amCreator = null;
	
	/** A value representing the simulation's start timestamp without unit */
	protected final long startTs;

	/** A value representing the simulation's end timestamp without unit */
	protected final long endTs;

	/**
	 * Creates a new instance of a simulation using the default time unit.
	 * 
	 * @param id Simulation identifier
	 * @param description A short text describing this simulation.
	 * @param startTs Simulation start expressed in simulation time units
	 * @param endTs Simulation end expressed in simulation time units
	 */
	public Simulation(int id, String description, long startTs, long endTs) {
		this(id, description, DEF_TIME_UNIT, startTs, endTs);
	}

	/**
	 * Creates a new instance of a simulation 
	 *
	 * @param id Simulation identifier
	 * @param description A short text describing this simulation.
	 * @param unit This simulation's time unit
	 * @param startTs Simulation start timestamp
	 * @param endTs Simulation end timestamp
	 */
	public Simulation(int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		this(id, description, unit, unit.convert(startTs), unit.convert(endTs));
	}
	
	/**
	 * Creates a new instance of a simulation
	 *
	 * @param id Simulation identifier
	 * @param description A short text describing this simulation.
	 * @param unit This simulation's time unit
	 * @param startTs Simulation start expressed in simulation time units
	 * @param endTs Simulation end expressed in simulation time units
	 */
	public Simulation(int id, String description, TimeUnit unit, long startTs, long endTs) {
		this.id = id;
		this.unit = unit;
		this.description = description;
		this.startTs = startTs;
		this.endTs = endTs;
	}
	
	@Override
	public int getIdentifier() {
		return id;
	}
	
	/**
	 * Returns the default simulation time unit 
	 * @return the default simulation time unit
	 */
	public static TimeUnit getDefTimeUnit() {
		return DEF_TIME_UNIT;
	}

	/**
	 * Returns this simulation's time unit
	 * @return the unit
	 */
	public TimeUnit getTimeUnit() {
		return unit;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a long value representing the simulation's end timestamp without unit.
	 * @return A long value representing the simulation's end timestamp without unit
	 */
	public long getEndTs() {
		return endTs;
	}

	/**
	 * Returns a long value representing the simulation's start timestamp without unit.
	 * @return A long value representing the simulation's start timestamp without unit
	 */
	public long getStartTs() {
		return startTs;
	}

    /**
     * Returns the current simulation time
     * @return The current simulation time
     */
	public long getTs() {
		return simulationEngine.getTs();
	}
	
	/**
	 * Returns the simulation engine that executes this model
	 * @return The simulation engine that executes this model
	 */
	public SimulationEngine getSimulationEngine() {
		return simulationEngine;
	}

	/**
	 * Sets the simulation engine that executes this model
	 * @param simulationEngine the simulation engine to set
	 */
	public void setSimulationEngine(SimulationEngine simulationEngine) {
		this.simulationEngine = simulationEngine;
		for (ElementType et : elementTypeList)
			et.assignSimulation(simulationEngine);
		for (ResourceType rt : resourceTypeList)
			rt.assignSimulation(simulationEngine);
		for (Resource res : resourceList)
			res.assignSimulation(simulationEngine);
		for (WorkGroup wg : workGroupList)	
			wg.assignSimulation(simulationEngine);
		for (BasicFlow f : flowList)
			f.assignSimulation(simulationEngine);
		for (Generator<?> gen : tGenList)
			gen.assignSimulation(simulationEngine);
		for (ActivityManager am : amList)
			am.assignSimulation(simulationEngine);
	}

	/**
	 * Returns whether {@link ActivityManager Activity managers} are notified randomly when an element is available
	 * @return True is activity managers are notified randomly; false otherwise.
	 */
	public static boolean isRandomNotifyAMs() {
		return RANDOM_NOTIFY_AMS;
	}
	
	/**
	 * Sets an output for debugging and error messages.
	 * @param out The new output for debugging and error messages
	 */
	public static void setOutput(Output out) {
		Simulation.out = out;
	}

	/**
	 * Prints a debug message
	 * @param description Debug message
	 */
	public static void debug(String description) {
		out.debug(description);
	}

	/**
	 * Prints an error message
	 * @param description Error message
	 */
	public static void error(String description) {
		out.error(description);
	}

	/**
	 * Returns true if debug is enable; false otherwise
	 * @return true if debug is enable; false otherwise
	 */
	public static boolean isDebugEnabled() {
		return out.isDebugEnabled();
	}
	
	/**
	 * Returns a unique identifier for a newly created element. 
	 * @return a unique identifier for a newly created element.
	 */
	public int getNewElementId() {
		return elemCounter++;
	}
	
	/**
	 * Starts the simulation execution in a threaded way. Initializes all the structures, and
	 * starts the workers. 
	 */
	public void start() {
		new Thread(this).start();		
	}
	
	/**
	 * Adds a new event to the simulation
	 * @param ev New event
	 */
	public void addEvent(DiscreteEvent ev) {
		simulationEngine.addEvent(ev);
	}
	
	/**
	 * Starts the execution of the simulation. It creates and initializes all the necessary 
	 * structures.<p> The following checks and initializations are performed within this method:
	 * <ol>
	 * <li>If no customized {@link ActivityManagerCreator AM creator} has been defined, the 
	 * {@link StandardActivityManagerCreator default one} is used.</li>
	 * <li>If no customized {@link SimulationEngine simulation engine} has been defined, a
	 * {@link SequentialSimulationEngine sequential engine} is used.</li>
	 * <li>The user defined method {@link #init()} is invoked.</li>
	 * <li>{@link Resource Resources} and {@link Generator generators} are started.</li>
	 * <li>The main simulation loop is run</li>
	 * <li>The user defined method {@link #end()} is invoked.</li>
	 * </ol>
     */ 
	@Override
	public void run() {
		debug("SIMULATION MODEL CREATED");
		// Sets default AM creator
		if (amCreator == null)
			amCreator = new StandardActivityManagerCreator(this);
		amCreator.createActivityManagers();
		debugPrintActManager();					
		// Sets default simulation engine
		if (simulationEngine == null) {
			setSimulationEngine(new SequentialSimulationEngine(id, this));
		}
		simulationEngine.initializeEngine();
		init();

		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationStartInfo(this, System.nanoTime(), startTs));
		
		// Starts all the time driven generators
		for (TimeDrivenGenerator<?> evSource : tGenList)
			simulationEngine.addWait(evSource.onCreate(startTs));
		// Starts all the resources
		for (Resource res : resourceList)
			simulationEngine.addWait(res.onCreate(startTs));

		// Adds the event to control end of simulation
		simulationEngine.addWait(new SimulationEndEvent());
		
		simulationEngine.simulationLoop();

		debug("SIMULATION FINISHES\r\nSimulation time = "
            	+ getTs() + "\r\nPreviewed simulation time = " 
    			+ endTs);
    	simulationEngine.printState();
    	
        // The user defined method for finalization is invoked
		end();
		
		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationEndInfo(this, System.nanoTime(), getTs()));
		debug("SIMULATION COMPLETELY FINISHED");
	}

	/**
	 * Checks the conditions stated in the condition-driven generators. If the condition meets, creates the corresponding event sources.
	 * @param ts simulation time when the simulations are checked.
	 */
	public void checkConditions(long ts) {
		for (ConditionDrivenGenerator<?> gen : cGenList) {
			if (gen.getCondition().check(null))
				gen.create(ts);
		}
	}
	
	/**
	 * Resets variables or contents of the model. It should be invoked by the user when the same model is used for multiple replicas
	 * and contains variables that must be initialized among replicas.
	 */
	public void reset() {		
	}
	
	/**
	 * Adds an {@link ElementType} to the model. This method is invoked from the object's constructor.
	 * @param et Element Type that's added to the model.
	 */
	public void add(ElementType et) { 
		elementTypeList.add(et);
	}
	
	/**
	 * Adds a {@link Resource} to the simulation. This method is invoked from the object's constructor.
	 * @param res Resource that's added to the model.
	 */
	public void add(Resource res) { 
		resourceList.add(res);
	}
	
	/**
	 * Adds an {@link ResourceType} to the model. This method is invoked from the object's constructor.
	 * @param rt Resource Type that's added to the model.
	 */
	public void add(ResourceType rt) { 
		resourceTypeList.add(rt);
	}

	/**
	 * Adds an {@link WorkGroup} to the model. This method is invoked from the object's constructor.
	 * @param wg Workgroup that's added to the model.
	 */
	public void add(WorkGroup wg) { 
		workGroupList.add(wg);
	}
	/**
	 * Adds an {@link BasicFlow} to the model. This method is invoked from the object's constructor.
	 * @param f Flow that's added to the model.
	 */
	public void add(BasicFlow f) { 
		flowList.add(f);
		if (f instanceof RequestResourcesFlow)
			actList.add((RequestResourcesFlow)f);
	}
	
	/**
	 * Adds an {@link TimeDrivenGenerator} to the model. This method is invoked from the object's constructor.
	 * @param gen Time-driven generator that's added to the model.
	 */
	public void add(TimeDrivenGenerator<?> gen) {
		tGenList.add(gen);
	}
	
	/**
	 * Adds an {@link ConditionDrivenGenerator} to the model. This method is invoked from the object's constructor.
	 * @param gen Condition-driven generator that's added to the model.
	 */
	public void add(ConditionDrivenGenerator<?> gen) {
		cGenList.add(gen);
	}
	
	/**
	 * Adds an {@link ActivityManager} to the simulation. The activity managers are  automatically added from their constructor.
	 * @param am Activity manager.
	 */
	public void add(ActivityManager am) {
		amList.add(am);
	}

	/**
	 * Returns the list of {@link ElementType element types} defined within this simulation
	 * @return the list of {@link ElementType element types} defined within this simulation
	 */
	public List<ElementType> getElementTypeList() { 
		return elementTypeList;
	}
	
	/**
	 * Returns the list of {@link Resource resources} defined within this simulation
	 * @return the list of {@link Resource resources} defined within this simulation
	 */
	public List<Resource> getResourceList() { 
		return resourceList;
	}
	
	/**
	 * Returns the list of {@link ResourceType resource types} defined within this simulation
	 * @return the list of {@link ResourceType resource types} defined within this simulation
	 */
	public List<ResourceType> getResourceTypeList() { 
		return resourceTypeList;
	}
	
	/**
	 * Returns the list of {@link WorkGroup workgroups} defined within this simulation
	 * @return the list of {@link WorkGroup workgroups} defined within this simulation
	 */
	public List<WorkGroup> getWorkGroupList() { 
		return workGroupList;
	}
	
	/**
	 * Returns the list of {@link BasicFlow flows} defined within this simulation
	 * @return the list of {@link BasicFlow flows} defined within this simulation
	 */
	public List<BasicFlow> getFlowList() { 
		return flowList;
	}
	
	/**
	 * Returns the list of {@link RequestResourcesFlow flows requesting resources} defined within this simulation
	 * @return the list of {@link RequestResourcesFlow flows requesting resources} defined within this simulation
	 */
	public List<RequestResourcesFlow> getActivityList() { 
		return actList;
	}

	/**
	 * Returns the list of {@link TimeDrivenGenerator time-driven generators} defined within this simulation
	 * @return the list of {@link TimeDrivenGenerator time-driven generators} defined within this simulation
	 */
	public List<TimeDrivenGenerator<?>> getTimeDrivenGeneratorList() {
		return tGenList;
	}

	/**
	 * Returns the list of {@link ConditionDrivenGenerator condition-driven generators} defined within this simulation
	 * @return the list of {@link ConditionDrivenGenerator condition-driven generators} defined within this simulation
	 */
	public List<ConditionDrivenGenerator<?>> getConditionDrivenGeneratorList() {
		return cGenList;
	}

	/**
	 * Returns the list of {@link ActivityManager activity managers} defined within this simulation
	 * @return the list of {@link ActivityManager activity managers} defined within this simulation
	 */
	public List<ActivityManager> getActivityManagerList() {
		return amList;
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
	public String toString() {
		return description;
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
	 * Prints the contents of the activity managers created.
	 */
	protected void debugPrintActManager() {
		if (isDebugEnabled()) {
			StringBuffer str1 = new StringBuffer("Activity Managers:\r\n");
			for (ActivityManager am : amList)
				str1.append(am.getDescription() + "\r\n");
			debug(str1.toString());
		}
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
	 * @param info A piece of information produced by the simulation
	 */
	public Number notifyInfo(SimulationInfo info) {
		return infoHandler.notifyInfo(info);
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

    /**
     * Allows a user for setting a termination condition for the simulation. The default condition will be that
     * the simulation time is equal or higher than the expected simulation end time.
     * @return True if the simulation must finish; false otherwise.
     */
    public boolean isSimulationEnd(long currentTs) {
    	return (currentTs >= endTs);
    }
    
	// End of user methods
	
	/**
	 * A basic event which facilitates the control of the end of the simulation. Scheduling this event
	 * ensures that there's always at least one event in the simulation. 
	 * @author Iván Castilla Rodríguez
	 */
    class SimulationEndEvent extends DiscreteEvent {
    	/**
    	 * Creates a very simple element to control the simulation end.
    	 */
		public SimulationEndEvent() {
			super(Long.MAX_VALUE);
		}

		@Override
		public void event() {
		}

    }

}
