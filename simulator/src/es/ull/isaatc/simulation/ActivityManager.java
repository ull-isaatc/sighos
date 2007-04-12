package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

import es.ull.isaatc.util.*;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
 */
public class ActivityManager extends TimeStampedSimulationObject {
    /** Static counter for assigning each new id */
	private static int nextid = 0;
	/** A prioritized table of activities */
	protected NonRemovablePrioritizedTable<Activity> activityTable;
    /** A list of resorce types */
    protected ArrayList<ResourceType> resourceTypeList;
    /** Semaphore for mutual exclusion control */
	private Semaphore sem;
    /** Logical process */
    private LogicalProcess lp;
    /** This queue contains the single flows that are waiting for activities of this AM */
    private SingleFlowQueue sfQueue;
    
   /**
	* Creates a new instance of ActivityManager.
    */
    public ActivityManager(Simulation simul) {
        super(nextid++, simul);
        sem = new Semaphore(1);
        resourceTypeList = new ArrayList<ResourceType>();
        activityTable = new NonRemovablePrioritizedTable<Activity>();
        sfQueue = new SingleFlowQueue();
        simul.add(this);
    }

    /**
     * Returns the logical process where this activity manager is included.
	 * @return Returns the lp.
	 */
	public LogicalProcess getLp() {
		return lp;
	}

	/**
	 * Assigns a logical process to this ctivity manager. 
	 * @param lp The lp to set.
	 */
	protected void setLp(LogicalProcess lp) {
		this.lp = lp;
		lp.add(this);

	}

    /**
     * Adds an activity to this activity manager.
     * @param a Activity added
     */
    protected void add(Activity a) {
        activityTable.add(a);
    }
    
    /**
     * Adds a resource type to this activity manager.
     * @param rt Resource type added
     */
    protected void add(ResourceType rt) {
        resourceTypeList.add(rt);
    }

    /**
     * Adds a single flow to the waiting queue.
     * @param sf Single flow which is added to the waiting queue.
     */
    protected void queueAdd(SingleFlow sf) {
    	sfQueue.add(sf);
    }
    
    /**
     * Removes a single flow from the waiting queue.
     * @param sf single flow which is removed from the waiting queue.
     */
    protected void queueRemove(SingleFlow sf) {
    	sfQueue.remove(sf);
    }
    
	/**
     * Starts a mutual exclusion access to this activity manager.
     */    
    protected void waitSemaphore() {
		debug("MUTEX\trequesting");    	
        try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		debug("MUTEX\tadquired");    	
    }
    
    /**
     * Finishes a mutual exclusion access to this activity manager.
     */    
    protected void signalSemaphore() {
		debug("MUTEX\treleasing");    	
        sem.release();
		debug("MUTEX\tfreed");    	
    }
        
    /**
     * Informs the activities of new available resources. Reviews the queue of waiting single 
     * flows looking for those which can be executed with the new available resources. The 
     * single flows used are removed from the waiting queue.<p>
     * In order not to traverse the whole list of single flows, this method determines the
     * amount of "useless" ones, that is, the amount of single flows belonging to an activity 
     * which can't be performed with the current resources. If this amount is equal to the size
     * of waiting single flows, this method stops. 
     */
    protected void availableResource() {
    	// First marks all the activities as "potentially feasible"
        Iterator<Activity> actIter = activityTable.iterator(NonRemovablePrioritizedTable.IteratorType.FIFO);
        while (actIter.hasNext())
        	actIter.next().resetFeasible();
        // A count of the useless single flows 
    	int uselessSF = 0;
    	// A postponed removal list
    	ArrayList<SingleFlow> toRemove = new ArrayList<SingleFlow>();
    	Iterator<SingleFlow> iter = sfQueue.iterator();
    	while (iter.hasNext() && (uselessSF < sfQueue.size())) {
    		SingleFlow sf = iter.next();
            Element e = sf.getElement();
            Activity act = sf.getActivity();
    		e.debug("MUTEX\trequesting\t" + act + " (av. res.)");    	
            e.waitSemaphore();
    		e.debug("MUTEX\tadquired\t" + act + " (av. res.)");    	
            
    		// The element's timestamp is updated. That's only useful to print messages
            e.setTs(getTs());
            if ((e.getCurrentSF() == null) || act.isNonPresential()) {
            	if (act.isFeasible(sf)) {	// The activity can be performed
                    e.carryOutActivity(sf);
                    // If after carrying out the activity it'll be completely finished 
                    // (only for interruptible activities)
                    if (sf.getTimeLeft() == 0) {
	            		toRemove.add(sf);
	            		uselessSF--;
                    }
            	}
            	else	// The activity can't be performed with the current resources
                	uselessSF += act.getQueueSize();
            }
        	e.debug("MUTEX\treleasing\t" + act + " (av. res.)");    	
        	e.signalSemaphore();
        	e.debug("MUTEX\tfreed\t" + act + " (av. res.)");
		}
    	// Postponed removal
    	for (SingleFlow sf : toRemove)
    		sf.act.queueRemove(sf);
    } 

