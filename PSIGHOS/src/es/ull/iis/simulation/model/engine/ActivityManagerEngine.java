/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.Comparator;
import java.util.TreeSet;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.PrioritizedMap;

/**
 * @author Iván Castilla
 *
 */
public interface ActivityManagerEngine {
	/**
     * Informs the activities of new available resources. Reviews the queue of waiting work items 
     * looking for those which can be executed with the new available resources. The work items 
     * used are removed from the waiting queue.<p>
     * In order not to traverse the whole list of work items, this method determines the
     * amount of "useless" ones, that is, the amount of work items belonging to an activity 
     * which can't be performed with the current resources. If this amount is equal to the size
     * of waiting work items, this method stops. 
     */
	void processAvailableResources();
	/**
	 * Checks if there are new elements available and executes the corresponding actions.
	 * This method centralizes the execution of this code to preserve all the elements and activities priorities.
	 */
	void processAvailableElements();
	
	void notifyAvailableResource();
	
	boolean getAvailableResource();

    /**
     * Adds a work thread to the waiting queue.
     * @param fe Work thread which is added to the waiting queue.
     */
    void queueAdd(ElementInstance fe);
    
    /**
     * Removes a work thread from the waiting queue.
     * @param fe work thread which is removed from the waiting queue.
     */
    void queueRemove(ElementInstance fe);
    
    void notifyAvailableElement(ElementInstance fe);

	/**
	 * A queue which stores the activity requests of the elements. The flow executors are
	 * stored by following this order: <ol>
     * <li>the flow executor's priority (its element type's priority)</li>
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
		 * Adds a work thread to the queue. The arrival order and timestamp of the 
		 * work thread are set here. 
		 * @param wt The work thread to be added.
		 */
		@Override
		public void add(ElementInstance wt) {
			// The arrival order and timestamp are only assigned if the single flow 
			// has never been added to the queue (interruptible activities)
			if (wt.getArrivalTs() == -1) {
				wt.setArrivalOrder(arrivalOrder++);
				wt.setArrivalTs(wt.getElement().getTs());
			}
			super.add(wt);
		}

		@Override
		public TreeSet<ElementInstance> createLevel(Integer priority) {
			return new TreeSet<ElementInstance>(comp);
		}
		
	}

}
