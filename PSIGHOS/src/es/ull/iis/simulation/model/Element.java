/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayDeque;
import java.util.Map.Entry;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.model.engine.ElementEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import es.ull.iis.simulation.variable.EnumVariable;
import es.ull.iis.util.Prioritizable;

/**
 * An entity capable of following a {@link Flow workflow}. Elements have a {@link ElementType type} and interact with {@link Resource resources}
 * by means of {@link es.ull.iis.simulation.model.flow.ResourceHandlerFlow resource handler flows}  
 * @author Iván Castilla
 *
 */
public class Element extends VariableStoreSimulationObject implements Prioritizable, EventSource {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	/** Exclusive instance which the element is currently carrying out */
	protected ElementInstance current = null;
	/** Main element instance */
	protected ElementInstance mainInstance = null;
    /** List of seized resources indexed as groups by an identifier */
    final protected SeizedResourcesCollection seizedResources;
	/** The engine that executes specific behavior of the element */
	private ElementEngine engine;
	
	/**
	 * Creates an element with a type and initial flow
	 * @param simul Simulation model this element belongs to
	 * @param elementType Element type
	 * @param initialFlow First step of the flow of the element
	 */
	public Element(Simulation simul, ElementType elementType, InitializerFlow initialFlow) {
		super(simul, simul.getNewElementId(), "E");
		this.elementType = elementType;
		this.initialFlow = initialFlow;
        this.seizedResources = new SeizedResourcesCollection();
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
	public void incInQueue(ElementInstance ei) {
		engine.incInQueue(ei);
	}
	
	/**
	 * Notifies an instance of the element has finished waiting in an activity queue.
	 * @param ei Element instance that was waiting in a queue.
	 */
	public void decInQueue(ElementInstance ei) {
		engine.decInQueue(ei);
	}
	
	/**
	 * If the element is currently performing an activity, returns the element instance used by the element. If the element is not performing any exclusive 
	 * activity, returns null.
	 * @return The element instance performing the current exclusive activity
	 */
	public ElementInstance getCurrent() {
		return current;
	}

	/**
	 * Sets the element instance performing the current exclusive activity. If such instance is null, it means that the element is available to 
	 * perform other exclusive activity. Hence, creates the events to notify the activities that this element is now available. 
	 * @param current The element instance performing the current exclusive activity; null if the element has finished performing the activity
	 */
	public void setCurrent(ElementInstance current) {
		this.current = current;
		if (current == null) {
			engine.notifyAvailableElement();
		}
	}

	/**
	 * Adds the specified resources to the list of seized resources
	 * @param reqFlow The flow that seized the resources
	 * @param ei Element instance performing the seizing
	 * @param newResources New resources seized by the element
	 */
    public void seizeResources(RequestResourcesFlow reqFlow, ElementInstance ei, ArrayDeque<Resource> newResources) {
    	seizedResources.addResources(reqFlow, ei, newResources);
    }

    /**
     * Removes the resources specified in the workgroup from the list of seized resources
     * @param relFlow The flow that released the resources
     * @param ei Element instance performing the releasing
     * @param wg Set of resources to release
     * @return The released resources
     */
    public ArrayDeque<Resource> releaseResources(ReleaseResourcesFlow relFlow, ElementInstance ei, WorkGroup wg) {
    	return seizedResources.removeResources(relFlow, ei, wg);
    	
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
	public ArrayDeque<Resource> getCaughtResources(RequestResourcesFlow reqFlow, ElementInstance ei) {
		return seizedResources.get(reqFlow, ei);
	}

	/**
	 * Returns <code>true</code> if the element has currently acquired any resource of type <code>rt</code>; <code>false</code> otherwise. 
	 * @param rt ResourceType been searched
	 * @return <code>true</code> if the element has currently acquired any resource of type <code>rt</code>
	 */
	public boolean isAcquiredResourceType(ResourceType rt) {
		return seizedResources.containsResourceType(rt);
	}
	
	/**
	 * Initializes the variables of the element as indicated by a generator
	 * @param varList List of variables and values
	 */
	public void initializeElementVars(TreeMap<String, Object> varList) {
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
	 * Initializes the element by requesting the <code>initialFlow</code>. If there's no initial flow
	 * the element finishes immediately.
	 */
	@Override
	public DiscreteEvent onCreate(long ts) {
		simul.notifyInfo(new ElementInfo(simul, this, elementType, ElementInfo.Type.START, getTs()));
		if (initialFlow != null) {
			mainInstance = ElementInstance.getMainElementInstance(this);
			return (new RequestFlowEvent(getTs(), initialFlow, mainInstance.getDescendantElementInstance(initialFlow)));
		}
		else
			return onDestroy(ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		simul.notifyInfo(new ElementInfo(simul, this, elementType, ElementInfo.Type.FINISH, getTs()));
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
		// TODO: Check if the following action should be performed within the event
		// simul.removeActiveElement(this);
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
	public void addRequestEvent(Flow f, ElementInstance ei) {
		simul.addEvent(new RequestFlowEvent(getTs(), f, ei));
	}
	
	/**
     * Creates and adds an event to finish a flow at the specified simulation time
	 * @param ts Timestamp when the finalization of the flow is scheduled to happen
	 * @param f Flow to be finished
	 * @param ei Element instance that will finish the flow
	 */
	public void addFinishEvent(long ts, TaskFlow f, ElementInstance ei) {
		simul.addEvent(new FinishFlowEvent(ts, f, ei));
	}
	
	/**
	 * An event to request a flow.
	 * @author Iván Castilla Rodríguez
	 */
	public class RequestFlowEvent extends DiscreteEvent {
		/** The element instance that executes the request */
		private final ElementInstance ei;
		/** The flow to be requested */
		private final Flow f;

		public RequestFlowEvent(long ts, Flow f, ElementInstance ei) {
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
	public class FinishFlowEvent extends DiscreteEvent {
		/** The element instance that executes the finish */
		private final ElementInstance ei;
		/** The flow to be finished */
		private final TaskFlow f;

		public FinishFlowEvent(long ts, TaskFlow f, ElementInstance ei) {
			super(ts);
			this.ei = ei;
			this.f = f;
		}		

		@Override
		public void event() {
			f.finish(ei);
		}
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
	    public boolean containsResourceType(ResourceType rt) {
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
	     * @param reqFlow The flow that seized the resources
	     * @param ei Element instance that performed the seizing
	     * @return the list of resources caught by the specified element instance when performing the specified flow
	     */
	    public ArrayDeque<Resource> get(RequestResourcesFlow reqFlow, ElementInstance ei) {
	    	final ArrayDeque<Resource> list = new ArrayDeque<Resource>();
	    	final int resId = (reqFlow.getResourcesId() < 0) ? -ei.getIdentifier() : reqFlow.getResourcesId();
	    	
	    	final TreeMap<ResourceType, ArrayDeque<Resource>> item1 = resources.get(resId);
	    		for (ArrayDeque<Resource> item2 : item1.values()) {
	    			list.addAll(item2);
	    		}
	    	return list;
	    }
	    
		/**
		 * Adds the specified resources to the list of seized resources
		 * @param reqFlow The flow that seized the resources
		 * @param ei Element instance performing the seizing
		 * @param newResources New resources seized by the element
		 */
	    public void addResources(RequestResourcesFlow reqFlow, ElementInstance ei, ArrayDeque<Resource> newResources) {
	    	// If it's a request flow inside an activity, use minus the element instance identifier
	    	final int resId = (reqFlow.getResourcesId() < 0) ? -ei.getIdentifier() : reqFlow.getResourcesId();
	    	TreeMap<ResourceType, ArrayDeque<Resource>> collection = resources.get(resId);
	    	// Not already created
	    	if (collection == null) {
	    		collection = new TreeMap<ResourceType, ArrayDeque<Resource>>();
				resources.put(resId, collection);
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
	     * @param relFlow The flow that released the resources
	     * @param ei Element instance performing the releasing
	     * @param wg Set of resources to release
	     * @return The released resources
	     */
	    public ArrayDeque<Resource> removeResources(ReleaseResourcesFlow relFlow, ElementInstance ei, WorkGroup wg) {
	    	final int resId = (relFlow.getResourcesId() < 0) ? -ei.getIdentifier() : relFlow.getResourcesId();
	    	final TreeMap<ResourceType, ArrayDeque<Resource>> collection = resources.get(resId);
	    	// Not already created
	    	if (collection == null) {
				error("Trying to release group of resources not already created. ID:" + resId);
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
	    				error("Trying to release non-seized resource of type " + rts[i] + " from group with ID " + resId);
	    			}
	    			else {
	    				int max = wg.getNeeded(i);
	    				// More resources to remove than available
	    				if (wg.getNeeded(i) > list.size()) {
	    					max = collection.get(rts[i]).size();
		    				error("Trying to release " + wg.getNeeded(i) + " resources of type " + rts[i] + " from group with ID " + resId + ". Available: " + list.size());
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
