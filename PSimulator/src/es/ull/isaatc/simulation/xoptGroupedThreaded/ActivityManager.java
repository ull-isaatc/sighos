package es.ull.isaatc.simulation.xoptGroupedThreaded;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import es.ull.isaatc.simulation.common.Describable;
import es.ull.isaatc.util.PrioritizedMap;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. Each Activity Manager (AM) must be controlled by a single thread so to 
 * ensure this mutual exclusion.
 * TODO Comment
 * @author Iván Castilla Rodríguez
 */
public class ActivityManager extends TimeStampedSimulationObject implements Describable {
    /** Static counter for assigning each new identifier */
	private static int nextid = 0;
	/** A prioritized table of the activities handled from this AM */
	protected final ArrayList<Activity> activityList = new ArrayList<Activity>();
    /** The list of resource types handled from this AM */
    protected final ArrayList<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
    /** A queue containing the work items that are waiting for activities of this AM */
    private final WorkItemQueue wiQueue = new WorkItemQueue();
    /** */  
    private final ArrayDeque<WorkItem> requestingElements = new ArrayDeque<WorkItem>();
    private volatile boolean avResource = false;
    
   /**
	* Creates a new instance of ActivityManager.
	* @param simul Simulation this activity manager belongs to
    */
    public ActivityManager(Simulation simul) {
        super(nextid++, simul);
        simul.add(this);
    }

    /**
     * Adds an activity to this activity manager.
     * @param a Activity added
     */
    protected void add(Activity a) {
        activityList.add(a);
    }
    
    /**
     * Adds a resource type to this activity manager.
     * @param rt Resource type added
     */
    protected void add(ResourceType rt) {
        resourceTypeList.add(rt);
    }

    /**
     * Adds a work item to the waiting queue.
     * @param wi Work item which is added to the waiting queue.
     */
    protected void queueAdd(WorkItem wi) {
    	wiQueue.add(wi);
    }
    
    /**
     * Removes a work item from the waiting queue.
     * @param wi work item which is removed from the waiting queue.
     */
    protected void queueRemove(WorkItem wi) {
    	wiQueue.remove(wi);
    }
    
    protected void notifyElement(WorkItem wi) {
    	requestingElements.add(wi);
    }
    
    protected void notifyResource() {
    	avResource = true;
    }
        
    /**
     * Informs the activities of new available resources. Reviews the queue of waiting work items 
     * looking for those which can be executed with the new available resources. The work items 
     * used are removed from the waiting queue.<p>
     * In order not to traverse the whole list of work items, this method determines the
     * amount of "useless" ones, that is, the amount of work items belonging to an activity 
     * which can't be performed with the current resources. If this amount is equal to the size
     * of waiting work items, this method stops. 
     */
    protected void availableResource() {
    	// First marks all the activities as "potentially feasible"
    	for (Activity act : activityList)
        	act.resetFeasible();
        // A count of the useless single flows 
    	int uselessSF = 0;
    	// A postponed removal list
    	ArrayList<WorkItem> toRemove = new ArrayList<WorkItem>();
    	Iterator<WorkItem> iter = wiQueue.iterator();
    	while (iter.hasNext() && (uselessSF < wiQueue.size())) {
    		WorkItem wi = iter.next();
            Element e = wi.getElement();
            Activity act = wi.getActivity();
            e.waitSemaphore();
            
    		// The element's timestamp is updated. That's only useful to print messages
            e.setTs(getTs());
            if (act.validElement(wi)) {
            	if (act.isFeasible(wi)) {	// The activity can be performed
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
    	// Postponed removal
    	for (WorkItem sf : toRemove)
    		sf.getActivity().queueRemove(sf);
    } 

    /**
     * Returns an iterator over the array containing the work items which have requested
     * activities belonging to this activity manager.<p>
     * The order followed is: <ol>
     * <li>the work item's priority (its element type's priority)</li>
     * <li>the activity's priority</li>
     * <li>the arrival order</li>
     * </ol>
     * @return an iterator over the array containing the work items which have requested
     * activities belonging to this activity manager.
     */
    public Iterator<WorkItem> getQueueIterator() {
    	return wiQueue.iterator();
    }
    
	public String getObjectTypeIdentifier() {
		return "AM";
	}

	public long getTs() {
		return simul.getTs();
	}

	/**
	 * Builds a detailed description of this activity manager, including activities and 
	 * resource types.
	 * @return A large description of this activity manager.
	 */
	public String getDescription() {
        StringBuffer str = new StringBuffer();
        str.append("Activity Manager " + id + "\r\n(Activity[priority]):");
        for (Activity a : activityList)
            str.append("\t\"" + a + "\"[" + a.getPriority() + "]");
        str.append("\r\nResource Types: ");
        for (ResourceType rt : resourceTypeList)
            str.append("\t\"" + rt + "\"");
        return str.toString();
	}


	public void executeWork() {
		if (!requestingElements.isEmpty() || avResource) {
			if (avResource) {
				availableResource();
				avResource = false;
				requestingElements.clear();
			}
			else {
				while (!requestingElements.isEmpty()) {
					WorkItem wi = requestingElements.poll();
					Element elem = wi.getElement();
					Activity act = wi.getActivity();
		            elem.waitSemaphore();
					if (isDebugEnabled())
						debug("Calling availableElement()\t" + act + "\t" + act.getDescription());
					// If the element is not performing a presential activity yet
					if (elem.getCurrent() == null) {
						if (act.isFeasible(wi)) {
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
			}
		}
	}
	
	/**
	 * A queue which stores the activity requests of the elements. The work items are
	 * stored by following this order: <ol>
     * <li>the work item's priority (its element type's priority)</li>
     * <li>the activity's priority</li>
     * <li>the arrival order</li>
     * </ol> 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	protected class WorkItemQueue extends PrioritizedMap<TreeSet<WorkItem>, WorkItem>{		
		/** A counter for the arrival order of the single flows */
		private int arrivalOrder = 0;
		/** A comparator to properly order the single flows. */
		private Comparator<WorkItem> comp = new Comparator<WorkItem>() {
			public int compare(WorkItem o1, WorkItem o2) {
				if (o1.equals(o2))
					return 0;
				if (o1.getActivity().getPriority() > o2.getActivity().getPriority())
					return 1;
				if (o1.getActivity().getPriority() < o2.getActivity().getPriority())
					return -1;
				if (o1.getArrivalOrder() > o2.getArrivalOrder())
					return 1;
				return -1;
			}			
		};
		
		/**
		 * Creates a new queue.
		 */
		public WorkItemQueue() {
			super();
		}

		/**
		 * Adds a work item to the queue. The arrival order and timestamp of the 
		 * work item are set here. 
		 * @param wi The work item to be added.
		 */
		public void add(WorkItem wi) {
			// The arrival order and timestamp are only assigned if the single flow 
			// has never been added to the queue (interruptible activities)
			if (wi.getArrivalTs() == -1) {
				wi.setArrivalOrder(arrivalOrder++);
				wi.setArrivalTs(getTs());
			}
			super.add(wi);
		}

		@Override
		public TreeSet<WorkItem> createLevel(Integer priority) {
			return new TreeSet<WorkItem>(comp);
		}
		
	}
}
