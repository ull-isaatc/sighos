package es.ull.iis.simulation.parallel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.engine.ActivityManagerEngine.FlowExecutorQueue;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.PrioritizedMap;

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
    	final ArrayList<FlowExecutor> toRemove = new ArrayList<FlowExecutor>();
    	Iterator<WorkItem> iter = waitingQueue.iterator();
    	while (iter.hasNext() && (uselessSF < waitingQueue.size())) {
    		WorkItem wi = iter.next();
            Element e = wi.getElement();
            Activity act = wi.getBasicStep();
    		// The element's timestamp is updated. That's only useful to print messages
            e.setTs(getTs());
            if (act.mainElementActivity()) {
            	e.waitSemaphore();
                if (e.getCurrent() == null) {
                	if (act.isFeasible(wi)) {	// The activity can be performed
                		e.setCurrent(wi);
                    	e.signalSemaphore();
                        act.carryOut(wi);
                		toRemove.add(wi);
                		uselessSF--;
                	}
                	else {	// The activity can't be performed with the current resources
                    	e.signalSemaphore();
                    	uselessSF += act.getQueueSize();
                	}
                }
                else {
                	e.signalSemaphore();
                }
            }   
            // The activity can be freely accessed by the element
            else {
            	if (act.isFeasible(wi)) {	// The activity can be performed
                    act.carryOut(wi);
            		toRemove.add(wi);
            		uselessSF--;
            	}
            	else {	// The activity can't be performed with the current resources
                	uselessSF += act.getQueueSize();
            	}
            }
		}
    	// Postponed removal
    	for (FlowExecutor fe : toRemove)
    		((RequestResourcesFlow) fe.getCurrentFlow()).queueRemove(fe);
	}
	
    @Override
    public void queueAdd(FlowExecutor fe) {
    	// Synchronized because it can be concurrently accessed by different elements requesting different activities in
    	// this AM
    	synchronized (waitingQueue) {
    		waitingQueue.add(fe);			
		}
    }
    
    @Override
    public void queueRemove(FlowExecutor fe) {
    	waitingQueue.remove(fe);
    }
    
    @Override
    public void notifyAvailableElement(FlowExecutor fe) {
    	synchronized (currentQueue) {
    		currentQueue.add(fe);			
		}
    }

	@Override
	public void processAvailableElements() {
		while (!currentQueue.isEmpty()) {
			final WorkItem wi = requestingElements.poll();
			final Element elem = wi.getElement();
			final Activity act = wi.getBasicStep();
			if (elem.isDebugEnabled())
				elem.debug("Calling availableElement()\t" + act);
			if (act.mainElementActivity()) {
	            elem.waitSemaphore();
				// If the element is not performing a presential activity yet
				if (elem.getCurrent() == null) {
					if (act.isFeasible(wi)) {
						elem.setCurrent(wi);
			        	elem.signalSemaphore();
						act.carryOut(wi);
						act.queueRemove(wi);
					}
					else {
			        	elem.signalSemaphore();
					}
				}
				else {
		        	elem.signalSemaphore();
				}						
			}
			else {
				if (act.isFeasible(wi)) {
					act.carryOut(wi);
					act.queueRemove(wi);
				}
			}
		}
	}

	@Override
	public void setAvailableResource(boolean available) {
		avResource = available;		
	}

	@Override
	public boolean getAvailableResource() {
		return avResource;
	}
	
}
