/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayDeque;
import java.util.Map.Entry;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.model.engine.ElementEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.MoveFlow;
import es.ull.iis.simulation.variable.EnumVariable;
import es.ull.iis.util.Prioritizable;

/**
 * An entity capable of following a {@link Flow workflow}. Elements have a {@link ElementType type} and interact with {@link Resource resources}
 * by means of {@link es.ull.iis.simulation.model.flow.ResourceHandlerFlow resource handler flows}
 * Elements can also move from a {@link Location} to another.   
 * @author Iván Castilla
 *
 */
public class Element extends VariableStoreSimulationObject implements Prioritizable, EventSource, Movable {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	/** If true, the element is in exclusive mode, and cannot perform other exclusive tasks concurrently */ 
	protected boolean exclusive = false;
	/** The current location of the element */
	private Location currentLocation;
	/** The initial location of the element */
	private final Location initLocation;
	/** The current element instance that drives the movement of the element */
	private ElementInstance movingInstance = null;
	/** The size of the element */
	private final int size;
	/** Main element instance */
	protected ElementInstance mainInstance = null;
    /** List of seized resources indexed as groups by an identifier */
    final protected SeizedResourcesCollection seizedResources;
	/** The engine that executes specific behavior of the element */
	private ElementEngine engine;
	
	/**
	 * Creates an element with a type and initial flow; and 0 size.
	 * @param simul Simulation model this element belongs to
	 * @param elementType Element type
	 * @param initialFlow First step of the flow of the element
	 */
	public Element(final Simulation simul, final ElementType elementType, final InitializerFlow initialFlow) {
		this(simul, "E", elementType, initialFlow, 0, null);
	}
	
	/**
	 * Creates an element with a type and initial flow
	 * @param simul Simulation model this element belongs to
	 * @param elementType Element type
	 * @param initialFlow First step of the flow of the element
	 * @param size The size of the element
     * @param initLocation The initial location of the element
	 */
	public Element(final Simulation simul, String objectTypeId, final ElementType elementType, final InitializerFlow initialFlow, final int size, final Location initLocation) {
		super(simul, simul.getNewElementId(), objectTypeId);
		this.elementType = elementType;
		this.initialFlow = initialFlow;
        this.seizedResources = new SeizedResourcesCollection();
        this.size = size;
        this.initLocation = initLocation;
		initializeElementVars(this.elementType.getElementValues());
	}
	
	/**
	 * Creates an element from the information of a Generator
	 * @param simul Simulation model this element belongs to
	 * @param info Information required to create the element  
	 */
	public Element(final Simulation simul, String objectTypeId, final StandardElementGenerationInfo info) {
		super(simul, simul.getNewElementId(), objectTypeId);
		this.elementType = info.getElementType();
		this.initialFlow = info.getFlow();
        this.seizedResources = new SeizedResourcesCollection();
        this.initLocation = info.getInitLocation();
        this.size = info.getSize(this);
		initializeElementVars(this.elementType.getElementValues());
	}
	
	/**
	 * Creates an element from the information of a Generator
	 * @param simul Simulation model this element belongs to
	 * @param info Information required to create the element  
	 */
	public Element(final Simulation simul, final StandardElementGenerationInfo info) {
		this(simul, "E", info);
	}
	
	/**
	 * Returns the corresponding type of the element.
	 * @return the corresponding type of the element
	 */
	public ElementType getType() {
		return elementType;
	}
	
	/**
	 * Returns the associated {@link es.ull.iis.simulation.model.flow.InitializerFlow Flow}.
	 * @return the associated {@link es.ull.iis.simulation.model.flow.InitializerFlow Flow}
	 */
	public InitializerFlow getFlow() {
		return initialFlow;
	}

	/**
	 * Returns the element's priority, which is the element type's priority.
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return elementType.getPriority();
	}

	/**
	 * Notifies an instance of the element is waiting in an activity queue.
	 * @param ei Element instance waiting in queue.
	 */
	public void incInQueue(final ElementInstance ei) {
		engine.incInQueue(ei);
	}
	
