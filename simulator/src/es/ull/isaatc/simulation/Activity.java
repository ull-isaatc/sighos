package es.ull.isaatc.simulation;

import java.util.Iterator;
import java.util.Vector;

import es.ull.isaatc.random.RandomNumber;
import es.ull.isaatc.simulation.results.ActivityStatistics;
import es.ull.isaatc.util.*;

/**
 * A task which could be carry out by an element. An activity is characterized by its priority,
 * presentiality, and a set of workgropus. Each workgroup represents a combination of resource 
 * types required for carrying out the activity, and the duration of the activity when performed
 * with this workgroup. 
 * @author Carlos Martín Galán
 */
public class Activity extends DescSimulationObject implements Prioritizable {
    /** Priority of the activity */
    protected int priority = 0;
    /** Indicates if the activity is presential (an element carrying out this activity could
     * not make anything else) or not presential (the element could perform other activities at
     * the same time) */
    protected boolean presential = true;
    /** This queue contains the single flows that are waiting for this activity */
    protected Vector<SingleFlow> elementQueue;
    /** Activity manager which this activity is associated to */
    protected ActivityManager manager = null;
    /** Work Group Pool */
    protected PrioritizedTable<WorkGroup> workGroupTable;

    // constructores
    /**
     * Creates a new activity.
     * @param id Activity's identifier
     * @param simul Associated simulation
     * @param description Activity's description
     */
    public Activity(int id, Simulation simul, String description) {
        this(id, simul, description, 0, true);
    }

    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description Activity's description
     * @param priority Activity's priority.
     */
    public Activity(int id, Simulation simul, String description, int priority) {
        this(id, simul, description, priority, true);
    }
    
    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description Activity's description
     * @param priority Activity's priority.
     * @param presential Indicates if the activity requires the presence of an element to be carried out. 
     */
    public Activity(int id, Simulation simul, String description, int priority, boolean presential) {
        super(id, simul, description);
        this.priority = priority;
        this.presential = presential;
        elementQueue = new Vector<SingleFlow>();
        workGroupTable = new PrioritizedTable<WorkGroup>();
    }

    /**
     * Indicates if the activity requires the presence of the element in order to be carried out. 
     * @return The "presenciality" of the activity.
     */
    public boolean isPresential() {
        return presential;
    }
    
    /**
     * Getter for property prioridad.
     * @return Value of property prioridad.
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Returns the activity manager which this activity belongs to.
     * @return The activity manager which this activity belongs to.
     */
    public ActivityManager getManager() {
        return manager;
    }

    /**
     * Associates this activity to an activity manager.
     * @param manager The activity manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
    /**
     * Creates a new workgroup for this activity. The workgroup is added and returned in order
     * to be used.
     * @param wgId The workgroup's identifier
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @return A new workgroup.
     */
    public WorkGroup getNewWorkGroup(int wgId, RandomNumber duration, int priority) {
    	WorkGroup wg = new WorkGroup(wgId, this, duration, priority);
        workGroupTable.add(wg);
        return wg;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. The workgroup 
     * is added and returned in order to be used.
     * @param wgId The workgroup's identifier
     * @param duration Duration of the activity when performed with the new workgroup
     * @return A new workgroup.
     */
    public WorkGroup getNewWorkGroup(int wgId, RandomNumber duration) {    	
        return getNewWorkGroup(wgId, duration, 0);
    }

    /**
     * Returns an array containing the whole set of workgroups of this activity.
     * @return The workgroups that can perform this activity.
     */
    public Prioritizable[] getWorkGroupTable() {
        return (Prioritizable[])workGroupTable.toArray();    	
    }

	/**
     * Checks if this activity can be performed with any of its workgroups.
     * @param e Element trying to carry out the activity 
     * @return "True" if the activity is feasible, "false" in other case.
     */
    protected boolean isFeasible(Element e) {
    	// FIXME Debería ser aleatorio
        Iterator<WorkGroup> iter = workGroupTable.iterator(false);
        while (iter.hasNext()) {
        	WorkGroup opc = iter.next();
            if (opc.isFeasible(e)) {
                e.setCurrentWG(opc);
                return true;
            }            
        }
        return false;
    }

	/**
	 * Checks if there are "valid" elements waiting for this activity in the activity queue. 
	 * If the first element is valid, there is nothing else to do, in other case, the first valid
	 * element is put at the head of the queue. An element is valid when it's not busy carrying out 
	 * another activity.
	 * @return "True" if there are pending elements; "false" in other case
	 */
    protected boolean hasPendingElements() {
        // Pending list empty
        if (elementQueue.isEmpty())
            return false;
        
        // Checking the first element
        SingleFlow flow = elementQueue.get(0);
        Element e = flow.getElement();
        e.waitSemaphore();
        
        // MOD 26/01/06 Añadido
        e.setTs(getTs());

        if (e.getCurrentWG() == null)
            return true;
        else 
            e.signalSemaphore();
        
        // Continue with the rest of elements
        for (int i = 1; i < elementQueue.size(); i++) {
        	flow = elementQueue.get(i);
            e = flow.getElement();
            e.waitSemaphore();
			if (e.getCurrentWG() == null) {
			    // The element is put at the head of the queue
				flow = elementQueue.remove(i);
			    elementQueue.add(0, flow);
			    return true;
			}
			else 
			    e.signalSemaphore();
        }
        return false;
    }

    /**
     * Add a single flow to the element queue.
     * @param flow Single Flow added
     */
    protected void addElement(SingleFlow flow) {
        elementQueue.add(flow);
    }
    
    /**
     * Remove the first single flow from the element queue.
     * @return The first singler flow of the element queue
     */
    protected SingleFlow removeElement() {
        return elementQueue.remove(0);
    }

    /**
     * Remove a specific single flow from the element queue.
     * @param flow Single flow that must be removed from the element queue.
     * @return True if the flow belongs to the queue; false in other case.
     */
    protected boolean removeElement(SingleFlow flow) {
        return elementQueue.remove(flow);
    }
    
    /**
     * Returns a specific element of the element queue.
     * @param ind Element's index
     * @return The element corresponding to the ind position.
     */
    protected Element getElement(int ind) {
        if (ind < 0 || ind >= elementQueue.size())
            return null;
        return elementQueue.get(ind).getElement();
    }

	/**
	 * Clears the element queue. This method is invoked from the logical process when the
	 * simulation finishes.
	 */
    protected void clearQueue() {
    	for (SingleFlow sf : elementQueue) {
            simul.addStatistic(new ActivityStatistics(this.id, sf.getId(), sf.getElement().getIdentifier()));
            sf.getElement().notifyEndSimulation();
        }
        elementQueue.clear();
    } 

	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	public double getTs() {
		return manager.getTs();
	}

} // fin Actividad
