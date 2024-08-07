/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.PrioritizedMap;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityManagerEngine extends EngineObject {
    /** True if there is at least one new resource available the current timestamp */ 
    private boolean availableResource;
    /** This queue contains the flow executors that are waiting for activities of this AM */
    private final FlowExecutorQueue waitingQueue;
    /** This queue contains the element instances that have become available the current timestamp */
    private final FlowExecutorQueue currentQueue;
    /** The associated {@link ActivityManager} */
    private final ActivityManager modelAM;

   /**
	* Creates a new instance of ActivityManager.
	* @param simul ParallelSimulationEngine this activity manager belongs to
    */
    public ActivityManagerEngine(SimulationEngine simul, ActivityManager modelAM) {
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

	/**
     * Informs the activities of new available resources. Reviews the queue of waiting element instances 
     * looking for those which can be executed with the new available resources. The element instances 
     * used are removed from the waiting queue.<p>
     * In order not to traverse the whole list of element instances, this method determines the
     * amount of "useless" ones, that is, the amount of element instances belonging to an activity 
     * which can't be performed with the current resources. If this amount is equal to the size
     * of waiting element instances, this method stops. 
     */
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
            if (!e.isExclusive() || !reqFlow.isInExclusiveActivity()) {
				final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
    			// There are enough resources to perform the activity
    			if (solution != null) {
    				if (reqFlow.isInExclusiveActivity()) 
    					e.setExclusive(true);
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

	/**
	 * Checks if there are new elements available and executes the corresponding actions.
	 * This method centralizes the execution of this code to preserve all the elements and activities priorities.
	 */
	public void processAvailableElements() {
		// Checks if there are pending activities that haven't noticed the
		// element availability
		for (final ElementInstance ei : currentQueue) {
			final Element elem = ei.getElement();
			final RequestResourcesFlow reqFlow = (RequestResourcesFlow) ei.getCurrentFlow();
			if (elem.isDebugEnabled())
				elem.debug("Calling availableElement()\t" + reqFlow + "\t" + reqFlow.getDescription());
			if (!elem.isExclusive()) {
				final ArrayDeque<Resource> solution = reqFlow.isFeasible(ei);
				if (solution != null) {
					if (reqFlow.isInExclusiveActivity()) {
						elem.setExclusive(true);
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

	/**
	 * Notifies the engine that there are new resources available
	 */
	public void notifyAvailableResource() {
		availableResource = true;
	}

	/**
	 * Returns true if there is at least one new resource available the current timestamp
	 * @return true if there is at least one new resource available the current timestamp
	 */
	public boolean getAvailableResource() {
		return availableResource;
	}

    /**
     * Adds an element instance to the waiting queue.
     * @param ei Element instance which is added to the waiting queue.
     */
    public void queueAdd(ElementInstance fe) {
    	waitingQueue.add(fe);
    }

    /**
     * Removes an element instance from the waiting queue.
     * @param ei Element instance which is removed from the waiting queue.
     */
    public void queueRemove(ElementInstance fe) {
    	waitingQueue.remove(fe);
    }
    
    /**
     * Notifies the engine that an element is now available to perform activities
     * @param ei Element instance 
     */
    public void notifyAvailableElement(ElementInstance fe) {
    	currentQueue.add(fe);
    }

	/**
	 * A queue which stores the activity requests of the elements. The element instances are
	 * stored by following this order: <ol>
     * <li>the element instance's priority (its element type's priority)</li>
     * <li>the activity's priority</li>
     * <li>the arrival order</li>
     * </ol> 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public static final class FlowExecutorQueue extends PrioritizedMap<TreeSet<ElementInstance>, ElementInstance>{		
		/** A counter for the arrival order of the single flows */
		private int arrivalOrder = 0;
		/** A comparator to properly order the single flows. */
		private Comparator<ElementInstance> comp = new Comparator<ElementInstance>() {
			public int compare(ElementInstance o1, ElementInstance o2) {
				if (o1.equals(o2))
					return 0;
				if (((RequestResourcesFlow) o1.getCurrentFlow()).getPriority() > ((RequestResourcesFlow) o2.getCurrentFlow()).getPriority())
					return 1;
				if (((RequestResourcesFlow) o1.getCurrentFlow()).getPriority() < ((RequestResourcesFlow) o2.getCurrentFlow()).getPriority())
					return -1;
				if (o1.getArrivalOrder() > o2.getArrivalOrder())
					return 1;
				return -1;
			}			
		};
		
		/**
		 * Creates a new queue.
		 */
		public FlowExecutorQueue() {
			super();
		}

		/**
		 * Adds an element instance to the queue. The arrival order and timestamp of the 
		 * element instance are set here. 
		 * @param ei The element instance to be added.
		 */
		@Override
		public void add(ElementInstance ei) {
			// The arrival order and timestamp are only assigned if the single flow 
			// has never been added to the queue (interruptible activities)
			if (ei.getArrivalTs() == -1) {
				ei.setArrivalOrder(arrivalOrder++);
				ei.setArrivalTs(ei.getElement().getTs());
			}
			super.add(ei);
		}

		@Override
		public TreeSet<ElementInstance> createLevel(Integer priority) {
			return new TreeSet<ElementInstance>(comp);
		}
		
	}

}
