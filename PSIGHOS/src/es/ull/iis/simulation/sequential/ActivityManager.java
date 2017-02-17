package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import es.ull.iis.simulation.core.Describable;
import es.ull.iis.util.PrioritizedMap;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
 */
public class ActivityManager extends TimeStampedSimulationObject implements Describable {
    /** Static counter for assigning each new id */
	private static int nextid = 0;
	/** A prioritized table of activities */
	protected final ArrayList<RequestResources> activityList;
    /** A list of resorce types */
    protected final ArrayList<ResourceType> resourceTypeList;
    /** This queue contains the work threads that are waiting for activities of this AM */
    private final WorkThreadQueue wtQueue;
    
   /**
	* Creates a new instance of ActivityManager.
	* @param simul Simulation this activity manager belongs to
    */
    public ActivityManager(Simulation simul) {
        super(nextid++, simul);
        resourceTypeList = new ArrayList<ResourceType>();
        activityList = new ArrayList<RequestResources>();
        wtQueue = new WorkThreadQueue();
        simul.add(this);
    }

    /**
     * Adds an activity to this activity manager.
     * @param a Activity added
     */
    public void add(RequestResources a) {
        activityList.add(a);
    }
    
    /**
     * Adds a resource type to this activity manager.
     * @param rt Resource type added
     */
    public void add(ResourceType rt) {
        resourceTypeList.add(rt);
    }

    /**
     * Adds a work thread to the waiting queue.
     * @param wt Work thread which is added to the waiting queue.
     */
    public void queueAdd(WorkThread wt) {
    	wtQueue.add(wt);
    }
    
    /**
     * Removes a work thread from the waiting queue.
     * @param wt work thread which is removed from the waiting queue.
     */
    public void queueRemove(WorkThread wt) {
    	wtQueue.remove(wt);
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
    public void availableResource() {
    	// First marks all the activities as "potentially feasible"
    	for (RequestResources act : activityList)
        	act.resetFeasible();
        // A count of the useless single flows 
    	int uselessSF = 0;
    	// A postponed removal list
    	final ArrayList<WorkThread> toRemove = new ArrayList<WorkThread>();
    	final Iterator<WorkThread> iter = wtQueue.iterator();
    	while (iter.hasNext() && (uselessSF < wtQueue.size())) {
    		final WorkThread wt = iter.next();
            // TODO: Check whether it always works fine
            final RequestResources reqResources = (RequestResources) wt.getCurrentFlow();
            
            final int result = wt.availableResource(reqResources);
            if (result == -1) {
        		toRemove.add(wt);
        		uselessSF--;
        	}
        	else if (result > 0) {	// The activity can't be performed with the current resources
            	uselessSF += result;
        	}
		}
    	// Postponed removal to avoid conflict with the activity manager queue
    	for (WorkThread wt : toRemove)
    		((RequestResources) wt.getCurrentFlow()).queueRemove(wt);
    } 

    /**
     * Returns an iterator over the array containing the work threads which have requested
     * activities belonging to this activity manager.<p>
     * The order followed is: <ol>
     * <li>the work thread's priority (its element type's priority)</li>
     * <li>the activity's priority</li>
     * <li>the arrival order</li>
     * </ol>
     * @return an iterator over the array containing the work threads which have requested
     * activities belonging to this activity manager.
     */
    public Iterator<WorkThread> getQueueIterator() {
    	return wtQueue.iterator();
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "AM";
	}

	@Override
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
        for (RequestResources a : activityList)
            str.append("\t\"" + a + "\"[" + a.getPriority() + "]");
        str.append("\r\nResource Types: ");
        for (ResourceType rt : resourceTypeList)
            str.append("\t\"" + rt + "\"");
        return str.toString();
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
	private static final class WorkThreadQueue extends PrioritizedMap<TreeSet<WorkThread>, WorkThread>{		
		/** A counter for the arrival order of the single flows */
		private int arrivalOrder = 0;
		/** A comparator to properly order the single flows. */
		private Comparator<WorkThread> comp = new Comparator<WorkThread>() {
			public int compare(WorkThread o1, WorkThread o2) {
				if (o1.equals(o2))
					return 0;
				if (((RequestResources) o1.getCurrentFlow()).getPriority() > ((RequestResources) o2.getCurrentFlow()).getPriority())
					return 1;
				if (((RequestResources) o1.getCurrentFlow()).getPriority() < ((RequestResources) o2.getCurrentFlow()).getPriority())
					return -1;
				if (o1.getArrivalOrder() > o2.getArrivalOrder())
					return 1;
				return -1;
			}			
		};
		
		/**
		 * Creates a new queue.
		 */
		public WorkThreadQueue() {
			super();
		}

		/**
		 * Adds a work thread to the queue. The arrival order and timestamp of the 
		 * work thread are set here. 
		 * @param wt The work thread to be added.
		 */
		@Override
		public void add(WorkThread wt) {
			// The arrival order and timestamp are only assigned if the single flow 
			// has never been added to the queue (interruptible activities)
			if (wt.getArrivalTs() == -1) {
				wt.setArrivalOrder(arrivalOrder++);
				wt.setArrivalTs(wt.getElement().getTs());
			}
			super.add(wt);
		}

		@Override
		public TreeSet<WorkThread> createLevel(Integer priority) {
			return new TreeSet<WorkThread>(comp);
		}
		
	}
}