	/**
	 * Notifies an instance of the element has finished waiting in an activity queue.
	 * @param ei Element instance that was waiting in a queue.
	 */
	public void decInQueue(final ElementInstance ei) {
		engine.decInQueue(ei);
	}
	
	/**
	 * Returns true if the element is currently performing an exclusive activity; returns false otherwise 
	 * @return true if the element is currently performing an exclusive activity; returns false otherwise
	 */
	public boolean isExclusive() {
		return exclusive;
	}

	/**
	 * Sets the element instance performing the current exclusive activity. If such instance is null, it means that the element is available to 
	 * perform other exclusive activity. Hence, creates the events to notify the activities that this element is now available. 
	 * @param current The element instance performing the current exclusive activity; null if the element has finished performing the activity
	 */
	public void setExclusive(final boolean exclusive) {
		this.exclusive = exclusive;
		if (!exclusive) {
			engine.notifyAvailableElement();
		}
	}

	/**
	 * Adds the specified resources to the list of seized resources
	 * @param reqFlow The flow that seized the resources
	 * @param ei Element instance performing the seizing
	 * @param newResources New resources seized by the element
	 */
    protected void seizeResources(final RequestResourcesFlow reqFlow, final ElementInstance ei, final ArrayDeque<Resource> newResources) {
    	final int resId = (reqFlow.getResourcesId() < 0) ? -ei.getIdentifier() : reqFlow.getResourcesId();
    	seizedResources.addResources(resId, newResources);
    }

    /**
     * Removes the resources specified in the workgroup from the list of seized resources
     * @param relFlow The flow that released the resources
     * @param ei Element instance performing the releasing
     * @return The released resources
     */
    protected ArrayDeque<Resource> releaseResources(final ReleaseResourcesFlow relFlow, final ElementInstance ei) {
    	final int resId = (relFlow.getResourcesId() < 0) ? -ei.getIdentifier() : relFlow.getResourcesId();
    	final WorkGroup wg = relFlow.getWorkGroup();
    	return seizedResources.removeResources(resId, wg);
    	
    }

    /**
     * Returns the list of resources currently seized by the element
     * @return the list of resources currently seized by the element
     */
	public ArrayDeque<Resource> getCaughtResources() {
		return seizedResources.getAll();
	}

	/**
	 * Returns the list of resources caught by the specified element instance when performing the specified flow
	 * @param reqFlow The flow that seized the resources
	 * @param ei Element instance that performed the seizing
	 * @return the list of resources caught by the specified element instance when performing the specified flow
	 */
	public ArrayDeque<Resource> getCaughtResources(final RequestResourcesFlow reqFlow, final ElementInstance ei) {
    	final int resId = (reqFlow.getResourcesId() < 0) ? -ei.getIdentifier() : reqFlow.getResourcesId();
		return seizedResources.get(resId);
	}

    /**
     * Returns the list of resources identifier by the resourcesId and belonging to the specified workgroup
     * @param resourcesId Identifier of the group of resources
     * @param wg Resource types 
     * @return the list of resources caught by the specified element instance when performing the specified flow
     */
	public ArrayDeque<Resource> getCaughtResources(final int resourcesId, final WorkGroup wg) {
		return seizedResources.get(resourcesId, wg);
	}

	/**
	 * Returns <code>true</code> if the element has currently acquired any resource of type <code>rt</code>; <code>false</code> otherwise. 
	 * @param rt ResourceType been searched
	 * @return <code>true</code> if the element has currently acquired any resource of type <code>rt</code>
	 */
	public boolean isAcquiredResourceType(final ResourceType rt) {
		return seizedResources.containsResourceType(rt);
	}
	
	/**
	 * Initializes the variables of the element as indicated by a generator
	 * @param varList List of variables and values
	 */
	protected void initializeElementVars(final TreeMap<String, Object> varList) {
		for (Entry<String, Object> entry : varList.entrySet()) {
			final String name = entry.getKey();
			final Object value = entry.getValue();
			if (value instanceof Number) {
				this.putVar(name, ((Number)value).doubleValue());
			}
			else {
				if (value instanceof Boolean) {
					this.putVar(name, ((Boolean)value).booleanValue());
				}
				else if (value instanceof EnumVariable) {
						this.putVar(name, ((EnumVariable)value));
				}
				else if (value instanceof Character) {
					this.putVar(name, ((Character)value).charValue());
				}
			}
		}
	}

