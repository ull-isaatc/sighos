package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.SimulationEngine;

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
public class SequentialSimulationEngine extends es.ull.iis.simulation.model.SimulationEngine {

	/** The identifier to be assigned to the next resource */ 
	protected int nextResourceId = 0;
	/** List of resources present in the simulation. */
	protected final TreeMap<Integer, ResourceEngine> resourceList = new TreeMap<Integer, ResourceEngine>();

	/** List of element generators of the simulation. */
	protected final ArrayList<EventSourceEngine> eventSourceList = new ArrayList<EventSourceEngine>();

	/** The identifier to be assigned to the next activity */ 
	protected int nextActivityId = 1;
	/** List of activities present in the simulation. */
	protected final TreeMap<Integer, RequestResources> reqFlowList = new TreeMap<Integer, RequestResources>();
	/** List of activities present in the simulation. */
	protected final TreeMap<es.ull.iis.simulation.model.flow.RequestResourcesFlow, RequestResources> reqFlowMap = new TreeMap<es.ull.iis.simulation.model.flow.RequestResourcesFlow, RequestResources>();
	/** List of activities present in the simulation. */
	protected final TreeMap<Integer, ReleaseResources> relFlowList = new TreeMap<Integer, ReleaseResources>();
	/** List of activities present in the simulation. */
	protected final TreeMap<es.ull.iis.simulation.model.flow.ReleaseResourcesFlow, ReleaseResources> relFlowMap = new TreeMap<es.ull.iis.simulation.model.flow.ReleaseResourcesFlow, ReleaseResources>();

	/** The identifier to be assigned to the next resource type */ 
	protected int nextResourceTypeId = 0;
	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ResourceTypeEngine> resourceTypeList = new TreeMap<Integer, ResourceTypeEngine>();
	/** List of resource types present in the simulation. */
	protected final TreeMap<es.ull.iis.simulation.model.ResourceType, ResourceTypeEngine> resourceTypeMap = new TreeMap<es.ull.iis.simulation.model.ResourceType, ResourceTypeEngine>();

	/** The identifier to be assigned to the next element type */ 
	protected int nextElementTypeId = 0;
	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ElementType> elementTypeList = new TreeMap<Integer, ElementType>();

	/** List of activity managers that partition the simulation. */
	protected final ArrayList<ActivityManager> activityManagerList = new ArrayList<ActivityManager>();
	
	/** End-of-simulation control */
	private CountDownLatch endSignal;

	/** The identifier to be assigned to the next element */ 
	protected int nextElementId = 0;
	/** List of active elements */
	private final Map<Integer, Element> activeElementList = new TreeMap<Integer, Element>();

	protected ActivityManagerCreator amCreator = null;

    /** Local virtual time. Represents the current simulation time */
	protected long lvt;

	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityQueue<DiscreteEvent> waitQueue = new PriorityQueue<DiscreteEvent>();
	
	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id
	 *            This simulation's identifier
	 * @param startTs
	 *            Simulation's start timestamp expresed in Simulation Time Units
	 * @param endTs
	 *            Simulation's end timestamp expresed in Simulation Time Units
	 */
	public SequentialSimulationEngine(int id, Model model, long startTs, long endTs) {
		super(id, model, startTs, endTs);
	}

//	public EventSource getEventSourceFromModel(es.ull.iis.simulation.model.EventSource evSource) {
//		if (evSource instanceof es.ull.iis.simulation.model.TimeDrivenGenerator) {
//			return new TimeDrivenGenerator(this, (es.ull.iis.simulation.model.TimeDrivenGenerator)evSource);
//		}
//		else if (evSource instanceof es.ull.iis.simulation.model.Element) {
//			final es.ull.iis.simulation.model.Element modelElem = (es.ull.iis.simulation.model.Element) evSource;
//			return new Element(this, modelElem);
//		}
//		return null;
//	}
	
    /**
     * Indicates if the simulation clock has reached the simulation end.
     * @return True if the simulation clock is higher or equal to the simulation end. False in other case.
     */
    public boolean isSimulationEnd() {
        return(lvt >= internalEndTs);
    }

