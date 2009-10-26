/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.TreeMap;

import es.ull.isaatc.simulation.Describable;
import es.ull.isaatc.simulation.Identifiable;
import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.condition.TrueCondition;
import es.ull.isaatc.util.Prioritizable;

/**
 * A task which could be carried out by an element. An activity is characterized by its priority
 * and a set of workgropus. Each workgroup represents a combination of resource types required 
 * for carrying out the activity.<p>

 * @author Iván Castilla Rodríguez
 *
 */
public class Activity extends VariableStoreModelObject implements Prioritizable, Describable {
    /** Priority. The lowest the value, the highest the priority */
    protected int priority = 0;
    /** A brief description of the activity */
    protected final String description;
    /** Work Groups available to perform this activity */
    protected final TreeMap<Integer, ActivityWorkGroup> workGroupTable;
    int wgCounter = 0;

	/**
     * Creates a new activity with 0 priority.
     * @param id Activity's identifier
     * @param model Model which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public Activity(int id, Model model, String description) {
        this(id, model, description, 0);
    }

    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param model Model which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public Activity(int id, Model model, String description, int priority) {
        super(id, model);
        this.description = description;
        this.priority = priority;
        workGroupTable = new TreeMap<Integer, ActivityWorkGroup>();
        model.add(this);
    }

    public boolean isInterruptible() {
    	return false;
    }    
    
    /*
     * (non-Javadoc)
     * @see es.ull.isaatc.simulation.Describable#getDescription()
     */
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	/**
     * Returns the activity's priority.
     * @return Value of the activity's priority.
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Creates a new workgroup for this activity using the specified wg.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.model.WorkGroup wg) {
        workGroupTable.put(wgCounter, new ActivityWorkGroup(wgCounter, priority, wg));
        return wgCounter++;
    }
    
    /**
     * Creates a new workgroup for this activity using the specified wg. This workgroup
     * is only available if cond is true.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {
        workGroupTable.put(wgCounter, new ActivityWorkGroup(wgCounter, priority, wg, cond));
        return wgCounter++;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(es.ull.isaatc.simulation.model.WorkGroup wg) {    	
        return addWorkGroup(0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg. This workgroup is only available if cond is true.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(0, wg, cond);
    }

    /**
     * Creates a new empty workgroup for this activity. 
     * @param priority Priority of the workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority) {
        workGroupTable.put(wgCounter, new ActivityWorkGroup(wgCounter, priority));
        return wgCounter++;
    }
    
    /**
     * Creates a new empty workgroup for this activity. This workgroup is only available 
     * if cond is true.
     * @param priority Priority of the workgroup
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, Condition cond) {
        workGroupTable.put(wgCounter, new ActivityWorkGroup(wgCounter, priority, cond));
        return wgCounter++;
    }
    
    /**
     * Creates a new empty workgroup for this activity with the highest level of priority. 
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup() {    	
        return addWorkGroup(0);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. This 
     * workgroup is only available if cond is true.
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(Condition cond) {    	
        return addWorkGroup(0, cond);
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
    	return workGroupTable.get(wgId);
    }
    
    /**
     * Adds the corresponding resource type and number to the workgroup with the specified id
     * @param wgId The id of the workgroup searched
     * @param rt Resource Type
     * @param needed Needed units
     * @return True if the specified id corresponds to an existent WG; false in other case.
     */
    public boolean addWorkGroupEntry(int wgId, ResourceType rt, int needed ) {
    	ActivityWorkGroup wg = getWorkGroup(wgId);
    	if (wg != null) {
    		wg.add(rt, needed);
    		return true;
    	}
        return false;
    }

	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, a condition which determines if the 
	 * workgroup can be used or not, and the priority of the workgroup inside the activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends es.ull.isaatc.simulation.model.WorkGroup implements Identifiable, Describable {
	    /** Workgroup's identifier */
		protected int id;
		/** Priority of the workgroup */
	    protected int priority = 0;
	    /** Availability condition */
	    protected Condition cond;
	    private final String idString; 
		
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param priority Priority of the workgroup.
	     */    
	    protected ActivityWorkGroup(int id, int priority) {
	        this(id, priority, new TrueCondition());
	    }
	    
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param priority Priority of the workgroup.
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, int priority, Condition cond) {
	        super();
	        this.id = id;
	        this.priority = priority;
	        this.cond = cond;
	        this.idString = new String("(" + Activity.this + ")" + getDescription());
	    }

	    /**
	     * Creates a new instance of WorkGroup which contains the same resource types
	     * than an already existing one.
	     * @param id Identifier of this workgroup.
	     * @param priority Priority of the workgroup.
	     * @param wg The original workgroup
	     */    
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.model.WorkGroup wg) {
	        this(id, priority, wg, new TrueCondition());
	    }
	    
	    /**
	     * Creates a new instance of WorkGroup which contains the same resource types
	     * than an already existing one.
	     * @param id Identifier of this workgroup.
	     * @param priority Priority of the workgroup.
	     * @param wg The original workgroup
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {
	        this(id, priority, cond);
	        this.resourceTypeTable.addAll(wg.resourceTypeTable);
	    }


	    /**
	     * Returns the activity this WG belongs to.
	     * @return Activity this WG belongs to.
	     */    
	    protected Activity getActivity() {
	        return Activity.this;
	    }
	    
	    /**
	     * Getter for property priority.
	     * @return Value of property priority.
	     */
	    public int getPriority() {
	        return priority;
	    }

	    public int getIdentifier() {
			return id;
		}

		public String getDescription() {
			StringBuilder str = new StringBuilder("WG" + id);
			for (ResourceTypeTableEntry rtte : resourceTypeTable)
				str.append(" [" + rtte.getResourceType() + "," + rtte.getNeeded() + "]");
			return str.toString();
		}

	    @Override
	    public String toString() {
	    	return idString;
	    }
	}
    
}
