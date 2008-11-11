package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.simulation.state.ResourceTypeState;

/**
 * Represents the different roles that can be found in the system. The resources can serve for
 * different purposes, and each purpose is a role.
 * @author Carlos Martin Galan
 */
public class ResourceType extends TimeStampedSimulationObject implements RecoverableState<ResourceTypeState>, Describable {
    /** Activity manager this resource type belongs to. */
    protected ActivityManager manager;
    /** A list of the currently available resources. */
    protected ResourceList availableResourceList;
    /** A brief description of the resource type */
    protected String description;

    /**
     * Creates a new resource type.
     * @param id Resource type's identifier
     * @param simul Associated simulation
     * @param description A short text describing this resource type.
     */
	public ResourceType(int id, Simulation simul, String description) {
		super(id, simul);
        this.description = description;
        availableResourceList = new ResourceList();
        simul.add(this);
	}

    /**
	 * @return the description
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
	 * Books all the available resources and gets the total amount.
     * @param sf Single flow looking for available resources.
	 * @return An array that contains the total amount of available resources without 
	 * any conflicts (0) and the total amount of resources which are booked for, at least, 
	 * other resource type in the same activity (1). 
	 */
    protected int[] getAvailable(SingleFlow sf) {
        int total[] = new int[2];
        for (int i = 0; i < availableResourceList.size(); i++) {
            Resource res = availableResourceList.get(i);
    		res.debug("MUTEX\trequesting\t" + sf.getElement() + "(get avail.)");    	
    		res.waitSemaphore();
    		res.debug("MUTEX\tadquired\t" + sf.getElement() + "(get avail.)");    	
            // First, I check if the resource is being used
            if (res.getCurrentSF() == null) {
            	if (!sf.getActivity().isInterruptible() || res.getAvailability(this) > getTs()) {
		            if (res.addBook(sf))
		            	total[0]++;
		            else
		            	total[1]++;
            	}
            }
    		res.debug("MUTEX\treleasing\t" + sf.getElement() + " (get avail.)");    	
    		res.signalSemaphore();
    		res.debug("MUTEX\tfreed\t" + sf.getElement() + " (get avail.)");    	
        }
        return total;
    }

    /**
     * Frees the resources previously booked by a single flow.
     * @param sf Single flow the resources were booked for.
     */
    protected void resetAvailable(SingleFlow sf) {
        for (int i = 0; i < availableResourceList.size(); i++) {
        	Resource res = availableResourceList.get(i);
    		res.debug("MUTEX\trequesting\t" + sf.getElement() + "(reset avail.)");    	
    		res.waitSemaphore();
    		res.debug("MUTEX\tadquired\t" + sf.getElement() + "(reset avail.)");    	
        	res.removeBook(sf);
    		res.debug("MUTEX\treleasing\t" + sf.getElement() + " (reset avail.)");    	
    		res.signalSemaphore();
    		res.debug("MUTEX\tfreed\t" + sf.getElement() + " (reset avail.)");    	
        }
    }

    /**
     * Returns the resource corresponding to the "ind" position.
     * @param ind Resource position in the availability list. 
     * @return Resource corresponding to the "ind" position".
     */
    protected Resource getResource(int ind) {
        if (ind >= availableResourceList.size())
            return null;
        return availableResourceList.get(ind);
    }
    
    /**
     * Searches the first available resource (a resource which is not being used yet) with 
     * this role. The search starts at position <code>ind</code>.   
     * @param ind Position to start the search.
     * @return The resource's index or -1 if there are not available resources.
     */
    protected int getNextAvailableResource(int ind, SingleFlow sf) {
        for (; ind < availableResourceList.size(); ind++) {
            Resource res = availableResourceList.get(ind);
            // Checks if the resource is busy (taken by other element or conflict in the same activity)
    		res.debug("MUTEX\trequesting\t" + sf.getElement() + "(next avail.)");    	
    		res.waitSemaphore();
    		res.debug("MUTEX\tadquired\t" + sf.getElement() + "(next avail.)");
    		// Only resources booked for this SF can be taken into account.
    		// The resource could have been released after the book phase, so it's needed to recheck this.
            if (res.isBooked(sf) && (res.getCurrentSF() == null) && (res.getCurrentResourceType() == null)) {
        		res.debug("MUTEX\treleasing\t" + sf.getElement() + " (next avail.)");    	
        		res.signalSemaphore();
        		res.debug("MUTEX\tfreed\t" + sf.getElement() + " (next avail.)");    	
            	return ind;
            }
    		res.debug("MUTEX\treleasing\t" + sf.getElement() + " (next avail.)");    	
    		res.signalSemaphore();
    		res.debug("MUTEX\tfreed\t" + sf.getElement() + " (next avail.)");    	
        }
        return -1;
    }
    
