package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.iis.simulation.core.TimeTableEntry;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * TODO Comment
 * @author Carlos Mart�n Gal�n
 */
public class ResourceEngine extends EngineObject implements es.ull.iis.simulation.model.engine.ResourceEngine {
    /** List of currently active roles and the timestamp which marks the end of their availability time */
    protected final TreeMap<ResourceType, Long> currentRoles;
    /** A counter of the valid timetable entries which this resource is following */
    private final AtomicInteger validTTEs = new AtomicInteger();
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** Work item which currently holds this resource */
    protected WorkItem currentWI = null;
    /** List of elements trying to book this resource */
    protected final TreeMap<WorkItem, ResourceType> bookList = new TreeMap<WorkItem, ResourceType>();
    /** Availability flag */
    protected volatile boolean notCanceled = true;
    /** List of current activity managers which contain a resource type using this resource */
    protected final TreeMap<ActivityManager, Integer> currentAMs = new TreeMap<ActivityManager, Integer>();
    /** Access control */
    final private AtomicBoolean sem;
    /** The associated {@link Resource} */
    private final Resource modelRes;

    /**
     * Creates a new ResourceEngine.
     * @param simul ParallelSimulationEngine this resource is attached to.
     * @param description A short text describing this resource.
     */
	public ResourceEngine(ParallelSimulationEngine simul, es.ull.iis.simulation.model.Resource modelRes) {
		super(modelRes.getIdentifier(), simul, "RES");
        currentRoles = new TreeMap<ResourceType, Long>();
        notCanceled = true;
        this.modelRes = modelRes;
        sem = new AtomicBoolean(false);
	}

    /**
     * Returns the associated {@link Resource}
	 * @return the associated {@link Resource}
	 */
	public Resource getModelRes() {
		return modelRes;
	}

    /**
     * Sends a "wait" signal to the semaphore.
     */    
    protected void waitSemaphore() {
    	while (!sem.compareAndSet(false, true));
    }
    
    /**
     * Sends a "continue" signal to the semaphore.
     */    
    protected void signalSemaphore() {
        sem.set(false);
    }
    
	/**
	 * Adds a new resource type to the list of current roles. If the list already contains an
	 * entry for the resource type, the greater timestamp is added.
	 * @param role New resource type added
	 * @param ts Timestamp when the availability of this resource finishes for this resource type. 
	 */
	@Override
	public void addRole(ResourceType role, long ts) {
		waitSemaphore();
		final Long avEnd = currentRoles.get(role);
		if ((avEnd == null) || (ts > avEnd))
			currentRoles.put(role, ts);
		// Updates AM list
		final ActivityManager am = role.getManager();
		Integer counter = currentAMs.get(am);
		if (counter == null)
			counter = 1;
		else
			counter++;
		currentAMs.put(am, counter);
		signalSemaphore();
		// The activity manger is informed of new available resources        		
		role.notifyResource();
	}

	/**
	 * Removes a resource type from the list of current roles. If the role doesn't exist
	 * the removal is silently skipped (that's because a resource can have several timetable 
	 * entries for the same role, but the <code>currentRoles</code> list only contains 
	 * one entry per role). However, checks if it's time for removing the role before doing it.
	 * @param role ResourceEngine type removed
	 */
	@Override
	public void removeRole(ResourceType role) {
		waitSemaphore();
		final Long avEnd = currentRoles.get(role);
		if (avEnd != null)
			if (avEnd <= simul.getTs())
				currentRoles.remove(role);
		// Updates AM list
		final ActivityManager am = role.getManager();
		final Integer counter = currentAMs.get(am);
		if (counter > 1)
			currentAMs.put(am, counter - 1);
		else
			currentAMs.remove(am);
		signalSemaphore();
	}

	/**
	 * Builds a list of activity managers referenced by the roles of the resource. 
	 * @return Returns the currentManagers.
	 */
	@Override
	public ArrayList<ActivityManager> getCurrentManagers() {
		return new ArrayList<ActivityManager>(currentAMs.keySet());		
	}
	
	/**
	 * Notifies all the activity managers using this resource that it has become available. 
	 */
	public void notifyCurrentManagers() {
		waitSemaphore();
		for (ActivityManager am : currentAMs.keySet())
			am.notifyResource();
		signalSemaphore();
	}

	/**
	 * Returns <code>true</code> if the resource is being used from multiple activity managers.
	 * @return <code>True</code> if the resource is being used from multiple activity managers; 
	 * <code>false</code> otherwise.
	 */
	protected boolean inSeveralManagers() {
		return (currentAMs.size() > 1);
	}
	
	/**
	 * Tentatively adds this resource to a solution built to carry out an activity. First checks if this 
	 * resource is not being using yet in another solution.
	 * @param rt ResourceEngine type to be assigned in this solution 
	 * @param wi Work item trying to catch this resource
	 * @return <code>True</code> if this resource can be used in the solution; <code>false</code> otherwise.
	 */
	protected boolean add2Solution(ResourceType rt, WorkItem wi) {
		if (notCanceled) {
	    	if (inSeveralManagers()) {
	            // Checks if the resource is busy (taken by other element or conflict in the same activity)
	    		waitSemaphore();
	    		// First checks if this resource was previously booked by this element 
	        	if ((currentWI == null) && !isBooked(wi)) {
	            	addBook(wi, rt);
	    	        wi.pushResource(this, true);
		        	// No other element has tried to book this resource
		        	if (currentResourceType == null)
		    	        currentResourceType = rt;
	        		signalSemaphore();
	        		return true;
	        	}
	    		signalSemaphore();
	    	}
	    	else {
	    		// Simply checks if the resource is available and has not been used in another RT of the same activity yet.
	            if (currentResourceType == null) {
	    	        // This resource belongs to the solution...
	    	        currentResourceType = rt;
	    	        wi.pushResource(this, false);    	        
	            	return true;
	            }
	    	}
		}
		return false;
	}
	
