package es.ull.iis.simulation.sequential;

import java.util.ArrayList;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iv�n Castilla Rodr�guez
 */
public class ActivityManagerEngine extends EngineObject implements es.ull.iis.simulation.model.engine.ActivityManagerEngine {
    /** True if there is at least one new resource available the current timestamp */ 
    private boolean availableResource;
    /** This queue contains the flow executors that are waiting for activities of this AM */
    private final FlowExecutorQueue waitingQueue;
    /** This queue contains the work threads that have become available the current timestamp */
    private final FlowExecutorQueue currentQueue;
    /** The associated {@link ActivityManager} */
    private final ActivityManager modelAM;
    
   /**
	* Creates a new instance of ActivityManager.
	* @param simul ParallelSimulationEngine this activity manager belongs to
    */
    public ActivityManagerEngine(SequentialSimulationEngine simul, ActivityManager modelAM) {
        super(modelAM.getIdentifier(), simul, "AM");
        waitingQueue = new FlowExecutorQueue();
        currentQueue = new FlowExecutorQueue();
        this.modelAM = modelAM;
    }

    /**
     * @return the associated {@link ActivityManager}
     */
    public ActivityManager getModelAM() {
    	return modelAM;
    }

	@Override
	public void processAvailableResources() {
        // A count of the useless single flows 
    	int uselessSF = 0;
    	// A postponed removal list
    	final ArrayList<FlowExecutor> toRemove = new ArrayList<FlowExecutor>();
    	final int queueSize = waitingQueue.size();
    	for (final FlowExecutor fe : waitingQueue) {
            // TODO: Check whether it always works fine
            final RequestResourcesFlow reqResources = (RequestResourcesFlow) fe.getCurrentFlow();
            
            final int result = fe.availableResource(reqResources);
            if (result == -1) {
        		toRemove.add(fe);
        		uselessSF--;
        	}
        	else if (result > 0) {	// The activity can't be performed with the current resources
            	uselessSF += result;
        	}
            // A little optimization to stop if it is detected that no more activities can be performed
            if (uselessSF == queueSize)
            	break;
    	}
    	// Postponed removal to avoid conflict with the activity manager queue
    	for (FlowExecutor fe : toRemove)
    		((RequestResourcesFlow) fe.getCurrentFlow()).queueRemove(fe);
		// After executing the work there are no more available resources
    	availableResource = false;
		// In any case, remove all the pending elements
		currentQueue.clear();
	} 

	@Override
	public void processAvailableElements() {
		// Checks if there are pending activities that haven't noticed the
		// element availability
		for (final FlowExecutor fe : currentQueue) {
			final Element elem = fe.getElement();
			if (elem.getCurrent() == null) {
				final RequestResourcesFlow reqResources = (RequestResourcesFlow) fe.getCurrentFlow();
				if (elem.isDebugEnabled())
					elem.debug("Calling availableElement()\t" + reqResources + "\t" + reqResources.getDescription());
				fe.availableElement(reqResources);
			}			
		}
		// In any case, remove all the pending elements
		currentQueue.clear();
	}

	@Override
	public void setAvailableResource(boolean available) {
		availableResource = available;
	}

	@Override
	public boolean getAvailableResource() {
		return availableResource;
	}

    @Override
    public void queueAdd(FlowExecutor fe) {
    	waitingQueue.add(fe);
    }

    @Override
    public void queueRemove(FlowExecutor fe) {
    	waitingQueue.remove(fe);
    }
    
    @Override
    public void notifyAvailableElement(FlowExecutor fe) {
    	currentQueue.add(fe);
    }
    
}
