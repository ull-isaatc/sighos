package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

import es.ull.isaatc.util.PrioritizedMap;

/**
 * Partition of processes. It serves as a mutual exclusion mechanism to access a set of processes
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
 */
public class ProcessManager extends TimeStampedSimulationObject {
    /** Static counter for assigning each new id */
	private static int nextid = 0;
	/** A prioritized table of processes */
	protected ArrayList<Process> processList;
    /** A list of resorce types */
    protected ArrayList<ResourceType> resourceTypeList;
    /** Semaphore for mutual exclusion control */
	private Semaphore sem;
    /** Logical process */
    private LogicalProcess lp;
    /** This queue contains the single flows that are waiting for processes of this AM */
    private SingleFlowQueue sfQueue;
    
   /**
	* Creates a new instance of ProcessManager.
    */
    public ProcessManager(Simulation simul) {
        super(nextid++, simul);
        sem = new Semaphore(1);
        resourceTypeList = new ArrayList<ResourceType>();
        processList = new ArrayList<Process>();
        sfQueue = new SingleFlowQueue();
        simul.add(this);
    }

    /**
     * Returns the logical process where this process manager is included.
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
     * Adds an process to this process manager.
     * @param a process added
     */
    protected void add(Process a) {
        processList.add(a);
    }
    
    /**
     * Adds a resource type to this process manager.
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
     * Starts a mutual exclusion access to this process manager.
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
     * Finishes a mutual exclusion access to this process manager.
     */    
    protected void signalSemaphore() {
		debug("MUTEX\treleasing");    	
        sem.release();
		debug("MUTEX\tfreed");    	
    }
        
    /**
     * Informs the processes of new available resources. Reviews the queue of waiting single 
     * flows looking for those which can be executed with the new available resources. The 
     * single flows used are removed from the waiting queue.<p>
     * In order not to traverse the whole list of single flows, this method determines the
     * amount of "useless" ones, that is, the amount of single flows belonging to an process 
     * which can't be performed with the current resources. If this amount is equal to the size
     * of waiting single flows, this method stops. 
     */
    protected void availableResource() {
    	// First marks all the processes as "potentially feasible"
    	for (Process pro : processList)
        	pro.resetFeasible();
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
            	if (act.isFeasible(sf)) {	// The process can be performed
                    e.carryOutActivity(sf);
            		toRemove.add(sf);
            		uselessSF--;
            	}
            	else	// The process can't be performed with the current resources
                	uselessSF += act.getQueueSize();
            }
        	e.debug("MUTEX\treleasing\t" + act + " (av. res.)");    	
        	e.signalSemaphore();
        	e.debug("MUTEX\tfreed\t" + act + " (av. res.)");
		}
    	// Postponed removal
    	for (SingleFlow sf : toRemove)
    		sf.getActivity().queueRemove(sf);
    } 

    /**
     * Returns an iterator over the array containing the single flows which have requested
     * processes belonging to this process manager.<p>
     * The order followed is: <ol>
     * <li>the single flow's priority (its element type's priority)</li>
     * <li>the process's priority</li>
     * <li>the arrival order</li>
     * </ol>
     * @return an iterator over the array containing the single flows which have requested
     * processes belonging to this process manager.
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
	 * Builds a detailed description of this process manager, including processes and 
	 * resource types.
	 * @return A large description of this process manager.
	 */
	public String getDescription() {
        StringBuffer str = new StringBuffer();
        str.append("Process Manager " + id + "\r\n(Process[priority]):");
        for (Process p : processList)
            str.append("\t\"" + p + "\"[" + p.getPriority() + "]");
        str.append("\r\nResource Types: ");
        for (ResourceType rt : resourceTypeList)
            str.append("\t\"" + rt + "\"");
        return str.toString();
	}

	/**
	 * A queue which stores the process requests of the elements. The single flows are
	 * stored by following this order: <ol>
     * <li>the single flow's priority (its element type's priority)</li>
     * <li>the process's priority</li>
     * <li>the arrival order</li>
     * </ol> 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	protected class SingleFlowQueue extends PrioritizedMap<TreeSet<SingleFlow>, SingleFlow>{		
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
			super();
		}

		/**
		 * Adds a single flow to the queue. The arrival order and timestamp of the 
		 * single flow are set here. 
		 * @param sf The single flow to be added.
		 */
		public void add(SingleFlow sf) {
			// The arrival order and timestamp are only assigned if the single flow 
			// has never been added to the queue (interruptible processes)
			if (Double.isNaN(sf.getArrivalTs())) {
				sf.setArrivalOrder(arrivalOrder++);
				sf.setArrivalTs(getTs());
			}
			super.add(sf);
		}

		@Override
		public TreeSet<SingleFlow> createLevel(Integer priority) {
			return new TreeSet<SingleFlow>(comp);
		}
		
	}
}
