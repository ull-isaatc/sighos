package es.ull.iis.simulation.parallel;

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
 * and a set of resource types. Each Activity Manager (AM) must be controlled by a single thread so to 
 * ensure this mutual exclusion.
 * TODO Comment
 * @author Iván Castilla Rodríguez
 */
public class ActivityManagerEngine extends EngineObject implements es.ull.iis.simulation.model.engine.ActivityManagerEngine {
    /** True if there is at least one new resource available the current timestamp */ 
    private volatile boolean avResource = false;
    /** This queue contains the flow executors that are waiting for activities of this AM */
    private final FlowExecutorQueue waitingQueue;
    /** This queue contains the work threads that have become available the current timestamp */
    private final FlowExecutorQueue currentQueue;
    /** The associated {@link ActivityManager} */
    private final ActivityManager modelAM;
    
   /**
	* Creates a new instance of ActivityManagerEngine.
	* @param simul ParallelSimulationEngine this activity manager belongs to
    */
    public ActivityManagerEngine(ParallelSimulationEngine simul, ActivityManager modelAM) {
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
            final RequestResourcesFlow reqFlow = (RequestResourcesFlow) ei.getCurrentFlow();
            final Element e = ei.getElement();
            final ElementEngine engine = (ElementEngine)e.getEngine();
            if (reqFlow.isExclusive()) {
            	engine.waitSemaphore();
                if (e.getCurrent() == null) {
    				final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
        			// There are enough resources to perform the activity
        			if (solution != null) {
                		e.setCurrent(ei);
                    	engine.signalSemaphore();
        				final long delay = ei.catchResources(solution);
        				if (delay > 0)
        					ei.startDelay(delay);
        				else
        					reqFlow.next(ei);
                		toRemove.add(ei);
                		uselessSF--;
                	}
                	else {	// The activity can't be performed with the current resources
                    	engine.signalSemaphore();
                    	uselessSF += reqFlow.getQueueSize();
                	}
                }
                else {
                	engine.signalSemaphore();
                }
            }   
            // The activity can be freely accessed by the element
            else {
				final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
    			// There are enough resources to perform the activity
    			if (solution != null) {
    				final long delay = ei.catchResources(solution);
    				if (delay > 0)
    					ei.startDelay(delay);
    				else
    					reqFlow.next(ei);
            		toRemove.add(ei);
            		uselessSF--;
            	}
            	else {	// The activity can't be performed with the current resources
                	uselessSF += reqFlow.getQueueSize();
            	}
            }
            // A little optimization to stop if it is detected that no more activities can be performed
            if (uselessSF == queueSize)
            	break;
		}
    	// Postponed removal
    	for (ElementInstance fe : toRemove)
    		((RequestResourcesFlow) fe.getCurrentFlow()).queueRemove(fe);
		// After executing the work there are no more available resources
    	avResource = false;
		// In any case, remove all the pending elements
		currentQueue.clear();
	}
	
    @Override
    public void queueAdd(ElementInstance fe) {
    	// Synchronized because it can be concurrently accessed by different elements requesting different activities in
    	// this AM
    	synchronized (waitingQueue) {
    		waitingQueue.add(fe);			
		}
    }
    
    @Override
    public void queueRemove(ElementInstance fe) {
    	waitingQueue.remove(fe);
    }
    
    @Override
    public void notifyAvailableElement(ElementInstance fe) {
    	synchronized (currentQueue) {
    		currentQueue.add(fe);			
		}
    }

	@Override
	public void processAvailableElements() {
		for (final ElementInstance ei : currentQueue) {
			final Element elem = ei.getElement();			
            final ElementEngine engine = (ElementEngine)elem.getEngine();
			final RequestResourcesFlow reqFlow = (RequestResourcesFlow) ei.getCurrentFlow();
			if (elem.isDebugEnabled())
				elem.debug("Calling availableElement()\t" + reqFlow + "\t" + reqFlow.getDescription());
			if (reqFlow.isExclusive()) {
	            engine.waitSemaphore();
				// If the element is not performing a presential activity yet
				if (elem.getCurrent() == null) {
					final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
	    			// There are enough resources to perform the activity
	    			if (solution != null) {
						elem.setCurrent(ei);
			        	engine.signalSemaphore();
						final long delay = ei.catchResources(solution);
						reqFlow.queueRemove(ei);
						if (delay > 0)
							ei.startDelay(delay);
						else
							reqFlow.next(ei);
					}
					else {
			        	engine.signalSemaphore();					
					}				
				}
				else {
		        	engine.signalSemaphore();					
				}
			}
			else {
				final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
    			// There are enough resources to perform the activity
    			if (solution != null) {
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
		avResource = true;		
	}

	@Override
	public boolean getAvailableResource() {
		return avResource;
	}
	
}
