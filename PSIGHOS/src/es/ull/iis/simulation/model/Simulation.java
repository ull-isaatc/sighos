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
 * @author Ivan Castilla Rodriguez
 *
 */
public class Simulation implements Identifiable, Runnable, Describable, VariableStore {
	/** A short text describing this simulation. */
	protected final String description;
	/** ParallelSimulationEngine time unit */
	protected final TimeUnit unit;
	/** Model identifier */
	protected final int id;
	private final static TimeUnit defTimeUnit = TimeUnit.MINUTE; 
	private int elemCounter = 0;
//	private final ArrayList<EventSource> eventSourceList = new ArrayList<EventSource>();
	private final ArrayList<ElementType> elementTypeList = new ArrayList<ElementType>();
	private final ArrayList<Resource> resourceList = new ArrayList<Resource>();
	private final ArrayList<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
	private final ArrayList<WorkGroup> workGroupList = new ArrayList<WorkGroup>();
	private final ArrayList<BasicFlow> flowList = new ArrayList<BasicFlow>();
	private final ArrayList<RequestResourcesFlow> actList = new ArrayList<RequestResourcesFlow>();
//	private final ArrayList<Element> elemList = new ArrayList<Element>();
	private final ArrayList<TimeDrivenGenerator<?>> tGenList = new ArrayList<TimeDrivenGenerator<?>>();
	private final ArrayList<ConditionDrivenGenerator<?>> cGenList = new ArrayList<ConditionDrivenGenerator<?>>();
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
	 * 
	 */
	public Simulation(int id, String description, long startTs, long endTs) {
		this(id, description, defTimeUnit, startTs, endTs);
	}

	/**
	 * Creates a new instance of a model
	 *
	 * @param description A short text describing this simulation.
	 * @param unit This simulation's time unit
	 * @param startTs Timestamp of simulation's start expressed in ParallelSimulationEngine Time Units
	 * @param endTs Timestamp of simulation's end expressed in ParallelSimulationEngine Time Units
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
	 * @return the defTimeUnit
	 */
	public static TimeUnit getDefTimeUnit() {
		return defTimeUnit;
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
	 * Sets an output for debugging and error messages.
	 * @param out The new output for debugging and error messages
	 */
	public static void setOutput(Output out) {
		Simulation.out = out;
	}

	public static void debug(String description) {
		out.debug(description);
	}

	public static void error(String description) {
		out.error(description);
	}

	public static boolean isDebugEnabled() {
		return out.isDebugEnabled();
	}
	
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
	 * Starts the simulation execution. It creates and starts all the necessary 
	 * structures. This method blocks until all the logical processes have finished 
	 * their execution.<p>
	 * If a state is indicated, sets the state of this simulation.<p>
	 * Checks if a valid output for debug messages has been declared. Note that no 
	 * debug messages can be printed before this method is declared unless <code>setOutput</code>
	 * had been invoked. 
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
			simulationEngine = new SequentialSimulationEngine(id, this);
		}
		simulationEngine.initializeEngine();
		init();

		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationStartInfo(this, System.nanoTime(), startTs));
		
		simulationEngine.launchInitialEvents();
		simulationEngine.simulationLoop();

		debug("SIMULATION TIME FINISHES\r\nSimulation time = "
            	+ simulationEngine.getTs() + "\r\nPreviewed simulation time = " 
    			+ endTs);
    	simulationEngine.printState();
    	
        // The user defined method for finalization is invoked
		end();
		
		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationEndInfo(this, System.nanoTime(), endTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

	/**
	 * Checks the conditions stated int he condition-driven generators. If the condition meets, creates the corresponding event sources.
	 * @param ts ParallelSimulationEngine time when the simulations are checked.
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
	
//	public void add(EventSource ev) { 
//		eventSourceList.add(ev);
//	}
	public void add(ElementType et) { 
		elementTypeList.add(et);
	}
	public void add(Resource res) { 
		resourceList.add(res);
	}
	public void add(ResourceType rt) { 
		resourceTypeList.add(rt);
	}
	public void add(WorkGroup wg) { 
		workGroupList.add(wg);
	}
	public void add(BasicFlow f) { 
		flowList.add(f);
		if (f instanceof RequestResourcesFlow)
			actList.add((RequestResourcesFlow)f);
	}
	public void add(TimeDrivenGenerator<?> gen) {
		tGenList.add(gen);
	}
	public void add(ConditionDrivenGenerator<?> gen) {
		cGenList.add(gen);
	}
	public void add(ActivityManager am) {
		amList.add(am);
	}
//	public void add(Element elem) {
//		elemList.add(elem);
//	}

//	public List<EventSource> getEventSourceList() { 
//		return eventSourceList;
//	}
	public List<ElementType> getElementTypeList() { 
		return elementTypeList;
	}
	public List<Resource> getResourceList() { 
		return resourceList;
	}
	public List<ResourceType> getResourceTypeList() { 
		return resourceTypeList;
	}
	public List<WorkGroup> getWorkGroupList() { 
		return workGroupList;
	}
	public List<BasicFlow> getFlowList() { 
		return flowList;
	}
	public List<RequestResourcesFlow> getActivityList() { 
		return actList;
	}
	public List<TimeDrivenGenerator<?>> getTimeDrivenGeneratorList() {
		return tGenList;
	}
	public List<ConditionDrivenGenerator<?>> getConditionDrivenGeneratorList() {
		return cGenList;
	}
	public List<ActivityManager> getActivityManagerList() {
		return amList;
	}
//	public List<Element> getElementList() {
//	return elemList;
//}
	
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
	 * @param info TODO
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

	// End of user methods
	
	
}
