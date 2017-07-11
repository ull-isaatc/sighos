package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
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
    	final ArrayList<ElementInstance> toRemove = new ArrayList<ElementInstance>();
    	final int queueSize = waitingQueue.size();
    	for (final ElementInstance ei : waitingQueue) {
            final Element e = ei.getElement();
            // TODO: Check whether it always works fine
            final RequestResourcesFlow reqFlow = (RequestResourcesFlow) ei.getCurrentFlow();
            
    		if (!reqFlow.isExclusive() || (e.getCurrent() == null)) {
				final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
    			// There are enough resources to perform the activity
    			if (solution != null) {
    				if (reqFlow.isExclusive()) 
    					e.setCurrent(ei);
    				final long delay = ei.catchResources(solution);
    				if (delay > 0)
    					ei.startDelay(delay);
    				else
    					reqFlow.next(ei);
            		toRemove.add(ei);
            		uselessSF--;
    			}
    			else { // The activity can't be performed with the current resources
                	uselessSF += reqFlow.getQueueSize();
    			}
    		}
            // A little optimization to stop if it is detected that no more activities can be performed
            if (uselessSF == queueSize)
            	break;
    	}
    	// Postponed removal to avoid conflict with the activity manager queue
    	for (ElementInstance fe : toRemove)
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
		for (final ElementInstance ei : currentQueue) {
			final Element elem = ei.getElement();
			final RequestResourcesFlow reqFlow = (RequestResourcesFlow) ei.getCurrentFlow();
			if (elem.isDebugEnabled())
				elem.debug("Calling availableElement()\t" + reqFlow + "\t" + reqFlow.getDescription());
			if (elem.getCurrent() == null) {
				final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
				if (solution != null) {
					if (reqFlow.isExclusive()) {
						elem.setCurrent(ei);
					}
					final long delay = ei.catchResources(solution);
					reqFlow.queueRemove(ei);
					if (delay > 0)
						ei.startDelay(delay);
					else
						reqFlow.next(ei);
				}    	
			}			
		}
		// In any case, remove all the pending elements
		currentQueue.clear();
	}

	@Override
	public void notifyAvailableResource() {
		availableResource = true;
	}

	@Override
	public boolean getAvailableResource() {
		return availableResource;
	}

    @Override
    public void queueAdd(ElementInstance fe) {
    	waitingQueue.add(fe);
    }

    @Override
    public void queueRemove(ElementInstance fe) {
    	waitingQueue.remove(fe);
    }
    
    @Override
    public void notifyAvailableElement(ElementInstance fe) {
    	currentQueue.add(fe);
    }
    
}
