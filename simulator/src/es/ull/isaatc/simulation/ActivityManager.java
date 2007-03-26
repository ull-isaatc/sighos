package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;
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
    protected LogicalProcess lp;
    
   /**
	* Creates a new instance of ActivityManager.
    */
    public ActivityManager(Simulation simul) {
        super(nextid++, simul);
        sem = new Semaphore(1);
        resourceTypeList = new ArrayList<ResourceType>();
        activityTable = new NonRemovablePrioritizedTable<Activity>();
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
	public void setLp(LogicalProcess lp) {
		this.lp = lp;
		lp.add(this);

	}

    /**
     * Adds an activity to this activity manager.
     * @param a Activity added
     */
    public void add(Activity a) {
        activityTable.add(a);
    }
    
    /**
     * Adds a resource type to this activity manager.
     * @param rt Resource type added
     */
    public void add(ResourceType rt) {
        resourceTypeList.add(rt);
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
     * Informs the activities of new available resources. 
     */
    protected void availableResource() {
        Iterator<Activity> iter = activityTable.iterator(NonRemovablePrioritizedTable.IteratorType.RANDOM);
        while (iter.hasNext()) {
        	Activity act = iter.next();
            act.debug("Testing pool activity (availableResource)");
        	boolean activityOK = true;
            while (activityOK) {
	            SingleFlow sf = act.hasPendingElements();
	            if (sf != null) {
	                if (act.isFeasible(sf)) {
	                	act.queueRemove(sf); 
	                    Element e = sf.getElement();
	
	                    e.debug("Can carry out (available resource)\t" + act + "\t" + act.getDescription());
	                    
	                    // Fin Sincronización hasta que el elemento deje de ser accedido
	                    // MOD 26/01/06 Movida esta línea antes del e.sig...
	                    // MOD 23/05/06 Vuelta a poner aquí: ¿POR QUÉ LA MOVI?
	            		e.debug("MUTEX\treleasing\t" + act + " (av. res.)");    	
	                	e.signalSemaphore();
	            		e.debug("MUTEX\tfreed\t" + act + " (av. res.)");    	
	                    e.carryOutActivity(sf);
	                }
	                else {
	                	sf.getElement().debug("MUTEX\treleasing\t" + act + " (av. res.)");    	
	                	sf.getElement().signalSemaphore();
	                	sf.getElement().debug("MUTEX\tfreed\t" + act + " (av. res.)");
	                	activityOK = false;
	                }
	            }
	            else
	            	activityOK = false;
            }
        }
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

}