    /**
     * Returns an iterator over the array containing the single flows which have requested
     * activities belonging to this activity manager.<p>
     * The order followed is: <ol>
     * <li>the single flow's priority (its element type's priority)</li>
     * <li>the activity's priority</li>
     * <li>the arrival order</li>
     * </ol>
     * @return an iterator over the array containing the single flows which have requested
     * activities belonging to this activity manager.
     */
    public Iterator<SingleFlow> getQueueIterator() {
    	return sfQueue.iterator();
    }
    
	public String getObjectTypeIdentifier() {
		return "AM";
	}

	public double getTs() {
		return lp.getTs();
	}

	/**
	 * Builds a detailed description of this activity manager, including activities and 
	 * resource types.
	 * @return A large description of this activity manager.
	 */
	public String getDescription() {
        StringBuffer str = new StringBuffer();
        str.append("Activity Manager " + id + "\r\n(Activity[priority]):");
        Iterator<Activity> iter = activityTable.iterator(NonRemovablePrioritizedTable.IteratorType.FIFO);
        while (iter.hasNext()) {
        	Activity a = iter.next();
            str.append("\t\"" + a + "\"[" + a.getPriority() + "]");
        }
        str.append("\r\nResource Types: ");
        for (ResourceType rt : resourceTypeList)
            str.append("\t\"" + rt + "\"");
        return str.toString();
	}

	/**
	 * A queue which stores the activity requests of the elements. The single flows are
	 * stored by following this order: <ol>
     * <li>the single flow's priority (its element type's priority)</li>
     * <li>the activity's priority</li>
     * <li>the arrival order</li>
     * </ol> 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	protected class SingleFlowQueue {		
		private static final long serialVersionUID = 1L;
		/** The inner structure of the queue */ 
		private TreeMap<Integer, TreeSet<SingleFlow>> table;
		/** Queue size */
		private int nObj = 0;
		/** A counter for the arrival order of the single flows */
		private int arrivalOrder = 0;
		/** A comparator to properly order the single flows. */
		private Comparator<SingleFlow> comp = new Comparator<SingleFlow>() {
			public int compare(SingleFlow o1, SingleFlow o2) {
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
		public SingleFlowQueue() {
			table = new TreeMap<Integer, TreeSet<SingleFlow>>();
		}

		/**
		 * Adds a single flow to the queue. The arrival order and timestamp of the 
		 * single flow are set here. 
		 * @param sf The single flow to be added.
		 */
		public void add(SingleFlow sf) {
			TreeSet<SingleFlow> level = table.get(sf.getPriority());
			if (level == null) {
				level = new TreeSet<SingleFlow>(comp);
				table.put(sf.getPriority(), level);
			}
			sf.setArrivalOrder(arrivalOrder++);
			sf.setArrivalTs(getTs());
			level.add(sf);
			nObj++;
		}
		
		/**
		 * Removes a single flow from the queue.
		 * @param sf the single flow to be removed.
		 */
		public void remove(SingleFlow sf) {
			TreeSet<SingleFlow> level = table.get(sf.getPriority());
			level.remove(sf);
			if (level.isEmpty())
				table.remove(sf.getPriority());
			nObj--;
		}

		/**
		 * Returns an iterator over the 
		 * @return
		 */
		public Iterator<SingleFlow> iterator() {
			return new SingleFlowQueueIterator();
		}
		
		public int size() {
			return nObj;
		}
		
		public String toString() {
			return table.toString();
		}
		
		public class SingleFlowQueueIterator implements Iterator<SingleFlow> {
			Iterator<SingleFlow> inIter = null;
			Iterator<TreeSet<SingleFlow>> outIter;
			
			public SingleFlowQueueIterator() {
				super();
				outIter = table.values().iterator();
				if (outIter.hasNext()) {
					inIter = outIter.next().iterator();
				}
			}

			public boolean hasNext() {
				if (inIter != null)
					if (inIter.hasNext())
						return true;
				return false;
			}

			public SingleFlow next() {
				SingleFlow lastSF = inIter.next();
				if (!inIter.hasNext()) {
					if (outIter.hasNext())
						inIter = outIter.next().iterator();
					else
						inIter = null;
				}
				return lastSF;
			}

			public void remove() {
				throw new UnsupportedOperationException("Can't remove a single flow from the queue");
			}
			
		}
	}
}