	/**
	 * Takes <code>n</code> resources from the available resource list. These resources
	 * are marked as caught by the specified single flow. <p>
	 * The resources of this resource type are supposed to be already caught by another single
	 * flow or booked by this single flow. When <code>n</code> resources are taken, the rest
	 * are freed by releasing their book.
	 * @param n Resources needed
     * @param sf single flow catching the resources
	 * @return The minimum availability timestamp of the taken resources 
	 */
    protected double catchResources(int n, SingleFlow sf) {
    	double minAvailability = Double.MAX_VALUE;
        debug("Decrease amount\t" + n + "\t" + sf.getElement());
        
        // When this point is reached, it is suppose that there are enough resources
        for (int i = 0; i < availableResourceList.size(); i++) {
            Resource res = availableResourceList.get(i);
    		res.debug("MUTEX\trequesting\t" + sf.getElement() + "(catch res.)");    	
    		res.waitSemaphore();
    		res.debug("MUTEX\tadquired\t" + sf.getElement() + "(catch res.)");    	
    		// MOD 11/11/08
    		// Only resources booked by this SF are taken into account
    		if (res.isBooked(sf)) {
                // Checks the availability of the resource
                if (res.getCurrentSF() == null) {
                	// The resource has no conflict
                	if (res.getCurrentResourceType() == null) {
    	            	if (n > 0) {
    	            		minAvailability = Math.min(minAvailability, res.catchResource(sf, this));
    	            		n--;
    	                    debug("Resource taken\t" + res + "\t " + n + "\t" + sf.getElement());
    	            	}
                	}
                	// Conflict (in the same activity)
                	// Theoretically, I have no need to check "n"
                	else if (res.getCurrentResourceType() == this) {
                		minAvailability = Math.min(minAvailability, res.catchResource(sf, this));
                		n--;
                        debug("Resource taken\t" + res + "\t " + n + "\t" + sf.getElement());
                        // This check should be unneeded
                        if (n < 0) {
                        	error("More resources than expected\t"+ n + "\t" + sf.getElement());
                        }
                	}
                }
            	res.removeBook(sf);
            }
    		res.debug("MUTEX\treleasing\t" + sf.getElement() + " (catch res.)");    	
    		res.signalSemaphore();
    		res.debug("MUTEX\tfreed\t" + sf.getElement() + " (catch res.)");    	
        }
        // This check should be unneeded
        // FIXME: Esto está saltando
        if (n > 0)
        	error("UNEXPECTED ERROR: Less resources than expected\t"+ n + "\t" + sf.getElement());
        return minAvailability;
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
	public double getTs() {
		return manager.getTs();
	}

    /**
     * Returns the state of this resource type. The state of a resource type consists on the list
     * of available resources.
     * @return The state of this resource type.
     */
	public ResourceTypeState getState() {
		ResourceTypeState state = new ResourceTypeState(id);
		for (int i = 0; i < availableResourceList.size(); i++)
			state.add(availableResourceList.get(i).getIdentifier(), availableResourceList.getCounter(i));
		return state;
	}

    /**
     * Sets the state of this resource type. The state of a resource type consists on the list
     * of available resources.
     * @param state The state of this resource type.
     */
	public void setState(ResourceTypeState state) {
		for (ResourceTypeState.ResourceListEntry entry : state.getAvailableResourceQueue()) {
			Resource res = simul.getResourceList().get(new Integer(entry.getResId()));
			availableResourceList.add(res, entry.getCount());
		}
	}

	/**
	 * Handles the overlap of timetable entries for the same resource, i.e., a resource that has 
	 * several timetable entries at the same time interval with the same resource type. This list 
	 * counts how many times it occurs to avoid incorrect behaviours of the amount of available
	 * resources.
	 * @author Iván Castilla Rodríguez
	 */
	class ResourceList {
		/** List of resources */
	    protected ArrayList<Resource> resources;
		/** A count of how many times each resource has been put as available */
	    protected ArrayList<Integer> counter;
	    
	    /**
	     * Creates a new resource list.
	     */
	    ResourceList() {
	    	resources = new ArrayList<Resource>();
	    	counter = new ArrayList<Integer>();
	    }

	    /**
	     * Adds a resource. If the resource isn't present in the list, it's included with a "1" count.
	     * If the resource exists already, the count is increased.
	     * @param res The resource added
	     */
	    void add(Resource res) {
	    	int pos = resources.indexOf(res);
	    	if (pos == -1) {
	    		resources.add(res);
	    		counter.add(1);
	    	}
	    	else
	    		counter.set(pos, counter.get(pos).intValue() + 1); 
	    }
	    
	    /**
	     * Adds a resource. The count of the resource is explicitly declared.
	     * @param res The resource added
	     * @param count How many times the resource has been put as available for this resource type.
	     */
	    void add(Resource res, int count) {
	    	resources.add(res);
	    	counter.add(count);
	    }
	    
	    /**
	     * Removes a resource. The resource can have more than one appearance in the list. In 
	     * this case, it's no t really removed.
	     * @param res The resource removed.
	     * @return True if the resource is completely removed from the list. False in other case.
	     */
	    boolean remove(Resource res) {
	    	int pos = resources.indexOf(res);
	    	// FIXME Debería crearme un tipo personalizado de excepción
	    	if (pos == -1)
	    		throw new RuntimeException("Unexpected error: Resource not found in resource type");
	    	if (counter.get(pos).intValue() > 1) {
	    		counter.set(pos, new Integer(counter.get(pos).intValue() - 1));
	    		return false;
	    	}
			resources.remove(pos);
			counter.remove(pos);
	    	return true;
	    }
	    
	    /**
	     * Returns the resource at the specified position 
	     * @param index The position of the resource
	     * @return The resource at the specified position.
	     */
	    Resource get(int index) {
	    	return resources.get(index);
	    }
	    
	    /**
	     * Returns the count at the specified position
	     * @param index The position of the count.
	     * @return the count at the specified position
	     */
	    int getCounter(int index) {
	    	return counter.get(index);
	    }
	    
	    /**
	     * Returns the number of resources in this list. 
	     * @return The number of resources in this list.
	     */
	    int size() {
	    	return resources.size();
	    }
	}
} 
