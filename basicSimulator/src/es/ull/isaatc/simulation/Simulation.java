/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.state.ActivityState;
import es.ull.isaatc.simulation.state.ElementState;
import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.simulation.state.ResourceState;
import es.ull.isaatc.simulation.state.ResourceTypeState;
import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

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
public abstract class Simulation implements RecoverableState<SimulationState>, Describable, Callable<SimulationState>, Runnable {
	/** Simulation's identifier */
	protected int id;
	
	/** A short text describing this simulation. */
	protected String description;

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
	protected double internalStartTs;

	/** Timestamp of Simulation's end */
	protected double internalEndTs;

	/** Timestamp of simulation's start */
	protected SimulationTime startTs;

	/** Timestamp of Simulation's end */
	protected SimulationTime endTs;

	/** A previous stored state. */
	protected SimulationState previousState = null;
	
	/** Output for printing messages */
	protected Output out = null;

	/** End-of-simulation control */
	private CountDownLatch endSignal;

	/** List of active elements */
	private Map<Integer, Element> activeElementList;

	/** List of info listeners */
	private ListenerController listenerController = null;
	
	protected SimulationTimeUnit unit = null;
	
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
	 */
	public Simulation(int id, String description, SimulationTimeUnit unit) {
		activityList = new TreeMap<Integer, Activity>();
		resourceTypeList = new TreeMap<Integer, ResourceType>();
		elementTypeList = new TreeMap<Integer, ElementType>();
		activityManagerList = new ArrayList<ActivityManager>();
		resourceList = new TreeMap<Integer, Resource>();
		generatorList = new ArrayList<Generator>();
		activeElementList = Collections.synchronizedMap(new TreeMap<Integer, Element>());
		
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
	 * Creates a new instance of Simulation which continues a previous state
	 * 
	 * @param id
	 *            This simulation's identifier
	 * @param description
	 *            A short text describing this simulation.
	 * @param previousState
	 *            A previous stored state
	 * @param endTs
	 *            Timestamp of simulation's end
	 */
	public Simulation(int id, String description, SimulationTimeUnit unit, SimulationState previousState, SimulationTime endTs) {
		this(id, description, unit, previousState.getEndTs(), endTs);
		this.previousState = previousState;
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
		createActivityManagers();
		debugPrintActManager();
		createLogicalProcesses();
		
		if (previousState != null) {
			setState(previousState);
			// Elements from a previous simulation don't need to be started, but
			// they need a default LP
			synchronized(activeElementList) {
				for (Element elem : activeElementList.values())
					if (elem.getDefLP() == null)
						elem.setDefLP(getDefaultLogicalProcess());
			}
		}
		// Starts the listener controller so it can receive info events. 
		listenerController.start();
		
		getListenerController().notifyListeners(new SimulationStartInfo(this, System
				.currentTimeMillis(), Generator.getElemCounter()));
		
		// FIXME: Debería hacer un reparto más inteligente tanto de generadores
		// como de recursos
		// Starts all the generators
		for (Generator gen : generatorList)
			gen.start(getDefaultLogicalProcess());
		// Starts all the resources
		for (Resource res : resourceList.values())
			res.start(getDefaultLogicalProcess());		
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
		getListenerController().notifyListeners(new SimulationEndInfo(this, System.currentTimeMillis(),
				Generator.getElemCounter()));
	}

	public SimulationState call() {
		run();
		return getState();
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
	 * Adds an {@link es.ull.isaatc.simulation.Activity} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param act
	 *            Activity that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected Activity add(Activity act) {
		return activityList.put(act.getIdentifier(), act);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.ElementType} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param et
	 *            Element Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ElementType add(ElementType et) {
		return elementTypeList.put(et.getIdentifier(), et);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.ResourceType} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param rt
	 *            Resource Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ResourceType add(ResourceType rt) {
		return resourceTypeList.put(rt.getIdentifier(), rt);
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
	 * Returns a list of the resource types of the model.
	 * 
	 * @return Resource types of the model.
	 */
	public TreeMap<Integer, ResourceType> getResourceTypeList() {
		return resourceTypeList;
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
	 * Returns the activity with the corresponding identifier.
	 * 
	 * @param id
	 *            Activity identifier.
	 * @return An activity with the indicated identifier.
	 */
	public Activity getActivity(int id) {
		return activityList.get(id);
	}

	/**
	 * Returns the resource type with the corresponding identifier.
	 * 
	 * @param id
	 *            Resource type identifier.
	 * @return A resource type with the indicated identifier.
	 */
	public ResourceType getResourceType(int id) {
		return resourceTypeList.get(id);
	}

	/**
	 * Returns the element type with the corresponding identifier.
	 * 
	 * @param id
	 *            element type identifier.
	 * @return An element type with the indicated identifier.
	 */
	public ElementType getElementType(int id) {
		return elementTypeList.get(id);
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
	public void addActiveElement(Element elem) {
		activeElementList.put(elem.getIdentifier(), elem);
	}

	/**
	 * Removes an element when it finishes its execution.
	 * 
	 * @param elem
	 *            An element that finishes its execution.
	 */
	public void removeActiveElement(Element elem) {
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
		return activeElementList.get(id);
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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param out the out to set
	 */
	public void setOutput(Output out) {
		this.out = out;
	}

	/**
	 * @return the listenerController
	 */
	public ListenerController getListenerController() {
		return listenerController;
	}

	/**
	 * Sets a listener controller for this simulation
	 * @param listenerController the listenerController to set
	 */
	public void setListenerController(ListenerController listenerController) {
		this.listenerController = listenerController;
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
			for (ActivityManager am : lp.managerList) {
				Iterator<SingleFlow> iter = am.getQueueIterator();
				while (iter.hasNext()) {
					SingleFlow sf = iter.next();
					simState.add(sf.getIdentifier(), sf.getElement().getIdentifier(), sf.getArrivalTs(), sf.getArrivalOrder());
				}
				
			}
//				for (SingleFlow sf : am.getQueue())
//					simState.add(sf.getIdentifier(), sf.getElement().getIdentifier(), sf.getArrivalTs(), sf.getArrivalOrder());
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
		// Elements.
		for (ElementState eState : state.getElemStates()) {
			Element elem = new Element(eState.getElemId(), this,
					elementTypeList.get(new Integer(eState.getElemTypeId())));
			elem.setState(eState);
			activeElementList.put(elem.getIdentifier(), elem);
		}
		// Single flow's counter. This value is established here because the
		// element's state set modifies its value.
		SingleFlow.setCounter(state.getLastSFId());
		// Resources
		for (ResourceState rState : state.getResStates())
			resourceList.get(new Integer(rState.getResId())).setState(rState);

		// Waiting single flows. The queue was stored in order, so when building
		// the new queues the order is preserved.
		for (SimulationState.SFEntry sfe : state.getSfQueue()) {
			Element elem = getActiveElement(sfe.getElemId());
			SingleFlow sf = elem.searchSingleFlow(sfe.getFlowId());
			sf.getActivity().queueAdd(sf);
		}
		
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
		PeriodicCycle c = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1), -1.0);
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