    /**
     * Returns the current simulation time
     * @return The current simulation time
     */
	public long getTs() {
		return lvt;
	}
    
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    protected void execWaitingElements() {
        // Extracts the first event
        if (! waitQueue.isEmpty()) {
            DiscreteEvent e = removeWait();
            // Advances the simulation clock
            model.beforeClockTick();
            lvt = e.getTs();
            model.getInfoHandler().notifyInfo(new TimeChangeInfo(this, lvt));
            model.afterClockTick();
            debug("SIMULATION TIME ADVANCING " + lvt);
            // Events with timestamp greater or equal to the maximum simulation time aren't
            // executed
            if (lvt >= internalEndTs)
                addWait(e);
            else {
            	addExecution(e);
                // Extracts all the events with the same timestamp
                boolean flag = false;
                do {
                    if (! waitQueue.isEmpty()) {
                        e = removeWait();
                        if (e.getTs() == lvt) {
                        	addExecution(e);
                            flag = true;
                        }
                        else {  
                            flag = false;
                            addWait(e);
                        }
                    }
                    else {  // The waiting queue is empty
                        flag = false;
                    }
                } while ( flag );
            }
        }        
    }

    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	public void addWait(DiscreteEvent e) {
		waitQueue.add(e);
	}
	
	public void addExecution(DiscreteEvent e) {
		e.run();
	}
	
    protected DiscreteEvent removeWait() {
        return waitQueue.poll();
    }
    
    /**
     * Removes a specific event from the waiting queue. This function can be used to cancel an event
     * @param e Event to be removed
     * @return True if the queue contained the event; false otherwise
     */
    protected boolean removeWait(DiscreteEvent e) {
        return waitQueue.remove(e);
    }
    
	/**
	 * @return the nextResourceId
	 */
	protected int getNextResourceId() {
		return nextResourceId++;
	}


	/**
	 * @return the nextActivityId
	 */
	protected int getNextActivityId() {
		return nextActivityId++;
	}


	/**
	 * @return the nextResourceTypeId
	 */
	protected int getNextResourceTypeId() {
		return nextResourceTypeId++;
	}


	/**
	 * @return the nextElementTypeId
	 */
	protected int getNextElementTypeId() {
		return nextElementTypeId++;
	}


	/**
	 * @return the nextElementId
	 */
	protected int getNextElementId() {
		return nextElementId++;
	}


	/**
	 * Adds an {@link es.ull.iis.simulation.sequential.BasicStep} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param act
	 *            Activity that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	public RequestResources add(RequestResources act) {
		reqFlowMap.put(act.getModelReqFlow(), act);
		return reqFlowList.put(act.getIdentifier(), act);
	}

	/**
	 * Adds an {@link es.ull.iis.simulation.sequential.BasicStep} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param act
	 *            Activity that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	public ReleaseResources add(ReleaseResources act) {
		relFlowMap.put(act.getModelRelFlow(), act);
		return relFlowList.put(act.getIdentifier(), act);
	}
	
	/**
	 * Adds an {@link es.ull.iis.simulation.sequential.ElementType} to the model. These method
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
	 * Adds an {@link es.ull.iis.simulation.sequential.ResourceTypeEngine} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param rt
	 *            Resource Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ResourceTypeEngine add(ResourceTypeEngine rt) {
		resourceTypeMap.put(rt.getModelRT(), rt);
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
	protected void add(EventSourceEngine gen) {
		eventSourceList.add(gen);
	}

	/**
	 * Adds a resoruce to the simulation. The resources are automatically added
	 * from their constructor.
	 * 
	 * @param res
	 *            Resource.
	 */
	protected void add(ResourceEngine res) {
		resourceList.put(res.getIdentifier(), res);
	}
	
	/**
	 * Returns a list of the resources of the model.
	 * 
	 * @return Resources of the model.
	 */
	public Map<Integer, ResourceEngine> getResourceList() {
		return resourceList;
	}

	/** 	 
	 * Returns a list of the activities of the model. 	 
	 * 
	 *  @return Activities of the model. 	 
	 */ 	
	public Map<Integer, RequestResources> getActivityList() {
		return reqFlowList;
	}

	/**
	 * Returns a list of the resource types of the model.
	 * 
	 * @return Resource types of the model.
	 */
	public Map<Integer, ResourceTypeEngine> getResourceTypeList() {
		return resourceTypeList;
	}
	
