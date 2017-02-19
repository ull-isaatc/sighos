package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.Output;

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
public class Simulation extends es.ull.iis.simulation.core.Simulation {

	/** The identifier to be assigned to the next resource */ 
	protected int nextResourceId = 0;
	/** List of resources present in the simulation. */
	protected final TreeMap<Integer, Resource> resourceList = new TreeMap<Integer, Resource>();

	/** List of element generators of the simulation. */
	protected final ArrayList<EventSource> eventSourceList = new ArrayList<EventSource>();

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
	protected final TreeMap<Integer, ResourceType> resourceTypeList = new TreeMap<Integer, ResourceType>();
	/** List of resource types present in the simulation. */
	protected final TreeMap<es.ull.iis.simulation.model.ResourceType, ResourceType> resourceTypeMap = new TreeMap<es.ull.iis.simulation.model.ResourceType, ResourceType>();

	/** The identifier to be assigned to the next element type */ 
	protected int nextElementTypeId = 0;
	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ElementType> elementTypeList = new TreeMap<Integer, ElementType>();
	/** List of resource types present in the simulation. */
	protected final TreeMap<es.ull.iis.simulation.model.ElementType, ElementType> elementTypeMap = new TreeMap<es.ull.iis.simulation.model.ElementType, ElementType>();

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
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 */
	public Simulation(int id, String description, Model model) {
		super(id, description, model);
		createSimulationObjects();
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
	public Simulation(int id, String description, Model model, TimeStamp startTs, TimeStamp endTs) {
		super(id, description, model, startTs, endTs);
		createSimulationObjects();
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
	public Simulation(int id, String description, Model model, long startTs, long endTs) {
		super(id, description, model, startTs, endTs);
		createSimulationObjects();
	}

	public EventSource getEventSourceFromModel(es.ull.iis.simulation.model.EventSource evSource) {
		if (evSource instanceof es.ull.iis.simulation.model.TimeDrivenGenerator) {
			return new TimeDrivenGenerator(this, (es.ull.iis.simulation.model.TimeDrivenGenerator)evSource);
		}
		else if (evSource instanceof es.ull.iis.simulation.model.Element) {
			final es.ull.iis.simulation.model.Element modelElem = (es.ull.iis.simulation.model.Element) evSource;
			return new Element(this, getElementType(modelElem.getType()), modelElem.getFlow());
		}
		return null;
	}
	
	private void createSimulationObjects() {
		for (es.ull.iis.simulation.model.ElementType et : model.getElementTypeList()) {
			new ElementType(this, et);
		}
		for (es.ull.iis.simulation.model.ResourceType rt : model.getResourceTypeList()) {
			new ResourceType(this, rt);
		}
		for (es.ull.iis.simulation.model.Resource res : model.getResourceList()) {
			new Resource(this, res);
		}
		for (es.ull.iis.simulation.model.EventSource evSource : model.getEventSourceList()) {
			getEventSourceFromModel(evSource);
		}
		for (es.ull.iis.simulation.model.flow.Flow flow : model.getFlowList()) {
			if (flow instanceof RequestResourcesFlow) {
				new RequestResources(this, (RequestResourcesFlow)flow);				
			}
			else if (flow instanceof ReleaseResourcesFlow) {
				new ReleaseResources(this, (ReleaseResourcesFlow)flow);
			}
		}
		
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
		if (out == null)
			out = new Output();
		
		debug("SIMULATION MODEL CREATED");
		// Sets default AM creator
		if (amCreator == null)
			amCreator = new StandardActivityManagerCreator(this);
		amCreator.createActivityManagers();
		debugPrintActManager();
		
		init();

		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationStartInfo(this, System.nanoTime(), this.internalStartTs));
		
		// Starts all the generators
		for (EventSource evSource : eventSourceList)
			addWait(evSource.onCreate(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			addWait(res.onCreate(internalStartTs));

		// Adds the event to control end of simulation
		addWait(new SimulationElement().onCreate(internalEndTs));
		
		while (!isSimulationEnd())
            execWaitingElements();
		// Frees the execution queue
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + internalEndTs);
    	printState();
    	
        // The user defined method for finalization is invoked
		end();
		
		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationEndInfo(this, System.nanoTime(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

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
            beforeClockTick();
            lvt = e.getTs();
            infoHandler.notifyInfo(new TimeChangeInfo(this, lvt));
            afterClockTick();
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
		elementTypeMap.put(et.getModelET(), et);
		return elementTypeList.put(et.getIdentifier(), et);
	}
	
	/**
	 * Adds an {@link es.ull.iis.simulation.sequential.ResourceType} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param rt
	 *            Resource Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ResourceType add(ResourceType rt) {
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
	protected void add(EventSource gen) {
		eventSourceList.add(gen);
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
	public Map<Integer, Resource> getResourceList() {
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
	public Map<Integer, ResourceType> getResourceTypeList() {
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
	public Resource getResource(int id) {
		return resourceList.get(id);
	}

	/**
	 * Returns the resource type with the corresponding identifier.
	 * @param id Resource type identifier.
	 * @return A resource type with the indicated identifier.
	 */
	public ResourceType getResourceType(int id) {
		return resourceTypeList.get(id);
	}

	/**
	 * Returns the "simulation" resource type corresponding to a "model" resource type.
	 * @param modelRT Resource type.
	 * @return A resource type with the indicated identifier.
	 */
	public ResourceType getResourceType(es.ull.iis.simulation.model.ResourceType modelRT) {
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
	 * Returns the "simulation" element type  corresponding to a "model" element type.
	 * @param modelET Element type.
	 * @return An element type with the indicated identifier.
	 */
	public ElementType getElementType(es.ull.iis.simulation.model.ElementType modelET) {
		return elementTypeMap.get(modelET);
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
			super(0, Simulation.this, "SE");
		}

		@Override
		public DiscreteEvent onCreate(long ts) {
			return new EventSource.DefaultStartEvent(ts);
		}

		@Override
		public DiscreteEvent onDestroy() {
			return new EventSource.DefaultFinalizeEvent();
		}
    }
    
    
	
}
