/*
 * GestorActividades.java
 *
 * Created on 22 de junio de 2004, 9:34
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.state.ActivityManagerState;
import es.ull.isaatc.simulation.state.ActivityState;
import es.ull.isaatc.simulation.state.ResourceTypeState;
import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.sync.Semaphore;
import es.ull.isaatc.util.*;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
 */
public class ActivityManager extends SimulationObject implements RecoverableState<ActivityManagerState> {
    /** Static counter for assigning each new id */
	private static int nextid = 0;
	/** A prioritized table of activities */
	protected PrioritizedTable<Activity> activityTable;
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
        activityTable = new PrioritizedTable<Activity>();
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
        sem.waitSemaphore();
    }
    
    /**
     * Finishes a mutual exclusion access to this activity manager.
     */    
    protected void signalSemaphore() {
        sem.signalSemaphore();
    }
        
    /**
     * Informs the activities of new available resources. 
     */
    protected void availableResource() {
        Iterator<Activity> iter = activityTable.iterator(true);
        while (iter.hasNext()) {
        	Activity act = iter.next();
            act.print(Output.MessageType.DEBUG, "Testing pool activity (availableResource)");
            if (act.hasPendingElements()) {
                if (act.isFeasible(act.queueGet(0))) {
                	SingleFlow flow = act.queueRemove(); 
                    Element e = flow.getElement();

                    e.print(Output.MessageType.DEBUG, "Can carry out (available resource)\t" + act, 
                    		"Can carry out (available resource)\t" + act + "\t" + act.getDescription());
                    
                    // Fin Sincronización hasta que el elemento deje de ser accedido
                    // MOD 26/01/06 Movida esta línea antes del e.sig...
                    // MOD 23/05/06 Vuelta a poner aquí: ¿POR QUÉ LA MOVI?
                    e.signalSemaphore();
                    e.carryOutActivity(flow);
                }
                else
                    act.queueGet(0).getElement().signalSemaphore();
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
        Prioritizable actividades[] = activityTable.toArray();
        for (int i = 0; i < actividades.length; i++) {
            Activity a = (Activity)actividades[i];
            str.append("\t\"" + a + "\"[" + a.getPriority() + "]");
        }
        str.append("\r\nResource Types: ");
        for (ResourceType rt : resourceTypeList)
            str.append("\t\"" + rt + "\"");
        return str.toString();
	}

	public ActivityManagerState getState() {
		ActivityManagerState amState = new ActivityManagerState(id);
        Iterator<Activity> iter = activityTable.iterator(false);
        while (iter.hasNext())
        	amState.add(iter.next().getState());
        for (ResourceType rt : resourceTypeList)
        	amState.add(rt.getState());
		return amState;
	}

	public void setState(ActivityManagerState state) {
		for (ActivityState aState : state.getAStates()) {
			Activity act = simul.getActivity(aState.getActId());
			act.setManager(this);
			act.setState(aState);			
		}
		for (ResourceTypeState rtState : state.getRtStates()) {
			ResourceType rt = simul.getResourceType(rtState.getRtId());
			rt.setManager(this);
			rt.setState(rtState);
		}
	}

}
