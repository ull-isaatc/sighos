/*
 * FlujoSimple.java
 *
 * Created on 17 de junio de 2005, 12:47
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.state.FlowState;
import es.ull.isaatc.simulation.state.SingleFlowState;
import es.ull.isaatc.sync.Semaphore;
import es.ull.isaatc.util.Orderable;
import es.ull.isaatc.util.Output;

/**
 * A single-activity flow. This flow represents a leaf node in the flow tree. 
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends Flow implements Orderable {
	/** Single flows' Counter. Useful for identying each single flow */
	private static int counter = 0;
	/** Single flow's identifier */
	protected int id;
    /** Activity wrapped with this flow */
    protected Activity act;
    /** Indicates if the activity has been already executed */
    protected boolean finished = false;
    /** Workgroup that is currently being used. A null value indicates that this 
    element is not currently performing any activity. */
   protected WorkGroup currentWG = null;
    /** List of caught resources */
    protected ArrayList<Resource> caughtResources;
    // Avoiding deadlocks (time-overlapped resources)
    /** List of conflictive elements */
    protected ConflictZone conflicts;
    /** Stack of nested semaphores */
	protected ArrayList<Semaphore> semStack;
    
    /** 
     * Creates a new parent single flow which wraps an activity. 
     * @param elem Element that executes this flow.
     * @param act Activity wrapped with this flow.
     */
    public SingleFlow(Element elem, Activity act) {
        super(elem);
        this.act = act;
        id = counter++;
        caughtResources = new ArrayList<Resource>();
    }
    
    /** 
     * Creates a new single flow which wraps an activity. 
     * @param parent This flow's parent.
     * @param elem Element that executes this flow.
     * @param act Activity wrapped with this flow.
     */
    public SingleFlow(GroupFlow parent, Element elem, Activity act) {
        super(parent, elem);
        if (parent != null)
        	parent.add(this);
        this.act = act;
        id = counter++;
        caughtResources = new ArrayList<Resource>();
    }

    /**
     * Getter for property act.
     * @return Value of property act.
     */
    public Activity getActivity() {
        return act;
    }
    
    /**
     * Setter for property act.
     * @param act New value of property act.
     */
    public void setActivity(Activity act) {
        this.act = act;
    }

    /**
     * This flow is marked as "finished". 
     * The parent flow is finished too.
     */
    protected ArrayList<SingleFlow> finish() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        finished = true;
        if (parent != null)
            sfList.addAll(parent.finish());
        return sfList;
    }
    
    /**
     * Requests this activity, taking into account the presenciality.
     */
    protected ArrayList<SingleFlow> request() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
    	sfList.add(this);
    	return sfList;
    }
    
    /**
     * Devuelve si la actividad es presencial (requiere de la dedicación 
     * exclusiva del elemento que la solicitó).
     * @return Verdadero (true) si es presencial; Falso (false) si no.
     */
    protected boolean isPresential() {
        if (parent != null)
            if (elem != parent.getElement())
                return true;
        return act.isPresential();
    }
    
    /**
     * Devuelve un array con dos componentes: una de ellas vale 1 y la otra 0. 
     * Si la actividad es presencial, la componente 0 es la que vale 1. Si es 
     * no presencial será la componente 1.
     * @return El número de actividades de este flujo.
     */
    protected int[] countActivities() {
        int [] cont = new int[2];
        if (act.isPresential())
            cont[0]++;
        else
            cont[1]++;
        return cont;
    }

	/**
	 * @return Returns the id.
	 */
	public int getIdentifier() {
		return id;
	}

	/**
	 * @param counter The counter to set.
	 */
	public static void setCounter(int counter) {
		SingleFlow.counter = counter;
	}
	
	/**
	 * 
	 * @return The single flows' counter
	 */
	public static int getCounter() {
		return counter;
	}

	@Override
	protected SingleFlow search(int id) {
		if (this.id == id)
			return this;
		return null;
	}

	@Override
	protected boolean isFinished() {
		return finished;
	}
	
    /**
     * If the element is currently performing this activity, returns the workgroup that the 
     * element is using. If the element is not performing this activity, returns null.
     * @return The current workgroup used by this element.
     */
    protected WorkGroup getCurrentWG() {
        return currentWG;
    }
    
    /**
     * Sets the current workgroup used by this single flow to carry out an activity.
     * @param currentWG The workgroup that the single flow is going to use. A null value indicates
     * that the element is not performing this activity.
     */
    protected void setCurrentWG(WorkGroup currentWG) {
        this.currentWG = currentWG;
    }
    
    /**
     * Returns the list of the elements currently used by the element. 
	 * @return Returns the list of resources caught by this element.
	 */
	protected ArrayList<Resource> getCaughtResources() {
		return caughtResources;
	}

	/**
	 * Adds a resource to the list of resources caught by this element.
	 * @param res A new resource.
	 */
	protected void addCaughtResource(Resource res) {
		caughtResources.add(res);
	}
	
    /**
     * Releases the resources caught by an element.
     * @return A list of activity managers affected by the released resources
     */
    protected ArrayList<ActivityManager> releaseCaughtResources() {
        ArrayList<ActivityManager> amList = new ArrayList<ActivityManager>();
        for (Resource res : caughtResources) {
        	elem.print(Output.MessageType.DEBUG, "Returned " + res);
        	// The resource is freed
        	if (res.releaseResource()) {
        		// The activity managers involved are included in the list
        		for (ActivityManager am : res.getCurrentManagers())
        			if (!amList.contains(am))
        				amList.add(am);
        	}
        }
        caughtResources.clear();
        return amList;
    }

    /**
     * Creates a new conflict zone. This method should be invoked previously to
     * any activity request.
     */
	protected void resetConflictZone() {
        conflicts = new ConflictZone(this);
	}
	
	/**
	 * Establish a different conflict zone for this element.
	 * @param zone The new conflict zone for this element.
	 */
	protected void setConflictZone(ConflictZone zone) {
		conflicts = zone;
	}
	
	/**
	 * Removes this element from its conflict list. This method is invoked in case
	 * the element detects that it can not carry out an activity.
	 */
	protected void removeFromConflictZone() {
		conflicts.remove(this);
	}
	
	/**
	 * Returns the conflict zone of this element.
	 * @return The conflict zone of the element.
	 */
	protected ConflictZone getConflictZone() {
		return conflicts;
	}
	
	/**
	 * Merges the conflict list of this element and other one. Since one zonflict zone must
	 * be merged into the other, the election of the element which "recibes" the merging operation
	 * depends on the id of the element: the element with lower id "recibes" the merging, and the
	 * other one "produces" the operation.
	 * @param e The element whose conflict zone must be merged. 
	 */
	protected void mergeConflictList(SingleFlow sf) {
		// If it's the same list there's no need of merge
		if (conflicts != sf.getConflictZone()) {
			int result = this.compareTo(sf); 
			if (result < 0)
				conflicts.merge(sf.getConflictZone());
			else if (result > 0)
				sf.getConflictZone().merge(conflicts);
		}
	}
	
	/**
	 * Obtains the stack of semaphores from the conflict zone and goes through
	 * this stack performing a wait operation on each semaphore.
	 */
	protected void waitConflictSemaphore() {
		semStack = conflicts.getSemaphores();
		for (Semaphore sem : semStack)
			sem.waitSemaphore();
	}
	
	/** 
	 * Goes through the stack of semaphores performing a signal operation on each semaphore.
	 */
	protected void signalConflictSemaphore() {
		for (Semaphore sem : semStack)
			sem.signalSemaphore();
	}

	public FlowState getState() {
		SingleFlowState state = null;
		if (currentWG == null)
			state = new SingleFlowState(id, act.getIdentifier(), finished);
		else {
			state = new SingleFlowState(id, act.getIdentifier(), finished, currentWG.getIdentifier());
			for(Resource r : caughtResources)
				state.add(r.getIdentifier());
		}		 
		return state;
	}

	public void setState(FlowState state) {
		SingleFlowState sfState = (SingleFlowState)state;
		finished = sfState.isFinished();
		id = sfState.getFlowId();
		if (sfState.getCurrentWGId() != -1) {
			currentWG = act.getWorkGroup(sfState.getCurrentWGId());
			for (Integer rId : sfState.getCaughtResources())
				caughtResources.add(act.getSimul().getResourceList().get(rId));
		}		
	}

	public Comparable getKey() {
		return new Integer(id);
	}

	public int compareTo(Orderable obj) {
		return compareTo(obj.getKey());		
	}

	public int compareTo(Object o) {
		return getKey().compareTo(o);		
	}
    
	
}
