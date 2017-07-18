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
	 * Notifies a new work thread is waiting in an activity queue.
	 * @param wt Work thread waiting in queue.
	 */
	public void incInQueue(ElementInstance fe) {
		engine.incInQueue(fe);
	}
	
	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	public void decInQueue(ElementInstance fe) {
		engine.decInQueue(fe);
	}
	
	/**
	 * If the element is currently performing an activity, returns the work
	 * thread used by the element. If the element is not performing any presential 
	 * activity, returns null.
	 * @return The work thread corresponding to the current presential activity being
	 * performed by this element.
	 */
	public ElementInstance getCurrent() {
		return current;
	}

	/**
	 * Sets the work thread corresponding to the current presential activity 
	 * being performed by this element.Creates the events to notify the activities that this element is now
	 * available. All the activities this element is in their queues are notified. 
	 * @param current The work thread corresponding to the current presential activity 
	 * being performed by this element. A null value indicates that the element has 
	 * finished performing the activity.
	 */
	public void setCurrent(ElementInstance current) {
		this.current = current;
		if (current == null) {
			engine.notifyAvailableElement();
		}
	}

	/**
	 * TODO
	 * @param reqFlow
	 * @param ei
	 * @param newResources
	 */
    public void seizeResources(RequestResourcesFlow reqFlow, ElementInstance ei, ArrayDeque<Resource> newResources) {
    	seizedResources.addResources(reqFlow, ei, newResources);
    }

    /**
     * TODO
     * @param relFlow
     * @param ei
     * @param wg
     * @return
     */
    public ArrayDeque<Resource> releaseResources(ReleaseResourcesFlow relFlow, ElementInstance ei, WorkGroup wg) {
    	return seizedResources.removeResources(relFlow, ei, wg);
    	
    }

    /**
     * TODO
     * @return
     */
	public ArrayDeque<Resource> getCaughtResources() {
		return seizedResources.getAll();
	}

	/**
	 * TODO
	 * @param reqFlow
	 * @param ei
	 * @return
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
    
	public void addRequestEvent(Flow f, ElementInstance fe) {
		simul.addEvent(new RequestFlowEvent(getTs(), f, fe));
	}
	
	public void addFinishEvent(long ts, TaskFlow f, ElementInstance fe) {
		simul.addEvent(new FinishFlowEvent(ts, f, fe));
	}
	
	/**
	 * Requests a flow.
	 * @author Iván Castilla Rodríguez
	 */
	public class RequestFlowEvent extends DiscreteEvent {
		/** The work thread that executes the request */
		private final ElementInstance fe;
		/** The flow to be requested */
		private final Flow f;

		public RequestFlowEvent(long ts, Flow f, ElementInstance fe) {
			super(ts);
			this.fe = fe;
			this.f = f;
		}		

		@Override
		public void event() {
			fe.setCurrentFlow(f);
			f.request(fe);
		}
	}
	
	/**
	 * Finishes a flow. 
	 * @author Iván Castilla Rodríguez
	 */
	public class FinishFlowEvent extends DiscreteEvent {
		/** The work thread that executes the finish */
		private final ElementInstance fe;
		/** The flow to be finished */
		private final TaskFlow f;

		public FinishFlowEvent(long ts, TaskFlow f, ElementInstance fe) {
			super(ts);
			this.fe = fe;
			this.f = f;
		}		

		@Override
		public void event() {
			f.finish(fe);
		}
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		engine = simul.getElementEngineInstance(this);
	}

	/**
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
	    
	    public boolean containsResourceType(ResourceType rt) {
	    	for (TreeMap<ResourceType, ArrayDeque<Resource>> item1 : resources.values()) {
	    		if (item1.containsKey(rt))
	    			return true;
	    	}
	    	return false;
	    }
	    
	    public ArrayDeque<Resource> getAll() {
	    	final ArrayDeque<Resource> list = new ArrayDeque<Resource>();
	    	
	    	for (TreeMap<ResourceType, ArrayDeque<Resource>> item1 : resources.values()) {
	    		for (ArrayDeque<Resource> item2 : item1.values()) {
	    			list.addAll(item2);
	    		}
	    	}
	    	return list;
	    }
	    
	    public ArrayDeque<Resource> get(RequestResourcesFlow reqFlow, ElementInstance ei) {
	    	final ArrayDeque<Resource> list = new ArrayDeque<Resource>();
	    	final int resId = (reqFlow.getResourcesId() < 0) ? -ei.getIdentifier() : reqFlow.getResourcesId();
	    	
	    	final TreeMap<ResourceType, ArrayDeque<Resource>> item1 = resources.get(resId);
	    		for (ArrayDeque<Resource> item2 : item1.values()) {
	    			list.addAll(item2);
	    		}
	    	return list;
	    }
	    
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
