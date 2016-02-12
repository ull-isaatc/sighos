package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the different roles that can be found in the system. The resources can serve for
 * different purposes, and each purpose is a role.
 * TODO Comment
 * @author Carlos Martin Galan
 */
public class ResourceType extends TimeStampedSimulationObject implements es.ull.iis.simulation.core.ResourceType {
    /** Activity manager this resource type belongs to. */
    protected ActivityManager manager;
    /** A list of the currently available resources. */
    protected final ResourceList availableResourceList = new ResourceList();
    /** A brief description of the resource type */
    protected final String description;

    /**
     * Creates a new resource type.
     * @param id Resource type's identifier
     * @param simul Associated simulation
     * @param description A short text describing this resource type.
     */
	public ResourceType(int id, Simulation simul, String description) {
		super(id, simul);
        this.description = description;
        simul.add(this);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Describable#getDescription()
	 */
	public String getDescription() {
		return description;
	}

    /**
     * Returns the activity manager this resource type belongs to.
     * @return Value of property manager.
     */
    public ActivityManager getManager() {
        return manager;
    }
    
    /**
     * Sets the activity manager this resource type belongs to. It also
     * adds this resource type to the manager.
     * @param manager New value of property manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
    /**
     * Returns the resource corresponding to the "ind" position.
     * @param ind Resource position in the availability list. 
     * @return Resource corresponding to the "ind" position".
     */
    protected Resource getResource(int ind) {
        return availableResourceList.get(ind);
    }

    /**
     * Searches the first available resource (a resource which is not being used yet) with 
     * this role. The search starts at position <code>ind</code>.   
     * @param ind Position to start the search.
     * @param wi WorkItem.
     * @return The resource's index or -1 if there are not available resources.
     */
    protected int getNextAvailableResource(int ind, WorkItem wi) {
    	final int total = availableResourceList.size();
    	for (; ind < total; ind++) {
    		final Resource res = availableResourceList.get(ind);
        	if (res.add2Solution(this, wi))
        		return ind;
    	}
    	return -1;
    }
    
    /**
     * Checks if there are enough available resources starting from the ind-th one. 
     * @param ind Index of the first resource to check
     * @param need Total amount of available resources required.
     * @return True if there are more available resources than needed; false in other case.
     */
    protected boolean checkNeeded(int ind, int need) {
    	final int total = availableResourceList.size();
    	if (ind + need > total)
    		return false;
    	int disp = 0;
    	for (int i = ind; (i < total) && (disp < need); i++) {
    		final Resource res = availableResourceList.get(i);
            if ((res.getCurrentWI() == null) && (res.getCurrentResourceType() == null))
                disp++;    		
    	}
    	if (disp < need)
    		return false;
    	return true;
    }
    
    /**
     * Adds a resource as available
     * @param res New available resource.
     */
    protected void incAvailable(Resource res) {
    	debug("Resource added\t" + res);
        availableResourceList.add(res);
		// If the resource was being used in a previous "availability period", it was marked as
        // "timeOut". This mark can be removed.
        if ((res.getCurrentResourceType() == this) && res.isTimeOut())
        	res.setTimeOut(false);
    }
    
    /**
     * Removes a resource from the available list. 
     * @param res New unavailable resource.
     */
    protected void decAvailable(Resource res) {
    	debug("Resource removed\t" + res);
        // If the resource is being used for this resource type, it's marked as "timeOut"
        if (availableResourceList.remove(res) && (res.getCurrentResourceType() == this))
        	res.setTimeOut(true);
    }
    
    @Override
	public String getObjectTypeIdentifier() {
		return "RT";
	}

    @Override
	public long getTs() {
		return manager.getTs();
	}

	/**
	 * Handles the overlap of timetable entries for the same resource, i.e., a resource that has 
	 * several timetable entries at the same time interval with the same resource type. This list 
	 * counts how many times it occurs to avoid incorrect behaviors of the amount of available
	 * resources.
	 * @author Iván Castilla Rodríguez
	 */
	private final static class ResourceList  {
		/** A set of <resource - count> pairs, where the count value indicates how many times a 
		 * resource has been added to this resource type. */
		final private Map<Resource, Integer> tree = new TreeMap<Resource, Integer>();
		/** The list of available resources */
		final private List<Resource> list = new ArrayList<Resource>();

	    /**
	     * Adds a resource. If the resource isn't present in the list, it's included with a "1" count.
	     * If the resource exists already, the count is increased.
	     * @param res The resource added
	     */
	    private synchronized void add(Resource res) {
	    	Integer val = tree.get(res);
	    	if (val == null) {
	    		val = 1;
	    		list.add(res);
	    	}
	    	else
	    		val++;
	    	tree.put(res, val);
	    }
	    
	    /**
	     * Removes a resource. The resource can have more than one appearance in the list. In 
	     * this case, it's no t really removed.
	     * @param res The resource removed.
	     * @return True if the resource is completely removed from the list. False in other case.
	     */
	    private synchronized boolean remove(Resource res) {
	    	Integer val = tree.get(res);
	    	assert val != null : "Unexpected error: Resource not found in resource type";
	    	if (val > 1) {
	    		tree.put(res, val - 1);
	    		return false;
	    	}
	    	tree.remove(res);
	    	list.remove(res);
	    	return true;
	    }
	    
	    /**
	     * Returns the resource at the specified position 
	     * @param index The position of the resource
	     * @return The resource at the specified position.
	     */
	    private Resource get(int index) {
	    	return list.get(index);
	    }
	    
	    private int size() {
	    	return list.size();
	    }
	}
	
	/**
	 * Allows a user to specify an extra waiting time before the RollOn event. Returns a 
	 * long which represents the wait time before the RollOn event can be performed. By default
	 * returns 0.
	 * @return The wait time.
	 */
	public long beforeRoleOn() { return 0; };
	
	/**
	 * Allows a user to specify actions to be performed after the RollOn event.
	 */
	public void afterRoleOn() {};
	
	/**
	 * Allows a user to specify an extra waiting time before the RollOff event. Returns a 
	 * long which represents the wait time before the RollOff event can be performed. By default
	 * returns 0.
	 * @return The wait time.
	 */
	public long beforeRoleOff() { return 0; };
	
	/**
	 * Allows a user to specify actions to be performed after the RollOff event.
	 */
	public void afterRoleOff() {}

	/**
	 * Returns the list of available resources.
	 * @return the list of available resources
	 */
	public ResourceList getAvailableResourceList() {
		return availableResourceList;
	}
} 
