/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.state.*;
import es.ull.isaatc.util.*;

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
public abstract class Simulation implements RecoverableState<SimulationState> {
	/** A short text describing this simulation. */
	String description;

	/** List of resources present in the simulation. */
	protected TreeMap<Integer, Resource> resourceList;

	/** List of element generators of the simulation. */
	protected ArrayList<Generator> generatorList;

	/** List of activities present in the simulation. */
	protected TreeMap<Integer, Activity> activityList;

	/** List of resource types present in the simulation. */
	protected TreeMap<Integer, ResourceType> resourceTypeList;

	/** List of resource types present in the simulation. */
	protected TreeMap<Integer, ElementType> elementTypeList;

	/** List of activity managers that partition the simulation. */
	protected ArrayList<ActivityManager> activityManagerList;

	/** Logical Process list */
	protected LogicalProcess[] logicalProcessList;

	/** Timestamp of simulation's start */
	protected double startTs;

	/** Timestamp of Simulation's end */
	protected double endTs;

	/** Output for printing messages */
	protected Output out;

	/** End-of-simulation control */
	private CountDownLatch endSignal;

	/** List of info listeners */
	private ArrayList<SimulationListener> listeners;

	/** List of active elements */
	private TreeMap<Integer, Element> activeElementList;

	/**
	 * Creates a new instance of Simulation
	 * 
	 * @param description
	 *            A short text describing this simulation.
	 * @param startTs
	 *            Timestamp of simulation's start.
	 * @param endTs
	 *            Timestamp of Simulation's end.
	 * @param out
	 *            Output for printing debug messages.
	 */
	public Simulation(String description, double startTs, double endTs,
			Output out) {
		activityList = new TreeMap<Integer, Activity>();
		resourceTypeList = new TreeMap<Integer, ResourceType>();
		elementTypeList = new TreeMap<Integer, ElementType>();
		activityManagerList = new ArrayList<ActivityManager>();
		resourceList = new TreeMap<Integer, Resource>();
		generatorList = new ArrayList<Generator>();

		this.description = description;
		this.startTs = startTs;
		this.endTs = endTs;
		this.out = out;
		// MOD 29/06/06
		listeners = new ArrayList<SimulationListener>();
		activeElementList = new TreeMap<Integer, Element>();
	}

	/**
	 * Creates a new instance of Simulation
	 * 
	 * @param description
	 *            A short text describing this simulation.
	 * @param startTs
	 *            Timestamp of simulation's start.
	 * @param endTs
	 *            Timestamp of Simulation's end.
	 */
	public Simulation(String description, double startTs, double endTs) {
		this(description, startTs, endTs, new Output());
	}

	/**
	 * Simulation initialization. It creates and starts all the necessary
	 * structures.
	 * <p>
	 * If a state is indicated, sets the state of this simulation.
	 * 
	 * @param state
	 *            A previous stored state. <code>null</code> if no previous
	 *            state is going to be used.
	 */
	protected void init(SimulationState state) {
		createModel();
		debug("SIMULATION MODEL CREATED");
		createActivityManagers();
		debugPrintActManager();
		createLogicalProcesses();
		if (state != null) {
			setState(state);
			// Elements from a previous simulation don't need to be started, but
			// they need a default LP
			for (Element elem : activeElementList.values())
				if (elem.getDefLP() == null)
					elem.setDefLP(getDefaultLogicalProcess());
		}
		notifyListeners(new SimulationStartInfo(this, System
				.currentTimeMillis(), Generator.getElemCounter()));
		// FIXME: Debería hacer un reparto más inteligente tanto de generadores
		// como de recursos
		// Starts all the generators
		for (Generator gen : generatorList)
			gen.start(getDefaultLogicalProcess());
		// Starts all the resources
		for (Resource res : resourceList.values())
			res.start(getDefaultLogicalProcess());
	}