	/**
	 * Removes this resource from a solution it was tentatively added to. 
	 * @param wi Work item which won't use this resource to carry out an activity.
	 */
	protected void removeFromSolution(WorkItem wi) {
    	if (inSeveralManagers()) {
    		waitSemaphore();
	        wi.popResource(true);
	        final ResourceType rt = bookList.get(wi);
	        removeBook(wi);
    		if (currentResourceType == rt) {
    			if (bookList.isEmpty())
        			currentResourceType = null;
    			else
    				currentResourceType = bookList.firstEntry().getValue();
    		}
    		signalSemaphore();    		
    	}
    	else {
	        currentResourceType = null;
	        wi.popResource(false);    		
    	}
	}
	
	/**
	 * Checks if this resource, which was tentatively added to a solution, is still valid, i.e. has 
	 * not been used to carry out another activity.
	 * @param wi Work item trying to catch this resource
	 * @return <code>True</code> if this resource is still valid for a solution; <code>false</code> otherwise.
	 */
	protected boolean checkSolution(WorkItem wi) {
		if (inSeveralManagers()) {
			waitSemaphore();
			if (currentWI == null) {
		        currentResourceType = bookList.get(wi);
			}
			else {
				signalSemaphore();
				return false;
			}
			signalSemaphore();
		}
		return true;
	}
	
	/**
	 * Makes a reservation on this resource. This step is required when a resource is being used from several activity
	 * managers.
	 * @param wi The work item booking this resource
	 * @param rt The resource type to be assigned to this resource.
	 */
	protected void addBook(WorkItem wi, ResourceType rt) {
		// First I complete the conflicts list
		if (bookList.size() > 0)
			wi.mergeConflictList(bookList.firstKey());
		bookList.put(wi, rt);
		modelRes.debug("booked\t" + wi.getElement());
	}
	
	/**
	 * Releases a reservation previously made on this resource. This step is required when a resource is being used 
	 * from several activity managers.
	 * @param wi The work item releasing the book over this resource.
	 */
	protected void removeBook(WorkItem wi) {
		bookList.remove(wi); 
		modelRes.debug("unbooked\t" + wi.getElement());
	}

	/**
	 * Checks if this resource is currently booked by the specified work item.
	 * @param wi Work item which may have booked this resource
	 * @return <code>True</code> if this resource is currently booked by the specified work item 
	 */
	protected boolean isBooked(WorkItem wi) {
		return bookList.containsKey(wi);
	}

	/**
	 * Definitely takes this resource by setting its current work item. If the resource was booked, the 
	 * reservation is released. 
	 * @param wi The work item which an element is executing
	 * @return The availability timestamp of this resource for this resource type 
	 */
	protected long catchResource(WorkItem wi) {
		setTs(wi.getElement().getTs());
		simul.notifyInfo(null).notifyInfo(new ResourceUsageInfo(this.simul, this, currentResourceType, wi, ResourceUsageInfo.Type.CAUGHT, getTs()));
		if (inSeveralManagers()) {
			waitSemaphore();
			removeBook(wi);
			currentWI = wi;
			signalSemaphore();
		}
		else
			currentWI = wi;
		return currentRoles.get(currentResourceType);
	}
	
    /**
     * Releases this resource. If the resource has already expired its availability time, 
     * the <code>timeOut</code> flag is set off. Sets the current work item and the current resource type 
     * to <code>null</code>. 
     * @return True if the resource could be correctly released. False if the availability
     * time of the resource had already expired.
     */
    protected boolean releaseResource() {
		waitSemaphore();
		setTs(simul.getTs());
		simul.notifyInfo(new ResourceUsageInfo(this.simul, this, this.getCurrentResourceType(), currentWI, ResourceUsageInfo.Type.RELEASED, getTs()));
        currentWI = null;
        currentResourceType = null;        
        if (timeOut) {
        	timeOut = false;
    		signalSemaphore();
        	return false;
        }
		signalSemaphore();
        return true;
    }
    
    /**
     * Returns the work item of the element which currently owns this resource.
     * @return The current work item.
     */
    protected WorkItem getCurrentWI() {
        return currentWI;
    }
    
    /**
     * Returns the current resource type this resource is being used for.
     * @return Current resource type of this resource
     */
    public ResourceType getCurrentResourceType() {
        return currentResourceType;
    }
    
    /**
     * Returns the availability of this resource for the specified resource type.
     * @param rt ResourceEngine type
     * @return The availability of this resource for the specified resource type; 
     * <code>null</code> if the resource is not available for this resource type.
     */
    protected Long getAvailability(ResourceType rt) {
    	return currentRoles.get(rt); 
    }
	
	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 */
    @Override
	public synchronized void setNotCanceled(boolean available) {
		notCanceled = available;
	}
	
	public TreeMap<ResourceType, Long> getCurrentRoles() {
		return currentRoles;
	}

	@Override
	public int incValidTimeTableEntries() {
		return validTTEs.incrementAndGet();
	}

	@Override
	public int decValidTimeTableEntries() {
		return validTTEs.decrementAndGet();
	}

	@Override
	public int getValidTimeTableEntries() {
		return validTTEs.get();
	}

}
