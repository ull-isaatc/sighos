/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayDeque;

import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.location.Location;

/**
 * The type of a resource. Defines roles or specializations of the resources.<p>
 * A user can define customized code associated to different events where this class
 * is involved:
 * <ul>
 * <li>When a resource is activated for this type: {@link #beforeRoleOn()}, {@link #afterRoleOn()}</li>
 * <li>When a resource is deactivated for this type: {@link #beforeRoleOff()}, {@link #afterRoleOff()}</li>
 * </ul>
 * @author Iv�n Castilla Rodr�guez
 * @author Carlos Mart�n Gal�n
 */
public class ResourceType extends SimulationObject implements Describable {
    /** A list of the currently available resources. */
    protected ResourceList availableResourceList = null;
    /** A brief description of the resource type */
    protected final String description;
    /** Activity manager this resource type belongs to. */
    protected ActivityManager manager;

	/**
	 * Creates a resource type
	 * @param model The simulation model this resource type belongs to
	 * @param description A brief description of the resource type
	 */
	public ResourceType(Simulation model, String description) {
		super(model, model.getResourceTypeList().size(), "RT");
		this.description = description;
		model.add(this);
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		availableResourceList = simul.getResourceListInstance();
	}
	
	@Override
	public String getDescription() {
		return description;
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
	 * @param solution Tentative solution with booked resources
     * @param ind Position to start the search.
     * @param ei Element instance requesting the resources
     * @return The resource's index or -1 if there are not available resources.
     */
    protected int getNextAvailableResource(ArrayDeque<Resource> solution, int ind, ElementInstance ei) {
    	final int total = availableResourceList.size();
        for (; ind < total; ind++) {
        	if (availableResourceList.get(ind).add2Solution(solution, this, ei)) {
            	return ind;
            }
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
            if ((!res.isSeized()) && (res.getCurrentResourceType() == null))
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

    /**
     * Notifies the activity managers that a resource is available for this resource type
     */
    public void notifyResource() {
    	manager.notifyResource();    	
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
	 * Obtain the available resources for this type.
	 * @return The available resources.
	 */
	public int getAvailableResources() {
		int counter = 0;
		
		for(Resource res: availableResourceList.getResources())
			if (res.isAvailable(this))
				counter++;
		return counter;
	}
    
	// User methods
	
	/**
	 * Allows a user for adding customized code before a resource is activated for this type. If this
	 * method returns a value higher than 0, the activation of the resource is delayed such returned value.
	 * @return The delay in activating the resource. No delay is applied if 0 
	 */
	public long beforeRoleOn() {
		return 0;
	}
	
	/**
	 * Allows a user for adding customized code after a resource is activated for this type. 
	 */
	public void afterRoleOn() {	
	}
	
	/**
	 * Allows a user for adding customized code before a resource is deactivated for this type. If this
	 * method returns a value higher than 0, the deactivation of the resource is delayed such returned value.
	 * @return The delay in deactivating the resource. No delay is applied if 0 
	 */
	public long beforeRoleOff() {
		return 0;
	}
	
	/**
	 * Allows a user for adding customized code after a resource is deactivated for this type. 
	 */
	public void afterRoleOff() {
	}

	// End of user methods
	
	/**
	 * Adds n resources of type {@link ResourceType}. This method is useful when you simply want to create a
	 * set of resources that are available all the time as {@link ResourceType}.
	 * @param n Number of generic resources to create.
	 * @return The set of resources created.
	 */
	public Resource[] addGenericResources(int n) {
		final Resource[] res = new Resource[n];
		for (int i = 0; i < n; i++) {
			res[i] = new Resource(simul, description + " " + i);
			res[i].newTimeTableOrCancelEntriesAdder(this).addTimeTableEntry();
		}
		return res;
	}

	/**
	 * Adds n resources of type {@link ResourceType}. This method is useful when you simply want to create a
	 * set of resources that are available all the time as {@link ResourceType}. It also initializes the location of the 
	 * resources
	 * @param n Number of generic resources to create.
	 * @param size Size of the created resources
	 * @param initLocation Initial location of the created resources
	 * @return The set of resources created.
	 */
	public Resource[] addGenericResources(int n, int size, Location initLocation) {
		final Resource[] res = new Resource[n];
		for (int i = 0; i < n; i++) {
			res[i] = new Resource(simul, description + " " + i, size, initLocation);
			res[i].newTimeTableOrCancelEntriesAdder(this).addTimeTableEntry();
		}
		return res;
	}

	/**
	 * Returns the list of available resources.
	 * @return the list of available resources
	 */
	public ResourceList getAvailableResourceList() {
		return availableResourceList;
	}

	
}