	/**
	 * Listener adapter. Adds a new listener to the listener list.
	 * 
	 * @param listener
	 *            A simulation's listener
	 */
	public void addListener(SimulationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(SimulationObjectInfo info) {
		for (SimulationListener il : listeners)
			il.infoEmited(info);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(SimulationStartInfo info) {
		for (SimulationListener il : listeners)
			il.infoEmited(info);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(SimulationEndInfo info) {
		for (SimulationListener il : listeners)
			il.infoEmited(info);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(TimeChangeInfo info) {
		for (SimulationListener il : listeners)
			il.infoEmited(info);
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
	
	/**
	 * Starts the simulation execution. Initializes all the structures, and
	 * starts the logical processes. This method blocks until all the logical
	 * processes have finished their execution.
	 * 
	 * @param state
	 *            A previously stored state of the simulation.
	 */
	public void start(SimulationState state) {
		init(state);
		// The barrier is set to the amount of LPs
		endSignal = new CountDownLatch(logicalProcessList.length);
		for (int i = 0; i < logicalProcessList.length; i++)
			logicalProcessList[i].start();
		try {
			// ... and now the simulation is wating for all the LPs to finish
			endSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		debug("SIMULATION COMPLETELY FINISHED");
		notifyListeners(new SimulationEndInfo(this, System.currentTimeMillis(),
				Generator.getElemCounter()));
	}

	/**
	 * Starts the simulation execution. Initializes all the structures, and
	 * starts the logical processes. This method blocks until all the logical
	 * processes have finished their execution.
	 * <p>
	 * This method is invoked when there isn't a previous state to restore.
	 */
	public void start() {
		start(null);
	}

	/**
	 * Adds an identified object to the model. The allowed id. objects are:
	 * {@link Activity}, {@link ResourceType}, {@link ElementType}. Any other
	 * object is ignored. These objects are automatically added from their
	 * constructor.
	 * 
	 * @param obj
	 *            Identified object that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected DescSimulationObject add(DescSimulationObject obj) {
		DescSimulationObject resul = null;
		if (obj instanceof ResourceType)
			resul = resourceTypeList.put(obj.getIdentifier(), (ResourceType) obj);
		else if (obj instanceof Activity)
			resul = activityList.put(obj.getIdentifier(), (Activity) obj);
		else if (obj instanceof ElementType)
			resul = elementTypeList.put(obj.getIdentifier(), (ElementType) obj);
		else
			error("Trying to add an unidentified object to the Model");
		return resul;
	}

	/**
	 * Adds an activity manager to the simulation. The activity managers are
	 * automatically added from their constructor.
	 * 
	 * @param am
	 *            Activity manager.
	 */
	protected void add(ActivityManager am) {
		activityManagerList.add(am);
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

	/**
	 * Adds a resoruce to the simulation. The resources are automatically added
	 * from their constructor.
	 * 
	 * @param res
	 *            Resource.
	 */
	protected void add(Resource res) {
		resourceList.put(res.getIdentifier(), res);
	}

	/**
	 * Returns a list of the resources of the model.
	 * 
	 * @return Resources of the model.
	 */
	public TreeMap<Integer, Resource> getResourceList() {
		return resourceList;
	}

	/**
	 * Returns a list of the activities of the model.
	 * 
	 * @return Activities of the model.
	 */
	public TreeMap<Integer, Activity> getActivityList() {
		return activityList;
	}

	/**
	 * Returns the activity with the corresponding identifier.
	 * 
	 * @param id
	 *            Activity identifier.
	 * @return An activity with the indicated identifier.
	 */
	public Activity getActivity(int id) {
		return activityList.get(new Integer(id));
	}

	/**
	 * Returns a list of the resource types of the model.
	 * 
	 * @return Resource types of the model.
	 */
	public TreeMap<Integer, ResourceType> getResourceTypeList() {
		return resourceTypeList;
	}

	/**
	 * Returns the resource type with the corresponding identifier.
	 * 
	 * @param id
	 *            Resource type identifier.
	 * @return A resource type with the indicated identifier.
	 */
	public ResourceType getResourceType(int id) {
		return resourceTypeList.get(new Integer(id));
	}

	/**
	 * Returns the element type with the corresponding identifier.
	 * 
	 * @param id
	 *            element type identifier.
	 * @return An element type with the indicated identifier.
	 */
	public ElementType getElementType(int id) {
		return elementTypeList.get(new Integer(id));
	}

	/**
	 * Returns a list of the element types of the model.
	 * 
	 * @return element types of the model.
	 */
	public TreeMap<Integer, ElementType> getElementTypeList() {
		return elementTypeList;
	}

	/**
	 * Returns a list of the activity managers of the model.
	 * 
	 * @return Work activity managers of the model.
	 */
	public ArrayList<ActivityManager> getActivityManagerList() {
		return activityManagerList;
	}

	/**
	 * Returns the logical process at the specified position.
	 * 
	 * @param ind
	 *            The position of the logical process
	 * @return The logical process at the specified position.
	 */
	public LogicalProcess getLogicalProcess(int ind) {
		return logicalProcessList[ind];
	}

	/**
	 * Number of Logical processes that this simulation contains.
	 * 
	 * @return The size of the LPs list.
	 */
	public int getLPSize() {
		return logicalProcessList.length;
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
		return logicalProcessList[logicalProcessList.length - 1];
	}

	/**
	 * Adds an element when it starts its execution.
	 * 
	 * @param elem
	 *            An element that starts its execution.
	 */
	public synchronized void addActiveElement(Element elem) {
		activeElementList.put(elem.getIdentifier(), elem);
	}

	/**
	 * Removes an element when it finishes its execution.
	 * 
	 * @param elem
	 *            An element that finishes its execution.
	 */
	public synchronized void removeActiveElement(Element elem) {
		activeElementList.remove(elem.getIdentifier());
	}

	/**
	 * Returns the element with the specified identifier.
	 * 
	 * @param id
	 *            The element's identifier.
	 * @return The element with the specified identifier.
	 */
	public Element getActiveElement(int id) {
		return activeElementList.get(new Integer(id));
	}

	/**
	 * Returns the simulation end timestamp.
	 * 
	 * @return Value of property endTs.
	 */
	public double getEndTs() {
		return endTs;
	}

	/**
	 * Returns the simulation start timestamp.
	 * 
	 * @return Returns the startTs.
	 */
	public double getStartTs() {
		return startTs;
	}

	/**
	 * Notifies the end of a logical process.
	 */
	protected void notifyEnd() {
		endSignal.countDown();
	}

	@Override
	public String toString() {
		return description;
	}

	/**
	 * Prints a debug message. Uses the same text for short and long
	 * description.
	 * 
	 * @param description
	 *            The content of the message
	 */
	public void debug(String description) {
		out.debug(description);
	}

	/**
	 * Prints an error message.
	 * 
	 * @param description
	 *            The content of the message
	 */
	public void error(String description) {
		out.error(description);
	}

	/**
	 * @return True if the debug mode is activated
	 */
	public boolean isDebugEnabled() {
		return out.isDebugEnabled();
	}
	
	/**
	 * Returns the state of this simulation. The state of a simulation consists
	 * on the state of its logical processes, elements and resources.
	 * 
	 * @return The state of this simulation.
	 */
	public SimulationState getState() {
		SimulationState simState = new SimulationState(Generator
				.getElemCounter(), SingleFlow.getCounter(), endTs);
		for (LogicalProcess lp : logicalProcessList) {
			for (BasicElement.DiscreteEvent ev : lp.waitQueue) {
				if (ev instanceof Element.FinalizeActivityEvent) {
					Element.FinalizeActivityEvent fev = (Element.FinalizeActivityEvent) ev;
					simState.add(SimulationState.EventType.FINALIZEACT, fev
							.getElement().getIdentifier(), fev.getTs(), fev
							.getFlow().getIdentifier());
				} else if (ev instanceof Resource.RoleOffEvent) {
					Resource.RoleOffEvent rev = (Resource.RoleOffEvent) ev;
					simState.add(SimulationState.EventType.ROLOFF, rev
							.getElement().getIdentifier(), rev.getTs(), rev
							.getRole().getIdentifier());
				}
			}
		}
		for (Activity act : activityList.values())
			simState.add(act.getState());
		for (ResourceType rt : resourceTypeList.values())
			simState.add(rt.getState());
		for (Element elem : activeElementList.values())
			simState.add(elem.getState());
		for (Resource res : resourceList.values())
			simState.add(res.getState());
		return simState;
	}

	/**
	 * Fills up the simulation with data from a previous simulation. The model
	 * is supposed to be previously created.
	 * 
	 * @param state
	 *            Previous simulation data
	 */
	public void setState(SimulationState state) {
		// FIXME: ¿Debería hacer startTs = state.getEndTs()?
		// Elements.
		for (ElementState eState : state.getElemStates()) {
			Element elem = new Element(eState.getElemId(), this,
					elementTypeList.get(new Integer(eState.getElemTypeId())));
			elem.setState(eState);
			activeElementList.put(elem.getIdentifier(), elem);
		}
		// Single flow's counter. This value is established here because the
		// element's state set
		// modifies its value.
		SingleFlow.setCounter(state.getLastSFId());
		// Resources
		for (ResourceState rState : state.getResStates())
			resourceList.get(new Integer(rState.getResId())).setState(rState);

		// Activities
		for (ActivityState aState : state.getAStates()) {
			Activity act = getActivity(aState.getActId());
			act.setState(aState);
		}

		// Resource Types
		for (ResourceTypeState rtState : state.getRtStates()) {
			ResourceType rt = getResourceType(rtState.getRtId());
			rt.setState(rtState);
		}

		// Creates a null cycle to non-iterative cycles
		PeriodicCycle c = new PeriodicCycle(0.0, new Fixed(1), -1.0);
		// Events
		for (SimulationState.EventEntry entry : state.getWaitQueue()) {
			if (entry.getType() == SimulationState.EventType.FINALIZEACT) {
				Element elem = getActiveElement(entry.getId());
				// The element has not been started yet, so it hasn't got a
				// default logical process...
				elem.setDefLP(getDefaultLogicalProcess());
				// ... however, the event starts immediately
				elem.addEvent(elem.new FinalizeActivityEvent(entry.getTs(),
						elem.searchSingleFlow(entry.getValue())));
			} else if (entry.getType() == SimulationState.EventType.ROLOFF) {
				Resource res = resourceList.get(new Integer(entry.getId()));
				// The resource has not been started yet, so it hasn't got a
				// default logical process
				res.setDefLP(getDefaultLogicalProcess());
				// ... however, the event starts immediately
				res.addEvent(res.new RoleOffEvent(entry.getTs(),
						getResourceType(entry.getValue()),
						c.iterator(0.0, 0.0), 0.0));
			}
		}

		// Element's counter of the generators
		Generator.setElemCounter(state.getLastElemId());
	}

	/**
	 * Prints a graph, where the resource types are nodes and the activities are
	 * the links.
	 * 
	 * @param graph
	 *            The graph to print.
	 */
	protected void debugPrintGraph(HashSet[] graph) {
		if (isDebugEnabled()) {
			StringBuffer str = new StringBuffer();
			// Pinto el graph para chequeo
			for (int i = 0; i < resourceTypeList.size(); i++) {
				ResourceType rt = resourceTypeList.get(i);
				str.append("Resource Type (" + i + "): " + rt.getDescription()
						+ "\r\n");
				str.append("\tNeighbours: ");
				Iterator it = graph[i].iterator();
				while (it.hasNext()) {
					Integer nodo = (Integer) it.next();
					str.append(nodo + "\t");
				}
				str.append("\r\n");
			}
			debug("Graph created\r\n" + str.toString());
		}
	}

	/**
	 * Prints the contents of the activity managers created.
	 */
	protected void debugPrintActManager() {
		if (isDebugEnabled()) {
			StringBuffer str1 = new StringBuffer("Activity Managers:\r\n");
			for (ActivityManager am : activityManagerList)
				str1.append(am.getDescription() + "\r\n");
			debug(str1.toString());
		}
	}
}
