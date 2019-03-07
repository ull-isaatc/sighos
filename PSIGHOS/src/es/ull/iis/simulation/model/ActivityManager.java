package es.ull.iis.simulation.model;

import java.util.ArrayList;

import es.ull.iis.simulation.model.engine.ActivityManagerEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
 */
public class ActivityManager extends SimulationObject implements Describable {
    /** Static counter for assigning each new id */
	private static int nextid = 0;
	/** A prioritized table of activities */
	protected final ArrayList<RequestResourcesFlow> activityList;
    /** A list of resorce types */
    protected final ArrayList<ResourceType> resourceTypeList;
    /** The specific implementation of the behavior of the activity manager */
    private ActivityManagerEngine engine;
    
   /**
	* Creates a new instance of ActivityManager.
	* @param simul ParallelSimulationEngine this activity manager belongs to
    */
    public ActivityManager(final Simulation model) {
        super(model, nextid++, "AM");
        resourceTypeList = new ArrayList<ResourceType>();
        activityList = new ArrayList<RequestResourcesFlow>();
        model.add(this);
    }

    /**
     * Adds a request resources flows to this activity manager.
     * @param flow request resources flow
     */
    public void add(final RequestResourcesFlow flow) {
        activityList.add(flow);
    }
    
    /**
     * Adds a resource type to this activity manager.
     * @param rt Resource type added
     */
    public void add(final ResourceType rt) {
        resourceTypeList.add(rt);
    }

    /**
     * Adds an element instance to the waiting queue.
     * @param ei Element instance which is added to the waiting queue.
     */
    public void queueAdd(final ElementInstance ei) {
    	engine.queueAdd(ei);
    }
    
    /**
     * Removes an element instance from the waiting queue.
     * @param ei Element instance which is removed from the waiting queue.
     */
    public void queueRemove(final ElementInstance ei) {
    	engine.queueRemove(ei);
    }
    
    /**
     * Informs this activity manager that a resource has become available. 
     */
    public void notifyResource() {
    	engine.notifyAvailableResource();
    }

    /**
     * Notifies the activity manager that an element is now available to request resources handled within the manager
     * @param ei Element instance now available
     */
    public void notifyAvailableElement(final ElementInstance ei) {
    	engine.notifyAvailableElement(ei);
    }
    
	/**
	 * Builds a detailed description of this activity manager, including activities and 
	 * resource types.
	 * @return A large description of this activity manager.
	 */
	public String getDescription() {
        StringBuffer str = new StringBuffer();
        str.append("Activity Manager " + getIdentifier() + "\r\n(Activity[priority]):");
        for (RequestResourcesFlow a : activityList)
            str.append("\t\"" + a + "\"[" + a.getPriority() + "]");
        str.append("\r\nResource Types: ");
        for (ResourceType rt : resourceTypeList)
            str.append("\t\"" + rt + "\"");
        return str.toString();
	}

	/**
	 * Checks if there are new resources or elements available and executes the corresponding actions.
	 * This method centralizes the execution of this code to preserve all the elements and activities priorities.
	 */
	public void executeWork() {
		if (engine.getAvailableResource()) {
	    	// First marks all the activities as "potentially feasible"
	    	for (RequestResourcesFlow act : activityList)
	        	act.resetFeasible();
	    	engine.processAvailableResources();
		}
		else {
			engine.processAvailableElements();
		}
	}
	
	@Override
	protected void assignSimulation(SimulationEngine simul) {
		engine = simul.getActivityManagerEngineInstance(this);
	}
}