	/**
	 * Initializes the element by requesting the <code>initialFlow</code>, and placing the element in its initial location. If there's no initial flow 
	 * or the element does not fit into the initial location, the element finishes immediately.
	 */
	@Override
	public DiscreteEvent onCreate(final long ts) {
		simul.notifyInfo(new ElementInfo(simul, this, elementType, ElementInfo.Type.START, getTs()));
		if (initLocation != null) {
			if (initLocation.fitsIn(this)) {
				initLocation.enter(this);
			}
			else {
				error("Unable to initialize element. Not enough space in location " + initLocation + " (available: " + initLocation.getAvailableCapacity() + " - required: " + size + ")");				
				return onDestroy(ts);
			}
		}
		if (initialFlow != null) {
			mainInstance = ElementInstance.getMainElementInstance(this);
			return (new RequestFlowEvent(ts, initialFlow, mainInstance.getDescendantElementInstance(initialFlow)));
		}
		else
			return onDestroy(ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		simul.notifyInfo(new ElementInfo(simul, this, elementType, ElementInfo.Type.FINISH, getTs()));
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    public void notifyEnd() {
    	engine.notifyEnd();
    }
    
    /**
     * Creates and adds an event to request a flow at the current simulation time
     * @param f Flow to be requested
     * @param ei Element instance that will perform the flow
     */
	public void addRequestEvent(final Flow f, final ElementInstance ei) {
		simul.addEvent(new RequestFlowEvent(getTs(), f, ei));
	}
	
	/**
     * Creates and adds an event to finish a flow at the specified simulation time
	 * @param ts Timestamp when the finalization of the flow is scheduled to happen
	 * @param f Flow to be finished
	 * @param ei Element instance that will finish the flow
	 */
	public void addFinishEvent(final long ts, final TaskFlow f, final ElementInstance ei) {
		simul.addEvent(new FinishFlowEvent(ts, f, ei));
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		engine = simul.getElementEngineInstance(this);
	}

	/**
	 * Returns the engine that helps the simulation processing the element actions
	 * @return the engine
	 */
	public ElementEngine getEngine() {
		return engine;
	}
	

	@Override
	public int getCapacity() {
		return size;
	}

	@Override
	public Location getLocation() {
		return currentLocation;
	}

	@Override
	public void setLocation(final Location location) {
		if (currentLocation == null) {
			simul.notifyInfo(new EntityLocationInfo(simul, this, location, EntityLocationInfo.Type.START, getTs()));
			currentLocation = location;
		}
		else {
			simul.notifyInfo(new EntityLocationInfo(simul, this, currentLocation, EntityLocationInfo.Type.LEAVE, getTs()));
			currentLocation = location;
			simul.notifyInfo(new EntityLocationInfo(simul, this, currentLocation, EntityLocationInfo.Type.ARRIVE, getTs()));
		}
	}

	@Override
	public void notifyLocationAvailable(final Location location) {
		location.enter(this);

    	final MoveFlow flow = (MoveFlow)movingInstance.getCurrentFlow();
		
		if (currentLocation.equals(flow.getDestination())) {
			flow.finish(movingInstance);
		}
		else {
			keepMoving(flow, movingInstance);
		}
	}
	
	/**
	 * Makes the element issue a {@link MoveEvent move event} to continue the movement to its destination
	 * @param flow The flow indicating the destination
	 * @param ei Element instance moving
	 */
	public void keepMoving(final MoveFlow flow, final ElementInstance ei) {
    	movingInstance = ei;
		final MoveEvent mEvent = new MoveEvent(getTs() + currentLocation.getDelayAtExit(this), flow, ei);
    	simul.addEvent(mEvent);		
	}
	
	/**
	 * An event to request a flow.
	 * @author Iván Castilla Rodríguez
	 */
	protected class RequestFlowEvent extends DiscreteEvent {
		/** The element instance that executes the request */
		private final ElementInstance ei;
		/** The flow to be requested */
		private final Flow f;

		public RequestFlowEvent(final long ts, final Flow f, final ElementInstance ei) {
			super(ts);
			this.ei = ei;
			this.f = f;
		}		

		@Override
		public void event() {
			ei.setCurrentFlow(f);
			f.request(ei);
		}
	}
	
	/**
	 * An event to finish a flow. 
	 * @author Iván Castilla Rodríguez
	 */
	protected class FinishFlowEvent extends DiscreteEvent {
		/** The element instance that executes the finish */
		private final ElementInstance ei;
		/** The flow to be finished */
		private final TaskFlow f;

		public FinishFlowEvent(final long ts, final TaskFlow f, final ElementInstance ei) {
			super(ts);
			this.ei = ei;
			this.f = f;
		}		

		@Override
		public void event() {
			f.finish(ei);
		}
	}

    /**
     * An event to perform a move. The element try to move to the next location in its path to its final destination. The element only leaves 
     * its current location if there is enough free space for the element in the new location; otherwise, it waits. 
     * @author Iván Castilla
     *
     */
	protected class MoveEvent extends DiscreteEvent {
		/** The element instance that executes the event */
		private final ElementInstance ei;
		/** The instance that computes the path to the final destination */
		private final MoveFlow flow;
		
		/**
		 * Creates a move event that makes an intermediate step in the way to destination
		 * @param ts Timestamp when the resource will arrive at the intermediate location
		 * @param nextLocation Intermediate location 
		 * @param destination Final destination
		 * @param router Instance that returns the path for the resource
		 */
		public MoveEvent(final long ts, final MoveFlow flow, final ElementInstance ei) {
			super(ts);
			this.flow = flow;
			this.ei = ei;
		}

		@Override
		public void event() {
			flow.move(ei);
		}
	}

	/**
	 * A collection of resources that have been seized by this element. They are arranged in two levels: the
	 * first level represents resource groups, as logically defined by the modeler; the second level represents
	 * resource types.
	 * @author Iván Castilla
	 *
	 */
	protected final class SeizedResourcesCollection {
	    /** List of seized resources indexed as groups by an identifier */
	    final protected TreeMap<Integer, TreeMap<ResourceType, ArrayDeque<Resource>>> resources;
		
	    /**
	     * Creates an empty collection of seized resources.
	     */
	    public SeizedResourcesCollection() {
	    	resources = new TreeMap<Integer, TreeMap<ResourceType,ArrayDeque<Resource>>>();
	    	resources.put(0, new TreeMap<ResourceType, ArrayDeque<Resource>>());
		}
	    
	    /**
	     * Returns true if the collection contains any resource of the specified resource type; false otherwise
	     * @param rt Searched resource type 
	     * @return true if the collection contains any resource of the specified resource type; false otherwise
	     */
	    public boolean containsResourceType(final ResourceType rt) {
	    	for (TreeMap<ResourceType, ArrayDeque<Resource>> item1 : resources.values()) {
	    		if (item1.containsKey(rt))
	    			return true;
	    	}
	    	return false;
	    }
	    
	    /**
	     * Returns the whole list of resources seized by the element
	     * @return the whole list of resources seized by the element
	     */
	    public ArrayDeque<Resource> getAll() {
	    	final ArrayDeque<Resource> list = new ArrayDeque<Resource>();
	    	
	    	for (TreeMap<ResourceType, ArrayDeque<Resource>> item1 : resources.values()) {
	    		for (ArrayDeque<Resource> item2 : item1.values()) {
	    			list.addAll(item2);
	    		}
	    	}
	    	return list;
	    }
	    
	    /**
	     * Returns the list of resources caught by the specified element instance when performing the specified flow
	     * @param resourcesId Identifier of the group of resources
	     * @return the list of resources caught by the specified element instance when performing the specified flow
	     */
	    public ArrayDeque<Resource> get(final int resourcesId) {
	    	final ArrayDeque<Resource> list = new ArrayDeque<Resource>();
	    	final TreeMap<ResourceType, ArrayDeque<Resource>> item1 = resources.get(resourcesId);
    		for (ArrayDeque<Resource> item2 : item1.values()) {
    			list.addAll(item2);
    		}
	    	return list;
	    }
	    
	    /**
	     * Returns the list of resources identifier by the resourcesId and belonging to the specified workgroup
	     * @param resourcesId Identifier of the group of resources
	     * @param wg Resource types 
	     * @return the list of resources caught by the specified element instance when performing the specified flow
	     */
	    public ArrayDeque<Resource> get(final int resourcesId, final WorkGroup wg) {
	    	final ArrayDeque<Resource> list = new ArrayDeque<Resource>();
	    	final TreeMap<ResourceType, ArrayDeque<Resource>> item1 = resources.get(resourcesId);
    		final ResourceType[] rts = wg.getResourceTypes();
    		for (int i = 0; i < rts.length; i++) {
    			list.addAll(item1.get(rts[i]));
    		}
	    	return list;
	    }
	    
		/**
		 * Adds the specified resources to the list of seized resources
		 * @param resourcesId Identifier of the group of resources
		 * @param newResources New resources seized by the element
		 */
	    public void addResources(final int resourcesId, final ArrayDeque<Resource> newResources) {
	    	// If it's a request flow inside an activity, use minus the element instance identifier
	    	TreeMap<ResourceType, ArrayDeque<Resource>> collection = resources.get(resourcesId);
	    	// Not already created
	    	if (collection == null) {
	    		collection = new TreeMap<ResourceType, ArrayDeque<Resource>>();
				resources.put(resourcesId, collection);
	    	}
	    	for (final Resource res : newResources) {
	    		final ResourceType currentRT = res.getCurrentResourceType();
	    		if (!collection.containsKey(currentRT))
	    			collection.put(currentRT, new ArrayDeque<Resource>());
	    		collection.get(currentRT).push(res);
	    	}
	    }
	    
	    /**
	     * Removes the resources specified in the workgroup from the list of seized resources
		 * @param resourcesId Identifier of the group of resources
		 * @param wg Workgroup
	     * @return The released resources
	     */
	    public ArrayDeque<Resource> removeResources(final int resourcesId, final WorkGroup wg) {
	    	final TreeMap<ResourceType, ArrayDeque<Resource>> collection = resources.get(resourcesId);
	    	// Not already created
	    	if (collection == null) {
				error("Trying to release group of resources not already created. ID:" + resourcesId);
				return null;	    		
	    	}
	    	final ArrayDeque<Resource> toRemove = new ArrayDeque<Resource>();
	    	// Remove all resources from group
	    	if (wg == null) {
	    		for (ArrayDeque<Resource> list : collection.values()) {
	    			toRemove.addAll(list);
	    		}
	    		collection.clear();
	    	}
	    	else {
	    		final ResourceType[] rts = wg.getResourceTypes();
	    		for (int i = 0; i < rts.length; i++) {
	    			final ArrayDeque<Resource> list = collection.get(rts[i]);
	    			if (list == null) {
	    				error("Trying to release non-seized resource of type " + rts[i] + " from group with ID " + resourcesId);
	    			}
	    			else {
	    				int max = wg.getNeeded(i);
	    				// More resources to remove than available
	    				if (wg.getNeeded(i) > list.size()) {
	    					max = collection.get(rts[i]).size();
		    				error("Trying to release " + wg.getNeeded(i) + " resources of type " + rts[i] + " from group with ID " + resourcesId + ". Available: " + list.size());
	    					toRemove.addAll(collection.remove(rts[i]));
	    				}
	    				// Remove all resources from that type
	    				else if (wg.getNeeded(i) == collection.get(rts[i]).size()) {
	    					toRemove.addAll(collection.remove(rts[i]));
	    				}
	    				else {
			    			for (int j = 0; j < max; j++) {
			    				toRemove.add(list.pop());
			    			}
		    			}
	    			}
	    		}
	    	}
	    	return toRemove;
	    }
	}
}