	/**
	 * Returns a list of the element types of the model.
	 * 
	 * @return element types of the model.
	 */
	public Map<Integer, ElementType> getElementTypeList() {
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
	 * @param id Activity identifier.
	 * @return An activity with the indicated identifier.
	 */
	public RequestResources getRequestResource(int id) {
		return reqFlowList.get(id);
	}

	/**
	 * Returns the "simulation" resource handler corresponding to the specified "model" resource handler.
	 * @param modelResHandler "Model" resource handler.
	 * @return The "simulation" resource handler corresponding to the specified "model" resource handler.
	 */
	public ReleaseResources getReleaseResource(es.ull.iis.simulation.model.flow.ReleaseResourcesFlow modelRelHandler) {
		return relFlowMap.get(modelRelHandler);
	}

	/**
	 * Returns the activity with the corresponding identifier.
	 * @param id Activity identifier.
	 * @return An activity with the indicated identifier.
	 */
	public ReleaseResources getReleaseResource(int id) {
		return relFlowList.get(id);
	}

	/**
	 * Returns the "simulation" resource handler corresponding to the specified "model" resource handler.
	 * @param modelResHandler "Model" resource handler.
	 * @return The "simulation" resource handler corresponding to the specified "model" resource handler.
	 */
	public RequestResources getRequestResource(es.ull.iis.simulation.model.flow.RequestResourcesFlow modelResHandler) {
		return reqFlowMap.get(modelResHandler);
	}

	/**
	 * Returns the resource with the corresponding identifier.
	 * @param id Resource identifier.
	 * @return A resource with the indicated identifier.
	 */
	public ResourceEngine getResource(int id) {
		return resourceList.get(id);
	}

	/**
	 * Returns the resource type with the corresponding identifier.
	 * @param id Resource type identifier.
	 * @return A resource type with the indicated identifier.
	 */
	public ResourceTypeEngine getResourceType(int id) {
		return resourceTypeList.get(id);
	}

	/**
	 * Returns the "simulation" resource type corresponding to a "model" resource type.
	 * @param modelRT Resource type.
	 * @return A resource type with the indicated identifier.
	 */
	public ResourceTypeEngine getResourceType(es.ull.iis.simulation.model.ResourceType modelRT) {
		return resourceTypeMap.get(modelRT);
	}

	/**
	 * Returns the element type with the corresponding identifier.
	 * @param id Element type identifier.
	 * @return An element type with the indicated identifier.
	 */
	public ElementType getElementType(int id) {
		return elementTypeList.get(id);
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

	/**
	 * Prints the current state of the simulation for debug purposes. Prints the current local 
	 * time, the contents of the future event list and the execution queue. 
	 */
	protected void printState() {
		if (isDebugEnabled()) {
			StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
			strLong.append("LVT: " + lvt + "\r\n");
	        strLong.append(waitQueue.size() + " waiting elements: ");
	        for (DiscreteEvent e : waitQueue)
	            strLong.append(e + " ");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	public Map<Integer, Element> getActiveElementList() {
		return activeElementList;
	}
	
	/**
	 * A basic element which facilitates the control of the end of the simulation. It simply
	 * schedules an event at <code>endTs</code>, so there's always at least one event in 
	 * the simulation. 
	 * @author Iván Castilla Rodríguez
	 */
    class SimulationElement extends EventSource {

    	/**
    	 * Creates a very simple element to control the simulation end.
    	 */
		public SimulationElement() {
			super(SequentialSimulationEngine.this.model, 0, "SE");
		}

		@Override
		public DiscreteEvent onCreate(long ts) {
			return new EventSource.DefaultStartEvent(ts);
		}

		@Override
		public DiscreteEvent onDestroy() {
			return new EventSource.DefaultFinalizeEvent();
		}

		@Override
		protected void assignSimulation(SimulationEngine simul) {
			// TODO
		}
    }

	@Override
	public void initializeEngine() {
		// Sets default AM creator
		if (amCreator == null)
			amCreator = new StandardActivityManagerCreator(this);
		amCreator.createActivityManagers();
		debugPrintActManager();		
	}

	@Override
	public es.ull.iis.simulation.model.ResourceTypeEngine getResourceTypeEngineInstance(ResourceType modelRT) {
		return new ResourceTypeEngine(this, modelRT);
	}

	@Override
	public es.ull.iis.simulation.model.ResourceList getResourceListInstance(ResourceType modelRT) {
		return new ResourceList();
	}

	@Override
	public es.ull.iis.simulation.model.ResourceEngine getResourceEngineInstance(Resource modelRes) {
		return new ResourceEngine(this, modelRes);
	}

	@Override
	public es.ull.iis.simulation.model.ActivityWorkGroupEngine getActivityWorkGroupEngineInstance(ActivityWorkGroup modelWG) {
		return new ActivityWorkGroupEngine(this, modelWG);
	}

	@Override
	protected void launchInitialEvents() {
		// Starts all the generators
		for (EventSourceEngine evSource : eventSourceList)
			addWait(evSource.onCreate(internalStartTs));
		// Starts all the resources
		for (ResourceEngine res : resourceList.values())
			addWait(res.onCreate(internalStartTs));

		// Adds the event to control end of simulation
		addWait(new SimulationElement().onCreate(internalEndTs));		
	}

	@Override
	protected void simulationLoop() {
		while (!isSimulationEnd())
            execWaitingElements();		
	}

	@Override
	public void addEvent(DiscreteEvent ev) {
		if (ev.getTs() < lvt) {
			error("Causal restriction broken\t" + lvt + "\t" + ev);
		}
        else {
            addWait(ev);
        }		
	}
    
}
