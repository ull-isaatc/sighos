/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.Comparator;
import java.util.TreeSet;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.PrioritizedMap;

/**
 * The engine of an {@link ActivityManager activity manager}. Useful for implementing different strategies, such as parallel and sequential.
 * @author Iván Castilla
 *
 */
public interface ActivityManagerEngine {
	/**
     * Informs the activities of new available resources. Reviews the queue of waiting element instances 
     * looking for those which can be executed with the new available resources. The element instances 
     * used are removed from the waiting queue.<p>
     * In order not to traverse the whole list of element instances, this method determines the
     * amount of "useless" ones, that is, the amount of element instances belonging to an activity 
     * which can't be performed with the current resources. If this amount is equal to the size
     * of waiting element instances, this method stops. 
     */
	void processAvailableResources();
	
	/**
	 * Checks if there are new elements available and executes the corresponding actions.
	 * This method centralizes the execution of this code to preserve all the elements and activities priorities.
	 */
	void processAvailableElements();
	
	/**
	 * Notifies the engine that there are new resources available
	 */
	void notifyAvailableResource();
	
	/**
	 * Returns true if there is at least one new resource available the current timestamp
	 * @return true if there is at least one new resource available the current timestamp
	 */
	boolean getAvailableResource();

    /**
     * Adds an element instance to the waiting queue.
     * @param ei Element instance which is added to the waiting queue.
     */
    void queueAdd(ElementInstance ei);
    
    /**
     * Removes an element instance from the waiting queue.
     * @param ei Element instance which is removed from the waiting queue.
     */
    void queueRemove(ElementInstance ei);
    
    /**
     * Notifies the engine that an element is now available to perform activities
     * @param ei Element instance 
     */
    void notifyAvailableElement(ElementInstance ei);

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
