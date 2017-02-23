/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import es.ull.iis.simulation.core.VariableStore;
import es.ull.iis.simulation.inforeceiver.InfoHandler;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.SimulationInfoHandler;
import es.ull.iis.simulation.model.flow.Flow;
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
public class Model implements Callable<Integer>, Runnable, Describable, VariableStore {
	/** A short text describing this simulation. */
	protected final String description;
	/** Simulation time unit */
	protected final TimeUnit unit;

	private final static TimeUnit defTimeUnit = TimeUnit.MINUTE; 
	private int elemCounter = 0;
//	private final ArrayList<EventSource> eventSourceList = new ArrayList<EventSource>();
	private final ArrayList<ElementType> elementTypeList = new ArrayList<ElementType>();
	private final ArrayList<Resource> resourceList = new ArrayList<Resource>();
	private final ArrayList<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
	private final ArrayList<WorkGroup> workGroupList = new ArrayList<WorkGroup>();
	private final ArrayList<Flow> flowList = new ArrayList<Flow>();
//	private final ArrayList<Element> elemList = new ArrayList<Element>();
	private final ArrayList<ElementGenerator> genList = new ArrayList<ElementGenerator>();

	/** Output for printing debug and error messages */
	protected static Output out = new Output();
	
    /** Variable store */
	protected final Map<String, Variable> varCollection = new TreeMap<String, Variable>();
	
	/** A handler for the information produced by the execution of this simulation */
	protected final SimulationInfoHandler infoHandler = new SimulationInfoHandler();
	
	/** The simulation engine that executes this model */
	protected SimulationEngine simulationEngine = null;
	
	/**
	 * 
	 */
	public Model(String description) {
		this(description, defTimeUnit);
	}

	public Model(String description, TimeUnit unit) {
		this.unit = unit;
		this.description = description;
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
	}

	/**
	 * Sets an output for debugging and error messages.
	 * @param out The new output for debugging and error messages
	 */
	public static void setOutput(Output out) {
		Model.out = out;
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
		if (simulationEngine == null) {
			out.error("Simulation engine required to execute model. " +
					"Please use the setSimulationEngine() method before invoking run()"); 
		}
		else {
			simulationEngine.initializeEngine();
			init();
	
			infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationStartInfo(simulationEngine, System.nanoTime(), simulationEngine.getInternalStartTs()));
			
			simulationEngine.launchInitialEvents();
			simulationEngine.simulationLoop();

			debug("SIMULATION TIME FINISHES\r\nSimulation time = "
	            	+ simulationEngine.getTs() + "\r\nPreviewed simulation time = " 
	    			+ simulationEngine.getInternalEndTs());
	    	simulationEngine.printState();
	    	
	        // The user defined method for finalization is invoked
			end();
			
			infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationEndInfo(simulationEngine, System.nanoTime(), simulationEngine.getInternalEndTs()));
			debug("SIMULATION COMPLETELY FINISHED");
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
	public void add(Flow f) { 
		flowList.add(f);
	}
	public void add(ElementGenerator gen) {
	genList.add(gen);
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
	public List<Flow> getFlowList() { 
		return flowList;
	}
	public List<ElementGenerator> getElementGeneratorList() {
		return genList;
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
