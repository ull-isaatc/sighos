/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.threaded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.inforeceiver.InfoHandler;
import es.ull.isaatc.simulation.inforeceiver.InfoReceiver;
import es.ull.isaatc.simulation.threaded.flow.Flow;
import es.ull.isaatc.simulation.threaded.inforeceiver.SimulationInfoHandler;
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
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class Simulation extends es.ull.isaatc.simulation.Simulation {
	/** List of resources present in the simulation. */
	protected final TreeMap<Integer, Resource> resourceList = new TreeMap<Integer, Resource>();

	/** List of element generators of the simulation. */
	protected final ArrayList<Generator> generatorList = new ArrayList<Generator>();

	/** List of activities present in the simulation. */
	protected final TreeMap<Integer, Activity> activityList = new TreeMap<Integer, Activity>();

	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ResourceType> resourceTypeList = new TreeMap<Integer, ResourceType>();

	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ElementType> elementTypeList = new TreeMap<Integer, ElementType>();

	/** List of activity managers that partition the simulation. */
	protected final ArrayList<ActivityManager> activityManagerList = new ArrayList<ActivityManager>();
	
	/** List of flows present in the simulation */
	protected final TreeMap<Integer, Flow> flowList = new TreeMap<Integer, Flow>();
	
	/** Logical Process list */
	protected LogicalProcess[] logicalProcessList;

	/** End-of-simulation control */
	private CountDownLatch endSignal;

	/** List of active elements */
	private final Map<Integer, Element> activeElementList = Collections.synchronizedMap(new TreeMap<Integer, Element>());
	
	private final SimulationInfoHandler infoHandler = new SimulationInfoHandler();

	/** Number of threads which handle events in the logical processes */
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
		super(id, description, unit);
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
		super(id, description, unit, startTs, endTs);
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
	public Simulation(int id, String description, SimulationTimeUnit unit, double startTs, double endTs) {
		super(id, description, unit, startTs, endTs);
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
	public void run() {
		if (out == null)
			out = new Output();
		
		createModel();
		debug("SIMULATION MODEL CREATED");
		createActivityManagers();
		debugPrintActManager();
		createLogicalProcesses();
		init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.info.SimulationStartInfo(this, System.currentTimeMillis(), this.internalStartTs));
		
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
			// ... and now the simulation is waiting for all the LPs to finish
			endSignal.await();
			end();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		infoHandler.notifyInfo(new es.ull.isaatc.simulation.info.SimulationEndInfo(this, System.currentTimeMillis(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

	/**
	 * Adds an {@link es.ull.isaatc.simulation.threaded.Activity} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.threaded.ElementType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.threaded.ResourceType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.threaded.flow.Flow} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param f
	 *            Flow that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	public Flow add(Flow f) {
		return flowList.put(f.getIdentifier(), f);
		
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
	 * Returns a list of the flows of the model.
	 * 
	 * @return flows of the model.
	 */
	public TreeMap<Integer, Flow> getFlowList() {
		return flowList;
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
	 * Returns the flow with the corresponding identifier.
	 * 
	 * @param id
	 *            flow identifier.
	 * @return A flow with the indicated identifier.
	 */
	public Flow getFlow(int id) {
		return flowList.get(id);
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
	 * Notifies the end of a logical process.
	 */
	protected void notifyEnd() {
		endSignal.countDown();
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

	public Map<Integer, Element> getActiveElementList() {
		return activeElementList;
	};
	
	public void addInfoReciever(InfoReceiver reciever) {
		infoHandler.registerRecievers(reciever);
	}

	public InfoHandler getInfoHandler() {
		return infoHandler;
	}
}
